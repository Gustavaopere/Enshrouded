package com.gustavaopere.enshrouded.provenance;

/**
 * Pinned provenance metadata for sources and runtime providers audited by Enshrouded.
 *
 * <p>This class intentionally contains no classes from optional mods. It is metadata only,
 * so removing every optional integration from the pack cannot break core classloading.</p>
 */
public final class UpstreamInventory {
    public static final String SCULK_HORDE_SOURCE_ID = "sculk-horde-github-491aaa7e";
    public static final String SCULK_HORDE_SOURCE_SHA = "491aaa7e3c8c01eff0a7859ccfe2a62500ed15bc";
    public static final String SCULK_HORDE_LICENSE = "Apache License 2.0";

    public static final String ARS_ZERO_RUNTIME_ID = "ars-zero-runtime-2.0.2";
    public static final String ARS_ZERO_PACK_VERSION = "2.0.2";
    public static final String ARS_ZERO_SOURCE_SHA = "9478291a9f331ee2b4a391c4581a342d342ac7dc";
    public static final String ARS_ZERO_LICENSE = "GPLv3";

    private UpstreamInventory() {
    }
}
