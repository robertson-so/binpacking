package dev.rsoliveira.tools.binpacking.domain;

/**
 * The container that will receive items to be packed.
 */
public class Container extends Volume implements Cloneable {

    public Container() {
        super();
    }

    public Container(int id, long dimension1, long dimension2, long dimension3, ItemRotation rotation) {
        super(id, dimension1, dimension2, dimension3, 1, rotation);
    }

    @Override
    public Object clone() {
        Container other = new Container(this.getId(), this.getDimension1(), this.getDimension2(), this.getDimension3(), this.getRotation());
        other.setOrientation(this.getOrientation());
        other.setQuantity(this.getQuantity());
        return other;
    }
}
