
public class BookMyStayApp {

    public static void main(String[] args) {
        System.out.println("Welcome to Book My Stay!");
        System.out.println("Hotel Booking System v1.1\n");

        // Initialize room availability (static variables)
        int singleRoomAvailable = 5;
        int doubleRoomAvailable = 3;
        int suiteRoomAvailable = 2;

        // Create room objects
        Room singleRoom = new SingleRoom();
        Room doubleRoom = new DoubleRoom();
        Room suiteRoom = new SuiteRoom();

        // Display room details and availability
        System.out.println(singleRoom.getRoomDetails() + " | Available: " + singleRoomAvailable);
        System.out.println(doubleRoom.getRoomDetails() + " | Available: " + doubleRoomAvailable);
        System.out.println(suiteRoom.getRoomDetails() + " | Available: " + suiteRoomAvailable);

        System.out.println("\nApplication execution completed.");
    }
}

/**
 * Abstract class representing a generic hotel room.
 */
abstract class Room {
    protected int beds;
    protected int size; // in sq. ft.
    protected double price; // per night

    /**
     * Returns room details as a formatted string.
     */
    public String getRoomDetails() {
        return this.getClass().getSimpleName() +
                " [Beds: " + beds + ", Size: " + size + " sq.ft, Price: $" + price + "]";
    }
}

/**
 * Single room with specific attributes.
 */
class SingleRoom extends Room {
    public SingleRoom() {
        this.beds = 1;
        this.size = 150;
        this.price = 50.0;
    }
}

/**
 * Double room with specific attributes.
 */
class DoubleRoom extends Room {
    public DoubleRoom() {
        this.beds = 2;
        this.size = 250;
        this.price = 80.0;
    }
}

/**
 * Suite room with specific attributes.
 */
class SuiteRoom extends Room {
    public SuiteRoom() {
        this.beds = 3;
        this.size = 500;
        this.price = 150.0;
    }
}