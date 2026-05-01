package com.hello.hello.jdbc;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class BankTransactionProgram {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/lenden";
    private static final String DB_USER = System.getenv().getOrDefault("DB_USER", "root");
    private static final String DB_PASSWORD = System.getenv().getOrDefault("DB_PASSWORD", "root");
    private static final int CREDIT_ACCOUNT_NUMBER = 102;

    private record AccountDetails(int accountNumber, String name, BigDecimal balance) {
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("--- Bank Transaction Program ---");
        System.out.println("Database: lenden");
        System.out.println("Credit account: " + CREDIT_ACCOUNT_NUMBER);

        System.out.print("Enter account number to debit: ");
        int accountNumber = scanner.nextInt();

        System.out.print("Enter amount: ");
        BigDecimal amount = scanner.nextBigDecimal();

        System.out.println("Requested debit account: " + accountNumber);
        System.out.println("Requested amount: " + amount);

        transferAmount(accountNumber, amount);
    }

    private static void transferAmount(int fromAccount, BigDecimal amount) {
        Connection connection = null;
        try {
            System.out.println("Connecting to MySQL...");
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            connection.setAutoCommit(false);
            System.out.println("Auto-commit disabled.");

            AccountDetails sourceAccount = getAccountForUpdate(connection, fromAccount);
            AccountDetails creditAccount = getAccountForUpdate(connection, CREDIT_ACCOUNT_NUMBER);

            if (sourceAccount == null) {
                System.out.println("Source account not found.");
                connection.rollback();
                System.out.println("Transaction Failed");
                return;
            }

            if (creditAccount == null) {
                System.out.println("Credit account 102 not found.");
                connection.rollback();
                System.out.println("Transaction Failed");
                return;
            }

            System.out.println("Source account before transaction: " + formatAccount(sourceAccount));
            System.out.println("Credit account before transaction: " + formatAccount(creditAccount));

            if (sourceAccount.balance().compareTo(amount) < 0) {
                System.out.println("Insufficient balance. Available: " + sourceAccount.balance() + ", Required: " + amount);
                connection.rollback();
                System.out.println("Transaction Failed");
                return;
            }

            System.out.println("Debiting amount from account " + fromAccount + "...");
            boolean debitOk = updateBalance(connection, fromAccount, amount.negate());
            System.out.println("Crediting amount to account " + CREDIT_ACCOUNT_NUMBER + "...");
            boolean creditOk = updateBalance(connection, CREDIT_ACCOUNT_NUMBER, amount);

            if (debitOk && creditOk) {
                AccountDetails updatedSource = getAccount(connection, fromAccount);
                AccountDetails updatedCredit = getAccount(connection, CREDIT_ACCOUNT_NUMBER);

                connection.commit();
                System.out.println("Source account after transaction: " + formatAccount(updatedSource));
                System.out.println("Credit account after transaction: " + formatAccount(updatedCredit));
                System.out.println("Transaction Successful");
            } else {
                System.out.println("One of the update queries failed. Rolling back...");
                connection.rollback();
                System.out.println("Transaction Failed");
            }
        } catch (Exception ex) {
            rollbackQuietly(connection);
            System.out.println("Transaction Failed");
            ex.printStackTrace();
        } finally {
            closeQuietly(connection);
        }
    }

    private static AccountDetails getAccountForUpdate(Connection connection, int accountNumber) throws SQLException {
        String sql = "SELECT account_number, name, balance FROM accounts WHERE account_number = ? FOR UPDATE";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, accountNumber);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new AccountDetails(
                            resultSet.getInt("account_number"),
                            resultSet.getString("name"),
                            resultSet.getBigDecimal("balance")
                    );
                }
                return null;
            }
        }
    }

    private static AccountDetails getAccount(Connection connection, int accountNumber) throws SQLException {
        String sql = "SELECT account_number, name, balance FROM accounts WHERE account_number = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, accountNumber);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new AccountDetails(
                            resultSet.getInt("account_number"),
                            resultSet.getString("name"),
                            resultSet.getBigDecimal("balance")
                    );
                }
                return null;
            }
        }
    }

    private static boolean updateBalance(Connection connection, int accountNumber, BigDecimal delta) throws SQLException {
        String sql = "UPDATE accounts SET balance = balance + ? WHERE account_number = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setBigDecimal(1, delta);
            statement.setInt(2, accountNumber);
            return statement.executeUpdate() == 1;
        }
    }

    private static String formatAccount(AccountDetails accountDetails) {
        return "account_number=" + accountDetails.accountNumber()
                + ", name=" + accountDetails.name()
                + ", balance=" + accountDetails.balance();
    }

    private static void rollbackQuietly(Connection connection) {
        if (connection == null) {
            return;
        }
        try {
            connection.rollback();
        } catch (SQLException ignored) {
            // No-op: rollback best effort.
        }
    }

    private static void closeQuietly(Connection connection) {
        if (connection == null) {
            return;
        }
        try {
            connection.close();
        } catch (SQLException ignored) {
            // No-op: close best effort.
        }
    }
}
