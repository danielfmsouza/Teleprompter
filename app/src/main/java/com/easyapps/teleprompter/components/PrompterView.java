package com.easyapps.teleprompter.components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.widget.TextView;

import com.easyapps.teleprompter.R;

/**
 * Created by danielfmsouza on 13/08/2016.
 */
public class PrompterView extends TextView {
    private int animationId = 0;

    public PrompterView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PrompterView(Context context) {
        super(context);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        Animation animation = PausablePrompterAnimation.loadAnimation(getContext(), animationId, b);
        startAnimation(animation);
    }

    public void setAnimationId(int id) {
        animationId = id;
    }

    public void startStop() {
        if (getAnimation() != null)
            ((PausablePrompterAnimation) getAnimation()).startStop();
    }

    public boolean isRunning() {
        if (getAnimation() != null)
            return ((PausablePrompterAnimation) getAnimation()).isRunning();
        return false;
    }
}
