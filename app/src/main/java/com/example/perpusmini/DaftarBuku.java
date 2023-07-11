package com.example.perpusmini;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.textfield.TextInputLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.perpusmini.enums.CollectionHelper;
import com.example.perpusmini.enums.Role;
import com.example.perpusmini.helpers.Helper;
import com.example.perpusmini.models.Book;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class DaftarBuku extends AppCompatActivity {

    ProgressDialog progressDialog;
    Query query;
    private RecyclerView recycle;
    private TextView ifNoBook;
    private FirebaseFirestore db;
    private BookAdapter adapter;
    private int mode = 0;
    private String key = "";
    private TextInputLayout searchJudul, searchPengarang;
    private Button btnSearch, btnScanBarcode, btnRemoveIsbn;
    private FirebaseAuth firebaseAuth;
    private Helper helper;
    private  Role levelAccess = Role.GUEST;

    private String isbnQr = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daftar_buku);

        FirebaseApp.initializeApp(this);
        helper = new Helper(this);
        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        searchJudul = findViewById(R.id.searchJudul);
        searchPengarang = findViewById(R.id.searchPengarang);
        btnSearch = findViewById(R.id.btnSearch);
        btnScanBarcode = findViewById(R.id.btnScanBarcode);
        btnRemoveIsbn = findViewById(R.id.btnRemoveIsbn);
        recycle = findViewById(R.id.recycle);
        ifNoBook = findViewById(R.id.ifNoBook);

        getUserLogged();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait !");
        progressDialog.show();
        setupBookAdapter(false);
        progressDialog.cancel();

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setupBookAdapter(true);
            }
        });

        btnScanBarcode.setOnClickListener( View -> {
                IntentIntegrator intentIntegrator = new IntentIntegrator(this);
                intentIntegrator.setPrompt("Scan a barcode or QR Code");
                intentIntegrator.setOrientationLocked(true);
                intentIntegrator.initiateScan();
        });

        btnRemoveIsbn.setOnClickListener(View -> {
            setupBookAdapter(false);
            visibilityFilter(true);
        });

    }

    private void visibilityFilter(boolean show) {
        searchJudul.setVisibility(show ? View.VISIBLE :View.GONE);
        searchPengarang.setVisibility(show ? View.VISIBLE :View.GONE);
        btnSearch.setVisibility(show ? View.VISIBLE :View.GONE);
        btnScanBarcode.setVisibility(show ? View.VISIBLE :View.GONE);
        btnRemoveIsbn.setVisibility(show ? View.GONE :View.VISIBLE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (intentResult != null) {
            if (intentResult.getContents() == null) {
                Toast.makeText(getBaseContext(), "Cancelled", Toast.LENGTH_SHORT).show();
            } else {
                isbnQr = intentResult.getContents();
                visibilityFilter(false);
                setupBookAdapter(false);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    private void getUserLogged() {
        levelAccess = (Role) getIntent().getSerializableExtra("LEVEL");
    }

    private void setupNotfoundPage(boolean Visible) {
        if (Visible) {
            ifNoBook.setVisibility(View.VISIBLE);
            recycle.setVisibility(View.INVISIBLE);
        } else {
            ifNoBook.setVisibility(View.INVISIBLE);
            recycle.setVisibility(View.VISIBLE);
        }
    }


    private void setupBookAdapter(boolean withFilter) {
        query = db.collection(CollectionHelper.buku);
        if (!isbnQr.equals("")) {
            query = query.whereEqualTo("isbn", isbnQr);
        }

        FirestoreRecyclerOptions options = new FirestoreRecyclerOptions.Builder<Book>().setQuery(query, Book.class).build();
        adapter = new BookAdapter(this, options);

        if(withFilter) {
            adapter.searchLike("judul", helper.getValue(searchJudul));
            adapter.searchLike("pengarang", helper.getValue(searchPengarang));
        }

        adapter.disableActionButton(levelAccess);

        if (adapter.getItemCount() == 0) {
            setupNotfoundPage(false);
        } else {
            setupNotfoundPage(true);
        }

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recycle.setLayoutManager(mLayoutManager);
        recycle.setAdapter(adapter);
        adapter.startListening();
    }

    public void hapusBuku(final Book buku) {
        db.collection(CollectionHelper.buku).document(buku.getIsbn()).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("HAPUS BUKU", "Dokumen Buku " + buku.getIsbn() + "berhasil dihapus");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("HAPUS BUKU", "Error deleting document", e);
                    }
                });
    }
}