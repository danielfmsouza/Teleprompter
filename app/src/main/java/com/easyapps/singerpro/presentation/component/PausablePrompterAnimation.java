package com.easyapps.singerpro.presentation.component;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.util.AttributeSet;
import android.util.Xml;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.Transformation;
import android.view.animation.TranslateAnimation;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

/**
 * Created by daniel on 13/08/2016.
 * Animation that is pausable by touch on the screen, for example. Designed to work with animated
 * texts.
 */
public class PausablePrompterAnimation extends AnimationSet {

    private static final String ANIMATION_NAME_SET = "set";
    private static final String ANIMATION_NAME_TRANSLATE = "translate";
    private static String mFileName;

    private long mElapsedAtPause = 0;
    private boolean isPaused = false;

    private PausablePrompterAnimation(final Context context, AttributeSet attrs,
                                      final OnFinishAnimationListener listener) {
        super(context, attrs);

        setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                listener.onFinishAnimation(mFileName);
            }
        });
    }

    @Override
    public boolean getTransformation(long currentTime, Transformation outTransformation) {
        if (isPaused && mElapsedAtPause == 0)
            mElapsedAtPause = currentTime - getStartTime();
        if (isPaused)
            setStartTime(currentTime - mElapsedAtPause);

        return super.getTransformation(currentTime, outTransformation);
    }

    private void pause() {
        mElapsedAtPause = 0;
        isPaused = true;
    }

    private void resume() {
        isPaused = false;
    }

    void startStop() {
        if (isPaused) {
            resume();
        } else {
            pause();
        }
    }

    private static Animation createAnimationFromXml(Context c, XmlPullParser parser, int toYDelta,
                                                    int scrollSpeed, OnFinishAnimationListener listener)
            throws XmlPullParserException, IOException {

        return createAnimationFromXml(c, parser, null, Xml.asAttributeSet(parser), toYDelta,
                scrollSpeed, listener);
    }

    static Animation loadAnimation(Context context, int id, int toYDelta, int scrollSpeed,
                                   String fileName, OnFinishAnimationListener listener)
            throws Resources.NotFoundException {
        mFileName = fileName;

        XmlResourceParser parser = null;
        try {
            parser = context.getResources().getAnimation(id);
            return createAnimationFromXml(context, parser, toYDelta, scrollSpeed, listener);
        } catch (XmlPullParserException | IOException ex) {
            Resources.NotFoundException rnf = new Resources.NotFoundException("Can't load animation resource ID #0x" +
                    Integer.toHexString(id));
            rnf.initCause(ex);
            throw rnf;
        } finally {
            if (parser != null) parser.close();
        }
    }

    private static Animation createAnimationFromXml(Context c, XmlPullParser parser,
                                                    AnimationSet parent, AttributeSet attrs,
                                                    int toYDelta, int scrollSpeed,
                                                    OnFinishAnimationListener listener)
            throws XmlPullParserException, IOException {

        Animation anim = null;

        // Make sure we are on a start tag.
        int type;
        int depth = parser.getDepth();

        while (((type = parser.next()) != XmlPullParser.END_TAG || parser.getDepth() > depth)
                && type != XmlPullParser.END_DOCUMENT) {

            if (type != XmlPullParser.START_TAG) {
                continue;
            }

            String name = parser.getName();

            switch (name) {
                case ANIMATION_NAME_SET:
                    anim = new PausablePrompterAnimation(c, attrs, listener);
                    createAnimationFromXml(c, parser, (PausablePrompterAnimation) anim, attrs,
                            toYDelta, scrollSpeed, listener);
                    break;
                case ANIMATION_NAME_TRANSLATE:
                    anim = new TranslateAnimation(0, 0, 0, -toYDelta);
                    anim.setDuration(toYDelta * scrollSpeed);
                    break;
                default:
                    throw new RuntimeException("Unknown animation name: " + parser.getName());
            }

            if (parent != null) {
                parent.addAnimation(anim);
            }
        }
        return anim;
    }

    public interface OnFinishAnimationListener {
        void onFinishAnimation(String lyricName);
    }
}
