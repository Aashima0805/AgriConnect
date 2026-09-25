# AgriConnect - Unified Agricultural Ecosystem Platform

AgriConnect is an end-to-end platform bridging the gap between farmers, customers, agricultural machinery providers, and government/NGO resources. It integrates a Java Spring Boot backend, a responsive Bootstrap frontend, and a Python Flask microservice powering AI crop disease detection.

---

## 📁 Project Architecture & Skeleton

```
AgriConnect/
├── pom.xml                               # Maven Project Descriptor (Java 24, Spring Boot 3.3.4)
├── README.md                             # Project Documentation
├── src/
│   ├── main/
│   │   ├── java/com/agriconnect/         # Spring Boot Application Backend
│   │   │   ├── AgriConnectApplication.java # Application Main Entry Point
│   │   │   ├── config/                   # Configuration classes (Security, Data initializers)
│   │   │   ├── controller/               # REST API Controllers
│   │   │   ├── dto/                      # Data Transfer Objects
│   │   │   ├── entity/                   # JPA Entity Models
│   │   │   ├── repository/               # Spring Data JPA Repositories
│   │   │   └── service/                  # Business Logic Services
│   │   └── resources/
│   │       ├── application.properties    # Application & Database Configuration
│   │       └── static/                   # Frontend UI (HTML5, CSS3, Bootstrap 5, JS)
│   │           ├── index.html            # Landing page
│   │           ├── login.html            # Authentication page
│   │           ├── register.html         # User registration page
│   │           ├── dashboard.html        # Main dashboard
│   │           ├── style.css             # Custom styles
│   │           └── app.js                # Frontend logic & API bindings
└── ai-service/                           # Python Flask AI Microservice
    ├── README.md                         # AI Service Documentation
    ├── requirements.txt                  # Python Dependencies (Flask, PyTorch, Ultralytics YOLO)
    ├── app.py                            # Flask Inference Server
    └── models/
        └── best.pt                       # Custom-trained YOLO11n Rice Leaf Disease Model
```

---

## 🛠️ Technology Stack

| Layer | Technologies |
|---|---|
| **Backend** | Java 24, Spring Boot 3.3.4, Spring Data JPA, Maven |
| **Database** | MySQL (with Hibernate ORM) |
| **Frontend** | HTML5, CSS3, Bootstrap 5, JavaScript (Vanilla ES6+) |
| **AI Microservice** | Python 3.10+, Flask, Ultralytics YOLO11n, PyTorch |
| **Model** | `best.pt` (Trained Rice Leaf Disease Detection YOLO model) |

---

## ⚙️ Configuration

### 1. Database Configuration
Configured in `src/main/resources/application.properties`:
- **Database URL**: `jdbc:mysql://localhost:3306/agriconnect`
- **Dialect / Driver**: MySQL Connector/J

### 2. AI Service Configuration
- Configured in `src/main/resources/application.properties`: `ai.service.url=http://localhost:5000`
- Model file located at: `ai-service/models/best.pt`
