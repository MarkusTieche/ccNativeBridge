package org.cocos2dx.javascript;

import static android.provider.Settings.System.getString;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.ump.ConsentForm;
import com.google.android.ump.ConsentInformation;
import com.google.android.ump.ConsentRequestParameters;
import com.google.android.ump.FormError;
import com.google.android.ump.UserMessagingPlatform;
import com.inkfood.test.R; //NEED TO CHANGE/REIMPORT

import org.cocos2dx.lib.Cocos2dxHelper;
import org.cocos2dx.lib.Cocos2dxJavascriptJavaBridge;

public class adController {

    private static final String TAG = "adController";

    private static adController instance;   // SINGLETON instance

    private static Activity activity;
    private final FrameLayout adContainer;

    private static AdView bannerAdView;
    private static InterstitialAd interstitialAd;
    private static RewardedAd rewardedAd;

    private ConsentInformation consentInformation;
    private ConsentForm consentForm;

    private static boolean isPersonalized = false;


    public adController(Activity activity, FrameLayout adContainer) {
        this.activity = activity;
        this.adContainer = adContainer;

        MobileAds.initialize(activity, initializationStatus -> {
        });

        consentInformation = UserMessagingPlatform.getConsentInformation(activity);

        instance = this;  // assign singleton instance

        loadAdsBasedOnConsent();
    }

    public static void resetConsent()
    {
        instance.consentInformation.reset();
    }

    public static void requestConsent() {
        if (instance == null) {
            Log.e(TAG, "adController instance not initialized");
            return;
        }

        ConsentRequestParameters params = new ConsentRequestParameters.Builder()
                .setTagForUnderAgeOfConsent(false)
                .build();

        instance.consentInformation.requestConsentInfoUpdate(
                instance.activity,
                params,
                new ConsentInformation.OnConsentInfoUpdateSuccessListener() {
                    @Override
                    public void onConsentInfoUpdateSuccess() {
                        if (instance.consentInformation.isConsentFormAvailable()) {
                            instance.loadConsentForm();
                        } else {
                            Log.d(TAG, "Consent form not available");
                            instance.loadAdsBasedOnConsent();
                        }
                    }
                },
                new ConsentInformation.OnConsentInfoUpdateFailureListener() {
                    @Override
                    public void onConsentInfoUpdateFailure(@NonNull FormError formError) {
                        Log.e(TAG, "Consent info update failed: " + formError.getMessage());
                    }
                }
        );
    }

    private void loadConsentForm() {
        UserMessagingPlatform.loadConsentForm(
                activity,
                new UserMessagingPlatform.OnConsentFormLoadSuccessListener() {
                    @Override
                    public void onConsentFormLoadSuccess(@NonNull ConsentForm form) {
                        consentForm = form;

                        if (consentInformation.getConsentStatus() == ConsentInformation.ConsentStatus.REQUIRED) {
                            consentForm.show(
                                    activity,
                                    formError -> {
                                        if (formError == null) {
                                            consentInformation.requestConsentInfoUpdate(
                                                    activity,
                                                    new ConsentRequestParameters.Builder().build(),
                                                    new ConsentInformation.OnConsentInfoUpdateSuccessListener() {
                                                        @Override
                                                        public void onConsentInfoUpdateSuccess() {
                                                            loadAdsBasedOnConsent();
                                                        }
                                                    },
                                                    new ConsentInformation.OnConsentInfoUpdateFailureListener() {
                                                        @Override
                                                        public void onConsentInfoUpdateFailure(@NonNull FormError error) {
                                                            Log.e(TAG, "Consent info update after form dismissed failed: " + error.getMessage());
                                                        }
                                                    }
                                            );
                                        } else {
                                            Log.e(TAG, "Consent form dismissed with error: " + formError.getMessage());
                                        }
                                    }
                            );
                        } else {
                            loadAdsBasedOnConsent();
                        }
                    }
                },
                formError -> Log.e(TAG, "Consent form load failed: " + formError.getMessage())
        );
    }

    private void loadAdsBasedOnConsent() {
        isPersonalized = (consentInformation.getConsentStatus() == ConsentInformation.ConsentStatus.OBTAINED);
        Log.d(TAG, "Consent status: " + consentInformation.getConsentStatus() + ", personalized ads allowed: " + isPersonalized);

        loadBannerAd(isPersonalized);
        loadInterstitialAd(isPersonalized);
        loadRewardedAd(isPersonalized);
    }

    private AdRequest buildAdRequest(boolean personalized) {
        AdRequest.Builder builder = new AdRequest.Builder();

        if (!personalized) {
            Bundle extras = new Bundle();
            extras.putString("npa", "1");
            builder.addNetworkExtrasBundle(AdMobAdapter.class, extras);
        }

        return builder.build();
    }

    public void loadBannerAd(boolean personalized) {
        if (bannerAdView == null) {
            bannerAdView = new AdView(activity);
            bannerAdView.setAdSize(getAdSizeByName(activity.getString(R.string.banner_size)));
            bannerAdView.setAdUnitId(activity.getString(R.string.banner_ad_unit_id));


            adContainer.addView(bannerAdView, new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    Gravity.BOTTOM
            ));
        }

