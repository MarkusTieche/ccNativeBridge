// gameCenter.m
#import "gameCenter.h"

@implementation gameCenter

static BOOL authenticated = NO;
static NSDictionary *idValues = nil;

+ (void)initialize {
    if (self == [gameCenter class]) {
        NSString *path = [[NSBundle mainBundle] pathForResource:@"idValues" ofType:@"plist"];
        idValues = [NSDictionary dictionaryWithContentsOfFile:path];
    }
}

+ (void)signIn {
    GKLocalPlayer *localPlayer = [GKLocalPlayer localPlayer];
    if (localPlayer.isAuthenticated) {
        authenticated = YES;
        NSLog(@"gameCenter: Already signed in");
        return;
    }
    
    localPlayer.authenticateHandler = ^(UIViewController *viewController, NSError *error) {
        if (viewController != nil) {
            // Present the Game Center login view controller
            UIWindow *window = [UIApplication sharedApplication].keyWindow;
            UIViewController *rootVC = window.rootViewController;
            [rootVC presentViewController:viewController animated:YES completion:nil];
        } else if (localPlayer.isAuthenticated) {
            authenticated = YES;
            NSLog(@"gameCenter: Signed in successfully");
        } else {
            authenticated = NO;
            NSLog(@"gameCenter: Authentication failed: %@", error.localizedDescription);
        }
    };
}

+ (void)signOut {
    // No direct signOut method in GameCenter, to sign out user must logout from Game Center app
    NSLog(@"gameCenter: signOut called - user must sign out manually via Game Center app");
}

+ (BOOL)isSignedIn {
    return [GKLocalPlayer localPlayer].isAuthenticated;
}

+ (void)submitScore:(int64_t)score {
    if (![self isSignedIn]) {
        NSLog(@"gameCenter: Not signed in, cannot submit score");
        return;
    }

    GKScore *scoreReporter = [[GKScore alloc] initWithLeaderboardIdentifier:idValues[@"LeaderboardID"]];
    scoreReporter.value = score;
    
    [GKScore reportScores:@[scoreReporter] withCompletionHandler:^(NSError * _Nullable error) {
        if (error != nil) {
            NSLog(@"gameCenter: Failed to submit score: %@", error.localizedDescription);
        } else {
            NSLog(@"gameCenter: Score submitted successfully");
        }
    }];
}

+ (void)showLeaderboard{
    if (![self isSignedIn]) {
        NSLog(@"gameCenter: Not signed in, cannot show leaderboard");
        return;
    }
    
    GKGameCenterViewController *gcVC = [[GKGameCenterViewController alloc] init];
    gcVC.gameCenterDelegate = (id<GKGameCenterControllerDelegate>)self;
    gcVC.viewState = GKGameCenterViewControllerStateLeaderboards;
    gcVC.leaderboardIdentifier = idValues[@"LeaderboardID"];
    
    //GET VIEW CONTROLLER = [UIApplication sharedApplication].keyWindow.rootViewController;
    UIViewController * vc = [UIApplication sharedApplication].keyWindow.rootViewController;
    
    [vc presentViewController:gcVC animated:YES completion:nil];
}

// Delegate method for GameCenter view controller
- (void)gameCenterViewControllerDidFinish:(GKGameCenterViewController *)gameCenterViewController {
    [gameCenterViewController dismissViewControllerAnimated:YES completion:nil];
}

@end
