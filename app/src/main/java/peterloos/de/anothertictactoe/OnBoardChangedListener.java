package peterloos.de.anothertictactoe;

/**
 * Created by loospete on 28.01.2018.
 */

public interface OnBoardChangedListener {

    // TODO: DA GINGE AUCH EINE EINZELNE METHODE UPDATE
    // TODO: WARTE NOCH, BIS FIREBASE DRIN IST !!!
    // TODO: OnBoardUpdateListener müsste dann das Interface heißen
    void clearBoard();
    void stoneChangedAt (Stone stone, int row, int col);
}