package com.example.perpusmini;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.perpusmini.controllers.Admin.Home;
import com.example.perpusmini.controllers.Peminjam.PeminjamHome;
import com.example.perpusmini.enums.CollectionHelper;
import com.example.perpusmini.helpers.Helper;
import com.example.perpusmini.enums.Role;
import com.example.perpusmini.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class SignInActivity extends AppCompatActivity implements View.OnClickListener {


    private FirebaseFirestore db;
    private TextInputLayout editID;
    private TextInputLayout editPass;
    private Button buttonSignIn;
    private TextView toSignUp;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private Helper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        FirebaseApp.initializeApp(getApplicationContext());
        editID = (TextInputLayout) findViewById(R.id.editID);
        editPass = (TextInputLayout) findViewById(R.id.editPass);
        firebaseAuth = FirebaseAuth.getInstance();
        buttonSignIn = (Button) findViewById(R.id.buttonSignIn);
        toSignUp = (TextView) findViewById(R.id.toSignUp);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        db = FirebaseFirestore.getInstance();
        helper = new Helper(this);

        System.out.println("Please Wait... Signing You in !");
        if (firebaseAuth.getCurrentUser() != null) {
            System.out.println("Please Wait... Signing You in !");
            progressDialog.show();
            checkRoleUser();
            progressDialog.cancel();
        } else {
            progressDialog.setMessage("Firebase auth not found");
            progressDialog.cancel();
        }

        buttonSignIn.setOnClickListener(this);
        toSignUp.setOnClickListener(this);
    }


    private void signinUser() {
        boolean res = helper.notEmpty(editID);
        res = res && helper.notEmpty(editPass);

        if (res == false) {
            helper.toastMessage("Terdapat isian yang kosong");
            return;
        }

        String email = helper.getValue(editID);
        String pass = helper.getValue(editPass);
        progressDialog.setMessage("Signing In ... ");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(SignInActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    checkRoleUser();
                } else {
                    helper.toastMessage("Kresidensial tidak cocok atau koneksi buruk ! Silahkan coba kembali.");
                }
            }
        });
        progressDialog.cancel();

    }

    private void checkRoleUser() {
        String email = firebaseAuth.getCurrentUser().getEmail();
        db.collection(CollectionHelper.user)
                .whereEqualTo("email", email)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        User obj = new User();
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            obj = doc.toObject(User.class);
                        }

                        registerFcmToken();
                        if (obj.getRole() == Role.PEMINJAM) {
                            if(obj.getStatus() == 0) {
                                firebaseAuth.signOut();
                                helper.toastMessage("Menunggu akun dikonfirmasi");
                                return;
                            }
                            helper.toastMessage("Login Berhasil");
                                startActivity(new Intent(getApplicationContext(), PeminjamHome.class));
                            finish();
                        } else if(obj.getRole() == Role.ADMIN) {
                            helper.toastMessage("Login Berhasil");
                            startActivity(new Intent(getApplicationContext(), Home.class));
                            finish();
                        }

                    }
                });
    }

    private void registerFcmToken() {
        String email = firebaseAuth.getCurrentUser().getEmail();
        String fcmToken = SharedPref.getInstance(getApplicationContext()).getToken();
        db.document(CollectionHelper.user + "/" + email).update("fcmToken", fcmToken)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            helper.toastMessage("Fcm berhasil diupdate");
                        } else {
                            helper.toastMessage("Fcm gagal diupdate");
                        }

                    }
                });
    }


    @Override
    public void onClick(View v) {

        if (v == buttonSignIn)
            signinUser();

        else if (v == toSignUp) {
            startActivity(new Intent(getApplicationContext(), SignUpActivity.class));
            finish();

        }
    }
}

