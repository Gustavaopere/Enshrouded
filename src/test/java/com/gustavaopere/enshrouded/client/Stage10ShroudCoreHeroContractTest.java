package com.gustavaopere.enshrouded.client;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class Stage10ShroudCoreHeroContractTest {
    private static final Path ROOT = Path.of("").toAbsolutePath();

    @Test
    void shroudCoreUsesAnimatedGeckoLibBlockEntityRenderer() throws IOException {
        String block = read("src/main/java/com/gustavaopere/enshrouded/shroud/core/ShroudCoreBlock.java");
        String blockEntity = read("src/main/java/com/gustavaopere/enshrouded/shroud/core/ShroudCoreBlockEntity.java");
        String client = read("src/main/java/com/gustavaopere/enshrouded/client/EnshroudedClient.java");

        assertTrue(block.contains("RenderShape.ENTITYBLOCK_ANIMATED"), "Shroud Core must use animated BE rendering");
        assertTrue(blockEntity.contains("GeoBlockEntity"), "Shroud Core BE must implement GeoBlockEntity");
        assertTrue(blockEntity.contains("GeckoLibUtil.createInstanceCache(this)"), "Shroud Core needs a GeckoLib cache");
        assertTrue(client.contains("registerBlockEntityRenderer(ModBlockEntities.SHROUD_CORE.get(), ShroudCoreRenderer::new)"),
                "client bootstrap must register the Shroud Core renderer");
    }

    @Test
    void rendererProvidesSeparateOrdinaryAndDeadlySilhouettesWithSelectiveEmissive() throws IOException {
        String renderer = read("src/main/java/com/gustavaopere/enshrouded/client/render/shroud/ShroudCoreRenderer.java");
        String model = read("src/main/java/com/gustavaopere/enshrouded/client/render/shroud/ShroudCoreGeoModel.java");

        assertTrue(renderer.contains("AutoGlowingGeoLayer"), "Shroud Core must use a selective emissive layer");
        assertTrue(model.contains("shroud_core.geo.json"), "ordinary core geometry is required");
        assertTrue(model.contains("shroud_core_deadly.geo.json"), "deadly core must have a distinct silhouette resource");
        assertTrue(model.contains("shroud_core_deadly.png"), "deadly core must have a distinct material profile");
    }

    @Test
    void geometryAndAnimationsReadAsLivingCorruptionRatherThanCube() throws IOException {
        String ordinary = read("src/main/resources/assets/enshrouded/geo/shroud_core.geo.json");
        String deadly = read("src/main/resources/assets/enshrouded/geo/shroud_core_deadly.geo.json");
        String animations = read("src/main/resources/assets/enshrouded/animations/shroud_core.animation.json");

        for (String requiredBone : new String[]{"core_root", "outer_husk", "inner_heart", "tendrils", "roots"}) {
            assertTrue(ordinary.contains("\"name\": \"" + requiredBone + "\""), "ordinary geometry missing " + requiredBone);
        }
        assertTrue(deadly.contains("\"name\": \"deadly_thorns\""), "Deadly geometry needs additional thorn/split silhouette");
        assertTrue(animations.contains("animation.shroud_core.idle"));
        assertTrue(animations.contains("animation.shroud_core.threat"));
        assertTrue(animations.contains("animation.shroud_core.collapse"));
    }

    @Test
    void editableSourceAndFirstPartyTexturesArePresent() throws IOException {
        Path source = ROOT.resolve("art/blockbench/shroud_core.bbmodel");
        assertTrue(Files.isRegularFile(source), "editable Shroud Core Blockbench source is required");
        String bbmodel = Files.readString(source);
        assertTrue(bbmodel.contains("geometry.shroud_core"));
        assertTrue(bbmodel.contains("shroud_core.png"));
        assertTrue(bbmodel.contains("shroud_core_glowmask.png"));
        assertTrue(bbmodel.contains("shroud_core_deadly.png"));
        assertTrue(bbmodel.contains("shroud_core_deadly_glowmask.png"));

        for (String texture : new String[]{
                "src/main/resources/assets/enshrouded/textures/block/shroud_core.png",
                "src/main/resources/assets/enshrouded/textures/block/shroud_core_glowmask.png",
                "src/main/resources/assets/enshrouded/textures/block/shroud_core_deadly.png",
                "src/main/resources/assets/enshrouded/textures/block/shroud_core_deadly_glowmask.png"}) {
            assertTrue(Files.isRegularFile(ROOT.resolve(texture)), "missing first-party texture " + texture);
        }
    }

    @Test
    void deadlyPresentationIsDerivedFromCanonicalSeverityAndNeverFromTier() throws IOException {
        String blockEntity = read("src/main/java/com/gustavaopere/enshrouded/shroud/core/ShroudCoreBlockEntity.java");
        assertTrue(blockEntity.contains("ShroudSeverity.DEADLY"), "presentation profile must consume canonical Shroud severity");
        assertTrue(blockEntity.contains("presentationProfile"), "renderer needs a read-only presentation profile");
        assertFalse(blockEntity.contains("tier >="), "core tier must not be invented as a Deadly visual authority");
        assertFalse(blockEntity.contains("tier >"), "core tier must not be invented as a Deadly visual authority");
    }

    @Test
    void collapsePresentationRemainsDownstreamOfAuthoritativeDestruction() throws IOException {
        String blockEntity = read("src/main/java/com/gustavaopere/enshrouded/shroud/core/ShroudCoreBlockEntity.java");
        assertTrue(blockEntity.contains("ShroudCoreDestroyedEvent"), "authoritative destruction event must remain the causal source");
        assertTrue(blockEntity.contains("triggerAuthoritativeCollapsePresentation"),
                "collapse presentation must be explicitly downstream of authoritative retirement");
    }

    private static String read(String relative) throws IOException {
        Path path = ROOT.resolve(relative);
        assertTrue(Files.isRegularFile(path), "missing required Stage 10.03 file " + relative);
        return Files.readString(path);
    }
}
