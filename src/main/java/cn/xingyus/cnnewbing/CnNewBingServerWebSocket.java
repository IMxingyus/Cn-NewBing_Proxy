package cn.xingyus.cnnewbing;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoWSD;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class CnNewBingServerWebSocket extends NanoWSD.WebSocket {
    CnNewBingClientWebSocket cnNewBingClientWebSocket;
    LinkedList<String> messList = new LinkedList<>();
    ScheduledExecutorService scheduledExecutorService;
    ScheduledFuture<?> task;

    public CnNewBingServerWebSocket(NanoHTTPD.IHTTPSession handshakeRequest, ScheduledExecutorService scheduledExecutorService) {
        super(handshakeRequest);
        URI url;
        try {
            url = new URI("wss://sydney.bing.com/sydney/ChatHub");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);//这个异常这辈子都不会出的
        }
        this.scheduledExecutorService = scheduledExecutorService;
        cnNewBingClientWebSocket = new CnNewBingClientWebSocket(url,this,messList);
    }

    @Override
    protected void onOpen() {
        task = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            if (!isOpen()) {
                task.cancel(false);
                return;
            }
            try {
                ping(new byte[1]);
            } catch (IOException e) {
                task.cancel(false);
            }
        },2,2, TimeUnit.SECONDS);
        cnNewBingClientWebSocket.connect();
    }

    @Override
    protected void onClose(NanoWSD.WebSocketFrame.CloseCode code, String reason, boolean initiatedByRemote) {
        cnNewBingClientWebSocket.close();
    }

    @Override
    protected void onMessage(NanoWSD.WebSocketFrame message) {
        if(cnNewBingClientWebSocket.isOpen()){
            cnNewBingClientWebSocket.send(message.getTextPayload());
        }else {
            messList.addLast(message.getTextPayload());
        }
    }

    @Override
    protected void onPong(NanoWSD.WebSocketFrame pong) {

    }

    @Override
    protected void onException(IOException exception) {
        cnNewBingClientWebSocket.close();
    }
}
