package com.arvindp.unscramblethewords;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ExampleAdapter2 extends RecyclerView.Adapter<ExampleAdapter2.ExampleViewHolder> {

    private ArrayList<ExampleItem2> mExampleList;
    private ExampleAdapter2.OnItemClickListener mListener;

    public void setOnItemClickListener(ExampleAdapter2.OnItemClickListener listener) {
        mListener = listener;
    }

    public interface OnItemClickListener {
        void onDeleteClick(int position);
    }

    public static class ExampleViewHolder extends RecyclerView.ViewHolder {

        public TextView mTextView;
        public ImageView mDeleteImage;

        public ExampleViewHolder(View itemView, final ExampleAdapter2.OnItemClickListener listener) {

            super(itemView);
            mTextView = itemView.findViewById(R.id.textView);
            mDeleteImage = itemView.findViewById(R.id.image_delete);

            mDeleteImage.setOnClickListener(view -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onDeleteClick(position);
                    }
                }
            });
        }
    }

    public ExampleAdapter2(ArrayList<ExampleItem2> exampleList) {
        mExampleList = exampleList;
    }

    @NonNull
    @Override
    public ExampleAdapter2.ExampleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.example_item_2, parent, false);
        ExampleAdapter2.ExampleViewHolder evh = new ExampleAdapter2.ExampleViewHolder(v, mListener);
        return evh;
    }

    @Override
    public void onBindViewHolder(@NonNull ExampleAdapter2.ExampleViewHolder holder, int position) {

        ExampleItem2 currentItem = mExampleList.get(position);
        holder.mTextView.setText(currentItem.getText());
    }

    @Override
    public int getItemCount() {
        return mExampleList.size();
    }
}
