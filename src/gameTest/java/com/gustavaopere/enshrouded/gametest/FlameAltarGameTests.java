package com.gustavaopere.enshrouded.gametest;

import com.gustavaopere.enshrouded.Enshrouded;
import com.gustavaopere.enshrouded.api.progression.ProgressionOwner;
import com.gustavaopere.enshrouded.api.progression.ProgressionRuntimeBindings;
import com.gustavaopere.enshrouded.flame.altar.FlameAltarBlockEntity;
import com.gustavaopere.enshrouded.flame.altar.FlameAltarOffering;
import com.gustavaopere.enshrouded.flame.altar.FlameAltarRuntime;
import com.gustavaopere.enshrouded.flame.altar.FlameAltarService;
import com.gustavaopere.enshrouded.flame.ritual.FlameRitual;
import com.gustavaopere.enshrouded.flame.ritual.RitualOutcome;
import com.gustavaopere.enshrouded.flame.state.FlameProgressionSavedData;
import com.gustavaopere.enshrouded.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.GameType;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

@GameTestHolder(Enshrouded.MOD_ID)
@PrefixGameTestTemplate(false)
public final class FlameAltarGameTests {
    private static final ResourceLocation DOUBLE_RITUAL_ID =
            ResourceLocation.fromNamespaceAndPath(Enshrouded.MOD_ID, "gametest_flame_altar_double");
    private static final ResourceLocation DOUBLE_INTENT_ID =
            ResourceLocation.fromNamespaceAndPath(Enshrouded.MOD_ID, "gametest_flame_altar_double_intent");
    private static final ResourceLocation BREAK_RITUAL_ID =
            ResourceLocation.fromNamespaceAndPath(Enshrouded.MOD_ID, "gametest_flame_altar_break");
    private static final ResourceLocation BREAK_INTENT_ID =
            ResourceLocation.fromNamespaceAndPath(Enshrouded.MOD_ID, "gametest_flame_altar_break_intent");
    private static final AtomicBoolean REGISTERED = new AtomicBoolean();

    private FlameAltarGameTests() {
    }

    @GameTest(template = "foundation_empty")
    public static void recipePlacesAndReloadsPersistentAltarInventory(GameTestHelper helper) {
        ServerLevel level = GameTestBootstrap.requireServerLevel(helper);
        CraftingInput input = CraftingInput.of(3, 3, List.of(
                new ItemStack(Items.STONE_BRICKS), new ItemStack(Items.GOLD_INGOT), new ItemStack(Items.STONE_BRICKS),
                new ItemStack(Items.GOLD_INGOT), new ItemStack(Items.CAMPFIRE), new ItemStack(Items.GOLD_INGOT),
                new ItemStack(Items.STONE_BRICKS), new ItemStack(Items.GOLD_INGOT), new ItemStack(Items.STONE_BRICKS)
        ));
        Optional<RecipeHolder<CraftingRecipe>> recipe = level.getRecipeManager()
                .getRecipeFor(RecipeType.CRAFTING, input, level);
        helper.assertTrue(recipe.isPresent(), "Level 1 Flame Altar crafting recipe must be discoverable");
        ItemStack crafted = recipe.orElseThrow().value().assemble(input, level.registryAccess());
        helper.assertTrue(crafted.is(ModBlocks.FLAME_ALTAR.get().asItem()),
                "Flame Altar recipe must assemble enshrouded:flame_altar");

        BlockPos relative = new BlockPos(1, 1, 1);
        helper.setBlock(relative, ModBlocks.FLAME_ALTAR.get());
        FlameAltarBlockEntity altar = requireAltar(helper, relative);
        altar.inventory().setStackInSlot(0, new ItemStack(Items.BLAZE_POWDER, 2));

        CompoundTag saved = altar.saveWithoutMetadata(level.registryAccess());
        altar.inventory().setStackInSlot(0, ItemStack.EMPTY);
        altar.loadWithComponents(saved, level.registryAccess());

        helper.assertTrue(altar.inventory().getStackInSlot(0).is(Items.BLAZE_POWDER),
                "Reloaded Flame Altar must restore its offering item");
        helper.assertTrue(altar.inventory().getStackInSlot(0).getCount() == 2,
                "Reloaded Flame Altar must restore the exact offering count");
        helper.succeed();
    }

