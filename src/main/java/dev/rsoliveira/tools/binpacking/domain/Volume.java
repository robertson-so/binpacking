package dev.rsoliveira.tools.binpacking.domain;

public class Volume implements Cloneable {

  private int id;
  private int quantity;
  private long dimension1, dimension2, dimension3;
  private ItemRotation rotation;

  public Volume(int id, long dimension1, long dimension2, long dimension3, int quantity, ItemRotation rotation) {
    this.id = id;
    this.dimension1 = dimension1;
    this.dimension2 = dimension2;
    this.dimension3 = dimension3;
    this.quantity = quantity;
    this.rotation = rotation;
  }

  public double getVolume() {
    return dimension1 * dimension2 * dimension3;
  }

  public boolean isCubic() {
    return this.dimension1 == this.dimension2 && this.dimension2 == this.dimension3;
  }

  public long getDimension1() {
    return dimension1;
  }

  public long getDimension2() {
    return dimension2;
  }

  public long getDimension3() {
    return dimension3;
  }

  public ItemRotation getRotation() {
    return rotation;
  }

  public int getQuantity() {
    return quantity;
  }

  public void setQuantity(int quantity) {
    this.quantity = quantity;
  }

  public int getId() {
    return id;
  }

  @Override
  protected Object clone() throws CloneNotSupportedException {
    return new Volume(id, dimension1, dimension2, dimension3, quantity, rotation);
  }
}
