package com.example.musicplayer;

import static com.example.musicplayer.AlbumDetails.albumDetailsAdapter;
import static com.example.musicplayer.MainActivity.favourites;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MyViewHolder> {

    private Context mContext;
    static ArrayList<MusicFiles> mFiles;

    MusicAdapter(Context mContext, ArrayList<MusicFiles> mFiles){
        this.mFiles = mFiles;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public MusicAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.music_items, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.file_name.setText(mFiles.get(position).getTitle());
        try {
            byte[] image = getAlbumArt(mFiles.get(position).getPath());
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
        holder.menu_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                PopupMenu popupMenu = new PopupMenu(mContext, v);
                popupMenu.getMenuInflater().inflate(R.menu.popup, popupMenu.getMenu());
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener((item) -> {
                    if (item.getItemId() == R.id.delete){
                        mFiles.get(position).setDelete(true);
                        mFiles.remove(position);
                        notifyDataSetChanged();
                        Snackbar.make(v, "File is removed from list", Snackbar.LENGTH_LONG).show();
                    }
                    else if(item.getItemId() == R.id.favourite){
                        MusicFiles selectedSong = mFiles.get(position);
                        if (!selectedSong.getStatus()) { // Check if the song is not already a favorite
                            selectedSong.setStatus(true); // Mark the song as a favorite
                            favourites.add(selectedSong); // Add the song to the favorites list
                            Toast.makeText(mContext, "Added to favorites", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(mContext, "Already in favorites", Toast.LENGTH_SHORT).show();
                        }
                    }
                    return true;
                });
            }
        });
    }
    @Override
    public int getItemCount() {
        return mFiles.size();
    }
    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView file_name;
        ImageView album_art, menu_more;
        public MyViewHolder(@NonNull View itemView){
            super(itemView);
            file_name = itemView.findViewById(R.id.music_file_name);
            album_art = itemView.findViewById(R.id.music_img);
            menu_more = itemView.findViewById(R.id.menuMore);
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

    void updateList(ArrayList<MusicFiles> musicFilesArrayList){
        mFiles = new ArrayList<>();
        mFiles.addAll(musicFilesArrayList);
        notifyDataSetChanged();
    }
}
