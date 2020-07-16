package lc;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

import org.json.JSONException;
import org.json.JSONObject;


class Model {
    /**
     * A commit in git
     */
    public static class ChangeSet {
        /**
         * e.g.: "john doe"
         */
        public String author;

        /**
         * e.g.: "john_doe@cyb.org"
         */
        public String authorEmail;

        /**
         * e.g.: "868b078f9fb7aa90c4d1515a5e1909af74c174d5"
         */
        public String commitHash;

        /**
         * e.g.: "2014-10-30T21:16:20Z"
         */
        public String committedDate;

        /**
         * e.g.: ["src/org/apache/pig/Main.java",
         *        "src/org/apache/pig/PigServer.java",
         *        "src/org/apache/pig/impl/io/FileLocalizer.java"]
         */
        public Collection<String> filePath;

        public String message;

        static ChangeSet createDummyInstance() {
            ChangeSet cs = new ChangeSet();
            cs.author = "john doe";
            cs.authorEmail = "john_doe@cyb.org";
            cs.commitHash = "868b078f9fb7aa90c4d1515a5e1909af74c174d5";
            cs.committedDate = "2014-10-30T21:16:20Z";
            cs.filePath = Arrays.asList(
                "src/org/apache/pig/Main.java",
                "src/org/apache/pig/PigServer.java",
                "src/org/apache/pig/impl/io/FileLocalizer.java");
            cs.message = "the commit message";

            return cs;
        }
    }

    /**
     * An issue in Atlassian JIRA
     */
    public static class Issue {
        /**
         * e.g.: "Joe"
         */
        public String assignee;

        /**
         * e.g.: "joe"
         */
        public String assigneeUsername;

        /**
         * e.g.: "2007-10-10T22:29:17Z"
         */
        public String createdDate;

        public String description;

        /**
         * e.g.: "PIG-1"
         */
        public String id;

        /**
         * e.g.: "2007-10-31T16:57:41Z"
         */
        public String resolvedDate;

        public String summary;

        /**
         * e.g.: "Enhancement"
         */
        public String type;

        static Issue createDummyInstance() {
            Issue i = new Issue();
            i.assignee = "Joe";
            i.assigneeUsername = "joe";
            i.createdDate = "2007-10-10T22:29:17Z";
            i.description = "desc of ISSUE-1";
            i.id = "ISSUE-1";
            i.resolvedDate = "2007-10-31T16:57:41Z";
            i.summary = "summary of ISSUE-1";
            i.type = "Bug";

            return i;
        }
    }


    /**
     * Similarity between two objects
     *
     * An object is represented by its unique id, which is
     *  - Issue -> IssueID
     *  - ChangeSet -> CommitHash
     *  - File -> FilePath
     */
    public static class Similarity {
        public String source;
        public String target;
        public double value;

        public Similarity(final String source, final String target, final double value) {
            this.source = source;
            this.target = target;
            this.value = value;
        }
    }

    /**
     * Link completion input data
     */
    public static class InputData {
        public Collection<ChangeSet> changeSets;

        public Collection<Issue> issues;

        /**
         * issue id to linked commits
         *
         * e.g.: {
         *     "PIG-1": ["4bcad4bb2d34010c8c0f194f2722cbdf5903051b",
         *               "e513f9b160211d19ba2e790e53a31afed72001a1"],
         *     "PIG-2": ["bfc532149911d8fba7b52c8063d5496278114e19"]
         * }
         */
        public Map<String, Collection<String>> issueToChangeSet;

        /**
         * source: file path, e.g.: "another/path/to/file2.java"
         * target: issue id, e.g.:  "PIG-1"
         */
        public Collection<Similarity> similarityCodeToIssue;

        /**
         * source: commit hash, e.g.: "66fb979928d57643cd0c970dba60f35f0f7edf2a"
         * target: issue id, e.g.:    "PIG-2"
         */
        public Collection<Similarity> similarityChangeSetToIssue;

        /**
         * Special jacob trace metric
         *
         * source: file path, e.g.: "path/to/file3.java"
         * target: issue id, e.g.: "PIG-4"
         */
        public Collection<Similarity> traceMetric;
    }
}
