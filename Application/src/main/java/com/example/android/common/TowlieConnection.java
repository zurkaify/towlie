package com.example.android.common;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by twwildey on 2/21/15.
 */
public class TowlieConnection {
    private WebSocketClient mWebSocketClient;
    private boolean connected;
    private Runnable onClose;
    private Runnable onError;

    public TowlieConnection(Runnable onClose, Runnable onError)
    {
        connected = false;
        this.onClose = onClose;
        this.onError = onError;
    }

    public boolean connect(String ipAddress, String port) {
        URI uri;

        try {
            uri = new URI("ws://" + ipAddress + ":" + port);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return false;
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
        return true;
    }

    public void disconnect() {
        if (mWebSocketClient != null) {
            mWebSocketClient.close();
            mWebSocketClient = null;
            connected = false;
        }
    }

    public boolean push(String message) {
        if (!connected) {
            return false;
        }

        mWebSocketClient.send("{push:'" + message + "'}");

        return true;
    }

    public boolean clear() {
        if (!connected) {
            return false;
        }

        mWebSocketClient.send("{clear:null}");

        return true;
    }
}
