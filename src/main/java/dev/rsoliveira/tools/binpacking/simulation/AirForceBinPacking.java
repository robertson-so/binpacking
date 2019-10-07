package dev.rsoliveira.tools.binpacking.simulation;

import dev.rsoliveira.tools.binpacking.domain.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * A 3D bin packing simulation that uses a human heuristic to perform the packing.
 * The heuristic simulates a wall building; it packs items using left to right direction, creating layers
 * of items with compatible heights.
 * Both container and items can be rotated in horizontal axes only or in all directions, for better
 * space utilization.
 */
public class AirForceBinPacking implements ISimulation<Container, Item> {

    private final int MAX_LENGTH = 32767;

    private boolean packing;
    private boolean layerDone;
    private boolean evenedLayer;
    private int bestVariant;
    private boolean hundredPercentPacked;

    private long boxX, boxY, boxZ;
    private int boxFittingIndex;
    private long bboxx, bboxy, bboxz;
    private int boxNotfittingIndex;
    private long checkedBoxX, checkedBoxY, checkedBoxZ;
    private int checkedBoxIndex;
    private long boxFittingX, boxFittingY, boxFittingZ;
    private long boxNotFittingX, boxNotFittingY, botNotFittingZ;
    private long containerX, containerY, containerZ;

    private long layerInLayer;
    private long preLayer;
    private long layerInLayerZ;
    private long maxAvailableThickness, remainpz;
    private long layerThickness;
    private int bestIteration;
    private long packedItemCounter;
    private long bestPackedTotal;

    private double packedVolume;
    private double bestVolume;
    private double totalContainerVolume;
    private double totalItemVolume;
    private double percentageUsed;

    private List<Item> inputItems;
    private Item[] itemsToPack;

    private List<Layer> layers = new ArrayList<>();

    private ScrapPad scrapFirst;

    /**
     * Simulates packing a list of items into a container.
     * @param container a container that will receive all the possible items.
     * @param volumes a list of items to pack into to the container.
     * @return a list of packed items, with orientation and direction for each item, and the container orientation, and
     * a list of unpacked items.
     */
    public Solution simulate(Container container, List<Item> volumes) {
        initialize(container, volumes);
        iterate(container);
        Solution solution = report(container);

        return solution;
    }

    private void initialize(Container container, List<Item> items) {
        long totalItemsToPack = 0;

        layers = new ArrayList<>();
        layers.add(new Layer(-1, 0));

        inputItems = items;
        int total = 0;
        for (Item item : items) {
            total += item.getQuantity();
        }
        itemsToPack = new Item[total];

        // creates all necessary items, based on the items' configuration
        int index = 0;
        for (Item item : items) {
            totalItemsToPack += item.getQuantity();
            for (; index < totalItemsToPack; index++) {
                itemsToPack[index] = (new Item(index + 1, item.getCode(),
                        item.getDimension1(), item.getDimension2(), item.getDimension3(), 1,
                        item.getRotation()));
            }
        }

        totalContainerVolume = container.getVolume();
        totalItemVolume = 0.0;
        for (Item item : itemsToPack) {
            totalItemVolume += item.getVolume();
        }

        scrapFirst = new ScrapPad();
        hundredPercentPacked = false;
    }

