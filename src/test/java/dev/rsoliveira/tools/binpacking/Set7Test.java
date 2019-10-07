package dev.rsoliveira.tools.binpacking;

import dev.rsoliveira.tools.binpacking.domain.Item;
import dev.rsoliveira.tools.binpacking.domain.ItemRotation;

import java.util.ArrayList;
import java.util.List;

public class Set7Test extends SetAbstractTest {

    public Set7Test() {
        List<Item> items = new ArrayList<>();

        items.add(new Item(1, "1", 70, 104, 24, 4, ItemRotation.FULL));
        items.add(new Item(2, "2", 70, 104, 24, 4, ItemRotation.FULL));
        items.add(new Item(3, "3", 14, 104, 48, 2, ItemRotation.FULL));

        this.setItems(items);
        this.setContainerVolumeUsed(100d);
    }
}
