package com.example.uley;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SubTaskAdapter extends RecyclerView.Adapter<SubTaskAdapter.ViewHolder> {
    private Context mContext;
    private ArrayList<Tasks> tasksList;

    public SubTaskAdapter(Context mContext, ArrayList<Tasks> tasksList) {
        this.mContext = mContext;
        this.tasksList = tasksList;
    }

    @NonNull
    @Override
    public SubTaskAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.textView.setText(tasksList.get(position).getName());

        holder.itemView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                menu.add(holder.getAdapterPosition(),0,0,"Изменить цвет заметки");
                menu.add(holder.getAdapterPosition(),1,0,"Удалить");
            }
        });

        if (tasksList.get(position).getTypeSubTask().equals("m")){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(holder.itemView.getContext(),Map.class);
                    holder.itemView.getContext().startActivity(intent);
                }
            });
        }

        if (tasksList.get(position).getColor().equals("2"))
            holder.colorImportant.setBackgroundColor(Color.parseColor("#525AF3"));
        else if (tasksList.get(position).getColor().equals("1"))
            holder.colorImportant.setBackgroundColor(Color.parseColor("#fece2f"));
        else if (tasksList.get(position).getColor().equals("3"))
            holder.colorImportant.setBackgroundColor(Color.parseColor("#F33846"));
    }


    @Override
    public int getItemCount() {
        return tasksList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        LinearLayout colorImportant;
        TextView textView;

        public ViewHolder(@NonNull View itemView){
            super (itemView);
            colorImportant = itemView.findViewById(R.id.important_color);
            textView = itemView.findViewById(R.id.name_task);
        }
    }
}
