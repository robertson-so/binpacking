package dev.rsoliveira.tools.binpacking;

import dev.rsoliveira.tools.binpacking.domain.Container;
import dev.rsoliveira.tools.binpacking.domain.Item;
import dev.rsoliveira.tools.binpacking.domain.ItemRotation;
import dev.rsoliveira.tools.binpacking.domain.Solution;
import dev.rsoliveira.tools.binpacking.simulation.AirForceBinPacking;
import dev.rsoliveira.tools.binpacking.simulation.ISimulation;
import junit.framework.TestCase;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Set1Test extends TestCase {

    public void testSet1() {
        Container container = new Container(1, 104, 96, 84, ItemRotation.FULL);
        List<Item> items = new ArrayList<>();
        // set1: 307 items; 5 types; 89.5% full
        items.add(new Item(1, "1", 3, 5, 7, 51, ItemRotation.FULL));
        items.add(new Item(2, "2", 20, 4, 6, 90, ItemRotation.FULL));
        items.add(new Item(3, "3", 11, 21, 16, 80, ItemRotation.FULL));
        items.add(new Item(4, "4", 51, 2, 60, 80, ItemRotation.FULL));
        items.add(new Item(5, "5", 6, 17, 8, 6, ItemRotation.FULL));

        List<Solution> solutions = new ArrayList<>();
        ISimulation<Container, Item> simulator = new AirForceBinPacking();
        Solution solution = simulator.simulate(container, items);
        solutions.add(solution);
        while (solution.remainingItems.size() > 0) {
            solution = simulator.simulate(container, solution.remainingItems);
            solutions.add(solution);
        }

        assertEquals(2, solutions.size());
        assertEquals(89.48f, solutions.get(0).percentageContainerVolumeUsed, 0.01);
    }
}
