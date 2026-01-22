package ro.pub.cs.systems.eim.practicaltest02;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    private EditText serverPortEditText = null;
    private EditText clientAddressEditText = null;
    private EditText clientPortEditText = null;
    private EditText wordEditText = null;
    private EditText minLettersEditText = null;
    private TextView resultTextView = null;

    private ServerThread serverThread = null;

    private final ConnectButtonClickListener  connectButtonClickListener = new ConnectButtonClickListener();

    private class ConnectButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            String serverPort = serverPortEditText.getText().toString();
            if (serverPort.isEmpty()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Server port cannot be empty", Toast.LENGTH_SHORT).show();
            }
            serverThread = new ServerThread(Integer.parseInt(serverPort));
            if (serverThread.serverSocket == null) {
                Log.e(Constants.TAG, "[MAIN ACTIVITY] Could not create server thread");
                return;
            }
            serverThread.start();
            Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Started Server Thread!", Toast.LENGTH_SHORT).show();

        }
    }

    private ClientRequestListener clientRequestListener = new ClientRequestListener();

    private class ClientRequestListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            String clientAddress = clientAddressEditText.getText().toString();
            String clientPort = clientPortEditText.getText().toString();

            if (clientAddress == null || clientPort == null || clientAddress.isEmpty() || clientPort.isEmpty()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Client addr / port cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            if (serverThread == null || !serverThread.isAlive()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Server is dead, cant connect to it", Toast.LENGTH_SHORT).show();
                return;
            }

            String word = wordEditText.getText().toString();
            String minLetters = minLettersEditText.getText().toString();
            if (word == null || minLetters == null || word.isEmpty() || minLetters.isEmpty()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] word / min_letters cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }
            // clear previous output
            resultTextView.setText("");
            ClientThread clientThread = new ClientThread(clientAddress, Integer.parseInt(clientPort), word, minLetters, resultTextView);
            clientThread.start();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(Constants.TAG, "[MAIN ACTIVITY] onCreate() callback called");
        setContentView(R.layout.activity_main);

        Button connectButton = findViewById(R.id.connect_button);
        Button clientRequest = findViewById(R.id.client_request);
        connectButton.setOnClickListener(connectButtonClickListener);
        clientRequest.setOnClickListener(clientRequestListener);

        serverPortEditText = findViewById(R.id.server_port_edit_text);
        clientAddressEditText = findViewById(R.id.client_address_edit_text);
        clientPortEditText = findViewById(R.id.client_port_edit_text);
        resultTextView = findViewById(R.id.result);
        wordEditText = findViewById(R.id.word);
        minLettersEditText = findViewById(R.id.min_letters);
    }
}