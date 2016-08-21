package com.easyapps.teleprompter.components;

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
 * Created by danielfmsouza on 13/08/2016.
 */
public class PausablePrompterAnimation extends AnimationSet {

    private long mElapsedAtPause = 0;
    private boolean isPaused = false;

    private PausablePrompterAnimation(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean getTransformation(long currentTime, Transformation outTransformation) {
        if (isPaused && mElapsedAtPause == 0) {
            mElapsedAtPause = currentTime - getStartTime();
        }
        if (isPaused)
            setStartTime(currentTime - mElapsedAtPause);
        return super.getTransformation(currentTime, outTransformation);
    }

    public void pause() {
        mElapsedAtPause = 0;
        isPaused = true;
    }

    public void resume() {
        isPaused = false;
    }

    public void startStop() {
        if (isPaused) {
            resume();
        } else {
            pause();
        }
    }
    private static Animation createAnimationFromXml(Context c, XmlPullParser parser, int toYDelta)
            throws XmlPullParserException, IOException {

        return createAnimationFromXml(c, parser, null, Xml.asAttributeSet(parser), toYDelta);
    }

    public static Animation loadAnimation(Context context, int id, int toYDelta)
            throws Resources.NotFoundException {

        XmlResourceParser parser = null;
        try {
            parser = context.getResources().getAnimation(id);
            return createAnimationFromXml(context, parser, toYDelta);
        } catch (XmlPullParserException ex) {
            Resources.NotFoundException rnf = new Resources.NotFoundException("Can't load animation resource ID #0x" +
                    Integer.toHexString(id));
            rnf.initCause(ex);
            throw rnf;
        } catch (IOException ex) {
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
                                                    int toYDelta)
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

            if (name.equals("set")) {
                anim = new PausablePrompterAnimation(c, attrs);
                createAnimationFromXml(c, parser, (PausablePrompterAnimation) anim, attrs, toYDelta);
            } else if (name.equals("translate")) {
                anim = new TranslateAnimation(0, 0, 0, -toYDelta);
                anim.setDuration(toYDelta * 20);
            } else {
                throw new RuntimeException("Unknown animation name: " + parser.getName());
            }

            if (parent != null) {
                parent.addAnimation(anim);
            }
        }
        return anim;
    }

    public boolean isRunning(){
        return !isPaused;
    }
}
