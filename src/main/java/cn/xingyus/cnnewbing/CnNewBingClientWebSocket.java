package cn.xingyus.cnnewbing;

import fi.iki.elonen.NanoWSD;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.io.IOException;
import java.net.URI;
import java.util.LinkedList;

public class CnNewBingClientWebSocket extends WebSocketClient {
    CnNewBingServerWebSocket cnNewBingServerWebSocket;
    LinkedList<String> messList;
    public CnNewBingClientWebSocket(URI serverUri,CnNewBingServerWebSocket cnNewBingServerWebSocket,LinkedList<String> messList) {
        super(serverUri);
        this.cnNewBingServerWebSocket = cnNewBingServerWebSocket;
        this.messList = messList;
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        for (String s : messList) {
            send(s);
        }
    }


    @Override
    public void onMessage(String message) {
        try {
            cnNewBingServerWebSocket.send(message);
        } catch (IOException e) {
            close();
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        NanoWSD.WebSocketFrame.CloseCode rcode = NanoWSD.WebSocketFrame.CloseCode.find(code);
        if(rcode==null){
            rcode = NanoWSD.WebSocketFrame.CloseCode.NormalClosure;
        }
        try {
            cnNewBingServerWebSocket.close(rcode,reason,false);
        } catch (IOException e) {

        }
    }

    @Override
    public void onError(Exception ex) {
        close();
        try {
            String errorMessage = "{\"type\": 2,\"result\":{\"value\":\"Error\",\"message\":\"代理服务器连接到bing聊天时发生错误"+ex+"\"}}";
            cnNewBingServerWebSocket.send(errorMessage);
            cnNewBingServerWebSocket.close(NanoWSD.WebSocketFrame.CloseCode.NormalClosure,"error",false);
        } catch (IOException e) {

        }

    }
}