        bannerAdView.loadAd(buildAdRequest(personalized));
    }

    public static AdSize getAdSizeByName(String name) {
        if (name == null) return null;

        switch (name.toUpperCase()) {
            case "BANNER":
                return AdSize.BANNER; // 320x50
            case "LARGE_BANNER":
                return AdSize.LARGE_BANNER; // 320x100
            case "FULL_BANNER":
                return AdSize.FULL_BANNER; // 468x60
            case "LEADERBOARD":
                return AdSize.LEADERBOARD; // 728x90
            case "MEDIUM_RECTANGLE":
                return AdSize.MEDIUM_RECTANGLE; // 300x250
            case "SMART_BANNER": // Deprecated but still works
                return AdSize.SMART_BANNER;
            case "WIDE_SKYSCRAPER":
                return AdSize.WIDE_SKYSCRAPER; // 160x600
            case "FLUID":
                return AdSize.FLUID;
            case "ADAPTIVE":
                return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(activity, 360);
            default:
                return null; // Unknown or unsupported size
        }
    }

    public static void showBanner() {
        if (bannerAdView != null && bannerAdView.getVisibility() != View.VISIBLE) {

            activity.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    bannerAdView.setVisibility(View.VISIBLE);
                }
            });

            Log.d(TAG, "Banner shown");
        }
    }

    public static void hideBanner() {
        if (bannerAdView != null && bannerAdView.getVisibility() != View.GONE) {
            activity.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    bannerAdView.setVisibility(View.GONE);
                }
            });
            Log.d(TAG, "Banner hidden");
        }
    }

    public void loadInterstitialAd(boolean personalized) {
        InterstitialAd.load(activity, activity.getString(R.string.interstitial_ad_unit_id), buildAdRequest(personalized),
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd ad) {
                        interstitialAd = ad;
                        Log.d(TAG, "Interstitial loaded");

                        ad.setFullScreenContentCallback(new com.google.android.gms.ads.FullScreenContentCallback() {
                            @Override
                            public void onAdDismissedFullScreenContent() {
                                Log.d(TAG, "Interstitial dismissed");
                                interstitialAd = null;
                                onInterstitialClosed();
                                loadInterstitialAd(personalized);
                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(AdError adError) {
                                Log.e(TAG, "Interstitial failed to show: " + adError.getMessage());
                            }
                        });
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        Log.e(TAG, "Interstitial failed to load: " + loadAdError.getMessage());
                        interstitialAd = null;
                    }
                });
    }

    public static boolean isInterstitialAvailable() {
        return interstitialAd != null;
    }

    public static void showInterstitial() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (interstitialAd != null && instance != null) {
                    interstitialAd.show(instance.activity);

                } else {
                    //Log.d(TAG, "The interstitial ad is still loading.");
                    System.out.println("Interstitial fail");
                    //System.out.println(mInterstitialAd);
                }
            }
        });
    }

    public void loadRewardedAd(boolean personalized) {
        RewardedAd.load(activity, activity.getString(R.string.rewarded_ad_unit_id), buildAdRequest(personalized),
                new RewardedAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull RewardedAd ad) {
                        rewardedAd = ad;
                        Log.d(TAG, "Rewarded ad loaded");

                        ad.setFullScreenContentCallback(new com.google.android.gms.ads.FullScreenContentCallback() {
                            @Override
                            public void onAdDismissedFullScreenContent() {
                                Log.d(TAG, "Rewarded ad dismissed");
                                rewardedAd = null;
                                onRewardedAdClosed();
                                loadRewardedAd(personalized);
                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(AdError adError) {
                                Log.e(TAG, "Rewarded ad failed to show: " + adError.getMessage());
                            }
                        });
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        Log.e(TAG, "Rewarded ad failed to load: " + loadAdError.getMessage());
                        rewardedAd = null;
                    }
                });
    }

    public static boolean isRewardedAdAvailable() {
        return rewardedAd != null;
    }

    public static void showRewardedAd() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (rewardedAd != null && instance != null) {
                    rewardedAd.show(instance.activity, rewardItem -> {
                        Log.d(TAG, "User earned reward: " + rewardItem.getAmount() + " " + rewardItem.getType());
                        // Optionally send reward to JS side
                    });
                }
            }
        });
    }

    // Optional setters for listeners, still instance methods
    public void onInterstitialClosed() {
        Cocos2dxHelper.runOnGLThread(new Runnable() {
            @Override
            public void run() {
                Cocos2dxJavascriptJavaBridge.evalString("cc.systemEvent.emit('interstitial-ad-closed');");
            }});
        System.out.println("Ad dismissed/closed fullscreen content.");
    }

    public void onRewardedAdClosed() {
        Cocos2dxHelper.runOnGLThread(new Runnable() {
            @Override
            public void run() {
                Cocos2dxJavascriptJavaBridge.evalString("cc.systemEvent.emit('rewardVideo-ad-closed');");
            }});
        System.out.println("Ad dismissed/closed fullscreen content.");
    };
};