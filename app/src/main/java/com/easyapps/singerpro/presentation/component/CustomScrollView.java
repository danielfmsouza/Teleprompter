package com.easyapps.singerpro.presentation.component;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ScrollView;
import android.widget.TextView;

import com.easyapps.singerpro.R;
import com.easyapps.singerpro.domain.model.lyric.Configuration;

import java.sql.SQLOutput;

public class CustomScrollView extends ScrollView implements PrompterTimers.TimerListener {
    private static final int DELAY_MILLIS = 50;

    private ObjectAnimator animator;
    private Runnable scrollChecker;
    private int scrollPrevPosition;
    private int bottomScrollYValue;
    private boolean hasFinishedAnimation = false;
    private boolean hasStartedAnimation = false;
    private OnFinishAnimationCallback finishAnimationCallback;

    private String fileName = "";
    private Configuration timersConfig;

    private boolean isDragging = false;
    private long lastScrollUpdate = -1;
    private boolean wasAnimationRunning = false;

    private class ScrollStateHandler implements Runnable {

        @Override
        public void run() {
            long currentTime = System.currentTimeMillis();
            if ((currentTime - lastScrollUpdate) > 100) {
                lastScrollUpdate = -1;
                onScrollEnd();
            } else {
                postDelayed(this, 100);
            }
        }
    }

    private void onScrollStart() {
    }

    private void onScrollEnd() {
        if (isDragging) return;

        int diff = bottomScrollYValue - (getHeight() + getScrollY());
        if (diff <= 0)
            finishAnimationCallback.onFinishAnimation(fileName);

        initializeAnimator(getScrollY(), bottomScrollYValue);
        if (wasAnimationRunning)
            animator.start();
        wasAnimationRunning = false;
    }

    public interface OnFinishAnimationCallback {
        void onFinishAnimation(String fileScrolled);

        void onSwipeNext(String fileScrolled);

        void onSwipePrevious(String fileScrolled);
    }

    public CustomScrollView(Context context) {
        this(context, null, 0);
    }

    public CustomScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        setFinishAnimationCallback(context);

        setOnTouchListener(new OnTouchListener() {

            private static final int MIN_DRAGGING_DISTANCE = 30;
            private float downY;

            public boolean onTouch(View view, MotionEvent event) {
                if (!hasStartedAnimation) return true;

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        downY = event.getY();
                        onFingerPress();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        isDragging = true;
                        break;
                    case MotionEvent.ACTION_UP:
                        float deltaY = downY - event.getY();
                        onFingerRelease(deltaY);
                        break;
                }
                return false;
            }

            private void onFingerPress() {
                if (animator != null) {
                    if (animator.isRunning() && !animator.isPaused()) {
                        wasAnimationRunning = true;
                        animator.pause();
                    }
                }
                isDragging = true;
            }

            private void onFingerRelease(float deltaY) {
                if (Math.abs(deltaY) < MIN_DRAGGING_DISTANCE) {
                    if (wasAnimationRunning)
                        animator.pause();
                    else {
                        initializeAnimator(getScrollY(), bottomScrollYValue);
                        animator.start();
                    }
                    wasAnimationRunning = false;
                }
                isDragging = false;
            }
        });
    }

    @Override
    protected void onScrollChanged(int x, int y, int oldX, int oldY) {
        super.onScrollChanged(x, y, oldX, oldY);
        if (lastScrollUpdate == -1) {
            onScrollStart();
            postDelayed(new ScrollStateHandler(), 100);
        }

        lastScrollUpdate = System.currentTimeMillis();
    }

    @Override
    public void onFinishTimer() {
        startStopAnimation();
    }

    public void startAnimation(TextView tvTimeCounter) {
        setBottomScrollYValue();
        initializeAnimator(0, bottomScrollYValue);
        createAndInitializeTimers(tvTimeCounter);
        hasStartedAnimation = true;
    }

    public void cancelAnimation() {
        if (animator != null)
            animator.cancel();
    }

    private void setFinishAnimationCallback(Context context) {
        if (context instanceof OnFinishAnimationCallback) {
            finishAnimationCallback = (OnFinishAnimationCallback) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFinishAnimationCallback");
        }
    }

    private void setBottomScrollYValue() {
        View view = getChildAt(getChildCount() - 1);
        bottomScrollYValue = view.getBottom();
    }

    private void initializeAnimator(int fromValue, int toValue) {
        long duration = (toValue - fromValue) * timersConfig.getScrollSpeed();
        if (animator != null) animator.pause();
        animator = ObjectAnimator.ofInt(this, "scrollY", fromValue, toValue).setDuration(duration);
        animator.setTarget(this);
        animator.setInterpolator(new LinearInterpolator());
        animator.start();
        animator.pause();
    }

    private void createAndInitializeTimers(TextView tvCountTimer) {
        final String textTimerStopped = getResources().getString(R.string.tv_timeWaiting);
        final String textTimerRunning = getResources().getString(R.string.tv_timeRunning);

        PrompterTimers prompterTimers = new PrompterTimers(
                timersConfig,
                this,
                tvCountTimer,
                textTimerStopped,
                textTimerRunning);

        if (!prompterTimers.initialize()) {
            startStopAnimation();
        }
    }

    private void startStopAnimation() {
        if (animator == null) {
            return;
        }

        if (animator.isPaused()) {
            animator.resume();
        } else {
            animator.pause();
        }
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setTimersConfig(Configuration timersConfig) {
        this.timersConfig = timersConfig;
    }
}