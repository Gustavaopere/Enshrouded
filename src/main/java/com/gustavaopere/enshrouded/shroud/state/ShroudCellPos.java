package com.gustavaopere.enshrouded.shroud.state;

public record ShroudCellPos(int x, int y, int z) implements Comparable<ShroudCellPos> {
    @Override
    public int compareTo(ShroudCellPos other) {
        int byX = Integer.compare(x, other.x);
        if (byX != 0) {
            return byX;
        }
        int byY = Integer.compare(y, other.y);
        if (byY != 0) {
            return byY;
        }
        return Integer.compare(z, other.z);
    }
}
