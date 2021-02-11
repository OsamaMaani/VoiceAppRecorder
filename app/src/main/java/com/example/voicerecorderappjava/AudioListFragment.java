package com.example.voicerecorderappjava;

import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.io.File;
import java.io.IOException;


public class AudioListFragment extends Fragment implements VoiceListAdapter.onItemClickListener {

    private ConstraintLayout playerSheet;
    private BottomSheetBehavior mBottomSheetBehavior;
    private RecyclerView audioList;
    private File[] allFiles;
    private VoiceListAdapter mVoiceListAdapter;
    private boolean isPlaying = false;
    private File fileToPlay;
    private MediaPlayer mMediaPlayer;


    //UI Elements
    private ImageButton playBtn;
    private TextView playerHeader;
    private TextView playerFilename;
    private SeekBar  playerSeekBar;
    private Handler seekBarHandler;
    private Runnable updateSeekBar;

    public AudioListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_audio_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        playerSheet = view.findViewById(R.id.player_sheet);
        mBottomSheetBehavior = BottomSheetBehavior.from(playerSheet);
        audioList = view.findViewById(R.id.rv_audio_list);

        playBtn = view.findViewById(R.id.player_play_btn);
        playerHeader = view.findViewById(R.id.player_header_title);
        playerFilename = view.findViewById(R.id.player_file_name);
        playerSeekBar = view.findViewById(R.id.player_seek_bar);

        String path  = getActivity().getExternalFilesDir("/").getPath();
        File file = new File(path);
        allFiles = file.listFiles();
        mVoiceListAdapter = new VoiceListAdapter(allFiles,this);
        audioList.setHasFixedSize(true);
        audioList.setAdapter(mVoiceListAdapter);
        audioList.setLayoutManager(new LinearLayoutManager(getContext()));

        playerSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if(fileToPlay != null){
                    pauseAudio();
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
              if (fileToPlay != null){
                  int progress = seekBar.getProgress();
                  mMediaPlayer.seekTo(progress);
                  resumeAudio();

              }
            }
        });

        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPlaying){
                    pauseAudio();
                }else{
                    if(fileToPlay!= null) {
                        resumeAudio();
                    }
                    }
            }
        });

        mBottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if(newState == BottomSheetBehavior.STATE_HIDDEN){
                    mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
               //Nothing to do here for this app.
            }
        });
    }

    @Override
    public void onClickListener(File file, int position) {
        fileToPlay = file;
        if(isPlaying){
               stopAudio();
           }else{
               try {
                   playAudio(fileToPlay);
               } catch (IOException e) {
                   e.printStackTrace();
               }
           }
    }

    private void pauseAudio(){
        isPlaying = false;
        mMediaPlayer.pause();
        playBtn.setImageDrawable(ResourcesCompat.getDrawable(getResources(),R.drawable.player_pause_btn,null));
        seekBarHandler.removeCallbacks(updateSeekBar);
    }
    private void resumeAudio(){
        isPlaying = true;
        mMediaPlayer.start();
        playBtn.setImageDrawable(ResourcesCompat.getDrawable(getResources(),R.drawable.player_play_btn,null));
        updateRunnable();
        seekBarHandler.postDelayed(updateSeekBar,0);
    }

    private void stopAudio() {
        isPlaying = false;
        playBtn.setImageDrawable(ResourcesCompat.getDrawable(getResources(),R.drawable.player_play_btn,null));
        playerHeader.setText("Stopped");
        mMediaPlayer.stop();
        seekBarHandler.removeCallbacks(updateSeekBar);
    }

    private void playAudio(File fileToPlay) throws IOException {
       isPlaying = true;

       mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

       mMediaPlayer = new MediaPlayer();
       mMediaPlayer.setDataSource(fileToPlay.getAbsolutePath());
       mMediaPlayer.prepare();
       mMediaPlayer.start();
       mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mMediaPlayer.stop();
                playerHeader.setText("Finished");
            }
        });

       playBtn.setImageDrawable(ResourcesCompat.getDrawable(getResources(),R.drawable.player_pause_btn,null));
       playerFilename.setText(fileToPlay.getName());
       playerHeader.setText("Playing");

       playerSeekBar.setMax(mMediaPlayer.getDuration());
       seekBarHandler = new Handler();
       updateRunnable();
       seekBarHandler.postDelayed(updateSeekBar,0);



    }

    private void updateRunnable() {
        updateSeekBar = new Runnable() {
            @Override
            public void run() {
                playerSeekBar.setProgress(mMediaPlayer.getCurrentPosition());
                seekBarHandler.postDelayed(this,5000);
            }
        };
    }

    @Override
    public void onStop() {
        super.onStop();
        if(isPlaying){
            stopAudio();
        }
    }
}