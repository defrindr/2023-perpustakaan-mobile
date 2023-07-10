package com.example.perpusmini;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.perpusmini.enums.Role;
import com.example.perpusmini.enums.StatusPinjam;
import com.example.perpusmini.helpers.Helper;
import com.example.perpusmini.models.PinjamModel;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.util.HashMap;

public class PeminjamanAdapter extends FirestoreRecyclerAdapter<PinjamModel, PeminjamanAdapter.Pinjam> {
    private Context context;

    private HashMap<String, String> queries = new HashMap<>();
    private Role role = Role.GUEST;
    private Helper helper;

    public PeminjamanAdapter(@NonNull Context context, @NonNull FirestoreRecyclerOptions options) {
        super(options);
        this.context = context;
        helper = new Helper(this.context);
    }

    public void disableActionButton(Role r) {
        this.role = r;
    }

    @Override
    protected void onBindViewHolder(@NonNull Pinjam holder, int position, @NonNull PinjamModel model) {
        holder.setModel(model);
        setupTextView(holder, model);
        setupButtonView(holder, model);
    }

    private void setupTextView(Pinjam holder, PinjamModel model) {
        int denda = 0;

        long waktuSekarang = helper.getEpochTime();
        long waktuMaksimalKembali = 0;
        int telatDalamHari = 0;

        try {
            waktuMaksimalKembali = helper.getEpochTime(model.getTglKembali());
        } catch (Exception e) {
        }

        if (waktuMaksimalKembali != 0 && waktuSekarang > waktuMaksimalKembali) {
            telatDalamHari = (int) ((waktuSekarang - waktuMaksimalKembali) / (1000 * 60 * 60 * 24));
        }

        if (telatDalamHari > 0) {
            denda = telatDalamHari * Helper.DENDA;
        }

        holder.tvUid.setText("ID = " + model.getUid());
        holder.tvUserReference.setText("Peminjam = " + model.getUser().getUsername());
        holder.tvBukuReference.setText("Buku = " + model.getBuku().getJudul());
        holder.tvTglPinjam.setText("Tanggal Pinjam = " + model.getTglPinjam());
        holder.tvTglKembali.setText("Tanggal Kembali = " + model.getTglKembali());
        holder.tvDenda.setText("Denda = Rp " + denda);
        holder.tvStatus.setText("Status = " + model.getStatus().toString());
    }


    private void setupButtonView(Pinjam holder, PinjamModel model) {
        holder.btnHapus.setVisibility(View.GONE);
        holder.btnTolak.setVisibility(View.GONE);
        holder.btnSetuju.setVisibility(View.GONE);
        holder.btnKembali.setVisibility(View.GONE);
        holder.btnTolakKembali.setVisibility(View.GONE);
        holder.btnSetujuKembali.setVisibility(View.GONE);

        if (role == Role.PEMINJAM) {
            if (model.getStatus() == StatusPinjam.MENUNGGU_PERSETUJUAN_PINJAM) {
                holder.btnHapus.setVisibility(View.VISIBLE);
            } else if (model.getStatus() == StatusPinjam.DIPINJAM) {
                holder.btnKembali.setVisibility(View.VISIBLE);
            }
        } else {
            if (model.getStatus() == StatusPinjam.MENUNGGU_PERSETUJUAN_PINJAM) {
                holder.btnSetuju.setVisibility(View.VISIBLE);
                holder.btnTolak.setVisibility(View.VISIBLE);
            } else if (model.getStatus() == StatusPinjam.MENUNGGU_PERSETUJUAN_KEMBALI) {
                holder.btnSetujuKembali.setVisibility(View.VISIBLE);
                holder.btnTolakKembali.setVisibility(View.VISIBLE);
            }
        }
    }

    @NonNull
    @Override
    public PeminjamanAdapter.Pinjam onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.fragment_peminjaman_adapter, viewGroup, false);
        return new PeminjamanAdapter.Pinjam(view, this.context);
    }

    class Pinjam extends RecyclerView.ViewHolder {
        Context context;
        PinjamModel model;
        private TextView tvUid, tvUserReference, tvBukuReference, tvTglPinjam, tvTglKembali, tvDenda, tvStatus;
        private Button btnHapus, btnTolak, btnSetuju, btnKembali, btnTolakKembali, btnSetujuKembali;

        public Pinjam(@NonNull View itemView, @NonNull Context ctx) {
            super(itemView);
            context = ctx;

            //            TextView
            tvUid = itemView.findViewById(R.id.uid);
            tvUserReference = itemView.findViewById(R.id.user_reference);
            tvBukuReference = itemView.findViewById(R.id.book_reference);
            tvTglPinjam = itemView.findViewById(R.id.tgl_pinjam);
            tvTglKembali = itemView.findViewById(R.id.tgl_kembali);
            tvDenda = itemView.findViewById(R.id.denda);
            tvStatus = itemView.findViewById(R.id.status);

            //            Button
            btnHapus = itemView.findViewById(R.id.btnHapus);
            btnTolak = itemView.findViewById(R.id.btnTolak);
            btnSetuju = itemView.findViewById(R.id.btnSetuju);
            btnKembali = itemView.findViewById(R.id.btnKembali);
            btnTolakKembali = itemView.findViewById(R.id.btnTolakKembali);
            btnSetujuKembali = itemView.findViewById(R.id.btnSetujuKembali);


            btnHapus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (context instanceof DaftarPeminjaman) {
                        ((DaftarPeminjaman) context).hapus(model);
                    }
                }
            });

            btnTolak.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (context instanceof DaftarPeminjaman) {
                        ((DaftarPeminjaman) context).tolak(model);
                    }
                }
            });

            btnSetuju.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (context instanceof DaftarPeminjaman) {
                        ((DaftarPeminjaman) context).setujui(model);
                    }
                }
            });


            btnKembali.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (context instanceof DaftarPeminjaman) {
                        ((DaftarPeminjaman) context).kembalikan(model);
                    }
                }
            });

            btnTolakKembali.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (context instanceof DaftarPeminjaman) {
                        ((DaftarPeminjaman) context).tolakKembali(model);
                    }
                }
            });

            btnSetujuKembali.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (context instanceof DaftarPeminjaman) {
                        ((DaftarPeminjaman) context).setujuiKembali(model);
                    }
                }
            });


        }

        private void setModel(com.example.perpusmini.models.PinjamModel model) {
            this.model = model;
        }

        public void itemView(Object o) {
        }
    }
}