package org.BookWise.app;

import org.apache.ibatis.jdbc.ScriptRunner;

import java.io.InputStreamReader;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DatabaseAccess {

    private static Connection connection;

    public static Connection connection() {
    
        if (connection == null) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                try (var rootConnection = DriverManager.getConnection("jdbc:mysql://localhost:3306", "root",
                        System.getenv("DB_PASSWORD"))) {
                    // noinspection DataFlowIssue
                    new ScriptRunner(rootConnection)
                            .runScript(new InputStreamReader(DatabaseAccess.class.getResourceAsStream("BookWise.sql")));
                }
                connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/BookWise", "root",
                        System.getenv("DB_PASSWORD"));
            } catch (ClassNotFoundException | SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return connection;
    }

    interface Updatable<PK> {
        void updateDB();

        PK primaryKey();

        void updatePrimaryKey(PK oldKey);
    }

    public record Genre(String name, String category) {
    }

    public record Book(String bookName, String author, String isbn, String publisher, String genre, int totalCopies)
            implements Updatable<String> {
        public void updateDB() {
            try (var stmt = connection().prepareStatement(
                    "UPDATE books SET bookName = ?, author = ?, publisher = ?, genre = ?, totalCopies = ? WHERE isbn = ?")) {
                stmt.setString(1, bookName);
                stmt.setString(2, author);
                stmt.setString(3, publisher);
                stmt.setString(4, genre);
                stmt.setInt(5, totalCopies);
                stmt.setString(6, isbn);

                int rowsUpdated = stmt.executeUpdate();
                if (rowsUpdated > 0) {
                    System.out.println("Book with ISBN " + isbn + " was updated successfully.");
                } else {
                    throw new RuntimeException("No book with ISBN " + isbn + " was found.");
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public String primaryKey() {
            return isbn;
        }

        public void updatePrimaryKey(String oldKey) {
            try (var stmt = connection().prepareStatement("UPDATE books SET isbn = ? WHERE isbn = ?")) {
                stmt.setString(1, isbn);
                stmt.setString(2, oldKey);

                int rowsUpdated = stmt.executeUpdate();
                if (rowsUpdated > 0) {
                    System.out.println("Book with ISBN " + oldKey + " was updated successfully.");
                } else {
                    throw new RuntimeException("No book with ISBN " + oldKey + " was found.");
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        public static Book empty() {
            return new Book("<edit>", "<edit>", Utils.makeISBN(), "<edit>", "Reference", 0);
        }
    }

    public record User(String userID, String fullName, String email, String address, String password, boolean isAdmin)
            implements Updatable<String> {
        public void updateDB() {
            try (var stmt = connection()
                    .prepareStatement("UPDATE users SET fullName = ?, email = ?, address = ? WHERE userID = ?")) {
                stmt.setString(1, fullName);
                stmt.setString(2, email);
                stmt.setString(3, address);
                stmt.setString(4, userID);

                int rowsUpdated = stmt.executeUpdate();
                if (rowsUpdated > 0) {
                    System.out.println("User with ID " + userID + " was updated successfully.");
                } else {
                    throw new RuntimeException("No user with ID " + userID + " was found.");
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        public String primaryKey() {
            return userID;
        }

        public void updatePrimaryKey(String oldKey) {
            try (var stmt = connection().prepareStatement("UPDATE users SET userID = ? WHERE userID = ?")) {
                stmt.setString(1, userID);
                stmt.setString(2, oldKey);

                int rowsUpdated = stmt.executeUpdate();
                if (rowsUpdated > 0) {
                    System.out.println("User with ID " + oldKey + " was updated successfully.");
                } else {
                    throw new RuntimeException("No user with ID " + oldKey + " was found.");
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public record Loan(int loanID, String isbn, String userID, Date checkoutDate, Date expectedReturnDate,
            boolean returned) implements Updatable<Integer> {
        public void updateDB() {
            try (var stmt = connection().prepareStatement(
                    "UPDATE loans SET isbn = ?, userID = ?, checkoutDate = ?, expectedReturnDate = ?, returned = ? WHERE loanID = ?")) {
                stmt.setString(1, isbn);
                stmt.setString(2, userID);
                stmt.setDate(3, checkoutDate);
                stmt.setDate(4, expectedReturnDate);
                stmt.setBoolean(5, returned);
                stmt.setInt(6, loanID);

                int rowsUpdated = stmt.executeUpdate();
                if (rowsUpdated > 0) {
                    System.out.println("Loan with ID " + loanID + " was updated successfully.");
                } else {
                    throw new RuntimeException("No loan with ID " + loanID + " was found.");
                }
            } catch (SQLException e) {
                if (e.getMessage().contains("foreign key constraint fails")) {
                    throw new RuntimeException("Either the user ID or ISBN was not found.", e);
                }
                throw new RuntimeException(e);
            }
        }

        public Integer primaryKey() {
            return loanID;
        }

        public void updatePrimaryKey(Integer oldKey) {
            try (var stmt = connection().prepareStatement("UPDATE loans SET loanID = ? WHERE loanID = ?")) {
                stmt.setInt(1, loanID);
                stmt.setInt(2, oldKey);

                int rowsUpdated = stmt.executeUpdate();
                if (rowsUpdated > 0) {
                    System.out.println("Loan with ID " + oldKey + " was updated successfully.");
                } else {
                    throw new RuntimeException("No loan with ID " + oldKey + " was found.");
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        public static Loan empty(String isbn) {
            return new Loan(-1, isbn, "<edit>", Date.valueOf(LocalDate.now()), Date.valueOf(LocalDate.now()), false);
        }
    }

    public record Hold(int holdID, String isbn, String userID, Date holdDate) implements Updatable<Integer> {
        public void updateDB() {
            try (var stmt = connection()
                    .prepareStatement("UPDATE holds SET isbn = ?, userID = ?, holdDate = ? WHERE holdID = ?")) {
                stmt.setString(1, isbn);
                stmt.setString(2, userID);
                stmt.setDate(3, holdDate);
                stmt.setInt(4, holdID);

                int rowsUpdated = stmt.executeUpdate();
                if (rowsUpdated > 0) {
                    System.out.println("Hold with ID " + holdID + " was updated successfully.");
                } else {
                    throw new RuntimeException("No hold with ID " + holdID + " was found.");
                }
            } catch (SQLException e) {
                if (e.getMessage().contains("foreign key constraint fails")) {
                    throw new RuntimeException("Either the user ID or ISBN was not found.", e);
                }
                throw new RuntimeException(e);
            }
        }

        public Integer primaryKey() {
            return holdID;
        }

        public void updatePrimaryKey(Integer oldKey) {
            try (var stmt = connection().prepareStatement("UPDATE holds SET holdID = ? WHERE holdID = ?")) {
                stmt.setInt(1, holdID);
                stmt.setInt(2, oldKey);

                int rowsUpdated = stmt.executeUpdate();
                if (rowsUpdated > 0) {
                    System.out.println("Hold with ID " + oldKey + " was updated successfully.");
                } else {
                    throw new RuntimeException("No hold with ID " + oldKey + " was found.");
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        public static Hold empty(String isbn) {
            return new Hold(-1, isbn, "<edit>", Date.valueOf(LocalDate.now()));
        }
    }

    public record Fine(int fineID, int loanID, String userID, double fineAmount, Date fineDate, 
                      String paymentStatus, Date paymentDate) implements Updatable<Integer> {
        
        public void updateDB() {
            try (var stmt = connection().prepareStatement(
                    "UPDATE fines SET loanID = ?, userID = ?, fineAmount = ?, " +
                    "fineDate = ?, paymentStatus = ?, paymentDate = ? WHERE fineID = ?")) {
                stmt.setInt(1, loanID);
                stmt.setString(2, userID);
                stmt.setDouble(3, fineAmount);
                stmt.setDate(4, fineDate);
                stmt.setString(5, paymentStatus);
                if (paymentDate != null) {
                    stmt.setDate(6, paymentDate);
                } else {
                    stmt.setNull(6, Types.DATE);
                }
                stmt.setInt(7, fineID);

                int rowsUpdated = stmt.executeUpdate();
                if (rowsUpdated > 0) {
                    System.out.println("Fine with ID " + fineID + " was updated successfully.");
                } else {
                    throw new RuntimeException("No fine with ID " + fineID + " was found.");
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public Integer primaryKey() {
            return fineID;
        }

        public void updatePrimaryKey(Integer oldKey) {
            try (var stmt = connection().prepareStatement("UPDATE fines SET fineID = ? WHERE fineID = ?")) {
                stmt.setInt(1, fineID);
                stmt.setInt(2, oldKey);

                int rowsUpdated = stmt.executeUpdate();
                if (rowsUpdated > 0) {
                    System.out.println("Fine with ID " + oldKey + " was updated successfully.");
                } else {
                    throw new RuntimeException("No fine with ID " + oldKey + " was found.");
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static Optional<User> login(String username, String password) {
        try (var stmt = connection().prepareStatement("SELECT * FROM users where userID = ? AND userID != '<edit>'")) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next() && verifyPassword(password, rs.getString("password"))) {
                return Optional.of(new User(rs.getString("userID"), rs.getString("fullName"), rs.getString("email"),
                        rs.getString("address"), rs.getString("password"), rs.getBoolean("isAdmin")));
            } else {
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void updatePassword(String userID, String currentPassword, String newPassword) {
        try (var stmt = connection()
                .prepareStatement("SELECT password FROM users WHERE userID = ? AND userID != '<edit>'")) {
            stmt.setString(1, userID);
            ResultSet rs = stmt.executeQuery();
            if (rs.next() && verifyPassword(currentPassword, rs.getString("password"))) {
                try (var updateStmt = connection().prepareStatement("UPDATE users SET password = ? WHERE userID = ?")) {
                    updateStmt.setString(1, hashPassword(newPassword));
                    updateStmt.setString(2, userID);
                    int rowsUpdated = updateStmt.executeUpdate();
                    if (rowsUpdated > 0) {
                        System.out.println("Password for user with ID " + userID + " was updated successfully.");
                    } else {
                        throw new RuntimeException("Unable to update password for user '" + userID + "'.");
                    }
                }
            } else {
                throw new RuntimeException("Invalid password.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public static boolean verifyPassword(String password, String hash) {
        return BCrypt.checkpw(password, hash);
    }

    public static Optional<Book> getBook(String isbn) {
        try (var stmt = connection().prepareStatement("SELECT * FROM books WHERE isbn = ?")) {
            stmt.setString(1, isbn);
            ResultSet rs = stmt.executeQuery();
            return rs.next()
                    ? Optional.of(new Book(rs.getString("bookName"), rs.getString("author"), rs.getString("isbn"),
                            rs.getString("publisher"), rs.getString("genre"), rs.getInt("totalCopies")))
                    : Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Book> getBooks() {
        List<Book> books = new ArrayList<>();
        try (var stmt = connection().prepareStatement("SELECT * FROM books")) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                books.add(new Book(rs.getString("bookName"), rs.getString("author"), rs.getString("isbn"),
                        rs.getString("publisher"), rs.getString("genre"), rs.getInt("totalCopies")));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return books;
    }

    public static Optional<User> getPatron(String userID) {
        try (var stmt = connection()
                .prepareStatement("SELECT * FROM users WHERE isAdmin = FALSE AND userID = ? AND userID != '<edit>'")) {
            stmt.setString(1, userID);
            ResultSet rs = stmt.executeQuery();
            return rs.next()
                    ? Optional.of(new User(rs.getString("userID"), rs.getString("fullName"), rs.getString("email"),
                            rs.getString("address"), rs.getString("password"), false))
                    : Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<User> getPatrons() {
        var patrons = new ArrayList<User>();
        try (var stmt = connection()
                .prepareStatement("SELECT * FROM users WHERE isAdmin = FALSE AND userID != '<edit>'")) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                patrons.add(new User(rs.getString("userID"), rs.getString("fullName"), rs.getString("email"),
                        rs.getString("address"), rs.getString("password"), false));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return patrons;
    }

    public static void addPatron(User patron) {
        try (var stmt = connection().prepareStatement(
                "INSERT INTO users (userID, fullName, email, address, password) VALUES (?, ?, ?, ?, ?)")) {
            stmt.setString(1, patron.userID);
            stmt.setString(2, patron.fullName);
            stmt.setString(3, patron.email);
            stmt.setString(4, patron.address);
            stmt.setString(5, hashPassword(patron.password));

            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("A new Patron was added successfully.");
            }
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new RuntimeException("Duplicate User ID", e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void removePatron(String id) {
        try (var stmt = connection().prepareStatement("DELETE FROM users WHERE userID = ? AND userID != '<edit>'")) {
            stmt.setString(1, id);

            int rowsDeleted = stmt.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Patron with ID " + id + " was deleted successfully.");
            } else {
                System.out.println("No Patron with ID" + id + " was found.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Hold> getHolds() {
        var holds = new ArrayList<Hold>();
        try (var stmt = connection().prepareStatement("SELECT * FROM holds")) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                holds.add(new Hold(rs.getInt("holdID"), rs.getString("isbn"), rs.getString("userID"),
                        rs.getDate("holdDate")));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return holds;
    }

    public static List<Hold> getHoldsForPatron(String userID) {
        var holds = new ArrayList<Hold>();
        try (var stmt = connection().prepareStatement("SELECT * FROM holds WHERE userID = ?")) {
            stmt.setString(1, userID);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                holds.add(new Hold(rs.getInt("holdID"), rs.getString("isbn"), rs.getString("userID"),
                        rs.getDate("holdDate")));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return holds;
    }

    public static List<Loan> getLoans() {
        var loans = new ArrayList<Loan>();
        try (var stmt = connection().prepareStatement("SELECT * FROM loans")) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                loans.add(new Loan(rs.getInt("loanID"), rs.getString("isbn"), rs.getString("userID"),
                        rs.getDate("checkoutDate"), rs.getDate("expectedReturnDate"), rs.getBoolean("returned")));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return loans;
    }

    public static List<Loan> getLoansForPatron(String userID) {
        var loans = new ArrayList<Loan>();
        try (var stmt = connection().prepareStatement("SELECT * FROM loans WHERE userID = ?")) {
            stmt.setString(1, userID);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                loans.add(new Loan(rs.getInt("loanID"), rs.getString("isbn"), rs.getString("userID"),
                        rs.getDate("checkoutDate"), rs.getDate("expectedReturnDate"), rs.getBoolean("returned")));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return loans;
    }

    public static List<Loan> getLoansForBook(String isbn) {
        var loans = new ArrayList<Loan>();
        try (var stmt = connection().prepareStatement("SELECT * FROM loans WHERE isbn = ?")) {
            stmt.setString(1, isbn);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                loans.add(new Loan(rs.getInt("loanID"), rs.getString("isbn"), rs.getString("userID"),
                        rs.getDate("checkoutDate"), rs.getDate("expectedReturnDate"), rs.getBoolean("returned")));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return loans;
    }

    public static List<Fine> getFines() {
        var fines = new ArrayList<Fine>();
        try (var stmt = connection().prepareStatement("SELECT * FROM fines")) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                fines.add(new Fine(
                    rs.getInt("fineID"),
                    rs.getInt("loanID"),
                    rs.getString("userID"),
                    rs.getDouble("fineAmount"),
                    rs.getDate("fineDate"),
                    rs.getString("paymentStatus"),
                    rs.getDate("paymentDate")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return fines;
    }

    public static List<Fine> getFinesForPatron(String userID) {
        var fines = new ArrayList<Fine>();
        try (var stmt = connection().prepareStatement("SELECT * FROM fines WHERE userID = ?")) {
            stmt.setString(1, userID);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                fines.add(new Fine(
                    rs.getInt("fineID"),
                    rs.getInt("loanID"),
                    rs.getString("userID"),
                    rs.getDouble("fineAmount"),
                    rs.getDate("fineDate"),
                    rs.getString("paymentStatus"),
                    rs.getDate("paymentDate")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return fines;
    }

    public static Optional<Fine> addFine(Fine fine) {
        try (var stmt = connection().prepareStatement(
                "INSERT INTO fines (loanID, userID, fineAmount, fineDate, paymentStatus) " +
                "VALUES (?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, fine.loanID());
            stmt.setString(2, fine.userID());
            stmt.setDouble(3, fine.fineAmount());
            stmt.setDate(4, fine.fineDate());
            stmt.setString(5, fine.paymentStatus());

            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    return Optional.of(new Fine(
                        rs.getInt(1),
                        fine.loanID(),
                        fine.userID(),
                        fine.fineAmount(),
                        fine.fineDate(),
                        fine.paymentStatus(),
                        fine.paymentDate()
                    ));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to add fine.", e);
        }
    }

    public static boolean payFine(int fineID) {
        try (var stmt = connection().prepareStatement(
                "UPDATE fines SET paymentStatus = 'Paid', paymentDate = CURRENT_DATE WHERE fineID = ?")) {
            stmt.setInt(1, fineID);
            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void calculateFines() {
        try (var stmt = connection().prepareStatement(
                "SELECT * FROM loans WHERE returned = FALSE AND expectedReturnDate < CURRENT_DATE")) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Loan loan = new Loan(
                    rs.getInt("loanID"),
                    rs.getString("isbn"),
                    rs.getString("userID"),
                    rs.getDate("checkoutDate"),
                    rs.getDate("expectedReturnDate"),
                    rs.getBoolean("returned")
                );
                
                // Calculate days overdue
                long diffInMillies = System.currentTimeMillis() - loan.expectedReturnDate().getTime();
                int daysOverdue = (int) (diffInMillies / (1000 * 60 * 60 * 24));
                
                if (daysOverdue > 0) {
                    double fineAmount = daysOverdue * 0.50; // $0.50 per day
                    
                    // Check if fine already exists
                    try (var checkStmt = connection().prepareStatement(
                            "SELECT * FROM fines WHERE loanID = ? AND paymentStatus = 'Unpaid'")) {
                        checkStmt.setInt(1, loan.loanID());
                        ResultSet fineRs = checkStmt.executeQuery();
                        
                        if (!fineRs.next()) {
                            // Create new fine
                            Fine fine = new Fine(
                                -1,
                                loan.loanID(),
                                loan.userID(),
                                fineAmount,
                                new Date(System.currentTimeMillis()),
                                "Unpaid",
                                null
                            );
                            addFine(fine);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static Optional<User> getPatronForLoan(Loan loan) {
        try (var stmt = connection().prepareStatement("SELECT * FROM users WHERE userID = ? AND userID != '<edit>'")) {
            stmt.setString(1, loan.userID);
            ResultSet rs = stmt.executeQuery();
            return rs.next()
                    ? Optional.of(new User(rs.getString("userID"), rs.getString("fullName"), rs.getString("email"),
                            rs.getString("address"), rs.getString("password"), rs.getBoolean("isAdmin")))
                    : Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void addBook(Book book) {
        try (var stmt = connection().prepareStatement(
                "INSERT INTO books (bookName, author, isbn, publisher, genre, totalCopies) VALUES (?, ?, ?, ?, ?, ?)")) {
            stmt.setString(1, book.bookName);
            stmt.setString(2, book.author);
            stmt.setString(3, book.isbn);
            stmt.setString(4, book.publisher);
            stmt.setString(5, book.genre);
            stmt.setInt(6, book.totalCopies);
            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("A new book was added successfully.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void removeBook(String isbn) {
        try (var stmt = connection().prepareStatement("DELETE FROM books WHERE isbn = ?")) {
            stmt.setString(1, isbn);

            int rowsDeleted = stmt.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Book with ISBN " + isbn + " was deleted successfully.");
            } else {
                System.out.println("No book with ISBN " + isbn + " was found.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static int getAvailableCopies(String isbn) {
        try (var checkedOutCopiesStmt = connection()
                .prepareStatement("SELECT COUNT(*) FROM loans WHERE returned = FALSE AND isbn = ?")) {
            checkedOutCopiesStmt.setString(1, isbn);
            ResultSet rs = checkedOutCopiesStmt.executeQuery();
            int checkedOutCopies = rs.next() ? rs.getInt(1) : 0;
            try (var totalCopiesStmt = connection().prepareStatement("SELECT totalCopies FROM books WHERE isbn = ?")) {
                totalCopiesStmt.setString(1, isbn);
                rs = totalCopiesStmt.executeQuery();
                int totalCopies = rs.next() ? rs.getInt("totalCopies") : 0;
                return Math.max(totalCopies - checkedOutCopies, 0);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean isBookAvailable(String isbn) {
        return getAvailableCopies(isbn) > 0;
    }

    public static Optional<Hold> addHold(Hold hold) {
        var bookName = getBook(hold.isbn).orElseThrow(() -> new RuntimeException("Book not found.")).bookName;
        if (!isBookAvailable(hold.isbn)) {
            try (var stmt = connection()
                    .prepareStatement("INSERT INTO holds (isbn, userID, holdDate) VALUES (?, ?, ?)")) {
                stmt.setString(1, hold.isbn);
                stmt.setString(2, hold.userID);
                stmt.setDate(3, hold.holdDate);

                int rowsInserted = stmt.executeUpdate();
                if (rowsInserted > 0) {
                    System.out.println("A new hold was added successfully.");
                    try (var st = connection().prepareStatement(
                            "SELECT holdID FROM holds WHERE isbn = ? AND userID = ? AND holdDate = ?")) {
                        st.setString(1, hold.isbn);
                        st.setString(2, hold.userID);
                        st.setDate(3, hold.holdDate);

                        ResultSet rs = st.executeQuery();
                        return rs.next()
                                ? Optional.of(new Hold(rs.getInt("holdID"), hold.isbn, hold.userID, hold.holdDate))
                                : Optional.empty();
                    }
                }
                return Optional.empty();
            } catch (SQLException e) {
                throw new RuntimeException("Failed to add hold.", e);
            }
        } else {
            throw new RuntimeException("Book '" + bookName + "' is currently available. Please check it out instead.");
        }
    }

    public static Optional<Loan> addLoan(Loan loan) {
        var bookName = getBook(loan.isbn).orElseThrow(() -> new RuntimeException("Book not found.")).bookName;
        if (isBookAvailable(loan.isbn)) {
            try (var stmt = connection().prepareStatement(
                    "INSERT INTO loans (isbn, userID, checkoutDate, expectedReturnDate, returned) VALUES (?, ?, ?, ?,?)")) {
                stmt.setString(1, loan.isbn);
                stmt.setString(2, loan.userID);
                stmt.setDate(3, loan.checkoutDate);
                stmt.setDate(4, loan.expectedReturnDate);
                stmt.setBoolean(5, loan.returned);

                int rowsInserted = stmt.executeUpdate();
                if (rowsInserted > 0) {
                    try (var st = connection().prepareStatement(
                            "SELECT loanID FROM Loans WHERE isbn = ? AND userID = ? AND checkoutDate = ? AND returned = ?")) {
                        st.setString(1, loan.isbn);
                        st.setString(2, loan.userID);
                        st.setDate(3, loan.checkoutDate);
                        st.setBoolean(4, loan.returned);

                        ResultSet rs = st.executeQuery();
                        return rs.next()
                                ? Optional.of(new Loan(rs.getInt("loanID"), loan.isbn, loan.userID, loan.checkoutDate,
                                        loan.expectedReturnDate, loan.returned))
                                : Optional.empty();
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException("Failed to add loan.", e);
            }
            return Optional.empty();
        } else {
            throw new RuntimeException(
                    "Book '" + bookName + "' is currently checked out. Please place a hold instead.");
        }
    }

    public static void removeLoan(Loan loan) {
        try (var stmt = connection().prepareStatement("DELETE FROM loans WHERE isbn = ?")) {
            stmt.setString(1, loan.isbn);

            int rowsDeleted = stmt.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Book with ISBN " + loan.isbn + " was successfully returned.");
            } else {
                System.out.println("No book with ISBN " + loan.isbn + " was found in loans.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void removeHold(int holdID) {
        try (var stmt = connection().prepareStatement("DELETE FROM holds WHERE holdID = ?")) {
            stmt.setInt(1, holdID);

            int rowsDeleted = stmt.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Hold with ID " + holdID + " was successfully removed.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}