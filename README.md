# 📚 BookWise - Library Management System

## 📦 How to Build & Run the Project

### 1. Install Java 21
- Download and install Java 21 from the official [Java Download Page](https://www.oracle.com/java/technologies/javase/jdk21-archive-downloads.html).
- Make sure `java` is available in your system's `PATH`.

### 2. Install & Configure MySQL
- Install MySQL and start the server.
- Ensure it's running on `localhost` at port `3306`.
- Use the MySQL `root` user for access.

### 3. Set Database Password (if applicable)
If your MySQL `root` user has a password, set it as an environment variable:

```bash
export DB_PASSWORD=your_password
```

### 4. Run the Project
Use the following command to build and run the project:

```bash
./gradlew run
```

All necessary SQL commands for setting up the BookWise database are included in a single script file named BookWise.sql.

This file combines the database schema creation (create_schema.sql) and the data initialization (initialize_data.sql) into one convenient script.

It will:

Create all required tables with appropriate constraints.

Populate each table with at least 15 entries, with no NULL values.
