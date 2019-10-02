package dev.rsoliveira.tools.binpacking.domain;

/**
 * A topology of the edge of the current layer under construction.
 * X and Z coordinates are kept for each gap's right corner.
 * A search for gaps is made; for each gap found a test is made to fill the boxes, trying to keep
 * the edge of the layer even.
 */
public class ScrapPad {

  /**
   * Previous entry.
   */
  public ScrapPad left;
  /**
   * Following entry.
   */
  public ScrapPad right;

  /**
   * X coordinate of the gap's right corner.
   */
  public long gapX;
  /**
   * Z coordinate of the gap's right corner.
   */
  public long gapZ;

  public enum Situation {
    EMPTY, ONLY_LEFT_BOX, ONLY_RIGHT_BOX, EQUAL_SIDES, DIFFERENT_SIDES
  }

  public ScrapPad() {
    this.left = null;
    this.right = null;
  }

  public ScrapPad(ScrapPad left, ScrapPad right) {
    this.left = left;
    this.right = right;
  }

  public ScrapPad(ScrapPad left, ScrapPad right, long gapX, long gapZ) {
    this.left = left;
    this.right = right;
    this.gapX = gapX;
    this.gapZ = gapZ;
  }

  public Situation isSituation() {
    Situation ret;
    if (this.left == null && this.right == null) {
      ret = Situation.EMPTY;
    } else if (this.left == null) {
      ret = Situation.ONLY_RIGHT_BOX;
    } else if (this.right == null) {
      ret = Situation.ONLY_LEFT_BOX;
    } else if (this.left.gapZ == this.right.gapZ) {
      ret = Situation.EQUAL_SIDES;
    } else {
      ret = Situation.DIFFERENT_SIDES;
    }
    return ret;
  }

  public boolean noBoxes() {
    return this.left == null && this.right == null;
  }

  public boolean boxOnlyAtRight() {
    return this.left == null && this.right != null;
  }

  public boolean boxOnlyAtLeft() {
    return this.left != null && this.right == null;
  }
}
