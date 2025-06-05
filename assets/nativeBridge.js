window.nativeBridge = {

    load ()
    {
        //CALLBACK EVENTS
        cc.systemEvent.on('interstitial-ad-closed', function () {
            console.log("Interstitial ad closed! Handling global event.");
            // Resume gameplay, show UI, etc.
        }, this);

        cc.systemEvent.on('rewardVideo-ad-closed', function () {
            console.log("rewardVideo ad closed! Handling global event.");
            // Resume gameplay, show UI, etc.
        }, this);
    },

    requestConsent()
    {
        if (cc.sys.isNative) {
            if (cc.sys.os == cc.sys.OS_IOS) { jsb.reflection.callStaticMethod("adController", "requestConsentIfRequired"); }
            if (cc.sys.platform == cc.sys.ANDROID) { jsb.reflection.callStaticMethod("org/cocos2dx/javascript/adController", "requestConsent", "()V"); }
        }
    },

    showBanner()
    {
        if(cc.sys.isNative)
        {   
            if(cc.sys.os == cc.sys.OS_IOS) { jsb.reflection.callStaticMethod("adController","showBanner"); }
            if(cc.sys.platform == cc.sys.ANDROID){ jsb.reflection.callStaticMethod("org/cocos2dx/javascript/adController", "showBanner","()V"); }
        }
    },

    hideBanner()
    {
        if (cc.sys.isNative)
        {
            if (cc.sys.os == cc.sys.OS_IOS) { jsb.reflection.callStaticMethod("adController", "hideBanner"); }
            if (cc.sys.platform == cc.sys.ANDROID) { jsb.reflection.callStaticMethod("org/cocos2dx/javascript/adController", "hideBanner", "()V"); }
        }
    },

    requestReview()
    {
        if(cc.sys.isNative)
        {
            if(cc.sys.os == cc.sys.OS_IOS) { jsb.reflection.callStaticMethod("appRating","requestAppReview"); }
            if(cc.sys.platform == cc.sys.ANDROID){ jsb.reflection.callStaticMethod("org/cocos2dx/javascript/appRating", "requestAppReview","()V"); }
        }
    },

    showLeaderboard()
    {
        if(cc.sys.isNative)
        {
            if(cc.sys.os == cc.sys.OS_IOS) { jsb.reflection.callStaticMethod("gameCenter","showLeaderboard"); }
            if(cc.sys.platform == cc.sys.ANDROID){ jsb.reflection.callStaticMethod("org/cocos2dx/javascript/AppActivity", "showLeaderboard","()V"); }
        }
    },
    
    submitScore(score)
    {
        if(cc.sys.isNative)
        {
            if(cc.sys.os == cc.sys.OS_IOS) { jsb.reflection.callStaticMethod("gameCenter","submitScore:toLeaderboard:",score,"speedwings_leaderboard"); }
            if(cc.sys.platform == cc.sys.ANDROID){ jsb.reflection.callStaticMethod("org/cocos2dx/javascript/AppActivity", "submitScore","(I)V",score); }
        }
    },

    interstitialAvailable()
    {
        if (cc.sys.isNative) {
            if (cc.sys.os == cc.sys.OS_IOS) { return jsb.reflection.callStaticMethod("adController", "interstitialAvailable"); }
            if (cc.sys.platform == cc.sys.ANDROID) { return jsb.reflection.callStaticMethod("org/cocos2dx/javascript/adController", "interstitialAvailable", "()Z");}
        }
    },

    showInterstitial()
    {
        if(cc.sys.isNative)
        {
            if(cc.sys.os == cc.sys.OS_IOS) { jsb.reflection.callStaticMethod("adController","showInterstitial");}
            if(cc.sys.platform == cc.sys.ANDROID){ jsb.reflection.callStaticMethod("org/cocos2dx/javascript/adController", "showInterstitial","()V");}
        } 
    },
    
    rewardVideoAvailable()
    {
        if (cc.sys.isNative) {
            if (cc.sys.os == cc.sys.OS_IOS) { return jsb.reflection.callStaticMethod("adController", "rewardVideoAvailable");}
            if (cc.sys.platform == cc.sys.ANDROID) { return jsb.reflection.callStaticMethod("org/cocos2dx/javascript/adController", "rewardVideoAvailable", "()Z");}
        }
    },

    showRewardVideo()
    {
        if(cc.sys.isNative)
        {
            if(cc.sys.os == cc.sys.OS_IOS) { jsb.reflection.callStaticMethod("adController","showRewardedAd");}
            if(cc.sys.platform == cc.sys.ANDROID){ jsb.reflection.callStaticMethod("org/cocos2dx/javascript/adController", "showRewardedAd","()V");}
        }
    },

};

if(!CC_EDITOR)
{
    nativeBridge.load();
}
