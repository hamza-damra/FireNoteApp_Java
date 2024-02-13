package net.hamza.firenote;

import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class NoteViewHolder extends RecyclerView.ViewHolder{
    public TextView title;
    public TextView content;
    public View cardView;
    public NoteViewHolder(@NonNull View itemView) {
        super(itemView);
        cardView = itemView.findViewById(R.id.noteCard);
        title = itemView.findViewById(R.id.titles);
        content = itemView.findViewById(R.id.content);

    }
}
