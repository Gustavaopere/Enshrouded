package com.gustavaopere.enshrouded.integration.ftbchunks;

import com.gustavaopere.enshrouded.protection.ProtectionDecision;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

/** Cached reflective seam for the optional FTB Chunks 1.21.1 public API. */
public final class FtbChunksCompatibilityProbe {
    private static final String API_ROOT = "dev.ftb.mods.ftbchunks.api.FTBChunksAPI";
    private static final String API = "dev.ftb.mods.ftbchunks.api.FTBChunksAPI$API";
    private static final String MANAGER = "dev.ftb.mods.ftbchunks.api.ClaimedChunkManager";
    private static final String CHUNK_DIM_POS = "dev.ftb.mods.ftblibrary.math.ChunkDimPos";

    private final Status status;
    private final Method apiMethod;
    private final Method isManagerLoadedMethod;
    private final Method getManagerMethod;
    private final Constructor<?> chunkDimPosConstructor;
    private final Method getChunkMethod;
    private final Throwable detectionFailure;

    private FtbChunksCompatibilityProbe(
            Status status,
            Method apiMethod,
            Method isManagerLoadedMethod,
            Method getManagerMethod,
            Constructor<?> chunkDimPosConstructor,
            Method getChunkMethod,
            Throwable detectionFailure) {
        this.status = Objects.requireNonNull(status, "status");
        this.apiMethod = apiMethod;
        this.isManagerLoadedMethod = isManagerLoadedMethod;
        this.getManagerMethod = getManagerMethod;
        this.chunkDimPosConstructor = chunkDimPosConstructor;
        this.getChunkMethod = getChunkMethod;
        this.detectionFailure = detectionFailure;
    }

    public static FtbChunksCompatibilityProbe detect(boolean modLoaded, ClassLookup classLookup) {
        Objects.requireNonNull(classLookup, "classLookup");
        if (!modLoaded) {
            return absent();
        }
        try {
            Class<?> root = classLookup.load(API_ROOT);
            Class<?> api = classLookup.load(API);
            Class<?> manager = classLookup.load(MANAGER);
            Class<?> chunkDimPos = classLookup.load(CHUNK_DIM_POS);
            return new FtbChunksCompatibilityProbe(
                    Status.AVAILABLE,
                    root.getMethod("api"),
                    api.getMethod("isManagerLoaded"),
                    api.getMethod("getManager"),
                    chunkDimPos.getConstructor(Level.class, BlockPos.class),
                    manager.getMethod("getChunk", chunkDimPos),
                    null
            );
        } catch (ReflectiveOperationException | LinkageError failure) {
            return incompatible(failure);
        }
    }

    public static FtbChunksCompatibilityProbe detect(boolean modLoaded) {
        return detect(modLoaded, FtbChunksCompatibilityProbe::loadClass);
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
            throw new IllegalStateException("FTB Chunks is loaded but its public API is incompatible", detectionFailure);
        }
        try {
            Object api = invoke(apiMethod, null);
            if (!(boolean) invoke(isManagerLoadedMethod, api)) {
                throw new IllegalStateException("FTB Chunks manager is not available yet");
            }
            Object manager = invoke(getManagerMethod, api);
            Object chunkDimPos = chunkDimPosConstructor.newInstance(level, pos);
            Object claimedChunk = invoke(getChunkMethod, manager, chunkDimPos);
            return claimedChunk == null ? ProtectionDecision.UNPROTECTED : ProtectionDecision.PROTECTED;
        } catch (ReflectiveOperationException failure) {
            throw new IllegalStateException("FTB Chunks protection query failed closed", unwrap(failure));
        }
    }

    private static FtbChunksCompatibilityProbe absent() {
        return new FtbChunksCompatibilityProbe(Status.MOD_ABSENT, null, null, null, null, null, null);
    }

    private static FtbChunksCompatibilityProbe incompatible(Throwable failure) {
        return new FtbChunksCompatibilityProbe(Status.INCOMPATIBLE, null, null, null, null, null, failure);
    }

    private static Class<?> loadClass(String name) throws ClassNotFoundException {
        return Class.forName(name, false, FtbChunksCompatibilityProbe.class.getClassLoader());
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
