# CC2 NativeBridge
A Native helper for CocosCreater 2.4+. Implementation of AdMob,GameAnalytics,GameCenter,AppRating,ConsentRequest etc.
Easy to configure and enhance.

## How it works

## Preparation
Set up a proper android leaderboard with oAuth2 key in https://console.cloud.google.com/. For Google play services to work. 

## ANDROID
Copy the files from Android Folder to your Android build Folder. Dont just overwrite the folder in your Android project. Copy the single files.

Add to AppActivity.java in the onCreate function

    //IMPLEMENT ADCONTROLLER
    adController adcontroller = new adController(this,mFrameLayout);
    //IMPLEMENT REVIEW
    appRating apprating = new appRating(this);
    //GAMECENTER
    gameCenter gameCenter = new gameCenter(this);

Add to Android Manifest

     <!-- Sample AdMob app ID: ca-app-pub-3940256099942544~3347511713 -->
    <meta-data
        android:name="com.google.android.gms.ads.APPLICATION_ID"
        android:value="ca-app-pub-3940256099942544~3347511713"/>

    <meta-data 
        android:name="com.google.android.gms.games.APP_ID"
        android:value="21388063390"/>

Add to build.gradle dependencies
    //ADMOB
    implementation("com.google.android.gms:play-services-ads:24.3.0")
    implementation("com.google.android.ump:user-messaging-platform:3.1.0")

    //REVIEW
    implementation ('com.google.android.play:review:2.0.2')

    //GOOGLE PLAY SERVICE
    implementation "com.google.android.gms:play-services-games-v2:+"

Set PROP_MIN_SDK_VERSION=23 in gradle.properties
Add to gradle.properties
    
    #NECESSARY FOR ADMOB
    android.useAndroidX=true
    android.enableJetifier=true

## iOs
add cocoapods to the project
run "pod init" in xcode project folder
intsall via pod file
  - pod 'Google-Mobile-Ads-SDK'
  - pod 'GoogleUserMessagingPlatform'

run "pod install"

add to Header Search Paths for debug and release
    
    $(inherited)

use the newly created .xcworkspace file for further proceed in xcode


- copy idValues.xml to project folder
- copy adController.h & adController.m to project (includes adMob)
- add gemaceenter in "Signing & Capabilits" tab
- add gemekit in "General" tab under Frameworks,Libraries

## Use in CocosCreator
Call function in any script

    NativeBridge.showBanner();

Available functions
        
    requestConsent()
    showBanner()
    hideBanner()
    requestReview()
    showLeaderboard()
    submitScore(int:score)
    interstitialAvailable() //return true or false
    showInterstitial()
    rewardVideoAvailable() //return true or false
    showRewardVideo()

Available callbacks

    cc.systemEvent.on('interstitial-ad-closed', function () {})
    cc.systemEvent.on('rewardVideo-ad-closed', function () {})