    /**
     * Given a container, for each container orientation iterate all possible layers to fill it and find the best iteration.
     *
     * @param container the container to be filled with items.
     */
    private void iterate(Container container) {
        long prepackedy;
        long preremainpy;
        long packedy;
        bestVolume = 0.0;

        int maxContainerOrientation;
        switch (container.getRotation()) {
            case FULL:
                maxContainerOrientation = 6;
                break;
            case HORIZONTAL:
                maxContainerOrientation = 2;
                break;
            default:
                maxContainerOrientation = 1;
                break;
        }

        for (int containerOrientation = 1; containerOrientation <= maxContainerOrientation; containerOrientation++) {
            switch (containerOrientation) {
                case 2:
                    containerX = container.getDimension3();
                    containerY = container.getDimension2();
                    containerZ = container.getDimension1();
                    break;
                case 3:
                    containerX = container.getDimension3();
                    containerY = container.getDimension1();
                    containerZ = container.getDimension2();
                    break;
                case 4:
                    containerX = container.getDimension2();
                    containerY = container.getDimension1();
                    containerZ = container.getDimension3();
                    break;
                case 5:
                    containerX = container.getDimension1();
                    containerY = container.getDimension3();
                    containerZ = container.getDimension2();
                    break;
                case 6:
                    containerX = container.getDimension2();
                    containerY = container.getDimension3();
                    containerZ = container.getDimension1();
                    break;
                default:
                    containerX = container.getDimension1();
                    containerY = container.getDimension2();
                    containerZ = container.getDimension3();
                    break;
            }

            listCandidateLayers();
            Collections.sort(layers);

            for (int layersindex = 0; layersindex < layers.size(); layersindex++) {
                packedVolume = 0.0;
                packedy = 0;
                packing = true;
                layerThickness = layers.get(layersindex).getDimension();
                maxAvailableThickness = containerY;
                remainpz = containerZ;
                packedItemCounter = 0;

                Arrays.stream(itemsToPack).forEach(it -> it.reset());

                do {
                    layerInLayer = 0;
                    layerDone = false;
                    packLayer(packedy);
                    packedy += layerThickness;
                    maxAvailableThickness = containerY - packedy;
                    if (layerInLayer != 0) {
                        prepackedy = packedy;
                        preremainpy = maxAvailableThickness;
                        maxAvailableThickness = layerThickness - preLayer;
                        packedy = packedy - layerThickness + preLayer;
                        remainpz = layerInLayerZ;
                        layerThickness = layerInLayer;
                        layerDone = false;
                        packLayer(packedy);
                        packedy = prepackedy;
                        maxAvailableThickness = preremainpy;
                        remainpz = containerZ;
                    }
                    findLayer(maxAvailableThickness);
                }
                while (packing);

                if (packedVolume > bestVolume) {
                    bestVolume = packedVolume;
                    bestVariant = containerOrientation;
                    bestIteration = layersindex;
                    bestPackedTotal = packedItemCounter;
                }

                if (hundredPercentPacked) {
                    break;
                }
                percentageUsed = bestVolume * 100 / totalContainerVolume;
            }
            if (hundredPercentPacked) {
                break;
            }
            if (container.isCubic()) {
                containerOrientation = 6;
            }
        }
    }

    /**
     * Lists all possible layer heights, giving a weight value to each layer.
     */
    private void listCandidateLayers() {
        boolean same;
        long examinedDimension, dimdif, dimension2, dimension3;
        double weight;

        for (Item item : itemsToPack) {
            for (int y = 1; y <= 3; y++) {
                switch (y) {
                    case 2:
                        examinedDimension = item.getDimension2();
                        dimension2 = item.getDimension1();
                        dimension3 = item.getDimension3();
                        break;
                    case 3:
                        examinedDimension = item.getDimension3();
                        dimension2 = item.getDimension1();
                        dimension3 = item.getDimension2();
                        break;
                    default:
                        examinedDimension = item.getDimension1();
                        dimension2 = item.getDimension2();
                        dimension3 = item.getDimension3();
                        break;
                }
                if ((examinedDimension > containerY) ||
                    (((dimension2 > containerX) || (dimension3 > containerZ)) &&
                     ((dimension3 > containerX) || (dimension2 > containerZ)))) {
                    continue;
                }

                long finalExaminedDimension = examinedDimension;
                if (layers.stream().anyMatch(p -> p.getDimension() == finalExaminedDimension)) {
                    continue;
                }

                weight = 0;
                for (Item item2 : itemsToPack) {
                    if (item.getId() == item2.getId()) continue;

                    dimdif = Math.abs(examinedDimension - item2.getDimension1());
                    if (Math.abs(examinedDimension - item2.getDimension2()) < dimdif) {
                        dimdif = Math.abs(examinedDimension - item2.getDimension2());
                    }
                    if (Math.abs(examinedDimension - item2.getDimension3()) < dimdif) {
                        dimdif = Math.abs(examinedDimension - item2.getDimension3());
                    }
                    weight += dimdif;
                }
                layers.add(new Layer(weight, examinedDimension));
            }
        }
    }

