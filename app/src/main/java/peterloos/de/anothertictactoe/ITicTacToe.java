package peterloos.de.anothertictactoe;

/**
 * Created by loospete on 27.01.2018.
 */

// game specific constants - symbolic notation
enum Stone { Empty, X, O }

public interface ITicTacToe {

    void setOnBoardChangedListener (OnBoardChangedListener listener);

    void initGame();
    Stone getStoneAt (int row, int col);
    boolean setStone(int row, int col);
}
