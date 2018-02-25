package peterloos.de.anothertictactoe.interfaces;

/**
 * Created by loospete on 07.02.2018.
 */

public interface OnPlayersConfigurationChangedListener {

    void playersActivityStateChanged(int whichPlayer, boolean playersState);
    void currentPlayersNameChanged (String name);
    void otherPlayersNameChanged (String name);
}
