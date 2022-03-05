package com.naveejr.chatclient;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.naveejr.chatclient.util.AppSettings;
import com.naveejr.chatclient.util.NetworkUtil;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.TreeMap;

import io.socket.client.Manager;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import io.socket.engineio.client.Transport;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private EditText txtHostname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnLogin = findViewById(R.id.btn_login);
        txtHostname = findViewById(R.id.txt_hostname);
        EditText txtUsername = findViewById(R.id.txt_username);
        EditText txtPassword = findViewById(R.id.txt_password);

        btnLogin.setOnClickListener(view -> {
            try {
                URL url = new URL("https://" + txtHostname.getText().toString());
                Socket socket = NetworkUtil.getSocket(url.toString(), txtUsername.getText().toString(), txtPassword.getText().toString());
                socket.on(Socket.EVENT_CONNECT, args -> {
                    Log.d(TAG, "Connected");
                    AppSettings.setHostname(txtHostname.getText().toString());
                    socket.emit("send_message", "Hello there");

                });

/*
                socket.io().on(Manager.EVENT_TRANSPORT, new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        Transport transport = (Transport) args[0];
                        transport.on(Transport.EVENT_REQUEST_HEADERS, new Emitter.Listener() {
                            @Override
                            public void call(Object... args) {
                                Log.d(TAG, args.length + "");
                                ((TreeMap) args[0]).put("Authorization", "Basic " + "cmFqZWV2YW46ODY1ODY1");

                                //@SuppressWarnings("unchecked")
                                // Map<String, String> headers = (Map<String, String>) args[0];
                                //headers.put("authorization", "Basic " + "cmFqZWV2YW46ODY1ODY1");
                            }
                        });
                    }
                });
*/


                socket.on(Socket.EVENT_CONNECT_ERROR, args -> Log.d(TAG, "Connection error"));
                socket.connect();

            } catch (MalformedURLException e) {
                Toast.makeText(MainActivity.this, R.string.invalid_url, Toast.LENGTH_LONG).show();
                txtHostname.setError(getString(R.string.invalid_url));
            }
        });
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        txtHostname.setText(AppSettings.getHostname());
    }
}