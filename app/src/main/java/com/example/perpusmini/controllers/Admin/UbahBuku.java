package com.example.perpusmini.controllers.Admin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.example.perpusmini.R;
import com.example.perpusmini.enums.CollectionHelper;
import com.example.perpusmini.enums.KategoriBuku;
import com.example.perpusmini.helpers.Helper;
import com.example.perpusmini.models.Book;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;

public class UbahBuku extends AppCompatActivity {

    Button button1;
    KategoriBuku type;
    ProgressDialog p1;
    private TextInputLayout editIsbn;
    private TextInputLayout editJudul;
    private TextInputLayout editPengarang;
    private TextInputLayout editRating;
    private TextInputLayout editStok;
    private TextInputLayout editGambar;
    private Spinner editKategori;
    private FirebaseFirestore db;
    private Helper helper;
    Book model;
    String isbn;

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ubah_buku);


        FirebaseApp.initializeApp(this);
        db = FirebaseFirestore.getInstance();

        helper = new Helper(this);
        editIsbn = findViewById(R.id.editIsbn);
        editJudul = findViewById(R.id.editJudul);
        editPengarang = findViewById(R.id.editPengarang);
        editRating = findViewById(R.id.editRating);
        editStok = findViewById(R.id.editStok);
        editGambar = findViewById(R.id.editGambar);
        editKategori = (Spinner) findViewById(R.id.spinner1);

        button1 = (Button) findViewById(R.id.button1);
        p1 = new ProgressDialog(this);
        p1.setCancelable(false);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateBook();
            }
        });


        editIsbn.setEnabled(false);
        editRating.setEnabled(false);
//        get id
        isbn = getIntent().getStringExtra("ISBN");

        kategoriDropdown();
        fetchInitialData();
    }

    private  void setFieldValue(TextInputLayout target, String value) {
        target.setErrorEnabled(false);
        target.getEditText().setText(value);
    }

    private void fetchInitialData() {
        db.collection(CollectionHelper.buku).document(isbn).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                model = documentSnapshot.toObject(Book.class);
                setupFields();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    private  void setupFields() {
        double resultRating = model.getRatingAvg();
        setFieldValue(editIsbn, model.getIsbn());
        setFieldValue(editJudul, model.getJudul());
        setFieldValue(editPengarang, model.getPengarang());
        setFieldValue(editRating, String.valueOf(resultRating));
        setFieldValue(editGambar, model.getGambar());
        setFieldValue(editStok, String.valueOf(model.getStok()));

    }

    private void kategoriDropdown() {
        KategoriBuku[] A = KategoriBuku.values();

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, A);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        editKategori.setAdapter(adapter);
        editKategori.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                type = (KategoriBuku) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    private void updateBook() {
        boolean valid = true;

        valid = helper.notEmpty(editIsbn);
        valid = valid && helper.notEmpty(editJudul);
        valid = valid && helper.notEmpty(editPengarang);
        valid = valid && helper.notEmpty(editRating);
        valid = valid && helper.notEmpty(editStok);
        valid = valid && helper.notEmpty(editGambar);

        if (valid == false) return;

        p1.setMessage("Menambahkan Buku");
        p1.show();
        String isbn = helper.getValue(editIsbn);
        db.collection(CollectionHelper.buku).document(isbn).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if ((task.isSuccessful()) && (task.getResult().exists() == false)) {
                    helper.toastMessage("Nomor ISBN tidak ditemukan \nAtau terjadi masalah pada koneksi anda");
                } else {
                    Book schema = buildSchema();
                    db.collection(CollectionHelper.buku).document(schema.getIsbn()).set(schema).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                helper.toastMessage("Buku berhasil diubah !");
                                finish();
                            } else {
                                helper.toastMessage("Coba kembali !");
                            }
                        }
                    });
                }
                p1.cancel();
            }
        });
    }

    private Book buildSchema() {
        String isbn = helper.getValue(editIsbn);
        String judul = helper.getValue(editJudul).toUpperCase();
        String pengarang = helper.getValue(editPengarang);
        int rating = Integer.parseInt(helper.getValue(editRating));
        int stok = Integer.parseInt(helper.getValue(editStok));
        String gambar = helper.getValue(editGambar);

        return new Book(isbn, judul, pengarang, model.getRating(), stok, gambar, type);
    }
}