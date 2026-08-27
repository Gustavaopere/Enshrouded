package com.gustavaopere.enshrouded.provenance;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

final class ProvenanceDocumentationTest {
    private static final Path ROOT = Path.of(System.getProperty("user.dir"));

    @Test
    void requiredUpstreamDocumentationIsAuditable() throws IOException {
        Path notices = ROOT.resolve("THIRD_PARTY_NOTICES.md");
        Path compatibility = ROOT.resolve("docs/compat/current-pack-2026-08-26.md");
        Path upstream = ROOT.resolve("plans/00-foundation/UPSTREAM.md");

        assertTrue(Files.isRegularFile(notices), "THIRD_PARTY_NOTICES.md must exist");
        assertTrue(Files.isRegularFile(compatibility), "current-pack compatibility inventory must exist");
        assertTrue(Files.isRegularFile(upstream), "Foundation upstream inventory must exist");

        String noticeText = Files.readString(notices);
        assertTrue(noticeText.contains("491aaa7e3c8c01eff0a7859ccfe2a62500ed15bc"));
        assertTrue(noticeText.contains("Apache License 2.0"));
        assertTrue(noticeText.contains("Ars Zero"));
        assertTrue(noticeText.contains("GPLv3"));

        String packText = Files.readString(compatibility);
        assertTrue(packText.contains("Ars Zero 2.0.2"));
        assertTrue(packText.contains("Spore 2.2.0j — excluded"));
        assertTrue(packText.contains("Infnexus 2.0.4 — excluded"));
    }

    @Test
    void upstreamInventoryPinsAuditedSourcesWithoutCompileTimeCoupling() throws Exception {
        Class<?> inventory = Class.forName("com.gustavaopere.enshrouded.provenance.UpstreamInventory");
        Field sculkSha = inventory.getField("SCULK_HORDE_SOURCE_SHA");
        Field arsZeroVersion = inventory.getField("ARS_ZERO_PACK_VERSION");
        assertEquals("491aaa7e3c8c01eff0a7859ccfe2a62500ed15bc", sculkSha.get(null));
        assertEquals("2.0.2", arsZeroVersion.get(null));
    }

    @Test
    void everySourceDerivedMarkerNamesAnUpstreamRecordedInNotices() throws IOException {
        Path notices = ROOT.resolve("THIRD_PARTY_NOTICES.md");
        if (!Files.isRegularFile(notices)) {
            fail("THIRD_PARTY_NOTICES.md must exist before source-derived code can be audited");
        }
        String noticeText = Files.readString(notices);
        Path sourceRoot = ROOT.resolve("src/main/java");
        try (Stream<Path> paths = Files.walk(sourceRoot)) {
            List<String> markers = paths.filter(path -> path.toString().endsWith(".java"))
                    .flatMap(path -> {
                        try {
                            return Files.lines(path);
                        } catch (IOException exception) {
                            throw new RuntimeException(exception);
                        }
                    })
                    .map(String::trim)
                    .filter(line -> line.startsWith("// UPSTREAM-DERIVED:"))
                    .map(line -> line.substring("// UPSTREAM-DERIVED:".length()).trim())
                    .toList();
            for (String marker : markers) {
                assertFalse(marker.isBlank(), "upstream marker must name a source id");
                assertTrue(noticeText.contains("`" + marker + "`"),
                        () -> "missing THIRD_PARTY_NOTICES entry for upstream id " + marker);
            }
        }
    }
}
