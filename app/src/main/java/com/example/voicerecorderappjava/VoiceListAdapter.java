package com.example.voicerecorderappjava;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;

public class VoiceListAdapter extends RecyclerView.Adapter<VoiceListAdapter.VoiceListViewHolder> {

    private File[] allFiles;
    private TimeAgo mTimeAgo;
    private onItemClickListener mOnItemClickListener;

    public VoiceListAdapter(File[] allFiles, onItemClickListener onItemClickListener) {
        this.allFiles = allFiles;
        this.mOnItemClickListener  = onItemClickListener;
    }


    @NonNull
    @Override
    public VoiceListAdapter.VoiceListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_list_item, parent, false);
        mTimeAgo = new TimeAgo();
        return new VoiceListViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VoiceListAdapter.VoiceListViewHolder holder, int position) {
          holder.title.setText(allFiles[position].getName());
          holder.date.setText(mTimeAgo.getTimeAgo(allFiles[position].lastModified()));

    }

    @Override
    public int getItemCount() {
        return this.allFiles.length;
    }


   class VoiceListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView title ;
        TextView date;
        ImageView img;
        public VoiceListViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tv_title);
            date = itemView.findViewById(R.id.tv_date);
            img = itemView.findViewById(R.id.list_img);
            itemView.setOnClickListener(this);
        }

       @Override
       public void onClick(View v) {
        mOnItemClickListener.onClickListener(allFiles[getAdapterPosition()],getAdapterPosition());
       }
   }

   public interface onItemClickListener{
        void onClickListener(File file,int position);
   }
}
