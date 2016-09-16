package com.plattysoft.leonids;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

import java.util.LinkedList;

class ParticleField extends View {

	private LinkedList<Particle> mParticles;

	public ParticleField(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public ParticleField(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ParticleField(Context context) {
		super(context);
	}

	public void setParticles(LinkedList<Particle> particles) {
		mParticles = particles;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		synchronized (mParticles) {
			for (Particle particle : mParticles) {
				try {
					particle.draw(canvas);
				} catch (NullPointerException exc) {
					exc.printStackTrace();
				}
			}
		}

	}
}
