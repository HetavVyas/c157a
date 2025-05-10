-- ==============================
-- BookWise.sql
-- Combined Schema + Initialization Script
-- ==============================

CREATE DATABASE IF NOT EXISTS BookWise;
CREATE SCHEMA IF NOT EXISTS BookWise;
USE BookWise;

-- Genre Table
CREATE TABLE IF NOT EXISTS genre(
name     VARCHAR(45) NOT NULL,
category VARCHAR(45) NULL DEFAULT NULL,
PRIMARY KEY (name)
);

-- Books Table
CREATE TABLE IF NOT EXISTS books(
bookName    VARCHAR(45) NOT NULL,
author      VARCHAR(45) NOT NULL,
isbn        VARCHAR(45) NOT NULL,
publisher   VARCHAR(45) NOT NULL,
genre       VARCHAR(45) NOT NULL,
totalCopies INT DEFAULT 1,
PRIMARY KEY (isbn),
FOREIGN KEY (genre) REFERENCES genre (name)
);

-- Users Table
CREATE TABLE IF NOT EXISTS users(
userID   VARCHAR(45) NOT NULL,
fullName VARCHAR(45) NOT NULL,
email    VARCHAR(45) NOT NULL,
address  VARCHAR(45) NOT NULL,
password VARCHAR(255) NOT NULL,
isAdmin  BOOLEAN     NOT NULL DEFAULT FALSE,
PRIMARY KEY (userID)
);

-- Loans Table
CREATE TABLE IF NOT EXISTS loans(
loanID             INT         NOT NULL AUTO_INCREMENT,
isbn               VARCHAR(50) NOT NULL,
userID             VARCHAR(45) NOT NULL,
checkoutDate       DATE        NOT NULL,
expectedReturnDate DATE        NOT NULL,
returned           BOOLEAN,
PRIMARY KEY (loanID),
FOREIGN KEY (userID) REFERENCES users (userID) ON UPDATE CASCADE,
FOREIGN KEY (isbn) REFERENCES books (isbn) ON UPDATE CASCADE
);

