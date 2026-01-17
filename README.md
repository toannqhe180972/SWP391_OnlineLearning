# Online Learning Platform (SWP391)

A robust Spring Boot application for online learning, featuring advanced user management, Google OAuth2 integration, and automated email notifications.

## Features

### 🔐 Authentication & Security
- **Local Login/Register**: Traditional username/email and password authentication.
- **Google OAuth2**: One-click sign-in with Google.
- **Auto-Registration**: New Google users are automatically registered with generated credentials.
- **Account Linking**: Existing local accounts can be linked to Google profiles.
- **Spring Security**: Role-based access control (RBAC) with `ROLE_ADMIN` and `ROLE_USER`.

### 👤 User Management
- **Extended Profiles**: Detailed user information including First Name, Last Name, Phone, Address, and Avatar.
- **Data Validation**: Comprehensive input validation using Jakarta Validation.
- **Profile Updates**: Secure profile editing and password change functionality.

### 📧 Email Service
- **Automated Notifications**: HTML-based emails using Thymeleaf templates.
- **Scenarios**: 
  - Welcome email for new Google registrations (includes generated credentials).
  - Account linking confirmation.
  - Security alerts for password changes.

## Tech Stack
- **Backend**: Spring Boot 3.4.1, Spring Data JPA, Spring Security, OAuth2 Client.
- **Database**: MySQL.
- **View Engine**: Thymeleaf.
- **Styling**: Vanilla CSS (Modern aesthetic).
- **Communication**: Spring Mail (SMTP).

## Setup Instructions

### Pre-requisites
- Java 17+
- MySQL 8+
- Maven

### Configuration
Update `src/main/resources/application.properties` with your credentials:

```properties
# Database
spring.datasource.username=your_username
spring.datasource.password=your_password

# Google OAuth2
spring.security.oauth2.client.registration.google.client-id=YOUR_CLIENT_ID
spring.security.oauth2.client.registration.google.client-secret=YOUR_CLIENT_SECRET

# Email (SMTP)
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password
```

### Running the App
```bash
mvn clean install
mvn spring-boot:run
```
The application will be available at `http://localhost:8080`.

## License
MIT License
