package com.gustavaopere.enshrouded.exposure;

import com.gustavaopere.enshrouded.api.shroud.ShroudSample;
import com.gustavaopere.enshrouded.api.shroud.ShroudSeverity;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class ExposureReducerRedTest {
    @Test
    void shroudDrainsWhileClearAndSanctuaryRecoverWithClampingAndDeltaCap() throws Exception {
        try {
            Constructor<ShroudExposureAttachment> stateConstructor =
                    ShroudExposureAttachment.class.getConstructor(int.class, int.class);
            Constructor<ExposureService> serviceConstructor = ExposureService.class.getConstructor(
                    int.class,
                    int.class,
                    int.class,
                    int.class,
                    DeadlyExposurePolicy.class
            );
            Method tick = ExposureService.class.getMethod(
                    "tick",
                    ShroudExposureAttachment.class,
                    ShroudSample.class,
                    int.class
            );
            Method remainingTicks = ExposureSnapshot.class.getMethod("remainingTicks");

            Object unusedDeadlyPolicy = Proxy.newProxyInstance(
                    DeadlyExposurePolicy.class.getClassLoader(),
                    new Class<?>[]{DeadlyExposurePolicy.class},
                    (proxy, method, args) -> null
            );

            ExposureService service = serviceConstructor.newInstance(100, 2, 5, 4, unusedDeadlyPolicy);
            ShroudExposureAttachment start = stateConstructor.newInstance(ExposureSchema.CURRENT_VERSION, 80);

            ExposureSnapshot drained = (ExposureSnapshot) tick.invoke(
                    service,
                    start,
                    sample(ShroudSeverity.SHROUD, false),
                    3
            );
            assertEquals(74, remainingTicks.invoke(drained), "ordinary Shroud drains two reserve ticks per elapsed tick");

            ShroudExposureAttachment afterDrain = stateConstructor.newInstance(
                    ExposureSchema.CURRENT_VERSION,
                    (int) remainingTicks.invoke(drained)
            );
            ExposureSnapshot recovered = (ExposureSnapshot) tick.invoke(
                    service,
                    afterDrain,
                    ShroudSample.clear(),
                    3
            );
            assertEquals(89, remainingTicks.invoke(recovered), "clear space recovers five reserve ticks per elapsed tick");

            ExposureSnapshot sanctuaryRecovered = (ExposureSnapshot) tick.invoke(
                    service,
                    stateConstructor.newInstance(ExposureSchema.CURRENT_VERSION, 90),
                    sample(ShroudSeverity.SHROUD, true),
                    10
            );
            assertEquals(100, remainingTicks.invoke(sanctuaryRecovered),
                    "sanctuary suppression must behave as recovery and clamp at maximum reserve");

            ExposureSnapshot deltaCapped = (ExposureSnapshot) tick.invoke(
                    service,
                    stateConstructor.newInstance(ExposureSchema.CURRENT_VERSION, 20),
                    sample(ShroudSeverity.SHROUD, false),
                    100
            );
            assertEquals(12, remainingTicks.invoke(deltaCapped),
                    "elapsed delta must be capped before drain to avoid lag-spike punishment");

            ExposureSnapshot lowerClamped = (ExposureSnapshot) tick.invoke(
                    service,
                    stateConstructor.newInstance(ExposureSchema.CURRENT_VERSION, 3),
                    sample(ShroudSeverity.SHROUD, false),
                    4
            );
            assertEquals(0, remainingTicks.invoke(lowerClamped), "reserve must clamp at zero");
        } catch (NoSuchMethodException exception) {
            fail("Stage 03 exposure reducer contract is not implemented yet: " + exception.getMessage());
        }
    }

    private static ShroudSample sample(ShroudSeverity severity, boolean sanctuarySuppressed) {
        return new ShroudSample(1.0F, severity, Optional.empty(), sanctuarySuppressed);
    }
}