    /**
     * Packes the boxes found and arranges all variables and records properly.
     */
    private void packLayer(long packedy) {
        long gapLengthX, gapLengthZ, maxGapZ;
        long newPositionX;
        ScrapPad smallestZ;

        if (layerThickness == 0) {
            packing = false;
            return;
        }

        scrapFirst.updateGaps(containerX, 0);

        while (true) {
            smallestZ = findSmallestZ();
            maxGapZ = remainpz - smallestZ.getGapZ();

            // calculating remaining area in the XZ plane, based on the smallest z found
            switch (smallestZ.isSituation()) {
                case EMPTY: {
                    gapLengthX = smallestZ.getGapX();
                    gapLengthZ = maxGapZ;
                    break;
                }
                case ONLY_RIGHT_BOX: {
                    gapLengthX = smallestZ.getGapX();
                    gapLengthZ = smallestZ.getNext().getGapZ() - smallestZ.getGapZ();
                    break;
                }
                case ONLY_LEFT_BOX: {
                    gapLengthX = smallestZ.getGapX() - smallestZ.getPrevious().getGapX();
                    gapLengthZ = smallestZ.getPrevious().getGapZ() - smallestZ.getGapZ();
                    break;
                }
                case EQUAL_SIDES: {
                    gapLengthX = smallestZ.getGapX() - smallestZ.getPrevious().getGapX();
                    gapLengthZ = smallestZ.getPrevious().getGapZ() - smallestZ.getGapZ();
                    break;
                }
                default: {
                    gapLengthX = smallestZ.getGapX() - smallestZ.getPrevious().getGapX();
                    gapLengthZ = smallestZ.getPrevious().getGapZ() - smallestZ.getGapZ();
                    break;
                }
            }

            findBox(gapLengthX, layerThickness, maxAvailableThickness, gapLengthZ, maxGapZ);
            checkFound(smallestZ);

            if (layerDone) {
                break;
            }
            if (evenedLayer) {
                continue;
            }

            long newPositionY = packedy;
            long newPositionZ = smallestZ.getGapZ();
            // calculating x-position for the item and updating the smallest z, based on the
            switch (smallestZ.isSituation()) {
                case EMPTY: {
                    newPositionX = 0;

                    if (checkedBoxX == smallestZ.getGapX()) {
                        smallestZ.setGapZ(smallestZ.getGapZ() + checkedBoxZ);
                    } else {
                        smallestZ.setNext(new ScrapPad(smallestZ, null, smallestZ.getGapX(), smallestZ.getGapZ()));
                        smallestZ.updateGaps(checkedBoxX, smallestZ.getGapZ() + checkedBoxZ);
                    }
                    break;
                }
                case ONLY_RIGHT_BOX: {
                    newPositionX = 0;

                    if (checkedBoxX == smallestZ.getGapX()) {
                        if (smallestZ.getGapZ() + checkedBoxZ == smallestZ.getNext().getGapZ()) {
                            smallestZ.updateGaps(smallestZ.getNext().getGapX(), smallestZ.getNext().getGapZ());
                            smallestZ.setNext(smallestZ.getNext().getNext());
                            if (smallestZ.getNext() != null) {
                                smallestZ.getNext().setPrevious(smallestZ);
                            }
                        } else {
                            smallestZ.setGapZ(smallestZ.getGapZ() + checkedBoxZ);
                        }
                    } else {
                        newPositionX = smallestZ.getGapX() - checkedBoxX;
                        if (smallestZ.getGapZ() + checkedBoxZ == smallestZ.getNext().getGapZ()) {
                            smallestZ.setGapX(smallestZ.getGapX() - checkedBoxX);
                        } else {
                            smallestZ.getNext().setPrevious(new ScrapPad(smallestZ, smallestZ.getNext()));
                            smallestZ.setNext(smallestZ.getNext().getPrevious());
                            smallestZ.getNext().setGapX(smallestZ.getGapX());
                            smallestZ.setGapX(smallestZ.getGapX() - checkedBoxX);
                            smallestZ.getNext().setGapZ(smallestZ.getGapZ() + checkedBoxZ);
                        }
                    }
                    break;
                }
                case ONLY_LEFT_BOX: {
                    newPositionX = smallestZ.getPrevious().getGapX();

                    if (checkedBoxX == smallestZ.getGapX() - smallestZ.getPrevious().getGapX()) {
                        if (smallestZ.getGapZ() + checkedBoxZ == smallestZ.getPrevious().getGapZ()) {
                            smallestZ.getPrevious().setGapX(smallestZ.getGapX());
                            smallestZ.getPrevious().setNext(null);
                        } else {
                            smallestZ.setGapZ(smallestZ.getGapZ() + checkedBoxZ);
                        }
                    } else {
                        if (smallestZ.getGapZ() + checkedBoxZ == smallestZ.getPrevious().getGapZ()) {
                            smallestZ.getPrevious().setGapX(smallestZ.getPrevious().getGapX() + checkedBoxX);
                        } else {
                            smallestZ.getPrevious().setNext(new ScrapPad(smallestZ.getPrevious(), smallestZ));
                            smallestZ.setPrevious(smallestZ.getPrevious().getNext());
                            smallestZ.getPrevious().updateGaps(
                                    smallestZ.getPrevious().getPrevious().getGapX() + checkedBoxX,
                                    smallestZ.getGapZ() + checkedBoxZ);
                        }
                    }
                    break;
                }
                case EQUAL_SIDES: {
                    newPositionX = smallestZ.getPrevious().getGapX();

                    if (checkedBoxX == smallestZ.getGapX() - smallestZ.getPrevious().getGapX()) {
                        if (smallestZ.getGapZ() + checkedBoxZ == smallestZ.getNext().getGapZ()) {
                            smallestZ.getPrevious().setGapX(smallestZ.getNext().getGapX());
                            if (smallestZ.getNext().getNext() != null) {
                                smallestZ.getPrevious().setNext(smallestZ.getNext().getNext());
                                smallestZ.getNext().getNext().setPrevious(smallestZ.getPrevious());
                            } else {
                                smallestZ.getPrevious().setNext(null);
                            }
                        } else {
                            smallestZ.setGapZ(smallestZ.getGapZ() + checkedBoxZ);
                        }
                    } else if (smallestZ.getPrevious().getGapX() < containerX - smallestZ.getGapX()) {
                        if (smallestZ.getGapZ() + checkedBoxZ == smallestZ.getPrevious().getGapZ()) {
                            smallestZ.setGapX(smallestZ.getGapX() - checkedBoxX);
                            newPositionX = smallestZ.getGapX() - checkedBoxX;
                        } else {
                            smallestZ.getPrevious().setNext(new ScrapPad(smallestZ.getPrevious(), smallestZ));
                            smallestZ.setPrevious(smallestZ.getPrevious().getNext());
                            smallestZ.getPrevious().updateGaps(
                                    smallestZ.getPrevious().getPrevious().getGapX() + checkedBoxX,
                                    smallestZ.getGapZ() + checkedBoxZ);
                        }
                    } else {
                        if (smallestZ.getGapZ() + checkedBoxZ == smallestZ.getPrevious().getGapZ()) {
                            smallestZ.getPrevious().setGapX(smallestZ.getPrevious().getGapX() + checkedBoxX);
                            newPositionX = smallestZ.getPrevious().getGapX();
                        } else {
                            newPositionX = smallestZ.getGapX() - checkedBoxX;
                            smallestZ.getNext().setPrevious(new ScrapPad(smallestZ, smallestZ.getNext()));
                            smallestZ.setNext(smallestZ.getNext().getPrevious());
                            smallestZ.getNext().updateGaps(smallestZ.getGapX(), smallestZ.getGapZ() + checkedBoxZ);
                            smallestZ.setGapX(smallestZ.getGapX() - checkedBoxX);
                        }
                    }
                    break;
                }
                default: {
                    newPositionX = smallestZ.getPrevious().getGapX();

                    if (checkedBoxX == smallestZ.getGapX() - smallestZ.getPrevious().getGapX()) {
                        if (smallestZ.getGapZ() + checkedBoxZ == smallestZ.getPrevious().getGapZ()) {
                            smallestZ.getPrevious().setGapX(smallestZ.getGapX());
                            smallestZ.getPrevious().setNext(smallestZ.getNext());
                            smallestZ.getNext().setPrevious(smallestZ.getPrevious());
                        } else {
                            smallestZ.setGapZ(smallestZ.getGapZ() + checkedBoxZ);
                        }
                    } else {
                        if (smallestZ.getGapZ() + checkedBoxZ == smallestZ.getPrevious().getGapZ()) {
                            smallestZ.getPrevious().setGapX(smallestZ.getPrevious().getGapX() + checkedBoxX);
                        } else if (smallestZ.getGapZ() + checkedBoxZ == smallestZ.getNext().getGapZ()) {
                            newPositionX = smallestZ.getGapX() - checkedBoxX;
                            smallestZ.setGapX(smallestZ.getGapX() - checkedBoxX);
                        } else {
                            smallestZ.getPrevious().setNext(new ScrapPad(smallestZ.getPrevious(), smallestZ));
                            smallestZ.setPrevious(smallestZ.getPrevious().getNext());
                            smallestZ.getPrevious().updateGaps(
                                    smallestZ.getPrevious().getPrevious().getGapX() + checkedBoxX,
                                    smallestZ.getGapZ() + checkedBoxZ);
                        }
                    }
                    break;
                }
            }

            itemsToPack[checkedBoxIndex].setPosition(newPositionX, newPositionY, newPositionZ);
            volumeCheck();
        }
    }

