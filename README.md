# Java Banking System

A desktop-based banking application built using Core Java, JavaFX, and MySQL.
This system provides essential banking functionalities such as account management, transactions, and secure login.

---

## Features

* User Authentication – Secure login system
* Deposit and Withdraw – Perform banking transactions
* Fund Transfer – Transfer money between accounts
* Transaction Feedback – Instant success/failure messages
* Database Integration – Data stored using MySQL
* JavaFX UI – Interactive graphical interface

---

## Prerequisites

* Java JDK 11 or higher installed
* JavaFX SDK (e.g., OpenJFX 26)
* MySQL Server installed and running
* MySQL Connector J (`mysql-connector-j-9.6.0.jar`)

---

## How to Run

### 1. Compile

javac -cp ".;lib/mysql-connector-j-9.6.0.jar" --module-path "PATH_TO_JAVAFX/lib" --add-modules javafx.controls *.java

### 2. Run

java -cp ".;lib/mysql-connector-j-9.6.0.jar" --module-path "PATH_TO_JAVAFX/lib" --add-modules javafx.controls LoginApp

---

## Usage

1. Launch the application
2. Enter login credentials
3. Access dashboard features:

   * Deposit money
   * Withdraw money
   * Transfer funds
4. View transaction status messages

---

## Project Structure

banking/
├── Account.java
├── BankService.java
├── DBConnection.java
├── LoginApp.java
├── Main.java
│
├── lib/
│   └── mysql-connector-j-9.6.0.jar
│
├── .gitignore
└── README.md

---

## Technologies Used

* Java SE (Core Java)
* JavaFX
* MySQL
* JDBC

