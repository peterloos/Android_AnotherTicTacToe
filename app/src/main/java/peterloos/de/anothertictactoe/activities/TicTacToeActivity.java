package peterloos.de.anothertictactoe.activities;

import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import peterloos.de.anothertictactoe.Globals;
import peterloos.de.anothertictactoe.R;
import peterloos.de.anothertictactoe.interfaces.ITicTacToe;
import peterloos.de.anothertictactoe.interfaces.OnPlayersConfigurationChangedListener;
import peterloos.de.anothertictactoe.models.TicTacToeModelFirebase;
import peterloos.de.anothertictactoe.views.TicTacToeView;

public class TicTacToeActivity
        extends AppCompatActivity
        implements View.OnClickListener, OnPlayersConfigurationChangedListener {

    // UI controls
    private Button buttonEnter;
    private Button buttonExit;
    private Button buttonStart;
    private Button buttonRestart;

    private EditText edittextNickname;
    private TextView textviewPlayer1;
    private TextView textviewPlayer2;
    private TextView textviewScore1;
    private TextView textviewScore2;

    private TicTacToeView view;
    private Toolbar toolbar;

    private Resources res;
    private int red;
    private int green;
    private int blue;
    private int lightgrey;

    // data model
    private ITicTacToe model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_tic_tac_toe);

        // setup toolbar
        this.toolbar = (Toolbar) this.findViewById(R.id.main_toolbar);
        this.toolbar.setTitleTextColor(Color.WHITE);
        this.toolbar.setTitle("Another Tic-Tac-Toe");
        this.toolbar.setSubtitle("Simple Multiplayer Game");
        this.setSupportActionBar(this.toolbar);

        // retrieve references of controls
        this.view = this.findViewById(R.id.tictactoeView);
        this.buttonEnter = this.findViewById(R.id.buttonEnter);
        this.buttonExit = this.findViewById(R.id.buttonExit);
        this.edittextNickname = this.findViewById(R.id.edittextNickname);
        this.textviewPlayer1 = this.findViewById(R.id.textviewPlayer1);
        this.textviewPlayer2 = this.findViewById(R.id.textviewPlayer2);
        this.textviewScore1 = this.findViewById(R.id.textviewScore1);
        this.textviewScore2 = this.findViewById(R.id.textviewScore2);
        this.buttonStart = this.findViewById(R.id.buttonStart);
        this.buttonRestart = this.findViewById(R.id.buttonRestart);

        // register event handler
        this.buttonEnter.setOnClickListener(this);
        this.buttonExit.setOnClickListener(this);
        this.buttonStart.setOnClickListener(this);
        this.buttonRestart.setOnClickListener(this);

        // initialize textview's upon creation
        this.textviewPlayer1.setText("");
        this.textviewPlayer2.setText("");
        this.textviewScore1.setText(R.string.score_0);
        this.textviewScore2.setText(R.string.score_0);

        this.res = this.getResources();
        this.red = this.res.getColor(R.color.Red);
        this.green = this.res.getColor(R.color.LightGreen);
        this.blue = this.res.getColor(R.color.LightBlue);
        this.lightgrey = this.res.getColor(R.color.VeryLightGrey);

        this.textviewPlayer1.setBackgroundColor(this.lightgrey);
        this.textviewPlayer2.setBackgroundColor(this.lightgrey);

        // create model
        this.model = new TicTacToeModelFirebase(this.getApplicationContext());
        this.model.setOnPlayersChangedListener(this);
        this.view.setTicTacToeModel(this.model);
    }

    @Override
    public void onClick(View view) {

        if (view == this.buttonEnter) {

            String nickname = this.edittextNickname.getText().toString();
            if (!nickname.equals("")) {

                this.model.enterPlayer(nickname);
                this.edittextNickname.setText("");
            }
        } else if (view == this.buttonStart) {

            this.model.start();

        } else if (view == this.buttonRestart) {

            this.model.restart();

        } else if (view == this.buttonExit) {

            this.model.exit();
        }
    }

    // implementation of interface 'OnPlayersConfigurationChangedListener'

    @Override
    public void playersActivityStateChanged(int whichPlayer, boolean active) {

        if (whichPlayer == 0) {

            if (active) {

                this.textviewPlayer1.setBackgroundColor(this.green);
                this.textviewPlayer2.setBackgroundColor(this.red);
            } else {

                this.textviewPlayer1.setBackgroundColor(this.red);
                this.textviewPlayer2.setBackgroundColor(this.green);
            }
        } else if (whichPlayer == 1) {

            if (active) {

                this.textviewPlayer1.setBackgroundColor(this.green);
                this.textviewPlayer2.setBackgroundColor(this.red);
            } else {

                this.textviewPlayer1.setBackgroundColor(this.red);
                this.textviewPlayer2.setBackgroundColor(this.green);
            }
        }
    }

    @Override
    public void currentPlayersNameChanged(String name) {

        if (name == null || name.equals("")) {

            this.textviewPlayer1.setText("");
            this.textviewPlayer1.setBackgroundColor(this.lightgrey);
        }
        else {

            this.textviewPlayer1.setText(name);
            this.textviewPlayer1.setBackgroundColor(this.blue);
        }
    }

    @Override
    public void otherPlayersNameChanged(String name) {

        if (name == null || name.equals("")) {

            this.textviewPlayer2.setText("");
            this.textviewPlayer2.setBackgroundColor(this.lightgrey);
        }
        else {

            this.textviewPlayer2.setText(name);
            this.textviewPlayer2.setBackgroundColor(this.blue);
        }
    }

    @Override
    public void scoreChanged(int score, boolean atLeftSide) {

        if (atLeftSide) {
            this.textviewScore1.setText("Score: " + Integer.toString(score));
        } else {
            this.textviewScore2.setText("Score: " + Integer.toString(score));
        }
    }

    @Override
    public void clearPlayersStateChanged() {

        this.textviewPlayer1.setBackgroundColor(this.blue);
        this.textviewPlayer2.setBackgroundColor(this.blue);
    }
}
