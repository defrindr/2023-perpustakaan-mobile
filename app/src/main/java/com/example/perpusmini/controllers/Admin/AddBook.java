package com.example.perpusmini.controllers.Admin;

import static java.lang.Boolean.FALSE;

import android.app.ProgressDialog;
import android.database.Cursor;
import android.graphics.Color;
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
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class AddBook extends AppCompatActivity implements View.OnClickListener {

    Button button1, pickImage, pickImageDepan, pickImageBelakang, pickImageDaftarIsi;
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
    private StorageReference storage;

    public String imageUploaded = "", imageUploadedGambarDepan = "", imageUploadedGambarBelakang = "", imageUploadedGambarDaftarIsi = "";

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_book);
        FirebaseApp.initializeApp(this);
        storage = FirebaseStorage.getInstance().getReference();
        helper = new Helper(this);
        editIsbn = findViewById(R.id.editIsbn);
        editJudul = findViewById(R.id.editJudul);
        editPengarang = findViewById(R.id.editPengarang);
        editRating = findViewById(R.id.editRating);
        editStok = findViewById(R.id.editStok);
//        editGambar = findViewById(R.id.editGambar);
        editKategori = (Spinner) findViewById(R.id.spinner1);
        pickImage = findViewById(R.id.pickImage);
        pickImageDepan  = findViewById(R.id.pickImageDepan);
        pickImageBelakang = findViewById(R.id.pickImageBelakang);
        pickImageDaftarIsi = findViewById(R.id.pickImageDaftarIsi);

        editRating.getEditText().setText(String.valueOf(0));
        editRating.setEnabled(FALSE);

        button1 = (Button) findViewById(R.id.button1);


        p1 = new ProgressDialog(this);
        p1.setCancelable(false);
        db = FirebaseFirestore.getInstance();
        kategoriDropdown();
        button1.setOnClickListener(this);

        // Registers a photo picker activity launcher in single-select mode.
        ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
                registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                    // Callback is invoked after the user selects a media item or closes the
                    // photo picker.
                    if (uri != null) {
                        uploadImage(uri, pickImage);

                        Log.d("PhotoPicker", "Selected URI: " + uri);
                    } else {
                        Log.d("PhotoPicker", "No media selected");
                    }
                });


        // Registers a photo picker activity launcher in single-select mode.
        ActivityResultLauncher<PickVisualMediaRequest> pickMediaGambarDepan =
                registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                    // Callback is invoked after the user selects a media item or closes the
                    // photo picker.
                    if (uri != null) {
                        uploadImageDepan(uri, pickImageDepan);
                        Log.d("PhotoPicker", "Selected URI: " + uri);
                    } else {
                        Log.d("PhotoPicker", "No media selected");
                    }
                });


        // Registers a photo picker activity launcher in single-select mode.
        ActivityResultLauncher<PickVisualMediaRequest> pickMediaGambarDaftarIsi =
                registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                    // Callback is invoked after the user selects a media item or closes the
                    // photo picker.
                    if (uri != null) {
                        uploadImageDaftarIsi(uri, pickImageDaftarIsi);
                        Log.d("PhotoPicker", "Selected URI: " + uri);
                    } else {
                        Log.d("PhotoPicker", "No media selected");
                    }
                });

        // Registers a photo picker activity launcher in single-select mode.
        ActivityResultLauncher<PickVisualMediaRequest> pickMediaGambarBelakang =
                registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                    // Callback is invoked after the user selects a media item or closes the
                    // photo picker.
                    if (uri != null) {
                        uploadImageBelakang(uri, pickImageBelakang);
                        Log.d("PhotoPicker", "Selected URI: " + uri);
                    } else {
                        Log.d("PhotoPicker", "No media selected");
                    }
                });

        pickImage.setOnClickListener(View -> imagePicker(pickMedia));

        pickImageDepan.setOnClickListener(View -> imagePicker(pickMediaGambarDepan));
        pickImageBelakang.setOnClickListener(View -> imagePicker(pickMediaGambarBelakang));
        pickImageDaftarIsi.setOnClickListener(View -> imagePicker(pickMediaGambarDaftarIsi));

    }

    private void imagePicker(ActivityResultLauncher<PickVisualMediaRequest> pickMedia) {

        // Include only one of the following calls to launch(), depending on the types
        // of media that you want to let the user choose from.

        // Launch the photo picker and let the user choose only images.
        pickMedia.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                .build());
    }

    private void kategoriDropdown() {
        KategoriBuku[] A = KategoriBuku.values();

        ArrayAdapter<? extends KategoriBuku> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, A);
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

    private void addBook() {
        boolean valid = true;

        valid = helper.notEmpty(editIsbn);
        valid = valid && helper.notEmpty(editJudul);
        valid = valid && helper.notEmpty(editPengarang);
        valid = valid && helper.notEmpty(editRating);
        valid = valid && helper.notEmpty(editStok);
        valid = valid && !imageUploaded.equals("");
        valid = valid && !imageUploadedGambarDepan.equals("");
        valid = valid && !imageUploadedGambarBelakang.equals("");
        valid = valid && !imageUploadedGambarDaftarIsi.equals("");

        if (valid == false) return;

        p1.setMessage("Menambahkan Buku");
        p1.show();
        String isbn = helper.getValue(editIsbn);

        db.document(CollectionHelper.buku + "/" + isbn).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if ((task.isSuccessful()) && (task.getResult().exists() == false)) {
                    String isbn = helper.getValue(editIsbn);
                    String judul = helper.getValue(editJudul).toUpperCase();
                    String pengarang = helper.getValue(editPengarang);
                    List<Integer> rating = new ArrayList<Integer>();
                    int stok = Integer.parseInt(helper.getValue(editStok));
                    String gambar = imageUploaded;

                    Book b = new Book(isbn, judul, pengarang, rating, stok, gambar, type);
                    b.setGambarDepan(imageUploadedGambarDepan);
                    b.setGambarBelakang(imageUploadedGambarBelakang);
                    b.setGambarDaftarIsi(imageUploadedGambarDaftarIsi);
                    db.document(CollectionHelper.buku + "/" + isbn).set(b).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                helper.toastMessage("Buku berhasil ditambahkan !");
                            } else {
                                helper.toastMessage("Coba kembali !");
                            }
                        }
                    });
                } else {
                    helper.toastMessage("Buku ini telah ditambahkan \nAtau terjadi masalah pada koneksi anda");
                }
                p1.cancel();
            }
        });
    }

    private void uploadImage(Uri imageUri, Button target) {
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
                                    target.setBackgroundColor(Color.parseColor("red"));
                                    target.setTextColor(Color.parseColor("white"));
                                    Toast.makeText(this, "Image berhasil diunggah", Toast.LENGTH_SHORT).show();
                                });
                    })
                    .addOnFailureListener(exception -> {
                        // Image upload failed
                        Toast.makeText(this, "Image upload failed", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void uploadImageDepan(Uri imageUri, Button target) {
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
                                    imageUploadedGambarDepan = uri.toString();
                                    target.setBackgroundColor(Color.parseColor("red"));
                                    target.setTextColor(Color.parseColor("white"));
                                    Toast.makeText(this, "Image berhasil diunggah", Toast.LENGTH_SHORT).show();
                                });
                    })
                    .addOnFailureListener(exception -> {
                        // Image upload failed
                        Toast.makeText(this, "Image upload failed", Toast.LENGTH_SHORT).show();
                    });
        }
    }
    private void uploadImageBelakang(Uri imageUri, Button target) {
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
                                    imageUploadedGambarBelakang = uri.toString();

                                    target.setBackgroundColor(Color.parseColor("red"));
                                    target.setTextColor(Color.parseColor("white"));
                                    Toast.makeText(this, "Image berhasil diunggah", Toast.LENGTH_SHORT).show();
                                });
                    })
                    .addOnFailureListener(exception -> {
                        // Image upload failed
                        Toast.makeText(this, "Image upload failed", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void uploadImageDaftarIsi(Uri imageUri, Button target) {
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
                                    imageUploadedGambarDaftarIsi = uri.toString();
                                    target.setBackgroundColor(Color.parseColor("red"));
                                    target.setTextColor(Color.parseColor("white"));
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

    @Override
    public void onClick(View v) {
        addBook();
    }
}
