package peterloos.de.anothertictactoe;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class TicTacToeActivity extends AppCompatActivity implements View.OnClickListener {

    private Button buttonRestart;

    private TicTacToeView view;
    private ITicTacToe model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_tic_tac_toe);

        // retrieve references of controls
        this.view = this.findViewById(R.id.tictactoeView);
        this.buttonRestart = this.findViewById(R.id.buttonRestart);
        this.buttonRestart.setOnClickListener(this);

        // create model

        // this.model = new TicTacToeModelOffline(this.getApplicationContext());
        this.model = new TicTacToeModelFirebase(this.getApplicationContext());
        this.view.setTicTacToeModel(this.model);
    }

    @Override
    public void onClick(View view) {

        this.model.initGame();
    }
}
