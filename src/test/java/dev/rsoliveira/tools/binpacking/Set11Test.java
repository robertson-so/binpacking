package dev.rsoliveira.tools.binpacking;

import dev.rsoliveira.tools.binpacking.domain.Item;
import dev.rsoliveira.tools.binpacking.domain.ItemRotation;

import java.util.ArrayList;
import java.util.List;

public class Set11Test extends SetAbstractTest {

    public Set11Test() {
        List<Item> items = new ArrayList<>();

        items.add(new Item(1, "1", 19, 20, 42, 2, ItemRotation.FULL));
        items.add(new Item(2, "2", 25, 20, 30, 1, ItemRotation.FULL));
        items.add(new Item(3, "3", 25, 20, 25, 1, ItemRotation.FULL));
        items.add(new Item(4, "4", 25, 20, 29, 1, ItemRotation.FULL));
        items.add(new Item(5, "5", 8, 20, 21, 4, ItemRotation.FULL));
        items.add(new Item(6, "6", 36, 46, 84, 1, ItemRotation.FULL));
        items.add(new Item(7, "7", 16, 46, 10, 2, ItemRotation.FULL));
        items.add(new Item(8, "8", 16, 46, 32, 2, ItemRotation.FULL));
        items.add(new Item(9, "9", 20, 30, 15, 1, ItemRotation.FULL));
        items.add(new Item(10, "10", 20, 30, 69, 1, ItemRotation.FULL));
        items.add(new Item(11, "11", 20, 30, 21, 4, ItemRotation.FULL));
        items.add(new Item(12, "12", 12, 30, 7, 12, ItemRotation.FULL));
        items.add(new Item(13, "13", 52, 60, 42, 2, ItemRotation.FULL));
        items.add(new Item(14, "14", 26, 36, 21, 4, ItemRotation.FULL));
        items.add(new Item(15, "15", 26, 36, 84, 1, ItemRotation.FULL));

        this.setItems(items);
        this.setContainerVolumeUsed(84.47);
    }
}
