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

  public Volume atOrientation(int orientation) {
    try {
      Volume ret = (Volume) this.clone();
      switch (orientation) {
        case 2: {
          ret.dimension1 = dimension3;
          ret.dimension2 = dimension2;
          ret.dimension3 = dimension1;
          break;
        }
        case 3: {
          ret.dimension1 = dimension3;
          ret.dimension2 = dimension1;
          ret.dimension3 = dimension2;
          break;
        }
        case 4: {
          ret.dimension1 = dimension2;
          ret.dimension2 = dimension1;
          ret.dimension3 = dimension3;
          break;
        }
        case 5: {
          ret.dimension1 = dimension1;
          ret.dimension2 = dimension3;
          ret.dimension3 = dimension2;
          break;
        }
        case 6: {
          ret.dimension1 = dimension2;
          ret.dimension2 = dimension3;
          ret.dimension3 = dimension1;
          break;
        }
        default: {
          ret.dimension1 = dimension1;
          ret.dimension2 = dimension2;
          ret.dimension3 = dimension3;
          break;
        }
      }
      return ret;
    } catch (CloneNotSupportedException e) {
      e.printStackTrace();
    }
    return null;
  }

  @Override
  protected Object clone() throws CloneNotSupportedException {
    return new Volume(id, dimension1, dimension2, dimension3, quantity, rotation);
  }

  @Override
  public String toString() {
    return "Volume{" +
            "id=" + id +
            ", quantity=" + quantity +
            ", dimension1=" + dimension1 +
            ", dimension2=" + dimension2 +
            ", dimension3=" + dimension3 +
            ", rotation=" + rotation +
            '}';
  }
}
