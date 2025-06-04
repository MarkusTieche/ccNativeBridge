package org.cocos2dx.javascript;  // Replace with your package name

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.play.core.review.testing.FakeReviewManager;

import org.cocos2dx.lib.Cocos2dxActivity;

public class appRating {

    private static final String TAG = "appRating";

    public static void requestAppReview() {
        final Activity activity = (Activity) Cocos2dxActivity.getContext();
        if (activity == null) {
            Log.e(TAG, "Activity context is null!");
            return;
        }

        ReviewManager manager = ReviewManagerFactory.create(activity);
        //ReviewManager manager = new FakeReviewManager(activity);
        com.google.android.gms.tasks.Task<ReviewInfo> request = manager.requestReviewFlow();

        request.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                ReviewInfo reviewInfo = task.getResult();
                Task<Void> flow = manager.launchReviewFlow(activity, reviewInfo);

                flow.addOnCompleteListener(task2 -> {
                    Log.d(TAG, "In-app review flow finished");
                });
            } else {
                Log.e(TAG, "In-app review request failed, opening Play Store fallback");
                openPlayStore(activity);
            }
        });
    }

    private static void openPlayStore(Activity activity) {
        String appPackageName = activity.getPackageName();
        try {
            activity.startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException e) {
            activity.startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }
}

//How to call from Cocos Creator JS
//jsb.reflection.callStaticMethod("com/yourpackage/appRating", "requestAppReview", "()V");
