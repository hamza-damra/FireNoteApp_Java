package net.hamza.firenote;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;

public class EditNote extends AppCompatActivity {

    private FirebaseFirestore firestore;
    private String docId;
    private EditText titleEditText, contentEditText;
    private FloatingActionButton saveFab;
    private ImageButton backButton;
    private int color = Color.WHITE; // Default color if none is provided

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);
        initializeFirestore();
        initializeViews();
        receiveData();
    }

    private void initializeFirestore() {
        firestore = FirebaseFirestore.getInstance();
    }

    private void initializeViews() {
        titleEditText = findViewById(R.id.et_note_title_edit);
        contentEditText = findViewById(R.id.et_note_content_edit);
        saveFab = findViewById(R.id.fab_edit_note_save);
        backButton = findViewById(R.id.ib_note_details_back);

        saveFab.setOnClickListener(v -> updateNote());
        backButton.setOnClickListener(v -> finish());
    }

    private void receiveData() {
        Intent intent = getIntent();
        if (intent != null) {
            docId = intent.getStringExtra("docId");
            String title = intent.getStringExtra("title");
            String content = intent.getStringExtra("content");
            color = intent.getIntExtra("color", Color.WHITE);

            if (docId != null) { // Ensure docId is not null
                titleEditText.setText(title != null ? title : "");
                contentEditText.setText(content != null ? content : "");
                contentEditText.setBackgroundColor(color);

                // Adjust EditText for RTL if needed
                if (isRtlContext()) {
                    adjustForRtlContext(titleEditText);
                    adjustForRtlContext(contentEditText);
                }
            } else {
                // Handle the case where docId is null
                Toast.makeText(this, "Document ID is missing.", Toast.LENGTH_LONG).show();
            }
        }
    }


    private void adjustForRtlContext(EditText editText) {
        editText.setGravity(Gravity.END);
        editText.setTextDirection(View.TEXT_DIRECTION_RTL);
        editText.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
    }

    private boolean isRtlContext() {
        return getResources().getConfiguration().getLayoutDirection() == View.LAYOUT_DIRECTION_RTL;
    }


    private void updateNote() {
        String updatedTitle = titleEditText.getText().toString().trim();
        String updatedContent = contentEditText.getText().toString().trim();

        // Check if docId is null or empty
        if (docId != null && !docId.isEmpty()) {
            firestore.collection("notes").document(docId)
                    .update("title", updatedTitle, "content", updatedContent)
                    .addOnSuccessListener(aVoid -> Toast.makeText(EditNote.this, "Note updated successfully", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(EditNote.this, "Error updating note", Toast.LENGTH_SHORT).show())
                    .addOnCompleteListener(task -> finish()); // Ensure Activity finishes irrespective of success or failure
        } else {
            Toast.makeText(this, "Error: Document ID is missing.", Toast.LENGTH_SHORT).show();
        }
    }

}
