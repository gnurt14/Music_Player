        package com.example.musicplayer;

        import static android.app.ProgressDialog.show;

        import static com.example.musicplayer.MusicService.MUSIC_FILE;
        import static com.example.musicplayer.MusicService.MUSIC_LAST_PLAYED;

        import androidx.annotation.NonNull;
        import androidx.annotation.Nullable;
        import androidx.appcompat.app.AppCompatActivity;
        import androidx.appcompat.widget.SearchView;
        import androidx.core.app.ActivityCompat;
        import androidx.core.content.ContextCompat;
        import androidx.fragment.app.Fragment;
        import androidx.fragment.app.FragmentManager;
        import androidx.fragment.app.FragmentPagerAdapter;
        import androidx.viewpager.widget.ViewPager;

        import android.Manifest;
        import android.content.Context;
        import android.content.Intent;
        import android.content.SharedPreferences;
        import android.content.pm.PackageManager;
        import android.database.Cursor;
        import android.net.Uri;
        import android.os.Bundle;
        import android.provider.MediaStore;
        import android.view.Menu;
        import android.view.MenuItem;
        import android.view.View;
        import android.widget.FrameLayout;
        import android.widget.Toast;

        import com.google.android.material.tabs.TabLayout;

        import java.util.ArrayList;
        import java.util.HashSet;
        import java.util.Objects;
        import java.util.Set;

        public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

            static ArrayList<MusicFiles> musicFiles;
            static ArrayList<MusicFiles> albums = new ArrayList<>();
            static ArrayList<MusicFiles> favourites = new ArrayList<>();
            static boolean shuffleBoolean = false;
            static boolean repeatBoolean = false;
            private String MY_FAV_PREF = "FavOrder";
            public static boolean SHOW_MINI_PLAYER = false;
            public static String PATH_TO_FRAG = null;
            public static String SONG_TO_FRAG = null;
            public static final String SONG_NAME = "SONG NAME";

            public static final int REQUEST_CODE = 1;
            FrameLayout frag_bottom_player;
            @Override
            protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_main);
                permission();
                loadFavoriteSongs();
                initViewPager();
                frag_bottom_player = findViewById(R.id.frag_bottom_player);
                frag_bottom_player.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String tempSong = SONG_TO_FRAG;
                        int position = -1;
                        for(int i = 0; i < musicFiles.size(); i++){
                            if(musicFiles.get(i).getTitle().equals(tempSong)){
                                position = i;
                                break;
                            }
                        }
                        if (position != -1) {
                            Intent intent = new Intent(MainActivity.this, PlayerActivity.class);
                            intent.putExtra("position", position);
                            startActivity(intent);
                        } else {
                            // Handle the case when the song is not found
                            Toast.makeText(MainActivity.this, "Song not found", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }

            private void permission() {
                if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED)
                {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE);
                }
                else
                {
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                    musicFiles = getAllAudio(this);
                }
            }

            @Override
            public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                if(requestCode == REQUEST_CODE){
                    if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    {
                        musicFiles = getAllAudio(this);
                        Toast.makeText(this, "Get all music successfully", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE);
                    }
                }
            }

            private void initViewPager() {
                ViewPager viewPager = findViewById(R.id.viewpager);
                TabLayout tabLayout = findViewById(R.id.tab_layout);
                ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
                viewPagerAdapter.addFragments(new SongsFragment(), "Songs");
                viewPagerAdapter.addFragments(new AlbumFragment(), "Albums");
                viewPagerAdapter.addFragments(new FavouriteFragment(), "Favourites");
                viewPager.setAdapter(viewPagerAdapter);
                tabLayout.setupWithViewPager(viewPager);
            }

            public static class ViewPagerAdapter extends FragmentPagerAdapter{

                private ArrayList<Fragment> fragments;
                private ArrayList<String> titles;
                public ViewPagerAdapter(@NonNull FragmentManager fm) {
                    super(fm);
                    this.fragments = new ArrayList<>();
                    this.titles = new ArrayList<>();
                }

                void addFragments(Fragment fragment, String title){
                    fragments.add(fragment);
                    titles.add(title);
                }

                @NonNull
                @Override
                public Fragment getItem(int position) {
                    return fragments.get(position);
                }

                @Override
                public int getCount() {
                    return fragments.size();
                }

                @Nullable
                @Override
                public CharSequence getPageTitle(int position) {
                    return titles.get(position);
                }
            }
            public static ArrayList<MusicFiles> getAllAudio(Context context) {
                HashSet<String> uniqueAlbums = new HashSet<>();
                ArrayList<MusicFiles> tempAudioList = new ArrayList<>();
                Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                String[] projection = {
                        MediaStore.Audio.Media.ALBUM,
                        MediaStore.Audio.Media.TITLE,
                        MediaStore.Audio.Media.DURATION,
                        MediaStore.Audio.Media.DATA, // path
                        MediaStore.Audio.Media.ARTIST,
                        MediaStore.Audio.Media._ID
                };
                //order
                String sortOrder = MediaStore.Audio.Media.DATE_ADDED + " DESC";

                try (Cursor cursor = context.getContentResolver().query(uri, projection, null, null, sortOrder)){
                    if (cursor != null) {
                        int columnIndexAlbum = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);
                        int columnIndexTitle = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
                        int columnIndexDuration = cursor.getColumnIndex(MediaStore.Audio.Media.DURATION);
                        int columnIndexPath = cursor.getColumnIndex(MediaStore.Audio.Media.DATA);
                        int columnIndexArtist = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
                        int columnIndexId = cursor.getColumnIndex(MediaStore.Audio.Media._ID);

                        while (cursor.moveToNext()) {
                            String album = (columnIndexAlbum != -1) ? cursor.getString(columnIndexAlbum) : "";
                            String title = (columnIndexTitle != -1) ? cursor.getString(columnIndexTitle) : "";
                            String duration = (columnIndexDuration != -1) ? cursor.getString(columnIndexDuration) : "";
                            String path = (columnIndexPath != -1) ? cursor.getString(columnIndexPath) : "";
                            String artist = (columnIndexArtist != -1) ? cursor.getString(columnIndexArtist) : "";
                            String id = (columnIndexId != -1) ? cursor.getString(columnIndexId) : "";

                            if (path != null && !path.toLowerCase().endsWith(".ogg")) {
                                MusicFiles song = new MusicFiles(path, title, artist, album, duration, id);
                                if(!song.getDelete())
                                {
                                    tempAudioList.add(song);
                                }
                                if(!uniqueAlbums.contains(album) && !song.getDelete()){
                                    albums.add(song);
                                    uniqueAlbums.add(album);
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return tempAudioList;
            }

            private void saveFavouriteSongs(){
                SharedPreferences sharedPreferences = getSharedPreferences(MY_FAV_PREF, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                Set<String> favoriteIds = new HashSet<>();
                for (MusicFiles musicFile : favourites) {
                    favoriteIds.add(musicFile.getId());
                }
                editor.putStringSet("favoriteIds", favoriteIds);
                editor.apply();
            }
            private void loadFavoriteSongs() {
                SharedPreferences sharedPreferences = getSharedPreferences(MY_FAV_PREF, MODE_PRIVATE);
                Set<String> favoriteIds = sharedPreferences.getStringSet("favoriteIds", new HashSet<>());
                favourites.clear();
                for (MusicFiles musicFile : musicFiles) {
                    if (favoriteIds.contains(musicFile.getId())) {
                        favourites.add(musicFile);
                    }
                }
            }

            @Override
            protected void onResume() {
                super.onResume();
                loadFavoriteSongs(); // Reload favorite songs when the activity is resumed
                FavouriteFragment favouriteFragment = (FavouriteFragment) getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.viewpager + ":" + 2);
                if (favouriteFragment != null) {
                    favouriteFragment.updateFavoriteList(favourites);
                }
                SharedPreferences preferences = getSharedPreferences(MUSIC_LAST_PLAYED,
                        MODE_PRIVATE);
                String path = preferences.getString(MUSIC_FILE, null);
                String song_name = preferences.getString(SONG_NAME, null);
                if(path != null){
                    SHOW_MINI_PLAYER = true;
                    PATH_TO_FRAG = path;
                    SONG_TO_FRAG = song_name;
                }else{
                    SHOW_MINI_PLAYER = false;
                    PATH_TO_FRAG = null;
                    SONG_TO_FRAG = null;
                }
            }


            @Override
            public boolean onCreateOptionsMenu(Menu menu) {
                getMenuInflater().inflate(R.menu.search, menu);
                MenuItem item = menu.findItem(R.id.search_option);
                SearchView searchView = (SearchView) item.getActionView();
                Objects.requireNonNull(searchView).setOnQueryTextListener(this);
                return super.onCreateOptionsMenu(menu);
            }
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                String userInput = newText.toLowerCase();
                ArrayList<MusicFiles> myFiles = new ArrayList<>();
                for(MusicFiles song : musicFiles){
                    if(song.getTitle().toLowerCase().contains(userInput)){
                        myFiles.add(song);
                    }
                }
                SongsFragment.musicAdapter.updateList(myFiles);
                return true;
            }

            @Override
            public boolean onOptionsItemSelected(@NonNull MenuItem item) {
                SharedPreferences.Editor editor = getSharedPreferences(MY_FAV_PREF, MODE_PRIVATE).edit();
                if(item.getItemId() == R.id.favourite){
                    editor.putString("Add", "addToFavourite");
                    editor.apply();
                    saveFavouriteSongs();
                    this.recreate();
                }
                return super.onOptionsItemSelected(item);
            }
        }