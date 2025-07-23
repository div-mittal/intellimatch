# IntelliMatch 🎯

An AI-powered resume matching and rewriting system that analyzes resumes against job descriptions to provide compatibility scores and insights.

## 📋 Table of Contents

- [Overview](#-overview)
- [Features](#-features)
- [Architecture](#️-architecture)
- [Tech Stack](#️-tech-stack)
- [Prerequisites](#-prerequisites)
- [Installation](#-installation)
- [Configuration](#️-configuration)
- [Usage](#-usage)
- [API Documentation](#-api-documentation)
- [Project Structure](#-project-structure)
- [Contributing](#-contributing)
- [Development Commands](#-development-commands)
- [Deployment](#-deployment)
- [Security Features](#-security-features)
- [Known Issues](#-known-issues)
- [License](#-license)
- [Author](#-author)
- [Acknowledgments](#-acknowledgments)

## 🌟 Overview

IntelliMatch is a full-stack web application that helps job seekers optimize their resumes by comparing them against job descriptions. The system uses AI-powered analysis to provide matching scores and detailed feedback, helping users understand how well their resume aligns with specific job requirements.

## ✨ Features

- **Resume Upload & Analysis**: Upload resumes in PDF or DOCX format
- **Job Description Matching**: Compare resumes against job descriptions
- **AI-Powered Scoring**: Get detailed compatibility scores and insights
- **User Authentication**: Secure user registration and login system
- **Match History**: Track and view previous resume-job matches
- **File Storage**: Secure cloud storage for uploaded documents
- **Responsive Design**: Modern, mobile-friendly interface
- **Real-time Dashboard**: View all matches and their scores

## 🏗️ Architecture

IntelliMatch follows a microservices architecture with three main components:

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│                 │    │                 │    │                 │
│   Frontend      │◄──►│    Backend      │◄──►│   NLP Service   │
│   (Next.js)     │    │  (Spring Boot)  │    │   (Planned)     │
│                 │    │                 │    │                 │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                       │                       
         │                       │                       
         ▼                       ▼                       
┌─────────────────┐    ┌─────────────────┐               
│                 │    │                 │               
│   Web Browser   │    │    MongoDB      │               
│                 │    │   + AWS S3      │               
│                 │    │                 │               
└─────────────────┘    └─────────────────┘               
```

## 🛠️ Tech Stack

### Frontend (`intellimatch-frontend`)
- **Framework**: Next.js 14 with TypeScript
- **Styling**: Tailwind CSS
- **UI Components**: Radix UI primitives
- **State Management**: React Hooks
- **Build Tool**: Turbopack (development)

### Backend (`intellimatch-backend`)
- **Framework**: Spring Boot 3.5.3
- **Language**: Java 17
- **Database**: MongoDB
- **File Storage**: AWS S3
- **Authentication**: Cookie-based sessions
- **Build Tool**: Maven
- **Additional Libraries**:
  - Spring Data MongoDB
  - Spring Web
  - Spring Validation
  - Lombok
  - AWS SDK for S3

### Infrastructure
- **Database**: MongoDB Atlas
- **File Storage**: AWS S3
- **Deployment**: Ready for containerization

## 📋 Prerequisites

- Java 17 or higher
- Node.js 18 or higher
- npm or yarn
- MongoDB Atlas account (or local MongoDB)
- AWS S3 bucket and credentials

## 🚀 Installation

### 1. Clone the Repository

```bash
git clone <repository-url>
cd intellimatch
```

### 2. Backend Setup

```bash
cd intellimatch-backend

# Install dependencies and build
mvn clean install

# Run the application
mvn spring-boot:run
```

The backend will start on `http://localhost:8090`

### 3. Frontend Setup

```bash
cd intellimatch-frontend

# Install dependencies
npm install

# Run development server
npm run dev
```

The frontend will start on `http://localhost:3000`

## ⚙️ Configuration

### Backend Configuration

Create or update `application.properties` in `intellimatch-backend/src/main/resources/`:

```properties
# Server Configuration
server.port=8090

# Application Name
spring.application.name=Intellimatch

# MongoDB Configuration
spring.data.mongodb.uri=mongodb+srv://username:password@cluster.mongodb.net/intellimatch
spring.data.mongodb.database=intellimatch

# AWS S3 Configuration
cloud.aws.region.static=your-aws-region
aws.s3.bucket=your-s3-bucket-name
aws.accessKeyId=your-access-key
aws.secretKey=your-secret-key

# Cookie Configuration
cookie.maxAge=86400

# Frontend URL (for CORS)
frontend.url=http://localhost:3000
```

### Frontend Configuration

The frontend automatically connects to the backend API. Ensure the backend is running on port 8090.

## 📘 Usage

### 1. User Registration/Login
- Navigate to the application
- Register with email and password
- Login to access features

### 2. Upload Resume and Job Description
- Go to the Upload page
- Select your resume (PDF or DOCX, max 5MB)
- Select the job description file
- Click upload to process

### 3. View Results
- Access your dashboard to see all matches
- View detailed match information
- Track your matching history

### 4. Match History
- All uploaded matches are saved to your profile
- View past matches and scores
- Analyze trends in your applications

## 📚 API Documentation

### Authentication Endpoints

#### Register User
```http
POST /api/user/register
Content-Type: application/json

{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "password123",
  "phoneNumber": "1234567890"
}
```

#### Login User
```http
POST /api/user/login
Content-Type: application/json

{
  "email": "john@example.com",
  "password": "password123"
}
```

#### Logout User
```http
POST /api/user/logout
```

### File Upload Endpoints

#### Upload Resume and Job Description
```http
POST /api/upload
Content-Type: multipart/form-data

resume: <file>
jobDescription: <file>
```

### User Data Endpoints

#### Get User History
```http
GET /api/user/history
```

#### Get User by ID
```http
GET /api/user/get/{id}
```

## 📁 Project Structure

```
intellimatch/
├── intellimatch-backend/          # Spring Boot backend
│   ├── src/main/java/com/divyanshu/Intellimatch/
│   │   ├── controller/            # REST controllers
│   │   ├── model/                 # Data models
│   │   ├── repository/            # Data repositories
│   │   ├── service/               # Business logic
│   │   ├── dto/                   # Data transfer objects
│   │   ├── config/                # Configuration classes
│   │   └── exception/             # Custom exceptions
│   ├── src/main/resources/        # Configuration files
│   └── pom.xml                    # Maven configuration
│
├── intellimatch-frontend/         # Next.js frontend
│   ├── src/
│   │   ├── app/                   # Next.js app router
│   │   ├── components/            # React components
│   │   ├── api/                   # API utility functions
│   │   ├── types/                 # TypeScript type definitions
│   │   └── lib/                   # Utility functions
│   ├── public/                    # Static assets
│   └── package.json               # npm configuration
│
├── intellimatch-nlp/              # NLP service (planned)
└── README.md                      # This file
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

### Development Guidelines

- Follow the existing code style and conventions
- Write meaningful commit messages
- Add tests for new features
- Update documentation as needed
- Ensure all tests pass before submitting

## 🔧 Development Commands

### Backend
```bash
# Run tests
mvn test

# Build without tests
mvn clean compile

# Package application
mvn clean package

# Run with specific profile
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### Frontend
```bash
# Development server
npm run dev

# Build for production
npm run build

# Start production server
npm run start

# Lint code
npm run lint
```

## 🚀 Deployment

### Backend Deployment
The Spring Boot application can be deployed as:
- JAR file: `java -jar target/Intellimatch-0.0.1-SNAPSHOT.jar`
- Docker container (Dockerfile needed)
- Cloud platforms (AWS, GCP, Azure)

### Frontend Deployment
The Next.js application can be deployed on:
- Vercel (recommended)
- Netlify
- AWS Amplify
- Custom server

## 🔒 Security Features

- Password hashing using SHA-256
- HTTP-only cookies for session management
- CORS configuration for cross-origin requests
- File type validation for uploads
- File size limits (5MB max)

## 🐛 Known Issues

- NLP service is planned but not yet implemented
- Match scoring algorithm needs AI integration
- Email verification not implemented
- Password reset functionality pending

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👨‍💻 Author

**Divyanshu Mittal**
- GitHub: [@div-mittal](https://github.com/div-mittal)

## 🙏 Acknowledgments

- Spring Boot community for excellent documentation
- Next.js team for the amazing framework
- Radix UI for accessible UI components
- MongoDB for database solutions
- AWS for cloud storage services

---

**Made with ❤️ for better job matching**
