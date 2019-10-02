package dev.rsoliveira.tools.binpacking;

import dev.rsoliveira.tools.binpacking.domain.Item;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Collection;
import java.util.List;

@RunWith(Parameterized.class)
public class BischoffRatcliff10TypesTest extends BischofRatcliffAbstractText {

    public BischoffRatcliff10TypesTest(List<Item> items, Double containerVolume, Double itemsVolume) {
        super(items, containerVolume, itemsVolume);
    }

    @Parameterized.Parameters(name = "BR-4 {index}: {1}%, {2}%")
    public static Collection<Object[]> data() {
        return data("br4.txt", "br4-res.txt");
    }
}
