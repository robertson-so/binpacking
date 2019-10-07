package dev.rsoliveira.tools.binpacking;

import dev.rsoliveira.tools.binpacking.domain.Item;
import dev.rsoliveira.tools.binpacking.domain.ItemRotation;

import java.util.ArrayList;
import java.util.List;

public class Set1Test extends SetAbstractTest {

    public Set1Test() {
        List<Item> items = new ArrayList<>();

        items.add(new Item(1, "1", 3, 5, 7, 51, ItemRotation.FULL));
        items.add(new Item(2, "2", 20, 4, 6, 90, ItemRotation.FULL));
        items.add(new Item(3, "3", 11, 21, 16, 80, ItemRotation.FULL));
        items.add(new Item(4, "4", 51, 2, 60, 80, ItemRotation.FULL));
        items.add(new Item(5, "5", 6, 17, 8, 6, ItemRotation.FULL));

        this.setItems(items);
        this.setContainerVolumeUsed(89.48);
    }
}
