# 🎬 Movie Booking System

A full-stack Movie Ticket Booking Platform built using Spring Boot, React.js, MySQL, JWT Authentication, QR Code Generation, PDF Ticket Download, and Email Notifications.

This application allows users to browse movies, select shows, book seats, receive booking confirmations, generate QR-code tickets, and download tickets as PDF files. Administrators can manage movies, shows, users, and monitor booking analytics.

---

# 🚀 Features

## 👤 User Features

* User Registration
* User Login
* JWT Authentication
* Forgot Password
* Reset Password via Email
* Browse Movies
* View Shows
* View Available Seats
* Book Tickets
* Cancel Bookings
* View Booking History
* QR Code Ticket Generation
* PDF Ticket Download
* Booking Confirmation Email

---

## 👨‍💼 Admin Features

* Admin Login
* Manage Movies
* Add Movies
* Update Movies
* Delete Movies
* Create Shows
* Update Shows
* Delete Shows
* User Management
* Revenue Dashboard
* Booking Analytics

---

# 🛠 Technology Stack

## Backend

* Java 17
* Spring Boot
* Spring Security
* Spring Data JPA
* Hibernate
* JWT Authentication
* Java Mail Sender
* ZXing QR Code Library
* iText PDF
* Maven

## Frontend

* React.js
* Axios
* React Router
* Tailwind CSS

## Database

* MySQL

## Tools

* Git
* GitHub
* Postman
* VS Code
* Eclipse / IntelliJ IDEA

---

# 🏗 System Architecture

```text
React Frontend
       │
       ▼
Spring Boot REST APIs
       │
       ▼
Spring Security + JWT
       │
       ▼
Service Layer
       │
       ▼
Repository Layer
       │
       ▼
MySQL Database
```

---

# 🔐 Authentication Flow

1. User registers.
2. User logs in.
3. JWT token is generated.
4. Token is stored on frontend.
5. Every protected request sends JWT token.
6. Spring Security validates the token.
7. Authorized resources are returned.

---

# 🎟 Booking Workflow

1. User selects movie.
2. User selects show.
3. User selects seat.
4. Booking is created.
5. Payment record is generated.
6. QR code is generated.
7. Confirmation email is sent.
8. Ticket PDF becomes available.
9. User can download ticket anytime.

---

# 📂 Project Structure

```text
movie-booking-system
│
├── src
│   ├── main
│   │   ├── java
│   │   │   ├── config
│   │   │   ├── controller
│   │   │   ├── dto
│   │   │   ├── entity
│   │   │   ├── enums
│   │   │   ├── exception
│   │   │   ├── repository
│   │   │   ├── security
│   │   │   └── service
│   │   │
│   │   └── resources
│   │       └── application.properties
│
├── uploads
│   ├── posters
│   └── qrcodes
│
├── pom.xml
├── README.md
└── .gitignore
```

---

# ⚙️ Installation & Setup

## Prerequisites

Install the following software:

* Java 17+
* Maven 3.9+
* MySQL 8+
* Node.js 18+
* Git

---

## Step 1: Clone Repository

```bash
git clone https://github.com/Arthibalamurugan/movie-booking-system.git

cd movie-booking-system
```

---

## Step 2: Create Database

Login to MySQL and execute:

```sql
CREATE DATABASE movie_booking_db;
```

Verify:

```sql
SHOW DATABASES;
```

---

## Step 3: Configure Database

Open:

```text
src/main/resources/application.properties
```

Update:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/movie_booking_db

spring.datasource.username=root

spring.datasource.password=your_password

spring.jpa.hibernate.ddl-auto=update

spring.jpa.show-sql=true
```

---

## Step 4: Configure Email Service

Update Gmail SMTP settings:

```properties
spring.mail.host=smtp.gmail.com

spring.mail.port=587

spring.mail.username=your_email@gmail.com

spring.mail.password=your_app_password

spring.mail.properties.mail.smtp.auth=true

spring.mail.properties.mail.smtp.starttls.enable=true
```

### Gmail Setup

1. Enable Two-Step Verification.
2. Generate App Password.
3. Use App Password instead of Gmail password.

---

## Step 5: Install Dependencies

```bash
mvnd clean install
```

or

```bash
mvn clean install
```

---

## Step 6: Run Backend

```bash
mvnd spring-boot:run
```

or

```bash
mvn spring-boot:run
```

Backend URL:

```text
http://localhost:8080
```

---

## Step 7: Run Frontend

Navigate to React project:

```bash
cd booking-frontend
```

Install dependencies:

```bash
npm install
```

Start application:

```bash
npm start
```

Frontend URL:

```text
http://localhost:3000
```

---

# 🔌 API Endpoints

## Authentication

```http
POST /api/auth/register

POST /api/auth/login

POST /api/auth/forgot-password

POST /api/auth/reset-password
```

---

## Movies

```http
GET /api/movies

GET /api/movies/{id}

POST /api/admin/movies

PUT /api/admin/movies/{id}

DELETE /api/admin/movies/{id}
```

---

## Shows

```http
GET /api/shows

GET /api/shows/{id}

POST /api/admin/shows

PUT /api/admin/shows/{id}

DELETE /api/admin/shows/{id}
```

---

## Bookings

```http
POST /api/bookings

PUT /api/bookings/{id}/cancel

GET /api/bookings/my
```

---

# 📧 Email Notifications

The system automatically sends:

* Booking Confirmation Email
* Password Reset Email

---

# 🔲 QR Code Ticketing

Every booking generates:

* Unique QR Code
* Booking Information
* Ticket Verification Data

Users can view QR codes from the booking history page.

---

# 📄 PDF Ticket Download

Generated ticket contains:

* Booking ID
* Movie Name
* Theater Name
* Show Time
* Seat Number
* Payment Information
* QR Code

---

# 📊 Admin Analytics

Dashboard includes:

* Total Users
* Total Movies
* Total Bookings
* Revenue Tracking
* Booking Statistics

---

# 🔮 Future Enhancements

* Razorpay Payment Gateway
* Docker Deployment
* AWS Deployment
* Real-Time Seat Locking
* SMS Notifications
* Advanced Analytics Dashboard
* Booking Reports

---

# 👨‍💻 Author

**Arthi B**

B.E Computer Science Engineering

Final Year Student

Interested in:

* Java Backend Development
* Spring Boot
* Full Stack Development
* Software Engineering

---

# ⭐ If you like this project

Give this repository a star and support the project.
