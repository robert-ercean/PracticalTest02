package ro.pub.cs.systems.eim.practicaltest02;

import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
public class ClientThread extends Thread {
    public final String address;
    public final int port;
    public final String word;
    public final String minLetters;
    public final TextView resultView;

    private Socket socket;

    public ClientThread(String address, int port, String word, String minLetters, TextView resultView) {
        this.address = address;
        this.port = port;
        this.word = word;
        this.minLetters = minLetters;
        this.resultView = resultView;
    }

    @Override
    public void run() {
        try {
            socket = new Socket(this.address, this.port);

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);

            printWriter.println(word);
            printWriter.flush();
            printWriter.println(minLetters);
            printWriter.flush();

            String result = bufferedReader.readLine();

            while (result != null) {
                final String info = result;
                resultView.post(() -> resultView.setText(info));
                result = bufferedReader.readLine();
            }

        } catch (IOException e) {
            Log.e(Constants.TAG, "[CLIENT THREAD] Exception message : " + e.getMessage());
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    Log.e(Constants.TAG, "[CLIENT THREAD] Exception message : " + e.getMessage());
                }
            }
        }
    }
}