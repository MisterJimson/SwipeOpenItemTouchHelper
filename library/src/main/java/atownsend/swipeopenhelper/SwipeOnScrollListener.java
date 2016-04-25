package atownsend.swipeopenhelper;

import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;


public class SwipeOnScrollListener extends RecyclerView.OnScrollListener {

    public SwipeOpenItemTouchHelper main;

    @Override public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        if (main.closeOnAction && (dx != 0 || dy != 0)) {
            if (main.prevSelected != null && (Math.abs(
                    ViewCompat.getTranslationX(main.prevSelected.getSwipeView())) > 0
                    || Math.abs(ViewCompat.getTranslationY(main.prevSelected.getSwipeView())) > 0)) {
                main.closeOpenHolder(main.prevSelected);
                main.prevSelected = null;
            }
            // if we've got any open positions saved from a rotation, close those
            if (main.openedPositions.size() > 0) {
                for (int i = 0; i < main.openedPositions.size(); i++) {
                    View child = recyclerView.getChildAt(main.openedPositions.keyAt(0));
                    // view needs to be attached, otherwise we can just mark it has removed since it's not visible
                    if (child != null && child.getParent() != null) {
                        RecyclerView.ViewHolder holder = recyclerView.getChildViewHolder(child);
                        if (holder instanceof SwipeOpenViewHolder) {
                            main.closeOpenHolder((SwipeOpenViewHolder) holder);
                        }
                    }
                    main.openedPositions.removeAt(i);
                }
            }
        }
    }
}
