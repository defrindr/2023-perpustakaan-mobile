package com.example.perpusmini;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import com.example.perpusmini.enums.CollectionHelper;
import com.example.perpusmini.enums.Role;
import com.example.perpusmini.enums.StatusPinjam;
import com.example.perpusmini.helpers.Helper;
import com.example.perpusmini.models.Book;
import com.example.perpusmini.models.PinjamModel;
import com.example.perpusmini.models.User;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Calendar;
import java.util.function.Consumer;

public class DaftarPeminjaman extends AppCompatActivity {

    Query query;
    private FirebaseFirestore fStore;
    private FirebaseAuth fAuth;
    private TextView tvNotfound;
    private RecyclerView rvPinjam;
    private PeminjamanAdapter pAdapter;
    private Helper helper;

    private Book modelBook;
    private Role role;

    private Button btnSearch, btnPickDate, removeDate;
    private TextInputLayout searchPeminjam;
    private TextView searchTanggal;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daftar_peminjaman);

        FirebaseApp.initializeApp(this);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        helper = new Helper(this);

        rvPinjam = findViewById(R.id.recycle);
        tvNotfound = findViewById(R.id.ifNoBorrow);
        searchTanggal = findViewById(R.id.searchTanggal);
        searchPeminjam= findViewById(R.id.searchPeminjam);
        btnSearch= findViewById(R.id.btnSearch);
        btnPickDate = findViewById(R.id.pickDate);
        removeDate = findViewById(R.id.removeDate);

        role = (Role) getIntent().getSerializableExtra("LEVEL");

        setupPeminjamanAdapter(false);

        btnSearch.setOnClickListener(View -> {
            setupPeminjamanAdapter(true);
        });


        removeDate.setOnClickListener(View -> {
            searchTanggal.setText("");
            btnPickDate.setVisibility(View.VISIBLE);
            removeDate.setVisibility(View.GONE);
        });

        // on below line we are adding click listener for our pick date button
        btnPickDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // on below line we are getting
                // the instance of our calendar.
                final Calendar c = Calendar.getInstance();

                // on below line we are getting
                // our day, month and year.
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                // on below line we are creating a variable for date picker dialog.
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        // on below line we are passing context.
                        DaftarPeminjaman.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // on below line we are setting date to our text view.
                                String month = String.valueOf(monthOfYear + 1);
                                if(month.length() == 1) {
                                    month = "0" + month;
                                }
                                searchTanggal.setText(year + "-" + month  + "-" + dayOfMonth);
                                btnPickDate.setVisibility(View.GONE);
                                removeDate.setVisibility(View.VISIBLE);
                            }
                        },
                        // on below line we are passing year,
                        // month and day for selected date in our date picker.
                        year, month, day);
                // at last we are calling show to
                // display our date picker dialog.
                datePickerDialog.show();
            }
        });
    }

    private void setupNotfoundPage(boolean Visible) {
        if (Visible) {
            tvNotfound.setVisibility(View.VISIBLE);
            rvPinjam.setVisibility(View.INVISIBLE);
        } else {
            tvNotfound.setVisibility(View.INVISIBLE);
            rvPinjam.setVisibility(View.VISIBLE);
        }
    }

    private void setupPeminjamanAdapter(boolean withFilter) {
        query = fStore.collection(CollectionHelper.pinjam);
        if (role == Role.PEMINJAM) {
            query.whereEqualTo("user_reference", fAuth.getCurrentUser().getEmail());
        }
        FirestoreRecyclerOptions<? extends PinjamModel> options = new FirestoreRecyclerOptions.Builder<PinjamModel>().setQuery(query, PinjamModel.class).build();
        pAdapter = new PeminjamanAdapter(this, options);

        Role value = (Role) getIntent().getSerializableExtra("LEVEL");
        pAdapter.disableActionButton(value);

        if(withFilter) {
            pAdapter.searchLike("peminjam", helper.getValue(searchPeminjam));
            pAdapter.searchLike("tanggal", String.valueOf(searchTanggal.getText()));
        }

        if (pAdapter.getItemCount() == 0) {
            setupNotfoundPage(false);
        } else {
            setupNotfoundPage(true);
        }

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        rvPinjam.setLayoutManager(mLayoutManager);
        rvPinjam.setAdapter(pAdapter);
        pAdapter.startListening();
    }


    public void hapus(PinjamModel model) {
        if (model.getStatus() != StatusPinjam.MENUNGGU_PERSETUJUAN_PINJAM) {
            helper.toastMessage("Tidak dapat menghapus peminjaman dengan status selain menunggu persetujuan");
            return;
        }

        fStore.collection(CollectionHelper.pinjam).document(model.getUid()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                helper.toastMessage("Peminjaman berhasil dihapus");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                helper.toastMessage("Peminjaman gagal dihapus");
            }
        });
    }


    public void tolak(PinjamModel model) {
        if (model.getStatus() != StatusPinjam.MENUNGGU_PERSETUJUAN_PINJAM) {
            helper.toastMessage("Tidak dapat menghapus peminjaman dengan status selain menunggu persetujuan");
            return;
        }

        updateStatusPinjam(model.getUid(), StatusPinjam.DITOLAK);
    }

    public void setujui(PinjamModel model) {
        if (model.getStatus() != StatusPinjam.MENUNGGU_PERSETUJUAN_PINJAM) {
            helper.toastMessage("Tidak dapat menghapus peminjaman dengan status selain menunggu persetujuan");
            return;
        }


        updateJumlahBuku(model.getBuku(), false, () -> {
            updateStatusPinjam(model.getUid(), StatusPinjam.DIPINJAM);
        });
    }

    public void kembalikan(PinjamModel model) {
        if (model.getStatus() != StatusPinjam.DIPINJAM) {
            helper.toastMessage("Tidak dapat menghapus peminjaman dengan status selain menunggu persetujuan");
            return;
        }

        updateStatusPinjam(model.getUid(), StatusPinjam.MENUNGGU_PERSETUJUAN_KEMBALI);
    }


    public void tolakKembali(PinjamModel model) {
        if (model.getStatus() != StatusPinjam.MENUNGGU_PERSETUJUAN_KEMBALI) {
            helper.toastMessage("Tidak dapat menghapus peminjaman dengan status selain menunggu persetujuan");
            return;
        }

        updateStatusPinjam(model.getUid(), StatusPinjam.DIPINJAM);
    }


    public void setujuiKembali(PinjamModel model) {
        if (model.getStatus() != StatusPinjam.MENUNGGU_PERSETUJUAN_KEMBALI) {
            helper.toastMessage("Tidak dapat menghapus peminjaman dengan status selain menunggu persetujuan");
            return;
        }

        updateJumlahBuku(model.getBuku(), true, () -> {
            updateStatusPinjam(model.getUid(), StatusPinjam.DIKEMBALIKAN);
        });
    }

    public void updateStatusPinjam(String uid, StatusPinjam status) {
        fStore.collection(CollectionHelper.pinjam).document(uid).update("status", status).addOnSuccessListener(Void -> {
            helper.toastMessage("Status berhasil diubah");
        }).addOnFailureListener(error -> {
            helper.toastMessage("Status gagal diubah : " + error.getMessage());
        });
    }

    public void updateJumlahBuku(Book model, boolean tambah, Runnable runnable) {
        fStore.collection(CollectionHelper.buku).document(model.getIsbn()).get().addOnFailureListener(e -> {
            helper.toastMessage("Gagal mengupdate buku");
        }).addOnSuccessListener(documentSnapshot -> {

            modelBook = documentSnapshot.toObject(Book.class);
            int stok = modelBook.getAvailable();

            if (tambah) {
                stok += 1;
            } else {
                stok -= 1;
            }

            if (stok < 0) {
                helper.toastMessage("Stok kurang dari 1");
                return;
            }

            fStore.collection(CollectionHelper.buku).document(modelBook.getIsbn()).update("available", stok).addOnSuccessListener(unused -> {
                runnable.run();
            }).addOnFailureListener(e -> {
            });
        });
    }

}