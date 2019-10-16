package dev.rsoliveira.tools.binpacking.domain;

/**
 * Rotations:
 * xyz - height is represented by y
 * zyx - height is represented by y
 * zxy - height is represented by x
 * yxz - height is represented by x
 * xzy - height is represented by z
 * yzx - height is represented by z
 * <p>
 * faces:
 * xy
 * xz
 * yz
 */
public enum ItemRotation {

    FULL, // xyz, zyx, zxy, yxz, xzy, yzx
    HORIZONTAL, // xyz, zyx
    NONE // xyz
}
