package com.bytedance.todolist.activity;

import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bytedance.todolist.R;
import com.bytedance.todolist.database.TodoListEntity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author wangrui.sh
 * @since Jul 11, 2020
 */
public class TodoListAdapter extends RecyclerView.Adapter<TodoListAdapter.TodoListItemHolder> implements View.OnClickListener {
    public List<TodoListEntity> mDatas;

    public enum ViewName { CHECKBOX, BTNX }

    public TodoListAdapter() {
        mDatas = new ArrayList<>();
    }
    @NonNull
    @Override
    public TodoListItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TodoListItemHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.todo_item_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull TodoListItemHolder holder, int position) {
        holder.bind(mDatas.get(position), position);
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    @MainThread
    public void setData(List<TodoListEntity> list) {
        mDatas = list;
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onItemClick(View v, ViewName viewName, int position);
    }

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener (OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    @Override
    public void onClick(View v) {
        if (!(v instanceof CheckBox || v instanceof ImageView)) return;
        int position = (int) v.getTag();
        if (null != mOnItemClickListener) {
            switch (v.getId()){
                case R.id.cb:
                    mOnItemClickListener.onItemClick(v, ViewName.CHECKBOX, position);
                    break;
                case R.id.btnx:
                    mOnItemClickListener.onItemClick(v, ViewName.BTNX, position);
            }
        }
    }


    public class TodoListItemHolder extends RecyclerView.ViewHolder {
        private TextView mContent;
        private TextView mTimestamp;
        private CheckBox mFinished;
        private ImageView mDelete;
        public TodoListItemHolder(@NonNull View itemView) {
            super(itemView);
            mContent = itemView.findViewById(R.id.tv_content);
            mTimestamp = itemView.findViewById(R.id.tv_timestamp);
            mFinished = itemView.findViewById(R.id.cb);
            mDelete = itemView.findViewById(R.id.btnx);

            itemView.setOnClickListener(TodoListAdapter.this);
            mContent.setOnClickListener(TodoListAdapter.this);
            mTimestamp.setOnClickListener(TodoListAdapter.this);
            mFinished.setOnClickListener(TodoListAdapter.this);
            mDelete.setOnClickListener(TodoListAdapter.this);
        }

        public void bind(TodoListEntity entity, int position) {
            mContent.setText(entity.getContent());
            mTimestamp.setText(formatDate(entity.getTime()));
            if (0L!=entity.getState()) {
                mFinished.setChecked(true);
                mContent.setTextColor(Color.GRAY);
                mContent.setPaintFlags(mContent.getPaintFlags()| Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                mContent.setTextColor(Color.BLACK);
                mContent.setPaintFlags(mContent.getPaintFlags()&(~Paint.STRIKE_THRU_TEXT_FLAG));
                mFinished.setChecked(false);
            }
            mContent.setTag(position);
            mTimestamp.setTag(position);
            mFinished.setTag(position);
            mDelete.setTag(position);
        }

        private String formatDate(Date date) {
            DateFormat format = SimpleDateFormat.getDateInstance();
            return format.format(date);
        }
    }






}
