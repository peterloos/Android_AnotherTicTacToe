package peterloos.de.anothertictactoe.activities;

import android.content.res.Resources;
import android.graphics.Color;
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

public class TicTacToeActivity extends AppCompatActivity implements View.OnClickListener, OnPlayersConfigurationChangedListener {

    // UI controls
    private Button buttonEnter;
    private Button buttonLeave;
    private Button buttonStart;
    private Button buttonClear;
    private Button buttonRestart;

    private EditText edittextNickname;
    private TextView textviewPlayer1;
    private TextView textviewPlayer2;

    private TicTacToeView view;
    private Toolbar toolbar;

    private Resources res;
    private int red;
    private int green;
    private int blue;

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
        this.buttonEnter = this.findViewById(R.id.buttonEnter);
        this.buttonLeave = this.findViewById(R.id.buttonLeave);
        this.edittextNickname = this.findViewById(R.id.edittextNickname);
        this.textviewPlayer1 = this.findViewById(R.id.textviewPlayer1);
        this.textviewPlayer2 = this.findViewById(R.id.textviewPlayer2);
        this.buttonStart = this.findViewById(R.id.buttonStart);
        this.buttonRestart = this.findViewById(R.id.buttonRestart);
        this.buttonClear = this.findViewById(R.id.buttonClear);

        this.buttonEnter.setOnClickListener(this);
        this.buttonLeave.setOnClickListener(this);
        this.buttonStart.setOnClickListener(this);
        this.buttonClear.setOnClickListener(this);
        this.buttonRestart.setOnClickListener(this);

        // clear textview's upon creation
        this.textviewPlayer1.setText("");
        this.textviewPlayer2.setText("");

        this.res = this.getResources();
        this.red = res.getColor(R.color.LightRed1);
        this.green = res.getColor(R.color.LightGreen);
        this.blue = res.getColor(R.color.LightBlue1);

        // create model
        // this.model = new TicTacToeModelOffline(this.getApplicationContext());
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

                // clear UI
                this.edittextNickname.setText("");
            }
        } else if (view == this.buttonLeave) {

            this.model.leavePlayer();

        } else if (view == this.buttonStart) {

            this.model.start();

        } else if (view == this.buttonClear) {

            this.model.clear();

        } else if (view == this.buttonRestart) {

            this.model.restart();
        }
    }

    // implementation of interface 'OnPlayersConfigurationChangedListener'

    @Override
//    public void playersActivityStateChanged(boolean firstPlayerIsActive, boolean secondPlayerIsActive) {
//
//        if (!firstPlayerIsActive && !secondPlayerIsActive) {
//
//            this.textviewPlayer1.setBackgroundColor(Color.LTGRAY);
//            this.textviewPlayer2.setBackgroundColor(Color.LTGRAY);
//
//        } else if (firstPlayerIsActive && !secondPlayerIsActive) {
//
//            this.textviewPlayer1.setBackgroundColor(green);
//            this.textviewPlayer2.setBackgroundColor(red);
//
//
//        } else if (!firstPlayerIsActive && secondPlayerIsActive) {
//
//            this.textviewPlayer1.setBackgroundColor(red);
//            this.textviewPlayer2.setBackgroundColor(green);
//        }
//    }

    public void playersActivityStateChanged(int whichPlayer, boolean playersState) {

        if (whichPlayer == 0) {

            this.textviewPlayer1.setBackgroundColor((playersState) ? this.green : this.red);
        }
        else if (whichPlayer == 1) {

            this.textviewPlayer2.setBackgroundColor((playersState) ? this.green : this.red);
        }
    }

    @Override
    public void currentPlayersNameChanged(String name) {

        this.textviewPlayer1.setText(name);
        this.textviewPlayer1.setBackgroundColor(this.blue);
    }

    @Override
    public void otherPlayersNameChanged(String name) {

        this.textviewPlayer2.setText(name);
        this.textviewPlayer2.setBackgroundColor(this.blue);
    }
}
