package dev.rsoliveira.tools.binpacking.domain;

/**
 * Rotations:
 * xyz
 * xzy
 * yxz
 * yzx
 * zxy
 * zyx
 *
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
