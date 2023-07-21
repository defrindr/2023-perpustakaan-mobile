package com.example.perpusmini.controllers.Admin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.perpusmini.BookAdapter;
import com.example.perpusmini.R;
import com.example.perpusmini.enums.CollectionHelper;
import com.example.perpusmini.enums.KategoriBuku;
import com.example.perpusmini.enums.Role;
import com.example.perpusmini.helpers.Helper;
import com.example.perpusmini.models.Book;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DeletedBookActivity extends AppCompatActivity {

    ProgressDialog progressDialog;
    Query query;
    private RecyclerView recycle;
    private FirebaseFirestore db;
    private DeleteBookAdapter adapter;
    private int mode = 0;
    private String key = "";
    private FirebaseAuth firebaseAuth;
    private Helper helper;
    private Role levelAccess = Role.GUEST;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deleted_book);

        FirebaseApp.initializeApp(this);
        helper = new Helper(this);
        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        recycle = findViewById(R.id.recycle);

        getUserLogged();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait !");
        progressDialog.show();
        setupBookAdapter(false);
        progressDialog.cancel();
    }

    private void getUserLogged() {
        levelAccess = (Role) getIntent().getSerializableExtra("LEVEL");
    }


    private void setupBookAdapter(boolean withFilter) {
        query = db.collection(CollectionHelper.buku);

        query = query.whereEqualTo("flag", "0");

        FirestoreRecyclerOptions options = new FirestoreRecyclerOptions.Builder<Book>().setQuery(query, Book.class).build();
        adapter = new DeleteBookAdapter(this, options);

        adapter.disableActionButton(levelAccess);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recycle.setLayoutManager(mLayoutManager);
        recycle.setAdapter(adapter);
        adapter.startListening();
    }

}