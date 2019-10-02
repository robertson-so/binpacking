import dev.rsoliveira.tools.binpacking.domain.Container;
import dev.rsoliveira.tools.binpacking.domain.Item;
import dev.rsoliveira.tools.binpacking.domain.ItemRotation;
import dev.rsoliveira.tools.binpacking.domain.Solution;
import dev.rsoliveira.tools.binpacking.simulation.AirForceBinPacking;
import dev.rsoliveira.tools.binpacking.simulation.ISimulation;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

public class Set8Test extends TestCase {

    public void testSet8() {
        Container container = new Container(1, 104, 96, 84, ItemRotation.FULL);
        List<Item> items = new ArrayList<>();
        // set1: 12 items; 5 types; 89.5% full
        items.add(new Item(1, "1", 70, 45, 24, 4, ItemRotation.FULL));
        items.add(new Item(2, "2", 70, 59, 24, 4, ItemRotation.FULL));
        items.add(new Item(3, "3", 14, 40, 48, 2, ItemRotation.FULL));
        items.add(new Item(4, "4", 14, 64, 48, 2, ItemRotation.FULL));

        List<Solution> solutions = new ArrayList<>();
        ISimulation<Container, Item> simulator = new AirForceBinPacking();
        Solution solution = simulator.simulate(container, items);
        solutions.add(solution);
        while (solution.remainingItems.size() > 0) {
            solution = simulator.simulate(container, solution.remainingItems);
            solutions.add(solution);
        }

        assertEquals(1, solutions.size());
        assertEquals(100f, solutions.get(0).percentageContainerVolumeUsed, 0.01);
    }
}
