# ChaTTy - Real-time Multilingual Chat Application

ChaTTy is a full-stack real-time messaging application designed to bridge language barriers. It features automated message translation, enabling users who speak different languages to communicate seamlessly in real-time.

## 🚀 Key Features
- **Real-time Messaging**: Powered by WebSockets (STOMP) for instant message delivery.
- **Automated Translation**: Integrated with translation services to automatically translate messages into the recipient's preferred language.
- **WhatsApp-Inspired UI**: A modern, responsive, and "oil color" aesthetic design.
- **Friend System**: Add friends by User ID and get real-time notifications for friend requests.
- **Secure Authentication**: JWT-based authentication for secure user sessions.
- **Persistent Storage**: All data is stored in a MySQL database using Spring Data JPA.

## 🛠️ Technology Stack
- **Backend**: Spring Boot 3, Java 17, Spring Security (JWT), Spring WebSocket (STOMP), Hibernate/JPA.
- **Frontend**: React.js, Vite, Vanilla CSS (Glassmorphism & Oil Theme).
- **Database**: MySQL.
- **Logic**: Multilingual processing and automated translation.

## 📂 Project Structure
- `backend/`: Spring Boot application source code.
- `frontend/`: React application source code.

## 🏃 How to Run
1. **Database**: Ensure MySQL is running and create a database named `chaty_db`.
2. **Backend**: 
   - Navigate to `backend/`
   - Run `mvn spring-boot:run`
3. **Frontend**:
   - Navigate to `frontend/`
   - Run `npm install` followed by `npm run dev`

## 🚀 Production Deployment
To deploy this project to production using **Vercel** (frontend), **Render** (backend via Docker), and a cloud **MySQL** database, follow the step-by-step instructions in the [DEPLOYMENT.md](DEPLOYMENT.md) guide.

## 👨‍🏫 For Examiners/Lecturers
This project demonstrates the implementation of:
1. **WebSocket Protocol**: Handling bi-directional communication between client and server.
2. **ORM (Object-Relational Mapping)**: Using JPA to manage database entities.
3. **JWT Security**: Implementing stateless authentication.
4. **API Integration**: Handling external translation services.
5. **State Management**: Managing complex real-time states in a React application.
