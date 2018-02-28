package peterloos.de.anothertictactoe.interfaces;

import peterloos.de.anothertictactoe.models.GameStone;

/**
 * Created by loospete on 07.02.2018.
 */

public interface OnPlayersConfigurationChangedListener {

    void playersActivityStateChanged(int whichPlayer, boolean playersState);
    void clearPlayersStateChanged();
    void currentPlayersNameChanged (String name);
    void otherPlayersNameChanged (String name);
    void scoreChanged (int score, boolean atLeftSide);
}
