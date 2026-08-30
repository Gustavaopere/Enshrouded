package com.gustavaopere.enshrouded.ecology.state;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

final class EntityCorruptionAttachmentStreamCodecTest {
    @Test
    void streamCodecRoundTripsCanonicalState() {
        ByteBuf buffer = Unpooled.buffer();
        try {
            EntityCorruptionAttachment source = new EntityCorruptionAttachment(
                    EntityCorruptionSchema.CURRENT_VERSION,
                    0.75F
            );

            EntityCorruptionAttachment.STREAM_CODEC.encode(buffer, source);
            EntityCorruptionAttachment decoded = EntityCorruptionAttachment.STREAM_CODEC.decode(buffer);

            assertEquals(source, decoded);
            assertFalse(buffer.isReadable(), "stream codec must consume exactly the bytes it writes");
        } finally {
            buffer.release();
        }
    }
}