    /**
     * Finds a proper layer thickness by looking at the unpacked boxes and the remaining container space.
     *
     * @param thickness the layer thickness.
     */
    private void findLayer(double thickness) {
        long examinedDimension, dimdif, dimension2, dimension3;
        double layereval, eval = 1000000;
        layerThickness = 0;
        for (int x = 0; x < itemsToPack.length; x++) {
            if (itemsToPack[x].isPacked()) {
                continue;
            }

            int rotations;
            switch (itemsToPack[x].getRotation()) {
                case FULL: rotations = 3; break;
                case HORIZONTAL:
                case NONE:
                default: rotations = 1; break;
            }
            for (int y = 1; y <= rotations; y++) {
                switch (y) {
                    case 2:
                        examinedDimension = itemsToPack[x].getDimension2();
                        dimension2 = itemsToPack[x].getDimension1();
                        dimension3 = itemsToPack[x].getDimension3();
                        break;
                    case 3:
                        examinedDimension = itemsToPack[x].getDimension3();
                        dimension2 = itemsToPack[x].getDimension1();
                        dimension3 = itemsToPack[x].getDimension2();
                        break;
                    default:
                        examinedDimension = itemsToPack[x].getDimension1();
                        dimension2 = itemsToPack[x].getDimension2();
                        dimension3 = itemsToPack[x].getDimension3();
                        break;
                }
                layereval = 0;
                if ((examinedDimension <= thickness) && (((dimension2 <= containerX) && (dimension3 <= containerZ)) ||
                    ((dimension3 <= containerX) && (dimension2 <= containerZ)))) {
                    for (int z = 0; z < itemsToPack.length; z++) {
                        if (itemsToPack[z].isPacked()) {
                            continue;
                        }
                        if (x != z) {
                            Item otherItem = itemsToPack[z];
                            dimdif = Math.abs(examinedDimension - otherItem.getDimension1());
                            if (Math.abs(examinedDimension - otherItem.getDimension2()) < dimdif) {
                                dimdif = Math.abs(examinedDimension - otherItem.getDimension2());
                            }
                            if (Math.abs(examinedDimension - otherItem.getDimension3()) < dimdif) {
                                dimdif = Math.abs(examinedDimension - otherItem.getDimension3());
                            }
                            layereval += dimdif;
                        }
                    }
                    if (layereval < eval) {
                        eval = layereval;
                        layerThickness = examinedDimension;
                    }
                }
            }
        }
        if (layerThickness == 0 || layerThickness > maxAvailableThickness) {
            packing = false;
        }
    }

