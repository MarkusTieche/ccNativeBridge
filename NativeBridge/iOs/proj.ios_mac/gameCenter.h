// gameCenter.h
#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>
#import <GameKit/GameKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface gameCenter : NSObject <GKGameCenterControllerDelegate>

+ (void)signIn;
+ (void)signOut;
+ (BOOL)isSignedIn;
+ (void)submitScore:(int64_t)score;
+ (void)showLeaderboard;


@end

NS_ASSUME_NONNULL_END
