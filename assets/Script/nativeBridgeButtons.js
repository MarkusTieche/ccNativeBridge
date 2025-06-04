cc.Class({
    extends: cc.Component,

    properties: {
     
    },

   
    // onLoad () {},

    // start () { },

    //ADNETWORK
        //CONSENT
        requestConsent()
        {
            cc.log("requestConsent")
            nativeBridge.requestConsent()
        },

        //BANNER
        loadBanner()
        {
            cc.log("loadBanner")
        },
        showBanner()
        {
            cc.log("showBanner")
            nativeBridge.showBanner()
        },
        hideBanner()
        {
            cc.log("button hideBanner")
            nativeBridge.hideBanner()
        },

        //INTERSTITIAL
        loadInterstitial()
        {
            cc.log("loadInterstitial")
        },
        showInterstitial()
        {
            cc.log("showInterstitial")
            nativeBridge.showInterstitial()
        },
        InterstitialClosed(event)
        {
            cc.log("Interstitial closed event")
        },

        //REWARDVIDEO
        loadReward() 
        {
            cc.log("loadReward")
        },
        showReward() 
        {
            cc.log("showReward")
            nativeBridge.showRewardVideo();
        },
        RewardClosed(event) 
        {
            cc.log("Reward closed event")
        },

    //GAMECENTER
        initGameCenter()
        {
            cc.log("initGameCenter")
        },
    
    //LEADERBOARD
        showLeaderboard()
        {
            cc.log("showLeaderboard")
        },
        submitScore(event,Score)
        {
            cc.log("submitScore:",Score)
        },

    //RATING
        requestRating()
        {
            cc.log("request Rating")
            nativeBridge.requestReview();
        },

    // update (dt) {},
});
