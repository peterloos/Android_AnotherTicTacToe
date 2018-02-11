package peterloos.de.anothertictactoe.models;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.util.Locale;

import peterloos.de.anothertictactoe.interfaces.ITicTacToe;
import peterloos.de.anothertictactoe.interfaces.OnBoardChangedListener;
import peterloos.de.anothertictactoe.interfaces.OnPlayersConfigurationChangedListener;

import static peterloos.de.anothertictactoe.Globals.Dimension;

/**
 * Created by loospete on 27.01.2018.
 */

public class TicTacToeModelOffline implements ITicTacToe {

    // member data
    private Context context;

    private GameStone[][] board;
    private boolean firstPlayer;
    private GameState gameState;

    // listeners
    private OnBoardChangedListener boardListener;
    private OnPlayersConfigurationChangedListener playersListener;

    // c'tor
    public TicTacToeModelOffline(Context context) {

        this.context = context;
        this.board = new GameStone[Dimension][Dimension];
        this.startGame();
    }

    // implementation of interface 'ITicTacToe'

    @Override
    public void setOnBoardChangedListener(OnBoardChangedListener listener) {

        this.boardListener = listener;
    }

    @Override
    public void setOnPlayersChangedListener(OnPlayersConfigurationChangedListener listener) {

        this.playersListener = listener;
    }

    @Override
    public void registerPlayer (String player) {

        // TODO: TBD
    }

    @Override
    public void unregisterPlayer () {

        // TODO: TBD
    }

    @Override
    public void startGame() {

        for (int i = 0; i < Dimension; i++) {
            for (int j = 0; j < Dimension; j++) {
                this.board[i][j] = GameStone.Empty;
            }
        }

        this.firstPlayer = true;
        this.gameState = GameState.Active;

        if (this.boardListener != null) {
            this.boardListener.clearBoard();
        }
    }

    @Override
    public void restartGame() {

        // TODO:
        // probably to be done
    }

        @Override
    public GameStone getStoneAt(int row, int col) {

        return this.board[row][col];
    }

    @Override
    public boolean setStone(int row, int col) {

        // ignore this request - current game over or not initialized
        if (this.gameState == GameState.Inactive)
            return false;

        // is there already a stone, ignore call
        if (!isFieldEmpty(row, col))
            return false;

        // set stone on board
        Log.v("PeLo", "setStone ==> row = " + row + ", col = " + col);
        GameStone stone = (this.firstPlayer) ? GameStone.X : GameStone.O;
        this.board[row][col] = stone;
        this.firstPlayer = !this.firstPlayer;
        if (this.boardListener != null) {
            this.boardListener.stoneChangedAt(row, col, stone);
        }

        // check for end of game
        if (this.checkForEndOfGame()) {

            this.gameState = GameState.Inactive;

            String result = String.format(
                    Locale.getDefault(),
                    "Tic-Tac-Toe: %s player won the game !",
                    this.firstPlayer ? "Second" : "First");

            Toast.makeText(this.context, result, Toast.LENGTH_SHORT).show();
        }

        return true;
    }

    // private helper methods
    private boolean isFieldEmpty(int row, int col) {

        return this.board[row][col] == GameStone.Empty;
    }

    private boolean checkForEndOfGame() {

        boolean lastPlayer = !this.firstPlayer;
        GameStone stone = (lastPlayer) ? GameStone.X : GameStone.O;

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
        int emptyStones = 0;
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                if (this.board[row][col] == GameStone.Empty) {
                    emptyStones++;
                    break;
                }
            }
        }
        if (emptyStones == 0) {

            this.gameState = GameState.Inactive;

            Toast.makeText(this.context, "Tic-Tac-Toe: Sorry - Game over ...",
                    Toast.LENGTH_SHORT).show();

            // TODO:
            // Dialog:
            // "Game Over"
            // "It's a draw"
            // "Return to main menu" oder "ok"
        }

        return false;
    }
}
