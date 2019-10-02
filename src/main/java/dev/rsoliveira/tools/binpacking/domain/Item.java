package dev.rsoliveira.tools.binpacking.domain;

/**
 * The item to be packed inside a container.
 */
public class Item extends Volume {

  public String code;
  public boolean packed;
  public long positionX, positionY, positionZ;
  public long orientationX, orientationY, orientationZ;
  public ItemRotation rotation;

  public Item(int id, String code, long dimension1, long dimension2, long dimension3, int quantity, ItemRotation rotation) {
    super(id, dimension1, dimension2, dimension3, quantity);
    this.code = code;
    this.rotation = rotation;
  }

  public void reset() {
    this.packed = false;
    this.positionX = 0;
    this.positionY = 0;
    this.positionZ = 0;
    this.orientationX = 0;
    this.orientationY = 0;
    this.orientationZ = 0;
  }

  public void packAtOrientation(long orientationX, long orientationY, long orientationZ) {
    this.packed = true;
    this.orientationX = orientationX;
    this.orientationY = orientationY;
    this.orientationZ = orientationZ;
  }

  public void setPosition(long positionX, long positionY, long positionZ) {
    this.positionX = positionX;
    this.positionY = positionY;
    this.positionZ = positionZ;
  }
}
