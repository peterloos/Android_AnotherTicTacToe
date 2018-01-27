package peterloos.de.anothertictactoe;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import static peterloos.de.anothertictactoe.Globals.Dimension;

/**
 * Created by loospete on 27.01.2018.
 */

public class TicTacToeModel {

    // member data
    private Stone[][] board;
    private boolean firstPlayer;

    private Context context;

    // c'tor
    public TicTacToeModel(Context context) {

        this.context = context;
        this.board = new Stone[Dimension][Dimension];
    }

    // public interface
    public void restartGame() {

        for (int i = 0; i < Dimension; i++) {
            for (int j = 0; j < Dimension; j++) {
                this.board[i][j] = Stone.Empty;
            }
        }
        this.firstPlayer = true;
    }

    private boolean isFieldEmpty(int row, int col) {

        return this.board[row][col] == Stone.Empty;
    }

    public boolean setStone(int row, int col) {

        // is there already a stone
        if (!isFieldEmpty(row, col))
            return false;

        Log.v("PeLo", "setStone ==> row = " + row + ", col = " + col);
        this.board[row][col] = (this.firstPlayer) ? Stone.X : Stone.O;

        this.firstPlayer = !this.firstPlayer;

        return true;
    }

    public boolean checkForEndOfGame() {

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
        int emptyStones = 0;
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                if (this.board[row][col] == Stone.Empty) {
                    emptyStones++;
                    break;
                }
            }
        }
        if (emptyStones == 0) {

            Toast.makeText(this.context, "Tic-Tac-Toe: Sorry - Game over ...",
                    Toast.LENGTH_SHORT).show();
        }

        return false;
    }

    Stone getStoneAt (int row, int col) {

        return this.board[row][col];
    }

    // private helper methods
}
