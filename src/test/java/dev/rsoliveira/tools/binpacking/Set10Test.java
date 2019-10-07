package dev.rsoliveira.tools.binpacking;

import dev.rsoliveira.tools.binpacking.domain.Item;
import dev.rsoliveira.tools.binpacking.domain.ItemRotation;

import java.util.ArrayList;
import java.util.List;

public class Set10Test extends SetAbstractTest {

    public Set10Test() {
        List<Item> items = new ArrayList<>();

        items.add(new Item(1, "1", 28, 32, 18, 9, ItemRotation.FULL));
        items.add(new Item(2, "2", 24, 21, 35, 16, ItemRotation.FULL));
        items.add(new Item(3, "3", 19, 26, 20, 4, ItemRotation.FULL));
        items.add(new Item(4, "4", 19, 26, 16, 16, ItemRotation.FULL));
        items.add(new Item(5, "5", 16, 26, 20, 4, ItemRotation.FULL));
        items.add(new Item(6, "6", 20, 20, 26, 1, ItemRotation.FULL));
        items.add(new Item(7, "7", 16, 14, 25, 36, ItemRotation.FULL));

        this.setItems(items);
        this.setContainerVolumeUsed(91.58);
    }
}
