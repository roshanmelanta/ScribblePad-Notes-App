package com.example.notesapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.ByteArrayOutputStream;

public class NoteDetails extends AppCompatActivity {
    private TextView noteDetailTitle, noteDetailContent;
    private ImageView imageNote;
    FloatingActionButton editNoteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_details);

        noteDetailTitle = findViewById(R.id.noteDetailTitle);
        noteDetailContent = findViewById(R.id.noteDetailContent);
        imageNote = findViewById(R.id.imageNote);
        editNoteButton = findViewById(R.id.editNoteButton);
        Toolbar toolbar = findViewById(R.id.toolBarNoteDetail);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent data = getIntent();

        editNoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), EditNoteActivity.class);
                intent.putExtra("title", data.getStringExtra("title"));
                intent.putExtra("content", data.getStringExtra("content"));
                intent.putExtra("noteId", data.getStringExtra("noteId"));
                v.getContext().startActivity(intent);
            }
        });

        noteDetailTitle.setText(data.getStringExtra("title"));
        noteDetailContent.setText(data.getStringExtra("content"));

        String image = data.getStringExtra("image");
        System.out.println(image);
        System.out.println(data.getStringExtra("title"));

        if(image != null) {
            byte[] decodedString = Base64.decode(image, Base64.DEFAULT);
            Bitmap bitmap= BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            imageNote.setImageBitmap(bitmap);
        }

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}