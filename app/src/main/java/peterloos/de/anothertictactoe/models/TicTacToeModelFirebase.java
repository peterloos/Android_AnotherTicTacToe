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
                String key = Integer.toString  ((row + 1) * Dimension + col);
                this.board.put(key, "Empty");
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
        return false;
    }

    // implementation of interface 'ValueEventListener'
    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        // This method is called once with the initial value and again
        // whenever data at this location is updated.
        // String value = dataSnapshot.getValue(String.class);

        dataSnapshot.exists();

        // Log.d(Globals.Tag, "Value is: " + dataSnapshot.toString());

        // GEHT !!!
//        for (DataSnapshot data : dataSnapshot.getChildren()) {
//
//            Log.d(Globals.Tag, "    Key:   " + data.getKey());
//            Log.d(Globals.Tag, "    Value: " + data.getValue());
//        }

        // Iterable<DataSnapshot> children = dataSnapshot.getChildren();

        for (DataSnapshot data : dataSnapshot.getChildren()) {

            Log.d(Globals.Tag, "    Key:   " + data.getKey());
//            Log.d(Globals.Tag, "    Value: " + data.getValue());

            for (DataSnapshot subData : data.getChildren()) {

                // Log.d(Globals.Tag, "    Key:   " + subData.getKey());
//                Log.d(Globals.Tag, "    Value: " + subData.getValue());

                if (subData.getKey().equals("col1")) {

                    // Log.d(Globals.Tag, "        Value at col1: " + subData.getValue());
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

    @Override
    public void onCancelled(DatabaseError error) {
        // Failed to read value
        // TODO: ERror handling
        Log.w(Globals.Tag, "Failed to read value.", error.toException());
    }
}
