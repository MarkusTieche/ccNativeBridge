#import "appRating.h"
#import <StoreKit/StoreKit.h>
#import <UIKit/UIKit.h>

@implementation appRating


+ (void)requestAppReview {
    if (@available(iOS 10.3, *)) {
        [SKStoreReviewController requestReview];
    } else {
        NSString *path = [[NSBundle mainBundle] pathForResource:@"idValues" ofType:@"plist"];
        NSDictionary *idValues = [NSDictionary dictionaryWithContentsOfFile:path];
        
        NSString *appID = idValues[@"AppID"]; // Replace with your actual App Store ID
        NSString *urlString = [NSString stringWithFormat:idValues[@"StoreUrl"], appID];
        NSURL *url = [NSURL URLWithString:urlString];
        if ([[UIApplication sharedApplication] canOpenURL:url]) {
            [[UIApplication sharedApplication] openURL:url options:@{} completionHandler:nil];
        }
    }
}

@end
