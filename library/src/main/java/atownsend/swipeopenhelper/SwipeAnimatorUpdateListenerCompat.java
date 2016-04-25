package atownsend.swipeopenhelper;

import android.support.v4.animation.AnimatorUpdateListenerCompat;
import android.support.v4.animation.ValueAnimatorCompat;

public class SwipeAnimatorUpdateListenerCompat implements AnimatorUpdateListenerCompat {

    RecoverAnimation main;

    public SwipeAnimatorUpdateListenerCompat(RecoverAnimation source) {
        this.main = source;
    }

    @Override public void onAnimationUpdate(ValueAnimatorCompat animation) {
        main.setFraction(animation.getAnimatedFraction());
    }
}
