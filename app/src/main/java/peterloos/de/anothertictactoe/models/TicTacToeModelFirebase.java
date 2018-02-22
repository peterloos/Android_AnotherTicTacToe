package peterloos.de.anothertictactoe.models;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import peterloos.de.anothertictactoe.Globals;
import peterloos.de.anothertictactoe.interfaces.ITicTacToe;
import peterloos.de.anothertictactoe.interfaces.OnBoardChangedListener;
import peterloos.de.anothertictactoe.interfaces.OnPlayersConfigurationChangedListener;

import static peterloos.de.anothertictactoe.Globals.Dimension;

/**
 * Created by loospete on 28.01.2018.
 */

class Cell {

    private String state;

    public Cell() {
        // default constructor required for Firebase
    }

    public Cell(String state) {
        this.state = state;
    }

    public String getState() {
        return this.state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return "State: ==> " + this.state;
    }
}

// firebase how to create server timestamp java

// https://stackoverflow.com/questions/36658833/firebase-servervalue-timestamp-in-java-data-models-objects

// https://www.programcreek.com/java-api-examples/index.php?api=com.firebase.client.ServerValue

class Player {

    private String name;
    private long creationDate;
    private String key;

    // c'tors
    public Player() {
        // default constructor required for Firebase
    }

    public Player(String name) {
        this.name = name;
    }

    // getter/setter
    public Map<String, String> getCreationDate() {
        return ServerValue.TIMESTAMP;
    }

    @Exclude
    public long getCreationDateLong() {
        return creationDate;
    }

