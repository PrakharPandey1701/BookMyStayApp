import java.util.LinkedList;
import java.util.Queue;


public class BookMyStayApp {

    public static void main(String[] args) {
        System.out.println("Welcome to Book My Stay!");
        System.out.println("Hotel Booking System v1.4\n");

        // Initialize booking request queue
        BookingRequestQueue bookingQueue = new BookingRequestQueue();

        // Simulate guests submitting booking requests
        bookingQueue.addRequest(new Reservation("Alice", "SingleRoom"));
        bookingQueue.addRequest(new Reservation("Bob", "SuiteRoom"));
        bookingQueue.addRequest(new Reservation("Charlie", "DoubleRoom"));
        bookingQueue.addRequest(new Reservation("Diana", "SingleRoom"));

        // Display all queued booking requests in order
        System.out.println("Queued Booking Requests (First-Come-First-Served):");
        bookingQueue.displayAllRequests();

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

    @Override
    public String toString() {
        return "Guest: " + guestName + " | Requested Room: " + roomType;
    }
}


class BookingRequestQueue {
    private Queue<Reservation> requestQueue;

    public BookingRequestQueue() {
        requestQueue = new LinkedList<>();
    }


    public void addRequest(Reservation reservation) {
        requestQueue.add(reservation);
        System.out.println("Booking request received for " + reservation.getGuestName() + " (" + reservation.getRoomType() + ")");
    }


    public void displayAllRequests() {
        if (requestQueue.isEmpty()) {
            System.out.println("No pending booking requests.");
            return;
        }

        for (Reservation res : requestQueue) {
            System.out.println(res);
        }
    }


    public Reservation processNextRequest() {
        return requestQueue.poll(); // returns null if queue is empty
    }


    public boolean isEmpty() {
        return requestQueue.isEmpty();
    }
}