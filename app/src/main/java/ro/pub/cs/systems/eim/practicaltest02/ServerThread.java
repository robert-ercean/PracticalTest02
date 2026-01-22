package ro.pub.cs.systems.eim.practicaltest02;

import android.util.Log;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class ServerThread extends Thread {
    public ServerSocket serverSocket = null;


    // Port -> Port to which clients will connect to the server
    public ServerThread(int port) {
        try {
            this.serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            Log.e(Constants.TAG, "Exception occurred " + e.getMessage());
        }

    }

    @Override
    public void run() {
        try {
            while(!Thread.currentThread().isInterrupted()) {
                Log.i(Constants.TAG, "[SERVER_THREAD] Waiting for a client connect request...");
                Socket socket = serverSocket.accept();
                Log.i(Constants.TAG, "[SERVER_THREAD] Client request received from " + socket.getInetAddress() + ":" + socket.getLocalPort());
                CommunicationThread communicationThread = new CommunicationThread(this, socket);
                communicationThread.start();
            }
        } catch (IOException e) {
            Log.e(Constants.TAG, "Exception occurred " + e.getMessage());
        }
    }

    public void stopThread() {
        interrupt();
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                Log.e(Constants.TAG, "Exception occurred " + e.getMessage());
            }
        }
    }
}