    @GameTest(template = "foundation_empty")
    public static void forgedClientLikeActivationWithoutRegisteredOfferingIsDenied(GameTestHelper helper) {
        ensureSyntheticRitualsRegistered();
        ServerPlayer player = requireServerPlayer(helper);
        ServerLevel level = level(helper);
        ProgressionOwner owner = ProgressionRuntimeBindings.ownerResolver().resolve(player.getUUID());
        var before = FlameProgressionSavedData.get(level).progression(owner);
        BlockPos relative = new BlockPos(1, 1, 1);
        helper.setBlock(relative, ModBlocks.FLAME_ALTAR.get());
        FlameAltarBlockEntity altar = requireAltar(helper, relative);
        altar.inventory().setStackInSlot(0, new ItemStack(Items.ROTTEN_FLESH));

        FlameAltarService.ActivationResult result = FlameAltarRuntime.activate(player, altar.inventory());
        var after = FlameProgressionSavedData.get(level).progression(owner);

        helper.assertTrue(result.status() == FlameAltarService.Status.NO_MATCHING_RITUAL,
                "Forged client-like activation with an invalid server offering must be denied");
        helper.assertTrue(altar.inventory().getStackInSlot(0).is(Items.ROTTEN_FLESH),
                "Denied ritual must not consume the invalid offering");
        helper.assertTrue(after.equals(before),
                "Denied ritual must leave authoritative owner progression unchanged");
        helper.succeed();
    }

    @GameTest(template = "foundation_empty")
    public static void twoAltarsCannotDuplicateOneRitualOutcomeOrConsumption(GameTestHelper helper) {
        ensureSyntheticRitualsRegistered();
        ServerPlayer player = requireServerPlayer(helper);
        ServerLevel level = level(helper);
        BlockPos firstRelative = new BlockPos(1, 1, 1);
        BlockPos secondRelative = new BlockPos(2, 1, 1);
        helper.setBlock(firstRelative, ModBlocks.FLAME_ALTAR.get());
        helper.setBlock(secondRelative, ModBlocks.FLAME_ALTAR.get());
        FlameAltarBlockEntity first = requireAltar(helper, firstRelative);
        FlameAltarBlockEntity second = requireAltar(helper, secondRelative);
        first.inventory().setStackInSlot(0, new ItemStack(Items.BLAZE_POWDER));
        second.inventory().setStackInSlot(0, new ItemStack(Items.BLAZE_POWDER));

        FlameAltarService.ActivationResult applied = FlameAltarRuntime.activate(player, first.inventory());
        FlameAltarService.ActivationResult duplicate = FlameAltarRuntime.activate(player, second.inventory());
        ProgressionOwner owner = ProgressionRuntimeBindings.ownerResolver().resolve(player.getUUID());

        helper.assertTrue(applied.status() == FlameAltarService.Status.APPLIED,
                "First valid altar activation must apply the synthetic ritual");
        helper.assertTrue(duplicate.status() == FlameAltarService.Status.ALREADY_COMPLETED,
                "Second altar activation for the same owner must be idempotently rejected");
        helper.assertTrue(first.inventory().getStackInSlot(0).isEmpty(),
                "First successful ritual must consume exactly one offering");
        helper.assertTrue(second.inventory().getStackInSlot(0).is(Items.BLAZE_POWDER),
                "Duplicate ritual must not consume the second altar offering");
        helper.assertTrue(FlameProgressionSavedData.get(level).progression(owner).completedRituals().stream()
                        .filter(DOUBLE_RITUAL_ID::equals).count() == 1L,
                "Owner progression must contain the double-activation ritual exactly once");
        helper.succeed();
    }

