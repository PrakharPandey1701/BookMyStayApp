import java.util.HashMap;
import java.util.Map;


public class BookMyStayApp {

    public static void main(String[] args) {
        System.out.println("Welcome to Book My Stay!");
        System.out.println("Hotel Booking System v1.2\n");

        // Initialize centralized inventory
        RoomInventory inventory = new RoomInventory();
        inventory.addRoomType("SingleRoom", 5);
        inventory.addRoomType("DoubleRoom", 3);
        inventory.addRoomType("SuiteRoom", 2);

        // Create room objects
        Room singleRoom = new SingleRoom();
        Room doubleRoom = new DoubleRoom();
        Room suiteRoom = new SuiteRoom();

        // Display room details along with current availability
        System.out.println(singleRoom.getRoomDetails() + " | Available: " + inventory.getAvailability("SingleRoom"));
        System.out.println(doubleRoom.getRoomDetails() + " | Available: " + inventory.getAvailability("DoubleRoom"));
        System.out.println(suiteRoom.getRoomDetails() + " | Available: " + inventory.getAvailability("SuiteRoom"));

        // Example: Book a room (update inventory)
        System.out.println("\nBooking a SingleRoom...");
        inventory.bookRoom("SingleRoom");
        System.out.println("SingleRoom availability after booking: " + inventory.getAvailability("SingleRoom"));

        System.out.println("\nApplication execution completed.");
    }
}


abstract class Room {
    protected int beds;
    protected int size; // in sq. ft.
    protected double price; // per night


    public String getRoomDetails() {
        return this.getClass().getSimpleName() +
                " [Beds: " + beds + ", Size: " + size + " sq.ft, Price: $" + price + "]";
    }
}

class SingleRoom extends Room {
    public SingleRoom() {
        this.beds = 1;
        this.size = 150;
        this.price = 50.0;
    }
}

class DoubleRoom extends Room {
    public DoubleRoom() {
        this.beds = 2;
        this.size = 250;
        this.price = 80.0;
    }
}

class SuiteRoom extends Room {
    public SuiteRoom() {
        this.beds = 3;
        this.size = 500;
        this.price = 150.0;
    }
}


class RoomInventory {
    private Map<String, Integer> roomAvailability;

    public RoomInventory() {
        roomAvailability = new HashMap<>();
    }


    public void addRoomType(String roomType, int count) {
        roomAvailability.put(roomType, count);
    }


    public int getAvailability(String roomType) {
        return roomAvailability.getOrDefault(roomType, 0);
    }


    public boolean bookRoom(String roomType) {
        int available = roomAvailability.getOrDefault(roomType, 0);
        if (available > 0) {
            roomAvailability.put(roomType, available - 1);
            return true;
        } else {
            System.out.println("No " + roomType + " available to book!");
            return false;
        }
    }


    public void cancelBooking(String roomType) {
        int available = roomAvailability.getOrDefault(roomType, 0);
        roomAvailability.put(roomType, available + 1);
    }
}