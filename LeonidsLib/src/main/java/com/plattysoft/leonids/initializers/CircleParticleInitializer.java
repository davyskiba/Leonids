package com.plattysoft.leonids.initializers;

import com.plattysoft.leonids.Particle;

import java.util.Random;

public class CircleParticleInitializer implements ParticleInitializer {

    private int centerX;
    private int centerY;
    private float speed;

    private float speedVariation;
    private double angleVariation;

    private Random random;

    public CircleParticleInitializer(int centerX, int centerY, float speed) {
        this.centerX = centerX;
        this.centerY = centerY;
        this.speed = speed;

        speedVariation=0f;
        angleVariation=0f;
        random=new Random();
    }

    public void setSpeedVariation(float speedVariation) {
        this.speedVariation = speedVariation;
    }

    public void setAngleVariation(double angleVariation) {
        this.angleVariation = Math.toRadians(angleVariation);
    }

    @Override
    public void initParticle(Particle p, Random r) {
        float relativeX = p.mCurrentX - centerX;
        float relativeY = p.mCurrentY - centerY;
        double calculatedAngleRadians = Math.atan2(relativeY, relativeX)+ Math.PI/2;
        double angleRadians=calculatedAngleRadians-angleVariation+(random.nextDouble()*2*angleVariation);

        float particleSpeed=speed-speedVariation+(random.nextFloat()*2*speedVariation);

        p.mSpeedX = (float) (particleSpeed * Math.cos(angleRadians));
        p.mSpeedY = (float) (particleSpeed * Math.sin(angleRadians));
    }
}
