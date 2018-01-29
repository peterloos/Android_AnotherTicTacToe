package peterloos.de.anothertictactoe.views;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import peterloos.de.anothertictactoe.Globals;
import peterloos.de.anothertictactoe.interfaces.ITicTacToe;
import peterloos.de.anothertictactoe.interfaces.OnBoardChangedListener;
import peterloos.de.anothertictactoe.models.GameStone;

/**
 * Created by loospete on 29.01.2018.
 */

public class TicTacToeSurfaceView extends SurfaceView implements SurfaceHolder.Callback , View.OnTouchListener, OnBoardChangedListener {

    private SurfaceHolder holder;

    // model for this view
    private ITicTacToe model;

    // drawing utils
    private Paint paintLine;
    private Paint paintCircle;
    private Paint paintCross;

    private int mmInPxHorizontal;
    private int mmInPxVertical;

    private int top;
    private int left;
    private int length;
    private int distance;
    private Rect[][] helperRectangles;

    private int red;
    private int blue;
    private int black;

    private boolean firstOnDraw;

    public TicTacToeSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);

        Log.v(Globals.Tag, "TicTacToeSurfaceView ========================> ");

        this.holder = getHolder();
        this.holder.addCallback(this);

        // need some view metrics to layout UI elements
        DisplayMetrics metrics = new DisplayMetrics();
        Activity activity = ((Activity) this.getContext());
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        this.mmInPxHorizontal = this.pxToMMHorizontal(metrics);
        this.mmInPxVertical = this.pxToMMVertical(metrics);

        // setup colors
        this.red = Color.parseColor("#FF1E12");
        this.blue = Color.parseColor("#00C9FC");
        this.black = Color.parseColor("#333333");

        // setup painting objects
        this.paintLine = new Paint();
        this.paintLine.setStyle(Paint.Style.FILL_AND_STROKE);
        this.paintLine.setStrokeWidth(this.mmInPxHorizontal * 2);
        this.paintLine.setColor(Color.WHITE);

        this.paintCircle = new Paint();
        this.paintCircle.setColor(this.blue);
        this.paintCircle.setStrokeWidth(this.mmInPxHorizontal * 2);
        this.paintCircle.setStyle(Paint.Style.STROKE);

        this.paintCross = new Paint();
        this.paintCross.setStyle(Paint.Style.FILL_AND_STROKE);
        this.paintCross.setStrokeWidth(this.mmInPxHorizontal * 2);
        this.paintCross.setColor(this.red);

        // connect event handler
        this.setOnTouchListener(this);

        // do initialization stuff just once
        this.firstOnDraw = true;
    }

    // public interface
    public void setTicTacToeModel(ITicTacToe model) {

        this.model = model;
        this.model.setOnBoardChangedListener(this);
    }

