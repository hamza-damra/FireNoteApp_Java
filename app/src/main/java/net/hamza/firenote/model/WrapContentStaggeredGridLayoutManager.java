package net.hamza.firenote.model;
import android.util.Log;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

public class WrapContentStaggeredGridLayoutManager extends StaggeredGridLayoutManager {

    public WrapContentStaggeredGridLayoutManager(int spanCount, int orientation) {
        super(spanCount, orientation);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        try {
            super.onLayoutChildren(recycler, state);
        } catch (IndexOutOfBoundsException e) {
            Log.e("WrapContentStaggeredGLM", "IndexOutOfBoundsException in RecyclerView happens");
        }
    }
}
