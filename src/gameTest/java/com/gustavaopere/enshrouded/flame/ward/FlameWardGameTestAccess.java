package com.gustavaopere.enshrouded.flame.ward;

/** Test-only access to reset the process-local Flame Ward provider between GameTest fixtures. */
public final class FlameWardGameTestAccess {
    private FlameWardGameTestAccess() {
    }

    public static void clear() {
        FlameWardRuntime.service().clear();
    }
}
