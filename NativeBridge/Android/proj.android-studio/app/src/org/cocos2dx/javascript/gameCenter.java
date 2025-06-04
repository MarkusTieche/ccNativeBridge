package org.cocos2dx.javascript;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.games.PlayGames;
import com.google.android.gms.games.PlayGamesSdk;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

public class gameCenter {

    private static final String TAG = "gameCenter";
    private static final int RC_LEADERBOARD_UI = 9004;

    private static Activity activity;

    public static void initialize(Activity act) {
        activity = act;
        PlayGamesSdk.initialize(activity);
        Log.d(TAG, "Play Games SDK initialized");

    }

    public static void signIn() {
        PlayGames.getGamesSignInClient(activity)
                .isAuthenticated()
                .addOnSuccessListener(isAuthenticated -> {
                    if (isAuthenticated.isAuthenticated()) {
                        Log.d(TAG, "Already signed in to Play Games");
                    } else {
                        PlayGames.getGamesSignInClient(activity)
                                .signIn()
                                .addOnSuccessListener(result -> Log.d(TAG, "Play Games sign-in successful"))
                                .addOnFailureListener(e -> Log.e(TAG, "Play Games sign-in failed", e));
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error checking sign-in status", e));
    }

    public static void signOut() {

    }

    public static boolean isSignedIn() {
        return PlayGames.getGamesSignInClient(activity)
                .isAuthenticated()
                .getResult()
                .isAuthenticated();
    }

    public static void submitScore(String leaderboardId, long score) {
        PlayGames.getLeaderboardsClient(activity)
                .submitScore(leaderboardId, score);
        Log.d(TAG, "Submitted score: " + score + " to leaderboard: " + leaderboardId);
    }

    public static void showLeaderboard(String leaderboardId) {
        PlayGames.getLeaderboardsClient(activity)
                .getLeaderboardIntent(leaderboardId)
                .addOnSuccessListener(new OnSuccessListener<Intent>() {
                    @Override
                    public void onSuccess(Intent intent) {
                        activity.startActivityForResult(intent, RC_LEADERBOARD_UI);
                        Log.d(TAG, "Showing leaderboard UI");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Log.e(TAG, "Failed to load leaderboard UI", e);
                    }
                });
    }
}