    /**
     * Finds the most proper boxes by looking at the available orientations, empty space given, adjacent boxes
     * and container limits.
     *
     * @param maxGapX          the container x-dimension.
     * @param currentThickness the current layer thickness.
     * @param maxGapY          the container y-dimension.
     * @param currentGapZ      the remaining z-dimension gap.
     * @param maxGapZ          the container z-dimension.
     */
    private void findBox(long maxGapX, long currentThickness, long maxGapY, long currentGapZ, long maxGapZ) {
        boxFittingX = MAX_LENGTH;
        boxFittingY = MAX_LENGTH;
        boxFittingZ = MAX_LENGTH;
        boxNotFittingX = MAX_LENGTH;
        boxNotFittingY = MAX_LENGTH;
        botNotFittingZ = MAX_LENGTH;
        boxFittingIndex = -1;
        boxNotfittingIndex = -1;
        for (int y = 0; y < itemsToPack.length; y += itemsToPack[y].getQuantity()) {
            int index;
            for (index = y; index < index + (itemsToPack[y].getQuantity() - 1); index++) {
                if (!itemsToPack[index].isPacked()) {
                    break;
                }
            }
            if (itemsToPack[index].isPacked()) {
                continue;
            }
            analyzeBox(index, maxGapX, currentThickness, maxGapY, currentGapZ, maxGapZ, itemsToPack[index].getDimension1(), itemsToPack[index].getDimension2(), itemsToPack[index].getDimension3());
            // cubes need to be analyzed only one time
            if (itemsToPack[index].isCubic()) {
                continue;
            }
            if (ItemRotation.FULL.equals(itemsToPack[index].getRotation())) {
                analyzeBox(index, maxGapX, currentThickness, maxGapY, currentGapZ, maxGapZ, itemsToPack[index].getDimension1(), itemsToPack[index].getDimension3(), itemsToPack[index].getDimension2());
                analyzeBox(index, maxGapX, currentThickness, maxGapY, currentGapZ, maxGapZ, itemsToPack[index].getDimension2(), itemsToPack[index].getDimension1(), itemsToPack[index].getDimension3());
                analyzeBox(index, maxGapX, currentThickness, maxGapY, currentGapZ, maxGapZ, itemsToPack[index].getDimension2(), itemsToPack[index].getDimension3(), itemsToPack[index].getDimension1());
                analyzeBox(index, maxGapX, currentThickness, maxGapY, currentGapZ, maxGapZ, itemsToPack[index].getDimension3(), itemsToPack[index].getDimension1(), itemsToPack[index].getDimension2());
            }
            if (ItemRotation.FULL.equals(itemsToPack[index].getRotation()) || ItemRotation.HORIZONTAL.equals(itemsToPack[index].getRotation())) {
                analyzeBox(index, maxGapX, currentThickness, maxGapY, currentGapZ, maxGapZ, itemsToPack[index].getDimension3(), itemsToPack[index].getDimension2(), itemsToPack[index].getDimension1());
            }
        }
    }

