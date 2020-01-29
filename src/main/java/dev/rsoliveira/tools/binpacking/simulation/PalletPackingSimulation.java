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
public class PalletPackingSimulation implements ISimulation<Container, Item> {

    /**
     * Simulates packing a list of items into a container.
     * @param container a container that will receive all the possible items.
     * @param volumes a list of items to pack into to the container.
     * @return a list of packed items, with orientation and direction for each item, and the container orientation, and
     * a list of unpacked items.
     */
    public Solution simulate(Container container, List<Item> volumes) {
        PalletPackingState state = new PalletPackingState(container, volumes);
        iterate(state);
        return report(state);
    }

    /**
     * Given a container, for each container orientation iterate all possible layers to fill it and find the best iteration.
     *
     * @param state the packing state.
     */
    private void iterate(PalletPackingState state) {
        long packedy;
        state.setBestVolume(0.0);

        int maxContainerOrientation;
        switch (state.getContainer().getRotation()) {
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
            Volume orientation = state.getContainer().atOrientation(containerOrientation);

            List<Layer> layers = listCandidateLayers(state, orientation);

            for (int layersindex = 1; layersindex < layers.size(); layersindex++) {
                packedy = 0;

                state.resetPackedItems();
                state.resetThickness(layers.get(layersindex).getDimension(), orientation.getDimension2(), orientation.getDimension3());

                iterationCore(state, packedy, orientation);

                state.validateBestState(containerOrientation, layersindex);

                if (state.isHundredPercentPacked()) {
                    break;
                }
            }
            if (state.isHundredPercentPacked()) {
                break;
            }
            if (state.getContainer().isCubic()) {
                containerOrientation = 6;
            }
        }
    }

    /**
     * Lists all possible layer heights, giving a weight value to each layer.
     */
    private List<Layer> listCandidateLayers(PalletPackingState state, Volume orientation) {
        long examinedDimension, dimension2, dimension3;
        double weight;

        List<Layer> layers = new ArrayList<>();
        layers.add(new Layer(-1, 0));

        for (Item item : state.getInputItems()) {
            int max;
            switch (item.getRotation()) {
                case FULL: max = 3; break;
                case HORIZONTAL: max = 2; break;
                default: max = 1; break;
            }
            for (int y = 1; y <= max; y++) {
                switch (y) {
                    case 2:
                        // face down = yz
                        examinedDimension = item.getDimension2();
                        dimension2 = item.getDimension1();
                        dimension3 = item.getDimension3();
                        break;
                    case 3:
                        // face down = zy
                        examinedDimension = item.getDimension3();
                        dimension2 = item.getDimension1();
                        dimension3 = item.getDimension2();
                        break;
                    default:
                        // face down = xz
                        examinedDimension = item.getDimension1();
                        dimension2 = item.getDimension2();
                        dimension3 = item.getDimension3();
                        break;
                }
                if ((examinedDimension > orientation.getDimension2()) ||
                    (((dimension2 > orientation.getDimension1()) || (dimension3 > orientation.getDimension3())) &&
                     ((dimension3 > orientation.getDimension1()) || (dimension2 > orientation.getDimension3())))) {
                    continue;
                }

                long finalExaminedDimension = examinedDimension;
                if (layers.stream().anyMatch(p -> p.getDimension() == finalExaminedDimension)) {
                    continue;
                }

                weight = 0;
                for (Item item2 : state.getInputItems()) {
                    if (item.getId() == item2.getId()) continue;

                    weight = getDimensionDiff(examinedDimension, weight, item2);
                }
                layers.add(new Layer(weight, examinedDimension));
            }
        }

        Collections.sort(layers);
        return layers;
    }

    private double getDimensionDiff(long examinedDimension, double weight, Item item) {
        long dimdif;
        dimdif = Math.abs(examinedDimension - item.getDimension1());
        if (Math.abs(examinedDimension - item.getDimension2()) < dimdif) {
            dimdif = Math.abs(examinedDimension - item.getDimension2());
        }
        if (Math.abs(examinedDimension - item.getDimension3()) < dimdif) {
            dimdif = Math.abs(examinedDimension - item.getDimension3());
        }
        weight += dimdif;
        return weight;
    }

