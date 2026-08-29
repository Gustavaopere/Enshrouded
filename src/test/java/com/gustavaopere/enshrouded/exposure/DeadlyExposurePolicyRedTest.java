package com.gustavaopere.enshrouded.exposure;

import com.gustavaopere.enshrouded.api.shroud.ShroudSample;
import com.gustavaopere.enshrouded.api.shroud.ShroudSeverity;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class DeadlyExposurePolicyRedTest {
    @Test
    void deadlyPolicyIsStableSubstitutionPointAndLevelOneBarrierFailsClosed() throws Exception {
        try {
            Class<?> decisionType = Class.forName("com.gustavaopere.enshrouded.exposure.DeadlyExposurePolicy$Decision");
            Method evaluate = DeadlyExposurePolicy.class.getMethod(
                    "evaluate",
                    UUID.class,
                    ShroudExposureAttachment.class,
                    int.class,
                    int.class
            );
            Method remainingTicks = decisionType.getMethod("remainingTicks");
            Method barrierActive = decisionType.getMethod("barrierActive");
            Method levelOneBarrier = DeadlyExposurePolicy.class.getMethod(
                    "levelOneBarrier",
                    int.class,
                    int.class
            );
            Method tickWithPlayer = ExposureService.class.getMethod(
                    "tick",
                    UUID.class,
                    ShroudExposureAttachment.class,
                    ShroudSample.class,
                    int.class
            );

            DeadlyExposurePolicy fallback = (DeadlyExposurePolicy) levelOneBarrier.invoke(null, 20, 5);
            Object collapsed = evaluate.invoke(
                    fallback,
                    UUID.randomUUID(),
                    new ShroudExposureAttachment(ExposureSchema.CURRENT_VERSION, 80),
                    2,
                    100
            );
            assertEquals(10, remainingTicks.invoke(collapsed),
                    "Level-1 fallback must clamp to emergency window before rapid drain");
            assertTrue((boolean) barrierActive.invoke(collapsed),
                    "Level-1 fallback must report the Deadly barrier as active");

            Object missingIdentity = evaluate.invoke(
                    fallback,
                    null,
                    new ShroudExposureAttachment(ExposureSchema.CURRENT_VERSION, 80),
                    1,
                    100
            );
            assertEquals(15, remainingTicks.invoke(missingIdentity),
                    "Missing player identity must fail closed, never grant Deadly passage");
            assertTrue((boolean) barrierActive.invoke(missingIdentity));

            AtomicInteger deadlyCalls = new AtomicInteger();
            DeadlyExposurePolicy custom = (DeadlyExposurePolicy) Proxy.newProxyInstance(
                    DeadlyExposurePolicy.class.getClassLoader(),
                    new Class<?>[]{DeadlyExposurePolicy.class},
                    (proxy, method, args) -> {
                        if (method.getName().equals("evaluate")) {
                            deadlyCalls.incrementAndGet();
                            return decisionType.getConstructor(int.class, boolean.class).newInstance(37, false);
                        }
                        throw new UnsupportedOperationException(method.toString());
                    }
            );
            ExposureService service = new ExposureService(100, 2, 5, 4, custom);
            UUID playerId = UUID.randomUUID();

            ExposureSnapshot deadly = (ExposureSnapshot) tickWithPlayer.invoke(
                    service,
                    playerId,
                    new ShroudExposureAttachment(ExposureSchema.CURRENT_VERSION, 80),
                    new ShroudSample(1.0F, ShroudSeverity.DEADLY, Optional.empty(), false),
                    1
            );
            assertEquals(37, deadly.remainingTicks());
            assertFalse(deadly.deadlyBarrierActive(),
                    "ExposureService must preserve the injected policy decision rather than hard-code the barrier");
            assertEquals(1, deadlyCalls.get(), "DEADLY must delegate to the injected policy exactly once");

            tickWithPlayer.invoke(
                    service,
                    playerId,
                    new ShroudExposureAttachment(ExposureSchema.CURRENT_VERSION, 80),
                    new ShroudSample(1.0F, ShroudSeverity.SHROUD, Optional.empty(), false),
                    1
            );
            tickWithPlayer.invoke(
                    service,
                    playerId,
                    new ShroudExposureAttachment(ExposureSchema.CURRENT_VERSION, 80),
                    ShroudSample.clear(),
                    1
            );
            assertEquals(1, deadlyCalls.get(), "non-DEADLY samples must never invoke the deadly policy");
        } catch (ClassNotFoundException | NoSuchMethodException exception) {
            fail("Deadly exposure substitution contract is not implemented yet: " + exception.getMessage());
        }
    }
}
