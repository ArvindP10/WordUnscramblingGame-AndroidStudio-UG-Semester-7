package com.arvindp.unscramblethewords;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterActivity extends AppCompatActivity {

    EditText email, username, password, phno;
    MaterialButton registerBtn, backBtn;
    FirebaseFirestore firestore;
    String Email, Username, Password, Phno, emailRegex, passwordRegex, phoneNumberRegex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.cinnamon));
        setContentView(R.layout.activity_register);

        firestore = FirebaseFirestore.getInstance();
        email = findViewById(R.id.email);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        phno = findViewById(R.id.phno);

        password.setTransformationMethod(PasswordTransformationMethod.getInstance());

        emailRegex = "^(.+)@(\\S+)$";
        passwordRegex = "^(?=.*[0-9])"
                + "(?=.*[a-z])"
                + "(?=.*[A-Z])"
                + "(?=.*[$%^=])"
                + "(?=\\S+$).{8,20}$";
        phoneNumberRegex = "^[789]\\d{9}$";

        registerBtn = findViewById(R.id.btnRegister);
        registerBtn.setOnClickListener(view -> {

            Email = email.getText().toString();
            Username = username.getText().toString();
            Password = password.getText().toString();
            Phno = phno.getText().toString();

            if (!Email.isEmpty() && !Username.isEmpty() && !Password.isEmpty()) {
                if (!Email.matches(emailRegex) || !Password.matches(passwordRegex) || !Phno.matches(phoneNumberRegex)) {
                   if (!Email.matches(emailRegex))
                       Toast.makeText(RegisterActivity.this,"Invalid email...", Toast.LENGTH_SHORT).show();
                  else if (!Password.matches(passwordRegex))
                       Toast.makeText(RegisterActivity.this,"Password does not meet requirements...", Toast.LENGTH_SHORT).show();
                  else if (!Phno.matches(phoneNumberRegex))
                       Toast.makeText(RegisterActivity.this,"Not a valid phone number format!", Toast.LENGTH_SHORT).show();
                }
                //else if (Email.contains("@admin.")) {
                //    Toast.makeText(RegisterActivity.this,"You cannot register with that email!", Toast.LENGTH_SHORT).show();
                //}
                else {
                    openVerifyAccActivity();
                }
            }
            else {
                Toast.makeText(RegisterActivity.this,"One or more fields is empty...", Toast.LENGTH_SHORT).show();
            }
        });

        backBtn = findViewById(R.id.btnPrev);
        backBtn.setOnClickListener(view -> openMainActivity());
    }

    public void openVerifyAccActivity() {
        Intent intent = new Intent(this, VerifyAccActivity.class);
        intent.putExtra("phoneNumber", Phno);
        intent.putExtra("email", Email);
        intent.putExtra("username", Username);
        intent.putExtra("password", Password);
        startActivity(intent);
    }

    public void openMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}