package ro.pub.cs.systems.eim.practicaltest02;

import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.util.EntityUtils;

public class CommunicationThread extends Thread {
    public final ServerThread serverThread;
    public final Socket socket;

    public CommunicationThread(ServerThread serverThread, Socket socket) {
        this.serverThread = serverThread;
        this.socket = socket;
    }

    @Override
    public void run () {
        if (socket == null) {
            Log.e(Constants.TAG, "[COMMUNICATION THREAD] SOCKET IS NULL");
        }

        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);

            // get data sent from the client
            String word = bufferedReader.readLine();
            String minLetters = bufferedReader.readLine();

            if (word == null || word.isEmpty() || minLetters == null || minLetters.isEmpty()) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] word / minletters IS NULL / EMPTY");
                return;
            }
            Log.i(Constants.TAG, "[COMMUNICATION THREAD] Data NOT in cache, getting it from webserver");
            HttpClient httpClient = new DefaultHttpClient();
            String pageSourceCode = "";
            String url = Constants.WEB_SERVICE_ADDRESS + word;
            HttpGet httpGet = new HttpGet(url);
            HttpResponse httpGetResponse = httpClient.execute(httpGet);
            HttpEntity httpGetEntity = httpGetResponse.getEntity();

            if (httpGetEntity != null) {
                pageSourceCode = EntityUtils.toString(httpGetEntity);
            }

            JSONObject content = new JSONObject(pageSourceCode);
            JSONArray anagrams_array = content.getJSONArray("all");

            String result = anagrams_array.toString();
            printWriter.println(result);
            printWriter.flush();
        } catch (IOException | JSONException e) {
            Log.e(Constants.TAG, "[COMMUNICATION THREAD]: Exception message: " + e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] Failed to close the socket, msg: " + e.getMessage());
            }
        }
    }
}