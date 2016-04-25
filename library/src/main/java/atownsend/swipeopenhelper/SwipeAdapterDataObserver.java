package atownsend.swipeopenhelper;

import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;

/**
 * Data Observer that allow us to remove any opened positions when something is removed from the
 * adapter
 */
public class SwipeAdapterDataObserver extends RecyclerView.AdapterDataObserver {
    public SwipeOpenItemTouchHelper main;

    @Override public void onItemRangeRemoved(int positionStart, int itemCount) {
        // if an item is removed, we need to remove the opened position
        for (int i = positionStart; i < positionStart + itemCount; i++) {
            main.openedPositions.remove(i);
        }
    }
}