    /**
     * Packes the boxes found and arranges all variables and records properly.
     */
    private void packLayer(PalletPackingState state, Volume orientation, long packedy) {
        long gapLengthX, gapLengthZ, maxGapZ;
        long newPositionX;
        ScrapPad smallestZ;

        if (state.getLayerThickness() == 0) {
            state.setPacking(false);
            return;
        }

        state.getScrapFirst().updateGaps(orientation.getDimension1(), 0);

        while (true) {
            smallestZ = findSmallestZ(state);
            maxGapZ = state.getRemainpz() - smallestZ.getGapZ();

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
                case ONLY_LEFT_BOX:
                case EQUAL_SIDES:
                default: {
                    gapLengthX = smallestZ.getGapX() - smallestZ.getPrevious().getGapX();
                    gapLengthZ = smallestZ.getPrevious().getGapZ() - smallestZ.getGapZ();
                    break;
                }
            }

            findBox(state, gapLengthX, gapLengthZ, maxGapZ);
            checkFound(state, smallestZ);

            if (state.isLayerDone()) {
                break;
            }
            if (state.isEvenedLayer()) {
                continue;
            }

            long newPositionZ = smallestZ.getGapZ();
            // calculating x-position for the item and updating the smallest z, based on the smallest-z
            switch (smallestZ.isSituation()) {
                case EMPTY: {
                    newPositionX = 0;

                    if (state.getCheckedBoxX() == smallestZ.getGapX()) {
                        smallestZ.incrementGapZ(state.getCheckedBoxZ());
                    } else {
                        smallestZ.setNext(new ScrapPad(smallestZ, null, smallestZ.getGapX(), smallestZ.getGapZ()));
                        smallestZ.updateGaps(state.getCheckedBoxX(), smallestZ.getGapZ() + state.getCheckedBoxZ());
                    }
                    break;
                }
                case ONLY_RIGHT_BOX: {
                    newPositionX = 0;

                    if (state.getCheckedBoxX() == smallestZ.getGapX()) {
                        if (smallestZ.getGapZ() + state.getCheckedBoxZ() == smallestZ.getNext().getGapZ()) {
                            smallestZ.updateGaps(smallestZ.getNext().getGapX(), smallestZ.getNext().getGapZ());
                            smallestZ.setNext(smallestZ.getNext().getNext());
                            if (smallestZ.getNext() != null) {
                                smallestZ.getNext().setPrevious(smallestZ);
                            }
                        } else {
                            smallestZ.incrementGapZ(state.getCheckedBoxZ());
                        }
                    } else {
                        newPositionX = smallestZ.getGapX() - state.getCheckedBoxX();
                        if (smallestZ.getGapZ() + state.getCheckedBoxZ() == smallestZ.getNext().getGapZ()) {
                            smallestZ.incrementGapX(-state.getCheckedBoxX());
                        } else {
                            smallestZ.getNext().setPrevious(new ScrapPad(smallestZ, smallestZ.getNext()));
                            smallestZ.setNext(smallestZ.getNext().getPrevious());
                            smallestZ.getNext().setGapX(smallestZ.getGapX());
                            smallestZ.setGapX(smallestZ.getGapX() - state.getCheckedBoxX());
                            smallestZ.getNext().setGapZ(smallestZ.getGapZ() + state.getCheckedBoxZ());
                        }
                    }
                    break;
                }
                case ONLY_LEFT_BOX: {
                    newPositionX = smallestZ.getPrevious().getGapX();

                    if (state.getCheckedBoxX() == smallestZ.getGapX() - smallestZ.getPrevious().getGapX()) {
                        if (smallestZ.getGapZ() + state.getCheckedBoxZ() == smallestZ.getPrevious().getGapZ()) {
                            smallestZ.getPrevious().setGapX(smallestZ.getGapX());
                            smallestZ.getPrevious().setNext(null);
                        } else {
                            smallestZ.incrementGapZ(state.getCheckedBoxZ());
                        }
                    } else {
                        if (smallestZ.getGapZ() + state.getCheckedBoxZ() == smallestZ.getPrevious().getGapZ()) {
                            smallestZ.getPrevious().incrementGapX(state.getCheckedBoxX());
                        } else {
                            smallestZ.getPrevious().setNext(new ScrapPad(smallestZ.getPrevious(), smallestZ));
                            smallestZ.setPrevious(smallestZ.getPrevious().getNext());
                            smallestZ.getPrevious().updateGaps(
                                    smallestZ.getPrevious().getPrevious().getGapX() + state.getCheckedBoxX(),
                                    smallestZ.getGapZ() + state.getCheckedBoxZ());
                        }
                    }
                    break;
                }
                case EQUAL_SIDES: {
                    newPositionX = smallestZ.getPrevious().getGapX();

                    if (state.getCheckedBoxX() == smallestZ.getGapX() - smallestZ.getPrevious().getGapX()) {
                        if (smallestZ.getGapZ() + state.getCheckedBoxZ() == smallestZ.getNext().getGapZ()) {
                            smallestZ.getPrevious().setGapX(smallestZ.getNext().getGapX());
                            if (smallestZ.getNext().getNext() != null) {
                                smallestZ.getPrevious().setNext(smallestZ.getNext().getNext());
                                smallestZ.getNext().getNext().setPrevious(smallestZ.getPrevious());
                            } else {
                                smallestZ.getPrevious().setNext(null);
                            }
                        } else {
                            smallestZ.incrementGapZ(state.getCheckedBoxZ());
                        }
                    } else if (smallestZ.getPrevious().getGapX() < orientation.getDimension1() - smallestZ.getGapX()) {
                        if (smallestZ.getGapZ() + state.getCheckedBoxZ() == smallestZ.getPrevious().getGapZ()) {
                            smallestZ.incrementGapX(-state.getCheckedBoxX());
                            newPositionX = smallestZ.getGapX() - state.getCheckedBoxX();
                        } else {
                            smallestZ.getPrevious().setNext(new ScrapPad(smallestZ.getPrevious(), smallestZ));
                            smallestZ.setPrevious(smallestZ.getPrevious().getNext());
                            smallestZ.getPrevious().updateGaps(
                                    smallestZ.getPrevious().getPrevious().getGapX() + state.getCheckedBoxX(),
                                    smallestZ.getGapZ() + state.getCheckedBoxZ());
                        }
                    } else {
                        if (smallestZ.getGapZ() + state.getCheckedBoxZ() == smallestZ.getPrevious().getGapZ()) {
                            smallestZ.getPrevious().setGapX(smallestZ.getPrevious().getGapX() + state.getCheckedBoxX());
                            newPositionX = smallestZ.getPrevious().getGapX();
                        } else {
                            newPositionX = smallestZ.getGapX() - state.getCheckedBoxX();
                            smallestZ.getNext().setPrevious(new ScrapPad(smallestZ, smallestZ.getNext()));
                            smallestZ.setNext(smallestZ.getNext().getPrevious());
                            smallestZ.getNext().updateGaps(
                                    smallestZ.getGapX(),
                                    smallestZ.getGapZ() + state.getCheckedBoxZ());
                            smallestZ.incrementGapX(-state.getCheckedBoxX());
                        }
                    }
                    break;
                }
                default: {
                    newPositionX = smallestZ.getPrevious().getGapX();

                    if (state.getCheckedBoxX() == smallestZ.getGapX() - smallestZ.getPrevious().getGapX()) {
                        if (smallestZ.getGapZ() + state.getCheckedBoxZ() == smallestZ.getPrevious().getGapZ()) {
                            smallestZ.getPrevious().setGapX(smallestZ.getGapX());
                            smallestZ.getPrevious().setNext(smallestZ.getNext());
                            smallestZ.getNext().setPrevious(smallestZ.getPrevious());
                        } else {
                            smallestZ.incrementGapZ(state.getCheckedBoxZ());
                        }
                    } else {
                        if (smallestZ.getGapZ() + state.getCheckedBoxZ() == smallestZ.getPrevious().getGapZ()) {
                            smallestZ.getPrevious().incrementGapX(state.getCheckedBoxX());
                        } else if (smallestZ.getGapZ() + state.getCheckedBoxZ() == smallestZ.getNext().getGapZ()) {
                            newPositionX = smallestZ.getGapX() - state.getCheckedBoxX();
                            smallestZ.incrementGapX(-state.getCheckedBoxX());
                        } else {
                            smallestZ.getPrevious().setNext(new ScrapPad(smallestZ.getPrevious(), smallestZ));
                            smallestZ.setPrevious(smallestZ.getPrevious().getNext());
                            smallestZ.getPrevious().updateGaps(
                                    smallestZ.getPrevious().getPrevious().getGapX() + state.getCheckedBoxX(),
                                    smallestZ.getGapZ() + state.getCheckedBoxZ());
                        }
                    }
                    break;
                }
            }

            state.getItemsToPack()[state.getCheckedBoxIndex()]
                    .setPosition(newPositionX, packedy, newPositionZ);
            state.getItemsToPack()[state.getCheckedBoxIndex()]
                    .packAtOrientation(state.getCheckedBoxX(), state.getCheckedBoxY(), state.getCheckedBoxZ());
            state.setPackedVolume(state.getPackedVolume() +
                    state.getItemsToPack()[state.getCheckedBoxIndex()].getVolume());

            if (state.getPackedVolume() == state.getTotalContainerVolume() || state.getPackedVolume() == state.getTotalItemVolume()) {
                state.setPacking(false);
                state.setHundredPercentPacked(true);
            }
        }
    }

    /**
     * Finds a proper layer thickness by looking at the unpacked boxes and the remaining container space.
     *
     * @param thickness the layer thickness.
     */
    private void findLayer(PalletPackingState state, Volume orientation, double thickness) {
        long examinedDimension, dimension2, dimension3;
        double layereval, eval = 1000000;
        state.setLayerThickness(0);
        for (int x = 0; x < state.getItemsToPack().length; x++) {
            if (state.getItemsToPack()[x].isPacked()) {
                continue;
            }

            int max;
            switch (state.getItemsToPack()[x].getRotation()) {
                case FULL: max = 3; break;
                case HORIZONTAL: max = 2; break;
                default: max = 1; break;
            }
            for (int y = 1; y <= max; y++) {
                switch (y) {
                    case 2:
                        examinedDimension = state.getItemsToPack()[x].getDimension2();
                        dimension2 = state.getItemsToPack()[x].getDimension1();
                        dimension3 = state.getItemsToPack()[x].getDimension3();
                        break;
                    case 3:
                        examinedDimension = state.getItemsToPack()[x].getDimension3();
                        dimension2 = state.getItemsToPack()[x].getDimension1();
                        dimension3 = state.getItemsToPack()[x].getDimension2();
                        break;
                    default:
                        examinedDimension = state.getItemsToPack()[x].getDimension1();
                        dimension2 = state.getItemsToPack()[x].getDimension2();
                        dimension3 = state.getItemsToPack()[x].getDimension3();
                        break;
                }
                layereval = 0;
                if ((examinedDimension <= thickness) && (((dimension2 <= orientation.getDimension1()) &&
                    (dimension3 <= orientation.getDimension3())) ||
                    ((dimension3 <= orientation.getDimension1()) && (dimension2 <= orientation.getDimension3())))) {
                    for (int z = 0; z < state.getItemsToPack().length; z++) {
                        if (state.getItemsToPack()[z].isPacked()) {
                            continue;
                        }
                        if (x != z) {
                            Item otherItem = state.getItemsToPack()[z];
                            layereval = getDimensionDiff(examinedDimension, layereval, otherItem);
                        }
                    }
                    if (layereval < eval) {
                        eval = layereval;
                        state.setLayerThickness(examinedDimension);
                    }
                }
            }
        }
        if (state.getLayerThickness() == 0 || state.getLayerThickness() > state.getMaxAvailableThickness()) {
            state.setPacking(false);
        }
    }

    /**
     * Finds the most proper boxes by looking at the available orientations, empty space given, adjacent boxes
     * and container limits.
     *
     * @param state            the packing state.
     * @param maxGapX          the container x-dimension.
     * @param currentGapZ      the remaining z-dimension gap.
     * @param maxGapZ          the container z-dimension.
     */
    private void findBox(PalletPackingState state, long maxGapX, long currentGapZ, long maxGapZ) {
        int MAX_LENGTH = 32767;
        state.setBoxFittingX(MAX_LENGTH);
        state.setBoxFittingY(MAX_LENGTH);
        state.setBoxFittingZ(MAX_LENGTH);
        state.setBoxNotFittingX(MAX_LENGTH);
        state.setBoxNotFittingY(MAX_LENGTH);
        state.setBotNotFittingZ(MAX_LENGTH);
        state.setBoxFittingIndex(-1);
        state.setBoxNotFittingIndex(-1);
        for (int y = 0; y < state.getItemsToPack().length; y += state.getItemsToPack()[y].getQuantity()) {
            int index;
            for (index = y; index < (index + state.getItemsToPack()[y].getQuantity()) - 1; index++) {
                if (index == state.getItemsToPack().length) return;
                if (!state.getItemsToPack()[index].isPacked()) {
                    break;
                }
            }
            if (state.getItemsToPack()[index].isPacked()) {
                continue;
            }

            int max;
            if (state.getItemsToPack()[index].isCubic()) {
                max = 1;
            } else {
                switch (state.getItemsToPack()[index].getRotation()) {
                    case FULL: max = 6; break;
                    case HORIZONTAL: max = 2; break;
                    default: max = 1; break;
                }
            }
            for (int i = 1; i <= max; i++) {
                analyzeBox(state, index, maxGapX, currentGapZ, maxGapZ, state.getItemsToPack()[index].atOrientation(i));
            }
        }
    }

    /**
     * Verifies if the given item dimensions fit in the remaining container space;<br>
     * if true, update the remaining space and item dimensions;<br>
     * if false, reserve the found values for a possible greater layer thickness.<br>
     *
     * @param state       the packing state.
     * @param index       the actual box index being evaluated.
     * @param maxGapX     the container x-dimension.
     * @param currentGapZ the currently used z-dimension.
     * @param maxGapZ     the container z-dimension.
     * @param orientation the item dimensions.
     */
    private void analyzeBox(PalletPackingState state, int index, long maxGapX, long currentGapZ, long maxGapZ, Volume orientation) {
        long dimension1 = orientation.getDimension1();
        long dimension2 = orientation.getDimension2();
        long dimension3 = orientation.getDimension3();
        if (dimension1 <= maxGapX && dimension2 <= state.getMaxAvailableThickness() && dimension3 <= maxGapZ) {
            if (dimension2 <= state.getLayerThickness()) {
                if ((state.getLayerThickness() - dimension2 < state.getBoxFittingY()) ||
                        (state.getLayerThickness() - dimension2 == state.getBoxFittingY() &&
                                maxGapX - dimension1 < state.getBoxFittingX()) ||
                        (state.getLayerThickness() - dimension2 == state.getBoxFittingY() &&
                                maxGapX - dimension1 == state.getBoxFittingX() &&
                                Math.abs(currentGapZ - dimension3) < state.getBoxFittingZ())) {
                    state.setBoxX(dimension1);
                    state.setBoxY(dimension2);
                    state.setBoxZ(dimension3);
                    state.setBoxFittingX(maxGapX - dimension1);
                    state.setBoxFittingY(state.getLayerThickness() - dimension2);
                    state.setBoxFittingZ(Math.abs(currentGapZ - dimension3));
                    state.setBoxFittingIndex(index);
                }
            } else {
                if ((dimension2 - state.getLayerThickness() < state.getBoxNotFittingY()) ||
                        (dimension2 - state.getLayerThickness() == state.getBoxNotFittingY() &&
                                maxGapX - dimension1 < state.getBoxNotFittingX()) ||
                        (dimension2 - state.getLayerThickness() == state.getBoxNotFittingY() &&
                                maxGapX - dimension1 == state.getBoxNotFittingX() &&
                                Math.abs(currentGapZ - dimension3) < state.getBotNotFittingZ())) {
                    state.setbBoxX(dimension1);
                    state.setbBoxY(dimension2);
                    state.setbBoxZ(dimension3);
                    state.setBoxNotFittingX(maxGapX - dimension1);
                    state.setBoxNotFittingY(dimension2 - state.getLayerThickness());
                    state.setBotNotFittingZ(Math.abs(currentGapZ - dimension3));
                    state.setBoxNotFittingIndex(index);
                }
            }
        }
    }

    /**
     * Finds the item with the smallest z-dimension gap.
     */
    private ScrapPad findSmallestZ(PalletPackingState state) {
        ScrapPad temp = state.getScrapFirst();
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
     * - if a box with a y-dimension greater than the current layer thickness has been found and the edge of the current
     * layer is evenedLayer, reserve the box for another layer.<br>
     * - if there is no gap in the edge of the current layer, the packing of this layer is done.<br>
     * - if there is not fitting box to the current layer gap, skip this gap and even it by arranging the already packed items.
     */
    private void checkFound(PalletPackingState state, ScrapPad smallestZ) {
        state.setEvenedLayer(false);

        if (state.getBoxFittingIndex() > -1) {
            state.setCheckedBoxIndex(state.getBoxFittingIndex());
            state.setCheckedBoxX(state.getBoxX());
            state.setCheckedBoxY(state.getBoxY());
            state.setCheckedBoxZ(state.getBoxZ());
        } else {
            if ((state.getBoxNotFittingIndex() > -1) &&
                (state.getLayerInLayer() != 0 || smallestZ.isSituation().equals(ScrapPad.Situation.EMPTY))) {
                if (state.getLayerInLayer() == 0) {
                    state.setPreLayer(state.getLayerThickness());
                    state.setLayerInLayerZ(smallestZ.getGapZ());
                }
                state.setCheckedBoxIndex(state.getBoxNotFittingIndex());
                state.setCheckedBoxX(state.getbBoxX());
                state.setCheckedBoxY(state.getbBoxY());
                state.setCheckedBoxZ(state.getbBoxZ());
                state.setLayerInLayer(state.getLayerInLayer() + state.getbBoxY() - state.getLayerThickness());
                state.setLayerThickness(state.getbBoxY());
            } else {
                state.setEvenedLayer(true);
                switch (smallestZ.isSituation()) {
                    case EMPTY:  {
                        state.setEvenedLayer(false);
                        state.setLayerDone(true);
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

    private Solution report(PalletPackingState state) {
        long packedy = 0;

        Volume orientation = state.getContainer().atOrientation(state.getBestVariant());
        List<Layer> layers = listCandidateLayers(state, orientation);

        state.setPackedVolume(0.0);
        state.setPacking(true);
        state.setLayerThickness(layers.get(state.getBestIteration()).getDimension());
        state.setMaxAvailableThickness(orientation.getDimension2());
        state.setRemainpz(orientation.getDimension3());

        for (Item item : state.getItemsToPack()) {
            item.reset();
        }

        iterationCore(state, packedy, orientation);

        return new Solution(
                new ArrayList<>(state.getInputItems()),
                Arrays.asList(state.getItemsToPack()),
                orientation,
                state.getBestVolume());
    }

    private void iterationCore(PalletPackingState state, long packedy, Volume orientation) {
        long prepackedy;
        long preremainpy;
        do {
            state.setLayerInLayer(0);
            state.setLayerDone(false);
            packLayer(state, orientation, packedy);
            packedy += state.getLayerThickness();
            state.setMaxAvailableThickness(orientation.getDimension2() - packedy);
            if (state.getLayerInLayer() != 0) {
                prepackedy = packedy;
                preremainpy = state.getMaxAvailableThickness();
                state.setMaxAvailableThickness(state.getLayerThickness() - state.getPreLayer());
                packedy = packedy - state.getLayerThickness() + state.getPreLayer();
                state.setRemainpz(state.getLayerInLayerZ());
                state.setLayerThickness(state.getLayerInLayer());
                state.setLayerDone(false);
                packLayer(state, orientation, packedy);
                packedy = prepackedy;
                state.setMaxAvailableThickness(preremainpy);
                state.setRemainpz(orientation.getDimension3());
            }
            findLayer(state, orientation, state.getMaxAvailableThickness());
        } while (state.isPacking());
    }
}
