package peterloos.de.anothertictactoe.models;

import android.content.Context;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import peterloos.de.anothertictactoe.Globals;
import peterloos.de.anothertictactoe.interfaces.ITicTacToe;
import peterloos.de.anothertictactoe.interfaces.OnBoardChangedListener;

import static peterloos.de.anothertictactoe.Globals.Dimension;

/**
 * Created by loospete on 28.01.2018.
 */

class Cell
{
    private String state;

    public Cell() {
        // default constructor required for Firebase
    }

    public Cell(String state) {
        this.state = state;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return "State: ==> " + this.state;
    }
}

public class TicTacToeModelFirebase implements ITicTacToe, ValueEventListener {

    // Firebase utils
    private FirebaseDatabase database;
    private DatabaseReference boardRef;

    // member data
    private Context context;
    private HashMap<String, String> board;
    private boolean firstPlayer;
    private GameState gameState;
    private OnBoardChangedListener listener;

    // c'tor
    public TicTacToeModelFirebase(Context context) {

        this.context = context;
        this.board = new HashMap<String, String>();
        this.initGame();

        // init access to database
        this.database = FirebaseDatabase.getInstance();
        this.boardRef = database.getReference("board");
        this.boardRef.addValueEventListener(this);
    }

    // public interface
    @Override
    public void initGame() {

        for (int row = 0; row < Dimension; row++) {
            for (int col = 0; col < Dimension; col++) {
                String key = this.cellToKey(row, col);
                this.board.put(key, GameStone.Empty.toString());
            }
        }

        this.firstPlayer = true;
        this.gameState = GameState.Active;

        if (this.listener != null) {
            this.listener.clearBoard();
        }
    }

    // implementation of interface 'ITicTacToe'
    @Override
    public void setOnBoardChangedListener(OnBoardChangedListener listener) {

    }

    @Override
    public GameStone getStoneAt(int row, int col) {
        return null;
    }

    @Override
    public boolean setStone(int row, int col) {

        String key = this.cellToKey(row, col);

        // ignore this request - current game over or not initialized
        if (this.gameState == GameState.Inactive)
            return false;

        // is there already a stone, ignore call
        if (!isFieldEmpty(key))
            return false;

        // set stone on board
        Log.v("PeLo", "setStone ==> row = " + row + ", col = " + col);
        GameStone stone = (this.firstPlayer) ? GameStone.X : GameStone.O;
        this.setStoneInternal(row, col, stone);
        this.firstPlayer = !this.firstPlayer;

        // ??????????????????????????????????????????????????
//        if (this.listener != null) {
//            this.listener.stoneChangedAt(stone, row, col);
//        }

        return true;
    }

    // implementation of interface 'ValueEventListener'
    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {

        if (dataSnapshot.exists()) {

            for (DataSnapshot data : dataSnapshot.getChildren()) {

                Log.d(Globals.Tag, "    Key:   " + data.getKey());
                for (DataSnapshot subData : data.getChildren()) {

                    if (subData.getKey().equals("col1")) {

                        Cell cell = subData.getValue(Cell.class);
                        Log.d(Globals.Tag, "        Value at col1: " + cell.toString());
                    }
                    else if (subData.getKey().equals("col2")) {

                        Cell cell = subData.getValue(Cell.class);
                        Log.d(Globals.Tag, "        Value at col2: " + cell.toString());
                    }
                    else if (subData.getKey().equals("col3")) {

                        Cell cell = subData.getValue(Cell.class);
                        Log.d(Globals.Tag, "        Value at col3: " + cell.toString());
                    }
                }
            }
        }
    }

    @Override
    public void onCancelled(DatabaseError error) {
        // Failed to read value
        // TODO: ERror handling
        Log.w(Globals.Tag, "Failed to read value.", error.toException());
    }

    // private helper methods
    private String cellToKey (int row, int col) {

        return Integer.toString (row * Dimension + (col+1));
    }

    private boolean isFieldEmpty(String key) {

        String value = this.board.get(key);
        return value.equals(GameStone.Empty.toString());
    }

    public void setStoneInternal(int r, int c, GameStone stone) {

        String row = "row" + (r+1);
        String col = "col" + (c+1);
        Cell cell = new Cell (stone.toString());
        this.boardRef.child(row).child(col).setValue(cell);
    }
}
