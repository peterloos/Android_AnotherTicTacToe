package peterloos.de.anothertictactoe.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import peterloos.de.anothertictactoe.R;
import peterloos.de.anothertictactoe.interfaces.ITicTacToe;
import peterloos.de.anothertictactoe.models.TicTacToeModelFirebase;
import peterloos.de.anothertictactoe.models.TicTacToeModelOffline;
import peterloos.de.anothertictactoe.views.TicTacToeSurfaceView;
import peterloos.de.anothertictactoe.views.TicTacToeView;

public class TicTacToeActivity extends AppCompatActivity implements View.OnClickListener {

    private Button buttonTest;
    private Button buttonRestart;

    private TicTacToeView view;
    // private TicTacToeSurfaceView view;
    private ITicTacToe model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_tic_tac_toe);

        // retrieve references of controls
        this.view = this.findViewById(R.id.tictactoeView);
        this.buttonTest = this.findViewById(R.id.buttonTest);
        this.buttonRestart = this.findViewById(R.id.buttonRestart);
        this.buttonTest.setOnClickListener(this);
        this.buttonRestart.setOnClickListener(this);

        // create model
        // this.model = new TicTacToeModelOffline(this.getApplicationContext());
        this.model = new TicTacToeModelFirebase(this.getApplicationContext());
        this.view.setTicTacToeModel(this.model);
    }

    @Override
    public void onClick(View view) {

        if (view == this.buttonRestart) {
            this.model.initGame();
        }
        else if (view == this.buttonTest) {

        }
    }
}
