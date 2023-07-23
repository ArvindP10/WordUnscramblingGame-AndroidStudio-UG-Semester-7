package com.arvindp.unscramblethewords;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class MainMenuActivity extends AppCompatActivity {

    Integer score = 0;
    String Email, documentID;
    TextView scoreTextView;

    @SuppressLint("SetTextI18n")
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.cinnamon));
        setContentView(R.layout.activity_main_menu);

        Intent intent = getIntent();
        score = intent.getIntExtra("high_score", 0);
        Email = intent.getStringExtra("current_email");
        documentID = intent.getStringExtra("documentID");

        scoreTextView = findViewById(R.id.subtext2);
        scoreTextView.setText("Highest streak: " + score);

        Button buttonPlay = findViewById(R.id.btnPlay);
        buttonPlay.setOnClickListener(view -> openPlayActivity());

        Button buttonSignOut = findViewById(R.id.btnExit);
        buttonSignOut.setOnClickListener(view -> openMainActivity());
    }

    public void openPlayActivity()
    {
        Intent intent = new Intent(this, PlayActivity.class);
        intent.putExtra("high_score", score);
        intent.putExtra("current_email", Email);
        intent.putExtra("documentID", documentID);
        startActivity(intent);
    }

    public void openMainActivity()
    {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}