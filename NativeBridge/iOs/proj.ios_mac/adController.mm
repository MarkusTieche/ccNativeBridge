#import "adController.h"
#import <GoogleMobileAds/GoogleMobileAds.h>
#import <UIKit/UIKit.h>
#import <UserMessagingPlatform/UserMessagingPlatform.h>
//#import "bindings/se/ScriptEngine.h"
#include "cocos/scripting/js-bindings/jswrapper/SeApi.h"

@interface adController () <GADBannerViewDelegate, GADFullScreenContentDelegate>

@property (nonatomic, strong) GADBannerView *bannerView;
@property (nonatomic, strong) GADInterstitialAd *interstitial;
@property (nonatomic, strong) GADRewardedAd *rewardedAd;
@property (nonatomic, strong) UIViewController *rootViewController;

@end

@implementation adController

static NSDictionary *idValues = nil;

+ (instancetype)sharedInstance {
    static adController *sharedInstance = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        sharedInstance = [[adController alloc] init];
    });
    return sharedInstance;
}

- (instancetype)init {
    NSString *path = [[NSBundle mainBundle] pathForResource:@"idValues" ofType:@"plist"];
    idValues = [[NSDictionary dictionaryWithContentsOfFile:path]retain];
    
    if (self = [super init]) {
        
        self.rootViewController = [UIApplication sharedApplication].keyWindow.rootViewController;
        NSLog(@"AdMob initialized (or already initialized).");
        [[GADMobileAds sharedInstance] startWithCompletionHandler:^(GADInitializationStatus *status) {
                   NSLog(@"AdMob initialized (or already initialized).");

                   // Run on main thread for UI safety
                   dispatch_async(dispatch_get_main_queue(), ^{
                       [self loadInterstitial];
                       [self loadRewardedAd];
                   });
               }];


        // Test device ID for simulator
        //GADMobileAds.sharedInstance.requestConfiguration.testDeviceIdentifiers = @[ GADSimulatorID ];
        
    }
    
    return self;
}

#pragma mark - Consent
+ (void)requestConsentIfRequired {
    UMPRequestParameters *parameters = [[UMPRequestParameters alloc] init];
    parameters.tagForUnderAgeOfConsent = NO;

    [UMPConsentInformation.sharedInstance requestConsentInfoUpdateWithParameters:parameters
                                                              completionHandler:^(NSError *_Nullable requestError) {
        if (requestError) {
            NSLog(@"Consent info update error: %@", requestError.localizedDescription);
            return;
        }

        UMPFormStatus formStatus = UMPConsentInformation.sharedInstance.formStatus;
        if (formStatus == UMPFormStatusAvailable) {
            [UMPConsentForm loadWithCompletionHandler:^(UMPConsentForm *_Nullable form, NSError *_Nullable loadError) {
                if (loadError) {
                    NSLog(@"Consent form load error: %@", loadError.localizedDescription);
                    return;
                }

                if (form) {
                    [form presentFromViewController:[UIApplication sharedApplication].keyWindow.rootViewController
                                  completionHandler:^(NSError *_Nullable dismissError) {
                        if (dismissError) {
                            NSLog(@"Consent form dismissed with error: %@", dismissError.localizedDescription);
                        }

                        UMPConsentStatus consentStatus = [UMPConsentInformation.sharedInstance consentStatus];
                        if (consentStatus == UMPConsentStatusObtained) {
                            NSLog(@"✅ User consent obtained.");
                            [[adController sharedInstance] loadInterstitial];
                            [[adController sharedInstance] loadRewardedAd];
                        } else {
                            NSLog(@"⚠️ User did not give consent.");
                        }
                    }];
                }
            }];
        } else {
            NSLog(@"Consent form not available or already shown.");
        }
    }];
}

+ (NSDictionary *)getAdMobConsentParameters {
    UMPConsentStatus status = UMPConsentInformation.sharedInstance.consentStatus;

    if (status == UMPConsentStatusObtained) {
        // ✅ User gave consent — no need for NPA
        return @{};
    } else {
        // ❌ Consent not obtained or dismissed — serve non-personalized ads
        return @{@"npa": @"1"};
    }
}

- (GADRequest *)createAdRequest {
    GADRequest *request = [GADRequest request];
    NSDictionary *consentParams = [adController getAdMobConsentParameters];

    if (consentParams.count > 0) {
        GADExtras *extras = [[GADExtras alloc] init];
        extras.additionalParameters = consentParams;
        [request registerAdNetworkExtras:extras];
    }
    return request;
}


#pragma mark - Banner

- (void)showBanner {
    if (self.bannerView) {
        [self.bannerView removeFromSuperview];
        self.bannerView = nil;
    }
    GADAdSize adSize = [self adSizeFromPlist];//BANNERSIZE
    
    CGRect screenBounds = [UIScreen mainScreen].bounds;
    self.bannerView = [[GADBannerView alloc] initWithAdSize:adSize];
    self.bannerView.adUnitID = idValues[@"BannerAdUnitID"]; // Test Banner ID
    self.bannerView.rootViewController = self.rootViewController;
    self.bannerView.delegate = self;

    CGFloat bannerWidth = self.bannerView.bounds.size.width;
    CGFloat screenWidth = CGRectGetWidth(screenBounds);
    CGFloat screenHeight = CGRectGetHeight(screenBounds);

    self.bannerView.frame = CGRectMake((screenWidth - bannerWidth)/2, screenHeight - 50, bannerWidth, 50);

    [self.rootViewController.view addSubview:self.bannerView];

    GADRequest *request = [self createAdRequest];
    [self.bannerView loadRequest:request];
}

