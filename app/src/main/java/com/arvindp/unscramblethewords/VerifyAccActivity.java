package com.arvindp.unscramblethewords;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class VerifyAccActivity extends AppCompatActivity {

    EditText otp;
    MaterialButton btnGenerateOTP, btnVerify;
    FirebaseAuth mAuth;
    String Email, Username, Password, Phno, verificationID;
    String encryptedEmail, encryptedUsername, encryptedPassword, encryptedHighscore, encryptedPhno;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.cinnamon));
        setContentView(R.layout.activity_verify_acc);

        Intent intent = getIntent();
        Phno = intent.getStringExtra("phoneNumber");
        Email = intent.getStringExtra("email");
        Username = intent.getStringExtra("username");
        Password = intent.getStringExtra("password");

        otp = findViewById(R.id.otp);
        btnGenerateOTP = findViewById(R.id.btnGenerateOTP);
        btnVerify =findViewById(R.id.btnVerify);
        mAuth = FirebaseAuth.getInstance();

        btnGenerateOTP.setOnClickListener(view -> {

            sendVerificationCode(Phno);
        });

        btnVerify.setEnabled(false);
        btnVerify.setOnClickListener(view -> {
            if(TextUtils.isEmpty(otp.getText().toString())) {
                Toast.makeText(VerifyAccActivity.this, "Please enter the OTP...", Toast.LENGTH_SHORT).show();
            }
            else
                verifyCode(otp.getText().toString());
        });
    }

    private void sendVerificationCode(String phoneNumber) {
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth)
                                    .setPhoneNumber("+91" + phoneNumber)
                                    .setTimeout(60L, TimeUnit.SECONDS)
                                    .setActivity(this)
                                    .setCallbacks(mCallbacks)
                                    .build();

        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private final PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks =
            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

            final String code = phoneAuthCredential.getSmsCode();
            if(code != null) {
                verifyCode(code);
            }
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException firebaseException) {

            Toast.makeText(VerifyAccActivity.this, "Could not verify account...", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken token)
        {
            super.onCodeSent(s, token);
            verificationID = s;

            Toast.makeText(VerifyAccActivity.this, "An OTP has been sent to your registered phone number.", Toast.LENGTH_SHORT).show();
            btnVerify.setEnabled(true);
        }

    };

    private void verifyCode(String Code) {

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationID, Code);
        signInByCredentials(credential);
    }

    private void signInByCredentials(PhoneAuthCredential phoneAuthCredential) {

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithCredential(phoneAuthCredential)
                .addOnCompleteListener((OnCompleteListener<AuthResult>) task -> {

                    if(task.isSuccessful()) {
                        Toast.makeText(VerifyAccActivity.this, "Your account has been verified!", Toast.LENGTH_SHORT).show();

                        try {
                            encryptedEmail = AES.encrypt(Email);
                            encryptedUsername = AES.encrypt(Username);
                            encryptedPassword = AES.encrypt(Password);
                            encryptedHighscore = AES.encrypt("0");
                            encryptedPhno = AES.encrypt(Password);

                        } catch (Exception exception) {
                            exception.printStackTrace();
                        }

                        Map<String, Object> user = new HashMap<>();
                        user.put("email", encryptedEmail);
                        user.put("username", encryptedUsername);
                        user.put("password", encryptedPassword);
                        user.put("highscore", encryptedHighscore);
                        user.put("phone number", encryptedPhno);

                        firestore.collection("users")
                                .add(user)
                                .addOnSuccessListener(docRef -> Toast.makeText(VerifyAccActivity.this, "Registration Successful!", Toast.LENGTH_SHORT).show())
                                .addOnFailureListener(exception -> {
                                    Toast.makeText(VerifyAccActivity.this, "Registration Failed...", Toast.LENGTH_SHORT).show();
                                    exception.printStackTrace();
                                });
                    }
                    else {
                        Toast.makeText(VerifyAccActivity.this, "OTP provided is incorrect...", Toast.LENGTH_SHORT).show();
                    }
                });

        openMainActivity();
    }

    public void openMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}