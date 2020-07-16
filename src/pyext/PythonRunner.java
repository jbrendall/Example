package pyext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteWatchdog;

class PythonRunner implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(PythonRunner.class);

    private String scriptName;
    private int portNumber;
    private String dir;

    private boolean hasFailed = false;
    private boolean isRunning = false;
    private boolean hasFinished = false;
    private boolean forceExit = false;

    private DefaultExecutor executor;
    private ExecuteWatchdog watchdog;

    PythonRunner(int portNumber, String dir) {
        this.scriptName = "pythonScripts/Client.py";
        this.portNumber = portNumber;
        this.dir = dir;
    }

    @Override
    public void run() {
        try {
            this.isRunning = true;
            runImpl();
        }  catch (IOException e) {
            logger.error("Python process crashed", e);
            e.printStackTrace();
            this.hasFailed = true;
        }
        this.isRunning = false;
        this.hasFinished = true;
    }

    boolean isRunning() {
        return this.isRunning;
    }

    boolean hasFailed() {
        return this.hasFailed;
    }

    boolean hasFinished() {
        return this.hasFinished;
    }

    void forceExit() {
        logger.warn("receive forceExit");
        this.forceExit = true;
        this.watchdog.destroyProcess();
    }

    private void runImpl() throws IOException {
        final CommandLine cmdLine = new CommandLine("python3");
        cmdLine.addArgument(this.scriptName);
        cmdLine.addArgument("--port");
        cmdLine.addArgument(Integer.toString(this.portNumber));
        cmdLine.addArgument("--path");
        cmdLine.addArgument(this.dir);
        this.watchdog = new ExecuteWatchdog(ExecuteWatchdog.INFINITE_TIMEOUT);
        this.executor = new DefaultExecutor();

        this.executor.setWatchdog(this.watchdog);

        this.executor.execute(cmdLine);
    }
}
