package dev.rsoliveira.tools.binpacking;

import dev.rsoliveira.tools.binpacking.domain.Item;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Collection;
import java.util.List;

@RunWith(Parameterized.class)
public class BischoffRatcliff12TypesTest extends BischofRatcliffAbstractText {

    public BischoffRatcliff12TypesTest(List<Item> items, Double containerVolume, Double itemsVolume) {
        super(items, containerVolume, itemsVolume);
    }

    @Parameterized.Parameters(name = "BR-5 {index}: {1}%, {2}%")
    public static Collection<Object[]> data() {
        return data("br5.txt", "br5-res.txt");
    }
}
