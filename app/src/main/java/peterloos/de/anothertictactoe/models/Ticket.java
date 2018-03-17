package peterloos.de.anothertictactoe.models;

/**
 * Created by Peter on 17.03.2018.
 */

public class Ticket {

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
