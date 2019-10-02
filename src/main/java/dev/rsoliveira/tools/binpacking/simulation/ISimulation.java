package dev.rsoliveira.tools.binpacking.simulation;

import dev.rsoliveira.tools.binpacking.domain.Item;
import dev.rsoliveira.tools.binpacking.domain.Solution;
import dev.rsoliveira.tools.binpacking.domain.Volume;

import java.util.List;

public interface ISimulation<C extends Volume, P extends Item> {

  Solution simulate(C container, List<P> volumes);

}
