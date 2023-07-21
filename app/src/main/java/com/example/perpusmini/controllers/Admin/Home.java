package com.example.perpusmini.controllers.Admin;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.perpusmini.DaftarBuku;
import com.example.perpusmini.DaftarPeminjaman;
import com.example.perpusmini.R;
import com.example.perpusmini.SignInActivity;
import com.example.perpusmini.UpdateProfile;
import com.example.perpusmini.enums.CollectionHelper;
import com.example.perpusmini.enums.Role;
import com.example.perpusmini.models.Admin;
import com.example.perpusmini.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Home extends AppCompatActivity implements View.OnClickListener {


    private Button searchBook, addBook, logOut, btnListBorrow, btnUpdateProfile, removedBook;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;
    private TextView adminWelcomeMessage;
    private User user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        FirebaseApp.initializeApp(this);
        firebaseAuth = FirebaseAuth.getInstance();
        adminWelcomeMessage = findViewById(R.id.adminWelcomeMessage);
        searchBook = findViewById(R.id.findBook);
        addBook = (Button) findViewById(R.id.addBook);
        logOut = (Button) findViewById(R.id.logOut);
        btnListBorrow = findViewById(R.id.listBorrow);
        btnUpdateProfile = findViewById(R.id.updateProfile);
        removedBook = findViewById(R.id.removedBook);
        db = FirebaseFirestore.getInstance();

        addBook.setOnClickListener(this);
        logOut.setOnClickListener(this);
        searchBook.setOnClickListener(this);

        setupWelcomeMessage();

        btnUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent target = new Intent(getApplicationContext(), UpdateProfile.class);
                target.putExtra("LEVEL", Role.PEMINJAM);
                startActivity(target);
            }
        });
        btnListBorrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent target = new Intent(getApplicationContext(), DaftarPeminjaman.class);
                target.putExtra("LEVEL", user.getRole());
                startActivity(target);
            }
        });

        removedBook.setOnClickListener(View -> {
            Intent target = new Intent(getApplicationContext(), DeletedBookActivity.class);
            target.putExtra("LEVEL", user.getRole());
            startActivity(target);
        });
    }

    private void setupWelcomeMessage() {
        String email = firebaseAuth.getCurrentUser().getEmail();

        db.collection(CollectionHelper.user).document(email).get().addOnSuccessListener(documentSnapshot -> {
            user = documentSnapshot.toObject(User.class);
            adminWelcomeMessage.setText("Selamat datang "+ user.getUsername());
        }).addOnFailureListener(err -> {});
    }

    @Override
    public void onClick(View v) {
        if (v == logOut) {
            db.document(CollectionHelper.user + "/" + firebaseAuth.getCurrentUser().getEmail()).update("fcmToken", null).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        firebaseAuth.signOut();
                        startActivity(new Intent(getApplicationContext(), SignInActivity.class));
                        finish();
                    } else {
                        Toast.makeText(Home.this, "Coba Kembali !", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        if (v == addBook) {
            startActivity(new Intent(getApplicationContext(), AddBook.class));
        }

        if( v == searchBook) {
            Intent target = new Intent(getApplicationContext(), DaftarBuku.class);
            target.putExtra("LEVEL", Role.ADMIN);
            startActivity(target);
        }

    }

}
