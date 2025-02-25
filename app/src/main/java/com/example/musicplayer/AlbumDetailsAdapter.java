package com.example.musicplayer;

import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class AlbumDetailsAdapter extends RecyclerView.Adapter<AlbumDetailsAdapter.MyHolder> {
    private Context mContext;
    static ArrayList<MusicFiles> albumFiles;
    View view;
    public AlbumDetailsAdapter(Context mContext, ArrayList<MusicFiles> albumFiles){
        this.mContext = mContext;
        this.albumFiles = albumFiles;
    }
    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(mContext).inflate(R.layout.music_items, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        // lay ten bai hat
        holder.album_name.setText(albumFiles.get(position).getTitle());
        try {
            byte[] image = getAlbumArt(albumFiles.get(position).getPath());
            if (image != null) {
                Glide.with(mContext)
                        .asBitmap()
                        .load(image)
                        .into(holder.album_image);
            } else {
                Glide.with(mContext)
                        .load(R.drawable.ic_launcher_foreground)
                        .into(holder.album_image);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Glide.with(mContext)
                    .load(R.drawable.ic_launcher_foreground)
                    .into(holder.album_image);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, PlayerActivity.class);
                intent.putExtra("sender", "albumDetails");
                intent.putExtra("position", position);
                mContext.startActivities(new Intent[]{intent});
            }
        });
    }

    @Override
    public int getItemCount() {
        return albumFiles.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder{
        ImageView album_image;
        TextView album_name;
        public MyHolder(@NonNull View itemView) {
            super(itemView);
            // Hien thi ten va anh cua bai hat
            album_image = itemView.findViewById(R.id.music_img);
            album_name = itemView.findViewById(R.id.music_file_name);
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
    public void RemoveFile(MusicFiles deletedFile){
        int position = albumFiles.indexOf(deletedFile);
        if(position != -1){
            albumFiles.remove(position);
            notifyItemRemoved(position);
            notifyItemChanged(position, albumFiles.size());
        }
    }
}
