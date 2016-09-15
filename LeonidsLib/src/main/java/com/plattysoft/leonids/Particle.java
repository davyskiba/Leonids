package com.plattysoft.leonids;

import com.plattysoft.leonids.modifiers.ParticleModifier;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

public class Particle {

	protected Bitmap mImage;

	public float mCurrentX;
	public float mCurrentY;

	public float mSpeedX = 0f;
	public float mSpeedY = 0f;

	private Matrix mMatrix;
	private Paint mPaint;
	private int alpha;

	private float mInitialX;
	private float mInitialY;

	private long mTimeToLive;

	protected long mStartingMilisecond;

	private List<ParticleModifier> mModifiers;


	protected Particle() {
		mMatrix = new Matrix();
		mPaint = new Paint();
		alpha=0;
		mPaint.setAlpha(alpha);
	}

	public Particle (Bitmap bitmap) {
		this();
		mImage = bitmap;
	}

	public int getAlpha() {
		return alpha;
	}

	public void setAlpha(int alpha){
		if(this.alpha!=alpha){
			this.alpha=alpha;
			mPaint.setAlpha(alpha);
		}
	}

	public void configure(long timeToLive, float emiterX, float emiterY) {

		mInitialX = emiterX - mImage.getWidth()/2;
		mInitialY = emiterY - mImage.getHeight()/2;
		mCurrentX = mInitialX;
		mCurrentY = mInitialY;

		mTimeToLive = timeToLive;
	}

	public boolean update (long miliseconds) {
		long realMiliseconds = miliseconds - mStartingMilisecond;
		if (realMiliseconds > mTimeToLive) {
			return false;
		}
		mCurrentX = mInitialX+mSpeedX*realMiliseconds;
		mCurrentY = mInitialY+mSpeedY*realMiliseconds;

		for (int i=0; i<mModifiers.size(); i++) {
			mModifiers.get(i).apply(this, realMiliseconds);
		}
		return true;
	}

	public void draw (Canvas c) {
		mMatrix.setTranslate(mCurrentX,mCurrentY);
		c.drawBitmap(mImage, mMatrix, mPaint);
	}

	public Particle activate(long startingMilisecond, List<ParticleModifier> modifiers) {
		mStartingMilisecond = startingMilisecond;
		// We do store a reference to the list, there is no need to copy, since the modifiers do not carte about states 
		mModifiers = modifiers;
		return this;
	}
}
