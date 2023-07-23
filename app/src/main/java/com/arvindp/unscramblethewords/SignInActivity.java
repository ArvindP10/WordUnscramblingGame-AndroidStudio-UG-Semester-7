package com.arvindp.unscramblethewords;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SignInActivity extends AppCompatActivity {

    EditText email, password;
    MaterialButton signInBtn, prevBtn;
    FirebaseFirestore firestore;
    Integer score = 0;
    String encryptedEmail, decryptedPassword, decryptedUsername;
    String Email, emailRegex, Password, documentID;
    List documentsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.cinnamon));
        setContentView(R.layout.activity_sign_in);

        firestore = FirebaseFirestore.getInstance();
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        password.setTransformationMethod(PasswordTransformationMethod.getInstance());

        emailRegex = "^(.+)@(.+)$";

        signInBtn = findViewById(R.id.btnSignIn);
        signInBtn.setOnClickListener(view -> {

            Email = email.getText().toString();
            Password = password.getText().toString();

            if (!Email.isEmpty() && !Password.isEmpty()) {
                if (!Email.matches(emailRegex)) {
                    if (!Email.matches(emailRegex))
                        Toast.makeText(SignInActivity.this, "Invalid email...", Toast.LENGTH_SHORT).show();
                } else {

                    encryptedEmail = AES.encrypt(Email);
                    firestore.collection("users")
                            .whereEqualTo("email", encryptedEmail)
                            .get()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {

                                    QuerySnapshot querySnapshot = task.getResult();
                                    documentsList = new ArrayList<>();
                                    documentsList = querySnapshot.getDocuments();

                                    if (!documentsList.isEmpty()) {

                                        DocumentSnapshot document = (DocumentSnapshot) documentsList.get(0);
                                        documentID = document.getId();
                                        decryptedPassword = AES.decrypt(document.getString("password"));

                                        assert decryptedPassword != null;
                                        if (decryptedPassword.equals(Password))
                                        {
                                            decryptedUsername = AES.decrypt(document.getString("username"));
                                            if (Email.contains("@admin.com")) {

                                                Toast.makeText(SignInActivity.this, "Credentials verified! Welcome Admin " + decryptedUsername + "!", Toast.LENGTH_SHORT).show();
                                                openAdminActivity();

                                            } else {

                                                Toast.makeText(SignInActivity.this, "Credentials verified! Welcome " + decryptedUsername + "!", Toast.LENGTH_SHORT).show();
                                                score = Integer.parseInt(Objects.requireNonNull(AES.decrypt(document.getString("highscore"))));
                                                openMainMenuActivity();
                                            }
                                        } else
                                            Toast.makeText(SignInActivity.this, "Incorrect password...", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(SignInActivity.this, "Could not identify account with email: " + Email, Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(SignInActivity.this, "Sign in failed with: " + task.getException(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
            else {
                Toast.makeText(SignInActivity.this,"One or more fields is empty...", Toast.LENGTH_SHORT).show();
            }
        });

        prevBtn = findViewById(R.id.btnPrev);
        prevBtn.setOnClickListener(view -> openMainActivity());
    }

    public void openMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void openAdminActivity() {
        Intent intent = new Intent(this, AdminActivity.class);
        startActivity(intent);
    }

    public void openMainMenuActivity() {
        Intent intent = new Intent(this, MainMenuActivity.class);
        intent.putExtra("high_score", score);
        intent.putExtra("current_email", Email);
        intent.putExtra("documentID", documentID);
        startActivity(intent);
    }
}