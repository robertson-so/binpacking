package dev.rsoliveira.tools.binpacking;

import dev.rsoliveira.tools.binpacking.domain.Item;
import dev.rsoliveira.tools.binpacking.domain.ItemRotation;

import java.util.ArrayList;
import java.util.List;

public class Set4Test extends SetAbstractTest {

    public Set4Test() {
        List<Item> items = new ArrayList<>();

        // set4: 1493 items; 21 types; 96.4% full
        items.add(new Item(1, "1", 1, 2, 3, 200, ItemRotation.FULL));
        items.add(new Item(2, "2", 2, 4, 5, 200, ItemRotation.FULL));
        items.add(new Item(3, "3", 6, 7, 1, 200, ItemRotation.FULL));
        items.add(new Item(4, "4", 6, 8, 2, 29, ItemRotation.FULL));
        items.add(new Item(5, "5", 11, 2, 3, 29, ItemRotation.FULL));
        items.add(new Item(6, "6", 9, 4, 2, 29, ItemRotation.FULL));
        items.add(new Item(7, "7", 14, 5, 3, 30, ItemRotation.FULL));
        items.add(new Item(8, "8", 10, 4, 6, 30, ItemRotation.FULL));
        items.add(new Item(9, "9", 11, 8, 3, 30, ItemRotation.FULL));
        items.add(new Item(10, "10", 1, 2, 19, 50, ItemRotation.FULL));
        items.add(new Item(11, "11", 8, 13, 11, 50, ItemRotation.FULL));
        items.add(new Item(12, "12", 1, 3, 21, 10, ItemRotation.FULL));
        items.add(new Item(13, "13", 8, 9, 10, 30, ItemRotation.FULL));
        items.add(new Item(14, "14", 7, 13, 31, 115, ItemRotation.FULL));
        items.add(new Item(15, "15", 12, 66, 3, 30, ItemRotation.FULL));
        items.add(new Item(16, "16", 4, 15, 19, 90, ItemRotation.FULL));
        items.add(new Item(17, "17", 5, 16, 9, 100, ItemRotation.FULL));
        items.add(new Item(18, "18", 10, 2, 5, 100, ItemRotation.FULL));
        items.add(new Item(19, "19", 10, 10, 1, 90, ItemRotation.FULL));
        items.add(new Item(20, "20", 9, 18, 15, 50, ItemRotation.FULL));
        items.add(new Item(21, "21", 6, 9, 14, 1, ItemRotation.FULL));

        this.setItems(items);
        this.setContainerVolumeUsed(96.38);
    }
}
