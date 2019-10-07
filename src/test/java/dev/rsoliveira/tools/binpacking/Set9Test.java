package dev.rsoliveira.tools.binpacking;

import dev.rsoliveira.tools.binpacking.domain.Item;
import dev.rsoliveira.tools.binpacking.domain.ItemRotation;

import java.util.ArrayList;
import java.util.List;

public class Set9Test extends SetAbstractTest {

    public Set9Test() {
        List<Item> items = new ArrayList<>();

        items.add(new Item(1, "1", 70, 45, 24, 4, ItemRotation.FULL));
        items.add(new Item(2, "2", 70, 30, 24, 4, ItemRotation.FULL));
        items.add(new Item(3, "3", 70, 29, 24, 4, ItemRotation.FULL));
        items.add(new Item(4, "4", 14, 40, 48, 2, ItemRotation.FULL));
        items.add(new Item(5, "5", 14, 32, 48, 2, ItemRotation.FULL));
        items.add(new Item(6, "6", 14, 32, 48, 2, ItemRotation.FULL));

        this.setItems(items);
        this.setContainerVolumeUsed(89.74);
    }
}
