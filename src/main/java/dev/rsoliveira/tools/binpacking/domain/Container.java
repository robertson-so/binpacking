package dev.rsoliveira.tools.binpacking.domain;

/**
 * The container that will receive items to be packed.
 */
public class Container extends Volume {

    public Container() {
        super();
    }

    public Container(int id, long dimension1, long dimension2, long dimension3, ItemRotation rotation) {
        super(id, dimension1, dimension2, dimension3, 1, rotation);
    }
}
