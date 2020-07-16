package lc;

import org.junit.Assert;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

public class LoadTest {
    @Test
    public void loadTest() throws Exception {
        final Path p = Paths.get("./test_data/apache_pig_10issues.zip");
        final Model.InputData model = Load.loadModel(p);

        Assert.assertEquals(10, model.issues.size());
        Assert.assertEquals(3149, model.changeSets.size());
        Assert.assertEquals(2458, model.issueToChangeSet.size());
    }
}
