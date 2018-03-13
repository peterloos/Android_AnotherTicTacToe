package peterloos.de.anothertictactoe.models;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.DatabaseReference.CompletionListener;
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
 * Created by PeLo on 28.01.2018.
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

class Player {

    private String name;
    private long creationDate;
    private String key;
    private String stone;
    private int score;

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
        return this.creationDate;
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

    public String getStone() {
        return stone;
    }

    public void setStone(String stone) {
        this.stone = stone;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getScore() {
        return this.score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return "Name: " + this.name;
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
        return this.ticketNumber;
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
        this.id = "";
        this.parameter1 = "";
        this.parameter2 = "";
    }

    // getter/setter
    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParameter1() {
        return this.parameter1;
    }

    public void setParameter1(String parameter) {
        this.parameter1 = parameter;
    }

    public String getParameter2() {
        return this.parameter2;
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

    // cloud states
    private final String GameInit = "";
    private final String GameIdle = "GameIdle";
    private final String GameActive = "GameActive";
    private final String GameOver = "GameOver";

    // game commands
    private final String GameCommandClear = "clear";
    private final String GameCommandStart = "start";

    // Firebase utils
    private FirebaseDatabase database;
    private DatabaseReference refPlayers;
    private DatabaseReference refBoard;
    private DatabaseReference refCommand;
    private DatabaseReference refStatus;
    private DatabaseReference refTicket;

    // general member data
    private Context context;

    // game utils
    private HashMap<String, String> board;
    private AppState appState;
    private GameStone stone;

    // players utils
    private String currentPlayer;
    private String otherPlayer;
    private String currentPlayerKey;

    // listeners
    private OnBoardChangedListener boardListener;
    private OnPlayersConfigurationChangedListener playersListener;

    // c'tor
    public TicTacToeModelFirebase(Context context) {

        this.context = context;

        this.appState = AppState.Idle;
        this.board = new HashMap<>();

        // init access to database
        this.database = FirebaseDatabase.getInstance();
        this.refPlayers = database.getReference("players");
        this.refBoard = database.getReference("board");
        this.refCommand = this.database.getReference("control/command");
        this.refStatus = this.database.getReference("control/status");
        this.refTicket = this.database.getReference("control/ticket");

        this.refPlayers.addChildEventListener(this.childEventListener);
        this.refBoard.addValueEventListener(this.boardValueEventListener);
        this.refStatus.addValueEventListener(this.controlValueEventListener);

        this.currentPlayer = "";
        this.currentPlayerKey = "";
        this.otherPlayer = "";

        this.stone = GameStone.Empty;

        this.initializeBoardInternal();
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
        if (this.currentPlayer.equals("")) {

            this.tryEnterRoom(name);
        }
    }

    @Override
    public void start() {

        // trigger 'start' command in cloud
        this.emitComand(GameCommandStart);
    }

    @Override
    public void restart() {

        // trigger 'clear' command in cloud
        this.emitComand(GameCommandClear);
    }

    @Override
    public void exit() {

        this.emitComand(GameCommandClear);   // send "clear" command to firebase cloud
        this.deleteAllPlayers();   // remove all players
        this.resetTicketNumber();  // reset ticket number to zero
        this.clearStatus();        // clear game status
    }

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

    private ValueEventListener controlValueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

            TicTacToeModelFirebase.this.evaluateStatusSnapshot(dataSnapshot);
        }

        @Override
        public void onCancelled(DatabaseError error) {
            Log.e(Globals.Tag, "Failed to read value.", error.toException());
        }
    };

    // private helper methods
    private void evaluateStatusSnapshot(DataSnapshot dataSnapshot) {

        if (!dataSnapshot.exists()) {
            return;
        }

        Status status = dataSnapshot.getValue(Status.class);
        String msg = String.format("evaluateStatusSnapshot  ==> Status: %s", status.toString());
        Log.v(Globals.Tag, msg);

        switch (status.getId()) {

            case GameInit:
                Log.v(Globals.Tag, "No game state yet set - no error");
                break;

            case GameIdle:
                Log.v(Globals.Tag, "Game state reset to idle");
                break;

            case GameActive:

                // check for key of next player
                if (this.currentPlayerKey.equals("") || status.getParameter1().equals("")) {

                    Log.v(Globals.Tag, "Internal ERROR: Unexpected Game State ===> " + status.toString());
                    break;
                }

                // look at key of next player
                if (this.currentPlayerKey.equals(status.getParameter1())) {

                    String s = String.format("Player with key %s should *play* now", status.getParameter1());
                    Log.v(Globals.Tag, s);

                    this.appState = AppState.Active;
                    if (this.playersListener != null) {

                        this.playersListener.playersActivityStateChanged(0, true);
                    }
                }
                else {

                    String s = String.format("Player with key %s should *wait* now", status.getParameter1());
                    Log.v(Globals.Tag, s);

                    this.appState = AppState.Passive;
                    if (this.playersListener != null) {

                        this.playersListener.playersActivityStateChanged(1, false);
                    }
                }
                break;

            case GameOver:

                // check key of player, who won the game
                if (this.currentPlayerKey.equals("")) {

                    Log.v(Globals.Tag, "Internal ERROR: Unexpected Game State ===> " + status.toString());
                    break;
                }

                if (! status.getParameter1().equals("")) {

                    // game is over, the first parameter contains the key of the winner
                    Log.v(Globals.Tag, "GameOver => " + status.toString());
                    int score = Integer.valueOf(status.getParameter2());

                    // place a toast for both winner and loser
                    if (this.currentPlayerKey.equals(status.getParameter1())) {

                        String toast = String.format("Yeaah %s you're the winner!", this.currentPlayer);
                        Toast.makeText(this.context, toast, Toast.LENGTH_SHORT).show();

                        if (this.playersListener != null) {

                            this.playersListener.scoreChanged(score, true);
                        }
                    }
                    else {

                        String toast = String.format("Sorry %s you've lost the game!", this.currentPlayer);
                        Toast.makeText(this.context, toast, Toast.LENGTH_SHORT).show();

                        if (this.playersListener != null) {

                            this.playersListener.scoreChanged(score, false);
                        }
                    }

                    if (TicTacToeModelFirebase.this.playersListener != null) {

                        this.playersListener.clearPlayersStateChanged();
                    }
                }
                else {

                    // game is over, first parameter is empty: game ended with a draw
                    String toast = String.format("Game over --- it's a draw !!!");
                    Toast.makeText(this.context, toast, Toast.LENGTH_SHORT).show();
                }

                this.appState = AppState.Idle;
                break;

            default:

                String s = String.format("Internal ERROR => Status: %s", status.getId());
                Log.v(Globals.Tag, s);
                break;
        }
    }

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
        if (this.appState == AppState.Idle || this.appState == AppState.PendingForNextCloudState)
            return false;

