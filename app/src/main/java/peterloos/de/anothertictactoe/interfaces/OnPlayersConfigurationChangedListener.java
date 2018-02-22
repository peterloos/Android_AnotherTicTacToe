package peterloos.de.anothertictactoe.interfaces;

/**
 * Created by loospete on 07.02.2018.
 */

public interface OnPlayersConfigurationChangedListener {

    void playersNamesChanged(String firstPlayer, String secondPlayer);
    void playersActivityStateChanged(boolean firstPlayerIsActive, boolean secondPlayerIsActive);

    void currentPlayersNameChanged (String name);
    void anotherPlayersNameChanged (String name);
}
