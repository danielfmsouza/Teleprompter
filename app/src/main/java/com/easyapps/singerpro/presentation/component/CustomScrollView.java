package com.easyapps.singerpro.presentation.component;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ScrollView;
import android.widget.TextView;

import com.easyapps.singerpro.R;
import com.easyapps.singerpro.presentation.helper.CountDownTimer;

import java.util.ArrayList;
import java.util.List;

public class CustomScrollView extends ScrollView {
    private static final int DELAY_MILLIS = 50;

    private GestureDetector gestureDetector;
    private ObjectAnimator animator;
    private Runnable scrollChecker;
    private int scrollPrevPosition;
    private int bottomScrollYValue;
    private boolean hasFinishedAnimation = false;
    private OnFinishAnimationCallback onFinishAnimationCallback;

    private String fileName = "";
    private int scrollSpeed;
    private int[] timeRunning;
    private int[] timeStopped;
    private int totalTimers;
    private final List<CountDownTimerPrompter> timers = new ArrayList<>();

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
            System.out.println("onFling started");
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
            System.out.println("onFling stopped");
            if (getScrollY() != bottomScrollYValue) {
                initializeAnimator(getContext(), getScrollY(), bottomScrollYValue);
                if (wasAnimationRunning) {
                    animator.start();
                }
            }
            wasAnimationRunning = false;
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
            private boolean itMoved = false;
            private boolean wasAnimationRunning = false;

            public boolean onTouch(View view, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (animator != null) {
                        if (animator.isRunning() && !animator.isPaused()) {
                            wasAnimationRunning = true;
                            animator.pause();
                        }
                    }
                    System.out.println("touched down!");
                } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    System.out.println("MOVING!!!!");
                    itMoved = true;
                } else if (event.getAction() == android.view.MotionEvent.ACTION_UP) {
                    System.out.println("touched up!");
                    if (itMoved) {
                        animator.cancel();
                        initializeAnimator(getContext(), getScrollY(), bottomScrollYValue);
                        if (wasAnimationRunning)
                            animator.start();
                    } else if (!wasAnimationRunning) {
                        animator.resume();
                    }
                    itMoved = false;
                    wasAnimationRunning = false;
                }
                return false;
            }
        });
    }

    public void startStop() {
        startStopAnimation();
        CountDownTimerPrompter current = getCurrentTimer();
        startStopCurrentTimer(current);
    }

    public void startAnimation(TextView tvTimeCounter) {
        setBottomScrollYValue();
        initializeAnimator(getContext(), 0, bottomScrollYValue);
        createTimers(tvTimeCounter);
        initializePrompt();
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
        long duration = (toValue - fromValue) * scrollSpeed;
        animator = ObjectAnimator.ofInt(this, "scrollY", fromValue, toValue).setDuration(duration);
        animator.setTarget(this);
        animator.setInterpolator(new LinearInterpolator());
        animator.start();
        animator.pause();
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
//        int diff = (bottomScrollYValue - (getHeight() + getScrollY()));
//        if (!hasFinishedAnimation && !flingListener.isFlinging() && bottomScrollYValue != 0 && diff <= 0) {
//            onFinishAnimationCallback.onFinishAnimation(fileName);
//            hasFinishedAnimation = true;
//        } else
        super.onScrollChanged(x, y, oldX, oldY);
    }

    private void createTimers(TextView tvCountTimer) {
        final int SECONDS_TO_MILLISECONDS = 1000;
        final String textTimerWaiting = getResources().getString(R.string.tv_timeWaiting);
        final String textTimerRunning = getResources().getString(R.string.tv_timeRunning);

        int pos = 0;
        for (int i = 0; i < totalTimers; i++, pos++) {
            if (timeRunning[i] == 0) {
                timers.add(new CountDownTimerPrompter(
                        timeStopped[i] * SECONDS_TO_MILLISECONDS,
                        textTimerWaiting, pos, tvCountTimer));
                break;
            }
            timers.add(new CountDownTimerPrompter(
                    timeStopped[i] * SECONDS_TO_MILLISECONDS,
                    textTimerWaiting, pos, tvCountTimer));
            timers.add(new CountDownTimerPrompter(
                    timeRunning[i] * SECONDS_TO_MILLISECONDS,
                    textTimerRunning, ++pos, tvCountTimer));
        }
    }

    private void initializePrompt() {
        if (timers.size() > 0) {
            timers.get(0).start();
        } else {
            startStopAnimation();
        }
    }

    private class CountDownTimerPrompter extends CountDownTimer {
        private final String text;
        private boolean finished = false;
        private final int id;
        private final TextView tvCountTimer;

        CountDownTimerPrompter(long timeToCount, String text, int id, TextView tvCountTimer) {
            super(timeToCount, 1000);
            this.id = id;
            this.text = text;
            this.tvCountTimer = tvCountTimer;
        }

        @Override
        public void onTick(long countDownInterval) {
            setCountTimerText(String.format(text, id + 1, countDownInterval / 1000));
        }

        @Override
        public void onFinish() {
            startStopAnimation();
            if (!finished)
                startNextTimer(id + 1);

            finished = true;
            setCountTimerText("");
        }

        private void setCountTimerText(String text) {
            if (tvCountTimer != null) {
                tvCountTimer.setText(text);
            }
        }
    }

    private void startStopCurrentTimer(CountDownTimerPrompter currentTimer) {
        if (currentTimer == null) return;

        if (currentTimer.isPaused()) {
            currentTimer.resume();
        } else {
            currentTimer.pause();
        }
    }

    private void startStopAnimation() {
        if (animator == null) return;

        if (animator.isPaused()) {
            animator.resume();
        } else {
            animator.pause();
        }
    }

    private synchronized void startNextTimer(int id) {
        if (id >= 0 && id < timers.size()) {
            timers.get(id).start();
        }
    }

    private CountDownTimerPrompter getCurrentTimer() {
        for (CountDownTimerPrompter timer : timers) {
            if (timer.isRunning()) {
                return timer;
            }
        }
        return null;
    }

    public void setScrollSpeed(int scrollSpeed) {
        this.scrollSpeed = scrollSpeed;
    }

    public void setTimeRunning(int[] timeRunning) {
        this.timeRunning = timeRunning;
    }

    public void setTotalTimers(int totalTimers) {
        this.totalTimers = totalTimers;
    }

    public void setTimeStopped(int[] timeStopped) {
        this.timeStopped = timeStopped;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

}