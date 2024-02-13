package net.hamza.firenote;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ContentNoteDetails extends AppCompatActivity {

    private String docId, title, content;
    private int color;
    private TextView titleTextView, contentTextView;

    private final ActivityResultLauncher<Intent> editNoteActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    // Retrieve and display the updated note details
                    title = result.getData().getStringExtra("title");
                    content = result.getData().getStringExtra("content");
                    // Assuming color could change, you could retrieve and update it here as well

                    titleTextView.setText(title);
                    contentTextView.setText(content);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_note_details);
        initializeViews();
        setupFabButton();
    }

    private void initializeViews() {
        Intent intent = getIntent();
        docId = intent.getStringExtra("docId");
        title = intent.getStringExtra("title");
        content = intent.getStringExtra("content");
        color = intent.getIntExtra("color", Color.WHITE);

        titleTextView = findViewById(R.id.tv_note_details_title);
        contentTextView = findViewById(R.id.tv_note_details_content);
        ImageButton backButton = findViewById(R.id.ib_note_details_back);

        titleTextView.setText(title);
        contentTextView.setText(content);
        contentTextView.setBackgroundColor(color);

        backButton.setOnClickListener(v -> finish());
    }

    private void setupFabButton() {
        FloatingActionButton fab = findViewById(R.id.fab_edit_note);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(ContentNoteDetails.this, EditNote.class);
            intent.putExtra("docId", docId);
            intent.putExtra("title", title);
            intent.putExtra("content", content);
            intent.putExtra("color", color);

            editNoteActivityResultLauncher.launch(intent);
        });
    }
}
