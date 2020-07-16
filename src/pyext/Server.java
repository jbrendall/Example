package pyext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

import org.json.JSONException;
import org.json.JSONObject;

class Server implements Runnable {
    private int portNumber;
    private Socket socket;
    private ServerSocket serverSocket;

    private boolean hasFailed = false;
    private boolean isRunning = false;
    private boolean hasFinished = false;
    private boolean forceExit = false;

    private JSONObject sendData = null;
    private JSONObject resultData = null;

    private static final Logger logger = LoggerFactory.getLogger(Server.class);

    Server(int portNumber,
           String algorithmName,
           JSONObject algorithmArguments,
           JSONObject data) {
    	
    	try {
			this.serverSocket = new ServerSocket();
			serverSocket.bind(new InetSocketAddress(0));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    	
        this.portNumber = serverSocket.getLocalPort();
        
        
        try {
			this.sendData = assembleSendData(algorithmName, algorithmArguments, data);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    private static JSONObject assembleSendData(String algorithmName,
                                               JSONObject algorithmArguments,
                                               JSONObject data) throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("name", algorithmName);
        obj.put("args", algorithmArguments);
        obj.put("data", data);

        return obj;
    }

    @Override
    public void run() {
        try {
            isRunning = true;
            runImpl();
        } catch (IOException e) {
            logger.error("Communication Server crashed", e);
            e.printStackTrace();
            hasFailed = true;
        } catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        isRunning = false;
        hasFinished = true;
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
        // close the input socket, this causes an exception

        try {
            if (this.socket != null)
                this.socket.close();
            if (this.serverSocket != null)
                this.serverSocket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void runImpl() throws IOException, JSONException {
        //this.serverSocket = new ServerSocket(this.portNumber);

        serverSocket.setSoTimeout(10000);
        socket = serverSocket.accept();
    
        if (socket != null) {
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            Message.send(dos, this.sendData);

            DataInputStream dis = new DataInputStream(socket.getInputStream());
            this.resultData = Message.receive(dis);
        }

        if (socket != null && !this.socket.isClosed())
            socket.close();

        serverSocket.close();
    }

    JSONObject getResultData() {
        return this.resultData;
    }
    
    public int getPort() {
    	return portNumber;
    }
}
