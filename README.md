# Secure Login with 2FA & Order Management System  
**Java Swing · MySQL · AWS S3 (Archival)**

A Java Swing–based desktop application demonstrating **secure authentication using username/password + Time-based One-Time Password (TOTP) Two-Factor Authentication (2FA)**, followed by an **Order Management System backed by MySQL and AWS S3 (JSON archival)**.

This project showcases **security fundamentals**, **database integration**, and **cloud-aware design**, implemented as a lightweight desktop application.

---

## 🔐 Features

### Authentication
- **Username & Password Login**
  - Demo credentials:
    - **Username:** `admin`
    - **Password:** `password123`

- **Two-Factor Authentication (TOTP)**
  - RFC 6238–compliant TOTP implementation
  - Compatible with **Google Authenticator**
  - Shared secret: `JBSWY3DPEHPK3PXP`
  - 30-second time window

---

### 📦 Order Management System
Accessible **only after successful 2FA authentication**.

- View all orders in a Swing `JTable`
- Add new orders
- Delete selected orders
- Refresh orders from database
- **Search orders by:**
  - Order ID
  - Order Date (YYYY-MM-DD)

---

### 🗄️ Data Storage
- **MySQL (Transactional Database)**
  - Stores live order data
  - Supports insert, delete, and query operations
- **AWS S3 (JSON Archival)**
  - Orders serialized to JSON using Jackson
  - Designed for scalable, immutable archival
  - S3 integration implemented and **disabled by default** for local execution

---

## 🧱 Architecture Overview

```
+-------------------+
| LoginFrame |
| (Username/Password)
+-------------------+
|
v
+-------------------+
| TwoFAFrame |
| (TOTP Verification)
+-------------------+
|
v
+-------------------+
| OrdersFrame |
| (Order Management)
+-------------------+
| |
v v
+---------+ +------------------+
| MySQL | | AWS S3 (JSON) |
| Live DB | | Archival Storage |
+---------+ +------------------+
```

---

## 🛠️ Technologies & Dependencies

- **Java 11**
- **Java Swing** (GUI)
- **Apache Commons Codec** (Base32 decoding for TOTP)
- **MySQL** (JDBC)
- **AWS SDK v2** (S3)
- **Jackson Databind** (JSON serialization)

### Maven Dependencies

```xml
<dependencies>
    <dependency>
        <groupId>commons-codec</groupId>
        <artifactId>commons-codec</artifactId>
        <version>1.15</version>
    </dependency>

    <dependency>
        <groupId>mysql</groupId>
        <artifactId>mysql-connector-j</artifactId>
        <version>8.4.0</version>
    </dependency>

    <dependency>
        <groupId>software.amazon.awssdk</groupId>
        <artifactId>s3</artifactId>
        <version>2.20.0</version>
    </dependency>

    <dependency>
        <groupId>com.fasterxml.jackson.core</groupId>
        <artifactId>jackson-databind</artifactId>
        <version>2.16.0</version>
    </dependency>
</dependencies>
```

---

## Project Structure
```
secure-login-2fa-orders/
├─ pom.xml
└─ src/
   └─ main/
      └─ java/
         ├─ LoginFrame.java       # Username/password login UI
         ├─ TwoFAFrame.java       # TOTP verification UI
         ├─ Order.java            # Order model (POJO)
         ├─ OrderStatus.java      # Order lifecycle enum
         └─ OrdersFrame.java      # Order management UI
```
## 🗄️ Database Setup (MySQL)
```
CREATE DATABASE orders_db;
USE orders_db;

CREATE TABLE orders (
    id VARCHAR(50) PRIMARY KEY,
    date DATETIME,
    customer VARCHAR(100),
    items JSON,
    status VARCHAR(20)
);
```
## ▶️ How to Run Locally

- **1. Start MySQL**
   - brew services start mysql

- **2. Build the project**
   - mvn clean compile

- **3. Run the application**
   - mvn exec:java -Dexec.mainClass="LoginFrame"

- **4. Login**

   - Username: admin

   - Password: password123

- **5. Complete 2FA**

   - Add secret JBSWY3DPEHPK3PXP to Google Authenticator

   - Enter the generated 6-digit TOTP code

- **6. Use the Order Management System**

   - Add, delete, refresh, and search orders

   - Orders persist in MySQL

## ☁️ AWS S3 Notes

AWS S3 integration is implemented for JSON archival

Disabled by default for local execution:

private static final boolean ENABLE_S3 = false;


- **To enable S3:**

   - Create an S3 bucket

   - Configure AWS credentials locally

   - Set ENABLE_S3 = true

🎓Academic Notes

- Demonstrates secure authentication with 2FA

- Shows transactional vs archival data separation

- Uses industry-standard libraries

- Designed for extensibility to web or cloud deployment

## 👤 Author

Shifaa Nadaf
MS Computer Science – Illinois Institute of Technology

📧 Email: snadaf@hawk.illinoistech.edu

🔗 LinkedIn: https://www.linkedin.com/in/shifaannadaf/
