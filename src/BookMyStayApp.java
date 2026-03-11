import java.util.*;

public class BookMyStayApp {

    public static void main(String[] args) {
        System.out.println("Welcome to Book My Stay!");
        System.out.println("Hotel Booking System v1.8\n");

        // Initialize inventory
        RoomInventory inventory = new RoomInventory();
        inventory.addRoomType("SingleRoom", 5);
        inventory.addRoomType("DoubleRoom", 3);
        inventory.addRoomType("SuiteRoom", 2);

        // Initialize booking history and service
        BookingHistory bookingHistory = new BookingHistory();
        BookingService bookingService = new BookingService(inventory, bookingHistory);
        BookingValidator validator = new BookingValidator(inventory);

        // Example bookings
        List<Reservation> reservations = Arrays.asList(
                new Reservation("Alice", "SingleRoom"),
                new Reservation("Bob", "SuiteRoom"),
                new Reservation("Charlie", "DoubleRoom"),
                new Reservation("Diana", "InvalidRoom") // Invalid input
        );

        // Process bookings with validation and error handling
        for (Reservation r : reservations) {
            try {
                validator.validateReservation(r); // validate input
                bookingService.allocateRoom(r);
            } catch (InvalidBookingException e) {
                System.out.println("Booking failed for " + r.getGuestName() + ": " + e.getMessage());
            }
        }

        // Display booking history
        System.out.println("\nConfirmed Bookings:");
        for (Reservation r : bookingHistory.getAllReservations()) {
            System.out.println(r);
        }
    }
}


class Reservation {
    private static int counter = 1;
    private String reservationId;
    private String guestName;
    private String roomType;

    public Reservation(String guestName, String roomType) {
        this.guestName = guestName;
        this.roomType = roomType;
        this.reservationId = "RES-" + counter++;
    }

    public String getReservationId() { return reservationId; }
    public String getGuestName() { return guestName; }
    public String getRoomType() { return roomType; }

    @Override
    public String toString() {
        return reservationId + " | " + guestName + " | " + roomType;
    }
}


class BookingValidator {
    private RoomInventory inventory;

    public BookingValidator(RoomInventory inventory) {
        this.inventory = inventory;
    }


    public void validateReservation(Reservation r) throws InvalidBookingException {
        if (r.getGuestName() == null || r.getGuestName().isEmpty()) {
            throw new InvalidBookingException("Guest name cannot be empty.");
        }
        if (!inventory.hasRoomType(r.getRoomType())) {
            throw new InvalidBookingException("Invalid room type: " + r.getRoomType());
        }
        if (inventory.getAvailability(r.getRoomType()) <= 0) {
            throw new InvalidBookingException("No availability for room type: " + r.getRoomType());
        }
    }
}


class InvalidBookingException extends Exception {
    public InvalidBookingException(String message) {
        super(message);
    }
}


class BookingService {
    private RoomInventory inventory;
    private Map<String, Set<String>> allocatedRooms;
    private BookingHistory bookingHistory;

    public BookingService(RoomInventory inventory, BookingHistory bookingHistory) {
        this.inventory = inventory;
        this.bookingHistory = bookingHistory;
        allocatedRooms = new HashMap<>();
    }

    public void allocateRoom(Reservation reservation) {
        String roomType = reservation.getRoomType();
        String roomId = generateUniqueRoomId(roomType);
        allocatedRooms.computeIfAbsent(roomType, k -> new HashSet<>()).add(roomId);
        inventory.decrementAvailability(roomType);
        System.out.println("Reservation confirmed for " + reservation.getGuestName() +
                " | Room: " + roomType + " | Room ID: " + roomId +
                " | Remaining: " + inventory.getAvailability(roomType));

        // Add to history
        bookingHistory.addReservation(reservation);
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


class RoomInventory {
    private Map<String, Integer> roomAvailability;

    public RoomInventory() {
        roomAvailability = new HashMap<>();
    }

    public void addRoomType(String roomType, int count) {
        roomAvailability.put(roomType, count);
    }

    public boolean hasRoomType(String roomType) {
        return roomAvailability.containsKey(roomType);
    }

    public int getAvailability(String roomType) {
        return roomAvailability.getOrDefault(roomType, 0);
    }

    public boolean decrementAvailability(String roomType) {
        int available = getAvailability(roomType);
        if (available > 0) {
            roomAvailability.put(roomType, available - 1);
            return true;
        }
        return false;
    }
}


class BookingHistory {
    private List<Reservation> history;

    public BookingHistory() {
        history = new ArrayList<>();
    }

    public void addReservation(Reservation reservation) {
        history.add(reservation);
    }

    public List<Reservation> getAllReservations() {
        return new ArrayList<>(history); // defensive copy
    }
}