package dev.rsoliveira.tools.binpacking;

import dev.rsoliveira.tools.binpacking.domain.Container;
import dev.rsoliveira.tools.binpacking.domain.Item;
import dev.rsoliveira.tools.binpacking.domain.ItemRotation;
import dev.rsoliveira.tools.binpacking.domain.Solution;
import dev.rsoliveira.tools.binpacking.simulation.AirForceBinPacking;
import dev.rsoliveira.tools.binpacking.simulation.ISimulation;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

public class Set3Test extends TestCase {

    public void testSet3() {
        Container container = new Container(1, 104, 96, 84, ItemRotation.FULL);
        List<Item> items = new ArrayList<>();

        // set3: 637 items; 11 types; 92.4% full
        items.add(new Item(1, "1", 3, 5, 7, 200, ItemRotation.FULL));
        items.add(new Item(2, "2", 9, 11, 2, 29, ItemRotation.FULL));
        items.add(new Item(3, "3", 14, 6, 8, 30, ItemRotation.FULL));
        items.add(new Item(4, "4", 1, 4, 19, 51, ItemRotation.FULL));
        items.add(new Item(5, "5", 10, 13, 21, 12, ItemRotation.FULL));
        items.add(new Item(6, "6", 27, 23, 34, 5, ItemRotation.FULL));
        items.add(new Item(7, "7", 12, 9, 13, 10, ItemRotation.FULL));
        items.add(new Item(8, "8", 24, 15, 19, 50, ItemRotation.FULL));
        items.add(new Item(9, "9", 5, 16, 9, 100, ItemRotation.FULL));
        items.add(new Item(10, "10", 10, 20, 5, 100, ItemRotation.FULL));
        items.add(new Item(11, "11", 9, 18, 15, 50, ItemRotation.FULL));

        List<Solution> solutions = new ArrayList<>();
        ISimulation<Container, Item> simulator = new AirForceBinPacking();
        Solution solution = simulator.simulate(container, items);
        solutions.add(solution);
        while (solution.remainingItems.size() > 0) {
            solution = simulator.simulate(container, solution.remainingItems);
            solutions.add(solution);
        }

        assertEquals(2, solutions.size());
        assertEquals(92.4f, solutions.get(0).percentageContainerVolumeUsed, 0.01);
    }
}
