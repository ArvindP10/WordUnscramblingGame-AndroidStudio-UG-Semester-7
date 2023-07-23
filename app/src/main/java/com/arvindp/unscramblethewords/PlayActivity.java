package com.arvindp.unscramblethewords;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class PlayActivity extends AppCompatActivity {

    private long mTimeLeftInMillis = 36000;
    private CountDownTimer mCountDownTimer;
    Integer highscore = 0, score = 0;
    String Email, ans, encryptedHighscore, documentID;
    DocumentSnapshot document;
    List documentsList;
    MaterialButton genWordBtn, answerCheckBtn, exitBtn;
    TextView timerTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.cinnamon));
        setContentView(R.layout.activity_play);

        Intent intent = getIntent();
        highscore = intent.getIntExtra("high_score", 0);
        Email = intent.getStringExtra("current_email");
        documentID = intent.getStringExtra("documentID");

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        EditText answer = findViewById(R.id.answer);
        timerTextView = findViewById(R.id.timer);

        exitBtn = findViewById(R.id.btnExit);
        exitBtn.setEnabled(false);

        firestore.collection("word")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        QuerySnapshot querySnapshot = task.getResult();

                        documentsList = new ArrayList<>();

                        documentsList = querySnapshot.getDocuments();
                        TextView scrambledWord = findViewById(R.id.scrambledword);
                        answerCheckBtn = findViewById(R.id.btnAnswerCheck);
                        answerCheckBtn.setEnabled(false);

                        genWordBtn = findViewById(R.id.btnGenWord);
                        genWordBtn.setOnClickListener(view -> {

                            exitBtn.setEnabled(true);
                            answerCheckBtn.setEnabled(true);
                            genWordBtn.setEnabled(false);

                            Random rand = new Random();
                            if (!documentsList.isEmpty()) {

                                int index = rand.nextInt(documentsList.size());
                                document = (DocumentSnapshot) documentsList.get(index);
                                ans = AES.decrypt(document.getString("word"));
                                assert ans != null;

                                LinkedList<String> list = new LinkedList<>();
                                String[] chars = ans.split("");

                                Collections.addAll(list, chars);

                                // To ensure the scrambled word is not the same as the actual word
                                LinkedList<String> temp = new LinkedList<>(list);
                                while (temp.equals(list)) {
                                    Collections.shuffle(list);
                                }

                                StringBuilder scrWord = new StringBuilder();
                                for (int i=0; i<list.size(); i++) {
                                    scrWord.append(list.get(i));
                                }

                                scrambledWord.setText(scrWord.toString());

                                mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
                                    @Override
                                    public void onTick(long millisUntilFinished) {
                                        mTimeLeftInMillis = millisUntilFinished;

                                        int minutes = (int) (mTimeLeftInMillis / 1000) / 60;
                                        int seconds = (int) (mTimeLeftInMillis / 1000) % 60;

                                        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
                                        timerTextView.setText(timeLeftFormatted);
                                    }

                                    @Override
                                    public void onFinish() {
                                        Toast.makeText(PlayActivity.this, "Time's up! The word was \""+ ans + "\". Final number of words unscrambled: " + score, Toast.LENGTH_SHORT).show();

                                        if (score > highscore) {
                                            highscore = score;
                                            encryptedHighscore = AES.encrypt(String.valueOf(highscore));

                                            firestore.collection("users")
                                                    .document(documentID)
                                                    .update("highscore", encryptedHighscore);
                                        }

                                        openMainMenuActivity();
                                    }
                                }.start();
                            } else {
                                Toast.makeText(PlayActivity.this, "Could not fetch a word...", Toast.LENGTH_SHORT).show();
                            }
                        });

                        answerCheckBtn.setOnClickListener(view -> {

                            String text = answer.getText().toString();

                            if (!text.isEmpty()) {

                                mCountDownTimer.cancel();
                                answerCheckBtn.setEnabled(false);
                                genWordBtn.setEnabled(true);

                                answer.setText("");

                                if (!text.equals(ans)) {
                                    Toast.makeText(PlayActivity.this, "That was not the right word! The word was \"" + ans + "\". Final number of words unscrambled: " + score, Toast.LENGTH_SHORT).show();

                                    if (score > highscore) {
                                        highscore = score;

                                        encryptedHighscore = AES.encrypt(highscore.toString());

                                        firestore.collection("users")
                                                .document(documentID)
                                                .update("highscore", encryptedHighscore);
                                    }
                                    openMainMenuActivity();

                                } else {
                                    mTimeLeftInMillis = 36000;
                                    score += 1;
                                    Toast.makeText(PlayActivity.this, "Correct word! " + score + " word(s) unscrambled", Toast.LENGTH_SHORT).show();
                                }
                            }
                            else {
                                Toast.makeText(PlayActivity.this, "Make a guess first!", Toast.LENGTH_SHORT).show();
                            }
                        });

                        exitBtn.setOnClickListener(view -> {
                            mCountDownTimer.cancel();
                            openMainMenuActivity();
                        });
                    }
                }).addOnFailureListener(exception -> Log.d("this", exception.getMessage()));
    }

    public void openMainMenuActivity() {
        Intent intent = new Intent(this, MainMenuActivity.class);
        intent.putExtra("high_score", highscore);
        intent.putExtra("current_email", Email);
        intent.putExtra("documentID", documentID);
        startActivity(intent);
    }
}