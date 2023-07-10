package com.example.perpusmini.controllers.Peminjam;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.perpusmini.DaftarBuku;
import com.example.perpusmini.DaftarPeminjaman;
import com.example.perpusmini.R;
import com.example.perpusmini.SignInActivity;
import com.example.perpusmini.UpdateProfile;
import com.example.perpusmini.enums.CollectionHelper;
import com.example.perpusmini.enums.Role;
import com.example.perpusmini.helpers.Helper;
import com.example.perpusmini.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class PeminjamHome extends AppCompatActivity {

    private Button searchBook, logOut, btnListBorrow, btnUpdateProfile;
    private FirebaseFirestore db;
    private TextView welcomeMessage;
    private Helper helper;
    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_peminjam_home);

        FirebaseApp.initializeApp(this);


        searchBook = findViewById(R.id.findBook);
        logOut = (Button) findViewById(R.id.logOut);
        btnListBorrow = findViewById(R.id.listBorrow);
        btnUpdateProfile = findViewById(R.id.updateProfile);
        welcomeMessage = findViewById(R.id.adminWelcomeMessage);

        db = FirebaseFirestore.getInstance();
        helper = new Helper(this);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();


        setupWelcome();

        btnUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent target = new Intent(getApplicationContext(), UpdateProfile.class);
                target.putExtra("LEVEL", Role.PEMINJAM);
                startActivity(target);
            }
        });

        searchBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent target = new Intent(getApplicationContext(), DaftarBuku.class);
                target.putExtra("LEVEL", Role.PEMINJAM);
                startActivity(target);
            }
        });

        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.collection(CollectionHelper.user).document(fAuth.getCurrentUser().getEmail()).update("fcmToken", null).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            fAuth.signOut();
                            startActivity(new Intent(getApplicationContext(), SignInActivity.class));
                            finish();
                        } else {
                            helper.toastMessage("Coba kembali!");
                        }
                    }
                });
            }
        });

        btnListBorrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent target = new Intent(getApplicationContext(), DaftarPeminjaman.class);
                target.putExtra("LEVEL", Role.PEMINJAM);
                startActivity(target);
            }
        });
    }

    private void setupWelcome() {
        String email = fAuth.getCurrentUser().getEmail();

        fStore.collection(CollectionHelper.user).document(email).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = documentSnapshot.toObject(User.class);
                welcomeMessage.setText("Selamat datang kembali " + user.getUsername());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

}