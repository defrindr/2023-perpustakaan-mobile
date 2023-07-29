package com.example.perpusmini;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.perpusmini.enums.CollectionHelper;
import com.example.perpusmini.helpers.Helper;
import com.example.perpusmini.models.Book;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.Callable;

public class DetailBukuActivity extends AppCompatActivity {
    ImageView gambarDepan, gambarBelakang, gambarDaftarIsi;

    FirebaseFirestore fStore;
    String isbn_buku;
    Book buku;

    Helper helper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_buku);


        // connect variable with resource
        gambarDepan = findViewById(R.id.gambarDepan);
        gambarBelakang = findViewById(R.id.gambarBelakang);
        gambarDaftarIsi = findViewById(R.id.gambarDaftarIsi);

        // initialize firebase
        FirebaseApp.initializeApp(getApplicationContext());
        fStore = FirebaseFirestore.getInstance();

        // initialize helper
        helper = new Helper(getApplicationContext());


        // get data from intent
        isbn_buku = getIntent().getStringExtra("ISBN_BUKU");
        if(isbn_buku.equals("")) finish();

        fetchData(() -> injectDataToResource());
    }

    private void injectDataToResource() {
        setImageToResource(gambarDepan, buku.getGambarDepan());
        setImageToResource(gambarBelakang, buku.getGambarBelakang());
        setImageToResource(gambarDaftarIsi, buku.getGambarDaftarIsi());
    }

    private void setImageToResource(ImageView img, String source) {

        // Load and resize the image using Glide
        RequestOptions requestOptions = new RequestOptions()
                .override(750) // Specify the desired width and height for resizing
                .transform(new RoundedCorners(20)); // Optional: apply rounded corners to the image

        Glide.with(getApplicationContext())
                .load(source)
                .apply(requestOptions)
                .into(img);

        img.setOnClickListener(View -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(source));

            startActivity(intent);
        });
    }


    // get resource buku dari fstore
    private void fetchData(Runnable callback) {
        fStore.collection(CollectionHelper.buku).document(isbn_buku).get().addOnSuccessListener(result -> {
            buku = result.toObject(Book.class);
            callback.run();
        }).addOnFailureListener(exception -> {
            helper.toastMessage("Gagal mendapatkan data buku");
            finish();
        });
    }

}