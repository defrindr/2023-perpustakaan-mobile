package com.example.perpusmini.controllers.Peminjam;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;

import com.example.perpusmini.R;
import com.example.perpusmini.enums.CollectionHelper;
import com.example.perpusmini.enums.StatusPinjam;
import com.example.perpusmini.helpers.Helper;
import com.example.perpusmini.models.Book;
import com.example.perpusmini.models.PinjamModel;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;

public class BeriRating extends AppCompatActivity {

    private TextInputLayout tilRating;
    private Button btnRating;
    private FirebaseFirestore fStore;
    private String reference_pinjam;
    private Helper helper;

    private PinjamModel pModel;
    private Book bModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beri_rating);

        helper = new Helper(this);
        FirebaseApp.initializeApp(this);

        fStore = FirebaseFirestore.getInstance();

        tilRating = findViewById(R.id.tilRating);
        btnRating = findViewById(R.id.btnRating);

        reference_pinjam = getIntent().getStringExtra("ID_PINJAM");

        btnRating.setOnClickListener(view -> {
            btnRating.setEnabled(false);
            String source = helper.getValue(tilRating);

            if (source.equals("")) {
                helper.toastMessage("Rating tidak boleh kosong");
                btnRating.setEnabled(true);
                return;
            }

            int rating = Integer.parseInt(source);
            if (rating < 1 || rating > 5) {
                helper.toastMessage("Rating harus diantara 1 sampai 5");
                btnRating.setEnabled(true);
                return;
            }

            getDataPinjam(() -> getDataBuku(() -> updateRatingPinjam(rating, () -> addRatingKeBuku(rating, () -> finish()))));
            btnRating.setEnabled(true);
        });
    }

    private void addRatingKeBuku(int rating, Runnable callback) {
        bModel.addRating(rating);
        fStore.collection(CollectionHelper.buku).document(bModel.getIsbn()).set(bModel).addOnFailureListener(err -> {
            helper.toastMessage("Gagal memberi rating");
        }).addOnSuccessListener(Void -> {
            helper.toastMessage("Berhasil memberi rating");
            callback.run();
        });
    }

    private void updateRatingPinjam(int rating, Runnable callback) {
        pModel.setRating(rating);
        pModel.setStatus(StatusPinjam.SELESAI);

        fStore.collection(CollectionHelper.pinjam).document(pModel.getUid()).set(pModel).addOnFailureListener(err -> {
            helper.toastMessage("Gagal memberi rating");
        }).addOnSuccessListener(Void -> {
            helper.toastMessage("Berhasil memberi rating");
            callback.run();
        });
    }

    private void getDataPinjam(Runnable callback) {
        fStore.collection(CollectionHelper.pinjam).document(reference_pinjam).get().addOnFailureListener(err -> {
            helper.toastMessage("Gagal memberi rating");
        }).addOnSuccessListener(documentSnapshot -> {
            pModel = documentSnapshot.toObject(PinjamModel.class);
            callback.run();
        });
    }

    private void getDataBuku(Runnable callback) {
        fStore.collection(CollectionHelper.buku).document(pModel.getBuku().getIsbn()).get().addOnFailureListener(err -> {
            helper.toastMessage("Gagal memberi rating");
        }).addOnSuccessListener(documentSnapshot -> {
            bModel = documentSnapshot.toObject(Book.class);
            callback.run();
        });
    }
}