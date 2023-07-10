package com.example.perpusmini;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import android.view.View;
import android.widget.Button;

import com.example.perpusmini.enums.CollectionHelper;
import com.example.perpusmini.enums.Role;
import com.example.perpusmini.helpers.Helper;
import com.example.perpusmini.models.Admin;
import com.example.perpusmini.models.Peminjam;
import com.example.perpusmini.models.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class UpdateProfile extends AppCompatActivity {

    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;
    private TextInputLayout tilEmail, tilUsername, tilNoHp, tilAlamat;
    private Button btnSubmit;

    private User user;
    private Admin admin;
    private Peminjam peminjam;

    private Helper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        FirebaseApp.initializeApp(this);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        helper = new Helper(this);

        tilEmail = findViewById(R.id.inputEmail);
        tilUsername = findViewById(R.id.inputUsername);
        tilNoHp = findViewById(R.id.inputNoHp);
        tilAlamat = findViewById(R.id.inputAlamat);
        btnSubmit = findViewById(R.id.tombolUpdateProfile);


        setupTil();


        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                update();
            }
        });
    }

    private void update() {
        String username = helper.getValue(tilUsername);
        String alamat = helper.getValue(tilAlamat);
        String noHp = helper.getValue(tilNoHp);
        if (user.getRole() == Role.ADMIN) {
            if (!helper.notEmpty(tilUsername)) {
                helper.toastMessage("Bidang username tidak boleh kosong");
                return;
            }
            Admin updateSchema = new Admin(username, user.getEmail(), user.getRole());
            fStore.collection(CollectionHelper.user).document(user.getEmail()).set(updateSchema).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    helper.toastMessage("Profile berhasil diperbaharui");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    helper.toastMessage("Profile gagal diperbaharui");
                }
            });
        } else if (user.getRole() == Role.PEMINJAM) {
            if (!(helper.notEmpty(tilUsername) && helper.notEmpty(tilNoHp) && helper.notEmpty(tilAlamat))) {
                helper.toastMessage("Bidang tidak boleh kosong");
                return;
            }
            Peminjam updateSchema = new Peminjam(username, user.getEmail(), user.getRole(), noHp, alamat);
            fStore.collection(CollectionHelper.user).document(user.getEmail()).set(updateSchema).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    helper.toastMessage("Profile berhasil diperbaharui");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    helper.toastMessage("Profile gagal diperbaharui");
                }
            });
        }
    }


    private void setupTil() {
        String email = fAuth.getCurrentUser().getEmail();

        fStore.collection(CollectionHelper.user).document(email).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                user = documentSnapshot.toObject(User.class);
                if (user.getRole() == Role.ADMIN) {
                    admin = documentSnapshot.toObject(Admin.class);

                    tilEmail.setEnabled(false);
                    tilAlamat.setEnabled(false);
                    tilNoHp.setEnabled(false);

                    tilEmail.getEditText().setText(admin.getEmail());
                    tilUsername.getEditText().setText(admin.getUsername());
                } else {
                    peminjam = documentSnapshot.toObject(Peminjam.class);

                    tilEmail.setEnabled(false);

                    tilEmail.getEditText().setText(peminjam.getEmail());
                    tilUsername.getEditText().setText(peminjam.getUsername());
                    tilAlamat.getEditText().setText(peminjam.getAlamat());
                    tilNoHp.getEditText().setText(peminjam.getNoHp());
                }


            }
        });
    }
}