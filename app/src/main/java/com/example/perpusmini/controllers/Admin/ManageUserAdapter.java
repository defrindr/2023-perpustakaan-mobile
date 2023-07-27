package com.example.perpusmini.controllers.Admin;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.perpusmini.DaftarBuku;
import com.example.perpusmini.R;
import com.example.perpusmini.controllers.Peminjam.Pinjam;
import com.example.perpusmini.enums.Role;
import com.example.perpusmini.models.Book;
import com.example.perpusmini.models.User;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.util.HashMap;
import java.util.Map;

public class ManageUserAdapter extends FirestoreRecyclerAdapter<User, ManageUserAdapter.User> {
    private Context context;

    private HashMap<String, String> queries = new HashMap<>();
    private Role role = Role.GUEST;

    public ManageUserAdapter(@NonNull Context context, @NonNull FirestoreRecyclerOptions options) {
        super(options);
        this.context = context;
    }

    public void searchLike(String field, String argument) {
        this.queries.put(field, argument);
    }

    public void disableActionButton(Role r) {
        this.role = r;
    }

    protected void hideHolder(User holder) {
        holder.itemView.setVisibility(View.GONE);
        RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) holder.itemView.getLayoutParams();
        layoutParams.height = 0;
        layoutParams.width = 0;
        holder.itemView(layoutParams);
    }

    @Override
    protected void onBindViewHolder(@NonNull User holder, int position, @NonNull com.example.perpusmini.models.User model) {
        holder.bookId.setText("EMAIL : " + model.getEmail());
        holder.bookType.setText("USERNAME : " + model.getUsername());
        holder.bookAvailable.setText("NO HP : " + model.getNoHp());

        // show The Image in a ImageView
//        new DownloadImageTask(holder.imageView).execute(model.getGambar());

        // Load and resize the image using Glide
        RequestOptions requestOptions = new RequestOptions()
                .override(300, 200) // Specify the desired width and height for resizing
                .transform(new RoundedCorners(20)); // Optional: apply rounded corners to the image

        Glide.with(this.context)
                .load(model.getFotoKtp())
                .apply(requestOptions)
                .into(holder.imageView);

        holder.imageView.setOnClickListener(View-> {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(model.getFotoKtp()));
            context.startActivity(i);
        });

        holder.btnAksiPinjam.setOnClickListener(View-> {
            ((ManageUserActivity) context).activateUser(model);
        });

//        holder.btnAksiPinjam.setVisibility(View.GONE);
//        holder.btnAksiUbah.setVisibility(View.GONE);
//        holder.btnAksiHapus.setVisibility(View.GONE);

//        if (this.role == Role.ADMIN) {
//            holder.btnAksiUbah.setVisibility(View.VISIBLE);
//            holder.btnAksiHapus.setVisibility(View.VISIBLE);
//        } else if(this.role == Role.PEMINJAM) {
//            holder.btnAksiPinjam.setVisibility(View.VISIBLE);
//        }
    }

    @NonNull
    @Override
    public User onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.user_view, viewGroup, false);
        return new User(view, this.context);
    }

    class User extends RecyclerView.ViewHolder {
        Context context;
        com.example.perpusmini.models.User user;
        TextView bookName, bookId, bookType, bookAvailable, bookTotal, bookRating, deleteCause;
        Button btnAksiPinjam, btnAksiHapus, btnAksiUbah;

        ImageView imageView;

        public User(@NonNull View itemView, @NonNull Context ctx) {
            super(itemView);
            bookId = itemView.findViewById(R.id.bookId);
            bookAvailable = itemView.findViewById(R.id.bookAvailable);
            bookName = itemView.findViewById(R.id.bookName);
            bookType = itemView.findViewById(R.id.bookType);
            bookTotal = itemView.findViewById(R.id.bookTotal);
            bookRating = itemView.findViewById(R.id.bookRating);

            context = ctx;

            btnAksiPinjam = itemView.findViewById(R.id.aksiKonfirmasi);
//            btnAksiHapus = itemView.findViewById(R.id.aksiHapusBuku);
//            btnAksiUbah = itemView.findViewById(R.id.aksiUbah);
            imageView = itemView.findViewById(R.id.imageView);
            deleteCause = itemView.findViewById(R.id.deleteCause);


        }



        public void itemView(Object o) {
        }
    }
}
