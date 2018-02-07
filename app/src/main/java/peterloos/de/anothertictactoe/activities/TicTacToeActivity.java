package peterloos.de.anothertictactoe.activities;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import peterloos.de.anothertictactoe.R;
import peterloos.de.anothertictactoe.interfaces.ITicTacToe;
import peterloos.de.anothertictactoe.interfaces.OnPlayersChangedListener;
import peterloos.de.anothertictactoe.models.TicTacToeModelFirebase;
import peterloos.de.anothertictactoe.views.TicTacToeView;

// TODO
// https://github.com/riscie/websocket-tic-tac-toe
// "Waiting to get paired"
// "Game begins"
// "Restart"

public class TicTacToeActivity extends AppCompatActivity implements View.OnClickListener, OnPlayersChangedListener {

    // UI controls
    private Button buttonRegister;
    private Button buttonUnregister;
    private Button buttonRestart;

    private TextView textviewNickname;
    private TextView textviewPlayer1;
    private TextView textviewPlayer2;

    private TicTacToeView view;
    private Toolbar toolbar;

    // data model
    private ITicTacToe model;
    private String currentPlayer;

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
        this.buttonUnregister = this.findViewById(R.id.buttonUnregister);
        this.textviewNickname = this.findViewById(R.id.textviewNickname);
        this.textviewPlayer1 = this.findViewById(R.id.textviewPlayer1);
        this.textviewPlayer2 = this.findViewById(R.id.textviewPlayer2);
        this.buttonRestart = this.findViewById(R.id.buttonRestart);
        this.buttonRegister.setOnClickListener(this);
        this.buttonRestart.setOnClickListener(this);

        // clear textview's upon creation
        this.textviewPlayer1.setText("---------------");
        this.textviewPlayer2.setText("---------------");

        // create model
        // this.model = new TicTacToeModelOffline(this.getApplicationContext());
        this.model = new TicTacToeModelFirebase(this.getApplicationContext());
        this.model.setOnPlayersChangedListener(this);
        this.view.setTicTacToeModel(this.model);

        this.currentPlayer = "";
    }

    @Override
    public void onClick(View view) {

        if (view == this.buttonRestart) {
            this.model.initGame();
        }
        else if (view == this.buttonRegister) {

            String nickname = this.textviewNickname.getText().toString();

            if (! nickname.equals("")) {

                this.model.registerPlayer(nickname);
            }
        }
        else if (view == this.buttonUnregister) {

        }
    }

    // implementation of interface 'OnPlayersChangedListener'
    @Override
    public void playersChanged(String firstPlayer, String secondPlayer) {

        // determine who am I :-)
        if (firstPlayer.equals("") && secondPlayer.equals("")) {

            String msg = "Waiting to get paired ...";
            Toast.makeText(this.getApplicationContext(), msg, Toast.LENGTH_LONG).show();
        }
        else if (! firstPlayer.equals("") && secondPlayer.equals("")) {

            this.currentPlayer = firstPlayer;
            this.textviewPlayer1.setText(firstPlayer);

            String msg = "1. Player: " + firstPlayer + " - waiting for 2. Player ...";
            Toast.makeText(this.getApplicationContext(), msg, Toast.LENGTH_LONG).show();
        }
        else if (! firstPlayer.equals("") && ! secondPlayer.equals("")) {

            this.currentPlayer = secondPlayer;
            this.textviewPlayer1.setText(firstPlayer);
            this.textviewPlayer2.setText(secondPlayer);

            String msg = "1. Player: " + firstPlayer + " 2. Player: "  + secondPlayer + " - Game begins";
            Toast.makeText(this.getApplicationContext(), msg, Toast.LENGTH_LONG).show();
        }
    }
}
