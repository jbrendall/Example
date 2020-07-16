"""
Link completion extension module for TraceDynamo
"""

import sys
import json
import logging
import collections

# import the external implementation
import spojit

from spojit.io import JsonImport, IssueToOtherSimilarityHandler
from spojit.io import TextSimilarityHandler,TraceSimilarityHandler

from spojit.event import EventStreamCreator
from spojit.profile import DataSource, ProfileGenerator, ProcessAllStrategy
from spojit.sample import create_weka_dataset, SampleCollector
import spojit.weka as weka

SPOJIT_VERSION = spojit.__version__

LOGGER = logging.getLogger(__name__)
LOGGER.setLevel(logging.INFO)

###############################################################################
# BEGIN TESTING
###############################################################################

class TestDriver:
    """TODO: temporary class for showcase / testing purposes
    """
    @staticmethod
    def dummy_result_dataset():
        """Demonstrating all supported weka dataset features
        """
        attrs = [
            weka.create_numeric_attribute('numeric_att', 'numeric description'),
            weka.create_string_attribute('string_att', 'string description\nmulti line'),
            weka.create_nominal_attribute('nominal_att', ['linked', 'non_linked'], 'nominal description')
        ]
        dataset = weka.create_dataset('my_relation', attrs)

        dataset = weka.add_instance(dataset, [None, None, None])
        dataset = weka.add_instance(dataset, [1, 'spojit', 'linked'])
        dataset = weka.add_instance(dataset, [1, None, 'non_linked'])

        return dataset

    @staticmethod
    def load_project():
        """load project from disk
        """
        importer = JsonImport('apache_pig_10issues')
        importer.run('/Users/micha/PycharmProjects/spojit/test_data/pig_export_small')
        return importer

###############################################################################
# END TESTING
###############################################################################

class SimilarityHandler(IssueToOtherSimilarityHandler):
    def __init__(self, data):
        self.data = data

    def query_similarity(self, o1: str, o2: str):
        item = self.data.get(o1)
        return item.get(o2, None) if item else None

    def __str__(self):
        n_entries = len(self.data.keys())
        entry = next(iter(self.data.items())) if n_entries > 0 else None
        return 'SimilarityHandler with {} entries, 1st: {}'.format(n_entries, entry)


def _create_similarity_handler(entries: list, key1: str, key2: str):
    def build_double_lookup():
        from collections import defaultdict
        mapping = defaultdict(defaultdict)

        for entry in entries:
            mapping[entry[key1]].update({entry[key2]: entry['v']})

        return mapping

    return SimilarityHandler(build_double_lookup()) if entries else None


def _run_spojit(data_source: dict,
                text_sim: TextSimilarityHandler,
                trace_sim: TraceSimilarityHandler) -> weka.Instances:

    profile_gen = ProfileGenerator(data_source,
                                   ProcessAllStrategy(),
                                   text_similarity_handler=text_sim,
                                   trace_handler=trace_sim,
                                   issue_types=['improvement'])

    collector = SampleCollector()
    profile_gen.run(collector)

    return create_weka_dataset('dataset', collector.samples)


def run(algorithm_arguments: dict, project_data: dict, *args, **kwargs):
    """Run link completion
    """

    LOGGER.debug('spojit arguments %s', algorithm_arguments)

    artifacts = {k: project_data[k] for k in ['issues',
                                              'change_sets',
                                              'issue_to_change_set']}
    data_source = DataSource(**artifacts)

    text_sim = TextSimilarityHandler(
        # turn (code, issue, val) into double lookup: issue -> code
        issue_to_code_vsm_ngram=_create_similarity_handler(
            project_data['similarity_code_to_issue'], key1='t', key2='s'),
        # turn (change set, issue, val) into double lookup: issue -> change set
        issue_to_commit_vsm_ngram=_create_similarity_handler(
            project_data['similarity_change_set_to_issue'], key1='t', key2='s'))

    trace_sim = TraceSimilarityHandler(
        # turn (code, issue, val) into double lookup: issue -> code
        issue_to_file=_create_similarity_handler(
            project_data['trace_metric'], key1='t', key2='s'))

    LOGGER.debug(data_source)
    LOGGER.debug(text_sim)
    LOGGER.debug(trace_sim)

    # FIXME: use real implementation
    generated_dataset = TestDriver.dummy_result_dataset()

    if False:
        LOGGER.warning('loading project data from disc')
        data_source = TestDriver.load_project()
        generated_dataset = _run_spojit(data_source, text_sim, trace_sim)

    result_data = {
        'extension': {
            'name': 'link completion',
            'pythonVersion': sys.version,
            'spojitVersion': SPOJIT_VERSION
        },
        'resultDataset': generated_dataset._asdict()
    }

    result = json.dumps(result_data)
    LOGGER.debug('result: %s', result)

    return result


###############################################################################

if __name__ == '__main__':
    LOGGER.warning('extension is not supposed to be called stand alone')
    print(run({}, {}))
