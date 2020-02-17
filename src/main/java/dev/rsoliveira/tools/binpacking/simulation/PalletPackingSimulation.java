package dev.rsoliveira.tools.binpacking.simulation;

import dev.rsoliveira.tools.binpacking.domain.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * A 3D bin packing simulation that uses a human heuristic to perform the packing.
 * The heuristic simulates a {floor, wall} building; it packs items using left to right direction, creating layers
 * of items with compatible heights.
 * Both container and items can be rotated in horizontal axes only or in all directions, for better
 * space usage.
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
     * The execution finishes when, on a given iteration, all items are packed.
     *
     * @param state the packing state.
     */
    private void iterate(PalletPackingState state) {

        int maxContainerOrientation;
        switch (state.getContainer().getRotation()) {
            case FULL:
                maxContainerOrientation = 6;
                break;
            case HORIZONTAL:
                maxContainerOrientation = 2;
                break;
            case NONE:
            default:
                maxContainerOrientation = 1;
                break;
        }

        int packedy;
        state.setBestVolume(0.0);
        for (int containerOrientation = 1; containerOrientation <= maxContainerOrientation; containerOrientation++) {
            Volume orientation = state.getContainer().atOrientation(containerOrientation);

            List<Layer> layers = listCandidateLayers(state, orientation);

            for (int layersindex = 1; layersindex < layers.size(); layersindex++) {
                packedy = 0;

                state.restartPacking();
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
                case NONE:
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

        layers.sort(Comparator.comparing(Layer::getWeight));
        return layers;
    }

    /**
     * Increments a weight based on the source dimension and the item's lowest dimension.
     * @param examinedDimension the source dimension length.
     * @param weight the actual weight.
     * @param item the item dimensions to evaluate.
     * @return a layer weight.
     */
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
     * Packs the boxes found and arranges all variables and records properly.
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
                case DIFFERENT_SIDES:
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

            // the smallest gap inside the layer will be adapted, accordingly
            // to its items' neighborhood and the next item to be packed found earlier;
            // the next box will be packed:
            // - at most left position, if there is no other box occupying the x-z area;
            // - at left of an already occupied area, if there is enough space to do it;
            // - at right of an already occupied area, if there is enough space to do it;
            // - in an existing gap between two boxes.
            // the neighborhood for the gap then is updated, following the situations cited above.
            long newPositionZ = smallestZ.getGapZ();
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
                            newPositionX = smallestZ.getGapX();
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
                case DIFFERENT_SIDES:
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
        double layereval, eval = 100000000;
        int max;
        state.setLayerThickness(0);
        Item[] toPack = Arrays.stream(state.getItemsToPack()).filter(j -> !j.isPacked()).toArray(Item[]::new);
        for (int x = 0; x < toPack.length; x++) {
            switch (toPack[x].getRotation()) {
                case FULL: max = 3; break;
                case HORIZONTAL: max = 2; break;
                case NONE:
                default: max = 1; break;
            }
            for (int y = 1; y <= max; y++) {
                switch (y) {
                    case 2:
                        examinedDimension = toPack[x].getDimension2();
                        dimension2 = toPack[x].getDimension1();
                        dimension3 = toPack[x].getDimension3();
                        break;
                    case 3:
                        examinedDimension = toPack[x].getDimension3();
                        dimension2 = toPack[x].getDimension1();
                        dimension3 = toPack[x].getDimension2();
                        break;
                    default:
                        examinedDimension = toPack[x].getDimension1();
                        dimension2 = toPack[x].getDimension2();
                        dimension3 = toPack[x].getDimension3();
                        break;
                }
                layereval = 0;
                if ((examinedDimension <= thickness) && (((dimension2 <= orientation.getDimension1()) &&
                    (dimension3 <= orientation.getDimension3())) ||
                    ((dimension3 <= orientation.getDimension1()) && (dimension2 <= orientation.getDimension3())))) {
                    for (int z = 0; z < toPack.length; z++) {
                        if (x != z) {
                            Item otherItem = toPack[z];
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
        int MAX_LENGTH = 1048576;
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
                    case NONE:
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
        long diff1 = Math.abs(maxGapX - dimension1);
        long diff2 = Math.abs(state.getLayerThickness() - dimension2);
        long diff3 = Math.abs(currentGapZ - dimension3);
        if (dimension1 <= maxGapX && dimension2 <= state.getMaxAvailableThickness() && dimension3 <= maxGapZ) {
            if (dimension2 <= state.getLayerThickness()) {
                if ((diff2 < state.getBoxFittingY()) ||
                    (diff2 == state.getBoxFittingY() && diff1 < state.getBoxFittingX()) ||
                    (diff2 == state.getBoxFittingY() && diff1 == state.getBoxFittingX() && diff3 < state.getBoxFittingZ())) {
                    state.setBoxX(dimension1);
                    state.setBoxY(dimension2);
                    state.setBoxZ(dimension3);
                    state.setBoxFittingX(diff1);
                    state.setBoxFittingY(diff2);
                    state.setBoxFittingZ(diff3);
                    state.setBoxFittingIndex(index);
                }
            } else {
                if ((diff2 < state.getBoxNotFittingY()) ||
                    (diff2 == state.getBoxNotFittingY() && diff1 < state.getBoxNotFittingX()) ||
                    (diff2 == state.getBoxNotFittingY() && diff1 == state.getBoxNotFittingX() && diff3 < state.getBotNotFittingZ())) {
                    state.setbBoxX(dimension1);
                    state.setbBoxY(dimension2);
                    state.setbBoxZ(dimension3);
                    state.setBoxNotFittingX(diff1);
                    state.setBoxNotFittingY(diff2);
                    state.setBotNotFittingZ(diff3);
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
                    case DIFFERENT_SIDES:
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

        state.restartPacking();
        state.resetThickness(layers.get(state.getBestIteration()).getDimension(), orientation.getDimension2(), orientation.getDimension3());
        iterationCore(state, packedy, orientation);

        //boolean isValid = isSolutionValid(state);

        List<Item> toPack = Arrays.asList(state.getItemsToPack());
        toPack.sort(Comparator.comparing(Item::getPositionZ).thenComparing(Item::getPositionY).thenComparing(Item::getPositionX));

        return new Solution(
                new ArrayList<>(state.getInputItems()),
                toPack,
                orientation,
                state.getBestVolume());
    }

    /**
     * The packing flow execution. It's used while iterating the defined axes for both
     * container and items, and to recover the best packing solution.
     * @param state the packing state.
     * @param packedy how much of the container's y axis is used.
     * @param orientation the container orientation.
     */
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

    /**
     * AABB validation for the items. In this case, the objects' faces can overlap.
     * @param state a packing state.
     * @return true if there is a collision, false otherwise.
     */
    private boolean isSolutionValid(PalletPackingState state) {
        boolean hasCollision = false;

        for (Item item1 : state.getItemsToPack()) {
            for (Item item2 : state.getItemsToPack()) {
                if (item1.getId() != item2.getId()) {
                    // boxes' faces can touch, but cannot overlap
                    if ((item1.getMinPositionX() < item2.getMaxPositionX() && item1.getMaxPositionX() > item2.getMinPositionX()) &&
                        (item1.getMinPositionY() < item2.getMaxPositionY() && item1.getMaxPositionY() > item2.getMinPositionY()) &&
                        (item1.getMinPositionZ() < item2.getMaxPositionZ() && item1.getMaxPositionZ() > item2.getMinPositionZ())) {
                        hasCollision = true;
                        break;
                    }
                }
            }
            if (hasCollision) break;
        }

        return !hasCollision;
    }
}
