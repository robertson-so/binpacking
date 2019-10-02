package dev.rsoliveira.tools.binpacking;

import dev.rsoliveira.tools.binpacking.domain.Item;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Collection;
import java.util.List;

@RunWith(Parameterized.class)
public class BischoffRatcliff3TypesTest extends BischofRatcliffAbstractText {

    public BischoffRatcliff3TypesTest(List<Item> items, Double containerVolume, Double itemsVolume) {
        super(items, containerVolume, itemsVolume);
    }

    @Parameterized.Parameters(name = "BR-1 {index}: {1}%, {2}%")
    public static Collection<Object[]> data() {
        return data("br1.txt", "br1-res.txt");
    }
}
