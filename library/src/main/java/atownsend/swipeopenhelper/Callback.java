package atownsend.swipeopenhelper;

import android.graphics.Canvas;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import java.util.List;

/**
 * Base Callback class that extends off of {@link ItemTouchHelper.Callback}
 */
@SuppressWarnings("UnusedParameters") public abstract class Callback extends ItemTouchHelper.Callback {

    @Override public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        // do not use
        return false;
    }

    @Override public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        // do not use
    }

    /**
     * Convenience method to create movement flags.
     * <p>
     * For instance, if you want to let your items be drag & dropped vertically and swiped
     * left to be dismissed, you can call this method with:
     * <code>makeMovementFlags(UP | DOWN, LEFT);</code>
     *
     * @param swipeFlags The directions in which the item can be swiped.
     * @return Returns an integer composed of the given drag and swipe flags.
     */
    public static int makeMovementFlags(int swipeFlags) {
        return makeFlag(SwipeOpenItemTouchHelper.ACTION_STATE_IDLE, swipeFlags) | makeFlag(SwipeOpenItemTouchHelper.ACTION_STATE_SWIPE, swipeFlags);
    }

    final int getAbsoluteMovementFlags(RecyclerView recyclerView,
                                       RecyclerView.ViewHolder viewHolder) {
        final int flags = getMovementFlags(recyclerView, viewHolder);
        return convertToAbsoluteDirection(flags, ViewCompat.getLayoutDirection(recyclerView));
    }

    /**
     * Called when the ViewHolder is changed.
     * <p/>
     * If you override this method, you should call super.
     *
     * @param viewHolder The new ViewHolder that is being swiped. Might be null if
     * it is cleared.
     * @param actionState One of {@link SwipeOpenItemTouchHelper#ACTION_STATE_IDLE},
     * {@link SwipeOpenItemTouchHelper#ACTION_STATE_SWIPE}
     * @see #clearView(RecyclerView, SwipeOpenViewHolder)
     */
    public void onSelectedChanged(SwipeOpenViewHolder viewHolder, int actionState) {
        if (viewHolder != null) {
            getDefaultUIUtil().onSelected(viewHolder.getSwipeView());
        }
    }

    void onDraw(Canvas c, RecyclerView parent, SwipeOpenViewHolder selected,
                        List<RecoverAnimation> recoverAnimationList, int actionState, float dX, float dY,
                        boolean isRtl) {
        final int recoverAnimSize = recoverAnimationList.size();
        for (int i = 0; i < recoverAnimSize; i++) {
            final RecoverAnimation anim = recoverAnimationList.get(i);
            anim.update();
            final int count = c.save();
            onChildDraw(c, parent, anim.mViewHolder, anim.mX, anim.mY, false);
            c.restoreToCount(count);
        }
        if (selected != null) {
            final int count = c.save();
            notifySwipeDirections(selected, isRtl, dX, dY);
            onChildDraw(c, parent, selected, dX, dY, true);
            c.restoreToCount(count);
        }
    }

    /**
     * Notifies the SwipeOpenHolder when one of its hidden views has become visible.
     *
     * @param holder the holder
     * @param isRtl if the layout is RTL or not
     * @param dX the new dX of the swiped view
     * @param dY the new dY of the swiped view
     */
    private void notifySwipeDirections(SwipeOpenViewHolder holder, boolean isRtl, float dX,
                                       float dY) {
        // check if we are about to start a swipe to open start or open end positions
        View swipeView = holder.getSwipeView();
        // 0 or negative translationX, heading to positive translationX
        if (ViewCompat.getTranslationX(swipeView) <= 0 && dX > 0) {
            if (isRtl) {
                holder.notifyEndOpen();
            } else {
                holder.notifyStartOpen();
            }
            // 0 or positive translationX, heading to negative translationX
        } else if (ViewCompat.getTranslationX(swipeView) >= 0 && dX < 0) {
            if (isRtl) {
                holder.notifyStartOpen();
            } else {
                holder.notifyEndOpen();
            }
            // 0 or positive translationY, heading to negative translationY
        } else if (ViewCompat.getTranslationY(swipeView) >= 0 && dY < 0) {
            holder.notifyEndOpen();
        } else if (ViewCompat.getTranslationY(swipeView) <= 0 && dY > 0) {
            holder.notifyStartOpen();
        }
    }

    void onDrawOver(Canvas c, RecyclerView parent, SwipeOpenViewHolder selected,
                            List<RecoverAnimation> recoverAnimationList, int actionState, float dX, float dY) {
        final int recoverAnimSize = recoverAnimationList.size();
        boolean hasRunningAnimation = false;
        for (int i = recoverAnimSize - 1; i >= 0; i--) {
            final RecoverAnimation anim = recoverAnimationList.get(i);
            if (anim.mEnded && !anim.mIsPendingCleanup) {
                recoverAnimationList.remove(i);
            } else if (!anim.mEnded) {
                hasRunningAnimation = true;
            }
        }
        if (hasRunningAnimation) {
            parent.invalidate();
        }
    }

    public void clearView(RecyclerView recyclerView, SwipeOpenViewHolder viewHolder) {
        getDefaultUIUtil().clearView(viewHolder.getSwipeView());
    }

    public void onChildDraw(Canvas c, RecyclerView recyclerView, SwipeOpenViewHolder viewHolder,
                            float dX, float dY, boolean isCurrentlyActive) {
        // handle the draw
        getDefaultUIUtil().onDraw(c, recyclerView, viewHolder.getSwipeView(), dX, dY,
                SwipeOpenItemTouchHelper.ACTION_STATE_SWIPE, isCurrentlyActive);
    }

    public long getAnimationDuration(RecyclerView recyclerView, int animationType, float animateDx,
                                     float animateDy) {
        final RecyclerView.ItemAnimator itemAnimator = recyclerView.getItemAnimator();
        if (itemAnimator == null) {
            return DEFAULT_SWIPE_ANIMATION_DURATION;
        } else {
            return itemAnimator.getMoveDuration();
        }
    }
}