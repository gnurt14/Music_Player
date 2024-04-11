package com.example.musicplayer;

import static com.example.musicplayer.MainActivity.favourites;
import static com.example.musicplayer.MainActivity.musicFiles;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;

public class FavouriteFragment extends Fragment {
    RecyclerView recyclerView;
    FavouriteAdapter favouriteAdapter;

    public FavouriteFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favourite, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        if (favourites != null && !favourites.isEmpty()) {
            favouriteAdapter = new FavouriteAdapter(getContext(), favourites);
            recyclerView.setAdapter(favouriteAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        } else {
            Toast.makeText(getContext(), "No favorite songs found", Toast.LENGTH_LONG).show();
        }
        return view;
    }
    public void updateFavoriteList(ArrayList<MusicFiles> newFavoriteList) {
        favouriteAdapter = new FavouriteAdapter(getContext(), newFavoriteList); // Initialize adapter with new list
        recyclerView.setAdapter(favouriteAdapter); // Set adapter to RecyclerView
        favouriteAdapter.notifyDataSetChanged(); // Notify adapter of data change
    }
}