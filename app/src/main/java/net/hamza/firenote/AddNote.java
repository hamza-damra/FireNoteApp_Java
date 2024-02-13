package net.hamza.firenote;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import net.hamza.firenote.databinding.ActivityAddNoteBinding;

import java.util.HashMap;
import java.util.Map;

import io.github.muddz.styleabletoast.StyleableToast;

public class AddNote extends AppCompatActivity {
    private FirebaseFirestore firestore;
    private EditText title, content;
    private ProgressBar progressBar;
    private ActivityAddNoteBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAddNoteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initializeViews();
        setupFabButton();
    }

    private void initializeViews() {
        setSupportActionBar(binding.toolbar);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        // Directly access views from binding to avoid findViewById
        progressBar = findViewById(R.id.progressBar);
        firestore = FirebaseFirestore.getInstance();
        title = findViewById(R.id.et_note_title_add);
        content = findViewById(R.id.et_note_content_add);

        // Setup back button
        ImageButton back = findViewById(R.id.ib_note_details_back);
        back.setOnClickListener(view -> onBackPressed());
    }

    private void setupFabButton() {
        FloatingActionButton fab = binding.fab;
        fab.setOnClickListener(view -> storeDataLogic(view));
    }

    private void storeDataLogic(View view) {
        String titleText = title.getText().toString().trim();
        String contentText = content.getText().toString().trim();

        // Ensure both title and content are provided
        if (titleText.isEmpty() || contentText.isEmpty()) {
            Snackbar.make(view, "Both title and content are required.", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            return; // Exit the method early
        }

        progressBar.setVisibility(View.VISIBLE);

        // Create a new note document in the "notes" collection
        DocumentReference documentReference = firestore.collection("notes").document();
        Map<String, Object> noteMap = new HashMap<>();
        noteMap.put("title", titleText);
        noteMap.put("content", contentText);

        ((DocumentReference) documentReference).set(noteMap).addOnSuccessListener(aVoid -> {
            new StyleableToast.Builder(getBaseContext())
                    .text("Note saved successfully!")
                    .textColor(Color.WHITE)
                    .backgroundColor(Color.GREEN)
                    .show();
            progressBar.setVisibility(View.GONE);
            finish(); // Close the activity and return to the previous screen
        }).addOnFailureListener(e -> {
            Snackbar.make(view, "Failed to save note. Try again.", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            progressBar.setVisibility(View.GONE);
        });
    }
}
