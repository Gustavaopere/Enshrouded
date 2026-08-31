package com.gustavaopere.enshrouded.story.ritual;

import com.gustavaopere.enshrouded.Enshrouded;
import com.gustavaopere.enshrouded.flame.altar.FlameAltarOffering;
import com.gustavaopere.enshrouded.flame.ritual.FlameRitual;
import com.gustavaopere.enshrouded.flame.ritual.RitualOutcome;
import com.gustavaopere.enshrouded.story.reward.LichSkullItem;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;

/** Concrete Stage-06 binding from the authentic first Lich skull to the generic Stage-05 engine. */
public final class LevelOneLichSkullRitual implements FlameRitual {
    public static final ResourceLocation RITUAL_ID =
            ResourceLocation.fromNamespaceAndPath(Enshrouded.MOD_ID, "lich_manifestation_1");
    public static final ResourceLocation INTENT_ID = RITUAL_ID;

    private final OfferingPolicy offeringPolicy;

    public LevelOneLichSkullRitual() {
        this(new PhysicalOfferingPolicy());
    }

    LevelOneLichSkullRitual(OfferingPolicy offeringPolicy) {
        this.offeringPolicy = Objects.requireNonNull(offeringPolicy, "offeringPolicy");
    }

    @Override
    public ResourceLocation id() {
        return RITUAL_ID;
    }

    @Override
    public ResourceLocation intentId() {
        return INTENT_ID;
    }

    @Override
    public boolean isEligible(Context context) {
        return context.progression().flameLevel() == 1
                && context.progression().passageLevel() == 1
                && !context.progression().nextLevelReady();
    }

    @Override
    public OfferingContract offering() {
        return new OfferingContract() {
            @Override
            public boolean accepts(Context context, Offering offering) {
                return offeringPolicy.accepts(offering);
            }

            @Override
            public void consume(Context context, Offering offering) {
                offeringPolicy.consume(offering);
            }
        };
    }

    @Override
    public RitualOutcome outcome(Context context) {
        return RitualOutcome.levelOneCheckpoint();
    }

    interface OfferingPolicy {
        boolean accepts(Offering offering);

        void consume(Offering offering);
    }

    private static final class PhysicalOfferingPolicy implements OfferingPolicy {
        @Override
        public boolean accepts(Offering offering) {
            return offering instanceof FlameAltarOffering altarOffering
                    && LichSkullItem.isAuthenticLevelOne(altarOffering.stack());
        }

        @Override
        public void consume(Offering offering) {
            if (!(offering instanceof FlameAltarOffering altarOffering)
                    || !LichSkullItem.isAuthenticLevelOne(altarOffering.stack())
                    || !altarOffering.consumeOne()) {
                throw new IllegalStateException("authentic Lich skull offering changed before ritual commit");
            }
        }
    }
}
