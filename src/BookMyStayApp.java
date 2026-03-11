import java.util.*;


public class BookMyStayApp {

    public static void main(String[] args) {
        System.out.println("Welcome to Book My Stay!");
        System.out.println("Hotel Booking System v1.5\n");

        // Initialize centralized inventory
        RoomInventory inventory = new RoomInventory();
        inventory.addRoomType("SingleRoom", 5);
        inventory.addRoomType("DoubleRoom", 3);
        inventory.addRoomType("SuiteRoom", 2);

        // Initialize booking request queue
        BookingRequestQueue bookingQueue = new BookingRequestQueue();
        bookingQueue.addRequest(new Reservation("Alice", "SingleRoom"));
        bookingQueue.addRequest(new Reservation("Bob", "SuiteRoom"));
        bookingQueue.addRequest(new Reservation("Charlie", "DoubleRoom"));
        bookingQueue.addRequest(new Reservation("Diana", "SingleRoom"));

        // Initialize allocation service
        BookingService bookingService = new BookingService(inventory);

        // Process queued booking requests
        System.out.println("\nProcessing Booking Requests:");
        while (!bookingQueue.isEmpty()) {
            Reservation reservation = bookingQueue.processNextRequest();
            bookingService.allocateRoom(reservation);
        }

        System.out.println("\nApplication execution completed.");
    }
}


class Reservation {
    private String guestName;
    private String roomType;

    public Reservation(String guestName, String roomType) {
        this.guestName = guestName;
        this.roomType = roomType;
    }

    public String getGuestName() {
        return guestName;
    }

    public String getRoomType() {
        return roomType;
    }
}


class BookingRequestQueue {
    private Queue<Reservation> requestQueue;

    public BookingRequestQueue() {
        requestQueue = new LinkedList<>();
    }

    public void addRequest(Reservation reservation) {
        requestQueue.add(reservation);
        System.out.println("Booking request received: " + reservation.getGuestName() + " (" + reservation.getRoomType() + ")");
    }

    public Reservation processNextRequest() {
        return requestQueue.poll();
    }

    public boolean isEmpty() {
        return requestQueue.isEmpty();
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

    public boolean decrementAvailability(String roomType) {
        int available = getAvailability(roomType);
        if (available > 0) {
            roomAvailability.put(roomType, available - 1);
            return true;
        } else {
            return false;
        }
    }
}


class BookingService {
    private RoomInventory inventory;
    private Map<String, Set<String>> allocatedRooms;

    public BookingService(RoomInventory inventory) {
        this.inventory = inventory;
        this.allocatedRooms = new HashMap<>();
    }


    public void allocateRoom(Reservation reservation) {
        String roomType = reservation.getRoomType();
        int available = inventory.getAvailability(roomType);

        if (available <= 0) {
            System.out.println("No " + roomType + " available for " + reservation.getGuestName() + ". Booking cannot be confirmed.");
            return;
        }

        // Generate unique room ID
        String roomId = generateUniqueRoomId(roomType);

        // Record allocation
        allocatedRooms.computeIfAbsent(roomType, k -> new HashSet<>()).add(roomId);

        // Decrement inventory
        inventory.decrementAvailability(roomType);

        // Confirm reservation
        System.out.println("Reservation confirmed for " + reservation.getGuestName() +
                " | Room Type: " + roomType + " | Room ID: " + roomId +
                " | Remaining: " + inventory.getAvailability(roomType));
    }


    private String generateUniqueRoomId(String roomType) {
        Set<String> allocated = allocatedRooms.getOrDefault(roomType, new HashSet<>());
        int id = 1;
        String roomId = roomType + "-" + id;
        while (allocated.contains(roomId)) {
            id++;
            roomId = roomType + "-" + id;
        }
        return roomId;
    }
}