//    @Override
//    public void draw(Canvas canvas) {
//        super.draw(canvas);
//
//        Log.v(Globals.Tag, "draw ========================> ");
//
//        // canvas.drawColor(Color.BLACK);
//
//        this.drawBoard2(canvas);
//    }

    // implementation of interface 'View.OnTouchListener'
    @Override
    public boolean onTouch(View view, MotionEvent event) {

        if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
            Log.v(Globals.Tag, "onTouch ........................................");
            this.handleClickEvent((int) event.getX(), (int) event.getY());
        }

        return true;  // remove event from the event pipeline
    }

    // implementation of interface 'SurfaceHolder.Callback'
    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {


    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        init(width, height);
        this.drawBoard();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

    }

    // implementation of interface 'SurfaceHolder.Callback'
    @Override
    public void clearBoard() {
        Log.v(Globals.Tag, "clearBoard ==========");
    }

    @Override
    public void stoneChangedAt(GameStone stone, int row, int col) {

        Log.v(Globals.Tag, "stoneChangedAt ==========");

        if (stone == GameStone.X) {

            this.paintCross(row, col);
        }
        else if (stone == GameStone.O) {

            this.paintCircle(row, col);
        }
    }

    // private helper methods
    private void paintCircle(int row, int col) {

        Canvas canvas = null;
        try {

            canvas = this.holder.lockCanvas();
            if (canvas != null) {

                // surface can be edited
                this.paintCircleHelper (canvas, row, col);
            }
        } finally {
            if (canvas != null) {
                this.holder.unlockCanvasAndPost(canvas);
            }
        }
    }

    private void paintCircleHelper(Canvas canvas, int row, int col) {

        int padding = this.mmInPxHorizontal * 3;
        Rect rect = this.helperRectangles[row][col];
        float radius = this.distance / 2 - padding;
        canvas.drawCircle(rect.centerX(), rect.centerY(), radius, this.paintCircle);
    }

    private void paintCross(int row, int col) {

        Canvas canvas = null;
        try {

            canvas = this.holder.lockCanvas();
            if (canvas != null) {

                // surface can be edited
                this.paintCrossHelper (canvas, row, col);
            }
        } finally {
            if (canvas != null) {
                this.holder.unlockCanvasAndPost(canvas);
            }
        }
    }

    private void paintCrossHelper(Canvas canvas, int row, int col) {

        // compute padding
        int paddingHorizontal = this.mmInPxHorizontal * 3;   // 3mm
        int paddingVertical = this.mmInPxVertical * 3;   // 3mm

        Rect rect = this.helperRectangles[row][col];

        canvas.drawLine(
                rect.left + paddingHorizontal,
                rect.top + paddingVertical,
                rect.right - paddingHorizontal,
                rect.bottom - paddingVertical,
                this.paintCross);

        canvas.drawLine(
                rect.right - paddingHorizontal,
                rect.top + paddingVertical,
                rect.left + paddingHorizontal,
                rect.bottom - paddingVertical,
                this.paintCross);
    }

    private void drawBoardHelper(Canvas canvas) {

        // surface can be edited
        // canvas.drawColor(this.black);
        canvas.drawColor(Color.RED);

        // compute padding
        int paddingHorizontal = this.mmInPxHorizontal * 2;   // 2mm
        int paddingVertical = this.mmInPxVertical * 2;   // 2mm

        // vertical lines
        this.drawLine(canvas, this.left + this.distance, this.top + paddingVertical, this.left + this.distance, this.top + this.length - paddingVertical);
        this.drawLine(canvas, this.left + 2 * this.distance, this.top + paddingVertical, this.left + 2 * this.distance, this.top + this.length - paddingVertical);

        // horizontal lines
        this.drawLine(canvas, this.left + paddingHorizontal, this.top + this.distance, this.left + this.length - paddingHorizontal, this.top + this.distance);
        this.drawLine(canvas, this.left + paddingHorizontal, this.top + 2 * this.distance, this.left + this.length - paddingHorizontal, this.top + 2 * this.distance);
    }

    public void drawBoard() {

        Canvas canvas = null;
        try {

            canvas = this.holder.lockCanvas();
            if (canvas != null) {

                // surface can be edited
                this.drawBoardHelper (canvas);
            }
        } finally {
            if (canvas != null) {
                this.holder.unlockCanvasAndPost(canvas);
            }
        }
    }

    private void drawLine(Canvas canvas, float startX, float startY, float stopX, float stopY) {

        canvas.drawLine(startX, startY, stopX, stopY, this.paintLine);
    }

    private void init(int width, int height) {

        if (width <= height) {

            this.length = width;
            this.top = (height - this.length) / 2;
            this.left = 0;
        } else {

            this.length = height;
            this.top = 0;
            this.left = (width - this.length) / 2;
        }

        this.distance = length / 3;

        // need these rectangles for touch/click detection and to draw circles and crosses
        Rect r1 = new Rect(this.left, this.top, this.left + this.distance, this.top + this.distance);
        Rect r2 = new Rect(this.left + this.distance, this.top, this.left + 2 * this.distance, this.top + this.distance);
        Rect r3 = new Rect(this.left + 2 * this.distance, this.top, this.left + this.length, this.top + this.distance);
        Rect r4 = new Rect(this.left, this.top + this.distance, this.left + this.distance, this.top + 2 * this.distance);
        Rect r5 = new Rect(this.left + this.distance, this.top + this.distance, this.left + 2 * this.distance, this.top + 2 * this.distance);
        Rect r6 = new Rect(this.left + 2 * this.distance, this.top + this.distance, this.left + this.length, this.top + 2 * this.distance);
        Rect r7 = new Rect(this.left, this.top + 2 * this.distance, this.left + this.distance, this.top + this.length);
        Rect r8 = new Rect(this.left + this.distance, this.top + 2 * this.distance, this.left + 2 * this.distance, this.top + this.length);
        Rect r9 = new Rect(this.left + 2 * this.distance, this.top + 2 * this.distance, this.left + this.length, this.top + this.length);

        this.helperRectangles = new Rect[][]{
                {r1, r2, r3},
                {r4, r5, r6},
                {r7, r8, r9}
        };
    }

    private void handleClickEvent(int x, int y) {

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                if (this.helperRectangles[row][col].contains(x, y)) {

                    this.model.setStone(row, col); // update model
                    return;
                }
            }
        }
    }

    private int pxToMMHorizontal(DisplayMetrics metrics) {

        // compute exact physical pixels per inch of the screen (X dimension)
        float xdpi = metrics.xdpi;
        return (int) Math.round(xdpi / 25.4);
    }

    private int pxToMMVertical(DisplayMetrics metrics) {

        // compute exact physical pixels per inch of the screen (Y dimension)
        float ydpi = metrics.ydpi;
        return (int) Math.round(ydpi / 25.4);
    }
}
