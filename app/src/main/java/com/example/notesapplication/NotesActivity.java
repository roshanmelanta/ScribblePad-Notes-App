package com.example.notesapplication;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;


public class NotesActivity extends AppCompatActivity {
    FloatingActionButton mcreateNewNote;
    private FirebaseAuth firebaseAuth;

    RecyclerView recyclerView;
    StaggeredGridLayoutManager staggeredGridLayoutManager;

    FirebaseUser firebaseUser;
    FirebaseFirestore firebaseFirestore;
    FirestoreRecyclerAdapter<FirebaseModel, NoteViewHolder> noteAdapter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_activity);

        mcreateNewNote = findViewById(R.id.createNewNote);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();

        getSupportActionBar().setTitle("My Notes");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#3A52Fc")));

        // On click of create new note button
        mcreateNewNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(NotesActivity.this, NewNote.class));
            }
        });

        Query query = firebaseFirestore.collection("notes").document(firebaseUser.getUid()).collection("myNotes").orderBy("title", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<FirebaseModel> allUserNotes = new FirestoreRecyclerOptions.Builder<FirebaseModel>().setQuery(query, FirebaseModel.class).build();

        // https://imgur.com/a/Po8c92x
        // The Adapter class gets data from the datasource and passes it into the ViewHolder which updates the views it is holding.
        noteAdapter = new FirestoreRecyclerAdapter<FirebaseModel, NoteViewHolder>(allUserNotes) {

            /*onBindViewHolder() is called with the ViewHolder and a “position,”
            which denotes the item’s position in the notes list that is being bound.
            This position can be used to extract the underlying data for the cell and
            pass that into the ViewHolder to bind the data to that holder’s UI.*/
            @Override
            protected void onBindViewHolder(@NonNull NoteViewHolder holder, int i, @NonNull FirebaseModel model) {
                ImageView noteOptions = holder.itemView.findViewById(R.id.menuPopButton);
                
                holder.noteTitle.setText(model.getTitle());
                holder.noteContent.setText(model.getContent());

                String docId = noteAdapter.getSnapshots().getSnapshot(i).getId(); // Fetch note ID

                // On click open note in detail
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Open note detail activity
                        Intent intent = new Intent(v.getContext(), NoteDetails.class);

                        intent.putExtra("title", model.getTitle());
                        intent.putExtra("content", model.getContent());
                        intent.putExtra("noteId", docId);
                        intent.putExtra("image", model.getImage());

                        v.getContext().startActivity(intent);
                    }
                });

                // On Click of edit and delete option
                noteOptions.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
                        popupMenu.setGravity(Gravity.END); // set popup to right

                        // Edit Note
                        popupMenu.getMenu().add("Edit").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                Intent intent = new Intent(v.getContext(), EditNoteActivity.class);

                                intent.putExtra("title", model.getTitle());
                                intent.putExtra("content", model.getContent());
                                intent.putExtra("noteId", docId);
                                intent.putExtra("noteId", model.getImage());

                                v.getContext().startActivity(intent);
                                return false;
                            }
                        });

                        // Delete Note
                        popupMenu.getMenu().add("Delete").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                DocumentReference documentReference = firebaseFirestore.collection("notes").
                                document(firebaseUser.getUid()).collection("myNotes").document(docId);
                                documentReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(getApplicationContext(), "Note was deleted", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getApplicationContext(), "Failed to delete note", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                return false;
                            }
                        });

                        popupMenu.show();
                    }
                });
            }

            /*This method is called when the ViewHolder is created.
            It initializes and inflates the view for the item in the RecyclerView.
            This view uses the item layout created earlier which displays text.*/
            @NonNull
            @Override
            public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notes_layout, parent, false);
                return new NoteViewHolder(view);
            }
        };

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        //recyclerView.setAnimation(null);
        staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        recyclerView.setAdapter(noteAdapter);


    }

    // The ViewHolders are “recycled” (repopulated with new data) as the user scrolls; existing items disappear on one end and new items appear on the other end.
    public class NoteViewHolder extends RecyclerView.ViewHolder {
        private TextView noteTitle;
        private TextView noteContent;
        LinearLayout note;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            noteTitle = itemView.findViewById(R.id.noteTitle);
            noteContent = itemView.findViewById(R.id.noteContent);
            note = itemView.findViewById(R.id.note);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu); // populate the menu with items
        return true;
    }

    // On click of logout menu item
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                firebaseAuth.signOut();
                finish();
                startActivity(new Intent(NotesActivity.this, MainActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        noteAdapter.startListening();
        recyclerView.getRecycledViewPool().clear();
        noteAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(noteAdapter != null) {
            noteAdapter.stopListening();
        }

    }
}
