package com.gustavaopere.enshrouded.integration.minecolonies;

import com.gustavaopere.enshrouded.protection.ProtectionDecision;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

/** Cached reflective seam for the optional MineColonies 1.21.1 public API. */
public final class MineColoniesCompatibilityProbe {
    private static final String MANAGER = "com.minecolonies.api.colony.IColonyManager";

    private final Status status;
    private final Method getInstanceMethod;
    private final Method getColonyByPosMethod;
    private final Throwable detectionFailure;

    private MineColoniesCompatibilityProbe(
            Status status,
            Method getInstanceMethod,
            Method getColonyByPosMethod,
            Throwable detectionFailure) {
        this.status = Objects.requireNonNull(status, "status");
        this.getInstanceMethod = getInstanceMethod;
        this.getColonyByPosMethod = getColonyByPosMethod;
        this.detectionFailure = detectionFailure;
    }

    public static MineColoniesCompatibilityProbe detect(boolean modLoaded, ClassLookup classLookup) {
        Objects.requireNonNull(classLookup, "classLookup");
        if (!modLoaded) {
            return absent();
        }
        try {
            Class<?> manager = classLookup.load(MANAGER);
            return new MineColoniesCompatibilityProbe(
                    Status.AVAILABLE,
                    manager.getMethod("getInstance"),
                    manager.getMethod("getColonyByPosFromWorld", Level.class, BlockPos.class),
                    null
            );
        } catch (ReflectiveOperationException | LinkageError failure) {
            return incompatible(failure);
        }
    }

    public static MineColoniesCompatibilityProbe detect(boolean modLoaded) {
        return detect(modLoaded, MineColoniesCompatibilityProbe::loadClass);
    }

    public Status status() {
        return status;
    }

    public ProtectionDecision protectionAt(Level level, BlockPos pos) {
        Objects.requireNonNull(pos, "pos");
        if (status == Status.MOD_ABSENT) {
            return ProtectionDecision.UNPROTECTED;
        }
        if (status == Status.INCOMPATIBLE) {
            throw new IllegalStateException("MineColonies is loaded but its public API is incompatible", detectionFailure);
        }
        try {
            Object manager = invoke(getInstanceMethod, null);
            Object colony = invoke(getColonyByPosMethod, manager, level, pos);
            return colony == null ? ProtectionDecision.UNPROTECTED : ProtectionDecision.PROTECTED;
        } catch (ReflectiveOperationException failure) {
            throw new IllegalStateException("MineColonies protection query failed closed", unwrap(failure));
        }
    }

    private static MineColoniesCompatibilityProbe absent() {
        return new MineColoniesCompatibilityProbe(Status.MOD_ABSENT, null, null, null);
    }

    private static MineColoniesCompatibilityProbe incompatible(Throwable failure) {
        return new MineColoniesCompatibilityProbe(Status.INCOMPATIBLE, null, null, failure);
    }

    private static Class<?> loadClass(String name) throws ClassNotFoundException {
        return Class.forName(name, false, MineColoniesCompatibilityProbe.class.getClassLoader());
    }

    private static Object invoke(Method method, Object target, Object... args) throws ReflectiveOperationException {
        return method.invoke(target, args);
    }

    private static Throwable unwrap(ReflectiveOperationException failure) {
        return failure instanceof InvocationTargetException invocation && invocation.getCause() != null
                ? invocation.getCause()
                : failure;
    }

    public enum Status {
        MOD_ABSENT,
        AVAILABLE,
        INCOMPATIBLE
    }

    @FunctionalInterface
    public interface ClassLookup {
        Class<?> load(String name) throws ClassNotFoundException;
    }
}
