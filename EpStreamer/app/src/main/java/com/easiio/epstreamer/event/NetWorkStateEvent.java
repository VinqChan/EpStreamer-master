package com.easiio.epstreamer.event;

public class NetWorkStateEvent {
    public boolean isConnected;

    public NetWorkStateEvent(boolean isConnected) {
        this.isConnected = isConnected;
    }
}