-- Holds Table
CREATE TABLE IF NOT EXISTS holds(
isbn     VARCHAR(45) NOT NULL,
holdID   INT         NOT NULL AUTO_INCREMENT,
userID   VARCHAR(45) NOT NULL,
holdDate DATE        NOT NULL,
PRIMARY KEY (holdID),
FOREIGN KEY (userID) REFERENCES users (userID) ON UPDATE CASCADE,
FOREIGN KEY (isbn) REFERENCES books (isbn) ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS fines(
fineID        INT NOT NULL AUTO_INCREMENT,
loanID        INT NOT NULL,
userID        VARCHAR(45) NOT NULL,
fineAmount    DECIMAL(10,2) NOT NULL,
fineDate      DATE NOT NULL,
paymentStatus ENUM('Paid', 'Unpaid') NOT NULL,
paymentDate   DATE NULL,
PRIMARY KEY (fineID),
UNIQUE KEY unique_fine (loanID, userID),
FOREIGN KEY (loanID) REFERENCES loans(loanID) ON UPDATE CASCADE,
FOREIGN KEY (userID) REFERENCES users(userID) ON UPDATE CASCADE
);

-- Genre Entries (15+)
INSERT IGNORE INTO genre (name, category) VALUES ('Fantasy', 'Fiction');
INSERT IGNORE INTO genre (name, category) VALUES ('Science Fiction', 'Fiction');
INSERT IGNORE INTO genre (name, category) VALUES ('Mystery', 'Fiction');
INSERT IGNORE INTO genre (name, category) VALUES ('Romance', 'Fiction');
INSERT IGNORE INTO genre (name, category) VALUES ('Reference', 'Non-Fiction');
INSERT IGNORE INTO genre (name, category) VALUES ('Biography', 'Non-Fiction');
INSERT IGNORE INTO genre (name, category) VALUES ('Self-Help', 'Non-Fiction');
INSERT IGNORE INTO genre (name, category) VALUES ('Thriller', 'Fiction');
INSERT IGNORE INTO genre (name, category) VALUES ('Adventure', 'Fiction');
INSERT IGNORE INTO genre (name, category) VALUES ('Philosophy', 'Non-Fiction');
INSERT IGNORE INTO genre (name, category) VALUES ('History', 'Non-Fiction');
INSERT IGNORE INTO genre (name, category) VALUES ('Travel', 'Non-Fiction');
INSERT IGNORE INTO genre (name, category) VALUES ('Education', 'Non-Fiction');
INSERT IGNORE INTO genre (name, category) VALUES ('Poetry', 'Fiction');
INSERT IGNORE INTO genre (name, category) VALUES ('Drama', 'Fiction');

-- Books Entries (15+)
INSERT IGNORE INTO books (bookName, author, isbn, publisher, genre) VALUES ('Harry Potter and the Sorcerer''s Stone', 'J.K. Rowling', '978-0439708180', 'Scholastic', 'Fantasy');
INSERT IGNORE INTO books (bookName, author, isbn, publisher, genre) VALUES ('The Hobbit', 'J.R.R. Tolkien', '978-0345534835', 'Houghton Mifflin Harcourt', 'Fantasy');
INSERT IGNORE INTO books (bookName, author, isbn, publisher, genre) VALUES ('The Hunger Games', 'Suzanne Collins', '978-0439023481', 'Scholastic', 'Science Fiction');
INSERT IGNORE INTO books (bookName, author, isbn, publisher, genre) VALUES ('Divergent', 'Veronica Roth', '978-0062024039', 'Katherine Tegen Books', 'Science Fiction');
INSERT IGNORE INTO books (bookName, author, isbn, publisher, genre) VALUES ('The Da Vinci Code', 'Dan Brown', '978-0307474278', 'Anchor', 'Mystery');
INSERT IGNORE INTO books (bookName, author, isbn, publisher, genre) VALUES ('English Dictionary', 'Merriam-Webster', '978-0877792956', 'Merriam-Webster', 'Reference');
INSERT IGNORE INTO books (bookName, author, isbn, publisher, genre) VALUES ('Steve Jobs', 'Walter Isaacson', '978-1451648539', 'Simon & Schuster', 'Biography');
INSERT IGNORE INTO books (bookName, author, isbn, publisher, genre) VALUES ('The 7 Habits of Highly Effective People', 'Stephen R. Covey', '978-1982137274', 'Simon & Schuster', 'Self-Help');
INSERT IGNORE INTO books (bookName, author, isbn, publisher, genre) VALUES ('Inferno', 'Dan Brown', '978-1400079155', 'Anchor', 'Thriller');
INSERT IGNORE INTO books (bookName, author, isbn, publisher, genre) VALUES ('Into the Wild', 'Jon Krakauer', '978-0385486804', 'Anchor', 'Travel');
INSERT IGNORE INTO books (bookName, author, isbn, publisher, genre) VALUES ('The Alchemist', 'Paulo Coelho', '978-0061122415', 'HarperOne', 'Philosophy');
INSERT IGNORE INTO books (bookName, author, isbn, publisher, genre) VALUES ('Educated', 'Tara Westover', '978-0399590504', 'Random House', 'Biography');
INSERT IGNORE INTO books (bookName, author, isbn, publisher, genre) VALUES ('1984', 'George Orwell', '978-0451524935', 'Signet Classics', 'Science Fiction');
INSERT IGNORE INTO books (bookName, author, isbn, publisher, genre) VALUES ('Hamlet', 'William Shakespeare', '978-0743477123', 'Simon & Schuster', 'Drama');
INSERT IGNORE INTO books (bookName, author, isbn, publisher, genre) VALUES ('Leaves of Grass', 'Walt Whitman', '978-0486456768', 'Dover', 'Poetry');

-- Users Entries (15+)
INSERT IGNORE INTO users (fullName, email, address, userID, password) VALUES ('dummy user', 'only to satisfy', 'foreign key constraint', 'dummy', 'dummyPass');
INSERT IGNORE INTO users (fullName, email, address, userID, password) VALUES ('John Doe', 'john@doe.com', '123 Main St', 'johndoe', 'hashedpass1');
INSERT IGNORE INTO users (fullName, email, address, userID, password) VALUES ('Jane Doe', 'jane@doe.com', '123 First St', 'janedoe', 'hashedpass2');
INSERT IGNORE INTO users (fullName, email, address, userID, password) VALUES ('Bob Smith', 'bob@smith.com', '123 Second St', 'bobsmith', 'hashedpass3');
INSERT IGNORE INTO users (fullName, email, address, userID, password) VALUES ('James Stewart', 'james@stewart.com', '123 Third St', 'jstewart', 'hashedpass4');
INSERT IGNORE INTO users (fullName, email, address, userID, password, isAdmin) VALUES ('admin man', 'admin@admin.com', 'admin st', 'admin', 'adminpass', TRUE);
INSERT IGNORE INTO users (fullName, email, address, userID, password) VALUES ('Alice Johnson', 'alice@johnson.com', '234 West Ave', 'alicej', 'hashedpass5');
INSERT IGNORE INTO users (fullName, email, address, userID, password) VALUES ('Charlie Brown', 'charlie@brown.com', '567 Oak St', 'cbrown', 'hashedpass6');
INSERT IGNORE INTO users (fullName, email, address, userID, password) VALUES ('David Lee', 'david@lee.com', '789 Pine St', 'dlee', 'hashedpass7');
INSERT IGNORE INTO users (fullName, email, address, userID, password) VALUES ('Eva Green', 'eva@green.com', '321 Cedar St', 'egreen', 'hashedpass8');
INSERT IGNORE INTO users (fullName, email, address, userID, password) VALUES ('Frank White', 'frank@white.com', '654 Birch St', 'fwhite', 'hashedpass9');
INSERT IGNORE INTO users (fullName, email, address, userID, password) VALUES ('Grace Park', 'grace@park.com', '987 Willow St', 'gpark', 'hashedpass10');
INSERT IGNORE INTO users (fullName, email, address, userID, password) VALUES ('Helen Kim', 'helen@kim.com', '246 Elm St', 'hkim', 'hashedpass11');
INSERT IGNORE INTO users (fullName, email, address, userID, password) VALUES ('Ian Thomas', 'ian@thomas.com', '135 Spruce St', 'ithomas', 'hashedpass12');
INSERT IGNORE INTO users (fullName, email, address, userID, password) VALUES ('Julie Clark', 'julie@clark.com', '864 Maple St', 'jclark', 'hashedpass13');

-- Loans Entries (15+)
INSERT IGNORE INTO loans (isbn, userID, checkoutDate, expectedReturnDate, returned) VALUES ('978-0439708180', 'johndoe', '2021-01-01', '2021-01-15', FALSE);
INSERT IGNORE INTO loans (isbn, userID, checkoutDate, expectedReturnDate, returned) VALUES ('978-0345534835', 'janedoe', '2021-01-01', '2021-01-15', FALSE);
INSERT IGNORE INTO loans (isbn, userID, checkoutDate, expectedReturnDate, returned) VALUES ('978-0439023481', 'bobsmith', '2021-01-01', '2021-01-15', FALSE);
INSERT IGNORE INTO loans (isbn, userID, checkoutDate, expectedReturnDate, returned) VALUES ('978-0062024039', 'jstewart', '2021-01-01', '2021-01-15', FALSE);
INSERT IGNORE INTO loans (isbn, userID, checkoutDate, expectedReturnDate, returned) VALUES ('978-0307474278', 'johndoe', '2021-01-01', '2021-01-15', TRUE);
INSERT IGNORE INTO loans (isbn, userID, checkoutDate, expectedReturnDate, returned) VALUES ('978-1400079155', 'alicej', '2024-04-10', '2024-04-24', FALSE);
INSERT IGNORE INTO loans (isbn, userID, checkoutDate, expectedReturnDate, returned) VALUES ('978-0385486804', 'cbrown', '2024-04-11', '2024-04-25', FALSE);
INSERT IGNORE INTO loans (isbn, userID, checkoutDate, expectedReturnDate, returned) VALUES ('978-0061122415', 'dlee', '2024-04-12', '2024-04-26', FALSE);
INSERT IGNORE INTO loans (isbn, userID, checkoutDate, expectedReturnDate, returned) VALUES ('978-0399590504', 'egreen', '2024-04-13', '2024-04-27', FALSE);
INSERT IGNORE INTO loans (isbn, userID, checkoutDate, expectedReturnDate, returned) VALUES ('978-0451524935', 'fwhite', '2024-04-14', '2024-04-28', FALSE);
INSERT IGNORE INTO loans (isbn, userID, checkoutDate, expectedReturnDate, returned) VALUES ('978-0743477123', 'gpark', '2024-04-15', '2024-04-29', FALSE);
INSERT IGNORE INTO loans (isbn, userID, checkoutDate, expectedReturnDate, returned) VALUES ('978-0486456768', 'hkim', '2024-04-16', '2024-04-30', FALSE);
INSERT IGNORE INTO loans (isbn, userID, checkoutDate, expectedReturnDate, returned) VALUES ('978-0877792956', 'ithomas', '2024-04-17', '2024-05-01', FALSE);
INSERT IGNORE INTO loans (isbn, userID, checkoutDate, expectedReturnDate, returned) VALUES ('978-1451648539', 'jclark', '2024-04-18', '2024-05-02', FALSE);
INSERT IGNORE INTO loans (isbn, userID, checkoutDate, expectedReturnDate, returned) VALUES ('978-1982137274', 'janedoe', '2024-04-19', '2024-05-03', FALSE);

-- Holds Entries (15+)
INSERT IGNORE INTO holds (isbn, userID, holdDate) VALUES ('978-0877792956', 'johndoe', '2024-04-01');
INSERT IGNORE INTO holds (isbn, userID, holdDate) VALUES ('978-1451648539', 'janedoe', '2024-04-16');
INSERT IGNORE INTO holds (isbn, userID, holdDate) VALUES ('978-1400079155', 'alicej', '2024-04-02');
INSERT IGNORE INTO holds (isbn, userID, holdDate) VALUES ('978-0385486804', 'cbrown', '2024-04-03');
INSERT IGNORE INTO holds (isbn, userID, holdDate) VALUES ('978-0061122415', 'dlee', '2024-04-04');
INSERT IGNORE INTO holds (isbn, userID, holdDate) VALUES ('978-0399590504', 'egreen', '2024-04-05');
INSERT IGNORE INTO holds (isbn, userID, holdDate) VALUES ('978-0451524935', 'fwhite', '2024-04-06');
INSERT IGNORE INTO holds (isbn, userID, holdDate) VALUES ('978-0743477123', 'gpark', '2024-04-07');
INSERT IGNORE INTO holds (isbn, userID, holdDate) VALUES ('978-0486456768', 'hkim', '2024-04-08');
INSERT IGNORE INTO holds (isbn, userID, holdDate) VALUES ('978-0439023481', 'bobsmith', '2024-04-09');
INSERT IGNORE INTO holds (isbn, userID, holdDate) VALUES ('978-0345534835', 'janedoe', '2024-04-10');
INSERT IGNORE INTO holds (isbn, userID, holdDate) VALUES ('978-0307474278', 'johndoe', '2024-04-11');
INSERT IGNORE INTO holds (isbn, userID, holdDate) VALUES ('978-0062024039', 'jstewart', '2024-04-12');
INSERT IGNORE INTO holds (isbn, userID, holdDate) VALUES ('978-1982137274', 'janedoe', '2024-04-13');
INSERT IGNORE INTO holds (isbn, userID, holdDate) VALUES ('978-0439708180', 'ithomas', '2024-04-14');

-- Fines Entries (15+)
INSERT IGNORE INTO fines (loanID, userID, fineAmount, fineDate, paymentStatus, paymentDate) VALUES (1, 'johndoe', 5.00, '2021-01-20', 'Paid', '2021-01-22');
INSERT IGNORE INTO fines (loanID, userID, fineAmount, fineDate, paymentStatus, paymentDate) VALUES (2, 'janedoe', 3.50, '2021-01-21', 'Unpaid', NULL);
INSERT IGNORE INTO fines (loanID, userID, fineAmount, fineDate, paymentStatus, paymentDate) VALUES (3, 'bobsmith', 7.00, '2021-01-22', 'Paid', '2021-01-25');
INSERT IGNORE INTO fines (loanID, userID, fineAmount, fineDate, paymentStatus, paymentDate) VALUES (4, 'jstewart', 2.25, '2021-01-23', 'Paid', '2021-01-24');
INSERT IGNORE INTO fines (loanID, userID, fineAmount, fineDate, paymentStatus, paymentDate) VALUES (5, 'johndoe', 4.75, '2021-01-24', 'Unpaid', NULL);
INSERT IGNORE INTO fines (loanID, userID, fineAmount, fineDate, paymentStatus, paymentDate) VALUES (6, 'alicej', 3.00, '2024-04-20', 'Paid', '2024-04-21');
INSERT IGNORE INTO fines (loanID, userID, fineAmount, fineDate, paymentStatus, paymentDate) VALUES (7, 'cbrown', 6.00, '2024-04-21', 'Unpaid', NULL);
INSERT IGNORE INTO fines (loanID, userID, fineAmount, fineDate, paymentStatus, paymentDate) VALUES (8, 'dlee', 4.00, '2024-04-22', 'Paid', '2024-04-23');
INSERT IGNORE INTO fines (loanID, userID, fineAmount, fineDate, paymentStatus, paymentDate) VALUES (9, 'egreen', 2.50, '2024-04-23', 'Paid', '2024-04-24');
INSERT IGNORE INTO fines (loanID, userID, fineAmount, fineDate, paymentStatus, paymentDate) VALUES (10, 'fwhite', 5.25, '2024-04-24', 'Unpaid', NULL);
INSERT IGNORE INTO fines (loanID, userID, fineAmount, fineDate, paymentStatus, paymentDate) VALUES (11, 'gpark', 6.75, '2024-04-25', 'Paid', '2024-04-26');
INSERT IGNORE INTO fines (loanID, userID, fineAmount, fineDate, paymentStatus, paymentDate) VALUES (12, 'hkim', 1.00, '2024-04-26', 'Paid', '2024-04-27');
INSERT IGNORE INTO fines (loanID, userID, fineAmount, fineDate, paymentStatus, paymentDate) VALUES (13, 'ithomas', 3.75, '2024-04-27', 'Unpaid', NULL);
INSERT IGNORE INTO fines (loanID, userID, fineAmount, fineDate, paymentStatus, paymentDate) VALUES (14, 'jclark', 2.80, '2024-04-28', 'Paid', '2024-04-29');
INSERT IGNORE INTO fines (loanID, userID, fineAmount, fineDate, paymentStatus, paymentDate) VALUES (15, 'janedoe', 4.40, '2024-04-29', 'Unpaid', NULL);