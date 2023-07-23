package com.arvindp.unscramblethewords;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UsersHandleActivity extends AppCompatActivity {

    private ArrayList<ExampleItem> mExampleList;
    List documentsList = new ArrayList<>();
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private ExampleAdapter mAdapter;
    MaterialButton backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.cinnamon));
        setContentView(R.layout.activity_users_handle);

        mExampleList = new ArrayList<>();

        firestore.collection("users")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        QuerySnapshot querySnapshot = task.getResult();
                        documentsList = querySnapshot.getDocuments();

                        for (int i=0; i<documentsList.size(); i++) {

                            DocumentSnapshot documentSnapshot = (DocumentSnapshot) documentsList.get(i);
                            String username = AES.decrypt(Objects.requireNonNull(documentSnapshot.getString("username")));
                            String email = AES.decrypt(Objects.requireNonNull(documentSnapshot.getString("email")));

                            assert email != null;
                            if (!email.contains("@admin."))
                                mExampleList.add(new ExampleItem(R.drawable.ic_baseline_person, username, email));
                        }

                        buildRecyclerView();
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

        firestore.collection("users")
                .document(Objects.requireNonNull(AES.encrypt(mExampleList.get(position).getText2())))
                .delete()
                .addOnSuccessListener(unused -> Toast.makeText(UsersHandleActivity.this,"Deletion successful!",Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> {
                    Toast.makeText(UsersHandleActivity.this,"Deletion unsuccessful...",Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                });

        mExampleList.remove(position);
        mAdapter.notifyItemRemoved(position);
    }

    public void buildRecyclerView() {
        RecyclerView mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new ExampleAdapter(mExampleList);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(this::removeItem);
    }
}
