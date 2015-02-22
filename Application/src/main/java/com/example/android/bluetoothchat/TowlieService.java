package com.example.android.bluetoothchat;

import java.util.concurrent.Future;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by twwildey on 2/21/15.
 */
public class TowlieService extends Service {
    private WebSocketClient mWebSocketClient;
    private boolean connected;

    public void connect(String hostname, String port) {
        try {
            URI uri = new URI("ws://" + hostname + ":" + port);

            mWebSocketClient = new WebSocketClient(uri) {
                @Override
                public void onOpen(ServerHandshake serverHandshake) {
                    connected = true;
                }

                @Override
                public void onMessage(String s) {
                }

                @Override
                public void onClose(int i, String s, boolean b) {
                }

                @Override
                public void onError(Exception e) {
                }
            };

            mWebSocketClient.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (mWebSocketClient == null) {
            String hostname = intent.getStringExtra("hostname");
            String port = intent.getStringExtra("port");
            connect(hostname, port);
            return Service.START_NOT_STICKY;
        }

        if (!connected) {
            return 0;
        }

        String action = intent.getStringExtra("action");
        switch(action) {
            case "push":
                String message = intent.getStringExtra("message");
                mWebSocketClient.send("{push:'" + message + "'}");
                break;
            case "clear":
                mWebSocketClient.send("{clear:null}");
                break;
            default:
                return 0;
        }

        return Service.START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        if (mWebSocketClient != null) {
            mWebSocketClient.close();
            mWebSocketClient = null;
            connected = false;
        }
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }
}