    public void setCreationDate(long creationDate) {
        this.creationDate = creationDate;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Name: " + this.name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}

class Ticket {

    private int ticketNumber;

    // c'tors
    public Ticket() {
        // default constructor required for Firebase
    }

    // getter/setter
    public int getTicketNumber() {
        return ticketNumber;
    }

    public void setTicketNumber(int ticketNumber) {
        this.ticketNumber = ticketNumber;
    }

    @Override
    public String toString() {
        return "Ticket: Number = " + this.ticketNumber;
    }
}

class Status {

    private String id;
    private String parameter1;
    private String parameter2;

    // c'tors
    public Status() {
        // default constructor required for Firebase
    }

    // getter/setter
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParameter1() {
        return parameter1;
    }

    public void setParameter1(String parameter) {
        this.parameter1 = parameter;
    }

    public String getParameter2() {
        return parameter2;
    }

    public void setParameter2(String parameter) {
        this.parameter2 = parameter;
    }

    @Override
    public String toString() {
        return "Id: " + this.id + ", Parameter1: [" + this.parameter1 + "]" + ", Parameter2: [" + this.parameter2 + "]";
    }
}

public class TicTacToeModelFirebase implements ITicTacToe {

    private final String GameIdle = "GameIdle";
    private final String GameRoom1Player = "GameRoom1Player";
    private final String GameRoom2Players = "GameRoom2Players";
    private final String GameActivePlayer = "GameActivePlayer";
    private final String GameActive1stPlayer = "GameActive1stPlayer";
    private final String GameActive2ndPlayer = "GameActive2ndPlayer";
    private final String GameOver1stPlayerWon = "GameOver1stPlayerWon";
    private final String GameOver2ndPlayerWon = "GameOver2ndPlayerWon";

    // game commands
    private final String GameCommandClear = "clear";
    private final String GameCommandStart = "start";

    private final String StateGameIdle = "gameIdle";
    private final String StateGameActive = "gameActive";
    private final String StateGameOver = "gameOver";
    private final String StateGameRestarted = "gameRestarted";

    // Firebase utils
    private FirebaseDatabase database;
    private DatabaseReference refBoard;
    private DatabaseReference refPlayers;
    private DatabaseReference refCommand;
    private DatabaseReference refStatus;
    private DatabaseReference refTicket;

    // general member data
    private Context context;

    // game utils
    private HashMap<String, String> board;
    private GameState gameState;
    private GameStone stone;

    // players utils
    // TODO: Das sollte ein array von Objekten sein !!!!!!!!!!!!!!!
    private String[] playerNames;
    private String[] playerKeys;
    private long[] playerTimestamps;
    private String currentPlayer;
    private int numPlayers;
    private int[] playerScores;

    // listeners
    private OnBoardChangedListener boardListener;
    private OnPlayersConfigurationChangedListener playersListener;
    private ValueEventListener valueEventListener;

    // c'tor
    public TicTacToeModelFirebase(Context context) {

        this.context = context;

        this.gameState = GameState.Inactive;
        this.board = new HashMap<>();

        // TODO: Warum steht das nicht in initGame
        this.playerNames = new String[2];
        this.playerKeys = new String[2];
        this.playerNames[0] = "";
        this.playerNames[1] = "";
        this.currentPlayer = "";
        this.playerTimestamps = new long[2];
        this.playerScores = new int[2];
        this.numPlayers = 0;

        // init access to database
        this.database = FirebaseDatabase.getInstance();
        this.refBoard = database.getReference("board");
        this.refPlayers = database.getReference("players");
        this.refCommand = this.database.getReference("control/command");
        this.refStatus = this.database.getReference("control/status");
        this.refTicket = this.database.getReference("control/ticket");

        this.refBoard.addValueEventListener(this.boardValueEventListener);
        this.refStatus.addValueEventListener(this.controlValueEventListener);

        this.initializeBoardInternal();
        this.initializeBoardRemote();
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
    public void enterPlayer(String name) {

        // TODO: Wie kann man verhindern, dass dieser Button mehrfach gedrückt wird ?!?!?

        // TODO: NUR ZUM TESTEN --- Muss wieder weg
        // addPlayer (name);

        if (this.currentPlayer.equals("")) {

            this.tryEnterRoom(name);
        }
    }

    @Override
    public void leavePlayer() {

        if (!this.currentPlayer.equals("")) {

            this.refPlayers.child(this.playerKeys[0]).removeValue();
        }
    }

    @Override
    public void start() {

        // trigger start command in cloud
        this.setCommand(GameCommandStart);
    }

    @Override
    public void clear() {

        // now initialize remote database
        this.initializeBoardRemote();

//        // notify view(s)
//        if (this.boardListener != null) {
//            this.boardListener.clearBoard();
//        }
//        if (this.playersListener != null) {
//            this.playersListener.playersActivityStateChanged(false, false);
//        }
    }


    // TODO : ACHTUNG :  DIESE SEQAUENZ HABEN WIR ZWEI MAL !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

    @Override
    public void restart() {

        if (TicTacToeModelFirebase.this.playerTimestamps[0] < TicTacToeModelFirebase.this.playerTimestamps[1]) {

            TicTacToeModelFirebase.this.gameState = GameState.ActiveIsMe;
            TicTacToeModelFirebase.this.stone = GameStone.X;
        } else {

            TicTacToeModelFirebase.this.gameState = GameState.ActiveIsOther;
            TicTacToeModelFirebase.this.stone = GameStone.O;
        }

        // TODO TODO : DAS MUSS EINE ON ROUTINE WERDEN --- das bläht mir den Quellcode zu sehr auf !!!!!!!!!!!
        // fire notification
        if (TicTacToeModelFirebase.this.playersListener != null) {

            TicTacToeModelFirebase.this.playersListener.playersNamesChanged
                    (TicTacToeModelFirebase.this.playerNames[0],
                            TicTacToeModelFirebase.this.playerNames[1]);
        }
    }

//    private ChildEventListener childEventListener = new ChildEventListener() {
//
//        @Override
//        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//
//            // a new player has been added
//            Player player = dataSnapshot.getValue(Player.class);
//            String name = player.getName();
//
//            TicTacToeModelFirebase.this.numPlayers ++;
//
//            Log.d(Globals.Tag, "onChildAdded: " + player.toString() + " [" + dataSnapshot.getKey() + "]");
//
//            if (TicTacToeModelFirebase.this.currentPlayer.equals(name)) {
//
//                TicTacToeModelFirebase.this.playerNames[0] = name;
//                TicTacToeModelFirebase.this.playerKeys[0] = dataSnapshot.getKey();
//                TicTacToeModelFirebase.this.playerTimestamps[0] = player.getCreationDateLong();
//            } else {
//
//                TicTacToeModelFirebase.this.playerKeys[1] = dataSnapshot.getKey();
//                TicTacToeModelFirebase.this.playerNames[1] = name;
//                TicTacToeModelFirebase.this.playerTimestamps[1] = player.getCreationDateLong();
//            }
//
//            if (TicTacToeModelFirebase.this.numPlayers == 1) {
//
//                if (TicTacToeModelFirebase.this.playersListener != null) {
//
//                    // fire name
//                    TicTacToeModelFirebase.this.playersListener.playersNamesChanged
//                            (TicTacToeModelFirebase.this.playerNames[0],
//                                    TicTacToeModelFirebase.this.playerNames[1]);
//                }
//            }
//            else if (TicTacToeModelFirebase.this.numPlayers == 2) {
//
//                // kick start of game
//                TicTacToeModelFirebase.this.updateState(StateGameActive);
//            }
//        }
//
//        @Override
//        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//        }
//
//        @Override
//        public void onChildRemoved(DataSnapshot dataSnapshot) {
//
//            // removing a player
//            Player player = dataSnapshot.getValue(Player.class);
//            String name = player.getName();
//
//            Log.d(Globals.Tag, "onChildRemoved: " + name + " [" + dataSnapshot.getKey() + "]");
//
//            // update internal data structures
//            if (TicTacToeModelFirebase.this.currentPlayer.equals(name)) {
//
//                // this (current) player is removed
//                TicTacToeModelFirebase.this.currentPlayer = "";
//                TicTacToeModelFirebase.this.playerNames[0] = "";
//            } else {
//                // second player is removed
//                TicTacToeModelFirebase.this.playerNames[1] = "";
//            }
//
//            // fire notification
//            if (TicTacToeModelFirebase.this.playersListener != null) {
//
//                TicTacToeModelFirebase.this.playersListener.playersNamesChanged
//                        (TicTacToeModelFirebase.this.playerNames[0],
//                         TicTacToeModelFirebase.this.playerNames[1]);
//            }
//        }
//
//        @Override
//        public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//        }
//
//        @Override
//        public void onCancelled(DatabaseError databaseError) {
//
//            Log.w(Globals.Tag, "refPlayers:onCancelled", databaseError.toException());
//            Toast.makeText(TicTacToeModelFirebase.this.context, "Failed to load comments.",
//                    Toast.LENGTH_SHORT).show();
//        }
//    };

    private ValueEventListener boardValueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

            TicTacToeModelFirebase.this.evaluateBoardSnapshot(dataSnapshot);
        }

        @Override
        public void onCancelled(DatabaseError error) {
            Log.e(Globals.Tag, "Failed to read value.", error.toException());
        }
    };

//    private ValueEventListener stateValueEventListener = new ValueEventListener() {
//        @Override
//        public void onDataChange(DataSnapshot dataSnapshot) {
//
//            TicTacToeModelFirebase.this.evaluateStateSnapshot(dataSnapshot);
//        }
//
//        @Override
//        public void onCancelled(DatabaseError error) {
//            Log.e(Globals.Tag, "Failed to read value.", error.toException());
//        }
//    };

    private ValueEventListener controlValueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

            Toast.makeText(TicTacToeModelFirebase.this.context, "Yeahhhhhhhhhhhhhhhh", Toast.LENGTH_SHORT).show();
            TicTacToeModelFirebase.this.evaluateStatusSnapshot(dataSnapshot);

        }

