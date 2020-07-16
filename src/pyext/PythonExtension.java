package pyext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.json.JSONObject;

/**
 * Python extension
 */
public class PythonExtension {
    private final static Logger logger = LoggerFactory.getLogger(PythonExtension.class);
    /**
     * Run python extension
     *
     * @param algorithm algorithm name
     * @param algorithmArguments arguments for the algorithm
     * @param data input data for the extension
     * @return execution result
     * @throws InterruptedException
     */
    public static JSONObject run(String algorithm,
                                 JSONObject algorithmArguments,
                                 JSONObject data,
                                 int port, String dir) throws InterruptedException {
        final int portNumber = port;
        
        Server server = new Server(portNumber,
                algorithm, algorithmArguments, data);
        PythonRunner runner = new PythonRunner(server.getPort(), dir);

        final Thread pyThread = new Thread(runner, "PythonExecutionThread");
        final Thread comThread = new Thread(server, "PythonCommunicationThread");

        comThread.start();
        pyThread.start();

        while (true) {
            logger.info("main loop poll ...");
            Thread.sleep(1000);

            if (runner.hasFinished() || server.hasFinished()) {
                logger.info("a thread has finished .. leave busy loop");
                break;
            }
        }

        if (runner.hasFailed()) {
            logger.error("Python failed ...");

            if (server.isRunning()) {
                server.forceExit();
            }
        }
        if (server.hasFailed()) {
            logger.error("PyExt: Communication failed ...");

            if (runner.isRunning()) {
                runner.forceExit();
            }
        }

        pyThread.join();
        comThread.join();

        return server.getResultData();
    }
}