    /**
     * Verifies if the given item dimensions fit in the remaining container space;<br>
     * if true, update the remaining space and item dimensions;<br>
     * if false, reserve the found values for a possible greater layer thickness.<br>
     *
     * @param maxGapX     the container x-dimension.
     * @param currentGapY the current layer thickness.
     * @param maxGapY     the container y-dimension.
     * @param currentGapZ the currently used z-dimension.
     * @param maxGapZ     the container z-dimension.
     * @param dimension1  first item dimension.
     * @param dimension2  second item dimension.
     * @param dimension3  third item dimension.
     */
    private void analyzeBox(int index, long maxGapX, long currentGapY, long maxGapY, long currentGapZ, long maxGapZ, long dimension1, long dimension2, long dimension3) {
        if (dimension1 <= maxGapX && dimension2 <= maxGapY && dimension3 <= maxGapZ) {
            if (dimension2 <= currentGapY) {
                if ((currentGapY - dimension2 < boxFittingY) ||
                        (currentGapY - dimension2 == boxFittingY && maxGapX - dimension1 < boxFittingX) ||
                        (currentGapY - dimension2 == boxFittingY && maxGapX - dimension1 == boxFittingX && Math.abs(currentGapZ - dimension3) < boxFittingZ)) {
                    boxX = dimension1;
                    boxY = dimension2;
                    boxZ = dimension3;
                    boxFittingX = maxGapX - dimension1;
                    boxFittingY = currentGapY - dimension2;
                    boxFittingZ = Math.abs(currentGapZ - dimension3);
                    boxFittingIndex = index;
                }
            } else {
                if ((dimension2 - currentGapY < boxNotFittingY) ||
                        (dimension2 - currentGapY == boxNotFittingY && maxGapX - dimension1 < boxNotFittingX) ||
                        (dimension2 - currentGapY == boxNotFittingY && maxGapX - dimension1 == boxNotFittingX && Math.abs(currentGapZ - dimension3) < botNotFittingZ)) {
                    bboxx = dimension1;
                    bboxy = dimension2;
                    bboxz = dimension3;
                    boxNotFittingX = maxGapX - dimension1;
                    boxNotFittingY = dimension2 - currentGapY;
                    botNotFittingZ = Math.abs(currentGapZ - dimension3);
                    boxNotfittingIndex = index;
                }
            }
        }
    }

