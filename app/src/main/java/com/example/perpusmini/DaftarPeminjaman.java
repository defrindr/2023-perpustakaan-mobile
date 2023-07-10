package com.example.perpusmini;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.perpusmini.enums.CollectionHelper;
import com.example.perpusmini.enums.Role;
import com.example.perpusmini.enums.StatusPinjam;
import com.example.perpusmini.helpers.Helper;
import com.example.perpusmini.models.Book;
import com.example.perpusmini.models.PinjamModel;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.messaging.FirebaseMessaging;

public class DaftarPeminjaman extends AppCompatActivity {

    Query query;
    private FirebaseFirestore fStore;
    private FirebaseAuth fAuth;
    private TextView tvNotfound;
    private RecyclerView rvPinjam;
    private PeminjamanAdapter pAdapter;
    private Helper helper;


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


        setupPeminjamanAdapter(false);
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
        FirestoreRecyclerOptions options = new FirestoreRecyclerOptions.Builder<PinjamModel>().setQuery(query, PinjamModel.class).build();
        pAdapter = new PeminjamanAdapter(this, options);

        Role value = (Role) getIntent().getSerializableExtra("LEVEL");
        pAdapter.disableActionButton(value);

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

        fStore.collection(CollectionHelper.pinjam).document(model.getUid()).update("status", StatusPinjam.DITOLAK).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                helper.toastMessage("Status berhasil diubah");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                helper.toastMessage("Status gagal diubah");
            }
        });
    }

    public void setujui(PinjamModel model) {
        if (model.getStatus() != StatusPinjam.MENUNGGU_PERSETUJUAN_PINJAM) {
            helper.toastMessage("Tidak dapat menghapus peminjaman dengan status selain menunggu persetujuan");
            return;
        }

        updateJumlahBuku(model.getBuku(), false);

        fStore.collection(CollectionHelper.pinjam).document(model.getUid()).update("status", StatusPinjam.DIPINJAM).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                helper.toastMessage("Status berhasil diubah");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                helper.toastMessage("Status gagal diubah");
            }
        });
    }

    public void kembalikan(PinjamModel model) {
        if (model.getStatus() != StatusPinjam.DIPINJAM) {
            helper.toastMessage("Tidak dapat menghapus peminjaman dengan status selain menunggu persetujuan");
            return;
        }

        fStore.collection(CollectionHelper.pinjam).document(model.getUid()).update("status", StatusPinjam.MENUNGGU_PERSETUJUAN_KEMBALI).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                helper.toastMessage("Status berhasil diubah");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                helper.toastMessage("Status gagal diubah");
            }
        });
    }


    public void tolakKembali(PinjamModel model) {
        if (model.getStatus() != StatusPinjam.MENUNGGU_PERSETUJUAN_KEMBALI) {
            helper.toastMessage("Tidak dapat menghapus peminjaman dengan status selain menunggu persetujuan");
            return;
        }

        fStore.collection(CollectionHelper.pinjam).document(model.getUid()).update("status", StatusPinjam.DIPINJAM).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                helper.toastMessage("Status berhasil diubah");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                helper.toastMessage("Status gagal diubah");
            }
        });
    }


    public void setujuiKembali(PinjamModel model) {
        if (model.getStatus() != StatusPinjam.MENUNGGU_PERSETUJUAN_KEMBALI) {
            helper.toastMessage("Tidak dapat menghapus peminjaman dengan status selain menunggu persetujuan");
            return;
        }

        updateJumlahBuku(model.getBuku(), true);

        fStore.collection(CollectionHelper.pinjam).document(model.getUid()).update("status", StatusPinjam.DIKEMBALIKAN).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                helper.toastMessage("Status berhasil diubah");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                helper.toastMessage("Status gagal diubah");
            }
        });
    }

    public void updateJumlahBuku(Book model, boolean tambah) {
        fStore.collection(CollectionHelper.buku).document(model.getIsbn()).get().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        }).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Book newModel = documentSnapshot.toObject(Book.class);
                int stok = newModel.getStok();
                if(tambah){
                    stok += 1;
                } else {
                    stok -= 1;
                }

                fStore.collection(CollectionHelper.buku).document(newModel.getIsbn()).update("stok", stok);
            }
        });
    }

}