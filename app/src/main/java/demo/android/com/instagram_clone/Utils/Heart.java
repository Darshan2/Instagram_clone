package demo.android.com.instagram_clone.Utils;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

/**
 * Created by Darshan B.S on 10-06-2018. To handle toggle between hearts
 */

public class Heart {
    private static final String TAG = "Heart";
    private static final AccelerateInterpolator ACCELERATE_INTERPOLATOR = new AccelerateInterpolator();
    private static final DecelerateInterpolator DECELERATE_INTERPOLATOR = new DecelerateInterpolator();
    
    ImageView heartWhite, heartRed;

    public Heart(ImageView heartWhite, ImageView heartRed) {
        this.heartWhite = heartWhite;
        this.heartRed = heartRed;
    }

    public void toggleLike() {
        Log.d(TAG, "toggleLike: toggling heart");

        AnimatorSet animatorSet = new AnimatorSet();

        if(heartRed.getVisibility() == View.VISIBLE) {
            Log.d(TAG, "toggleLike: Toggling red heart OFF.");
            heartRed.setScaleX(0.1f);
            heartRed.setScaleY(0.1f);

            ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(heartRed, "scaleX", 1f, 0f);
            scaleDownX.setDuration(300);
            scaleDownX.setInterpolator(ACCELERATE_INTERPOLATOR);

            ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(heartRed, "scaleY", 1f, 0f);
            scaleDownY.setDuration(300);
            scaleDownY.setInterpolator(ACCELERATE_INTERPOLATOR);

            heartRed.setVisibility(View.GONE);
            heartWhite.setVisibility(View.VISIBLE);

            animatorSet.playTogether(scaleDownX, scaleDownY);

        } else  if (heartRed.getVisibility() == View.GONE) {
            Log.d(TAG, "toggleLike: Toggling red heart ON.");
            heartRed.setScaleX(0.1f);
            heartRed.setScaleY(0.1f);

            ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(heartRed, "scaleX", 0f, 1f);
            scaleDownX.setDuration(300);
            scaleDownX.setInterpolator(DECELERATE_INTERPOLATOR);

            ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(heartRed, "scaleY", 0f, 1f);
            scaleDownY.setDuration(300);
            scaleDownY.setInterpolator(DECELERATE_INTERPOLATOR);

            heartWhite.setVisibility(View.GONE);
            heartRed.setVisibility(View.VISIBLE);

            animatorSet.playTogether(scaleDownX, scaleDownY);
        }

        animatorSet.start();
    }
}
