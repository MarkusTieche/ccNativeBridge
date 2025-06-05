window.nativeBridge = {

    adFrequency : 3,
    lastAdShown : Date.now(),
    
    load ()
    {
        this.callbackFunc = null;
        this.adCounter = this.adFrequency,
        cc.log("nativeBridgeLoaded")

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

    timeElapsed(time)
    {
        var timeElapsed =  Math.floor((new Date() - this.lastAdShown)/1000); //IN SECONDS
        //SHOW NEXT AD ONLY AFTER time in s
        cc.log(time,"timeElapsed: "+timeElapsed)
        if(timeElapsed >= time)
        {
            return true;
        }
        return false;
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
        //SHOW BANNER AFTER PRELOAD SCREEN :)
        if(cc.sys.isNative)
        {   
            if(cc.sys.os == cc.sys.OS_IOS) {
                jsb.reflection.callStaticMethod("adController","showBanner");
            }

            if(cc.sys.platform == cc.sys.ANDROID)
            {
                jsb.reflection.callStaticMethod("org/cocos2dx/javascript/adController", "showBanner","()V");
            }
        }
    },

    hideBanner()
    {
        if (cc.sys.isNative)
        {
            if (cc.sys.os == cc.sys.OS_IOS) {
                jsb.reflection.callStaticMethod("adController", "hideBanner");
            }

            if (cc.sys.platform == cc.sys.ANDROID) {
                jsb.reflection.callStaticMethod("org/cocos2dx/javascript/adController", "hideBanner", "()V");
            }
        }
    },

    requestReview()
    {
        if(cc.sys.isNative)
        {
            if(cc.sys.os == cc.sys.OS_IOS) {
                jsb.reflection.callStaticMethod("appRating","requestAppReview");
            }

            if(cc.sys.platform == cc.sys.ANDROID)
            {
                jsb.reflection.callStaticMethod("org/cocos2dx/javascript/appRating", "requestAppReview","()V");
            }
        }
    },

    showLeaderboard()
    {
        if(cc.sys.isNative)
        {
            if(cc.sys.os == cc.sys.OS_IOS) {
                jsb.reflection.callStaticMethod("gameCenter","showLeaderboard");
            }

            if(cc.sys.platform == cc.sys.ANDROID)
            {
                jsb.reflection.callStaticMethod("org/cocos2dx/javascript/AppActivity", "showLeaderboard","()V");
                // jsb.reflection.callStaticMethod("org/cocos2dx/javascript/googleService", "showLeaderboard","()V");
            }
        }
    },
    
    submitScore(score)
    {
        if(cc.sys.isNative)
        {
            if(cc.sys.os == cc.sys.OS_IOS) {
                jsb.reflection.callStaticMethod("gameCenter","submitScore:toLeaderboard:",score,"speedwings_leaderboard");
            }
            
            if(cc.sys.platform == cc.sys.ANDROID)
            {
                jsb.reflection.callStaticMethod("org/cocos2dx/javascript/AppActivity", "submitScore","(I)V",score);
            }
        }
    },

    interstitialAvailable()
    {
        if (cc.sys.isNative) {
            if (cc.sys.os == cc.sys.OS_IOS) {
                return jsb.reflection.callStaticMethod("adController", "interstitialAvailable");
                
            }

            if (cc.sys.platform == cc.sys.ANDROID) {

                return jsb.reflection.callStaticMethod("org/cocos2dx/javascript/adController", "interstitialAvailable", "()Z");
            }
        }
    },

    showInterstitial(callFunc)
    {
        // if(!this.timeElapsed(180)){return};

        if(cc.sys.isNative)
        {
            if(cc.sys.os == cc.sys.OS_IOS) {
                jsb.reflection.callStaticMethod("adController","showInterstitial");
            }
            
            if(cc.sys.platform == cc.sys.ANDROID)
            {
                jsb.reflection.callStaticMethod("org/cocos2dx/javascript/adController", "showInterstitial","()V");
            }
        } 

    },
    
    showRewardVideo(callFunc)
    {
        this.callbackFunc = callFunc;
        this.lastAdShown = Date.now();

        if(cc.sys.isNative)
        {
            if(cc.sys.os == cc.sys.OS_IOS) {
                jsb.reflection.callStaticMethod("adController","showRewardedAd");
            }

            if(cc.sys.platform == cc.sys.ANDROID)
            {
                jsb.reflection.callStaticMethod("org/cocos2dx/javascript/adController", "showRewardedAd","()V");
            }

        }
            
        //this.rewardClosed();

    },

    rewardVideoAvailable()
    {
        if (cc.sys.isNative) {
            if (cc.sys.os == cc.sys.OS_IOS) {
                return jsb.reflection.callStaticMethod("adController", "rewardVideoAvailable");
            }

            if (cc.sys.platform == cc.sys.ANDROID) {

                return jsb.reflection.callStaticMethod("org/cocos2dx/javascript/adController", "rewardVideoAvailable", "()Z");
            }
        }
    },
};

if(!CC_EDITOR)
{
    nativeBridge.load();
}
