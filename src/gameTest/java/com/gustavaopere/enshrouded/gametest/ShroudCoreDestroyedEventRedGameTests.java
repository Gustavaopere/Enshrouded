package com.gustavaopere.enshrouded.gametest;

import com.gustavaopere.enshrouded.Enshrouded;
import com.gustavaopere.enshrouded.registry.ModBlocks;
import com.gustavaopere.enshrouded.shroud.core.CoreMutationResult;
import com.gustavaopere.enshrouded.shroud.core.ShroudCoreService;
import com.gustavaopere.enshrouded.shroud.state.ShroudCoreState;
import com.gustavaopere.enshrouded.shroud.state.ShroudSavedData;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.bus.api.Event;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

import java.lang.reflect.Method;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

@GameTestHolder(Enshrouded.MOD_ID)
@PrefixGameTestTemplate(false)
public final class ShroudCoreDestroyedEventRedGameTests {
    private static final String EVENT_CLASS_NAME = "com.gustavaopere.enshrouded.shroud.core.ShroudCoreDestroyedEvent";

    private ShroudCoreDestroyedEventRedGameTests() {
    }

    @GameTest(template = "foundation_empty", timeoutTicks = 40)
    public static void physicalDestructionEmitsExactlyOneEventForTheDestroyedCore(GameTestHelper helper) {
        Class<? extends Event> eventClass;
        Method coreIdAccessor;
        Method levelAccessor;
        try {
            Class<?> rawEventClass = Class.forName(EVENT_CLASS_NAME);
            helper.assertTrue(Event.class.isAssignableFrom(rawEventClass),
                    "ShroudCoreDestroyedEvent must extend NeoForge Event");
            @SuppressWarnings("unchecked")
            Class<? extends Event> castEventClass = (Class<? extends Event>) rawEventClass;
            eventClass = castEventClass;
            coreIdAccessor = rawEventClass.getMethod("coreId");
            levelAccessor = rawEventClass.getMethod("level");
        } catch (ReflectiveOperationException exception) {
            helper.fail("Expected ShroudCoreDestroyedEvent(level, coreId) contract: " + exception);
            return;
        }

        ServerLevel level = GameTestBootstrap.requireServerLevel(helper);
        BlockPos relative = new BlockPos(1, 1, 1);
        BlockPos absolute = helper.absolutePos(relative);
        AtomicReference<UUID> targetCoreId = new AtomicReference<>();
        AtomicReference<ServerLevel> observedLevel = new AtomicReference<>();
        AtomicInteger matchingEventCount = new AtomicInteger();

        Consumer<Event> listener = event -> {
            if (!eventClass.isInstance(event) || targetCoreId.get() == null) {
                return;
            }
            try {
                UUID observedCoreId = (UUID) coreIdAccessor.invoke(event);
                if (targetCoreId.get().equals(observedCoreId)) {
                    matchingEventCount.incrementAndGet();
                    observedLevel.set((ServerLevel) levelAccessor.invoke(event));
                }
            } catch (ReflectiveOperationException exception) {
                throw new IllegalStateException("Invalid ShroudCoreDestroyedEvent accessors", exception);
            }
        };
        addEventListener(eventClass, listener);

        helper.setBlock(relative, ModBlocks.SHROUD_CORE.get());
        helper.runAtTickTime(2L, () -> {
            ShroudSavedData data = ShroudSavedData.get(level);
            ShroudCoreState core = data.state().cores().values().stream()
                    .filter(candidate -> candidate.center().equals(absolute))
                    .findFirst()
                    .orElse(null);
            helper.assertTrue(core != null, "Placed core did not register before event verification");
            targetCoreId.set(core.id());

            CoreMutationResult activation = ShroudCoreService.activate(data.state(), core.id());
            data.replace(activation.state());
            helper.destroyBlock(relative);
        });

        helper.runAtTickTime(5L, () -> {
            helper.assertTrue(matchingEventCount.get() == 1,
                    "Physical destruction must emit exactly one ShroudCoreDestroyedEvent for the target core");
            helper.assertTrue(observedLevel.get() == level,
                    "ShroudCoreDestroyedEvent must expose the owning ServerLevel");
            helper.succeed();
        });
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static void addEventListener(Class<? extends Event> eventClass, Consumer<Event> listener) {
        NeoForge.EVENT_BUS.addListener((Class) eventClass, (Consumer) listener);
    }
}
