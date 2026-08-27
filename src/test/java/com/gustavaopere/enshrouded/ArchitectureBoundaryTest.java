package com.gustavaopere.enshrouded;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

final class ArchitectureBoundaryTest {
    private static final Path ROOT = Path.of(System.getProperty("user.dir"));
    private static final Path MAIN_JAVA = ROOT.resolve("src/main/java/com/gustavaopere/enshrouded");

    @Test
    void coreApiDoesNotImportOptionalMods() throws IOException {
        Path apiRoot = MAIN_JAVA.resolve("api");
        List<String> invalidImports = new ArrayList<>();

        try (var paths = Files.walk(apiRoot)) {
            for (Path path : paths.filter(candidate -> candidate.toString().endsWith(".java")).toList()) {
                for (String line : Files.readAllLines(path)) {
                    String imported = importedType(line);
                    if (imported != null && !isAllowedCoreApiImport(imported)) {
                        invalidImports.add(MAIN_JAVA.relativize(path) + " -> " + imported);
                    }
                }
            }
        }

        assertTrue(invalidImports.isEmpty(),
                () -> "Core API imported an optional/foreign implementation type: " + invalidImports);
    }

    @Test
    void commonProductionCodeDoesNotReferenceMinecraftClientClasses() throws IOException {
        List<String> clientLeaks = new ArrayList<>();

        try (var paths = Files.walk(MAIN_JAVA)) {
            for (Path path : paths.filter(candidate -> candidate.toString().endsWith(".java")).toList()) {
                Path relative = MAIN_JAVA.relativize(path);
                if (relative.getNameCount() > 0 && relative.getName(0).toString().equals("client")) {
                    continue;
                }
                int lineNumber = 0;
                for (String line : Files.readAllLines(path)) {
                    lineNumber++;
                    if (line.contains("net.minecraft.client.")) {
                        clientLeaks.add(relative + ":" + lineNumber + " -> " + line.trim());
                    }
                }
            }
        }

        assertTrue(clientLeaks.isEmpty(),
                () -> "Common/server production code referenced client-only Minecraft classes: " + clientLeaks);
    }

    private static String importedType(String line) {
        String trimmed = line.trim();
        if (trimmed.startsWith("import static ")) {
            return trimmed.substring("import static ".length()).replace(";", "").trim();
        }
        if (trimmed.startsWith("import ")) {
            return trimmed.substring("import ".length()).replace(";", "").trim();
        }
        return null;
    }

    private static boolean isAllowedCoreApiImport(String imported) {
        return imported.startsWith("java.")
                || imported.startsWith("org.jetbrains.annotations.")
                || imported.startsWith("com.mojang.")
                || imported.startsWith("net.minecraft.")
                || imported.startsWith("net.neoforged.")
                || imported.startsWith("com.gustavaopere.enshrouded.");
    }
}
