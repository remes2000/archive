package pl.nieruchalski.scrumfamily.HelpingClasses;

import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;

/**
 * Created by michal on 08.03.17.
 */

public class RecyclerViewOnItemTouchListener implements RecyclerView.OnItemTouchListener {

    public GestureDetectorCompat detector;

    public RecyclerViewOnItemTouchListener(GestureDetectorCompat detector) {
        this.detector = detector;
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        detector.onTouchEvent(e);
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        ;
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {;
    }
}
