import java.sql.*;
import java.util.Scanner;

public class Main {
    private static final String username = "root";
    private static final String password = "Pratham45!";
    private static final String url = "jdbc:mysql://localhost:3306/hotel?useSSL=false&serverTimezone=UTC";

    public static void main(String[] args) {
        try {
            // Load the MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("Drivers connected successfully");
        } catch (Exception e) {
            System.out.println("Driver error: " + e.getMessage());
            return;
        }

        try (Connection connection = DriverManager.getConnection(url, username, password);
             Scanner sc = new Scanner(System.in)) {
            System.out.println("Database connected successfully");

            while (true) {
                System.out.println("\nHotel Reservation System");
                System.out.println("Select an Option");
                System.out.println("1. Reserve a room");
                System.out.println("2. View Reservations");
                System.out.println("3. Get Room number");
                System.out.println("4. Update Reservation");
                System.out.println("5. Delete Reservation");
                System.out.println("0. Exit");
                int choice = sc.nextInt();
                sc.nextLine(); // Consume the newline character

                switch (choice) {
                    case 1:
                        reserveRooms(connection, sc);
                        break;
                    case 2:
                        viewReservations(connection, sc);
                        break;
                    case 3:
                        getRooms(connection, sc);
                        break;
                    case 4:
                        updateReservation(connection, sc);
                        break;
                    case 5:
                        deleteReservation(connection, sc);
                        break;
                    case 0:
                        System.out.println("Exiting...");
                        return;
                    default:
                        System.out.println("Invalid option, please try again.");
                        break;
                }
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    public static void reserveRooms(Connection connection, Scanner scanner) {
        try {
            System.out.println("Enter guest name:");
            String name = scanner.nextLine();

            System.out.println("Enter Room number:");
            int roomNumber = scanner.nextInt();
            scanner.nextLine();

            System.out.println("Enter Phone number:");
            String phoneNumber = scanner.next();
            scanner.nextLine();

            String sql = "INSERT INTO reservations (guest_name, room_no, contact_number) VALUES (?, ?, ?)";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, name);
                pstmt.setInt(2, roomNumber);
                pstmt.setString(3, phoneNumber);

                int affected = pstmt.executeUpdate();
                if (affected > 0) {
                    System.out.println("Successful reservation");
                } else {
                    System.out.println("Reservation failed");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error in reserveRooms: " + e.getMessage());
        }
    }

    public static void viewReservations(Connection connection, Scanner sc) {
        String sql = "SELECT * FROM reservations";

        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            System.out.println("+---------------+----------------+----------+---------------+");
            System.out.println("| Reservation ID | Guest Name    | Room No  | Contact Number|");
            System.out.println("+---------------+----------------+----------+---------------+");

            while (rs.next()) {
                int reservationId = rs.getInt("reservation_id");
                String guestName = rs.getString("guest_name");
                int roomNo = rs.getInt("room_no");
                String contactNumber = rs.getString("contact_number");

                System.out.format("| %-13d | %-14s | %-8d | %-13s |%n",
                        reservationId, guestName, roomNo, contactNumber);
            }

            System.out.println("+---------------+----------------+----------+---------------+");
        } catch (SQLException e) {
            System.out.println("Error viewing reservations: " + e.getMessage());
        }
    }

    public static void getRooms(Connection connection, Scanner scanner) {
        System.out.println("Enter the reservation ID:");
        int id = scanner.nextInt();
        scanner.nextLine(); // consume newline character

        System.out.println("Enter the guest name:");
        String name = scanner.nextLine();

        String sql = "SELECT reservation_id, guest_name, room_no, contact_number FROM reservations WHERE reservation_id = ? AND guest_name = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.setString(2, name);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    System.out.println("+---------------+----------------+----------+---------------+");
                    System.out.println("| Reservation ID | Guest Name    | Room No  | Contact Number|");
                    System.out.println("+---------------+----------------+----------+---------------+");

                    int reservationId = rs.getInt("reservation_id");
                    String guestName = rs.getString("guest_name");
                    int roomNo = rs.getInt("room_no");
                    String contactNumber = rs.getString("contact_number");

                    System.out.format("| %-13d | %-14s | %-8d | %-13s |%n",
                            reservationId, guestName, roomNo, contactNumber);

                    System.out.println("+---------------+----------------+----------+---------------+");
                } else {
                    System.out.println("No reservation found with the provided ID and name.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving room information: " + e.getMessage());
        }
    }

    public static void updateReservation(Connection connection, Scanner scanner) {
        System.out.println("Enter the reservation ID to update:");
        int id = scanner.nextInt();
        scanner.nextLine(); // consume newline

        System.out.println("Enter new guest name:");
        String name = scanner.nextLine();

        System.out.println("Enter new room number:");
        int roomNumber = scanner.nextInt();
        scanner.nextLine();

        System.out.println("Enter new phone number:");
        String phoneNumber = scanner.nextLine();

        String sql = "UPDATE reservations SET guest_name = ?, room_no = ?, contact_number = ? WHERE reservation_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setInt(2, roomNumber);
            pstmt.setString(3, phoneNumber);
            pstmt.setInt(4, id);

            int affected = pstmt.executeUpdate();
            if (affected > 0) {
                System.out.println("Reservation updated successfully");
            } else {
                System.out.println("No reservation found with the provided ID.");
            }
        } catch (SQLException e) {
            System.out.println("Error updating reservation: " + e.getMessage());
        }
    }

    public static void deleteReservation(Connection connection, Scanner scanner) {
        System.out.println("Enter the reservation ID to delete:");
        int id = scanner.nextInt();
        scanner.nextLine(); // consume newline

        String sql = "DELETE FROM reservations WHERE reservation_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);

            int affected = pstmt.executeUpdate();
            if (affected > 0) {
                System.out.println("Reservation deleted successfully");
            } else {
                System.out.println("No reservation found with the provided ID.");
            }
        } catch (SQLException e) {
            System.out.println("Error deleting reservation: " + e.getMessage());
        }
    }
}
