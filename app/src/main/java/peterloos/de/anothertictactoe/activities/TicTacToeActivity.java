package peterloos.de.anothertictactoe.activities;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import peterloos.de.anothertictactoe.R;
import peterloos.de.anothertictactoe.interfaces.ITicTacToe;
import peterloos.de.anothertictactoe.models.TicTacToeModelFirebase;
import peterloos.de.anothertictactoe.views.TicTacToeView;

// TODO
// https://github.com/riscie/websocket-tic-tac-toe
// "Waiting to get paired"
// "Game begins"
// "Restart"

public class TicTacToeActivity extends AppCompatActivity implements View.OnClickListener {

    // UI controls
    private Button buttonRegister;
    private Button buttonUnregister;
    private Button buttonRestart;
    private TicTacToeView view;
    // private TicTacToeSurfaceView view;
    private Toolbar toolbar;

    // data model
    private ITicTacToe model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_tic_tac_toe);

        // setup toolbar
        this.toolbar = (Toolbar) this.findViewById(R.id.main_toolbar);
        this.toolbar.setTitleTextColor(Color.WHITE);
        this.toolbar.setTitle("Tic Tac Toe");
        this.toolbar.setSubtitle("noch was");
        this.setSupportActionBar(this.toolbar);

        // retrieve references of controls
        this.view = this.findViewById(R.id.tictactoeView);
        this.buttonRegister = this.findViewById(R.id.buttonRegister);
        this.buttonRestart = this.findViewById(R.id.buttonRestart);
        this.buttonRegister.setOnClickListener(this);
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
        else if (view == this.buttonRegister) {

        }
        else if (view == this.buttonUnregister) {

        }
    }
}