        @Override
        public void onCancelled(DatabaseError error) {
            Log.e(Globals.Tag, "Failed to read value.", error.toException());
        }
    };

//    private final String GameIdle = "GameIdle";
//    private final String GameRoom1Player = "GameRoom1Player";
//    private final String GameRoom2Players = "GameRoom2Players";
//    private final String GameActivePlayer = "GameActivePlayer";
//    private final String GameActive1stPlayer = "GameActive1stPlayer";
//    private final String GameActive2ndPlayer = "GameActive2ndPlayer";
//    private final String GameOver1stPlayerWon = "GameOver1stPlayerWon";
//    private final String GameOver2ndPlayerWon = "GameOver2ndPlayerWon";

    // private helper methods
    private void evaluateStatusSnapshot(DataSnapshot dataSnapshot) {

        if (!dataSnapshot.exists()) {
            return;
        }

        Status status = dataSnapshot.getValue(Status.class);
        String msg = String.format("evaluateStateSnapshot  ==> State: %s", status.toString());
        Log.v(Globals.Tag, msg);

        switch (status.getId()) {
            case GameIdle:

                // TODO:
                // Hmmm, das würde sich für die Initialisierung anbieten
                break;

            case GameRoom1Player:
            case GameRoom2Players:

                if (TicTacToeModelFirebase.this.playersListener != null) {

                    if (this.currentPlayer.equals(status.getParameter2())) {
                        // the current user of the app has entered the room

                        String s = String.format("==> Status: %s -- currentPlayer: %s -- calling CURRENT !!!", status.getId(), status.getParameter2());
                        Log.v(Globals.Tag, s);

                        TicTacToeModelFirebase.this.playersListener.currentPlayersNameChanged(status.getParameter2());
                    } else {

                        String s = String.format("==> Status: %s -- currentPlayer: %s -- calling ANOTHER !!!", status.getId(), status.getParameter2());
                        Log.v(Globals.Tag, s);

                        // a second player must have entered the room
                        TicTacToeModelFirebase.this.playersListener.anotherPlayersNameChanged(status.getParameter2());
                    }
                }
                break;

            case GameActivePlayer:
                break;

            default:

                String s = String.format("NOT YET IMPLEMENTED  ==> Status: %s", status.getId());
                Log.v(Globals.Tag, s);
                break;
        }
    }

