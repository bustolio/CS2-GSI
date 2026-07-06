package com.cs2gsi.nodes.helpers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class representing 3D vectors.
 */
public final class Vector3D {
    private static final Pattern VECTOR_PATTERN =
            Pattern.compile("([+-]?[0-9]*[.]?[0-9]+), ([+-]?[0-9]*[.]?[0-9]+), ([+-]?[0-9]*[.]?[0-9]+)");

    /**
     * The X component of the vector.
     */
    public final float x;

    /**
     * The Y component of the vector.
     */
    public final float y;

    /**
     * The Z component of the vector.
     */
    public final float z;

    public Vector3D() {
        this(0, 0, 0);
    }

    public Vector3D(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Construct Vector3D from a properly formatted string.
     *
     * @param str The string to construct the Vector3D from.
     */
    public Vector3D(String str) {
        float px = 0, py = 0, pz = 0;

        if (str != null && !str.isEmpty()) {
            Matcher matcher = VECTOR_PATTERN.matcher(str);

            if (matcher.find()) {
                try {
                    px = Float.parseFloat(matcher.group(1));
                    py = Float.parseFloat(matcher.group(2));
                    pz = Float.parseFloat(matcher.group(3));
                } catch (NumberFormatException ignored) {
                }
            }
        }

        this.x = px;
        this.y = py;
        this.z = pz;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Vector3D other
                && x == other.x
                && y == other.y
                && z == other.z;
    }

    @Override
    public int hashCode() {
        int hashCode = 547961354;
        hashCode = hashCode * -578989432 + Float.hashCode(x);
        hashCode = hashCode * -578989432 + Float.hashCode(y);
        hashCode = hashCode * -578989432 + Float.hashCode(z);
        return hashCode;
    }

    @Override
    public String toString() {
        return "[X: " + x + ", Y: " + y + ", Z: " + z + "]";
    }
}
