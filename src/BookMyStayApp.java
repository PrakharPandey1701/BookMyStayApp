import java.util.*;


public class BookMyStayApp {

    public static void main(String[] args) {
        System.out.println("Welcome to Book My Stay!");
        System.out.println("Hotel Booking System v1.7\n");

        // Initialize inventory
        RoomInventory inventory = new RoomInventory();
        inventory.addRoomType("SingleRoom", 5);
        inventory.addRoomType("DoubleRoom", 3);
        inventory.addRoomType("SuiteRoom", 2);

        // Initialize booking service and booking history
        BookingHistory bookingHistory = new BookingHistory();
        BookingService bookingService = new BookingService(inventory, bookingHistory);

        // Create reservations and confirm them
        Reservation res1 = new Reservation("Alice", "SingleRoom");
        Reservation res2 = new Reservation("Bob", "SuiteRoom");
        Reservation res3 = new Reservation("Charlie", "DoubleRoom");

        bookingService.allocateRoom(res1);
        bookingService.allocateRoom(res2);
        bookingService.allocateRoom(res3);

        // Initialize reporting service
        BookingReportService reportService = new BookingReportService(bookingHistory);

        // Display booking history
        System.out.println("\nFull Booking History:");
        reportService.displayAllBookings();

        // Display summary report
        System.out.println("\nBooking Summary Report:");
        reportService.displaySummary();
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

    public String getReservationId() {
        return reservationId;
    }

    public String getGuestName() {
        return guestName;
    }

    public String getRoomType() {
        return roomType;
    }

    @Override
    public String toString() {
        return reservationId + " | " + guestName + " | " + roomType;
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
        if (inventory.getAvailability(roomType) <= 0) {
            System.out.println("No " + roomType + " available for " + reservation.getGuestName());
            return;
        }

        String roomId = generateUniqueRoomId(roomType);
        allocatedRooms.computeIfAbsent(roomType, k -> new HashSet<>()).add(roomId);
        inventory.decrementAvailability(roomType);

        System.out.println("Reservation confirmed for " + reservation.getGuestName() +
                " | Room: " + roomType + " | Room ID: " + roomId +
                " | Remaining: " + inventory.getAvailability(roomType));

        // Add to booking history
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

class BookingReportService {
    private BookingHistory bookingHistory;

    public BookingReportService(BookingHistory bookingHistory) {
        this.bookingHistory = bookingHistory;
    }

    public void displayAllBookings() {
        List<Reservation> reservations = bookingHistory.getAllReservations();
        if (reservations.isEmpty()) {
            System.out.println("No bookings have been made yet.");
            return;
        }
        for (Reservation r : reservations) {
            System.out.println(r);
        }
    }

    public void displaySummary() {
        List<Reservation> reservations = bookingHistory.getAllReservations();
        Map<String, Integer> summary = new HashMap<>();
        for (Reservation r : reservations) {
            summary.put(r.getRoomType(), summary.getOrDefault(r.getRoomType(), 0) + 1);
        }

        System.out.println("Room Type | Number of Bookings");
        for (String roomType : summary.keySet()) {
            System.out.println(roomType + " | " + summary.get(roomType));
        }
    }
}