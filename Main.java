import java.sql.*;
import java.util.Scanner;

public class Main {

    private static final String url="jdbc:mysql://localhost:3306/hotel";
    private static final String username="root";
    private static final String password="0000";
    public static void main(String[] args) throws ClassNotFoundException,SQLException
    {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
        try {
            Connection conn= DriverManager.getConnection(url,username,password);

            Scanner xyz=new Scanner(System.in);
            System.out.println("Hotel Reservation System");

            while (true)
            {

                System.out.println();
                System.out.println("1. Reserve a Room");
                System.out.println("2. View Reservation");
                System.out.println("3. Get a Room Number");
                System.out.println("4. Update Reservation");
                System.out.println("5. Delete Reservation");
                System.out.println("0. Exit");
                System.out.println("Choose Your Option");
                int choice=xyz.nextInt();
                switch (choice)
                {
                    case 1:
                            reserveRoom( conn,xyz);
                        break;
                    case 2:
                        showReservation(conn,xyz);
                        break;
                    case 3:
                        getRoomNumber(conn,xyz);
                        break;
                    case 4:
                        updateReservation(conn,xyz);
                        break;
                    case 5:
                        deleteReservation(conn,xyz);
                        break;
                    case 0:
                        try {
                            exit();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        xyz.close();
                        return;
                    default:
                        throw new IllegalStateException("Unexpected value: " + choice);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    private static void  reserveRoom(Connection conn,Scanner xyz)
    {
        String query="insert into reservation( reser_id,name,contact_no,room_no) values(?,?,?,?)";
        try {
            PreparedStatement statement=conn.prepareStatement(query);
            System.out.println("Enter Reservation Id :");
            statement.setInt(1,xyz.nextInt());
            xyz.nextLine();
            System.out.println("Enter a Name :");
            statement.setString(2,xyz.nextLine());
            System.out.println("Enter a ConTact Number :");
            statement.setString(3,xyz.nextLine());
            System.out.println("Enter a Room Number");
            statement.setInt(4,xyz.nextInt());

            int res=statement.executeUpdate();
            if(res>0)
            {
                System.out.println("Reservation is Successful");
                System.out.println();
            }
            else System.out.println("Reservation Fails");
            System.out.println();


        } catch (SQLException e) {
            System.out.println( e.getMessage());
        }

    }
    private static void showReservation(Connection conn, Scanner xyz) {
        String query = "SELECT * FROM reservation";
        try {
            PreparedStatement statement = conn.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            System.out.println("--------------------------------------------------------------------------------------");
            System.out.printf("%-15s %-20s %-20s %-15s %-15s\n",
                    "Reservation Id", "Name", "Contact Number", "Room Number", "Date");
            System.out.println("--------------------------------------------------------------------------------------");

            while (resultSet.next()) {
                int reser_id = resultSet.getInt("reser_id");
                String name = resultSet.getString("name");
                String contact = resultSet.getString("contact_no");
                int room_no = resultSet.getInt("room_no");
                String date = resultSet.getDate("reser_date").toString();

                System.out.printf("%-15s %-20s %-20s %-15s %-15s\n",
                        String.format("%04d", reser_id), name, contact, room_no, date);
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    private static void getRoomNumber(Connection conn,Scanner xyz)
    {
        System.out.println("Enter Reservation Id :");
       int reser_id=xyz.nextInt();
       xyz.nextLine();
        System.out.println("Enter a Name :");
        String name=xyz.nextLine();

        String query="select room_no from reservation where reser_id=? and name=?";
        try {
            PreparedStatement statement=conn.prepareStatement(query);
            statement.setInt(1,reser_id);
            statement.setString(2,name);
            ResultSet resultSet= statement.executeQuery();
            while(resultSet.next())
            {
                System.out.println("Room number of Reservation ID "+reser_id+" Guest  "+name+" is :"+resultSet.getInt("room_no"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
    private static boolean reservationExits(Connection conn,int reser_id)
    {
        String query = "SELECT reser_id FROM reservation WHERE reser_id=?";

        try {
            PreparedStatement statement=conn.prepareStatement(query);
            statement.setInt(1,reser_id);
            ResultSet resultSet=statement.executeQuery();

            return resultSet.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    private static void updateReservation(Connection conn,Scanner xyz) {
        System.out.println("Enter Reservation Id");
        int reser_id = xyz.nextInt();
        xyz.nextLine();
        if (!reservationExits(conn, reser_id)) {
            System.out.println("Reservation is Not Found for given Id");
        } else {
            String query = "update reservation set name=?," +
                    "room_no=?,contact_no=? where reser_id=?";
            try {
                PreparedStatement statement = conn.prepareStatement(query);
                System.out.println("Enter a Name :");
                statement.setString(1, xyz.nextLine());
                System.out.println("Enter a Room Number :");
                statement.setInt(2, xyz.nextInt());
                xyz.nextLine();
                System.out.println("Enter a Contact Number :");
                statement.setString(3,xyz.nextLine());
                statement.setInt(4, reser_id);
                int effect=statement.executeUpdate();
                if(effect>0)
                {
                    System.out.println("Reservation successfully Updated");
                }
                else System.out.println("Reservation Update Fails");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
    private static void deleteReservation(Connection conn,Scanner xyz)
    {
        System.out.println("Enter Reservation ID :");
        int reser_id=xyz.nextInt();

        if(!reservationExits(conn,reser_id))
        {
            System.out.println("Reservation is Not Found for given Id");
        }
        else {
            String query = "DELETE FROM reservation WHERE reser_id=?";

            try {
                PreparedStatement statement= conn.prepareStatement(query);
                statement.setInt(1,reser_id);
                int effect= statement.executeUpdate();
                if(effect>0)
                {
                    System.out.println("Reservation successfully Deleted");
                }
                else System.out.println("Reservation Delete Fails");

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
    private static void exit() throws InterruptedException
    {
        System.out.print("Existing System");
        int i=5;
        while(i!=0)
        {
            System.out.print(".");
            Thread.sleep(550);
            i--;
        }
        System.out.println();
        System.out.println("Thank You For Using Hotel Management System");

    }

}