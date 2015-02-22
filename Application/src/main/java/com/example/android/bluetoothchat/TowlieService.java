package com.example.android.bluetoothchat;

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

    @Override
    public void onCreate() {
        URI uri;

        try {
            uri = new URI("ws://192.168.192.244:8080");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        mWebSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                connected = true;
            }

            @Override
            public void onMessage(String s) {
                final String message = s;
            }

            @Override
            public void onClose(int i, String s, boolean b) {
                onClose.run();
            }

            @Override
            public void onError(Exception e) {
                onError.run();
            }
        };

        mWebSocketClient.connect();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //TODO do something useful
        return Service.START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        //TODO for communication return IBinder implementation
        return null;
    }

    @Override
    public void onDestroy() {
        if (mWebSocketClient != null) {
            mWebSocketClient.close();
            mWebSocketClient = null;
        }
    }
}
