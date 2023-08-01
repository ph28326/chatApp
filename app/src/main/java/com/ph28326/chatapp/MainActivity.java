package com.ph28326.chatapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private Button btnSend;
    private EditText edtMessage;
    private List<String> list = new ArrayList<>();
    private ChatAdapter adapter;
    private String username;
    private final String URL_SERVER = "http://192.168.1.254:3000";
    private Socket socket;
//222
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        username = intent.getStringExtra("name");
        Toast.makeText(this, "Hello " + username, Toast.LENGTH_SHORT).show();

        btnSend = findViewById(R.id.btnSend);
        edtMessage = findViewById(R.id.edtMessage);
        recyclerView = findViewById(R.id.rcv);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new ChatAdapter(this, list);

        connectSocket();
        helloUser();

        socket.on("receiver_message", setOnNewMessage);

        recyclerView.setAdapter(adapter);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                socket.emit("send_message", edtMessage.getText().toString());
                edtMessage.setText("");
            }
        });
    }

    void connectSocket() {
        try {
            socket = IO.socket(URL_SERVER);
            socket.connect();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private Emitter.Listener setOnNewMessage = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String message = data.optString("data");
                    adapter.addItem(message);
                }
            });
        }
    };

    public void helloUser() {
        socket.emit("user_login", username);
    }

}