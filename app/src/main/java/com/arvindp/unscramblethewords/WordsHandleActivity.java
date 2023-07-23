package com.arvindp.unscramblethewords;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class WordsHandleActivity extends AppCompatActivity {

    private ArrayList<ExampleItem2> mExampleList;
    List documentsList = new ArrayList<>(), documentsList2;
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private ExampleAdapter2 mAdapter;
    MaterialButton backBtn, addBtn;
    EditText text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.cinnamon));
        setContentView(R.layout.activity_words_handle);

        mExampleList = new ArrayList<>();

        firestore.collection("word")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        QuerySnapshot querySnapshot = task.getResult();
                        documentsList = querySnapshot.getDocuments();

                        for (int i=0; i<documentsList.size(); i++) {

                            DocumentSnapshot d = (DocumentSnapshot) documentsList.get(i);
                            String word = AES.decrypt(Objects.requireNonNull(d.getString("word")));
                            mExampleList.add(new ExampleItem2(word));
                        }

                        buildRecyclerView();
                    }
                });

        text = findViewById(R.id.newWord);
        addBtn = findViewById(R.id.btnAdd);
        addBtn.setOnClickListener(view -> {
            String newWord = text.getText().toString();
            text.setText("");

            if (!newWord.matches("[a-z]{7}")) {
                Toast.makeText(WordsHandleActivity.this, "Invalid word syntax! (Must have 7 letters | Must be lowercase)", Toast.LENGTH_SHORT).show();
            }
            else
            {
                String encryptedNewWord = AES.encrypt(newWord);

                firestore.collection("word")
                        .whereEqualTo("word", encryptedNewWord)
                        .get()
                        .addOnCompleteListener(task -> {

                            QuerySnapshot querySnapshot = task.getResult();
                            documentsList2 = new ArrayList<>();
                            documentsList2 = querySnapshot.getDocuments();

                            if (documentsList2.size() != 0) {
                                Toast.makeText(WordsHandleActivity.this, "\"" + newWord + "\" already exists!", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Map<String, Object> word = new HashMap<>();
                                word.put("word", encryptedNewWord);

                                firestore.collection("word")
                                        .add(word)
                                        .addOnSuccessListener(docRef -> Toast.makeText(WordsHandleActivity.this, newWord + " has been added successfully!", Toast.LENGTH_SHORT).show())
                                        .addOnFailureListener(exception -> {
                                            Toast.makeText(WordsHandleActivity.this, "\"" + newWord + "\" could not be added...", Toast.LENGTH_SHORT).show();
                                            exception.printStackTrace();
                                        });

                                mExampleList.add(new ExampleItem2(newWord));
                                buildRecyclerView();
                            }
                        });
            }
        });

        backBtn = findViewById(R.id.btnExit);
        backBtn.setOnClickListener(view -> openAdminActivity());
    }

    public void openAdminActivity() {
        Intent intent = new Intent(this, AdminActivity.class);
        startActivity(intent);
    }

    public void removeItem(int position) {

        if (mExampleList.size() > 2) {
            firestore.collection("word")
                    .whereEqualTo("word", AES.encrypt(mExampleList.get(position).getText()))
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {

                            if (task.isSuccessful() && !task.getResult().isEmpty()) {

                                DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                                String documentID = documentSnapshot.getId();

                                firestore.collection("word")
                                        .document(documentID)
                                        .delete()
                                        .addOnSuccessListener(unused -> Toast.makeText(WordsHandleActivity.this, "Deletion successful!", Toast.LENGTH_SHORT).show())
                                        .addOnFailureListener(exception -> {
                                            Toast.makeText(WordsHandleActivity.this, "Deletion unsuccessful...", Toast.LENGTH_SHORT).show();
                                            exception.printStackTrace();
                                        });
                            } else {
                                Toast.makeText(WordsHandleActivity.this, "Deletion unsuccessful...", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

            mExampleList.remove(position);
            mAdapter.notifyItemRemoved(position);
        }
        else {
            Toast.makeText(WordsHandleActivity.this, "Cannot delete when there are two words left!", Toast.LENGTH_SHORT).show();
        }

    }

    public void buildRecyclerView() {
        RecyclerView mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new ExampleAdapter2(mExampleList);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(this::removeItem);
    }
}