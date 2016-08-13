package com.easyapps.teleprompter;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * Created by danielfmsouza on 13/08/2016.
 */
public class PausableAnimation extends Animation {

    private long mElapsedAtPause = 0;
    private boolean mPaused = false;

    public PausableAnimation(Context context, AttributeSet attrs){
        super(context, attrs);
    }

    @Override
    public boolean getTransformation(long currentTime, Transformation outTransformation) {
        if (mPaused && mElapsedAtPause == 0) {
            mElapsedAtPause = currentTime - getStartTime();
        }
        if (mPaused)
            setStartTime(currentTime - mElapsedAtPause);
        return super.getTransformation(currentTime, outTransformation);
    }

    public void pause() {
        mElapsedAtPause = 0;
        mPaused = true;
    }

    public void resume() {
        mPaused = false;
    }
}
