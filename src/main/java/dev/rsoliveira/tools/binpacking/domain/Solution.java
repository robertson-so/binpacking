package dev.rsoliveira.tools.binpacking.domain;

import java.util.List;

public class Solution {

  public double packedVolume;
  public double bestSolutionVolume;
  public double totalContainerVolume;
  public double totalBoxVolume;
  public double percentageContainerVolumeUsed;
  public double percentagePackedBox;

  public boolean completePacking;
  public List<Item> packedItems;
  public List<Item> unpackedItems;
  public List<Item> inputItems;
  public List<Item> remainingItems;

  public Container containerOrientation;

}
