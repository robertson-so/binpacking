package dev.rsoliveira.tools.binpacking;

import dev.rsoliveira.tools.binpacking.domain.Container;
import dev.rsoliveira.tools.binpacking.domain.Item;
import dev.rsoliveira.tools.binpacking.domain.ItemRotation;
import dev.rsoliveira.tools.binpacking.domain.Solution;
import dev.rsoliveira.tools.binpacking.service.PackingService;
import dev.rsoliveira.tools.binpacking.simulation.AirForceBinPacking;
import dev.rsoliveira.tools.binpacking.simulation.ISimulation;
import org.junit.Test;
import org.junit.runners.Parameterized;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;

public abstract class BischofRatcliffAbstractText {
    private static Container container;
    private List<Item> items;
    private Double itemsVolume;
    private Double containerVolume;

    public BischofRatcliffAbstractText(List<Item> items, Double containerVolume, Double itemsVolume) {
        this.items = items;
        this.containerVolume = containerVolume;
        this.itemsVolume = itemsVolume;
    }

    protected static Collection<Object[]> data(String inputFile, String resultFile) {
        List<Object[]> res = new ArrayList<>();
        try {
            Pattern number2 = Pattern.compile(" (\\d+) (\\d+)");
            Pattern number2f = Pattern.compile(" (\\d+\\.\\d+) (\\d+\\.\\d+)");
            Pattern number3 = Pattern.compile(" (\\d+) (\\d+) (\\d+)");
            Pattern number8 = Pattern.compile(" (\\d+) (\\d+) (\\d+) (\\d+) (\\d+) (\\d+) (\\d+) (\\d+)");

            BufferedReader reader = new BufferedReader(new FileReader("./src/test/resources/" + inputFile));
            BufferedReader reader2 = new BufferedReader(new FileReader("./src/test/resources/" + resultFile));
            String lineTotalTests = reader.readLine();
            Integer totalTests = Integer.valueOf(lineTotalTests.trim());
            for (int i = 0; i < totalTests; i++) {
                String txtTestId = reader.readLine();
                Matcher mTestId = number2.matcher(txtTestId);
                boolean b1 = mTestId.find();
                Integer testId = Integer.parseInt(mTestId.group(1)); // test id
                Matcher txtContainer = number3.matcher(reader.readLine()); // container
                boolean b2 = txtContainer.find();
                if (container == null) {
                    container = new Container(1, Long.parseLong(txtContainer.group(1)), Long.parseLong(txtContainer.group(2)), Long.parseLong(txtContainer.group(3)), ItemRotation.FULL);
                }
                List<Item> items = new ArrayList<>();
                Integer totalItems = Integer.valueOf((reader.readLine().trim()));
                for (int j = 0; j < totalItems; j++) {
                    Matcher txtItem = number8.matcher(reader.readLine());
                    boolean b3 = txtItem.find();
                    items.add(new Item(Integer.parseInt(txtItem.group(1)), txtItem.group(1), Long.parseLong(txtItem.group(2)), Long.parseLong(txtItem.group(4)), Long.parseLong(txtItem.group(6)), Integer.parseInt(txtItem.group(8)), ItemRotation.FULL));
                }

                String txtResult = reader2.readLine();
                Matcher mResult = number2f.matcher(txtResult);
                boolean b4 = mResult.find();
                res.add(new Object[]{items, Double.parseDouble(mResult.group(1)), Double.parseDouble(mResult.group(2))});
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return res;
    }

    @Test
    public void test() {
        List<Solution> solutions1 = PackingService.getInstance().simulate(container, items);

        assertEquals(2, solutions1.size());
        assertEquals(containerVolume, solutions1.get(0).getPercentageContainerVolumeUsed(), 0.01);
        assertEquals(itemsVolume, solutions1.get(0).getPercentagePackedItemsVolume(), 0.01);
    }
}
