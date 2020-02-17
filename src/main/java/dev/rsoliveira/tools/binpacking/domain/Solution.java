package dev.rsoliveira.tools.binpacking.domain;

import java.util.ArrayList;
import java.util.List;

public class Solution {

    private double packedVolume;
    private double bestSolutionVolume;
    private double totalItemVolume;
    private double totalContainerVolume;
    private double percentageContainerVolumeUsed;
    private double percentagePackedItemsVolume;
    private boolean completePacking;

    private List<Item> packedItems;
    private List<Item> unpackedItems;
    private List<Item> inputItems;
    private List<Item> remainingItems;

    private Volume containerOrientation;

    public Solution(List<Item> inputItems, List<Item> resultItems, Volume containerOrientation, double bestVolume) {
        this.inputItems = inputItems;
        this.containerOrientation = containerOrientation;
        this.bestSolutionVolume = bestVolume;

        this.totalContainerVolume = containerOrientation.getVolume();
        this.percentageContainerVolumeUsed = bestVolume * 100f / this.totalContainerVolume;

        this.packedItems = new ArrayList<>();
        this.unpackedItems = new ArrayList<>();
        long itemVolume = 0;
        for (Item item : resultItems) {
            itemVolume += item.getVolume();
        }
        this.totalItemVolume = itemVolume;
        this.percentagePackedItemsVolume = bestVolume * 100f / this.totalItemVolume;

        for (Item item : resultItems) {
            if (item.isPacked()) {
                packedItems.add(item);
                this.packedVolume += item.getVolume();
            } else {
                unpackedItems.add(item);
            }
        }
        this.completePacking = this.packedItems.size() == resultItems.size();

        this.remainingItems = new ArrayList<>();
        for (Item item : inputItems) {
            this.remainingItems.add((Item) item.clone());
        }
        List<Item> toRemove = new ArrayList<>();
        for (Item item : remainingItems) {
            int cont = 0;
            for (Item packed : this.packedItems) {
                if (item.getCode().equals(packed.getCode())) {
                    cont++;
                }
            }
            if (item.getQuantity() == cont) {
                toRemove.add(item);
            }
            item.setQuantity(item.getQuantity() - cont);
        }
        this.remainingItems.removeAll(toRemove);
    }

    public double getBestSolutionVolume() {
        return bestSolutionVolume;
    }

    public double getTotalItemVolume() {
        return totalItemVolume;
    }

    public double getTotalContainerVolume() {
        return totalContainerVolume;
    }

    public List<Item> getPackedItems() {
        return packedItems;
    }

    public List<Item> getUnpackedItems() {
        return unpackedItems;
    }

    public List<Item> getInputItems() {
        return inputItems;
    }

    public Volume getContainerOrientation() {
        return containerOrientation;
    }

    public boolean isCompletePacking() {
        return completePacking;
    }

    public List<Item> getRemainingItems() {
        return remainingItems;
    }

    public double getPackedVolume() {
        return packedVolume;
    }

    public double getPercentageContainerVolumeUsed() {
        return percentageContainerVolumeUsed;
    }

    public double getPercentagePackedItemsVolume() {
        return percentagePackedItemsVolume;
    }
}
