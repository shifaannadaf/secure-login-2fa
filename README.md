# Secure Login with 2FA (Java Swing)

A Java Swing-based desktop application demonstrating **username/password login** with **Time-based One-Time Password (TOTP) Two-Factor Authentication (2FA)** compatible with **Google Authenticator**.  

The project is designed with **clean separation of UI and authentication logic**, making it **extensible for server-side integration** (e.g., Apache, REST API).

---

## Features

- **Username & Password Login**
  - Demo credentials:
    - Username: `admin`
    - Password: `password123`

- **Two-Factor Authentication (TOTP)**
  - Uses **RFC 6238-compliant TOTP algorithm**
  - Compatible with **Google Authenticator**
  - Secret: `JBSWY3DPEHPK3PXP`
  - Clock-skew tolerance: ±30 seconds

- **Order Management System**
  - Accessible after successful 2FA
  - View, add, delete, and refresh orders
  - Swing `JTable` interface for demonstration

- **Clean Architecture**
  - `LoginFrame.java` → Login UI
  - `TwoFAFrame.java` → 2FA verification UI
  - `OTPServer.java` → TOTP generation & verification logic
  - `OrdersFrame.java` → Post-login order management UI

---

## Technologies & Dependencies

- **Java 11**
- **Swing** for desktop GUI
- **Apache Commons Codec** for Base32 decoding
  ```xml
  <dependency>
      <groupId>commons-codec</groupId>
      <artifactId>commons-codec</artifactId>
      <version>1.15</version>
  </dependency>

---

## Project Structure

secure-login-2fa/
│
├─ pom.xml                 # Maven dependencies & build
├─ src/main/java/
│   ├─ LoginFrame.java     # Login window
│   ├─ TwoFAFrame.java     # 2FA verification window
│   ├─ OTPServer.java      # TOTP generation & verification logic
│   └─ OrdersFrame.java    # Post-login order management GUI


---

## How to run locally

- **Build with Maven**
  - mvn clean compile
- **Run the Application**
  - mvn exec:java -Dexec.mainClass="LoginFrame"
- **Login with demo credentials:**
  - Username: admin
  - Password: password123
- **Scan QR code in 2FA step:**
  - Secret: JBSWY3DPEHPK3PXP
  - Use Google Authenticator or similar TOTP app
- **Enter TOTP code to access the Order Management System.**

 ---

## Architecture & Design

+-------------------+       +-------------------+
|    LoginFrame     | ----> |   TwoFAFrame      |
| (username/password)|      | (TOTP verification)|
+-------------------+       +-------------------+
                                   |
                                   v
                           +-------------------+
                           |    OTPServer      |
                           | (RFC 6238 TOTP)  |
                           +-------------------+
                                   |
                                   v
                           +-------------------+
                           |   OrdersFrame     |
                           | (Demo UI System)  |
                           +-------------------+



---

## Author

Shifaa Nadaf

Email: snadaf@hawk.illinoistech.edu

LinkedIn: linkedin.com/in/shifaannadaf

