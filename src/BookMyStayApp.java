import java.util.*;


public class BookMyStayApp {

    public static void main(String[] args) {
        System.out.println("Welcome to Book My Stay!");
        System.out.println("Hotel Booking System v1.9\n");

        // Initialize inventory
        RoomInventory inventory = new RoomInventory();
        inventory.addRoomType("SingleRoom", 5);
        inventory.addRoomType("DoubleRoom", 3);
        inventory.addRoomType("SuiteRoom", 2);

        // Initialize booking history and service
        BookingHistory bookingHistory = new BookingHistory();
        BookingService bookingService = new BookingService(inventory, bookingHistory);

        // Initialize cancellation service
        CancellationService cancellationService = new CancellationService(bookingService, inventory, bookingHistory);

        // Create reservations
        Reservation res1 = new Reservation("Alice", "SingleRoom");
        Reservation res2 = new Reservation("Bob", "SuiteRoom");
        Reservation res3 = new Reservation("Charlie", "DoubleRoom");

        // Allocate rooms
        bookingService.allocateRoom(res1);
        bookingService.allocateRoom(res2);
        bookingService.allocateRoom(res3);

        // Cancel one reservation
        System.out.println("\nAttempting cancellation for Bob's reservation:");
        cancellationService.cancelReservation(res2.getReservationId());

        // Display current booking history
        System.out.println("\nCurrent Booking History after cancellation:");
        for (Reservation r : bookingHistory.getAllReservations()) {
            System.out.println(r);
        }

        // Display inventory
        System.out.println("\nCurrent Inventory:");
        for (String roomType : inventory.getRoomTypes()) {
            System.out.println(roomType + " | Available: " + inventory.getAvailability(roomType));
        }
    }
}

/**
 * Reservation represents a confirmed room booking.
 */
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

/**
 * RoomInventory tracks availability for each room type.
 */
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
        }
        return false;
    }

    public void incrementAvailability(String roomType) {
        roomAvailability.put(roomType, getAvailability(roomType) + 1);
    }

    public Set<String> getRoomTypes() {
        return roomAvailability.keySet();
    }
}

/**
 * BookingHistory maintains confirmed reservations in order.
 */
class BookingHistory {
    private List<Reservation> history;

    public BookingHistory() {
        history = new ArrayList<>();
    }

    public void addReservation(Reservation reservation) {
        history.add(reservation);
    }

    public void removeReservation(String reservationId) {
        history.removeIf(r -> r.getReservationId().equals(reservationId));
    }

    public Reservation getReservationById(String reservationId) {
        for (Reservation r : history) {
            if (r.getReservationId().equals(reservationId)) return r;
        }
        return null;
    }

    public List<Reservation> getAllReservations() {
        return new ArrayList<>(history); // defensive copy
    }
}

/**
 * BookingService handles allocation and updates booking history.
 */
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
        if (!inventory.decrementAvailability(roomType)) {
            System.out.println("No availability for " + roomType + " for " + reservation.getGuestName());
            return;
        }

        String roomId = generateUniqueRoomId(roomType);
        allocatedRooms.computeIfAbsent(roomType, k -> new HashSet<>()).add(roomId);

        System.out.println("Reservation confirmed for " + reservation.getGuestName() +
                " | Room: " + roomType + " | Room ID: " + roomId +
                " | Remaining: " + inventory.getAvailability(roomType));

        bookingHistory.addReservation(reservation);
    }

    public String releaseRoom(Reservation reservation) {
        String roomType = reservation.getRoomType();
        Set<String> allocated = allocatedRooms.getOrDefault(roomType, new HashSet<>());
        if (!allocated.isEmpty()) {
            // Rollback using LIFO approach
            String roomId = allocated.stream().reduce((first, second) -> second).orElse(null);
            allocated.remove(roomId);
            inventory.incrementAvailability(roomType);
            return roomId;
        }
        return null;
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

/**
 * CancellationService handles reservation cancellations safely.
 */
class CancellationService {
    private BookingService bookingService;
    private RoomInventory inventory;
    private BookingHistory bookingHistory;

    public CancellationService(BookingService bookingService, RoomInventory inventory, BookingHistory bookingHistory) {
        this.bookingService = bookingService;
        this.inventory = inventory;
        this.bookingHistory = bookingHistory;
    }

    public void cancelReservation(String reservationId) {
        Reservation reservation = bookingHistory.getReservationById(reservationId);
        if (reservation == null) {
            System.out.println("Cancellation failed: Reservation ID " + reservationId + " not found.");
            return;
        }

        // Release room and update inventory
        String releasedRoomId = bookingService.releaseRoom(reservation);
        if (releasedRoomId != null) {
            bookingHistory.removeReservation(reservationId);
            System.out.println("Cancellation successful for " + reservation.getGuestName() +
                    " | Released Room ID: " + releasedRoomId);
        }
    }
}