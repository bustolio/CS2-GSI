package com.cs2gsi.nodes.helpers;

/**
 * Class representing 2D vectors.
 */
public final class Vector2D {
    /**
     * The X component of the vector.
     */
    public final int x;

    /**
     * The Y component of the vector.
     */
    public final int y;

    public Vector2D() {
        this(0, 0);
    }

    public Vector2D(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Vector2D other
                && x == other.x
                && y == other.y;
    }

    @Override
    public int hashCode() {
        int hashCode = 1861411795;
        hashCode = hashCode * -1521134295 + Integer.hashCode(x);
        hashCode = hashCode * -1521134295 + Integer.hashCode(y);
        return hashCode;
    }

    @Override
    public String toString() {
        return "[X: " + x + ", Y: " + y + "]";
    }
}
