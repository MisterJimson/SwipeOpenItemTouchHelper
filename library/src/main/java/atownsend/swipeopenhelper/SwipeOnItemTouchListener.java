package atownsend.swipeopenhelper;

import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;

public class SwipeOnItemTouchListener implements RecyclerView.OnItemTouchListener {

    public SwipeOpenItemTouchHelper main;

    @Override
    public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent event) {
        if (main.DEBUG) {
            Log.d(main.TAG, "intercept: x:" + event.getX() + ",y:" + event.getY() + ", " + event);
        }
        final int action = MotionEventCompat.getActionMasked(event);
        if (action == MotionEvent.ACTION_DOWN) {
            main.activePointerId = MotionEventCompat.getPointerId(event, 0);
            main.initialTouchX = event.getX();
            main.initialTouchY = event.getY();
            main.obtainVelocityTracker();
            if (main.selected == null) {
                final RecoverAnimation animation = main.findAnimation(event);
                if (animation != null) {
                    main.initialTouchX -= animation.mX;
                    main.initialTouchY -= animation.mY;
                    main.endRecoverAnimation(animation.mViewHolder, true);
                    main.select(animation.mViewHolder, animation.mActionState);
                    main.updateDxDy(event, main.selectedFlags, 0);
                }
            }
        } else if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            main.activePointerId = main.ACTIVE_POINTER_ID_NONE;
            main.select(null, main.ACTION_STATE_IDLE);
        } else if (main.activePointerId != main.ACTIVE_POINTER_ID_NONE) {
            // in a non scroll orientation, if distance change is above threshold, we
            // can select the item
            final int index = MotionEventCompat.findPointerIndex(event, main.activePointerId);
            if (main.DEBUG) {
                Log.d(main.TAG, "pointer index " + index);
            }
            if (index >= 0) {
                main.checkSelectForSwipe(action, event, index);
            }
        }
        if (main.velocityTracker != null) {
            main.velocityTracker.addMovement(event);
        }
        return main.selected != null;
    }

    @Override public void onTouchEvent(RecyclerView recyclerView, MotionEvent event) {
        if (main.DEBUG) {
            Log.d(main.TAG, "on touch: x:" + main.initialTouchX + ",y:" + main.initialTouchY + ", :" + event);
        }
        if (main.velocityTracker != null) {
            main.velocityTracker.addMovement(event);
        }
        if (main.activePointerId == main.ACTIVE_POINTER_ID_NONE) {
            return;
        }
        final int action = MotionEventCompat.getActionMasked(event);
        final int activePointerIndex = MotionEventCompat.findPointerIndex(event, main.activePointerId);
        if (activePointerIndex >= 0) {
            main.checkSelectForSwipe(action, event, activePointerIndex);
        }
        if (main.selected == null) {
            return;
        }
        switch (action) {
            case MotionEvent.ACTION_MOVE: {
                // Find the index of the active pointer and fetch its position
                if (activePointerIndex >= 0) {
                    main.updateDxDy(event, main.selectedFlags, activePointerIndex);
                    main.recyclerView.invalidate();
                }
                break;
            }
            case MotionEvent.ACTION_CANCEL:
                if (main.velocityTracker != null) {
                    main.velocityTracker.clear();
                }
                // fall through
            case MotionEvent.ACTION_UP:
                main.select(null, main.ACTION_STATE_IDLE);
                main.activePointerId = main.ACTIVE_POINTER_ID_NONE;
                break;
            case MotionEvent.ACTION_POINTER_UP: {
                final int pointerIndex = MotionEventCompat.getActionIndex(event);
                final int pointerId = MotionEventCompat.getPointerId(event, pointerIndex);
                if (pointerId == main.activePointerId) {
                    // This was our active pointer going up. Choose a new
                    // active pointer and adjust accordingly.
                    final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                    main.activePointerId = MotionEventCompat.getPointerId(event, newPointerIndex);
                    main.updateDxDy(event, main.selectedFlags, pointerIndex);
                }
                break;
            }
        }
    }

    @Override public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        if (!disallowIntercept) {
            return;
        }
        main.select(null, main.ACTION_STATE_IDLE);
    }
}
