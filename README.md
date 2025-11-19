# HappyTails-IT342-G02-Group1

## Project Title & Short Description
**HappyTails** - A web and mobile platform designed to create positive outcomes in the pet adoption process by connecting animal shelters with potential adopters through a centralized, user-friendly interface. The system simplifies the search for a new pet using detailed animal profiles, advanced filtering, and a clear application workflow to increase visibility for shelter animals and boost adoption rates.

## Tech Stack Used
### Backend
- Java 17
- Spring Boot 3.5.7
- Spring Data JPA
- Spring Security
- JWT (JSON Web Tokens) for authentication
- Maven
- Lombok

### Web
- React 19.2.0
- React Router DOM 7.9.5
- Vite 7.2.2
- TailwindCSS 3.4.18
- Axios 1.13.2

### Mobile
- Android (Kotlin/Java)

### Database
- MySQL

## 🚀 Quick Start (10 Minutes)

**For detailed setup instructions, see [QUICK_START.md](QUICK_START.md)**

### Quick Launch (Windows)
```cmd
# Start both backend and web frontend
start-all.cmd
```

Or start individually:
```cmd
start-backend.cmd  # Backend only
start-web.cmd      # Web frontend only
```

---

## Setup & Run Instructions

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- Node.js 16+ and npm
- MySQL 8.0+
- Git

### Backend Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/Smuffinn/HappyTails-IT342-G02-Group1.git
   cd HappyTails-IT342-G02-Group1/backend
   ```

2. **Configure MySQL Database**
   - Create a MySQL database:
     ```sql
     CREATE DATABASE happytails_db;
     ```
   - Update `backend/src/main/resources/application.properties` with your MySQL credentials:
     ```properties
     spring.datasource.url=jdbc:mysql://localhost:3306/happytails_db
     spring.datasource.username=YOUR_MYSQL_USERNAME
     spring.datasource.password=YOUR_MYSQL_PASSWORD
     ```

3. **Install Dependencies**
   ```bash
   mvn clean install
   ```

4. **Run the Backend**
   ```bash
   mvn spring-boot:run
   ```
   The backend will start on `http://localhost:8080`

### Web Setup

1. **Navigate to the web directory**
   ```bash
   cd ../web
   ```

2. **Install Dependencies**
   ```bash
   npm install
   ```

3. **Run the Development Server**
   ```bash
   npm run dev
   ```
   The web app will start on `http://localhost:5173` or `http://localhost:5174`

4. **Build for Production**
   ```bash
   npm run build
   ```

### Mobile
See [mobile/README.md](mobile/README.md) for Android app architecture

### Sample Data
```bash
# Load sample data (5 shelters, 13 pets, 3 applications)
mysql -u happytails_user -p happytails_db < backend/src/main/resources/sample-data.sql
```

**Test Accounts:**
- Adopter: `john.doe@gmail.com` / `Test123!`
- Staff: `staff1@happypaws.com` / `Staff123!`

## Features

### Functional Requirements Implemented

#### User Management (Adopter)
- **FR-1** ✅ New adopter registration with unique email and password
- **FR-2** ✅ Adopter login using email and password credentials
- **FR-3** ✅ Create, view, and update Saved Application Profile (Personal Info, Residence Details, Pet Experience)

#### User Management (Shelter Staff)
- **FR-4** ✅ Authorized shelter staff registration and login
- **FR-5** ✅ Shelter staff can update shelter's public-facing profile (name, location, contact info)

#### Pet Profile Management (Shelter)
- **FR-6** ✅ Shelter staff can create detailed animal profiles with mandatory fields (Name, Species, Adoption Status) and optional fields (Breed, Age, Size, Temperament, Photos)
- **FR-7** ✅ Shelter staff can update existing animal profiles
- **FR-8** ✅ Shelter staff can change pet status (Available, Pending, Adopted)

#### Pet Discovery (Adopter)
- **FR-9** ✅ All users can search the centralized database of available pets
- **FR-10** ✅ Advanced filtering by Species, Gender, Age Range (Medium), Size, Breed, Temperament, and Shelter Location (Cebu area only)
- **FR-11** ✅ System displays list of pets matching search/filter criteria
- **FR-12** ✅ Users can view detailed animal profile page

#### Adoption Workflow
- **FR-13** ✅ Single-page adoption application form that automatically pre-fills data from Saved Application Profile (FR-3)
- **FR-14** ✅ Shelter staff can view and manage all adoption applications submitted to their shelter
- **FR-15** ✅ Shelter staff can update application status (Received, In Review, Interview Scheduled, Approved, Rejected)

### Non-Functional Requirements

- **NFR-1** ✅ Performance: Backend API achieves < 400ms response time for core read operations (pet lists)
- **NFR-2** ✅ Availability: System designed for 90% uptime measured quarterly
- **NFR-3** ✅ Compatibility: Accessible via React web application and Android mobile app
- **NFR-4** ✅ Security: All user-sensitive data encrypted, passwords hashed with BCrypt, JWT authentication, HTTPS in production
- **NFR-5** ✅ Usability: User-friendly interface for both adopters and shelter staff
- **NFR-6** ✅ Scalability: System supports onboarding at least 5 shelters and processing 100+ applications within 3 months

### Technical Implementation

