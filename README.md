# 💳 E-Wallet Application

A full-stack e-wallet system built using **Spring Boot** microservices (Java) and a modern **HTML/CSS/JavaScript** frontend. It supports secure authentication, wallet balance management, money transfers, top-ups, and transaction history — all in one seamless dashboard.

---

## 🚀 Features

- 🔐 **JWT-Based User Authentication**
- 💼 **Wallet Management**
  - View wallet balance (with hide/show toggle)
  - Top-up wallet
- 💸 **Money Transfer**
  - Transfer funds between users securely
- 📜 **Transaction History**
  - View all past transactions
- 🌐 **Frontend**
  - Clean, responsive UI with dynamic JS functionality (no framework)

---

## 🛠️ Tech Stack

| Layer      | Technology                          |
|------------|-------------------------------------|
| Frontend   | HTML, CSS, JavaScript               |
| Backend    | Java, Spring Boot                   |
| Auth       | JWT (JSON Web Tokens)               |
| Database   | MySQL                               |
| Build Tool | Maven                               |

---

## 📁 Project Structure
E-WALLET-PROJECT/
├── User-auth-service/ # Auth microservice
├── Wallet-Service/ # Wallet microservice
├── TransactionService/ # Transaction microservice
└── Frontend/ # Static HTML/CSS/JS frontend


---

## 🔧 How to Run

### 1. Clone the Repository

```bash
git clone https://github.com/githubusername/E-WALLET-APP.git
cd E-WALLET-APP

## Each service is a Spring Boot app. You can run them using:

# Start Auth Service
cd User-auth-service
./mvnw spring-boot:run

# Start Wallet Service
cd ../Wallet-Service
./mvnw spring-boot:run

# Start Transaction Service
cd ../TransactionService
./mvnw spring-boot:run



Ports:

User-auth-service → 8082

Wallet-Service → 8083

TransactionService → 8084


🧠 Learning Objectives
✅ Understand microservices architecture using Spring Boot
✅ Implement secure APIs using JWT authentication
✅ Integrate frontend and backend via REST APIs
✅ Build a clean and professional UI without React or Tailwind

👨‍💻 Author
Sohail Javed Sheikh



