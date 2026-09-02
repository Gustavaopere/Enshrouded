package com.gustavaopere.enshrouded.integration.ftbteams;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/** Cached reflective seam for the optional FTB Teams 1.21.1 public API. */
public final class FtbTeamsCompatibilityProbe {
    private static final String API_ROOT = "dev.ftb.mods.ftbteams.api.FTBTeamsAPI";
    private static final String API = "dev.ftb.mods.ftbteams.api.FTBTeamsAPI$API";
    private static final String MANAGER = "dev.ftb.mods.ftbteams.api.TeamManager";
    private static final String TEAM = "dev.ftb.mods.ftbteams.api.Team";

    private final Status status;
    private final Method apiMethod;
    private final Method isManagerLoadedMethod;
    private final Method getManagerMethod;
    private final Method getTeamForPlayerIdMethod;
    private final Method isPlayerTeamMethod;
    private final Method getIdMethod;
    private final Throwable detectionFailure;

    private FtbTeamsCompatibilityProbe(
            Status status,
            Method apiMethod,
            Method isManagerLoadedMethod,
            Method getManagerMethod,
            Method getTeamForPlayerIdMethod,
            Method isPlayerTeamMethod,
            Method getIdMethod,
            Throwable detectionFailure) {
        this.status = Objects.requireNonNull(status, "status");
        this.apiMethod = apiMethod;
        this.isManagerLoadedMethod = isManagerLoadedMethod;
        this.getManagerMethod = getManagerMethod;
        this.getTeamForPlayerIdMethod = getTeamForPlayerIdMethod;
        this.isPlayerTeamMethod = isPlayerTeamMethod;
        this.getIdMethod = getIdMethod;
        this.detectionFailure = detectionFailure;
    }

    public static FtbTeamsCompatibilityProbe detect(boolean modLoaded, ClassLookup classLookup) {
        Objects.requireNonNull(classLookup, "classLookup");
        if (!modLoaded) {
            return absent();
        }
        try {
            Class<?> root = classLookup.load(API_ROOT);
            Class<?> api = classLookup.load(API);
            Class<?> manager = classLookup.load(MANAGER);
            Class<?> team = classLookup.load(TEAM);
            return new FtbTeamsCompatibilityProbe(
                    Status.AVAILABLE,
                    root.getMethod("api"),
                    api.getMethod("isManagerLoaded"),
                    api.getMethod("getManager"),
                    manager.getMethod("getTeamForPlayerID", UUID.class),
                    team.getMethod("isPlayerTeam"),
                    team.getMethod("getId"),
                    null
            );
        } catch (ReflectiveOperationException | LinkageError failure) {
            return incompatible(failure);
        }
    }

    public static FtbTeamsCompatibilityProbe detect(boolean modLoaded) {
        return detect(modLoaded, FtbTeamsCompatibilityProbe::loadClass);
    }

    public Status status() {
        return status;
    }

    /** Empty means standalone player ownership; incompatible loaded providers fail closed. */
    public Optional<String> teamId(UUID playerId) {
        Objects.requireNonNull(playerId, "playerId");
        if (status == Status.MOD_ABSENT) {
            return Optional.empty();
        }
        if (status == Status.INCOMPATIBLE) {
            throw new IllegalStateException("FTB Teams is loaded but its public API is incompatible", detectionFailure);
        }
        try {
            Object api = invoke(apiMethod, null);
            if (!(boolean) invoke(isManagerLoadedMethod, api)) {
                throw new IllegalStateException("FTB Teams manager is not available yet");
            }
            Object manager = invoke(getManagerMethod, api);
            Object optionalValue = invoke(getTeamForPlayerIdMethod, manager, playerId);
            if (!(optionalValue instanceof Optional<?> teamOptional)) {
                throw new IllegalStateException("FTB Teams getTeamForPlayerID returned a non-Optional value");
            }
            if (teamOptional.isEmpty()) {
                return Optional.empty();
            }
            Object team = teamOptional.get();
            if ((boolean) invoke(isPlayerTeamMethod, team)) {
                return Optional.empty();
            }
            Object id = invoke(getIdMethod, team);
            if (!(id instanceof UUID uuid)) {
                throw new IllegalStateException("FTB Teams shared team ID is not a UUID");
            }
            return Optional.of(uuid.toString());
        } catch (ReflectiveOperationException failure) {
            throw new IllegalStateException("FTB Teams owner query failed closed", unwrap(failure));
        }
    }

    private static FtbTeamsCompatibilityProbe absent() {
        return new FtbTeamsCompatibilityProbe(Status.MOD_ABSENT, null, null, null, null, null, null, null);
    }

    private static FtbTeamsCompatibilityProbe incompatible(Throwable failure) {
        return new FtbTeamsCompatibilityProbe(Status.INCOMPATIBLE, null, null, null, null, null, null, failure);
    }

    private static Class<?> loadClass(String name) throws ClassNotFoundException {
        return Class.forName(name, false, FtbTeamsCompatibilityProbe.class.getClassLoader());
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
