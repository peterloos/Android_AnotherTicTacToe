package peterloos.de.anothertictactoe.models;

/**
 * Created by Peter on 17.03.2018.
 */

public class Status {

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