//    private void evaluateStateSnapshot(DataSnapshot dataSnapshot) {
//
//        if (!dataSnapshot.exists()) {
//            return;
//        }
//
//        State state = dataSnapshot.getValue(State.class);
//        String msg = String.format("evaluateStateSnapshot  ==> State: %s", state.toString());
//        Log.v(Globals.Tag, msg);
//
//        if (state.getStatus().equals(StateGameActive) || state.getStatus().equals(StateGameRestarted)) {
//
//            // decide, which player begins
//            if (this.playerTimestamps[0] < this.playerTimestamps[1]) {
//
//                this.gameState = GameState.ActiveIsMe;
//                this.stone = GameStone.X;
//            } else {
//
//                this.gameState = GameState.ActiveIsOther;
//                this.stone = GameStone.O;
//            }
//
//            // fire notification
//            if (this.playersListener != null) {
//
//                // fire names
//                this.playersListener.playersNamesChanged(this.playerNames[0], this.playerNames[1]);
//
//                if (this.gameState == GameState.ActiveIsMe) {
//
//                    this.playersListener.playersActivityStateChanged(true, false);
//                } else if (this.gameState == GameState.ActiveIsOther) {
//
//                    this.playersListener.playersActivityStateChanged(false, true);
//                }
//            }
//        } else if (state.getStatus().equals(StateGameOver)) {
//
//            // clear players background colors
//            if (this.playersListener != null) {
//
//                this.playersListener.playersActivityStateChanged(false, false);
//            }
//        }
//    }

    @Override
    public GameStone getStoneAt(int row, int col) {

        String key = this.cellToKey(row, col);
        String value = this.board.get(key);
        return GameStone.valueOf(value);
    }

    @Override
    public boolean setStone(int row, int col) {

        String key = this.cellToKey(row, col);

        // ignore this request - current game over or not initialized
        if (this.gameState == GameState.Inactive)
            return false;

        // it's not your turn
        if (this.gameState == GameState.ActiveIsOther) {

            String msg = String.format("It's %s's turn!", this.playerNames[1]);
            Toast.makeText(this.context, msg, Toast.LENGTH_SHORT).show();
            return false;
        }

        // is there already a stone, ignore call
        if (!isFieldEmpty(key))
            return false;

        // set stone on board
        Log.v("PeLo", "setStone ==> row = " + row + ", col = " + col);
        this.setSingleStoneRemote(row, col, stone);

        return true;
    }

    private void evaluateBoardSnapshot(DataSnapshot dataSnapshot) {

        if (!dataSnapshot.exists()) {
            return;
        }

        for (DataSnapshot data : dataSnapshot.getChildren()) {

            Log.d(Globals.Tag, "    Key:   " + data.getKey());
            for (DataSnapshot subData : data.getChildren()) {

                if (subData.getKey().equals("col1")) {

                    Cell cell = subData.getValue(Cell.class);
                    this.onCellChanged(data.getKey(), "col1", cell.getState());
                } else if (subData.getKey().equals("col2")) {

                    Cell cell = subData.getValue(Cell.class);
                    this.onCellChanged(data.getKey(), "col2", cell.getState());
                } else if (subData.getKey().equals("col3")) {

                    Cell cell = subData.getValue(Cell.class);
                    this.onCellChanged(data.getKey(), "col3", cell.getState());
                }
            }
        }
    }

    private void onCellChanged(String row, String col, String stone) {

        String msg = String.format("bin hier in onCellChanged: %s  %s mit Stein %s", row, col, stone);
        Log.v(Globals.Tag, msg);

        String key = this.cellToKey(row, col);
        String value = this.board.get(key);

        GameStone oldStone = GameStone.valueOf(value);
        GameStone newStone = GameStone.valueOf(stone);

        if (oldStone != newStone) {

            // enter new stone into hash map of model
            this.board.put(key, stone);

            // fire notification according to state of board
            if (this.boardListener != null) {

                this.boardListener.stoneChangedAt(
                        this.rowToInt(row),
                        this.colToInt(col),
                        GameStone.valueOf(stone));
            }

            if (this.gameState == GameState.ActiveIsMe || this.gameState == GameState.ActiveIsOther) {

                // check for end of game
                if (this.checkForEndOfGame(newStone)) {

                    String result = "";
                    if (this.gameState == GameState.ActiveIsMe) {

                        result = "Congratulations - " + this.playerNames[0] + "\n" + "You've won !!!";

                    } else if (this.gameState == GameState.ActiveIsOther) {

                        result = "Sorry - " + this.playerNames[0] + "\n" + "You just lost the game !!!";
                    }
                    Toast.makeText(this.context, result, Toast.LENGTH_SHORT).show();

                    this.gameState = GameState.Inactive;

                    this.initializeBoardInternal();

                    // TODO: Hmmm, ist dieser Aufruf von updateState noch relevant
                    // this.updateState(StateGameOver);
                } else {

                    // switch players state
                    if (this.gameState == GameState.ActiveIsMe) {

                        this.gameState = GameState.ActiveIsOther;
                    } else if (this.gameState == GameState.ActiveIsOther) {

                        this.gameState = GameState.ActiveIsMe;
                    }

                    // fire notification according to players state
                    if (this.playersListener != null) {

                        this.playersListener.playersActivityStateChanged(
                                (this.gameState == GameState.ActiveIsMe) ? true : false,
                                (this.gameState == GameState.ActiveIsMe) ? false : true);
                    }
                }
            }
        }
    }

    private String cellToKey(int row, int col) {

        // assertion: row and col are in the range 1..3
        return Integer.toString((row - 1) * Dimension + col);
    }

    private String cellToKey(String srow, String scol) {

        int row = this.rowToInt(srow);
        int col = this.colToInt(scol);
        return this.cellToKey(row, col);
    }

    private boolean isFieldEmpty(String key) {

        String value = this.board.get(key);
        return value.equals(GameStone.Empty.toString());
    }

    private int rowToInt(String row) {

        return row.charAt(3) - '0';
    }

    private int colToInt(String row) {

        return row.charAt(3) - '0';
    }

    private void setSingleStoneRemote(int r, int c, GameStone stone) {

        String row = "row" + r;
        String col = "col" + c;
        Cell cell = new Cell(stone.toString());
        this.refBoard.child(row).child(col).setValue(cell);
    }

    private void initializeBoardRemote() {

        Map<String, Object> board = new HashMap<>();

        Map<String, String> col1 = new HashMap<>();
        col1.put("state", "Empty");
        Map<String, String> col2 = new HashMap<>();
        col2.put("state", "Empty");
        Map<String, String> col3 = new HashMap<>();
        col3.put("state", "Empty");

        Map<String, Object> row1 = new HashMap<>();
        row1.put("col1", col1);
        row1.put("col2", col2);
        row1.put("col3", col3);

        Map<String, Object> row2 = new HashMap<>();
        row2.put("col1", col1);
        row2.put("col2", col2);
        row2.put("col3", col3);

        Map<String, Object> row3 = new HashMap<>();
        row3.put("col1", col1);
        row3.put("col2", col2);
        row3.put("col3", col3);

        board.put("row1", row1);
        board.put("row2", row2);
        board.put("row3", row3);

        this.refBoard.setValue(board);
    }

    private boolean checkForEndOfGame(GameStone stone) {

        /*
         * check for end of game, using a hash map based board :-)
         */

        // test columns
        for (int row = 1; row <= 3; row++) {

            GameStone stone1 = this.getStoneAt(row, 1);
            GameStone stone2 = this.getStoneAt(row, 2);
            GameStone stone3 = this.getStoneAt(row, 3);

            if (stone1 == stone && stone2 == stone && stone3 == stone)
                return true;
        }

        // test rows
        for (int col = 1; col <= 3; col++) {

            GameStone stone1 = this.getStoneAt(1, col);
            GameStone stone2 = this.getStoneAt(2, col);
            GameStone stone3 = this.getStoneAt(3, col);

            if (stone1 == stone && stone2 == stone && stone3 == stone)
                return true;
        }

        // test diagonals
        GameStone stone11 = this.getStoneAt(1, 1);
        GameStone stone22 = this.getStoneAt(2, 2);
        GameStone stone33 = this.getStoneAt(3, 3);

        if (stone11 == stone && stone22 == stone && stone33 == stone)
            return true;

        GameStone stone31 = this.getStoneAt(3, 1);
        GameStone stone13 = this.getStoneAt(1, 3);

        if (stone31 == stone && stone22 == stone && stone13 == stone)
            return true;

        // could be a draw - count empty stones
        int emptyStones = 0;
        for (int row = 1; row <= 3; row++) {
            for (int col = 1; col <= 3; col++) {
                GameStone s = this.getStoneAt(row, col);
                if (s == GameStone.Empty) {
                    emptyStones++;
                    break;
                }
            }
        }

        if (emptyStones == 0) {

            this.gameState = GameState.Inactive;
            Toast.makeText(this.context, "Game over - It's a draw!", Toast.LENGTH_SHORT).show();

            // fire notification according to players state
            if (this.playersListener != null) {

                this.playersListener.playersActivityStateChanged(false, false);
            }
        }

        return false;
    }

    public void tryEnterRoom(final String nickname) {

        DatabaseReference ref = this.refTicket;
        ref.runTransaction(new Transaction.Handler() {

            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Object o = mutableData.getValue(Ticket.class);
                if (o == null) {
                    return Transaction.success(mutableData);
                }

                Ticket ticket = mutableData.getValue(Ticket.class);
                String msg = String.format("doTransaction --> currentValue ==> %d", ticket.getTicketNumber());
                Log.v(Globals.Tag, msg);

                int ticketNumber = ticket.getTicketNumber();

                if (ticketNumber >= 2) {

                    return Transaction.abort();

                } else {
                    ticket.setTicketNumber(ticket.getTicketNumber() + 1);
                    mutableData.setValue(ticket);
                    return Transaction.success(mutableData);
                }
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean committed, DataSnapshot dataSnapshot) {

                // transaction completed
                if (committed) {

                    Ticket state = dataSnapshot.getValue(Ticket.class);
                    int ticketNumber = state.getTicketNumber();

                    if (ticketNumber == 1 || ticketNumber == 2) {

                        // let player enter into the room
                        String info = "Player " + nickname + " has entered!";
                        Toast.makeText(TicTacToeModelFirebase.this.context, info, Toast.LENGTH_SHORT).show();
                        Log.v(Globals.Tag, info);

                        TicTacToeModelFirebase.this.currentPlayer = nickname;
                        TicTacToeModelFirebase.this.addPlayer(nickname);
                    } else {

                        String info = "Sorry - There are still 2 players in the room!";
                        Toast.makeText(TicTacToeModelFirebase.this.context, info, Toast.LENGTH_SHORT).show();
                        Log.v(Globals.Tag, info);
                    }
                } else {

                    String info = "Sorry - There are still 2 players in the room!";
                    Toast.makeText(TicTacToeModelFirebase.this.context, info, Toast.LENGTH_SHORT).show();
                    Log.v(Globals.Tag, info);
                }
            }
        });
    }

    private void addPlayer(String name) {

        DatabaseReference playersRef = this.refPlayers.push();
        Player player = new Player();
        player.setName(name);
        player.setKey(playersRef.getKey());
        playersRef.setValue(player);
    }

    private void setCommand(String cmd) {

        this.refCommand.setValue(cmd);
    }

//    private void updateState(String state) {
//
//        this.refState.child("status").setValue(state);
//    }

    private void initializeBoardInternal() {

        // initialize internal hash map of stones
        for (int row = 1; row <= Dimension; row++) {
            for (int col = 1; col <= Dimension; col++) {
                String key = this.cellToKey(row, col);
                this.board.put(key, GameStone.Empty.toString());
            }
        }
    }
}
