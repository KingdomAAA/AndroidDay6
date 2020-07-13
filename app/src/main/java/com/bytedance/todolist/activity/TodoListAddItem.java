package com.bytedance.todolist.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.bytedance.todolist.R;

public class TodoListAddItem extends AppCompatActivity {
    private Button mButton;
    private EditText mEditText;
    private String mText;
    public static final String KEY = "user_input";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_list_create_item);
        mButton = findViewById(R.id.btn);
        mEditText = findViewById(R.id.edit);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mText = mEditText.getText().toString();
                Intent mIntent = new Intent();
                mIntent.putExtra(KEY,mText);
                if (mText.isEmpty()){
                    setResult(RESULT_CANCELED,mIntent);
                } else {
                    setResult(RESULT_OK,mIntent);
                }
                finish();
            }
        });
    }



}