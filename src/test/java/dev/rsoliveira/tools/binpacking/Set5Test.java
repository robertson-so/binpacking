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

public class Set5Test extends TestCase {

    public void testSet5() {
        Container container = new Container(1, 104, 96, 84, ItemRotation.FULL);
        List<Item> items = new ArrayList<>();

        // set5: 31 items; 31 types; 68.7% full
        items.add(new Item(1, "1", 1, 2, 3, 1, ItemRotation.FULL));
        items.add(new Item(2, "2", 4, 5, 6, 1, ItemRotation.FULL));
        items.add(new Item(3, "3", 7, 8, 9, 1, ItemRotation.FULL));
        items.add(new Item(4, "4", 10, 11, 12, 1, ItemRotation.FULL));
        items.add(new Item(5, "5", 13, 14, 15, 1, ItemRotation.FULL));
        items.add(new Item(6, "6", 16, 17, 18, 1, ItemRotation.FULL));
        items.add(new Item(7, "7", 19, 20, 21, 1, ItemRotation.FULL));
        items.add(new Item(8, "8", 22, 23, 24, 1, ItemRotation.FULL));
        items.add(new Item(9, "9", 25, 26, 27, 1, ItemRotation.FULL));
        items.add(new Item(10, "10", 28, 29, 30, 1, ItemRotation.FULL));
        items.add(new Item(11, "11", 31, 32, 33, 1, ItemRotation.FULL));
        items.add(new Item(12, "12", 34, 35, 36, 1, ItemRotation.FULL));
        items.add(new Item(13, "13", 37, 38, 39, 1, ItemRotation.FULL));
        items.add(new Item(14, "14", 40, 41, 42, 1, ItemRotation.FULL));
        items.add(new Item(15, "15", 43, 44, 45, 1, ItemRotation.FULL));
        items.add(new Item(16, "16", 46, 47, 48, 1, ItemRotation.FULL));
        items.add(new Item(17, "17", 2, 3, 4, 1, ItemRotation.FULL));
        items.add(new Item(18, "18", 5, 6, 7, 1, ItemRotation.FULL));
        items.add(new Item(19, "19", 8, 9, 10, 1, ItemRotation.FULL));
        items.add(new Item(20, "20", 11, 12, 13, 1, ItemRotation.FULL));
        items.add(new Item(21, "21", 14, 15, 16, 1, ItemRotation.FULL));
        items.add(new Item(22, "22", 17, 18, 19, 1, ItemRotation.FULL));
        items.add(new Item(23, "23", 20, 21, 22, 1, ItemRotation.FULL));
        items.add(new Item(24, "24", 23, 24, 25, 1, ItemRotation.FULL));
        items.add(new Item(25, "25", 26, 27, 28, 1, ItemRotation.FULL));
        items.add(new Item(26, "26", 29, 30, 31, 1, ItemRotation.FULL));
        items.add(new Item(27, "27", 32, 33, 34, 1, ItemRotation.FULL));
        items.add(new Item(28, "28", 35, 36, 37, 1, ItemRotation.FULL));
        items.add(new Item(29, "29", 38, 39, 40, 1, ItemRotation.FULL));
        items.add(new Item(30, "30", 41, 42, 43, 1, ItemRotation.FULL));
        items.add(new Item(31, "31", 44, 45, 46, 1, ItemRotation.FULL));

        List<Solution> solutions = new ArrayList<>();
        ISimulation<Container, Item> simulator = new AirForceBinPacking();
        Solution solution = simulator.simulate(container, items);
        solutions.add(solution);
        while (solution.remainingItems.size() > 0) {
            solution = simulator.simulate(container, solution.remainingItems);
            solutions.add(solution);
        }

        assertEquals(2, solutions.size());
        assertEquals(68.65f, solutions.get(0).percentageContainerVolumeUsed, 0.01);
    }
}