- (void)hideBanner {
    if (self.bannerView) {
        [self.bannerView removeFromSuperview];
        self.bannerView = nil;
    }
}

- (GADAdSize)adSizeFromPlist {
    NSString *sizeString = idValues[@"BannerSize"];

    if ([sizeString isEqualToString:@"GADAdSizeLargeBanner"]) {
        return GADAdSizeLargeBanner;
    } else if ([sizeString isEqualToString:@"GADAdSizeMediumRectangle"]) {
        return GADAdSizeMediumRectangle;
    } else if ([sizeString isEqualToString:@"GADAdSizeFullBanner"]) {
        return GADAdSizeFullBanner;
    } else if ([sizeString isEqualToString:@"GADAdSizeLeaderboard"]) {
        return GADAdSizeLeaderboard;
    } else if ([sizeString isEqualToString:@"Adaptive"]) {
        CGFloat width = CGRectGetWidth([UIScreen mainScreen].bounds);
        return GADCurrentOrientationAnchoredAdaptiveBannerAdSizeWithWidth(width);
    } else {
        // Default fallback
        return GADAdSizeBanner;
    }
}

#pragma mark - Interstitial

- (void)loadInterstitial {
    GADRequest *request = [GADRequest request];
    [GADInterstitialAd loadWithAdUnitID:idValues[@"InterstitialAdUnitID"] // Test Interstitial ID
                                request:[self createAdRequest]
                      completionHandler:^(GADInterstitialAd *ad, NSError *error) {
        if (error) {
            NSLog(@"Failed to load interstitial: %@", error.localizedDescription);
            return;
        }
        NSLog(@"Interstitial ad ready.");
        self.interstitial = ad;
        self.interstitial.fullScreenContentDelegate = self;
    }];
}

- (void)showInterstitial {
    if (self.interstitial && self.rootViewController) {
        [self.interstitial presentFromRootViewController:self.rootViewController];
    } else {
        NSLog(@"Interstitial ad not ready.");
    }
}

#pragma mark - Rewarded

- (void)loadRewardedAd {
    GADRequest *request = [GADRequest request];
    [GADRewardedAd loadWithAdUnitID:idValues[@"RewardedAdUnitID"] // Test Rewarded ID
                            request:[self createAdRequest]
                  completionHandler:^(GADRewardedAd *ad, NSError *error) {
        if (error) {
            NSLog(@"Failed to load rewarded ad: %@", error.localizedDescription);
            return;
        }
        self.rewardedAd = ad;
        NSLog(@"Rewarded ad loaded.");
    }];
}

- (void)showRewardedAd {
    if (self.rewardedAd && self.rootViewController) {
        [self.rewardedAd presentFromRootViewController:self.rootViewController
                                 userDidEarnRewardHandler:^{
            GADAdReward *reward = self.rewardedAd.adReward;
            NSLog(@"User earned reward: %@ %@", reward.amount, reward.type);
          //  [self emitEventToJS:@"rewarded-earned"];
        }];

        self.rewardedAd.fullScreenContentDelegate = self;
    } else {
        NSLog(@"Rewarded ad not ready.");
    }
}

#pragma mark - GADFullScreenContentDelegate

- (void)adDidDismissFullScreenContent:(id)ad {
    if ([ad isKindOfClass:[GADInterstitialAd class]]) {
        NSLog(@"Interstitial closed.");
        se::ScriptEngine::getInstance()->evalString("cc.systemEvent.emit('interstitial-ad-closed');");
        [self loadInterstitial];
    } else if ([ad isKindOfClass:[GADRewardedAd class]]) {
        NSLog(@"Rewarded ad closed.");
        se::ScriptEngine::getInstance()->evalString("cc.systemEvent.emit('rewardVideo-ad-closed');");
        [self loadRewardedAd];
    }
}

#pragma mark - Static Methods (for jsb.reflection)

+ (void)initializeAdMob {
    [adController sharedInstance]; // triggers init
}

+ (void)showBanner {
    [[adController sharedInstance] showBanner];
}

+ (void)hideBanner {
    [[adController sharedInstance] hideBanner];
}

+ (void)showInterstitial {
    [[adController sharedInstance] showInterstitial];
}

+ (void)loadRewardedAd {
    [[adController sharedInstance] loadRewardedAd];
}

+ (void)showRewardedAd {
    [[adController sharedInstance] showRewardedAd];
}

+ (BOOL)isInterstitialAvailable {
    return [adController sharedInstance].interstitial != nil;
}

+ (BOOL)isRewardedAdAvailable {
    return [adController sharedInstance].rewardedAd != nil;
}

@end
