# # 🚆 Train Ticket Reservation System

A console/desktop-based Train Ticket Reservation System built using **Java** and **JDBC (Java Database Connectivity)**. This project demonstrates core **CRUD (Create, Read, Update, Delete)** operations to manage train schedules, passenger bookings, PNR tracking, and ticket cancellations.

---

## ✨ Features

* **User Authentication:** Secure login/registration system for passengers and administrators.
* **Train Management (Admin):** Add new trains, update train routes/timings, and remove inactive schedules.
* **Ticket Booking (Create):** Search available trains and book tickets with automatic PNR generation.
* **View Bookings & Availability (Read):** Check seat availability, view train lists, and retrieve ticket details using a PNR number.
* **Modify Passenger Details (Update):** Update passenger information or booking status in the database.
* **Ticket Cancellation (Delete):** Cancel booked tickets using the PNR number and automatically release seats.

---

## 🛠️ Tech Stack

* **Language:** Java (JDK 8 or higher)
* **Database Connectivity:** JDBC
* **Database:** MySQL / Oracle SQL *(update this to whichever database you used)*
* **IDE:** VS Code / IntelliJ IDEA / Eclipse

---


    source_station VARCHAR(50),
    destination_station VARCHAR(50)
);
