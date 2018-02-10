package peterloos.de.anothertictactoe.models;

/**
 * Created by loospete on 29.01.2018.
 */

public enum GameState {

    // TODO: Active wird derzeit nur im OFFLINE verwendet -
    // TODO: Vielleicht kann man das auch ändern ?!?!?!
    Active, Inactive, ActiveIsMe, ActiveIsOther
}

// TODO 2:  Noch 2 STates einführen: ActiveIsMeAndClickedOnStone
// Soll heißen: Bis von der Cloud die Bestätigng kommt, sollte das Brett inaktiv sein
// Ich muss aber wissen wer gecklickt hat ...