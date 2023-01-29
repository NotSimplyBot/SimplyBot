package ru.simplykel.simplybot.api;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Server {
    private final String address;
    private final int port;
    private final int reqProtocol;
    private String json;
    private String description;
    private Integer online;
    private Integer max;
    private boolean secureChat;
    private boolean previewChat;
    private Map<String, UUID> samples;
    private JSONArray samplesJSON = new JSONArray();
    private String version;
    private Integer protocol;
    private String favicon;
    private Long ping;
    private String hostAddress;

    public Server(String address, int port, int reqProtocol) {
        this.address = address;
        this.port = port;
        this.reqProtocol = reqProtocol;
    }

    public void connect(int timeout) throws Exception {
        long start = System.currentTimeMillis();
        InetSocketAddress host = new InetSocketAddress(address, port);
        try (Socket socket = new Socket()) {
            socket.connect(host, timeout);
            hostAddress=socket.getInetAddress().getHostAddress();
            DataOutputStream output = new DataOutputStream(socket.getOutputStream());
            DataInputStream input = new DataInputStream(socket.getInputStream());
            byte[] handshakeMessage = createHandshakeMessage(address, port);
            writeVarInt(output, handshakeMessage.length);
            output.write(handshakeMessage);
            output.writeByte(0x01);
            output.writeByte(0x00);
            readVarInt(input);
            int packetId = readVarInt(input);
            if (packetId != 0) throw new IOException();
            int length = readVarInt(input);
            if (length < 1) throw new IOException();
            byte[] in = new byte[length];
            input.readFully(in);
            json = new String(in, StandardCharsets.UTF_8);
        }
//        Main.LOG.info("AMOGUS! "+json.toString());
        parseJson();
        ping = System.currentTimeMillis() - start;
    }

    private void parseJson() {
        JSONObject jo;
        try {
            jo = new JSONObject(json);
        } catch (JSONException e) {
            return;
        }
        JSONObject desc = jo.optJSONObject("description");
        description = desc == null ? jo.optString("description", null) : String.valueOf(desc);
        favicon = jo.optString("favicon", null);
        secureChat = jo.optBoolean("enforcesSecureChat", false);
        previewChat = jo.optBoolean("previewsChat", false);
        if (jo.optJSONObject("players", new JSONObject()).has("online"))
            online = jo.optJSONObject("players").getInt("online");
        if (jo.optJSONObject("players", new JSONObject()).has("max")) max = jo.optJSONObject("players").getInt("max");
        JSONArray array = jo.optJSONObject("players", new JSONObject()).optJSONArray("sample");
        if (array != null) {
            samples = new HashMap<>();
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                if (object == null) continue;
                String name = object.optString("name", null);
                UUID uuid = null;
                try {
                    uuid = UUID.fromString(object.optString("id"));
                } catch (Exception ignored) {
                }
                samples.put(name, uuid);
                JSONObject samplesJSONObject = new JSONObject();
                samplesJSONObject.put("name", name);
                samplesJSONObject.put("uuid", uuid);
                samplesJSON.put(i, samplesJSONObject);
            }
        }
        version = jo.optJSONObject("version", new JSONObject()).optString("name", null);
        if (jo.optJSONObject("version", new JSONObject()).has("protocol"))
            protocol = jo.optJSONObject("version").getInt("protocol");
    }

    private byte[] createHandshakeMessage(String host, int port) throws Exception {

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        DataOutputStream handshake = new DataOutputStream(buffer);
        handshake.writeByte(0x00);
        writeVarInt(handshake, reqProtocol);
        writeString(handshake, host, StandardCharsets.UTF_8);
        handshake.writeShort(port);
        writeVarInt(handshake, 1);

        return buffer.toByteArray();
    }

    private void writeString(DataOutputStream out, String string, Charset charset) throws Exception {
        byte[] bytes = string.getBytes(charset);
        writeVarInt(out, bytes.length);
        out.write(bytes);
    }

    private void writeVarInt(DataOutputStream out, int paramInt) throws Exception {
        while (true) {
            if ((paramInt & 0xFFFFFF80) == 0) {
                out.writeByte(paramInt);
                return;
            }
            out.writeByte(paramInt & 0x7F | 0x80);
            paramInt >>>= 7;
        }
    }

    private int readVarInt(DataInputStream in) throws Exception {
        int i = 0;
        int j = 0;
        while (true) {
            int k = in.readByte();
            i |= (k & 0x7F) << j++ * 7;
            if (j > 5) throw new IllegalArgumentException();
            if ((k & 0x80) != 128) break;
        }
        return i;
    }

    public String getAddress() {
        return address;
    }
    public String getHostAddress() {
        return hostAddress;
    }

    public int getPort() {
        return port;
    }

    public String getJson() {
        return json;
    }

    public String getDescription() {
        return description;
    }

    public Integer getOnline() {
        return online;
    }

    public Integer getMax() {
        return max;
    }

    public Map<String, UUID> getSamples() {
        return samples;
    }
    public JSONArray getSamplesJSON(){return samplesJSON;}

    public String getVersion() {
        return version;
    }

    public Integer getProtocol() {
        return protocol;
    }

    public String getFavicon() {
        return favicon;
    }

    public Long getPing(){return ping;}
    public boolean getSecureChat(){return secureChat;}
    public boolean getPreviewChat(){return previewChat;}
}