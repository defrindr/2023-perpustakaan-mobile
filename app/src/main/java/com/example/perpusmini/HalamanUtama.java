package com.example.perpusmini;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.perpusmini.enums.Role;

public class HalamanUtama extends AppCompatActivity {

    private Button login, guest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_halaman_utama);

        login = findViewById(R.id.menuUtamaLogin);
        guest = findViewById(R.id.menuUtamaGuest);

        login.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(), SignInActivity.class));
        });

        guest.setOnClickListener(view -> {
            Intent target = new Intent(getApplicationContext(), DaftarBuku.class);
            target.putExtra("LEVEL", Role.GUEST);
            startActivity(target);
        });
    }
}