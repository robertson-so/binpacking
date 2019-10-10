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
  private ScrapPad previous;
  /**
   * Following entry.
   */
  private ScrapPad next;

  /**
   * X coordinate of the gap's right corner.
   */
  private long gapX;
  /**
   * Z coordinate of the gap's right corner.
   */
  private long gapZ;

  public enum Situation {
    EMPTY, ONLY_LEFT_BOX, ONLY_RIGHT_BOX, EQUAL_SIDES, DIFFERENT_SIDES
  }

  public ScrapPad() {
    this.previous = null;
    this.next = null;
  }

  public ScrapPad(ScrapPad previous, ScrapPad next) {
    this.previous = previous;
    this.next = next;
  }

  public ScrapPad(ScrapPad previous, ScrapPad next, long gapX, long gapZ) {
    this.previous = previous;
    this.next = next;
    this.gapX = gapX;
    this.gapZ = gapZ;
  }

  public Situation isSituation() {
    Situation ret;
    if (this.previous == null && this.next == null) {
      ret = Situation.EMPTY;
    } else if (this.previous == null) {
      ret = Situation.ONLY_RIGHT_BOX;
    } else if (this.next == null) {
      ret = Situation.ONLY_LEFT_BOX;
    } else if (this.previous.gapZ == this.next.gapZ) {
      ret = Situation.EQUAL_SIDES;
    } else {
      ret = Situation.DIFFERENT_SIDES;
    }
    return ret;
  }

  public void updateGaps(long gapX, long gapZ) {
    this.gapX = gapX;
    this.gapZ = gapZ;
  }

  public ScrapPad getPrevious() {
    return previous;
  }

  public void setPrevious(ScrapPad previous) {
    this.previous = previous;
  }

  public ScrapPad getNext() {
    return next;
  }

  public void setNext(ScrapPad next) {
    this.next = next;
  }

  public long getGapX() {
    return gapX;
  }

  public void setGapX(long gapX) {
    this.gapX = gapX;
  }

  public void incrementGapX(long toIncrement) {
    this.gapX += toIncrement;
  }

  public long getGapZ() {
    return gapZ;
  }

  public void setGapZ(long gapZ) {
    this.gapZ = gapZ;
  }

  public void incrementGapZ(long toIncrement) {
    this.gapZ += toIncrement;
  }
}
