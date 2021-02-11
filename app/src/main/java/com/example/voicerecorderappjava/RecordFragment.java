package com.example.voicerecorderappjava;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RecordFragment extends Fragment implements View.OnClickListener {

    private NavController mNavController;
    private ImageButton imgButtonList;
    private ImageButton recordBtn;
    private String recordPermission = Manifest.permission.RECORD_AUDIO;
    private boolean isRecording = false;
    private int permissionCode = 1;
    private MediaRecorder mMediaRecorder;
    private String recordFile;
    private Chronometer timer;
    private TextView tvFileName;
    public RecordFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_record, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mNavController = Navigation.findNavController(view);
        imgButtonList = view.findViewById(R.id.recorder_list_btn);
        recordBtn = view.findViewById(R.id.record_btn);
        timer = view.findViewById(R.id.record_timer);
        tvFileName = view.findViewById(R.id.file_name);
        imgButtonList.setOnClickListener(this);
        recordBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.recorder_list_btn:
                if(isRecording){
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
                    alertDialog.setTitle("App is still recording");
                    alertDialog.setMessage("Do you really want to leave ?");
                    alertDialog.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                          mNavController.navigate(R.id.action_recordFragment_to_audioListFragment);
                          isRecording = false;
                        }
                    });
                    alertDialog.setNegativeButton("Cancel",null);
                    alertDialog.create().show();
                }else{
                    mNavController.navigate(R.id.action_recordFragment_to_audioListFragment);
                }

                break;
            case R.id.record_btn:
                if (isRecording) {
                    // stop recording
                    stopRecording();
                    Log.d(RecordFragment.class.getSimpleName(), "onClick: Stopped ");
                    recordBtn.setImageDrawable(ResourcesCompat.getDrawable(v.getResources(), R.drawable.btnrecorder, null));
                    isRecording = false;
                } else {
                    //start recording if permission is granted.
                    if (checkPermission()) {
                        startRecording();
                        recordBtn.setImageDrawable(ResourcesCompat.getDrawable(v.getResources(), R.drawable.btnpause, null));
                        isRecording = true;
                    }
                }
                break;
        }
    }

    private void startRecording() {
        timer.setBase(SystemClock.elapsedRealtime());
        timer.start();
        String recordPath = getActivity().getExternalFilesDir("/").getPath();
        SimpleDateFormat formatter  = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");
        Date now = new Date();

        recordFile = "Recording "+formatter.format(now)+".3gp";
        tvFileName.setText("Recording file name : \n"+recordFile);
        mMediaRecorder = new MediaRecorder();
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mMediaRecorder.setOutputFile(recordPath+"/"+recordFile);
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        try {
            mMediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mMediaRecorder.start();
    }

    private void stopRecording() {
        timer.stop();
        mMediaRecorder.stop();
        mMediaRecorder.release();
        tvFileName.setText("Recording stopped file stopped : "+ recordFile);
        mMediaRecorder =null;
    }

    private boolean checkPermission() {
        if (ActivityCompat.checkSelfPermission(getContext(), recordPermission)
                == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{recordPermission}
                    , permissionCode);
            return false;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if(isRecording){
            stopRecording();
        }
    }
}