package com.ryanxli.sqlite_example;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.peak.salut.Salut;

public class StartActivity extends AppCompatActivity implements View.OnClickListener {

    public static final int IDENTITY_DEFAULT = 0;
    public static final int IDENTITY_HOST = 1;
    public static final int IDENTITY_CLIENT = 2;
    public static final String TAG = "DbSync";
    public Button hostButton;
    public Button clientButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        hostButton = (Button) findViewById(R.id.host_button);
        clientButton = (Button) findViewById(R.id.client_button);

        hostButton.setOnClickListener(this);
        clientButton.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {

        if(!Salut.isWiFiEnabled(getApplicationContext())) {
            Toast.makeText(getApplicationContext(), "Please enable WiFi first.", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(getApplicationContext(), Main_Screen.class);

        if (v.getId() == R.id.host_button)
        {
            intent.putExtra("Identity", IDENTITY_HOST);
            startActivity(intent);
            finish();
        }
        else //if (v.getId() == R.id.client_button)
        {
            intent.putExtra("Identity", IDENTITY_CLIENT);
            startActivity(intent);
            finish();
        }

    }
}
