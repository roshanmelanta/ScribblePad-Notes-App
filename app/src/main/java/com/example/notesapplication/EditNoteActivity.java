package com.example.notesapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EditNoteActivity extends AppCompatActivity {
    Intent data;
    private EditText noteEditTitle, noteEditContent;
    private ImageView editImageNote;
    FloatingActionButton updateNoteButton;

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);

        noteEditTitle = findViewById(R.id.noteEditTitle);
        noteEditContent = findViewById(R.id.noteEditContent);
        updateNoteButton = findViewById(R.id.updateNoteButton);
        editImageNote = findViewById(R.id.editImageNote);
        data = getIntent();

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        Toolbar toolbar = findViewById(R.id.toolBarEditNote);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        
        updateNoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newTitle = noteEditTitle.getText().toString();
                String newContent = noteEditContent.getText().toString();
                
                if(newTitle.isEmpty() || newContent.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Some fields are empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                else {
                    DocumentReference documentReference = firebaseFirestore.collection("notes").
                    document(firebaseUser.getUid()).collection("myNotes").document(data.getStringExtra("noteId"));
                    Map<String, Object> updatedNote = new HashMap<>();
                    updatedNote.put("title", newTitle);
                    updatedNote.put("content", newContent);
                    documentReference.set(updatedNote).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(getApplicationContext(), "Note has been updated", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(EditNoteActivity.this, NotesActivity.class));
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "Failed to update", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        String noteTitle = data.getStringExtra("title");
        String noteContent = data.getStringExtra("content");
        String image = data.getStringExtra("image");
        noteEditTitle.setText(noteTitle);
        noteEditContent.setText(noteContent);

        if(image != null) {
            byte[] decodedString = Base64.decode(image, Base64.DEFAULT);
            Bitmap bitmap= BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            editImageNote.setImageBitmap(bitmap);
        }
    }
}