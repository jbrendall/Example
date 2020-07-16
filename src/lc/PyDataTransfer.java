package lc;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import weka.core.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * deals with conversion from Java to python and back using Json
 */
class PyDataTransfer {

    private static JSONObject toJson(final Model.Issue i) {
        JSONObject obj = new JSONObject();
        obj.put("assignee", i.assignee);
        obj.put("assignee_username", i.assigneeUsername);
        obj.put("created_date", i.createdDate);
        obj.put("issue_id", i.id);
        obj.put("resolved_date", i.resolvedDate);

        // Note: 'description' and 'summary' are intentionally not serialized
        return obj;
    }

    private static JSONObject toJson(final Model.ChangeSet cs) {
        JSONObject obj = new JSONObject();
        obj.put("author", cs.author);
        obj.put("author_email", cs.authorEmail);
        obj.put("commit_hash", cs.commitHash);
        obj.put("committed_date", cs.committedDate);
        obj.put("file_path", cs.filePath);

        // Note: 'message' is intentionally not serialized
        return obj;
    }

    private static JSONObject toJson(final Model.Similarity s) {
        JSONObject obj = new JSONObject();
        obj.put("s", s.source);
        obj.put("t", s.target);
        obj.put("v", s.value);
        return obj;
    }

    static JSONObject toJson(Model.InputData project) {
        JSONObject obj = new JSONObject();

        obj.put("issues", project.issues.stream()
                .map(PyDataTransfer::toJson)
                .collect(Collectors.toList()));

        obj.put("change_sets", project.changeSets.stream()
                .map(PyDataTransfer::toJson)
                .collect(Collectors.toList()));

        obj.put("issue_to_change_set", project.issueToChangeSet);

        obj.put("similarity_code_to_issue", project.similarityCodeToIssue.stream()
                .map(PyDataTransfer::toJson)
                .collect(Collectors.toList())
        );

        obj.put("similarity_change_set_to_issue", project.similarityChangeSetToIssue.stream()
                .map(PyDataTransfer::toJson)
                .collect(Collectors.toList())
        );

        obj.put("trace_metric", project.traceMetric.stream()
                .map(PyDataTransfer::toJson)
                .collect(Collectors.toList())
        );

        return obj;
    }


    /**
     * Structure:
     *  {
     *      'relation_name': ...,
     *      'attributes': [],
     *      'values': []
     *  }
     *
     *
     * @param obj serialialized dataset
     * @return a weka dataset
     */
    static Instances parseDataset(JSONObject obj) {
        String relationName = obj.getString("relation_name");
        ArrayList<Attribute> attributes = Util.arrayToStream(obj.getJSONArray("attributes"))
                .map(JSONArray.class::cast)
                .map(PyDataTransfer::parseAttribute)
                .collect(Collectors.toCollection(ArrayList::new));

        Instances dataset = new Instances(relationName, attributes, 50);

        List<Instance> instances = Util.arrayToStream(obj.getJSONArray("values"))
                .map(JSONArray.class::cast)
                .map(i -> PyDataTransfer.parseInstance(i, attributes, dataset))
                .collect(Collectors.toList());

        dataset.addAll(instances);
        return dataset;
    }

    private static Attribute parseAttribute(final JSONArray jAttribute) {
        String attName = jAttribute.getString(0);
        String attType = jAttribute.getString(1);
        //noinspection unused
        String attDescription = jAttribute.getString(2);

        switch (attType) {
            case "numeric":
                return new Attribute(attName, false);
            case "string":
                return new Attribute(attName, true);
            case "nominal":
                JSONArray jNominalValues = jAttribute.getJSONArray(3);
                List<String> values = Util.arrayToStream(jNominalValues)
                        .map(Object::toString)
                        .collect(Collectors.toList());

                return new Attribute(attName, values);
            default:
                throw new IllegalArgumentException(String.format(
                        "Unsupported weka attribute type \'%s\': %s",
                        attType, jAttribute));
        }
    }

    private static Instance parseInstance(final JSONArray jInstance,
                                          final List<Attribute> attributes,
                                          final Instances dataset) {
        if (jInstance.length() != attributes.size()) {
            throw new IllegalArgumentException(String.format(
                    "Instance %s has %d attributes but %d expected",
                    jInstance, jInstance.length(), attributes.size()));
        }

        Instance instance = new SparseInstance(attributes.size());
        instance.setDataset(dataset);
        for (int i = 0; i < attributes.size(); i++) {
            Attribute attribute = attributes.get(i);

            if (jInstance.isNull(i)) {
                // there is no value at this index, so there is nothing to do. The default value
                // for each attribute in DenseInstance is 'missing'
                continue;
            }

            switch (attribute.type()) {
                case Attribute.NUMERIC:
                    Double dVal = jInstance.optDouble(i);
                    if (!dVal.isNaN()) {
                        instance.setValue(i, dVal);
                    }
                    break;
                case Attribute.STRING:
                    String sVal = jInstance.optString(i);
                    if (sVal != null) {
                        instance.setValue(i, sVal);
                    }
                    break;
                case Attribute.NOMINAL:
                    String nVal = jInstance.optString(i);
                    if (nVal != null) {
                        int index = attribute.indexOfValue(nVal);
                        if (index == -1) {
                            throw new IllegalArgumentException(String.format(
                                    "Nominal value \'%s\' not declared in header", nVal));
                        }

                        instance.setValue(i, index);
                    }
                    break;
                default:
                    throw new IllegalArgumentException(String.format(
                            "Unsupported weka attribute type %d", attribute.type()));
            }
        }

        return instance;
    }
}
