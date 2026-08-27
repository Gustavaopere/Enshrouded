package com.gustavaopere.enshrouded.api.combat;

import java.util.Objects;

public record MagicDamageClassification(MagicDamageKind kind, MagicDamageConfidence confidence) {
    public MagicDamageClassification {
        Objects.requireNonNull(kind, "kind");
        Objects.requireNonNull(confidence, "confidence");
    }

    public boolean magical() {
        return kind.magical();
    }
}
