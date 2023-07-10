package com.example.perpusmini.controllers.Peminjam;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.perpusmini.R;
import com.example.perpusmini.enums.CollectionHelper;
import com.example.perpusmini.enums.StatusPinjam;
import com.example.perpusmini.helpers.Helper;
import com.example.perpusmini.models.Book;
import com.example.perpusmini.models.PinjamModel;
import com.example.perpusmini.models.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.UUID;

public class Pinjam extends AppCompatActivity {

    private TextView tvBuku, tvTglKembali, tvPeminjam, tvAlamat, tvDenda;
    private String intentIsbn;
    private Button btnSubmit;
    private Helper helper;
    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;
    private User user;
    private Book buku;
    private Calendar calendar;
    private String tglPinjam, tglKembali;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pinjam);

        FirebaseApp.initializeApp(this);
        helper = new Helper(this);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        calendar = Calendar.getInstance();


        tvBuku = findViewById(R.id.buku);
        tvTglKembali = findViewById(R.id.tanggalKembali);
        tvPeminjam = findViewById(R.id.peminjam);
        tvAlamat = findViewById(R.id.alamat);
        tvDenda = findViewById(R.id.denda);
        btnSubmit = findViewById(R.id.btnPinjam);


        tvDenda.setText("Denda peminjaman per hari adalah Rp " + String.valueOf(Helper.DENDA));

        intentIsbn = getIntent().getStringExtra("ISBN");


        setupUserInfo();
        setupBookInfo();
        setupBorrowInfo();


        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actionPinjam();
            }
        });
    }

    private void setupBorrowInfo() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        tglPinjam = formatter.format(calendar.getTime()).toString();
        calendar.add(Calendar.DAY_OF_MONTH, 7);
        tglKembali = formatter.format(calendar.getTime()).toString();

        // inject to view
        tvTglKembali.setText("Tanggal Kembali : " + tglKembali);
    }

    private void setupBookInfo() {
        fStore.collection(CollectionHelper.buku).document(intentIsbn).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                buku = documentSnapshot.toObject(Book.class);

                // inject user information to view
                tvBuku.setText("Judul Buku : " + buku.getJudul());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    private void setupUserInfo() {
        String email = fAuth.getCurrentUser().getEmail();

        fStore.collection(CollectionHelper.user).document(email).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String userAlamat = String.valueOf(documentSnapshot.get("alamat"));
                user = documentSnapshot.toObject(User.class);

                // inject user information to view
                tvPeminjam.setText("Nama Peminjam : " + user.getUsername());
                tvAlamat.setText("Alamat Peminjam : " + userAlamat);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });
    }

    private void actionPinjam() {
        String uid = UUID.randomUUID().toString();
        PinjamModel model = new PinjamModel(uid, tglPinjam, tglKembali, buku.getIsbn(), user.getEmail(), 0, StatusPinjam.MENUNGGU_PERSETUJUAN_PINJAM, buku, user);
        fStore.collection(CollectionHelper.pinjam).document(uid).set(model).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                helper.toastMessage("Data peminjaman berhasil disimpan, menunggu persetujuan admin.");
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                helper.toastMessage("Gagal menyimpan data. Error : " + e.getMessage());
            }
        });
    }
}