import dev.rsoliveira.tools.binpacking.domain.Item;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Collection;
import java.util.List;

@RunWith(Parameterized.class)
public class BischoffRatcliff15TypesTest extends BischofRatcliffAbstractText {

    public BischoffRatcliff15TypesTest(List<Item> items, Double containerVolume, Double itemsVolume) {
        super(items, containerVolume, itemsVolume);
    }

    @Parameterized.Parameters(name = "BR-6 {index}: {1}%, {2}%")
    public static Collection<Object[]> data() {
        return data("br6.txt", "br6-res.txt");
    }
}
