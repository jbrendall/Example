package agents;

import java.lang.Exception;
import java.util.List;

import documents.Query;
import retrieval.Result;

/**
 * @author Carlos Castro
 * 
 * Interface for the agents
 * The agents are the specific classes that run the execution.
 * The idea is to program experiments as Agents.
 * To select which Agent to run, indicate its name in the parameters for the main method of the application.
 *
 */
public interface Agent {
	public void run() throws Exception;
        public void setQuery(Query doc);
        public List<Result> getResults();
}
