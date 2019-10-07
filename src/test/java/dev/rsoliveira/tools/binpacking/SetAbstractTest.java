package dev.rsoliveira.tools.binpacking;

import dev.rsoliveira.tools.binpacking.domain.Container;
import dev.rsoliveira.tools.binpacking.domain.Item;
import dev.rsoliveira.tools.binpacking.domain.ItemRotation;
import dev.rsoliveira.tools.binpacking.domain.Solution;
import dev.rsoliveira.tools.binpacking.simulation.AirForceBinPacking;
import dev.rsoliveira.tools.binpacking.simulation.ISimulation;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public abstract class SetAbstractTest {

    private List<Item> items;

    private Double containerVolumeUsed;

    @Test
    public void test() {
        Container container = new Container(1, 104, 96, 84, ItemRotation.FULL);

        List<Solution> solutions = new ArrayList<>();
        ISimulation<Container, Item> simulator = new AirForceBinPacking();
        Solution solution = simulator.simulate(container, items);
        solutions.add(solution);
        while (solution.getRemainingItems().size() > 0) {
            solution = simulator.simulate(container, solution.getRemainingItems());
            solutions.add(solution);
        }

        //assertEquals(2, solutions.size());
        assertEquals(containerVolumeUsed, solutions.get(0).getPercentageContainerVolumeUsed(), 0.01);
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public Double getContainerVolumeUsed() {
        return containerVolumeUsed;
    }

    public void setContainerVolumeUsed(Double containerVolumeUsed) {
        this.containerVolumeUsed = containerVolumeUsed;
    }
}