    @GameTest(template = "foundation_empty")
    public static void breakingAltarAfterRitualNeverRollsBackOwnerProgression(GameTestHelper helper) {
        ensureSyntheticRitualsRegistered();
        ServerPlayer player = requireServerPlayer(helper);
        ServerLevel level = level(helper);
        BlockPos relative = new BlockPos(1, 1, 1);
        helper.setBlock(relative, ModBlocks.FLAME_ALTAR.get());
        FlameAltarBlockEntity altar = requireAltar(helper, relative);
        altar.inventory().setStackInSlot(0, new ItemStack(Items.MAGMA_CREAM));

        FlameAltarService.ActivationResult applied = FlameAltarRuntime.activate(player, altar.inventory());
        ProgressionOwner owner = ProgressionRuntimeBindings.ownerResolver().resolve(player.getUUID());
        helper.assertTrue(applied.status() == FlameAltarService.Status.APPLIED,
                "Precondition: synthetic break-test ritual must apply before block destruction");

        helper.destroyBlock(relative);

        helper.assertBlockNotPresent(ModBlocks.FLAME_ALTAR.get(), relative);
        helper.assertTrue(FlameProgressionSavedData.get(level).progression(owner).completedRituals().contains(BREAK_RITUAL_ID),
                "Breaking/replacing a Flame Altar must never roll back earned owner progression");
        helper.succeed();
    }

    private static void ensureSyntheticRitualsRegistered() {
        if (REGISTERED.compareAndSet(false, true)) {
            FlameAltarRuntime.registerRitual(syntheticRitual(DOUBLE_RITUAL_ID, DOUBLE_INTENT_ID, Items.BLAZE_POWDER));
            FlameAltarRuntime.registerRitual(syntheticRitual(BREAK_RITUAL_ID, BREAK_INTENT_ID, Items.MAGMA_CREAM));
        }
    }

    private static FlameRitual syntheticRitual(ResourceLocation ritualId, ResourceLocation intentId, Item requiredItem) {
        return new FlameRitual() {
            @Override
            public ResourceLocation id() {
                return ritualId;
            }

            @Override
            public ResourceLocation intentId() {
                return intentId;
            }

            @Override
            public boolean isEligible(Context context) {
                return context.progression().flameLevel() == 1 && context.progression().passageLevel() == 1;
            }

            @Override
            public OfferingContract offering() {
                return new OfferingContract() {
                    @Override
                    public boolean accepts(Context context, Offering offering) {
                        return offering instanceof FlameAltarOffering altarOffering
                                && altarOffering.stack().is(requiredItem);
                    }

                    @Override
                    public void consume(Context context, Offering offering) {
                        if (!(offering instanceof FlameAltarOffering altarOffering) || !altarOffering.consumeOne()) {
                            throw new IllegalStateException("GameTest altar offering changed before consumption");
                        }
                    }
                };
            }

            @Override
            public RitualOutcome outcome(Context context) {
                return RitualOutcome.levelOneCheckpoint();
            }
        };
    }

    private static ServerLevel level(GameTestHelper helper) {
        return GameTestBootstrap.requireServerLevel(helper);
    }

    private static ServerPlayer requireServerPlayer(GameTestHelper helper) {
        var player = GameTestBootstrap.makeMockPlayer(helper, GameType.SURVIVAL);
        helper.assertTrue(player instanceof ServerPlayer,
                "Flame Altar GameTest requires the mock player to be a ServerPlayer");
        return (ServerPlayer) player;
    }

    private static FlameAltarBlockEntity requireAltar(GameTestHelper helper, BlockPos relative) {
        var blockEntity = level(helper).getBlockEntity(helper.absolutePos(relative));
        helper.assertTrue(blockEntity instanceof FlameAltarBlockEntity,
                "Placed enshrouded:flame_altar must create FlameAltarBlockEntity");
        return (FlameAltarBlockEntity) blockEntity;
    }
}
