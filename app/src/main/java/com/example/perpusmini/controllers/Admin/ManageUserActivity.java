package com.example.perpusmini.controllers.Admin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.Button;

import com.example.perpusmini.R;
import com.example.perpusmini.enums.CollectionHelper;
import com.example.perpusmini.enums.Role;
import com.example.perpusmini.helpers.Helper;
import com.example.perpusmini.models.Book;
import com.example.perpusmini.models.User;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class ManageUserActivity extends AppCompatActivity {

    ProgressDialog progressDialog;
    Query query;
    private RecyclerView recycle;
    private FirebaseFirestore db;
    private ManageUserAdapter adapter;
    private int mode = 0;
    private String key = "";
    private FirebaseAuth firebaseAuth;
    private Helper helper;
    private Role levelAccess = Role.GUEST;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_user);

        FirebaseApp.initializeApp(this);
        helper = new Helper(this);
        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        recycle = findViewById(R.id.recycle);

        getUserLogged();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait !");
        progressDialog.show();
        setupUserAdapter(false);
        progressDialog.cancel();

    }

    private void getUserLogged() {
        levelAccess = (Role) getIntent().getSerializableExtra("LEVEL");
    }


    private void setupUserAdapter(boolean withFilter) {
        query = db.collection(CollectionHelper.user);

        query = query.whereEqualTo("status", 0);

        FirestoreRecyclerOptions options = new FirestoreRecyclerOptions.Builder<User>().setQuery(query, User.class).build();
        adapter = new ManageUserAdapter(this, options);

        adapter.disableActionButton(levelAccess);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recycle.setLayoutManager(mLayoutManager);
        recycle.setAdapter(adapter);
        adapter.startListening();
    }

    public  void activateUser(User model) {
        model.setStatus(1);
        db.collection(CollectionHelper.user).document(model.getEmail()).set(model).addOnSuccessListener(Void -> {
            helper.toastMessage("Buku berhasil diubah !");
//            finish();
        }).addOnFailureListener(Void -> {
            helper.toastMessage("Gagal mngubah status pengguna !");
        });

    }

}