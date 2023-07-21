package com.example.perpusmini.controllers.Admin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.app.AppCompatActivity;

import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.Arrays;

public class UbahBuku extends AppCompatActivity {

    Button button1, pickImage;
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
    private StorageReference storage;

    public String imageUploaded = "";

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
        storage = FirebaseStorage.getInstance().getReference();

        helper = new Helper(this);
        editIsbn = findViewById(R.id.editIsbn);
        editJudul = findViewById(R.id.editJudul);
        editPengarang = findViewById(R.id.editPengarang);
        editRating = findViewById(R.id.editRating);
        editStok = findViewById(R.id.editStok);
//        editGambar = findViewById(R.id.editGambar);
        editKategori = (Spinner) findViewById(R.id.spinner1);

        button1 = (Button) findViewById(R.id.button1);
        pickImage = findViewById(R.id.pickImage);
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

        // Registers a photo picker activity launcher in single-select mode.
        ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
                registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                    // Callback is invoked after the user selects a media item or closes the
                    // photo picker.
                    if (uri != null) {
                        uploadImage(uri);
                        Log.d("PhotoPicker", "Selected URI: " + uri);
                    } else {
                        Log.d("PhotoPicker", "No media selected");
                    }
                });

        pickImage.setOnClickListener(View -> imagePicker(pickMedia));
    }

    private void imagePicker(ActivityResultLauncher<PickVisualMediaRequest> pickMedia) {

        // Include only one of the following calls to launch(), depending on the types
        // of media that you want to let the user choose from.

        // Launch the photo picker and let the user choose only images.
        pickMedia.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                .build());
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
//        setFieldValue(editGambar, model.getGambar());
        imageUploaded = String.valueOf(model.getGambar());
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
        valid = valid && !imageUploaded.equals("");

        if (valid == false) return;

        p1.setMessage("Menambahkan Buku");
        p1.show();
        String isbn = helper.getValue(editIsbn);
        db.collection(CollectionHelper.buku).document(isbn).get()
                .addOnSuccessListener(documentSnapshot -> {
                    Book  oldBook = documentSnapshot.toObject(Book.class);
                    Book schema = buildSchema(oldBook.getAvailable(), oldBook.getStok());
                    db.collection(CollectionHelper.buku).document(schema.getIsbn()).set(schema).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                helper.toastMessage("Buku berhasil diubah !");
                                finish();
                            } else {
                                helper.toastMessage("Coba kembali !");
                                p1.cancel();
                            }
                        }
                    });
                })
                .addOnFailureListener(error -> {
                    helper.toastMessage("Nomor ISBN tidak ditemukan \nAtau terjadi masalah pada koneksi anda");
                    p1.cancel();
                });
    }

    private void uploadImage(Uri imageUri) {
        // Use the imageUri to get the actual file path from the content resolver
        String filePath = getImageFilePath(imageUri);

        if (filePath != null) {
            File file = new File(filePath);
            String fileName = file.getName();
            StorageReference imageRef = storage.child("images/" + fileName);

            // Upload the image to Firebase Storage
            imageRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        // Image upload successful
                        // You can retrieve the download URL if needed
                        imageRef.getDownloadUrl()
                                .addOnSuccessListener(uri -> {
                                    imageUploaded = uri.toString();
                                    Toast.makeText(this, "Image berhasil diunggah", Toast.LENGTH_SHORT).show();
                                });
                    })
                    .addOnFailureListener(exception -> {
                        // Image upload failed
                        Toast.makeText(this, "Image upload failed", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private String getImageFilePath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor == null) return null;
        int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String filePath = cursor.getString(columnIndex);
        cursor.close();
        return filePath;
    }
    private Book buildSchema(int availablelama, int stokLama) {
        String isbn = helper.getValue(editIsbn);
        String judul = helper.getValue(editJudul).toUpperCase();
        String pengarang = helper.getValue(editPengarang);
        int stok = Integer.parseInt(helper.getValue(editStok));
        int available = availablelama + (stok - stokLama);
//        String gambar = helper.getValue(editGambar);

        return new Book(isbn, judul, pengarang, model.getRating(), stok, imageUploaded, type, available);
    }
}