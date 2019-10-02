import dev.rsoliveira.tools.binpacking.domain.Container;
import dev.rsoliveira.tools.binpacking.domain.Item;
import dev.rsoliveira.tools.binpacking.domain.ItemRotation;
import dev.rsoliveira.tools.binpacking.domain.Solution;
import dev.rsoliveira.tools.binpacking.simulation.AirForceBinPacking;
import dev.rsoliveira.tools.binpacking.simulation.ISimulation;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

public class Set2Test extends TestCase {

    public void testSet2() {
        Container container = new Container(1, 104, 96, 84, ItemRotation.FULL);
        List<Item> items = new ArrayList<>();
        // set2: 1728 items; 5 types; 97.5% full
        items.add(new Item(1, "1", 3, 5, 7, 200, ItemRotation.FULL));
        items.add(new Item(2, "2", 9, 11, 2, 290, ItemRotation.FULL));
        items.add(new Item(3, "3", 14, 6, 8, 300, ItemRotation.FULL));
        items.add(new Item(4, "4", 1, 4, 19, 748, ItemRotation.FULL));
        items.add(new Item(5, "5", 10, 13, 21, 190, ItemRotation.FULL));

        List<Solution> solutions = new ArrayList<>();
        ISimulation<Container, Item> simulator = new AirForceBinPacking();
        Solution solution = simulator.simulate(container, items);
        solutions.add(solution);
        while (solution.remainingItems.size() > 0) {
            solution = simulator.simulate(container, solution.remainingItems);
            solutions.add(solution);
        }

        assertEquals(2, solutions.size());
        assertEquals(97.45f, solutions.get(0).percentageContainerVolumeUsed, 0.01);
    }
}
