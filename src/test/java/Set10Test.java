import dev.rsoliveira.tools.binpacking.domain.Container;
import dev.rsoliveira.tools.binpacking.domain.Item;
import dev.rsoliveira.tools.binpacking.domain.ItemRotation;
import dev.rsoliveira.tools.binpacking.domain.Solution;
import dev.rsoliveira.tools.binpacking.simulation.AirForceBinPacking;
import dev.rsoliveira.tools.binpacking.simulation.ISimulation;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

public class Set10Test extends TestCase {

    public void testSet10() {
        Container container = new Container(1, 104, 96, 84, ItemRotation.FULL);
        List<Item> items = new ArrayList<>();
        // set1: 18 items; 5 types; 89.5% full
        items.add(new Item(1, "1", 28, 32, 18, 9, ItemRotation.FULL));
        items.add(new Item(2, "2", 24, 21, 35, 16, ItemRotation.FULL));
        items.add(new Item(3, "3", 19, 26, 20, 4, ItemRotation.FULL));
        items.add(new Item(4, "4", 19, 26, 16, 16, ItemRotation.FULL));
        items.add(new Item(5, "5", 16, 26, 20, 4, ItemRotation.FULL));
        items.add(new Item(6, "6", 20, 20, 26, 1, ItemRotation.FULL));
        items.add(new Item(7, "7", 16, 14, 25, 36, ItemRotation.FULL));

        List<Solution> solutions = new ArrayList<>();
        ISimulation<Container, Item> simulator = new AirForceBinPacking();
        Solution solution = simulator.simulate(container, items);
        solutions.add(solution);
        while (solution.remainingItems.size() > 0) {
            solution = simulator.simulate(container, solution.remainingItems);
            solutions.add(solution);
        }

        assertEquals(2, solutions.size());
        assertEquals(91.58f, solutions.get(0).percentageContainerVolumeUsed, 0.01);
    }
}
