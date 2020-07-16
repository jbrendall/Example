package lc;

import org.json.JSONObject;
import org.json.JSONTokener;
import org.junit.Assert;
import org.junit.Test;
import weka.core.Attribute;
import weka.core.Instances;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class PyDataTransferTest {
    @Test
    public void parseTest() throws FileNotFoundException {
        File file = new File("./test_data/result_dataset.json");

        FileInputStream fis = new FileInputStream(file);
        JSONObject document = new JSONObject(new JSONTokener(fis));
        Instances dataset = PyDataTransfer.parseDataset(document);

        // // // Attributes
        Assert.assertEquals(3, dataset.numAttributes());

        Attribute a0 = dataset.attribute(0);
        Assert.assertEquals("numeric_att", a0.name());
        Assert.assertEquals(Attribute.NUMERIC, a0.type());

        Attribute a1 = dataset.attribute(1);
        Assert.assertEquals("string_att", a1.name());
        Assert.assertEquals(Attribute.STRING, a1.type());

        Attribute a2 = dataset.attribute(2);
        Assert.assertEquals("nominal_att", a2.name());
        Assert.assertEquals(Attribute.NOMINAL, a2.type());
        Assert.assertEquals(2, a2.numValues());

        // // // Instances
        Assert.assertEquals(4, dataset.numInstances());
    }
}
