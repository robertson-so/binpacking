package dev.rsoliveira.tools.binpacking;

import dev.rsoliveira.tools.binpacking.domain.Item;
import dev.rsoliveira.tools.binpacking.domain.ItemRotation;

import java.util.ArrayList;
import java.util.List;

public class Set14Test extends SetAbstractTest {

    public Set14Test() {
        List<Item> items = new ArrayList<>();

        items.add(new Item(1, "1", 4, 6, 7, 4992, ItemRotation.FULL));

        this.setItems(items);
        this.setContainerVolumeUsed(100.0);
    }
}
