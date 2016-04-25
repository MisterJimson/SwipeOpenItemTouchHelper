package atownsend.swipeopenhelper;

import android.support.v7.widget.RecyclerView;

/**
 * Simple callback class that defines the swipe directions allowed and delegates everything else
 * to the base class
 */
@SuppressWarnings("UnusedParameters") public class SimpleCallback extends Callback {

    private int mDefaultSwipeDirs;

    public SimpleCallback(int swipeDirs) {
        mDefaultSwipeDirs = swipeDirs;
    }

    public void setDefaultSwipeDirs(int defaultSwipeDirs) {
        mDefaultSwipeDirs = defaultSwipeDirs;
    }

    public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        return mDefaultSwipeDirs;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        return makeMovementFlags(getSwipeDirs(recyclerView, viewHolder));
    }
}