        // it's not your turn
        if (this.appState == AppState.Passive) {

            String msg = String.format("It's %s's turn!", this.otherPlayer);
            Toast.makeText(this.context, msg, Toast.LENGTH_SHORT).show();
            return false;
        }

        // is there already a stone, ignore call
        if (! this.isFieldEmpty(key))
            return false;

        // accepting stone - set view into 'passive' state ...
        this.appState = AppState.PendingForNextCloudState;

        // ...  and set stone on (remote) board
        this.setSingleStoneRemote(row, col, this.stone);

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

                        TicTacToeModelFirebase.this.addPlayer(nickname, ticketNumber);
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

    private void initializeBoardInternal() {

        // initialize internal hash map of stones
        for (int row = 1; row <= Dimension; row++) {
            for (int col = 1; col <= Dimension; col++) {
                String key = this.cellToKey(row, col);
                this.board.put(key, GameStone.Empty.toString());
            }
        }
    }

    // =============================================================================================

    /*
     *   firebase specific functions
     */

    private void emitComand(final String cmd) {
        this.refCommand.setValue(cmd, new CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                Log.v(Globals.Tag, "data base: control/command => " + cmd);
            }
        });
    }

    private void addPlayer(String name, int ticketNumber) {

        DatabaseReference playersRef = this.refPlayers.push();

        this.currentPlayer = name;
        this.currentPlayerKey = playersRef.getKey();

        Player player = new Player();
        player.setName(name);
        player.setKey(this.currentPlayerKey);

        this.stone = (ticketNumber == 1) ? GameStone.X : GameStone.O;
        player.setStone(this.stone.toString());

        player.setScore(0);
        playersRef.setValue(player);
    }

    private void deleteAllPlayers() {
        this.refPlayers.removeValue(new CompletionListener() {

            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                Log.v(Globals.Tag, "data base: players => null");
            }
        });
    }

    private void resetTicketNumber() {
        this.refTicket.child("ticketNumber").setValue(0, new CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                Log.v(Globals.Tag, "data base: control/ticket/ticketNumber => 0");
            }
        });
    }

    private void clearStatus() {
        Status empty = new Status ();
        this.refStatus.setValue(empty, new CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                Log.v(Globals.Tag,"data base: control/status => { id : \"\", par1 : \"\", par2 : \"\" } ");
            }
        });
    }

    // =============================================================================================

    private ChildEventListener childEventListener = new ChildEventListener() {

        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {

            // a new player has been added
            Player player = dataSnapshot.getValue(Player.class);
            Log.d(Globals.Tag, "onChildAdded: " + player.toString() + " [" + dataSnapshot.getKey() + "]");

            if (TicTacToeModelFirebase.this.playersListener != null) {

                if (TicTacToeModelFirebase.this.currentPlayer.equals(player.getName())) {

                    TicTacToeModelFirebase.this.playersListener.currentPlayersNameChanged(player.getName());
                } else {

                    // a second player must have entered the room
                    TicTacToeModelFirebase.this.otherPlayer = player.getName();
                    TicTacToeModelFirebase.this.playersListener.otherPlayersNameChanged(TicTacToeModelFirebase.this.otherPlayer);
                }
            }
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {}

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {

            // removing a player
            Player player = dataSnapshot.getValue(Player.class);
            Log.d(Globals.Tag, "onChildRemoved: " + player.toString() + " [" + dataSnapshot.getKey() + "]");

            if (TicTacToeModelFirebase.this.playersListener != null) {

                if (TicTacToeModelFirebase.this.currentPlayer.equals(player.getName())) {

                    TicTacToeModelFirebase.this.currentPlayer = "";
                    TicTacToeModelFirebase.this.currentPlayerKey = "";
                    TicTacToeModelFirebase.this.playersListener.currentPlayersNameChanged("");

                    TicTacToeModelFirebase.this.playersListener.scoreChanged(0, true);
                } else {

                    TicTacToeModelFirebase.this.otherPlayer = "";
                    TicTacToeModelFirebase.this.playersListener.otherPlayersNameChanged("");

                    TicTacToeModelFirebase.this.playersListener.scoreChanged(0, false);
                }
            }
        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {}

        @Override
        public void onCancelled(DatabaseError databaseError) {}
    };
}
