#import <Foundation/Foundation.h>

@interface adController : NSObject

+ (void)initializeAdMob;
+ (void)showBanner;
+ (void)hideBanner;
+ (void)showInterstitial;
+ (void)loadRewardedAd;
+ (void)showRewardedAd;
+ (void)requestConsentIfRequired;
+ (BOOL)isInterstitialAvailable;
+ (BOOL)isRewardedAdAvailable;
+ (NSDictionary *)getAdMobConsentParameters;

@end
