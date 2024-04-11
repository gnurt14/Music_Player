package com.example.musicplayer;

import static com.example.musicplayer.MainActivity.favourites;
import static com.example.musicplayer.MainActivity.musicFiles;

import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class FavouriteAdapter extends RecyclerView.Adapter<FavouriteAdapter.MyViewHolder> {

    private Context mContext;
    static ArrayList<MusicFiles> favouriteFiles;
    View view;

    public FavouriteAdapter(Context mContext, ArrayList<MusicFiles> favouriteFiles){
        this.mContext = mContext;
        this.favouriteFiles = favouriteFiles;
    }

    @NonNull
    @Override
    public FavouriteAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(mContext).inflate(R.layout.music_favourites_items, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavouriteAdapter.MyViewHolder holder, int position) {
        holder.file_name.setText(favouriteFiles.get(position).getTitle());
        try {
            byte[] image = getAlbumArt(favouriteFiles.get(position).getPath());
            if (image != null) {
                Glide.with(mContext)
                        .asBitmap()
                        .load(image)
                        .into(holder.album_art);
            } else {
                // Nếu không có dữ liệu ảnh album, hiển thị hình ảnh mặc định
                Glide.with(mContext)
                        .load(R.drawable.ic_launcher_foreground)
                        .into(holder.album_art);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Glide.with(mContext)
                    .load(R.drawable.ic_launcher_foreground)
                    .into(holder.album_art);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, PlayerActivity.class);
                intent.putExtra("position", position);
                mContext.startActivities(new Intent[]{intent});
            }
        });
        if (holder.favourite_icon != null) {
            holder.favourite_icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    favouriteFiles.remove(favouriteFiles.get(position));
                    notifyDataSetChanged();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return favouriteFiles.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView file_name;
        ImageView album_art, favourite_icon;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            file_name = itemView.findViewById(R.id.music_file_name);
            album_art = itemView.findViewById(R.id.music_img);
            favourite_icon = itemView.findViewById(R.id.favourite);
        }
    }

    private byte[] getAlbumArt(String uri) {
        try {
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(uri);
            byte[] art = retriever.getEmbeddedPicture();
            retriever.release();
            return art;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
