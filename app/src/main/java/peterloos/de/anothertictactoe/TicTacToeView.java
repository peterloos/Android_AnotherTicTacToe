package peterloos.de.anothertictactoe;

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

import java.util.Locale;

/**
 * Created by loospete on 24.01.2018.
 */

public class TicTacToeView extends View implements View.OnTouchListener {

    private final int Dimension = 3;

    // drawing utils
    private Paint paintLine;
    private Paint paintCircle;
    private Paint paintCross;

    private int mmInPxHorizontal;
    private int mmInPxVertical;

    private int widthPx;
    private int heightPx;

    private int top;
    private int left;
    private int length;
    private int distance;
    private Rect[][] helperRectangles;

    private int red;
    private int blue;
    private int back;

    private boolean firstOnDraw;

    // game logic
    private Stone[][] board;
    private boolean firstPlayer;
    private GameState gameState;

    public TicTacToeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        // need some metrics to layout UI elements
        DisplayMetrics metrics = new DisplayMetrics();
        Activity activity = ((Activity) this.getContext());
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        this.mmInPxHorizontal = this.pixelToCmHorizontal(metrics);
        this.mmInPxVertical = this.pixelToCmVertical(metrics);

        // setup colors
        this.red = Color.parseColor("#FF1E12");
        this.blue = Color.parseColor("#00C9FC");
        this.back = Color.parseColor("#333333");

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

        // need preset value
        this.widthPx = -1;
        this.heightPx = -1;

        // initialize model data
        this.board = new Stone[Dimension][Dimension];
        this.firstOnDraw = true;

        this.restartGame();
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (this.firstOnDraw) {

            this.firstOnDraw = false;
            this.init();
        }

        this.drawBoard(canvas);
        this.drawStones(canvas);
    }

    // public interface
    public void restartGame() {

        for (int i = 0; i < Dimension; i++) {
            for (int j = 0; j < Dimension; j++) {
                this.board[i][j] = Stone.Empty;
            }
        }
        this.firstPlayer = true;
        this.gameState = GameState.Active;
        this.invalidate();
    }

    // private helper methods
    private void drawBoard(Canvas canvas) {

        canvas.drawColor(this.back);

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

                if (this.board[row][col] == Stone.X) {

                    this.paintCross(canvas, row, col);
                } else if (this.board[row][col] == Stone.O) {

                    this.paintCircle(canvas, row, col);
                }
            }
        }
    }

    private void drawLine(Canvas canvas, float startX, float startY, float stopX, float stopY) {

        canvas.drawLine(startX, startY, stopX, stopY, this.paintLine);
    }

    private int pixelToCmHorizontal(DisplayMetrics metrics) {

        // exact physical pixels per inch of the screen in the X dimension
        float xdpi = metrics.xdpi;
        return (int) Math.round(xdpi / 25.4);
    }

    private int pixelToCmVertical(DisplayMetrics metrics) {

        // exact physical pixels per inch of the screen in the Y dimension
        float ydpi = metrics.ydpi;
        return (int) Math.round(ydpi / 25.4);
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

        canvas.drawLine(rect.left + paddingHorizontal, rect.top + paddingVertical, rect.right - paddingHorizontal, rect.bottom - paddingVertical, this.paintCross);
        canvas.drawLine(rect.right - paddingHorizontal, rect.top + paddingVertical, rect.left + paddingHorizontal, rect.bottom - paddingVertical, this.paintCross);
    }

    private void evalClickEvent(int x, int y) {

        for (int row = 0; row < 3; row++) {

            for (int col = 0; col < 3; col++) {

                if (this.helperRectangles[row][col].contains(x, y)) {

                    // update logic
                    if (this.setStone(row, col)) {

                        this.firstPlayer = !this.firstPlayer;

                        // update view
                        this.invalidate();

                        if (this.checkForEndOfGame()) {

                            String result = String.format(
                                    Locale.getDefault(),
                                    "Tic-Tac-Toe: %s player won the game !",
                                    this.firstPlayer ? "Second" : "First");

                            Toast.makeText(this.getContext(), result, Toast.LENGTH_SHORT).show();
                        }
                    }

                    return;
                }
            }
        }
    }

    private void init() {

        // calculate helper variables
        this.widthPx = this.getWidth();
        this.heightPx = this.getHeight();

        if (this.widthPx <= this.heightPx) {

            this.length = this.widthPx;
            this.top = (this.heightPx - this.length) / 2;
            this.left = 0;
        } else {
            this.length = this.heightPx;
            this.top = 0;
            this.left = (this.widthPx - this.length) / 2;
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

    @Override
    public boolean onTouch(View view, MotionEvent event) {

        // this.gameState = GameState.Active;
        if (this.gameState == GameState.Inactive) {

            // ignore touch event
            return false;
        }

        if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
            this.evalClickEvent((int) event.getX(), (int) event.getY());
        }

        return true;  // remove event from the event pipeline
    }

    // logic of game
    private boolean isFieldEmpty(int row, int col) {

        return this.board[row][col] == Stone.Empty;
    }

    private boolean setStone(int row, int col) {

        // is there already a stone
        if (!isFieldEmpty(row, col))
            return false;

        Log.v("PeLo", "setStone ==> row = " + row + ", col = " + col);
        this.board[row][col] = (this.firstPlayer) ? Stone.X : Stone.O;
        return true;
    }

    private boolean checkForEndOfGame() {
        boolean lastPlayer = !this.firstPlayer;

        Stone stone = (lastPlayer) ? Stone.X : Stone.O;

        // test columns
        for (int row = 0; row < 3; row++) {
            if (this.board[row][0] == stone && this.board[row][1] == stone && this.board[row][2] == stone)
                return true;
        }

        // test rows
        for (int col = 0; col < 3; col++) {
            if (this.board[0][col] == stone && this.board[1][col] == stone && this.board[2][col] == stone)
                return true;
        }

        // test diagonals
        if (this.board[0][0] == stone && this.board[1][1] == stone && this.board[2][2] == stone)
            return true;
        if (this.board[2][0] == stone && this.board[1][1] == stone && this.board[0][2] == stone)
            return true;

        // could be a draw
        int emtpyStones = 0;
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                if (this.board[row][col] == Stone.Empty) {
                    emtpyStones++;
                    break;
                }
            }
        }
        if (emtpyStones == 0) {

            Toast.makeText(this.getContext(), "Tic-Tac-Toe: Sorry - Game over ...",
                    Toast.LENGTH_SHORT).show();
        }

        return false;
    }

    // game specific constants - symbolic notation
    private enum Stone {
        Empty, X, O
    }

    private enum GameState {
        Active, Inactive
    }
}