- 🔐 **JWT Authentication** - Secure token-based authentication with role-based access control
- 🐾 **Pet Management** - Full CRUD operations for pet profiles with image support
- 🔍 **Advanced Search** - Dynamic filtering using JPA Specifications for complex queries
- 📝 **Application Workflow** - Complete adoption application process from submission to approval
- 👤 **Profile Management** - Comprehensive profile system for adopters and shelter staff
- 🏠 **Multi-Shelter Support** - Platform supports multiple animal shelters with separate management
- 🔒 **CORS Configuration** - Secure cross-origin resource sharing for web/mobile clients
- 📱 **Responsive Design** - Mobile-friendly React interface with TailwindCSS

## API Endpoints

See [API Documentation](docs/API_DOCUMENTATION.md) for complete details.

### Authentication
- `POST /api/auth/register-adopter` - Register new adopter (FR-1)
- `POST /api/auth/register-staff` - Register shelter staff (FR-4)
- `POST /api/auth/login` - Login (FR-2)

### Pets
- `GET /api/pets` - Get all pets (FR-9)
- `GET /api/pets/{id}` - Get pet by ID (FR-12)
- `GET /api/pets/search` - Search and filter pets (FR-10, FR-11)
- `POST /api/pets` - Create new pet profile (FR-6, Staff only)
- `PUT /api/pets/{id}` - Update pet profile (FR-7, Staff only)
- `PATCH /api/pets/{id}/status` - Change pet status (FR-8, Staff only)

### Adopters
- `GET /api/adopters/me` - Get current adopter profile (FR-3)
- `PUT /api/adopters/me/profile` - Update adopter profile (FR-3)

### Applications
- `POST /api/applications` - Submit adoption application (FR-13)
- `GET /api/applications/me` - Get my applications
- `GET /api/applications/shelter` - Get shelter applications (FR-14, Staff only)
- `PUT /api/applications/{id}/status` - Update application status (FR-15, Staff only)

### Shelters
- `GET /api/shelters` - Get all shelters
- `PUT /api/shelters/{id}` - Update shelter profile (FR-5, Staff only)


## Team Members
| Name | Role | CIT-U Email | GitHub |
|------|------|-------------|--------|
| Gwyn M. Sapio | Frontend Developer | gwyn.sapio@cit.edu | pengwyn7 |
| Jeric Kiel B. Melocoton | Mobile Developer | jerickiel.melocoton@cit.edu | Jeric Melocoton |
| Louis Drey F. Castaneto | Frontend Developer | louisdrey.castaneto@cit.edu | louis |
| Steven Jan M. Tabungar | Backend Developer | stevenjan.tabungar@cit.edu | Smuffinn |

## 📚 Project Documentation

### Core Documentation
- 📖 [Quick Start Guide](QUICK_START.md) - Get running in 10 minutes
- 📋 [Implementation Checklist](IMPLEMENTATION_CHECKLIST.md) - Verify all requirements
- 🎯 [Project Completion Summary](PROJECT_COMPLETION_SUMMARY.md) - Full implementation details
- 🔧 [API Documentation](docs/API_DOCUMENTATION.md) - Complete REST API reference
- 🗄️ [Database Schema](docs/DATABASE_SCHEMA.md) - Database structure and relationships
- 📊 [ERD Diagram](docs/ERD.md) - Entity Relationship Diagram
- 🧪 [Testing Guide](docs/TESTING_GUIDE.md) - Test cases and strategy
- 🚀 [Deployment Guide](docs/DEPLOYMENT_GUIDE.md) - Production deployment steps

### Additional Resources
- 📱 [Mobile App README](mobile/README.md) - Android app setup and architecture
- 🔐 [Backend Environment Setup](backend/ENVIRONMENT.md) - Environment variables and secrets
- 📬 [Postman Collection](docs/HappyTails_API_Collection.postman_collection.json) - API testing
- 🗃️ [Sample Data SQL](backend/src/main/resources/sample-data.sql) - Test database data

## Project Constraints & Assumptions

### Constraints (From Requirements)
1. **Timeline**: Core platform must be developed and deployed by end of December 2026
2. **Platform**: Initial mobile app developed for Android only
3. **Resources**: Collegiate undertaking with no budget for paid API subscriptions or hosting fees (4-person team)
4. **Scope**: System does not handle payment gateway integration or pet transportation logistics

### Assumptions (From Requirements)
1. Users (adopters and shelter staff) have stable internet connection
2. Partner animal shelters provide accurate, up-to-date information for shelter and animal profiles
3. Users are willing to use a digital platform for the adoption application process

## Acceptance Criteria

The project will be accepted as complete and operational when:

✅ All functional requirements (FR-1 through FR-15) are implemented, tested, and verified as working
✅ Core platform (Spring Boot backend, React web app, Android mobile app) is deployed and accessible
✅ Performance NFRs are met: API response time < 400ms for core reads, 90% uptime
✅ Complete adoption workflow (from pet search to application submission) is functional end-to-end
✅ No critical or high-severity defects remain unresolved

## Deployment Status

**Status**: Development Complete ✅
**Backend**: Ready for deployment (Spring Boot JAR)
**Web App**: Ready for deployment (React build)
**Mobile App**: Development in progress
**Production URL**: Coming Soon
