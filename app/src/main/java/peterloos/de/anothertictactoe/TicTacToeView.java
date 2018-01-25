package peterloos.de.anothertictactoe;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by loospete on 24.01.2018.
 */

public class TicTacToeView extends View implements View.OnClickListener, View.OnTouchListener {

    private Paint paint;
    private Paint paintLine;

    private Paint paintRect;

    private int   cmInPxHorizontal;
    private int   cmInPxVertical;

    private int  widthPx;
    private int  heightPx;

    private int top;
    private int left;
    private int length;
    private int distance = length / 3;
    private Rect[][] touchRectangles;

    private DisplayMetrics metrics;

    public TicTacToeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        //
        this.metrics = new DisplayMetrics();
        Activity activity = ((Activity) this.getContext());
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);

        String msg = String.format( "onDraw ?????????????   width = %d, height = %d", this.getWidth(), this.getHeight());
        Log.v("PeLo", msg);

        // TOOO: Wie ist xdpi und ydpi deklariert
//        this.cmInPxHorizontal = (int) Math.round(metrics.xdpi / 2.54);
//        this.cmInPxVertical = (int) Math.round(metrics.ydpi / 2.54);

        this.cmInPxHorizontal = (int) Math.round(metrics.xdpi / 2.54);
        this.cmInPxVertical = (int) Math.round(metrics.ydpi / 2.54);

        this.paint = new Paint();
        this.paint.setStyle(Paint.Style.FILL_AND_STROKE);
        this.paint.setStrokeWidth(10);
        this.paint.setColor(Color.BLUE);

        this.paintLine = new Paint();
        this.paintLine.setStyle(Paint.Style.FILL_AND_STROKE);
        this.paintLine.setStrokeWidth(this.cmInPxHorizontal / 4);
        this.paintLine.setColor(Color.BLUE);

        this.paintRect = new Paint();
        this.paintRect.setStyle(Paint.Style.FILL_AND_STROKE);
        /// this.paintRect.setStrokeWidth(this.cmInPxHorizontal / 4);
        this.paintRect.setColor(Color.YELLOW);

        // connect onTouch event handler
        this.setOnClickListener(this);
        this.setOnTouchListener(this);

        // need preset value
        this.widthPx = -1;
        this.heightPx = -1;
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (this.widthPx == -1) {

            // just calculate helper variables once
            this.widthPx = this.getWidth();
            this.heightPx = this.getHeight();

            if (this.widthPx <= this.heightPx) {

                this.length = this.widthPx;
                this.top = (this.heightPx - length) / 2;
                this.left = 0;
            }
            else {
                this.length = this.heightPx;
                this.top = 0;
                this.left = (this.widthPx - length) / 2;;
            }

            this.distance = length / 3;
        }

        String msg = String.format( "onDraw ###############   width = %d, height = %d", canvas.getWidth(), canvas.getHeight());
        Log.v("PeLo", msg);

        canvas.drawColor(Color.parseColor("#AAAAAA"));
        // canvas.drawRect (rect, this.paint);

        this.drawBoard(canvas);
    }

    // private helper methods
    private void drawBoard (Canvas canvas) {

        // compute padding
        int paddingHorizontal = this.cmInPxHorizontal / 4;   // 2.5 mm
        int paddingVertical = this.cmInPxVertical / 4;   // 2.5 mm

        // vertical lines
        this.drawLine(canvas, left + distance, top + paddingVertical, left + distance, top +  length - paddingVertical);
        this.drawLine(canvas, left + 2 * distance, top + paddingVertical, left + 2 * distance, top +  length - paddingVertical);

        this.drawLine(canvas, left + paddingHorizontal, top + distance, left + length - paddingHorizontal, top +  distance);
        this.drawLine(canvas, left + paddingHorizontal, top + 2 * distance, left + length - paddingHorizontal, top +  2 * distance);

    }

    private void drawLine (Canvas canvas, float startX, float startY, float stopX, float stopY ) {

        canvas.drawLine(startX, startY, stopX, stopY, this.paintLine );
    }

    private int pixelToCmHorizontal (int pixel) {

        return (int) Math.round(this.metrics.xdpi / 2.54);
    }

    private int pixelToCmVertical (int pixel) {

        return (int) Math.round(this.metrics.ydpi / 2.54);
    }

    private void evalClickEvent (int x, int y) {


        // need these rectangles for touch/click detection
        if (this.touchRectangles == null) {

            Rect r1 = new Rect (left, top, left + distance, top + distance);
            Rect r2 = new Rect (left + distance, top, left + 2*distance, top + distance);
            Rect r3 = new Rect (left + 2*distance, top, left + length, top + distance);

            Rect r4 = new Rect (left, top+ distance, left + distance, top + 2*distance);
            Rect r5 = new Rect (left + distance, top+distance, left + 2*distance, top + 2*distance);
            Rect r6 = new Rect (left + 2*distance, top+distance, left + length, top + 2*distance);


            Rect r7 = new Rect (left, top+ 2*distance, left + distance, top + length);
            Rect r8 = new Rect (left + distance, top+2*distance, left + 2*distance, top + length);
            Rect r9 = new Rect (left + 2*distance, top+2*distance, left + length, top + length);

            this.touchRectangles = new Rect[][] {
                    {r1, r2, r3},
                    {r4, r5, r6},
                    {r7, r8, r9}
            };
        }


        for (int i = 0; i < 3; i ++) {

            for (int j = 0; j < 3; j ++) {

                if (this.touchRectangles[i][j].contains(x, y)) {

                    Log.v("PeLo", "JAAAAAAAAAAAAAAA  ==> i = " + i + ", j = " + j);
                }
            }
        }
    }

    // member variable to save the X,Y coordinates
    private float[] lastTouchDownXY = new float[2];

    @Override
    public void onClick(View view) {

        // retrieve the stored coordinates
        float x = lastTouchDownXY[0];
        float y = lastTouchDownXY[1];

        // use the coordinates for whatever
        Log.v("PeLo", "onClick  ==> x = " + x + ", y = " + y);

        this.evalClickEvent ((int) x, (int) y);
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {

        // save the X,Y coordinates
        if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
            lastTouchDownXY[0] = event.getX();
            lastTouchDownXY[1] = event.getY();

            Log.v("PeLo", "onTouch  ==> x = " + lastTouchDownXY[0] + ", y = " + lastTouchDownXY[1]);
        }

        // let the touch event pass on to whoever needs it
        return false;
    }
}
