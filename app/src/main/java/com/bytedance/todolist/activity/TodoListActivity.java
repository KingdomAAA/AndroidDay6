package com.bytedance.todolist.activity;

import android.content.Intent;
import android.os.Bundle;

import com.bytedance.todolist.database.TodoListDao;
import com.bytedance.todolist.database.TodoListDatabase;
import com.bytedance.todolist.database.TodoListEntity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.CheckBox;

import com.bytedance.todolist.R;
import com.google.android.material.snackbar.Snackbar;

import java.util.Date;
import java.util.List;

public class TodoListActivity extends AppCompatActivity {
    private static final String TAG = "TodoListActivity";
    private TodoListAdapter mAdapter;
    private FloatingActionButton mFab;
    private static final int REQUEST_CODE_ADD_ITEM = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.todo_list_activity_layout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        RecyclerView recyclerView = findViewById(R.id.rv_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new TodoListAdapter();
        recyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(TodoListClickListener);


        mFab = findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TodoListActivity.this, TodoListAddItem.class);
                startActivityForResult(intent,REQUEST_CODE_ADD_ITEM);
            }
        });

        mFab.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                new Thread() {
                    @Override
                    public void run() {
                        TodoListDao dao = TodoListDatabase.inst(TodoListActivity.this).todoListDao();
                        dao.deleteAll();

                        for (int i = 0; i < 20; ++i) {
                            dao.addTodo(new TodoListEntity("This is " + i + " item", new Date(System.currentTimeMillis()), 0L));
                        }
                        Snackbar.make(mFab, R.string.hint_complete, Snackbar.LENGTH_SHORT).show();
                        loadFromDatabase();
                    }
                }.start();
                return true;
            }
        });
        loadFromDatabase();
    }

    private void loadFromDatabase() {
        new Thread() {
            @Override
            public void run() {
                TodoListDao dao = TodoListDatabase.inst(TodoListActivity.this).todoListDao();
                final List<TodoListEntity> entityList = dao.loadAll();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.setData(entityList);
                    }
                });
            }
        }.start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ADD_ITEM) {
            if (RESULT_OK == resultCode && null != data) {
                new Thread(){
                    @Override
                    public void run() {
                        String item = data.getStringExtra(TodoListAddItem.KEY);
                        TodoListDao dao = TodoListDatabase.inst(TodoListActivity.this).todoListDao();
                        dao.addTodo(new TodoListEntity(item,new Date(System.currentTimeMillis()),0L));
                        Snackbar.make(mFab, R.string.hint_complete, Snackbar.LENGTH_SHORT).show();
                        loadFromDatabase();
                    }
                }.start();
            }
        }
    }

    private TodoListAdapter.OnItemClickListener TodoListClickListener
            = new TodoListAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(final View v, TodoListAdapter.ViewName viewName, final int position) {
            if (null == v) return;
            new Thread(){
                @Override
                public void run() {
                    super.run();
                    TodoListDao dao = TodoListDatabase.inst(TodoListActivity.this).todoListDao();
                    switch (v.getId()){
                        case R.id.cb:
                            if (((CheckBox)v).isChecked())
                                dao.update(1L,mAdapter.mDatas.get(position).getId());
                            else
                                dao.update(0,mAdapter.mDatas.get(position).getId());
                            break;
                        case R.id.btnx:
                            dao.deleteTodo(mAdapter.mDatas.get(position));
                    }
                    loadFromDatabase();
                }
            }.start();
        }
    };
}
