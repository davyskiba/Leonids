package com.plattysoft.leonids;

import com.plattysoft.leonids.initializers.ParticleInitializer;
import com.plattysoft.leonids.modifiers.FadeInModifier;
import com.plattysoft.leonids.modifiers.ParticleModifier;

import android.graphics.Bitmap;
import android.view.ViewGroup;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class ParticleSystem {

    private static final long TIMMERTASK_INTERVAL = 50;
    private ViewGroup mParentView;
    private int mMaxParticles;
    private Random mRandom;

    private ParticleField mDrawingView;

    private ArrayList<Particle> mParticles;
    private final ArrayList<Particle> mActiveParticles = new ArrayList<Particle>();
    private long mTimeToLive;
    private long mCurrentTime = 0;


    private List<ParticleModifier> mModifiers;
    private List<ParticleInitializer> mLateInitializers;
    private Timer mTimer;
    private final ParticleTimerTask mTimerTask = new ParticleTimerTask(this);

    private int[] mParentLocation;

    private int mEmiterX;
    private int mEmiterY;

    private static class ParticleTimerTask extends TimerTask {

        private final WeakReference<ParticleSystem> mPs;

        public ParticleTimerTask(ParticleSystem ps) {
            mPs = new WeakReference<ParticleSystem>(ps);
        }

        @Override
        public void run() {
            if (mPs.get() != null) {
                ParticleSystem ps = mPs.get();
                ps.onUpdate(ps.mCurrentTime);
                ps.mCurrentTime += TIMMERTASK_INTERVAL;
            }
        }
    }

    private ParticleSystem(int maxParticles, long timeToLive, ViewGroup parentViewGroup) {
        mRandom = new Random();
        mParentLocation = new int[2];

        mParentView = parentViewGroup;
        setParentViewGroup(mParentView);

        mModifiers = new ArrayList<ParticleModifier>();
        mLateInitializers = new ArrayList<ParticleInitializer>();

        mMaxParticles = maxParticles;
        // Create the particles

        mParticles = new ArrayList<Particle>();
        mTimeToLive = timeToLive;
    }

    /**
     * Utility constructor that receives a Drawable
     *
     * @param maxParticles    The maximum number of particles
     * @param particleBitmap  The drawable to use as particle (supports Bitmaps and Animations)
     * @param timeToLive      The time to live for the particles
     * @param parentViewGroup The the parent ViewGroup of the particle system
     */
    public ParticleSystem(int maxParticles, Bitmap particleBitmap, long timeToLive, ViewGroup parentViewGroup) {
        this(maxParticles, timeToLive, parentViewGroup);

        for (int i = 0; i < mMaxParticles; i++) {
            mParticles.add(new Particle(particleBitmap));
        }
    }

    /**
     * Configures a fade in for the particles when they appear
     *
     * @param durationMiliseconds fade in duration in milliseconds
     */
    public ParticleSystem setFadeIn(long durationMiliseconds) {
        mModifiers.add(new FadeInModifier(durationMiliseconds));
        return this;
    }

    /**
     * Adds Particle initializer that will be executed after rest of particle activation is
     * complete
     *
     * @param particleInitializer The late initializer to be executed after particles .
     * @return This.
     */
    public ParticleSystem addLateParticleInitializer(ParticleInitializer particleInitializer) {
        mLateInitializers.add(particleInitializer);
        return this;
    }

    /**
     * Initializes the parent view group. This needs to be done before any other configuration or
     * emitting is done. Drawing will be done to a child that is added to this view. So this view
     * needs to allow displaying an arbitrary sized view on top of its other content.
     *
     * @param viewGroup The view group to use.
     * @return This.
     */
    public ParticleSystem setParentViewGroup(ViewGroup viewGroup) {
        mParentView = viewGroup;
        if (mParentView != null) {
            mParentView.getLocationInWindow(mParentLocation);
        }
        return this;
    }

    private void startEmiting() {
        // Add a full size view to the parent view
        mDrawingView = new ParticleField(mParentView.getContext());
        mParentView.addView(mDrawingView);
        mDrawingView.setParticles(mActiveParticles);
        mTimer = new Timer();
        mTimer.schedule(mTimerTask, 0, TIMMERTASK_INTERVAL);
    }

    private void configureEmiter(int emitterX, int emitterY) {
        // We configure the emiter based on the window location to fix the offset of action bar if present
        mEmiterX = emitterX - mParentLocation[0];
        mEmiterY = emitterY - mParentLocation[1];
    }

    public void emit(int emitterX, int emitterY) {
        configureEmiter(emitterX, emitterY);
        startEmiting();
    }

    private void activateParticle(long delay) {
        Particle p = mParticles.remove(0);
        int particleX = mEmiterX;
        int particleY = mEmiterY;
        p.configure(mTimeToLive, particleX, particleY);
        p.activate(delay, mModifiers);
        mActiveParticles.add(p);

        for (ParticleInitializer particleInitializer : mLateInitializers) {
            particleInitializer.initParticle(p, mRandom);
        }
    }


    public void emitFrom(int x, int y) {
        configureEmiter(x, y);
        activateParticle(mCurrentTime);
    }


    private void onUpdate(long miliseconds) {
        synchronized (mActiveParticles) {
            int particlesSize = mActiveParticles.size();
            for (int i = 0; i < particlesSize; i++) {
                boolean active = mActiveParticles.get(i).update(miliseconds);
                if (!active) {
                    Particle p = mActiveParticles.remove(i);
                    i--; // Needed to keep the index at the right position
                    particlesSize--;
                    mParticles.add(p);
                }

            }
        }
        mDrawingView.postInvalidate();
    }

    private void cleanupAnimation() {
        mParentView.removeView(mDrawingView);
        mDrawingView = null;
        mParentView.postInvalidate();
        mParticles.addAll(mActiveParticles);
    }

    /**
     * Cancels the particle system and all the animations.
     * To stop emitting but animate until the end, use stopEmitting instead.
     */
    public void cancel() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer.purge();
            cleanupAnimation();
        }
    }

    public void clearParticles() {
        mParticles.addAll(mActiveParticles);
        mActiveParticles.clear();
    }
}
