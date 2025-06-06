# CC2 NativeBridge
A Native helper for CocosCreater 2.4+. Implementation of AdMob,GameCenter,AppRating,ConsentRequest etc.
Easy to configure and enhance.

## ANDROID
### Preparation
Set up a proper android leaderboard with oAuth2 key in https://console.cloud.google.com/ For Google play services to work. 

### Implement
Set/Update in gradle.properties

    PROP_MIN_SDK_VERSION=23

Add to gradle.properties
    
    #NECESSARY FOR ADMOB
    android.useAndroidX=true
    android.enableJetifier=true

Add to build.gradle dependencies
    
    //ADMOB
    implementation("com.google.android.gms:play-services-ads:24.3.0")
    implementation("com.google.android.ump:user-messaging-platform:3.1.0")

    //REVIEW
    implementation ('com.google.android.play:review:2.0.2')

    //GOOGLE PLAY SERVICE
    implementation "com.google.android.gms:play-services-games-v2:+"

Add to Android Manifest inside "application" tag

     <!-- Sample AdMob app ID: ca-app-pub-3940256099942544~3347511713 -->
    <meta-data
        android:name="com.google.android.gms.ads.APPLICATION_ID"
        android:value="@string/admobAppId"/>

    <meta-data 
        android:name="com.google.android.gms.games.APP_ID"
        android:value="@string/app_id"/>

Copy Files

    Copy idValue.xml to the res/values (Update values for your app / Set AdBanner size)
    Copy adController.java to app/src/org/cocos2dx/javascript //Admob
    Copy appRating.java to app/src/org/cocos2dx/javascript //AppRating
    Copy gameCenter.java to app/src/org/cocos2dx/javascript //Gamecenter/Leaderboard


Add to AppActivity.java in the onCreate function

    //IMPLEMENT ADCONTROLLER/Admob
    adController adcontroller = new adController(this,mFrameLayout);
    //IMPLEMENT GAMECENTER
    gameCenter.initialize(this);


## iOs
### Preparation
Set up a cocoaPods on your system 

### Implement
Add cocoapods to the project. Run "pod init" in xcode project folder.

Add to "Podfile" file
    
    pod 'Google-Mobile-Ads-SDK'
    pod 'GoogleUserMessagingPlatform'

run "pod install"

! Use the newly created .xcworkspace file for further proceed in xcode

Add to "Buildsettings/Header Search Paths" for debug and release
    
    $(inherited)

Change "Project/Info" Based on Configuration File to PodsFile

Update your app's Info.plist file to add two keys:
    
    <key>GADApplicationIdentifier</key>
        <string>ca-app-pub-3940256099942544~1458002511</string>
    <key>SKAdNetworkItems</key>
    <array>
        <dict>
            <key>SKAdNetworkIdentifier</key>
            <string>cstr6suwn9.skadnetwork</string>
        </dict>
    </array>
https://developers.google.com/admob/ios/quick-start


Copy Files
    Copy idValues.xml to project folder
    Copy adController.h & adController.m to project (includes adMob)
    
    add gemaceenter in "Signing & Capabilits" tab
    add gemekit in "General" tab under Frameworks,Libraries

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