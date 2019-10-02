import dev.rsoliveira.tools.binpacking.domain.Container;
import dev.rsoliveira.tools.binpacking.domain.Item;
import dev.rsoliveira.tools.binpacking.domain.ItemRotation;
import dev.rsoliveira.tools.binpacking.domain.Solution;
import dev.rsoliveira.tools.binpacking.simulation.AirForceBinPacking;
import dev.rsoliveira.tools.binpacking.simulation.ISimulation;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

public class Set16Test extends TestCase {

    public void testSet16() {
        Container container = new Container(1, 104, 96, 84, ItemRotation.FULL);
        List<Item> items = new ArrayList<>();
        // set1: 18 items; 5 types; 89.5% full
        items.add(new Item(1, "1", 4, 6, 7, 2496, ItemRotation.FULL));
        items.add(new Item(2, "2", 14, 13, 8, 288, ItemRotation.FULL));

        List<Solution> solutions = new ArrayList<>();
        ISimulation<Container, Item> simulator = new AirForceBinPacking();
        Solution solution = simulator.simulate(container, items);
        solutions.add(solution);
        while (solution.remainingItems.size() > 0) {
            solution = simulator.simulate(container, solution.remainingItems);
            solutions.add(solution);
        }

        assertEquals(2, solutions.size());
        assertEquals(98.09f, solutions.get(0).percentageContainerVolumeUsed, 0.01);
    }
}
