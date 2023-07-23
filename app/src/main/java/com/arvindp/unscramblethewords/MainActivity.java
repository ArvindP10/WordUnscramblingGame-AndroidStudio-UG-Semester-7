package com.arvindp.unscramblethewords;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.cinnamon));
        setContentView(R.layout.activity_main);

        Button buttonReg = findViewById(R.id.btnRegister);
        buttonReg.setOnClickListener(view -> openRegisterActivity());

        Button buttonSignIn = findViewById(R.id.btnSignIn);
        buttonSignIn.setOnClickListener(view -> openSignInActivity());
    }

    public void openRegisterActivity() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    public void openSignInActivity() {
        Intent intent = new Intent(this, SignInActivity.class);
        startActivity(intent);
    }
}