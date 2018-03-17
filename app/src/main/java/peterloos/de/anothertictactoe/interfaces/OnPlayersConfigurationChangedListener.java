package peterloos.de.anothertictactoe.interfaces;

/**
 * Created by PeLo on 07.02.2018.
 */

public interface OnPlayersConfigurationChangedListener {

    void playersActivityStateChanged(int whichPlayer, boolean playersState);
    void clearPlayersState();
    void currentPlayersNameChanged (String name);
    void otherPlayersNameChanged (String name);
    void scoreChanged (int score, boolean atLeftSide);
}
