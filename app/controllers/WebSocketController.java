package controllers;

import play.mvc.LegacyWebSocket;
import play.mvc.WebSocket;

/**
 * Created by prasad on 12/5/17.
 */
public class WebSocketController {

    private WebSocket.Out<String> outChannel = null;
    private static int numberOfMessages=0;

    public LegacyWebSocket<String> webSocket(){
        System.out.println("Method called -----------");
        return new LegacyWebSocket<String>() {
            @Override
            public void onReady(WebSocket.In<String> in, WebSocket.Out<String> out) {
                    if(outChannel == null)
                        outChannel = out;
                    in.onMessage(message -> listenClientMessage(message));
            }
        };
        //return WebSocket.reject(Results.forbidden());
    }

    public void listenClientMessage(String message){
        System.out.println("Client send :: " + message);
        System.out.println("Sending same message To client back");
        sendMessageToClient(message);
    }

    public void sendMessageToClient(String message){
        if(outChannel !=null){
            if(numberOfMessages >= 10)
                outChannel.close();
            numberOfMessages++;
            outChannel.write(message);
        }
    }
}
