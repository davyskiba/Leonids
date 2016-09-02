package com.plattysoft.leonids.modifiers;

import com.plattysoft.leonids.Particle;

public class FadeInModifier implements ParticleModifier {

    private static final int MAX_ALPHA=255;
    private long fadeInDuration;

    public FadeInModifier(long fadeInDuration) {
        this.fadeInDuration = fadeInDuration;
    }

    @Override
    public void apply(Particle particle, long miliseconds) {
        if(miliseconds<=0){
            particle.mAlpha=0;
            return;
        }

        if(particle.mAlpha>=MAX_ALPHA){
            return;
        }

        int interpolatedAlpha= (int) (((float)miliseconds/fadeInDuration)*MAX_ALPHA);
        particle.mAlpha= Math.min(interpolatedAlpha,MAX_ALPHA);
    }
}
