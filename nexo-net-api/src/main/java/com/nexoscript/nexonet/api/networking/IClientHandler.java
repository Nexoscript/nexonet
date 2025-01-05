package com.nexoscript.nexonet.api.networking;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;

public interface IClientHandler {
    String getId();
    Socket getClientSocket();
    boolean isAuth();
    void setAuth(boolean auth);
    IServer getServer();
    BufferedReader getReader();
    PrintWriter getWriter();
}
