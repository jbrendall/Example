package lc;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

class Load {
    /**
     * Load model from path
     *
     * @param p path
     * @return loaded model
     * @throws IOException
     */
    static Model.InputData loadModel(final Path p) throws IOException {
        if (p.toString().endsWith(".zip")) {
            try (final ZipFile zf = new ZipFile(p.toString())) {
                return loadZipArchive(zf);
            }
        }
        return null;
    }

    private static String ISSUE_TO_CHANGE_SET_JSON = "_issue_to_change_set.json";
    private static String CHANGE_SET_TO_CODE_JSON = "_change_set_to_code.json";

    private static Model.InputData loadZipArchive(final ZipFile zipFile) {
        Model.InputData data = new Model.InputData();

        data.issues = zipFile.stream()
                .filter(Load::isIssue)
                .map(e -> Load.toJSON(zipFile, e))
                .filter(Util.not(Objects::isNull))
                .map(Load::parseIssue)
                .collect(Collectors.toList());

        final ZipEntry e1 = zipFile.getEntry(ISSUE_TO_CHANGE_SET_JSON);
        data.issueToChangeSet = parseIssueToChangeSet(toJSON(zipFile, e1));

        final ZipEntry e2 = zipFile.getEntry(CHANGE_SET_TO_CODE_JSON);
        data.changeSets = parseChangeSetToCode(toJSON(zipFile, e2));

        return data;
    }

    private static boolean isIssue(final ZipEntry e) {
        final String n = e.getName();
        return n.endsWith(".json")
                && !n.equals(ISSUE_TO_CHANGE_SET_JSON)
                && !n.equals(CHANGE_SET_TO_CODE_JSON);
    }

    private static JSONObject toJSON(final ZipFile zf, final ZipEntry e) {
        try (InputStream is = zf.getInputStream(e)) {
            return new JSONObject(new JSONTokener(is));
        } catch (IOException ex){
            return null;
        }
    }

    private static Map<String, Collection<String>> parseIssueToChangeSet(final JSONObject obj) {
        Map<String, Collection<String>> m = new HashMap<>();

        for (String issue_id: obj.keySet()) {
            m.put(issue_id, extractCommitHashes(obj.getJSONArray(issue_id)));
        }
        return m;
    }

    private static Collection<String> extractCommitHashes(final JSONArray entries) {
        ArrayList<String> al = new ArrayList<>();
        for (int i = 0; i < entries.length(); ++i) {
            al.add(entries.getJSONObject(i).getString("commit_hash"));
        }
        return al;
    }

    private static Collection<Model.ChangeSet> parseChangeSetToCode(final JSONObject obj) {
        return obj.keySet().stream()
                .map(k -> new Util.Tuple<>(k, obj.getJSONObject(k)))
                .map(Load::parseChangeSet)
                .collect(Collectors.toList());
    }

    private static Model.ChangeSet parseChangeSet(final Util.Tuple<String, JSONObject> t) {
        Model.ChangeSet cs = new Model.ChangeSet();

        cs.commitHash = t._1;
        cs.author = stringOrNull(t._2, "author");
        cs.authorEmail = stringOrNull(t._2, "author_email");
        cs.committedDate = stringOrNull(t._2, "committed_date");
        cs.filePath = Util.arrayToStream(t._2.getJSONArray("file_path"))
                .map(String.class::cast)
                .collect(Collectors.toList());
        cs.message = stringOrNull(t._2, "message");

        return cs;
    }

    private static Model.Issue parseIssue(final JSONObject o) {
        Model.Issue i = new Model.Issue();

        i.assignee = stringOrNull(o, "assignee");
        i.assigneeUsername = stringOrNull(o,"assignee_username");
        i.createdDate = stringOrNull(o,"created_date");
        i.description = stringOrNull(o, "description");
        i.id = stringOrNull(o,"id");
        i.resolvedDate = stringOrNull(o,"resolved_date");
        i.summary = stringOrNull(o, "summary");
        i.type = stringOrNull(o, "type");

        return i;
    }

    private static String stringOrNull(final JSONObject obj, final String key) {
        try {
            return obj.getString(key);
        } catch (JSONException ex) {
            return null;
        }
    }
}
