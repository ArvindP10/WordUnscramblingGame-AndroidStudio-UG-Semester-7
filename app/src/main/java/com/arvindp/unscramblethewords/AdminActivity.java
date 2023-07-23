package com.arvindp.unscramblethewords;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class AdminActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.cinnamon));
        setContentView(R.layout.activity_admin);

        Button buttonUsersHandle = findViewById(R.id.btnUsersHandle);
        buttonUsersHandle.setOnClickListener(view -> openUsersHandleActivity());

        Button buttonWordsHandle = findViewById(R.id.btnWordsHandle);
        buttonWordsHandle.setOnClickListener(view -> openWordsHandleActivity());

        Button buttonExit = findViewById(R.id.btnExit);
        buttonExit.setOnClickListener(view -> openMainActivity());
    }

    public void openUsersHandleActivity() {
        Intent intent = new Intent(this, UsersHandleActivity.class);
        startActivity(intent);
    }

    public void openWordsHandleActivity() {
        Intent intent = new Intent(this, WordsHandleActivity.class);
        startActivity(intent);
    }

    public void openMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}