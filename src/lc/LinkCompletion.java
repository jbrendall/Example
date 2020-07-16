package lc;

import org.json.JSONException;
import org.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pyext.PythonExtension;
import weka.core.Instances;

import java.util.Arrays;
import java.util.HashMap;


public class LinkCompletion {
    private static final Logger logger = LoggerFactory.getLogger(LinkCompletion.class);

    // TODO: temporary class for testing / showcase
    private static class TestDriver {
        private static JSONObject createScriptData() throws JSONException {
            return new JSONObject();
        }

        private static Model.InputData createProjectTestData() {
            Model.InputData d = new Model.InputData();

            d.issues = Arrays.asList(
                    Model.Issue.createDummyInstance(),
                    Model.Issue.createDummyInstance(),
                    Model.Issue.createDummyInstance()
            );

            d.changeSets = Arrays.asList(
                    Model.ChangeSet.createDummyInstance(),
                    Model.ChangeSet.createDummyInstance()
            );

            d.issueToChangeSet = new HashMap<>();
            d.issueToChangeSet.put("ISSUE-1", Arrays.asList("abc", "def"));
            d.issueToChangeSet.put("ISSUE-2", Arrays.asList("123", "456", "789"));

            d.similarityChangeSetToIssue = Arrays.asList(
                    new Model.Similarity("abc", "ISSUE-1", 0.25),
                    new Model.Similarity("def", "ISSUE-2", 0.75)
            );

            d.similarityCodeToIssue = Arrays.asList(
                    new Model.Similarity("a.java", "ISSUE-1", 0.11),
                    new Model.Similarity("b.java", "ISSUE-2", 0.33),
                    new Model.Similarity("c.java", "ISSUE-3", 0.55)
            );

            d.traceMetric = Arrays.asList(
                    new Model.Similarity("trace/a.java", "TRACE-1", 9.3)
            );
            
            return d;
        }
    }


    /**
     * Testing main
     */
    public static void main(String[] args) {
        logger.info("Enter");

        new LinkCompletion(TestDriver.createProjectTestData()).process();

        logger.info("Leave");
    }


    /**
     * Input data for the link completion algorithm
     */
    private Model.InputData projectData;

    /**
    /**
     * Resulting weka dataset used to train a classifier, e.g. RandomForest, J48 etc
     */
    private Instances dataset;

    private LinkCompletion(Model.InputData project) {
        this.projectData = project;
    }

    public void process() {
        try {
            JSONObject result = PythonExtension.run("LC",
                    TestDriver.createScriptData(),
                    PyDataTransfer.toJson(this.projectData),
                    4444, "");

            logger.debug("received {}", result);
            handleResult(result);
        } catch (InterruptedException | JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void handleResult(JSONObject pyResultData) {
        this.dataset = PyDataTransfer.parseDataset(
                pyResultData.getJSONObject("resultDataset"));

        logger.info(this.dataset.toString());
    }
}
