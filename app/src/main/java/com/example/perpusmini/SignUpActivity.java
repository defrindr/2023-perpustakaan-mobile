package com.example.perpusmini;

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
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    private TextInputLayout tilUsername;
    private TextInputLayout tilEmail;
    private TextInputLayout tilNoHp;
    private TextInputLayout tilAlamat;

    private TextInputLayout tilPassword;
    private TextInputLayout tilKonfirmasiPassword;

    private Button tombolRegister, pickImage;
    private TextView toSignIn;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private CheckBox agreement;
    private FirebaseFirestore db;
    private Spinner userType;
    private Role type;
    private Helper helper;
    private StorageReference storage;

    private  String imageUploaded;



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
        pickImage = findViewById(R.id.pickImage);

        FirebaseApp.initializeApp(this);
        storage = FirebaseStorage.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        tombolRegister.setOnClickListener(this);
        toSignIn.setOnClickListener(this);
        agreement.setOnClickListener(this);

        setupDropdownRole();


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


    private void setupDropdownRole() {

        List<Role> list = new ArrayList<>();
        list.add(Role.PEMINJAM);
//        list.add(Role.ADMIN);


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

        if (type == Role.PEMINJAM) {
//        same password and konfirmasi password
            if (imageUploaded.equals("")) {
                helper.toastMessage("Pastikan anda sudah mengunggah KTP.");
                return;
            }
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
        if(imageUploaded.equals("")) {
            helper.toastMessage("Pastikan anda sudah mengunggah bukti foto");
            return;
        }
        if (type==Role.PEMINJAM) {
            userModel = new Peminjam(username, email, role, noHp, alamat, imageUploaded);
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


    private void uploadImage(Uri imageUri) {
        // Use the imageUri to get the actual file path from the content resolver
        String filePath = getImageFilePath(imageUri);

        pickImage.setText("Loading....");
        pickImage.setEnabled(false);

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

                                    pickImage.setText("Unggah Foto KTP");
                                    pickImage.setEnabled(true);
                                });
                    })
                    .addOnFailureListener(exception -> {
                        // Image upload failed
                        Toast.makeText(this, "Image upload failed", Toast.LENGTH_SHORT).show();

                        pickImage.setText("Unggah Foto KTP");
                        pickImage.setEnabled(true);
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
        if (v == agreement) tombolRegister.setEnabled(agreement.isChecked());
        else if (v == tombolRegister) registerUser();
        else if (v == toSignIn) {
            startActivity(new Intent(getApplicationContext(), SignInActivity.class));
            finish();
        }
    }

}