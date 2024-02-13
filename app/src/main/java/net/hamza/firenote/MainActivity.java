package net.hamza.firenote;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import net.hamza.firenote.model.Note;
import net.hamza.firenote.model.WrapContentStaggeredGridLayoutManager;

import java.util.Random;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private RecyclerView noteList;
    private FloatingActionButton fab;
    private FirebaseFirestore firestore;

    private FirestoreRecyclerAdapter<Note, NoteViewHolder> noteAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        int defaultNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (defaultNightMode == Configuration.UI_MODE_NIGHT_YES) {
            setTheme(R.style.Theme_FireNote);
        } else {
            setTheme(R.style.Base_Theme_FireNote);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeViews();
        initializeFirestore();
        setupRecyclerView();
        setupFabButton();
    }

    private void initializeViews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        noteList = findViewById(R.id.note_list);
        fab = findViewById(R.id.floatingActionButtonHomePage);
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.open, R.string.close
        );
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void initializeFirestore() {
        firestore = FirebaseFirestore.getInstance();
        setupFirestoreQuery();
    }

    private void setupFirestoreQuery() {
        Query query = firestore.collection("notes").orderBy("title", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Note> options = new FirestoreRecyclerOptions.Builder<Note>()
                .setQuery(query, Note.class)
                .build();
        noteAdapter = new FirestoreRecyclerAdapter<Note, NoteViewHolder>(options) {
            @NonNull
            @Override
            public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_view_layout, parent, false);
                return new NoteViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull NoteViewHolder holder, int position, @NonNull Note model) {
                holder.title.setText(model.getTitle());
                holder.content.setText(model.getContent());
                int color = getRandomColor(holder.itemView.getContext());
                holder.itemView.findViewById(R.id.cardView).setBackgroundColor(color);
                holder.itemView.findViewById(R.id.menuIcon).setOnClickListener(v -> {
                    showPopupMenu(v, position, model);



                });

                holder.itemView.setOnClickListener(v -> {
                    Intent intent = new Intent(v.getContext(), ContentNoteDetails.class);
                    intent.putExtra("docId", getSnapshots().getSnapshot(position).getId());
                    intent.putExtra("title", model.getTitle());
                    intent.putExtra("content", model.getContent());
                    intent.putExtra("color", color);
                    v.getContext().startActivity(intent);
                });
            }

            private void showPopupMenu(View view, int position, Note model) {
                PopupMenu popup = new PopupMenu(MainActivity.this, view);
                popup.inflate(R.menu.context_menu);
                popup.setOnMenuItemClickListener(item -> {
                        if (item.getItemId() == R.id.edit) {
                            String docId = getSnapshots().getSnapshot(position).getId();
                            navigateToEditNotePage(docId, model.getTitle(), model.getContent());
                            return true;
                        }
                        else if
                        (item.getItemId() == R.id.delete) {

                            deleteNoteFromFirebase(getSnapshots().getSnapshot(position).getId());
                            return true;
                        } else
                            return false;
                   });
                popup.show();
            }

            private void navigateToEditNotePage(String docId, String title, String content) {
                Intent intent = new Intent(MainActivity.this, EditNote.class);
                intent.putExtra("docId", docId);
                intent.putExtra("title", title);
                intent.putExtra("content", content);
                startActivity(intent);
            }


            private void deleteNoteFromFirebase(String docId) {
                firestore.collection("notes").document(docId)
                        .delete()
                        .addOnSuccessListener(aVoid -> Toast.makeText(MainActivity.this, "Note deleted successfully", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> Toast.makeText(MainActivity.this, "Error deleting note", Toast.LENGTH_LONG).show());
            }
        };
    }

    private void setupRecyclerView() {
        noteList.setAdapter(noteAdapter);
        noteList.setHasFixedSize(true);
        noteList.setLayoutManager(new WrapContentStaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
    }

    private void setupFabButton() {
        fab.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, AddNote.class)));
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.nav_add_note) {
            startActivity(new Intent(this, AddNote.class));
        }else if (item.getItemId() == R.id.nav_login) {
            startActivity(new Intent(this, LoginPage.class));
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.settings_menu) {
            // Call clearAllNotes directly
            clearAllNotes();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void clearAllNotes() {
        // Get a reference to the Firestore collection
        CollectionReference notesCollection = firestore.collection("notes");

        // Get all documents from the collection
        notesCollection.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult().isEmpty()) {
                    // If there are no notes, inform the user
                    Toast.makeText(MainActivity.this, "No notes to delete", Toast.LENGTH_SHORT).show();
                } else {
                    // If there are notes, show the alert dialog
                    new AlertDialog.Builder(this)
                            .setTitle("Delete All Notes")
                            .setMessage("Are you sure you want to delete all notes?")
                            .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                                // User clicked the Yes button, delete the notes
                                deleteAllNotes(task.getResult());
                            })
                            .setNegativeButton(android.R.string.no, null)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
            } else {
                Toast.makeText(MainActivity.this, "Error getting notes", Toast.LENGTH_LONG).show();
            }
        });
    }


    private void deleteAllNotes(QuerySnapshot querySnapshot) {
        WriteBatch batch = firestore.batch();
        for (QueryDocumentSnapshot documentSnapshot : querySnapshot) {
            // Add each document to the batch to be deleted
            batch.delete(documentSnapshot.getReference());
        }
        // Commit the batch
        batch.commit().addOnCompleteListener(batchTask -> {
            if (batchTask.isSuccessful()) {
                Toast.makeText(MainActivity.this, "All notes deleted successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "Error deleting notes", Toast.LENGTH_LONG).show();
            }
        });
    }


    private int getRandomColor(Context context) {
        int[] colorRes = {
                R.color.customBlue1, R.color.customGreen1, R.color.customRed1,
                R.color.customYellow1, R.color.customOrange1, R.color.customPurple1,
                R.color.customLime1, R.color.customPink1, R.color.customTeal1,
                R.color.customDeepPurple1, R.color.customIndigo1, R.color.customCyan1,
                R.color.customDeepOrange1, R.color.customBrown1, R.color.customGrey1,
                R.color.customBlueGrey1, R.color.customLightGreen1, R.color.customLightBlue1,
                R.color.customAmber1, R.color.customGrey2, R.color.customIndigo2,
                R.color.customDeepPurple2, R.color.customCyan2, R.color.customTeal2,
                R.color.customGreen2, R.color.customLightGreen2, R.color.customLime2,
                R.color.customYellow2, R.color.customAmber2, R.color.customOrange2,
                R.color.customDeepOrange2, R.color.customRed2, R.color.customPink2,
                R.color.customPurple2, R.color.customDeepPurple3, R.color.customIndigo3,
                R.color.customBlue2, R.color.customLightBlue2, R.color.customCyan3,
                R.color.customTeal3, R.color.customGreen3, R.color.customLightGreen3,
                R.color.customLime3, R.color.customYellow3, R.color.customAmber3,
                R.color.customOrange3, R.color.customDeepOrange3, R.color.customBrown2,
                R.color.customGrey3, R.color.customBlueGrey2, R.color.yellow, R.color.lightGreen,
                R.color.pink, R.color.lightPurple, R.color.skyblue, R.color.gray, R.color.red,
                R.color.blue, R.color.greenlight, R.color.notgreen
        };
        return ContextCompat.getColor(context, colorRes[new Random().nextInt(colorRes.length)]);
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (noteAdapter != null) {
            noteAdapter.startListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (noteAdapter != null) {
            noteAdapter.stopListening();
        }
    }
}