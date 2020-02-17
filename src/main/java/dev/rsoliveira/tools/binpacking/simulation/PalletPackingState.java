package dev.rsoliveira.tools.binpacking.simulation;

import dev.rsoliveira.tools.binpacking.domain.Container;
import dev.rsoliveira.tools.binpacking.domain.Item;
import dev.rsoliveira.tools.binpacking.domain.ScrapPad;

import java.util.ArrayList;
import java.util.List;

public class PalletPackingState implements Cloneable {

    private boolean packing;
    private boolean layerDone;
    private boolean evenedLayer;
    private boolean hundredPercentPacked;

    private int bestVariant;
    private int bestIteration;
    private double bestVolume;

    private long boxX, boxY, boxZ;
    private int boxFittingIndex;
    private long bBoxX, bBoxY, bBoxZ;
    private int boxNotFittingIndex;
    private long checkedBoxX, checkedBoxY, checkedBoxZ;
    private int checkedBoxIndex;
    private long boxFittingX, boxFittingY, boxFittingZ;
    private long boxNotFittingX, boxNotFittingY, botNotFittingZ;

    private long layerInLayer;
    private long preLayer;
    private long layerInLayerZ;
    private long maxAvailableThickness;
    private long remainpz;
    private long layerThickness;

    private double packedVolume;
    private double totalItemVolume;

    private Container container;
    private List<Item> inputItems;
    private Item[] itemsToPack;

    private ScrapPad scrapFirst;

    public PalletPackingState(Container container, List<Item> items) {
        this.container = container;
        this.inputItems = items;

        double totalItemVolume = 0.0;
        int total = 0;
        for (Item item : items) {
            total += item.getQuantity();
            totalItemVolume += (item.getVolume() * item.getQuantity());
        }
        this.totalItemVolume = totalItemVolume;
        this.itemsToPack = new Item[total];

        int index = 0;
        long totalItemsToPack = 0;
        for (Item item : items) {
            totalItemsToPack += item.getQuantity();
            for (; index < totalItemsToPack; index++) {
                this.itemsToPack[index] =
                        new Item(index, item.getCode(),
                                item.getDimension1(), item.getDimension2(), item.getDimension3(), item.getQuantity(),
                                item.getRotation());
            }
        }

        this.scrapFirst = new ScrapPad();
        this.hundredPercentPacked = false;
    }

    public void restartPacking() {
        setPackedVolume(0.0);
        setPacking(true);
        for (int i = 0; i < getItemsToPack().length; i++) {
            getItemsToPack()[i].reset();
        }
    }

    public void resetThickness(long layerThickness, long maxAvailableThickness, long remainpz) {
        setLayerThickness(layerThickness);
        setMaxAvailableThickness(maxAvailableThickness);
        setRemainpz(remainpz);
    }

    public void validateBestState(int containerOrientation, int layersindex) {
        if (getPackedVolume() > getBestVolume()) {
            setBestVolume(getPackedVolume());
            setBestVariant(containerOrientation);
            setBestIteration(layersindex);
        }
    }

    public boolean isPacking() {
        return packing;
    }

    public void setPacking(boolean packing) {
        this.packing = packing;
    }

    public boolean isLayerDone() {
        return layerDone;
    }

    public void setLayerDone(boolean layerDone) {
        this.layerDone = layerDone;
    }

    public boolean isEvenedLayer() {
        return evenedLayer;
    }

    public void setEvenedLayer(boolean evenedLayer) {
        this.evenedLayer = evenedLayer;
    }

    public boolean isHundredPercentPacked() {
        return hundredPercentPacked;
    }

    public void setHundredPercentPacked(boolean hundredPercentPacked) {
        this.hundredPercentPacked = hundredPercentPacked;
    }

    public int getBestVariant() {
        return bestVariant;
    }

    public void setBestVariant(int bestVariant) {
        this.bestVariant = bestVariant;
    }

    public int getBestIteration() {
        return bestIteration;
    }

    public void setBestIteration(int bestIteration) {
        this.bestIteration = bestIteration;
    }

    public double getBestVolume() {
        return bestVolume;
    }

    public void setBestVolume(double bestVolume) {
        this.bestVolume = bestVolume;
    }

    public long getBoxX() {
        return boxX;
    }

    public void setBoxX(long boxX) {
        this.boxX = boxX;
    }

    public long getBoxY() {
        return boxY;
    }

    public void setBoxY(long boxY) {
        this.boxY = boxY;
    }

    public long getBoxZ() {
        return boxZ;
    }

    public void setBoxZ(long boxZ) {
        this.boxZ = boxZ;
    }

    public int getBoxFittingIndex() {
        return boxFittingIndex;
    }

