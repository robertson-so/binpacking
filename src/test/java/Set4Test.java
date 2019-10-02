import dev.rsoliveira.tools.binpacking.domain.Container;
import dev.rsoliveira.tools.binpacking.domain.Item;
import dev.rsoliveira.tools.binpacking.domain.ItemRotation;
import dev.rsoliveira.tools.binpacking.domain.Solution;
import dev.rsoliveira.tools.binpacking.simulation.AirForceBinPacking;
import dev.rsoliveira.tools.binpacking.simulation.ISimulation;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

public class Set4Test extends TestCase {

    public void testSet4() {
        Container container = new Container(1, 104, 96, 84, ItemRotation.FULL);
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

        List<Solution> solutions = new ArrayList<>();
        ISimulation<Container, Item> simulator = new AirForceBinPacking();
        Solution solution = simulator.simulate(container, items);
        solutions.add(solution);
        while (solution.remainingItems.size() > 0) {
            solution = simulator.simulate(container, solution.remainingItems);
            solutions.add(solution);
        }

        assertEquals(2, solutions.size());
        assertEquals(96.38f, solutions.get(0).percentageContainerVolumeUsed, 0.01);
    }
}
