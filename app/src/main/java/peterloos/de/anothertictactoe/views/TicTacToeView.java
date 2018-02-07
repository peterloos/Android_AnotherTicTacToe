package peterloos.de.anothertictactoe.views;

import android.app.Activity;
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
import android.widget.Toast;

import peterloos.de.anothertictactoe.Globals;
import peterloos.de.anothertictactoe.interfaces.ITicTacToe;
import peterloos.de.anothertictactoe.interfaces.OnBoardChangedListener;
import peterloos.de.anothertictactoe.interfaces.OnPlayersChangedListener;
import peterloos.de.anothertictactoe.models.GameStone;

import static peterloos.de.anothertictactoe.Globals.Dimension;

/**
 * Created by loospete on 24.01.2018.
 */

public class TicTacToeView extends View implements View.OnTouchListener, OnBoardChangedListener {

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

    // c'tor
    public TicTacToeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

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
    public void setTicTacToeModel (ITicTacToe model) {

        this.model = model;
        this.model.setOnBoardChangedListener(this);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (this.firstOnDraw) {

            this.firstOnDraw = false;
            this.init();
        }

        this.drawBoard(canvas);
        this.drawStones(canvas);
    }

    // implementation of interface 'OnBoardChangedListener'
    @Override
    public void stoneChangedAt(int row, int col, GameStone stone) {

        // update view
        this.invalidate();
    }

    @Override
    public void clearBoard() {

        // update view
        this.invalidate();
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {

        if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
            Log.v(Globals.Tag, "onTouch ........................................");
            this.handleClickEvent((int) event.getX(), (int) event.getY());
        }

        return true;  // remove event from the event pipeline
    }

    // private helper methods
    private void drawBoard(Canvas canvas) {

        canvas.drawColor(this.black);

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

    private void drawStones(Canvas canvas) {

        for (int row = 0; row < Dimension; row++) {
            for (int col = 0; col < Dimension; col++) {
                if (this.model.getStoneAt(row + 1,col + 1) == GameStone.X) {
                    this.paintCross(canvas, row, col);
                } else if (this.model.getStoneAt(row + 1,col + 1) == GameStone.O) {
                    this.paintCircle(canvas, row, col);
                }
            }
        }
    }

    private void drawLine(Canvas canvas, float startX, float startY, float stopX, float stopY) {

        canvas.drawLine(startX, startY, stopX, stopY, this.paintLine);
    }

    private void paintCircle(Canvas canvas, int row, int col) {

        int padding = this.mmInPxHorizontal * 3;
        Rect rect = this.helperRectangles[row][col];
        float radius = this.distance / 2 - padding;
        canvas.drawCircle(rect.centerX(), rect.centerY(), radius, this.paintCircle);
    }

    private void paintCross(Canvas canvas, int row, int col) {

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

    private void handleClickEvent(int x, int y) {

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                if (this.helperRectangles[row][col].contains(x, y)) {

                    this.model.setStone(row + 1, col + 1); // update model
                    return;
                }
            }
        }
    }

    private void init() {

        // calculate helper variables
        int widthPx = this.getWidth();
        int heightPx = this.getHeight();

        if (widthPx <= heightPx) {

            this.length = widthPx;
            this.top = (heightPx - this.length) / 2;
            this.left = 0;
        } else {

            this.length = heightPx;
            this.top = 0;
            this.left = (widthPx - this.length) / 2;
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
