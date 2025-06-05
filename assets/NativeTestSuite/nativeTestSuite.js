cc.Class({
    extends: cc.Component,

    //ADNETWORK
    //CONSENT
    requestConsent() {
        cc.log("requestConsent")
        nativeBridge.requestConsent()
    },

    //BANNER
    showBanner() {
        cc.log("showBanner")
        nativeBridge.showBanner()
    },
    hideBanner() {
        cc.log("button hideBanner")
        nativeBridge.hideBanner()
    },

    //INTERSTITIAL
    showInterstitial() {
        cc.log("showInterstitial")
        nativeBridge.showInterstitial()
    },

    //REWARDVIDEO
    showReward() {
        cc.log("showReward")
        nativeBridge.showRewardVideo();
    },

    //LEADERBOARD
    showLeaderboard() {
        cc.log("showLeaderboard")
        nativeBridge.showLeaderboard();
    },
    submitScore(event, Score) {
        cc.log("submitScore:", Score)
        nativeBridge.submitScore(Score)
    },

    //RATING
    requestReview() {
        cc.log("request Review")
        nativeBridge.requestReview();
    },
   
});