    /**
     * Finds the item with the smallest z-dimension gap.
     */
    private ScrapPad findSmallestZ() {
        ScrapPad temp = scrapFirst;
        ScrapPad smallest = temp;
        while (temp.getNext() != null) {
            if (temp.getNext().getGapZ() < smallest.getGapZ()) {
                smallest = temp.getNext();
            }
            temp = temp.getNext();
        }
        return smallest;
    }

    /**
     * After finding each box, evaluate the candidate boxes and the current layer.
     * Verifies these rules:<br>
     * - if a box fitting in the current layer thickness has been found, keep its index and orientation for packing.<br>
     * - if a box with a y-dimension greater than the current layer thickness has been found and the edge of the current layer is evenedLayer, reserve the box for another layer.<br>
     * - if there is no gap in the edge of the current layer, the packing of this layer is done.<br>
     * - if there is not fitting box to the current layer gap, skip this gap and even it by arranging the already packed items.
     */
    private void checkFound(ScrapPad smallestZ) {
        evenedLayer = false;

        if (boxFittingIndex > -1) {
            checkedBoxIndex = boxFittingIndex;
            checkedBoxX = boxX;
            checkedBoxY = boxY;
            checkedBoxZ = boxZ;
        } else {
            if ((boxNotfittingIndex > -1) && (layerInLayer != 0 || smallestZ.noBoxes())) {
                if (layerInLayer == 0) {
                    preLayer = layerThickness;
                    layerInLayerZ = smallestZ.getGapZ();
                }
                checkedBoxIndex = boxNotfittingIndex;
                checkedBoxX = bboxx;
                checkedBoxY = bboxy;
                checkedBoxZ = bboxz;
                layerInLayer = layerInLayer + bboxy - layerThickness;
                layerThickness = bboxy;
            } else {
                evenedLayer = true;
                switch (smallestZ.isSituation()) {
                    case EMPTY:  {
                        evenedLayer = false;
                        layerDone = true;
                        break;
                    }
                    case ONLY_RIGHT_BOX: {
                        smallestZ.updateGaps(smallestZ.getNext().getGapX(), smallestZ.getNext().getGapZ());
                        smallestZ.setNext(smallestZ.getNext().getNext());
                        if (smallestZ.getNext() != null) {
                            smallestZ.getNext().setPrevious(smallestZ);
                        }
                        break;
                    }
                    case ONLY_LEFT_BOX: {
                        smallestZ.getPrevious().setNext(null);
                        smallestZ.getPrevious().setGapX(smallestZ.getGapX());
                        break;
                    }
                    case EQUAL_SIDES: {
                        smallestZ.getPrevious().setNext(smallestZ.getNext().getNext());
                        if (smallestZ.getNext().getNext() != null) {
                            smallestZ.getNext().getNext().setPrevious(smallestZ.getPrevious());
                        }
                        smallestZ.getPrevious().setGapX(smallestZ.getNext().getGapX());
                        break;
                    }
                    default: {
                        smallestZ.getPrevious().setNext(smallestZ.getNext());
                        smallestZ.getNext().setPrevious(smallestZ.getPrevious());
                        if (smallestZ.getPrevious().getGapZ() < smallestZ.getNext().getGapZ()) {
                            smallestZ.getPrevious().setGapX(smallestZ.getGapX());
                        }
                        break;
                    }
                }
            }
        }
    }

