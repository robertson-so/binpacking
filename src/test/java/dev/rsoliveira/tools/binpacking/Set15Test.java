package dev.rsoliveira.tools.binpacking;

import dev.rsoliveira.tools.binpacking.domain.Item;
import dev.rsoliveira.tools.binpacking.domain.ItemRotation;

import java.util.ArrayList;
import java.util.List;

public class Set15Test extends SetAbstractTest {

    public Set15Test() {
        List<Item> items = new ArrayList<>();

        items.add(new Item(1, "1", 14, 13, 2, 576, ItemRotation.FULL));
        items.add(new Item(2, "2", 21, 13, 4, 576, ItemRotation.FULL));

        this.setItems(items);
        this.setContainerVolumeUsed(100.0);
    }
}
