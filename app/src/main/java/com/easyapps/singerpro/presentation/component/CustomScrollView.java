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

public class CustomScrollView extends ScrollView implements PrompterTimers.TimerListener {
    private static final int DELAY_MILLIS = 50;

    private ObjectAnimator animator;
    private Runnable scrollChecker;
    private int scrollPrevPosition;
    private int bottomScrollYValue;
    private boolean hasFinishedAnimation = false;
    private OnFinishAnimationCallback onFinishAnimationCallback;

    private String fileName = "";
    private Configuration timersConfig;
    private PrompterTimers prompterTimers;

    public interface OnFinishAnimationCallback {
        void onFinishAnimation(String fileScrolled);
    }

    private interface OnFlingListener {
        public void onFlingStarted();

        public boolean isFlinging();

        public void onFlingStopped();
    }

    private OnFlingListener flingListener = new OnFlingListener() {

        private boolean wasAnimationRunning = false;
        private boolean isFlinging = false;

        @Override
        public void onFlingStarted() {
            isFlinging = true;
            if (animator != null) {
                if (animator.isRunning() && !animator.isPaused()) {
                    wasAnimationRunning = true;
                }
                animator.end();
            }
        }

        @Override
        public void onFlingStopped() {
            isFlinging = false;
            if (getScrollY() != bottomScrollYValue) {
                initializeAnimator(getContext(), getScrollY(), bottomScrollYValue);
                if (wasAnimationRunning) {
                    animator.start();
                }
            }
            wasAnimationRunning = false;

            //call to cover the edge case when the fling goes to the end of the scroll and it has to end
            onScrollChanged(getScrollX(), getScrollY(), getScrollX(), getScrollY());
        }

        @Override
        public boolean isFlinging() {
            return isFlinging;
        }
    };

    public CustomScrollView(Context context) {
        this(context, null, 0);
    }

    public CustomScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        setOnFinishAnimationCallback(context);

        scrollChecker = new Runnable() {
            @Override
            public void run() {
                int position = getScrollY();
                if (scrollPrevPosition - position == 0) {
                    flingListener.onFlingStopped();
                    removeCallbacks(scrollChecker);
                } else {
                    scrollPrevPosition = getScrollY();
                    postDelayed(scrollChecker, DELAY_MILLIS);
                }
            }
        };

        setOnTouchListener(new OnTouchListener() {
            private boolean isDragging = false;
            private boolean wasAnimationRunning = false;

            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        onFingerPress();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        onDragging();
                        break;
                    case MotionEvent.ACTION_UP:
                        onFingerRelease();
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
            }

            private void onDragging() {
                isDragging = true;
            }

            private void onFingerRelease() {
                if (isDragging) {
                    animator.cancel();
                    initializeAnimator(getContext(), getScrollY(), bottomScrollYValue);
                    if (wasAnimationRunning)
                        animator.start();
                } else if (!wasAnimationRunning) {
                    animator.resume();
                }
                isDragging = false;
                wasAnimationRunning = false;
            }
        });
    }

    @Override
    public void fling(int velocityY) {
        super.fling(velocityY);

        if (flingListener != null) {
            flingListener.onFlingStarted();
            post(scrollChecker);
        }
    }

    @Override
    protected void onScrollChanged(int x, int y, int oldX, int oldY) {
        int diff = (bottomScrollYValue - (getHeight() + getScrollY()));
        if (!hasFinishedAnimation && !flingListener.isFlinging() && bottomScrollYValue != 0 && diff <= 0) {
            onFinishAnimationCallback.onFinishAnimation(fileName);
            hasFinishedAnimation = true;
        } else
            super.onScrollChanged(x, y, oldX, oldY);
    }

    @Override
    public void onFinishTimer() {
        startStopAnimation();
    }

    public void startStop() {
        startStopAnimation();
        prompterTimers.startStopCurrentTimer();
    }

    public void startAnimation(TextView tvTimeCounter) {
        setBottomScrollYValue();
        initializeAnimator(getContext(), 0, bottomScrollYValue);
        createAndInitializeTimers(tvTimeCounter);
    }

    public void cancelAnimation() {
        if (animator != null)
            animator.cancel();
    }

    private void setOnFinishAnimationCallback(Context context) {
        if (context instanceof OnFinishAnimationCallback) {
            onFinishAnimationCallback = (OnFinishAnimationCallback) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFinishAnimationCallback");
        }
    }

    private void setBottomScrollYValue() {
        View view = getChildAt(getChildCount() - 1);
        bottomScrollYValue = view.getBottom();
    }

    private void initializeAnimator(Context context, int fromValue, int toValue) {
        long duration = (toValue - fromValue) * timersConfig.getScrollSpeed();
        animator = ObjectAnimator.ofInt(this, "scrollY", fromValue, toValue).setDuration(duration);
        animator.setTarget(this);
        animator.setInterpolator(new LinearInterpolator());
        animator.start();
        animator.pause();
    }

    private void createAndInitializeTimers(TextView tvCountTimer) {
        final String textTimerStopped = getResources().getString(R.string.tv_timeWaiting);
        final String textTimerRunning = getResources().getString(R.string.tv_timeRunning);

        prompterTimers = new PrompterTimers(
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