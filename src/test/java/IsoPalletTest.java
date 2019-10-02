import dev.rsoliveira.tools.binpacking.domain.Container;
import dev.rsoliveira.tools.binpacking.domain.Item;
import dev.rsoliveira.tools.binpacking.domain.ItemRotation;
import dev.rsoliveira.tools.binpacking.domain.Solution;
import dev.rsoliveira.tools.binpacking.simulation.AirForceBinPacking;
import dev.rsoliveira.tools.binpacking.simulation.ISimulation;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

public class IsoPalletTest extends TestCase {

    public void testPallet1ContainerFullItemFull() {
        Container container = new Container(1, 101, 122, 84, ItemRotation.FULL);
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

        assertEquals(1, solutions.size());
        assertTrue(solutions.get(0).percentageContainerVolumeUsed > 81f);
    }

    public void testPallet1ContainerYItemFull() {
        Container container = new Container(1, 101, 122, 84, ItemRotation.HORIZONTAL);
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
        assertTrue(solutions.get(0).percentageContainerVolumeUsed > 73.3f);
    }

    public void testPallet1ContainerFullItemY() {
        Container container = new Container(1, 101, 122, 84, ItemRotation.FULL);
        List<Item> items = new ArrayList<>();
        // set1: 307 items; 5 types; 89.5% full
        items.add(new Item(1, "1", 3, 5, 7, 51, ItemRotation.HORIZONTAL));
        items.add(new Item(2, "2", 20, 4, 6, 90, ItemRotation.HORIZONTAL));
        items.add(new Item(3, "3", 11, 21, 16, 80, ItemRotation.HORIZONTAL));
        items.add(new Item(4, "4", 51, 2, 60, 80, ItemRotation.HORIZONTAL));
        items.add(new Item(5, "5", 6, 17, 8, 6, ItemRotation.HORIZONTAL));

        List<Solution> solutions = new ArrayList<>();
        ISimulation<Container, Item> simulator = new AirForceBinPacking();
        Solution solution = simulator.simulate(container, items);
        solutions.add(solution);
        while (solution.remainingItems.size() > 0) {
            solution = simulator.simulate(container, solution.remainingItems);
            solutions.add(solution);
        }

        assertEquals(16, solutions.size());
        assertEquals(45.55f, solutions.get(0).percentageContainerVolumeUsed, 0.01);
    }

    public void testPallet1ContainerYItemY() {
        Container container = new Container(1, 101, 122, 84, ItemRotation.HORIZONTAL);
        List<Item> items = new ArrayList<>();
        // set1: 307 items; 5 types; 89.5% full
        items.add(new Item(1, "1", 3, 5, 7, 51, ItemRotation.HORIZONTAL));
        items.add(new Item(2, "2", 20, 4, 6, 90, ItemRotation.HORIZONTAL));
        items.add(new Item(3, "3", 11, 21, 16, 80, ItemRotation.HORIZONTAL));
        items.add(new Item(4, "4", 51, 2, 60, 80, ItemRotation.HORIZONTAL));
        items.add(new Item(5, "5", 6, 17, 8, 6, ItemRotation.HORIZONTAL));

        List<Solution> solutions = new ArrayList<>();
        ISimulation<Container, Item> simulator = new AirForceBinPacking();
        Solution solution = simulator.simulate(container, items);
        solutions.add(solution);
        while (solution.remainingItems.size() > 0) {
            solution = simulator.simulate(container, solution.remainingItems);
            solutions.add(solution);
        }

        assertEquals(24, solutions.size());
        assertEquals(40.82f, solutions.get(0).percentageContainerVolumeUsed, 0.01);
    }

    public void testPallet1ContainerYItemNone() {
        Container container = new Container(1, 101, 122, 84, ItemRotation.HORIZONTAL);
        List<Item> items = new ArrayList<>();
        // set1: 307 items; 5 types; 89.5% full
        items.add(new Item(1, "1", 3, 5, 7, 51, ItemRotation.NONE));
        items.add(new Item(2, "2", 20, 4, 6, 90, ItemRotation.NONE));
        items.add(new Item(3, "3", 11, 21, 16, 80, ItemRotation.NONE));
        items.add(new Item(4, "4", 51, 2, 60, 80, ItemRotation.NONE));
        items.add(new Item(5, "5", 6, 17, 8, 6, ItemRotation.NONE));

        List<Solution> solutions = new ArrayList<>();
        ISimulation<Container, Item> simulator = new AirForceBinPacking();
        Solution solution = simulator.simulate(container, items);
        solutions.add(solution);
        while (solution.remainingItems.size() > 0) {
            solution = simulator.simulate(container, solution.remainingItems);
            solutions.add(solution);
        }

        assertEquals(24, solutions.size());
        assertEquals(38.2f, solutions.get(0).percentageContainerVolumeUsed, 0.01);
    }

    public void testPallet1ContainerNoneItemY() {
        Container container = new Container(1, 101, 122, 84, ItemRotation.NONE);
        List<Item> items = new ArrayList<>();
        // set1: 307 items; 5 types; 89.5% full
        items.add(new Item(1, "1", 3, 5, 7, 51, ItemRotation.HORIZONTAL));
        items.add(new Item(2, "2", 20, 4, 6, 90, ItemRotation.HORIZONTAL));
        items.add(new Item(3, "3", 11, 21, 16, 80, ItemRotation.HORIZONTAL));
        items.add(new Item(4, "4", 51, 2, 60, 80, ItemRotation.HORIZONTAL));
        items.add(new Item(5, "5", 6, 17, 8, 6, ItemRotation.HORIZONTAL));

        List<Solution> solutions = new ArrayList<>();
        ISimulation<Container, Item> simulator = new AirForceBinPacking();
        Solution solution = simulator.simulate(container, items);
        solutions.add(solution);
        while (solution.remainingItems.size() > 0) {
            solution = simulator.simulate(container, solution.remainingItems);
            solutions.add(solution);
        }

        assertEquals(24, solutions.size());
        assertEquals(40.82f, solutions.get(0).percentageContainerVolumeUsed, 0.01);
    }

    public void testPallet1ContainerNoneItemNone() {
        Container container = new Container(1, 101, 122, 84, ItemRotation.NONE);
        List<Item> items = new ArrayList<>();
        // set1: 307 items; 5 types; 89.5% full
        items.add(new Item(1, "1", 3, 5, 7, 51, ItemRotation.NONE));
        items.add(new Item(2, "2", 20, 4, 6, 90, ItemRotation.NONE));
        items.add(new Item(3, "3", 11, 21, 16, 80, ItemRotation.NONE));
        items.add(new Item(4, "4", 51, 2, 60, 80, ItemRotation.NONE));
        items.add(new Item(5, "5", 6, 17, 8, 6, ItemRotation.NONE));

        List<Solution> solutions = new ArrayList<>();
        ISimulation<Container, Item> simulator = new AirForceBinPacking();
        Solution solution = simulator.simulate(container, items);
        solutions.add(solution);
        while (solution.remainingItems.size() > 0) {
            solution = simulator.simulate(container, solution.remainingItems);
            solutions.add(solution);
        }

        assertEquals(24, solutions.size());
        assertEquals(38.2f, solutions.get(0).percentageContainerVolumeUsed, 0.01);
    }
}
