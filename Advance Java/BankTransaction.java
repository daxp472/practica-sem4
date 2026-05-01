import java.sql.*;

public class BankTransaction {
    public static void main(String[] args) {

        String url = "jdbc:mysql://localhost:3306/lenden";
        String user = "root";
        String password = "M@hir2005";

        Connection con = null;

        try {
            con = DriverManager.getConnection(url, user, password);

            // Disable auto commit
            con.setAutoCommit(false);

            int fromAcc = 1;   
            int toAcc = 102;     
            int amount = 500;

            // Check balance
            PreparedStatement checkStmt = con.prepareStatement(
                "SELECT balance FROM accounts WHERE account_number = ?");
            checkStmt.setInt(1, fromAcc);

            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                int balance = rs.getInt("balance");

                if (balance >= amount) {

                    // Debit
                    PreparedStatement debit = con.prepareStatement(
                        "UPDATE accounts SET balance = balance - ? WHERE account_number = ?");
                    debit.setInt(1, amount);
                    debit.setInt(2, fromAcc);
                    debit.executeUpdate();

                    // Credit
                    PreparedStatement credit = con.prepareStatement(
                        "UPDATE accounts SET balance = balance + ? WHERE account_number = ?");
                    credit.setInt(1, amount);
                    credit.setInt(2, toAcc);
                    credit.executeUpdate();

                    // Commit
                    con.commit();
                    System.out.println("Transaction Successful!");

                } else {
                    // Rollback
                    con.rollback();
                    System.out.println("Transaction Failed! Not Enough Balance.");
                }
            }

        } catch (Exception e) {
            // This line prints the exact reason why it is failing!
            e.printStackTrace(); 
            try {
                if (con != null) con.rollback();
            } catch (Exception ex) {}
            System.out.println("Error occurred!");
        } finally {
            try {
                if (con != null) con.close();
            } catch (Exception e) {}
        }
    }
}