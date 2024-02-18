package com.example.notesapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;



import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class NewNote extends AppCompatActivity {

    EditText noteTitle, noteContent;
    FloatingActionButton saveNote, addImage;
    ImageView imageNote;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseFirestore firebaseFirestore;
    ProgressBar progressBarNewNote;
    String base64Image;

    private static final int pic_id = 123;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_note);

        saveNote = findViewById(R.id.saveNote);
        noteTitle = findViewById(R.id.noteTitle);
        noteContent = findViewById(R.id.noteContent);
        progressBarNewNote = findViewById(R.id.progressBarNewNote);

        Toolbar toolbar = findViewById(R.id.toolBarCreateNote);
        setSupportActionBar(toolbar); // setting Toolbar to act as ActionBar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Enable home/back button in the action bar

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();

        addImage = findViewById(R.id.addImage);
        imageNote = findViewById(R.id.imageNote);


        saveNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = noteTitle.getText().toString();
                String content = noteContent.getText().toString();
                if(title.isEmpty() || content.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Both fields are required", Toast.LENGTH_SHORT).show();
                }
                else {
                    progressBarNewNote.setVisibility(View.VISIBLE);
                    /*
                    * 1) Create collection named "notes"
                    * 2) Identify the user by getting current user UID
                    * 3) Create specific collection name "my notes" for the user
                    * 4) Create a new HashMap for user and populate values in it*/
                    DocumentReference documentReference = firebaseFirestore.collection("notes").
                    document(firebaseUser.getUid()).collection("myNotes").document();
                    Map<String, Object> note = new HashMap<>();
                    note.put("title", title);
                    note.put("content", content);
                    note.put("image", base64Image);

                    // Overwrites the document referred to by this DocumentReference.
                    documentReference.set(note).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(getApplicationContext(), "Note created successfully", Toast.LENGTH_SHORT).show();
                            progressBarNewNote.setVisibility(View.INVISIBLE);
                            startActivity(new Intent(NewNote.this, NotesActivity.class));
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "Failed to create note", Toast.LENGTH_SHORT).show();
                            progressBarNewNote.setVisibility(View.INVISIBLE);
                        }
                    });
                }
            }
        });

        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent camera_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(camera_intent, pic_id);
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Match the request 'pic id with requestCode
        if (requestCode == pic_id) {
            // BitMap is data structure of image file which store the image in memory
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            // Set the image in imageview for display
            imageNote.setImageBitmap(photo);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            byte[] imageBytes = outputStream.toByteArray();
            base64Image = Base64.encodeToString(imageBytes, Base64.DEFAULT);
            System.out.println(base64Image);
        }
    }

    // Watch video 9
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}