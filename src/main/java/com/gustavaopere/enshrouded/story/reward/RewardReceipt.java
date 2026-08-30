package com.gustavaopere.enshrouded.story.reward;

import com.gustavaopere.enshrouded.api.progression.ProgressionOwner;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;
import java.util.UUID;

/** Proof of one committed story-reward issuance. */
public record RewardReceipt(
        ProgressionOwner owner,
        UUID encounterId,
        int manifestationIndex,
        ItemStack reward) {

    public RewardReceipt {
        owner = Objects.requireNonNull(owner, "owner");
        encounterId = Objects.requireNonNull(encounterId, "encounterId");
        if (manifestationIndex < 1) {
            throw new IllegalArgumentException("manifestationIndex must be >= 1");
        }
        reward = Objects.requireNonNull(reward, "reward").copy();
        if (reward.isEmpty() || reward.getCount() != 1) {
            throw new IllegalArgumentException("reward receipt must contain exactly one item");
        }
    }

    @Override
    public ItemStack reward() {
        return reward.copy();
    }
}