    public void setBoxFittingIndex(int boxFittingIndex) {
        this.boxFittingIndex = boxFittingIndex;
    }

    public long getbBoxX() {
        return bBoxX;
    }

    public void setbBoxX(long bBoxX) {
        this.bBoxX = bBoxX;
    }

    public long getbBoxY() {
        return bBoxY;
    }

    public void setbBoxY(long bBoxY) {
        this.bBoxY = bBoxY;
    }

    public long getbBoxZ() {
        return bBoxZ;
    }

    public void setbBoxZ(long bBoxZ) {
        this.bBoxZ = bBoxZ;
    }

    public int getBoxNotFittingIndex() {
        return boxNotFittingIndex;
    }

    public void setBoxNotFittingIndex(int boxNotFittingIndex) {
        this.boxNotFittingIndex = boxNotFittingIndex;
    }

    public long getCheckedBoxX() {
        return checkedBoxX;
    }

    public void setCheckedBoxX(long checkedBoxX) {
        this.checkedBoxX = checkedBoxX;
    }

    public long getCheckedBoxY() {
        return checkedBoxY;
    }

    public void setCheckedBoxY(long checkedBoxY) {
        this.checkedBoxY = checkedBoxY;
    }

    public long getCheckedBoxZ() {
        return checkedBoxZ;
    }

    public void setCheckedBoxZ(long checkedBoxZ) {
        this.checkedBoxZ = checkedBoxZ;
    }

    public int getCheckedBoxIndex() {
        return checkedBoxIndex;
    }

    public void setCheckedBoxIndex(int checkedBoxIndex) {
        this.checkedBoxIndex = checkedBoxIndex;
    }

    public long getBoxFittingX() {
        return boxFittingX;
    }

    public void setBoxFittingX(long boxFittingX) {
        this.boxFittingX = boxFittingX;
    }

    public long getBoxFittingY() {
        return boxFittingY;
    }

    public void setBoxFittingY(long boxFittingY) {
        this.boxFittingY = boxFittingY;
    }

    public long getBoxFittingZ() {
        return boxFittingZ;
    }

    public void setBoxFittingZ(long boxFittingZ) {
        this.boxFittingZ = boxFittingZ;
    }

    public long getBoxNotFittingX() {
        return boxNotFittingX;
    }

    public void setBoxNotFittingX(long boxNotFittingX) {
        this.boxNotFittingX = boxNotFittingX;
    }

    public long getBoxNotFittingY() {
        return boxNotFittingY;
    }

    public void setBoxNotFittingY(long boxNotFittingY) {
        this.boxNotFittingY = boxNotFittingY;
    }

    public long getBotNotFittingZ() {
        return botNotFittingZ;
    }

    public void setBotNotFittingZ(long botNotFittingZ) {
        this.botNotFittingZ = botNotFittingZ;
    }

    public long getLayerInLayer() {
        return layerInLayer;
    }

    public void setLayerInLayer(long layerInLayer) {
        this.layerInLayer = layerInLayer;
    }

    public long getPreLayer() {
        return preLayer;
    }

    public void setPreLayer(long preLayer) {
        this.preLayer = preLayer;
    }

    public long getLayerInLayerZ() {
        return layerInLayerZ;
    }

    public void setLayerInLayerZ(long layerInLayerZ) {
        this.layerInLayerZ = layerInLayerZ;
    }

    public long getMaxAvailableThickness() {
        return maxAvailableThickness;
    }

    public void setMaxAvailableThickness(long maxAvailableThickness) {
        this.maxAvailableThickness = maxAvailableThickness;
    }

    public long getRemainpz() {
        return remainpz;
    }

    public void setRemainpz(long remainpz) {
        this.remainpz = remainpz;
    }

    public long getLayerThickness() {
        return layerThickness;
    }

    public void setLayerThickness(long layerThickness) {
        this.layerThickness = layerThickness;
    }

    public double getPackedVolume() {
        return packedVolume;
    }

    public void setPackedVolume(double packedVolume) {
        this.packedVolume = packedVolume;
    }

    public double getTotalContainerVolume() {
        return container.getVolume();
    }

    public double getTotalItemVolume() {
        return totalItemVolume;
    }

    public Container getContainer() {
        return container;
    }

    public void setContainer(Container container) {
        this.container = container;
    }

    public List<Item> getInputItems() {
        return inputItems;
    }

    public Item[] getItemsToPack() {
        return itemsToPack;
    }

    public ScrapPad getScrapFirst() {
        return scrapFirst;
    }

    @Override
    protected Object clone() {
        List<Item> otherItems = new ArrayList<>();
        for (Item it : this.inputItems) {
            otherItems.add((Item) it.clone());
        }
        return new PalletPackingState((Container) container.clone(), otherItems);
    }
}
