package com.example.perpusmini;

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
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.perpusmini.enums.CollectionHelper;
import com.example.perpusmini.helpers.Helper;
import com.example.perpusmini.enums.Role;
import com.example.perpusmini.models.Admin;
import com.example.perpusmini.models.Peminjam;
import com.example.perpusmini.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    private TextInputLayout tilUsername;
    private TextInputLayout tilEmail;
    private TextInputLayout tilNoHp;
    private TextInputLayout tilAlamat;

    private TextInputLayout tilPassword;
    private TextInputLayout tilKonfirmasiPassword;

    private Button tombolRegister;
    private TextView toSignIn;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private CheckBox agreement;
    private FirebaseFirestore db;
    private Spinner userType;
    private Role type;
    private Helper helper;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        helper = new Helper(this);

        tilUsername = findViewById(R.id.inputUsername);
        tilEmail = findViewById(R.id.inputEmail);
        tilNoHp = findViewById(R.id.inputNoHp);
        tilAlamat = findViewById(R.id.inputAlamat);
        tilPassword = findViewById(R.id.inputPassword);
        tilKonfirmasiPassword = findViewById(R.id.inputKonfirmasiPassword);

        tombolRegister = findViewById(R.id.tombolRegister);
        toSignIn = findViewById(R.id.toSignIn);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        agreement = findViewById(R.id.snk);
        userType = findViewById(R.id.userType);

        FirebaseApp.initializeApp(this);
        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        tombolRegister.setOnClickListener(this);
        toSignIn.setOnClickListener(this);
        agreement.setOnClickListener(this);

        setupDropdownRole();
    }

    private void setupDropdownRole() {

        List<Role> list = new ArrayList<>();
        list.add(Role.PEMINJAM);
        list.add(Role.ADMIN);


        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        userType.setAdapter(adapter);
        userType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextInputLayout[] listElement = {tilUsername, tilEmail, tilPassword, tilKonfirmasiPassword};
                TextInputLayout[] listElementUser = {tilNoHp, tilAlamat};

                type = (Role) parent.getItemAtPosition(position);
                boolean enableUser = false;
                boolean enableAdmin = false;

                if (type == Role.PEMINJAM) {
                    enableUser = true;
                    enableAdmin = true;
                } else if (type == Role.ADMIN) {
                    enableAdmin = true;
                }

                for (TextInputLayout element : listElement) {
                    element.setEnabled(enableAdmin);
                    element.setErrorEnabled(false);
                }

                for (TextInputLayout element : listElementUser) {
                    element.setEnabled(enableUser);
                    element.setErrorEnabled(false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void registerUser() {
        String email = helper.getValue(tilEmail);
        String password = helper.getValue(tilPassword);

        boolean validation = true;
        TextInputLayout[] listValidationInput;
        if (type == Role.PEMINJAM) {
            listValidationInput = new TextInputLayout[]{tilUsername, tilEmail, tilNoHp, tilAlamat, tilPassword, tilKonfirmasiPassword};
        } else{
            listValidationInput = new TextInputLayout[]{tilUsername, tilEmail, tilPassword, tilKonfirmasiPassword};
        }

//        Required
        for (TextInputLayout input : listValidationInput) {
            input.setErrorEnabled(false);
            validation = validation && helper.notEmpty(input);
        }

        if(!validation) {
            helper.toastMessage("Password dan Konfirmasi Password harus sama.");
            return;
        }

//        same password and konfirmasi password
        if (password.equals(helper.getValue(tilKonfirmasiPassword)) == false) {
            helper.toastMessage("Password dan Konfirmasi Password harus sama.");
            return;
        }

        progressDialog.setMessage("Registering User ... ");
        progressDialog.show();

        firebaseRegistration(email, password);
        progressDialog.cancel();
    }

    private void firebaseRegistration(String email, final String password) {
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    onFirebaseRegistrationSuccess();
                } else {
                    onFirebaseRegistrationFailed(task);
                }
            }
        });
    }


    private void onFirebaseRegistrationSuccess() {
        User userModel;
        String username = helper.getValue(tilUsername);
        String email = helper.getValue(tilEmail);
        Role role = type;
        String noHp = helper.getValue(tilNoHp);
        String alamat = helper.getValue(tilAlamat);
        if (type==Role.PEMINJAM) {
            userModel = new Peminjam(username, email, role, noHp, alamat);
        } else {
            userModel = new Admin(username, email, role);
        }
        saveModel(userModel);
    }


    private void saveModel(User userModel) {
        try {
            db.collection(CollectionHelper.user).document(userModel.getEmail()).set(userModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    helper.toastMessage("Akun berhasil dibuat. Silahkan login !");
                    firebaseAuth.signOut();

                    startActivity(new Intent(getApplicationContext(), SignInActivity.class));
                    finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    helper.toastMessage("Silahkan coba kembali ! " + e.getMessage());
                }
            });
        } catch (Exception e) {
            helper.toastMessage("Silahkan coba kembali ! " + e.getMessage());
        }
    }

    private void onFirebaseRegistrationFailed(Task task) {
        System.out.println("ERROR FIREBASE");
        if (task.getException() instanceof FirebaseAuthUserCollisionException) {
            helper.toastMessage("Pengguna sudah terdaftar !");
        } else {
            helper.toastMessage("Terjadi kesalahan saat mendaftar ! Silahkan coba kembali.");
        }
    }

    @Override
    public void onClick(View v) {
        if (v == agreement) tombolRegister.setEnabled(agreement.isChecked());
        else if (v == tombolRegister) registerUser();
        else if (v == toSignIn) {
            startActivity(new Intent(getApplicationContext(), SignInActivity.class));
            finish();
        }
    }

}