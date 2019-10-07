package dev.rsoliveira.tools.binpacking.domain;

/**
 * Represents a layer thickness obtained from an evaluation iteration.
 */
public class Layer implements Comparable<Layer> {

  /**
   * A weight value for the layer.
   */
  private double weight;
  /**
   * The thickness of the layer.
   */
  private long dimension;


  public Layer(double weight, long dimension) {
    this.weight = weight;
    this.dimension = dimension;
  }

  public int compareTo(Layer other) {
    double result = weight - other.weight;
    if (result < 0) {
      return -1;
    } else if (result == 0) {
      return 0;
    } else {
      return 1;
    }
  }

  public double getWeight() {
    return weight;
  }

  public long getDimension() {
    return dimension;
  }

}
