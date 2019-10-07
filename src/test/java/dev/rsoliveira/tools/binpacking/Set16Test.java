package dev.rsoliveira.tools.binpacking;

import dev.rsoliveira.tools.binpacking.domain.Item;
import dev.rsoliveira.tools.binpacking.domain.ItemRotation;

import java.util.ArrayList;
import java.util.List;

public class Set16Test extends SetAbstractTest {

    public Set16Test() {
        List<Item> items = new ArrayList<>();

        items.add(new Item(1, "1", 4, 6, 7, 2496, ItemRotation.FULL));
        items.add(new Item(2, "2", 14, 13, 8, 288, ItemRotation.FULL));

        this.setItems(items);
        this.setContainerVolumeUsed(98.09);
    }
}
