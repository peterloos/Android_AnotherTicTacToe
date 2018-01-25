package peterloos.de.anothertictactoe;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewTreeObserver;

public class TicTacToeActivity extends AppCompatActivity {

    private Paint paintBorder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_tic_tac_toe);

        final View surface = this.findViewById(R.id.someview);


        ViewTreeObserver viewTreeObserver = surface.getViewTreeObserver();
        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    surface.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    int viewWidth = surface.getWidth();
                    int viewHeight = surface.getHeight();

                    String msg = String.format( "EAH   width = %d, height = %d", viewWidth, viewHeight);
                    Log.v("PeLo", msg);
                }
            });
        }
    }
}
