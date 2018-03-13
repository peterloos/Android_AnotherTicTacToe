package peterloos.de.anothertictactoe.interfaces;

import peterloos.de.anothertictactoe.models.GameStone;

/**
 * Created by PeLo on 28.01.2018.
 */

public interface OnBoardChangedListener {

    // TODO: DA GINGE AUCH EINE EINZELNE METHODE UPDATE
    // TODO: WARTE NOCH, BIS FIREBASE DRIN IST !!!
    // TODO: OnBoardUpdateListener müsste dann das Interface heißen
    void clearBoard();
    void stoneChangedAt (int row, int col, GameStone stone);
}
