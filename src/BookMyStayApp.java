import java.util.*;

public class BookMyStayApp {

    public static void main(String[] args) {
        System.out.println("Welcome to Book My Stay!");
        System.out.println("Hotel Booking System v1.6\n");

        // Initialize inventory
        RoomInventory inventory = new RoomInventory();
        inventory.addRoomType("SingleRoom", 5);
        inventory.addRoomType("DoubleRoom", 3);
        inventory.addRoomType("SuiteRoom", 2);

        // Initialize booking service and allocate some rooms
        BookingService bookingService = new BookingService(inventory);
        Reservation res1 = new Reservation("Alice", "SingleRoom");
        Reservation res2 = new Reservation("Bob", "SuiteRoom");
        bookingService.allocateRoom(res1);
        bookingService.allocateRoom(res2);

        // Initialize add-on service manager
        AddOnServiceManager addOnManager = new AddOnServiceManager();

        // Guests select optional services
        addOnManager.addService(res1.getReservationId(), new AddOnService("Breakfast", 10.0));
        addOnManager.addService(res1.getReservationId(), new AddOnService("Airport Pickup", 20.0));
        addOnManager.addService(res2.getReservationId(), new AddOnService("Spa Package", 50.0));

        // Display services for each reservation
        System.out.println("\nSelected Add-On Services:");
        addOnManager.displayServices(res1.getReservationId());
        addOnManager.displayServices(res2.getReservationId());
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

    public String getGuestName() {
        return guestName;
    }

    public String getRoomType() {
        return roomType;
    }

    public String getReservationId() {
        return reservationId;
    }
}


class BookingService {
    private RoomInventory inventory;
    private Map<String, Set<String>> allocatedRooms;

    public BookingService(RoomInventory inventory) {
        this.inventory = inventory;
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


class AddOnService {
    private String serviceName;
    private double cost;

    public AddOnService(String serviceName, double cost) {
        this.serviceName = serviceName;
        this.cost = cost;
    }

    public String getServiceName() {
        return serviceName;
    }

    public double getCost() {
        return cost;
    }

    @Override
    public String toString() {
        return serviceName + " ($" + cost + ")";
    }
}


class AddOnServiceManager {
    private Map<String, List<AddOnService>> servicesMap;

    public AddOnServiceManager() {
        servicesMap = new HashMap<>();
    }


    public void addService(String reservationId, AddOnService service) {
        servicesMap.computeIfAbsent(reservationId, k -> new ArrayList<>()).add(service);
    }


    public void displayServices(String reservationId) {
        List<AddOnService> list = servicesMap.get(reservationId);
        if (list == null || list.isEmpty()) {
            System.out.println("Reservation " + reservationId + " has no add-on services.");
            return;
        }

        double totalCost = 0;
        System.out.println("Reservation " + reservationId + " selected services:");
        for (AddOnService service : list) {
            System.out.println("- " + service);
            totalCost += service.getCost();
        }
        System.out.println("Total additional cost: $" + totalCost + "\n");
    }
}