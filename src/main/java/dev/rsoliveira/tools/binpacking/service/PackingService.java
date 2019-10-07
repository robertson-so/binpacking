package dev.rsoliveira.tools.binpacking.service;

import dev.rsoliveira.tools.binpacking.domain.Container;
import dev.rsoliveira.tools.binpacking.domain.Item;
import dev.rsoliveira.tools.binpacking.domain.Solution;
import dev.rsoliveira.tools.binpacking.simulation.AirForceBinPacking;
import dev.rsoliveira.tools.binpacking.simulation.ISimulation;

import java.util.ArrayList;
import java.util.List;

public class PackingService {

    private static ISimulation<Container, Item> simulator;

    private static PackingService instance = new PackingService();

    private PackingService() {
        simulator = new AirForceBinPacking();
    }

    public static synchronized PackingService getInstance() {
        if (instance == null) {
            instance = new PackingService();
        }
        return instance;
    }

    /**
     * Simulates packing a list of items into a container, resulting in a list of solutions.
     * Each simulation iteration results in 2 lists of packed and unpacked items, and the next iteration uses
     * the unpacked items simulate the packing, until there are no items remaining to pack.
     * It is best to use this method to know how many containers are possibly needed to pack all the given items.
     * @param container the container used as template for packing.
     * @param items the list of all items that will be packed.
     * @return a list of possible packings, containing position and direction for each item inside the containers.
     */
    public List<Solution> simulate(Container container, List<Item> items) {
        List<Solution> solutions = new ArrayList<>();
        Solution solution = simulator.simulate(container, items);
        solutions.add(solution);
        while (!solution.getRemainingItems().isEmpty()) {
            solution = simulator.simulate(container, solution.getRemainingItems());
            solutions.add(solution);
        }

        return solutions;
    }

    /**
     * Simulates packing a list of items into a list of containers, resulting in a list of solutions.
     * Each simulation iteration results in 2 lists of packed and unpacked items, and the next iteration uses
     * the unpacked items simulate the packing, until all containers were used.
     * If there are unpacked items at the of simulations, the last solution will have the remaining ones.
     * It is best to use this method when the list of containers is known.
     * @param containers the list of containers to use on the simulation; each container can be used for one simulation only.
     * @param items the list of all items that will be packed.
     * @return a list of possible packings, containing position and direction for each item inside the containers.
     */
    public static List<Solution> simulate(List<Container> containers, List<Item> items) {
        List<Solution> solutions = new ArrayList<>();

        List<Item> remaining = new ArrayList<>(items);
        for (Container container : containers) {
            if (!remaining.isEmpty()) {
                Solution solution = simulator.simulate(container, remaining);
                solutions.add(solution);
                if (solution.isCompletePacking()) {
                    remaining.clear();
                    break;
                }
                remaining = new ArrayList<>(solution.getRemainingItems());
            }
        }

        return solutions;
    }
}
