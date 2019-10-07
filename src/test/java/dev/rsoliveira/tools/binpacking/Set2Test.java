package dev.rsoliveira.tools.binpacking;

import dev.rsoliveira.tools.binpacking.domain.Item;
import dev.rsoliveira.tools.binpacking.domain.ItemRotation;

import java.util.ArrayList;
import java.util.List;

public class Set2Test extends SetAbstractTest {

    public Set2Test() {
        List<Item> items = new ArrayList<>();

        items.add(new Item(1, "1", 3, 5, 7, 200, ItemRotation.FULL));
        items.add(new Item(2, "2", 9, 11, 2, 290, ItemRotation.FULL));
        items.add(new Item(3, "3", 14, 6, 8, 300, ItemRotation.FULL));
        items.add(new Item(4, "4", 1, 4, 19, 748, ItemRotation.FULL));
        items.add(new Item(5, "5", 10, 13, 21, 190, ItemRotation.FULL));

        this.setItems(items);
        this.setContainerVolumeUsed(97.45);
    }
}