    /**
     * Defines the last evaluated item as packed and verifies if the packing is complete.
     */
    private void volumeCheck() {
        itemsToPack[checkedBoxIndex].packAtOrientation(checkedBoxX, checkedBoxY, checkedBoxZ);
        packedVolume += itemsToPack[checkedBoxIndex].getVolume();
        packedItemCounter++;

        if (packedVolume == totalContainerVolume || packedVolume == totalItemVolume) {
            packing = false;
            hundredPercentPacked = true;
        }
    }

    private Solution report(Container container) {
        switch (bestVariant) {
            case 2:
                containerX = container.getDimension3();
                containerY = container.getDimension2();
                containerZ = container.getDimension1();
                break;
            case 3:
                containerX = container.getDimension3();
                containerY = container.getDimension1();
                containerZ = container.getDimension2();
                break;
            case 4:
                containerX = container.getDimension2();
                containerY = container.getDimension1();
                containerZ = container.getDimension3();
                break;
            case 5:
                containerX = container.getDimension1();
                containerY = container.getDimension3();
                containerZ = container.getDimension2();
                break;
            case 6:
                containerX = container.getDimension2();
                containerY = container.getDimension3();
                containerZ = container.getDimension1();
                break;
            default:
                containerX = container.getDimension1();
                containerY = container.getDimension2();
                containerZ = container.getDimension3();
                break;
        }

        double percentagepackedbox = bestVolume * 100 / totalItemVolume;
        percentageUsed = bestVolume * 100 / totalContainerVolume;
        long prepackedy;
        long preremainpy;
        long packedy;

        listCandidateLayers();
        Collections.sort(layers);
        packedVolume = 0.0;
        packedy = 0;
        packing = true;
        layerThickness = layers.get(bestIteration).getDimension();
        maxAvailableThickness = containerY;
        remainpz = containerZ;

        for (Item item : itemsToPack) {
            item.reset();
        }

        do {
            layerInLayer = 0;
            layerDone = false;
            packLayer(packedy);
            packedy += layerThickness;
            maxAvailableThickness = containerY - packedy;
            if (layerInLayer != 0) {
                prepackedy = packedy;
                preremainpy = maxAvailableThickness;
                maxAvailableThickness = layerThickness - preLayer;
                packedy = packedy - layerThickness + preLayer;
                remainpz = layerInLayerZ;
                layerThickness = layerInLayer;
                layerDone = false;
                packLayer(packedy);
                packedy = prepackedy;
                maxAvailableThickness = preremainpy;
                remainpz = containerZ;
            }
            findLayer(maxAvailableThickness);
        } while (packing);

        Solution solution = new Solution(
                new ArrayList<>(inputItems),
                Arrays.asList(itemsToPack),
                new Container(container.getId(), containerX, containerY, containerZ, container.getRotation()),
                bestVolume);

        return solution;
    }
}
