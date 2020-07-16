package pyext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * Python extension communication message
 *
 * A message consists of a header and the content
 * * The header is 4 bytes representing the length of the message
 * * The message is a byte stream encoded as JSON
 */
class Message {
    private static final Logger logger = LoggerFactory.getLogger(Message.class);

    /**
     * Wrap data in message and send
     *
     * @param dos sending stream
     * @param data data to send
     * @throws IOException
     */
    static void send(DataOutputStream dos, JSONObject data) throws IOException {
        final byte[] msgBuffer = (data.toString()+"\n").getBytes(StandardCharsets.UTF_8);

        // send
        final int msgLen = msgBuffer.length;
        logger.info("send {} bytes", msgLen);

        // write the message header
        ByteBuffer bb = ByteBuffer.allocate(4);
        dos.write(bb.putInt(msgLen).array());

        // write the message content
        dos.write(msgBuffer);
    }

    /**
     * Read a message
     *
     * @param dis stream to read from
     * @return the encoded data
     * @throws IOException
     * @throws JSONException 
     */
    static JSONObject receive(DataInputStream dis) throws IOException, JSONException {
        // read message header
        final int msgLen = dis.readInt();
        logger.info("expect msg of size {}", msgLen);

        // read the message content
        final byte[] msgBuffer = new byte[msgLen];
        dis.readFully(msgBuffer, 0, msgLen);

        String s = new String(msgBuffer, StandardCharsets.UTF_8);
        JSONTokener tokener = new JSONTokener(s);

        return new JSONObject(tokener);
    }
}

