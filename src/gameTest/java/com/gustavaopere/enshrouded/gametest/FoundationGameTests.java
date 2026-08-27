package com.gustavaopere.enshrouded.gametest;

import com.gustavaopere.enshrouded.Enshrouded;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(Enshrouded.MOD_ID)
@PrefixGameTestTemplate(false)
public final class FoundationGameTests {
    private FoundationGameTests() {
    }

    @GameTest(template = "foundation_empty")
    public static void intentionalRedProvesGameTestDiscovery(GameTestHelper helper) {
        helper.fail("INTENTIONAL RED: Enshrouded GameTest discovery is active");
    }
}
