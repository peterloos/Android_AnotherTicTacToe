package peterloos.de.anothertictactoe.models;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.ServerValue;

import java.util.Map;

/**
 * Created by Peter on 17.03.2018.
 */

public class Player {

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
