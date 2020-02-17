package dev.rsoliveira.tools.binpacking.domain;

import java.util.Objects;

/**
 * The item to be packed inside a container.
 */
public class Item extends Volume implements Cloneable {

    private String code;
    private boolean packed;
    private long positionX, positionY, positionZ;
    private long orientationX, orientationY, orientationZ;

    public Item() {
        super();
    }

    public Item(int id, String code, long dimension1, long dimension2, long dimension3, int quantity,
                ItemRotation rotation) {
        super(id, dimension1, dimension2, dimension3, quantity, rotation);
        this.code = code;
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

    public long getPositionX() {
        return positionX;
    }

    public long getPositionY() {
        return positionY;
    }

    public long getPositionZ() {
        return positionZ;
    }

    public long getOrientationX() {
        return orientationX;
    }

    public long getOrientationY() {
        return orientationY;
    }

    public long getOrientationZ() {
        return orientationZ;
    }

    public double getMinPositionX() {
        return positionX;
    }

    public long getMaxPositionX() {
        return positionX + orientationX;
    }

    public long getMinPositionY() {
        return positionY;
    }

    public long getMaxPositionY() {
        return positionY + orientationY;
    }

    public long getMinPositionZ() {
        return positionZ;
    }

    public long getMaxPositionZ() {
        return positionZ + orientationZ;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setPacked(boolean packed) {
        this.packed = packed;
    }

    public void setPositionX(long positionX) {
        this.positionX = positionX;
    }

    public void setPositionY(long positionY) {
        this.positionY = positionY;
    }

    public void setPositionZ(long positionZ) {
        this.positionZ = positionZ;
    }

    public void setOrientationX(long orientationX) {
        this.orientationX = orientationX;
    }

    public void setOrientationY(long orientationY) {
        this.orientationY = orientationY;
    }

    public void setOrientationZ(long orientationZ) {
        this.orientationZ = orientationZ;
    }

    public boolean isPacked() {
        return packed;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Item)) return false;
        Item item = (Item) o;
        return code.equals(item.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }

    @Override
    public Object clone() {
        return new Item(this.getId(), getCode(), this.getDimension1(), this.getDimension2(),
                this.getDimension3(), this.getQuantity(), this.getRotation());
    }

    @Override
    public String toString() {
        return "Item{" +
                "code='" + code + '\'' +
                ", packed=" + packed +
                ", positionX=" + positionX +
                ", positionY=" + positionY +
                ", positionZ=" + positionZ +
                ", orientationX=" + orientationX +
                ", orientationY=" + orientationY +
                ", orientationZ=" + orientationZ +
                '}';
    }
}
