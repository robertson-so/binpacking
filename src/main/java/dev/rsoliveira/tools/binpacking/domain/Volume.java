package dev.rsoliveira.tools.binpacking.domain;

public class Volume {

  public int id;
  public int quantity;
  public long dimension1, dimension2, dimension3;


  public Volume(int id, long dimension1, long dimension2, long dimension3, int quantity) {
    this.id = id;
    this.dimension1 = dimension1;
    this.dimension2 = dimension2;
    this.dimension3 = dimension3;
    this.quantity = quantity;
  }

  public double getVolume() {
    return dimension1 * dimension2 * dimension3;
  }

  public boolean isCubic() {
    return this.dimension1 == this.dimension2 && this.dimension2 == this.dimension3;
  }
}
