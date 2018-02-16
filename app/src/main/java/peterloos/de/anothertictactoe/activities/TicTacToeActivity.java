package peterloos.de.anothertictactoe.activities;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
    private Button buttonClear;
    private Button buttonRestart;

    private Button buttonXXXXXXX;

    private EditText edittextNickname;
    private TextView textviewPlayer1;
    private TextView textviewPlayer2;

    private TicTacToeView view;
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
        this.buttonEnter = this.findViewById(R.id.buttonEnter);
        this.buttonLeave = this.findViewById(R.id.buttonLeave);
        this.edittextNickname = this.findViewById(R.id.edittextNickname);
        this.textviewPlayer1 = this.findViewById(R.id.textviewPlayer1);
        this.textviewPlayer2 = this.findViewById(R.id.textviewPlayer2);
        this.buttonRestart = this.findViewById(R.id.buttonRestart);
        this.buttonClear = this.findViewById(R.id.buttonClear);

        this.buttonEnter.setOnClickListener(this);
        this.buttonLeave.setOnClickListener(this);
        this.buttonClear.setOnClickListener(this);
        this.buttonRestart.setOnClickListener(this);


        // NUR ZUM TESTEN
        this.buttonXXXXXXX = this.findViewById(R.id.buttonXXXXXXX);
        this.buttonXXXXXXX.setOnClickListener(this);


        // clear textview's upon creation
        this.textviewPlayer1.setText("---------------");
        this.textviewPlayer2.setText("---------------");

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
        } else if (view == this.buttonClear) {

            this.model.clearBoard();

        } else if (view == this.buttonRestart) {

            this.model.restartGame();

        }
//        else if (view == this.buttonXXXXXXX) {
//
//            this.model.enterPlayer();
//            Toast.makeText(this.getApplicationContext(), "Yeahhhhhhhhhhhhhhhhhh", Toast.LENGTH_SHORT).show();
//        }
    }

    // implementation of interface 'OnPlayersConfigurationChangedListener'
    @Override
    public void playersNamesChanged(String firstPlayer, String secondPlayer) {

        // TODO: Just for testing
        // TODO: Sollte man am Ende entfernen ...

        String s1 = firstPlayer.equals("") ? "EMPTY" : firstPlayer;
        String s2 = secondPlayer.equals("") ? "EMPTY" : secondPlayer;
        String s = String.format("%s - %s", s1, s2);
        Log.v(Globals.Tag, s);

        this.textviewPlayer1.setText(firstPlayer);
        this.textviewPlayer2.setText(secondPlayer);
    }

    @Override
    public void playersActivityStateChanged(boolean firstPlayerIsActive, boolean secondPlayerIsActive) {

        if (!firstPlayerIsActive && !secondPlayerIsActive) {

//            this.textviewPlayer1.setBackgroundColor(Color.LTGRAY);
//            this.textviewPlayer2.setBackgroundColor(Color.LTGRAY);

            this.changeBackground (this.textviewPlayer1.getBackground(), R.color.LightGrey);
            this.changeBackground (this.textviewPlayer2.getBackground(), R.color.LightGrey);
        } else if (firstPlayerIsActive && !secondPlayerIsActive) {

//            this.textviewPlayer1.setBackgroundColor(Color.RED);
//            this.textviewPlayer2.setBackgroundColor(Color.GREEN);

            this.changeBackground (this.textviewPlayer1.getBackground(), R.color.OrangeRed);
            this.changeBackground (this.textviewPlayer2.getBackground(), R.color.LightGreen);

        } else if (!firstPlayerIsActive && secondPlayerIsActive) {

//            this.textviewPlayer1.setBackgroundColor(Color.GREEN);
//            this.textviewPlayer2.setBackgroundColor(Color.RED);

            this.changeBackground (this.textviewPlayer1.getBackground(), R.color.LightGreen);
            this.changeBackground (this.textviewPlayer2.getBackground(), R.color.OrangeRed);
        }
    }

    // private helper methods
    private void changeBackground (Drawable background, int color) {

        if (background instanceof ColorDrawable) {
            // alpha value may need to be set again after this call
            GradientDrawable gradientDrawable = (GradientDrawable) background;
            int singleColor = ContextCompat.getColor(this.getApplicationContext(), color);
            gradientDrawable.setColor(singleColor);
        }
    }
}


//    // trying to change the background color
//    Drawable background = this.textviewPlayer1.getBackground();
//        if (background instanceof ShapeDrawable) {
//                // cast to 'ShapeDrawable'
//                ShapeDrawable shapeDrawable = (ShapeDrawable) background;
//                shapeDrawable.getPaint().setColor(ContextCompat.getColor(this.getApplicationContext(),R.color.common_border_color));
//                } else if (background instanceof GradientDrawable) {
//                // cast to 'GradientDrawable'
//                GradientDrawable gradientDrawable = (GradientDrawable) background;
//                gradientDrawable.setColor(ContextCompat.getColor(this.getApplicationContext(),R.color.common_border_color));
//                } else if (background instanceof ColorDrawable) {
//                // alpha value may need to be set again after this call
//                ColorDrawable colorDrawable = (ColorDrawable) background;
//                colorDrawable.setColor(ContextCompat.getColor(this.getApplicationContext(),R.color.common_border_color));
//                }