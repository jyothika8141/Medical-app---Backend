# 🏥 HealthConnect - Complete Medical Platform Backend

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![AI Powered](https://img.shields.io/badge/AI-Google%20Gemini-blue.svg)](https://ai.google.dev/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

## 🌟 Overview

HealthConnect is a comprehensive, production-ready medical platform backend built with Spring Boot. It provides a complete healthcare ecosystem with user management, appointment scheduling, real-time messaging, video consultations, and AI-powered health assistance.

## ✨ Key Features

### 🔐 **User Management**
- Patient and Doctor registration/authentication
- JWT-based security
- Role-based access control
- Profile management with avatars

### 📅 **Appointment System**
- Complete booking and scheduling
- Video call and in-person appointments
- Time slot availability checking
- Appointment status management

### 💬 **Real-time Messaging**
- Chat between patients and doctors
- Message read status tracking
- Conversation history
- Real-time communication

### 🎥 **Video Call Integration**
- Video consultation appointments
- Meeting link generation
- Recording start/stop functionality
- Recording URL management

### 🤖 **AI Health Bot**
- **Google Gemini AI Integration**
- Intelligent health conversations
- Symptom analysis and recommendations
- Emergency response guidance
- Conversation sharing with doctors

## 🏗️ Architecture

### **Technology Stack**
- **Framework:** Spring Boot 3.4.5
- **Language:** Java 17
- **Database:** H2 (Development) / PostgreSQL (Production Ready)
- **Security:** JWT Authentication
- **AI Integration:** Google Gemini API
- **Build Tool:** Maven
- **ORM:** JPA/Hibernate

### **Database Schema**
```
📊 6 Tables with Complete Relationships:
├── users (User accounts with roles)
├── doctor_details (Doctor-specific information)
├── appointments (Enhanced with meeting links)
├── chats (User conversations)
├── messages (Chat messages with read status)
└── video_call_recordings (Video call management)
```

## 🚀 Quick Start

### **Prerequisites**
- Java 17 or higher
- Maven 3.6+
- Git

### **Installation**

1. **Clone the repository**
```bash
git clone https://github.com/anudeep2710/Medical-app---Backend.git
cd Medical-app---Backend
```

2. **Configure Google Gemini AI (Optional)**
```properties
# Update src/main/resources/application.properties
google.ai.api.key=YOUR_GEMINI_API_KEY
```

3. **Run the application**
```bash
./mvnw spring-boot:run
```

4. **Access the application**
- **API Base URL:** `http://localhost:8081`
- **H2 Console:** `http://localhost:8081/h2-console`
- **Database URL:** `jdbc:h2:mem:medical_app`

## 📚 API Documentation

### **🔑 Authentication Endpoints**
```
POST /api/users/register     - Register new user
POST /api/auth/login         - User login
```

### **👤 User Management**
```
GET  /api/users/me           - Get current user profile
PUT  /api/users/me           - Update user profile
PUT  /api/users/me/password  - Update password
```

### **📅 Appointment Management**
```
POST   /api/appointments     - Create appointment
GET    /api/appointments     - Get user appointments
GET    /api/appointments/{id} - Get appointment by ID
PUT    /api/appointments/{id} - Update appointment
DELETE /api/appointments/{id} - Cancel appointment
```

### **👨‍⚕️ Doctor Management**
```
GET /api/doctors                        - Get all doctors
GET /api/doctors/{id}                   - Get doctor by ID
GET /api/doctors/{doctorId}/time-slots  - Get available time slots
GET /api/doctors/me/patients            - Get doctor's patients
```

### **🏥 Patient Management**
```
GET /api/patients/{id}        - Get patient by ID
GET /api/patients/me/doctors  - Get patient's doctors
```

### **💬 Chat & Messaging**
```
GET  /api/chats                    - Get chat list
GET  /api/chats/{chatId}/messages  - Get chat messages
POST /api/chats/{chatId}/messages  - Send message
PUT  /api/chats/{chatId}/read      - Mark messages as read
POST /api/chats/with-user/{userId} - Create/get chat with user
```

### **🤖 AI Health Bot**
```
POST /api/health-bot/message           - Send message to AI bot
POST /api/health-bot/analyze           - Get health analysis
POST /api/health-bot/share/{doctorId}  - Share conversation with doctor
```

### **🎥 Video Call API**
```
GET  /api/video-calls/appointment/{appointmentId}        - Get video call details
POST /api/video-calls/appointment/{appointmentId}/record/start - Start recording
POST /api/video-calls/appointment/{appointmentId}/record/stop  - Stop recording
```

## 🧪 Testing

### **Run Tests**
```bash
./mvnw test
```

### **API Testing Scripts**
The repository includes PowerShell test scripts:
- `test_api.ps1` - Basic API testing
- `test_chat_api.ps1` - Chat functionality testing
- `test_ai_healthbot.ps1` - AI Health Bot testing
- `test_video_calls.ps1` - Video call API testing

## 🔧 Configuration

### **Database Configuration**
```properties
# H2 Database (Development)
spring.datasource.url=jdbc:h2:mem:medical_app
spring.datasource.username=sa
spring.datasource.password=

# PostgreSQL (Production)
# spring.datasource.url=jdbc:postgresql://localhost:5432/healthconnect
# spring.datasource.username=your_username
# spring.datasource.password=your_password
```

### **JWT Configuration**
```properties
jwt.secret=your-secret-key
jwt.expiration=86400000
```

### **AI Configuration**
```properties
google.ai.api.key=YOUR_GEMINI_API_KEY
google.ai.model=gemini-1.5-flash
```

## 📊 Project Statistics

- **✅ 29 API Endpoints** - Complete functionality
- **✅ 61 Source Files** - Well-organized codebase
- **✅ 6 Database Tables** - Comprehensive data model
- **✅ 5 JPA Repositories** - Complete data access layer
- **✅ Real AI Integration** - Google Gemini powered
- **✅ Enterprise Security** - JWT authentication

## 🤝 Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👨‍💻 Author

**Anudeep**
- GitHub: [@anudeep2710](https://github.com/anudeep2710)

## 🙏 Acknowledgments

- Spring Boot team for the excellent framework
- Google for Gemini AI API
- All contributors and testers

---

**🏥 HealthConnect - Revolutionizing Healthcare with AI-Powered Technology**
