package com.example.perpusmini;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.perpusmini.controllers.Admin.UbahBuku;
import com.example.perpusmini.controllers.Peminjam.Pinjam;
import com.example.perpusmini.enums.Role;
import com.example.perpusmini.models.Book;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.util.HashMap;
import java.util.Map;

public class BookAdapter extends FirestoreRecyclerAdapter<Book, BookAdapter.Book> {
    private Context context;

    private HashMap<String, String> queries = new HashMap<>();
    private Role role = Role.GUEST;

    public BookAdapter(@NonNull Context context, @NonNull FirestoreRecyclerOptions options) {
        super(options);
        this.context = context;
    }

    public void searchLike(String field, String argument) {
        this.queries.put(field, argument);
    }

    public void disableActionButton(Role r) {
        this.role = r;
    }

    protected void hideHolder(Book holder) {
        holder.itemView.setVisibility(View.GONE);
        RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) holder.itemView.getLayoutParams();
        layoutParams.height = 0;
        layoutParams.width = 0;
        holder.itemView(layoutParams);
    }

    @Override
    protected void onBindViewHolder(@NonNull Book holder, int position, @NonNull com.example.perpusmini.models.Book model) {
        holder.setBuku(model);
        holder.bookId.setText("ISBN : " + model.getIsbn());
        holder.bookType.setText("Kategori : " + model.getKategori());
        holder.bookAvailable.setText("Jumlah Tersedia : " + model.getAvailable());
        holder.bookName.setText("Judul : " + model.getJudul());
        holder.bookTotal.setText("Pengarang : " + model.getPengarang());

//        Handle Filter
        for (Map.Entry<String, String> query :
                queries.entrySet()) {
            if (query.getKey() == "judul") {
                if (!model.getJudul().contains(query.getValue().toUpperCase())) {
                    hideHolder(holder);
                }
            }
            if (query.getKey() == "pengarang") {
                if (!model.getPengarang().toUpperCase().contains(query.getValue().toUpperCase())) {
                    hideHolder(holder);
                }
            }
        }

        holder.btnAksiPinjam.setVisibility(View.GONE);
        holder.btnAksiUbah.setVisibility(View.GONE);
        holder.btnAksiHapus.setVisibility(View.GONE);

        if (this.role == Role.ADMIN) {
            holder.btnAksiUbah.setVisibility(View.VISIBLE);
            holder.btnAksiHapus.setVisibility(View.VISIBLE);
        } else if(this.role == Role.PEMINJAM) {
            holder.btnAksiPinjam.setVisibility(View.VISIBLE);
        }
    }

    @NonNull
    @Override
    public Book onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.book_view, viewGroup, false);
        return new Book(view, this.context);
    }

    class Book extends RecyclerView.ViewHolder {
        Context context;
        com.example.perpusmini.models.Book buku;
        TextView bookName, bookId, bookType, bookAvailable, bookTotal;
        Button btnAksiPinjam, btnAksiHapus, btnAksiUbah;

        public Book(@NonNull View itemView, @NonNull Context ctx) {
            super(itemView);
            bookId = itemView.findViewById(R.id.bookId);
            bookAvailable = itemView.findViewById(R.id.bookAvailable);
            bookName = itemView.findViewById(R.id.bookName);
            bookType = itemView.findViewById(R.id.bookType);
            bookTotal = itemView.findViewById(R.id.bookTotal);

            context = ctx;

            btnAksiPinjam = itemView.findViewById(R.id.aksiPinjamBuku);
            btnAksiHapus = itemView.findViewById(R.id.aksiHapusBuku);
            btnAksiUbah = itemView.findViewById(R.id.aksiUbah);


            btnAksiHapus.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            hapus();
                        }
                    });
            btnAksiPinjam.setOnClickListener(actionBorrow());

            btnAksiUbah.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent target = new Intent(context, UbahBuku.class);
                    target.putExtra("ISBN", buku.getIsbn());
                    context.startActivity(target);
                }
            });
        }

        private void setBuku(com.example.perpusmini.models.Book buku) {
            this.buku = buku;
        }

        private void hapus() {
            if (context instanceof DaftarBuku) {
                ((DaftarBuku) context).hapusBuku(buku);
            }
        }


        private View.OnClickListener actionBorrow() {
            return new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent target = new Intent(context, Pinjam.class);
                    target.putExtra("ISBN", buku.getIsbn());
                    context.startActivity(target);
                }
            };
        }

        public void itemView(Object o) {
        }
    }
}