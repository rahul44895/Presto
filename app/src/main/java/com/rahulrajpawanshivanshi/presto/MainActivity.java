package com.rahulrajpawanshivanshi.presto;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ShareCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements song_list_recyclerViewInterface {

    ImageView home_btn, music_btn;
    ConstraintLayout fragmentCurrPlayMusicCard, fragmentContainerViewHome, fragmentContainerViewMusic, constraintLayoutHeader;
    RecyclerView song_list_recyclerView;
    public ArrayList<String> song_list, artist_list, image_list, url_list, movie_list;
    MediaPlayer mediaPlayer;
    TextView currSongTitle, currSongArtist;
    ImageView currSongImage;
    int global_position = -1, repeat_count = 0;
    ImageView play_btn, next_btn, previous_btn, repeat_btn, shuffle_btn, share_btn;
    SeekBar seekBar;
    TextView repeat_count_text;

    private final Handler handler = new Handler();
    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            seekBar.setProgress(mediaPlayer.getCurrentPosition());
            handler.postDelayed(this, 1000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        home_btn = findViewById(R.id.home);
        music_btn = findViewById(R.id.music);
        fragmentContainerViewHome = findViewById(R.id.fragmentContainerViewHome);
        fragmentContainerViewMusic = findViewById(R.id.fragmentContainerViewMusic);
        fragmentCurrPlayMusicCard = findViewById(R.id.fragmentCurrPlayMusicCard);
        constraintLayoutHeader = findViewById(R.id.constraintLayoutHeader);
        song_list_recyclerView = findViewById(R.id.song_list_recyclerView);
        currSongTitle = findViewById(R.id.songTitle);
        currSongArtist = findViewById(R.id.artistName);
        currSongImage = findViewById(R.id.song_image_display_head);
        play_btn = findViewById(R.id.play_btn);
        next_btn = findViewById(R.id.next);
        previous_btn = findViewById(R.id.previous);
        seekBar = findViewById(R.id.seekBar);
        repeat_btn = findViewById(R.id.repeat_btn);
        shuffle_btn = findViewById(R.id.shuffle_btn);
        share_btn = findViewById(R.id.share_btn);

        repeat_count_text = findViewById(R.id.repeat_text);
        repeat_count_text.setVisibility(View.GONE);

        song_list_recyclerView.setLayoutManager(new LinearLayoutManager(this));

        song_list = new ArrayList<>();
        url_list = new ArrayList<>();
        image_list = new ArrayList<>();
        movie_list = new ArrayList<>();
        artist_list = new ArrayList<>();
        mediaPlayer = new MediaPlayer();
        showHome();

        home_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showHome();
            }
        });
        music_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMusic();

            }
        });
        new JSON().execute();

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean fromUser) {
                if (fromUser) {
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.seekTo(seekBar.getProgress());
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        play_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                play_n_pause();
            }
        });
        next_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                next_song();
            }
        });
        previous_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                previous_song();
            }
        });
        repeat_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (repeat_count < 3 && mediaPlayer.isPlaying()) {
                    repeat_count++;
                    repeat_count_text.setText(String.valueOf(repeat_count));
                    repeat_count_text.setVisibility(View.VISIBLE);

                }
                else{
                    repeat_count=0;
                    repeat_count_text.setText(String.valueOf(repeat_count));
                    repeat_count_text.setVisibility(View.GONE);
                }
            }
        });
        shuffle_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int max_range, min_range;
                max_range = song_list.size();
                min_range = 0;
                int number = (int) (Math.random() * ((max_range - min_range) + 1));
                global_position = number;
                set_music(global_position);
            }
        });
        share_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaPlayer != null && global_position!=-1) {
                    String share_text = "Guys, look what I've discovered.\n*Song Name* :\t".concat(song_list.get(global_position)).concat("\n*Song Url :*\t").concat(url_list.get(global_position));
                    ShareCompat.IntentBuilder
                            .from(MainActivity.this)
                            .setType("text/plain")
                            .setChooserTitle("Presto")
                            .setText(share_text)
                            .startChooser();
//                    Intent intent=new Intent((Intent.ACTION_SEND));
//                    intent.setType("text/plain");
//                    intent.setPackage("com.whatsapp");
//                    intent.putExtra(Intent.EXTRA_TEXT, share_text);
//                    if(intent.resolveActivity(getPackageManager())==null){
//                        Toast.makeText(MainActivity.this, "Please install whatsapp first", Toast.LENGTH_SHORT).show();
//                    }
//                    else
//                    {
//                        startActivity(intent);
//                    }

                }
            }
        });

    }

    public void showHome() {
        fragmentContainerViewHome.setVisibility(View.VISIBLE);
        fragmentContainerViewMusic.setVisibility(View.GONE);
        constraintLayoutHeader.setVisibility(View.VISIBLE);
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            showMusicCardElements();
//            song_list_recyclerView.getLayoutManager().scrollToPosition(global_position);
            song_list_recyclerView.smoothScrollToPosition(global_position);
        }
    }

    public void showMusic() {
        fragmentContainerViewHome.setVisibility(View.GONE);
        fragmentContainerViewMusic.setVisibility(View.VISIBLE);
        fragmentCurrPlayMusicCard.setVisibility(View.GONE);
        constraintLayoutHeader.setVisibility(View.GONE);
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            currSongTitle.setText(song_list.get(global_position));
            currSongArtist.setText(artist_list.get(global_position));
            Picasso.get().load(image_list.get(global_position)).into(currSongImage);
            play_btn.setBackgroundResource(R.drawable.ic_pause);
        }
    }

    public void showMusicCardElements() {
        fragmentCurrPlayMusicCard.setVisibility(View.VISIBLE);
        TextView currSongName, currSongArtist;
        ImageView currPlayingBtn, currNextBtn;

        ImageView currSongImage;
        currSongName = findViewById(R.id.curr_song_name);
        currSongArtist = findViewById(R.id.curr_ARTIST_NAME);
        currSongImage = findViewById(R.id.curr_song_image);
        currPlayingBtn = findViewById(R.id.curr_play_btn);
        currNextBtn = findViewById(R.id.curr_next_btn);

        currSongName.setText(song_list.get(global_position));
        currSongArtist.setText((artist_list.get(global_position)).concat(" " + movie_list.get(global_position)));
        Picasso.get().load(image_list.get(global_position)).into(currSongImage);
        if (mediaPlayer.isPlaying())
            currPlayingBtn.setBackgroundResource(R.drawable.ic_pause);
        else
            currPlayingBtn.setBackgroundResource(R.drawable.ic_play_1);
        currPlayingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaPlayer.isPlaying()) {
                    currPlayingBtn.setBackgroundResource(R.drawable.ic_play_1);
                } else {
                    currPlayingBtn.setBackgroundResource(R.drawable.ic_pause);
                }
                play_n_pause();
            }
        });
        currNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                next_song();
            }
        });

    }

    @Override
    public void onItemClick(int position) {

    }


    public class JSON extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(MainActivity.this, "Please wait while we are loading...", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                URL url = new URL("https://raw.githubusercontent.com/rahul44895/MusicXa/main/db.json");
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.connect();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                String line;
                String data = "";
                while ((line = bufferedReader.readLine()) != null) {
                    data += line + "\n";
                }
                return data;
            } catch (IOException e) {
                Toast.makeText(MainActivity.this, "Unable to load", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            System.out.println(s);
            try {
                JSONArray jsonArray = new JSONArray(s);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    song_list.add(jsonObject.getString("song"));
                    url_list.add(jsonObject.getString("url"));
                    movie_list.add(jsonObject.getString("movie"));
                    image_list.add(jsonObject.getString("image"));
                    artist_list.add(jsonObject.getString("artist"));
                }

                song_list_recyclerView adapter = new song_list_recyclerView(MainActivity.this, song_list, url_list, movie_list, image_list, artist_list, new song_list_recyclerViewInterface() {
                    @Override
                    public void onItemClick(int position) {
                        Toast.makeText(MainActivity.this, song_list.get(position), Toast.LENGTH_SHORT).show();
                        global_position = position;
                        set_music(position);
                    }
                });
                song_list_recyclerView.setAdapter(adapter);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void set_music(int pos) {
        String url = url_list.get(pos);
        try {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
                mediaPlayer.reset();
                mediaPlayer.release();
            }
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(url);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    seekBar.setProgress(0);
                    mediaPlayer.start();
                    seekBar.setMax(mediaPlayer.getDuration());
                    handler.postDelayed(runnable, 1);
                    showMusic();
                }
            });
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    if (repeat_count > 0) {
                        set_music(global_position);
                        repeat_count--;
                        if (repeat_count > 0) {
                            repeat_count_text.setText(String.valueOf(repeat_count));
                            repeat_count_text.setVisibility(View.VISIBLE);
                        } else {
                            repeat_count_text.setVisibility(View.GONE);
                        }


                    } else {
                        next_song();
                    }
                }
            });
        } catch (IOException e) {
            Toast.makeText(this, "Unable to load", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    public void play_n_pause() {
        if (mediaPlayer != null && seekBar.getProgress() != 0) {
            if (mediaPlayer.isPlaying()) {
                play_btn.setBackgroundResource(R.drawable.ic_play);
                mediaPlayer.pause();
            } else {
                play_btn.setBackgroundResource(R.drawable.ic_pause);
                mediaPlayer.start();
            }
        }
    }

    public void next_song() {
        if (global_position != song_list.size() - 1) {
            global_position++;
        } else {
            global_position = 0;
        }
        set_music(global_position);

    }

    public void previous_song() {
        if (global_position > 0) {
            set_music(--global_position);
        }
    }

}