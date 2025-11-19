# HappyTails - Complete Code Structure Documentation

## Table of Contents
1. [Project Overview](#project-overview)
2. [Backend Architecture](#backend-architecture)
3. [Frontend Architecture](#frontend-architecture)
4. [Data Flow & Request Lifecycle](#data-flow--request-lifecycle)
5. [API Endpoints Reference](#api-endpoints-reference)
6. [Security Implementation](#security-implementation)
7. [Database Schema](#database-schema)

---

## Project Overview

**HappyTails** is a full-stack pet adoption platform connecting animal shelters with potential adopters. The system consists of:

- **Backend**: Spring Boot REST API (Java 17) with JWT authentication
- **Frontend**: React 19 with React Router and Tailwind CSS
- **Database**: MySQL with JPA/Hibernate ORM
- **Authentication**: JWT-based stateless authentication

### Tech Stack
- **Backend**: Spring Boot 3.5.7, Spring Security, Spring Data JPA, JWT (jjwt 0.11.5), Lombok
- **Frontend**: React 19, React Router 7, Axios, Tailwind CSS, Vite
- **Database**: MySQL 8.0+

---

## Backend Architecture

### Directory Structure
```
backend/src/main/java/com/happytails/backend/
├── BackendApplication.java          # Main Spring Boot application entry point
├── config/
│   └── SecurityConfig.java         # Spring Security configuration
├── controller/                     # REST API endpoints
│   ├── AdopterController.java
│   ├── ApplicationController.java
│   ├── AuthController.java
│   ├── GlobalExceptionHandler.java
│   ├── PetController.java
│   ├── ShelterController.java
│   └── ShelterStaffController.java
├── dto/                            # Data Transfer Objects
│   ├── ApplicationRequest.java
│   ├── LoginRequest.java
│   ├── LoginResponse.java
│   ├── RegisterAdopterRequest.java
│   ├── RegisterStaffRequest.java
│   ├── StatusUpdateRequest.java
│   ├── SubmitApplicationRequest.java
│   └── UpdateAdopterRequest.java
├── model/                          # JPA Entity classes
│   ├── Adopter.java
│   ├── Application.java
│   ├── Pet.java
│   ├── Shelter.java
│   └── ShelterStaff.java
├── repository/                     # Spring Data JPA repositories
│   ├── AdopterRepository.java
│   ├── ApplicationRepository.java
│   ├── PetRepository.java
│   ├── ShelterRepository.java
│   └── ShelterStaffRepository.java
├── security/                       # JWT authentication components
│   ├── JwtAuthenticationFilter.java
│   └── JwtUtils.java
└── service/                        # Business logic layer
    ├── AdopterService.java
    ├── ApplicationService.java
    ├── AuthService.java
    ├── PetService.java
    ├── ShelterService.java
    └── ShelterStaffService.java
```

---

### 1. Entity Models (JPA)

#### **Adopter.java**
- **Purpose**: Represents a user who can adopt pets
- **Key Fields**:
  - `adopterId` (Long, Primary Key, Auto-generated)
  - `email` (String, Unique, Not Null)
  - `password` (String, Hashed, Not Null, Write-only in JSON)
  - `profilePersonalInfo` (TEXT) - JSON or text field for personal information
  - `profileResidenceDetails` (TEXT) - Housing information
  - `profilePetExperience` (TEXT) - Previous pet ownership experience
- **Relationships**:
  - `@OneToMany` with `Application` (one adopter can have many applications)
  - `@OneToMany` with `Pet` (one adopter can adopt many pets)
- **Table**: `adopter`

#### **ShelterStaff.java**
- **Purpose**: Represents staff members working at shelters
- **Key Fields**:
  - `staffId` (Long, Primary Key)
  - `email` (String, Unique, Not Null)
  - `password` (String, Hashed, Not Null)
- **Relationships**:
  - `@ManyToOne` with `Shelter` (many staff belong to one shelter)
- **Table**: `shelter_staff`

#### **Pet.java**
- **Purpose**: Represents adoptable animals
- **Key Fields**:
  - `petId` (Long, Primary Key)
  - `name` (String, Not Blank)
  - `species` (String, Not Blank) - e.g., "Dog", "Cat", "Rabbit"
  - `breed` (String) - e.g., "Golden Retriever"
  - `age` (String) - e.g., "2 years"
  - `size` (Enum: PetSize) - Small, Medium, Large
  - `status` (Enum: PetStatus) - Available, Pending, Adopted
  - `description` (TEXT, Not Blank)
  - `temperament` (String) - Comma-separated traits
  - `photosJson` (JSON) - Array of photo URLs stored as JSON string
- **Relationships**:
  - `@ManyToOne` with `Shelter` (many pets belong to one shelter)
  - `@ManyToOne` with `Adopter` (optional, when adopted)
- **Enums**:
  - `PetStatus`: Available, Pending, Adopted
  - `PetSize`: Small, Medium, Large
- **Table**: `pet`

#### **Application.java**
- **Purpose**: Represents adoption applications submitted by adopters
- **Key Fields**:
  - `applicationId` (Long, Primary Key)
  - `status` (Enum: ApplicationStatus)
  - `supplementaryAnswers` (TEXT) - Additional questions/answers
  - `submittedAt` (LocalDateTime) - Auto-set on creation
- **Relationships**:
  - `@ManyToOne` with `Adopter` (many applications from one adopter)
  - `@ManyToOne` with `Pet` (many applications for one pet)
- **Enums**:
  - `ApplicationStatus`: Received, In_Review, Interview_Scheduled, Approved, Rejected
- **Lifecycle**:
  - `@PrePersist` sets `submittedAt` to current time and default status to "Received"
- **Table**: `application`

#### **Shelter.java**
- **Purpose**: Represents animal shelters/organizations
- **Key Fields**:
  - `shelterId` (Long, Primary Key)
  - `name` (String)
  - `location` (TEXT)
  - `contactInfo` (String)
- **Relationships**:
  - `@OneToMany` with `Pet` (one shelter has many pets)
  - `@OneToMany` with `ShelterStaff` (one shelter has many staff)
- **Table**: `shelter`

---

### 2. Repositories (Data Access Layer)

All repositories extend `JpaRepository<Entity, Long>`, providing CRUD operations automatically.

#### **AdopterRepository**
- `Optional<Adopter> findByEmail(String email)` - Find adopter by email

#### **ApplicationRepository**
- `List<Application> findByAdopterAdopterId(Long adopterId)` - Get all applications for an adopter
- `List<Application> findByPetPetId(Long petId)` - Get all applications for a pet

#### **PetRepository, ShelterRepository, ShelterStaffRepository**
- Standard JPA repository methods (findAll, findById, save, delete, etc.)

---

### 3. Services (Business Logic Layer)

#### **AuthService.java**
- **Purpose**: Handles authentication and user registration
- **Methods**:
  - `registerAdopter(RegisterAdopterRequest)` - Creates new adopter account
    - Validates email uniqueness
    - Hashes password with BCrypt
    - Saves to database
  - `registerStaff(RegisterStaffRequest)` - Creates new staff account
    - Validates email uniqueness
    - Links staff to shelter
    - Hashes password
  - `login(LoginRequest)` - Authenticates user
    - Tries to find user as Adopter first, then ShelterStaff
    - Validates password with BCrypt
    - Generates JWT token with role claim (ROLE_ADOPTER or ROLE_STAFF)
    - Returns JWT token string

#### **AdopterService.java**
- **Purpose**: Manages adopter profile operations
- **Methods**:
  - `getAllAdopters()` - Returns all adopters
  - `findByEmail(String)` - Finds adopter by email
  - `getById(Long)` - Finds adopter by ID
  - `updateProfileByEmail(String, UpdateAdopterRequest)` - Updates adopter profile
    - Updates password (if provided, hashes it)
    - Updates profile fields (personal info, residence, experience)
  - `deleteByEmail(String)` - Deletes adopter account

#### **PetService.java**
- **Purpose**: Manages pet data
- **Methods**:
  - `getAllPets()` - Returns all pets
  - `getPetById(Long)` - Returns pet by ID

#### **ApplicationService.java**
- **Purpose**: Manages adoption applications
- **Methods**:
  - `getAllApplications()` - Returns all applications
  - `submitApplication(Long adopterId, Long petId, String supplementaryAnswers)`
    - Creates new application
    - Sets pet status to "Pending"
    - Returns created application
  - `getApplicationsForAdopter(Long)` - Gets all applications for an adopter
  - `getApplicationsForPet(Long)` - Gets all applications for a pet
  - `getApplicationsForShelter(Long)` - Gets all applications for pets in a shelter
  - `updateApplicationStatus(Long, ApplicationStatus)` - Updates application status
    - If approved: Sets pet status to "Adopted" and links pet to adopter
    - If rejected: Sets pet status back to "Available" (if was Pending)

#### **ShelterService.java**
- **Methods**:
  - `getAllShelters()` - Returns all shelters

#### **ShelterStaffService.java**
- Currently minimal (placeholder for future staff management features)

---

### 4. Controllers (REST API Endpoints)

All controllers are annotated with `@RestController` and `@RequestMapping("/api/...")`.

#### **AuthController** (`/api/auth`)
- `POST /register-adopter` - Registers new adopter (FR-1)
  - Request: `RegisterAdopterRequest` (email, password)
  - Response: `Adopter` entity
- `POST /login` - Authenticates user (FR-2)
  - Request: `LoginRequest` (email, password)
  - Response: `LoginResponse` (token, tokenType: "Bearer")
- `POST /register-staff` - Registers new staff member (FR-4)
  - Request: `RegisterStaffRequest` (email, password, shelterId)
  - Response: `ShelterStaff` entity

#### **AdopterController** (`/api/adopters`)
- `GET /` - Gets all adopters (admin/development)
- `GET /me` - Gets current authenticated adopter's profile
  - Requires: JWT authentication
  - Extracts email from SecurityContext
- `PUT /profile` - Updates current adopter's profile
  - Request: `UpdateAdopterRequest` (password, profilePersonalInfo, profileResidenceDetails, profilePetExperience)
  - Requires: JWT authentication
- `DELETE /me` - Deletes current adopter's account
  - Requires: JWT authentication

#### **PetController** (`/api/pets`)
- `GET /` - Gets all pets (public)
- `GET /{id}` - Gets pet by ID (public)

#### **ApplicationController** (`/api/applications`)
- `GET /` - Gets all applications (admin/development)
- `GET /me` - Gets current adopter's applications
  - Requires: JWT authentication (adopter role)
- `GET /shelter` - Gets applications for current staff's shelter
  - Requires: JWT authentication (staff role)
- `POST /` - Submits new adoption application
  - Request: `ApplicationRequest` (petId, supplementaryAnswers)
  - Requires: JWT authentication (adopter role)
- `PUT /{id}/status` - Updates application status
  - Request: `StatusUpdateRequest` (status: "Received", "In_Review", etc.)
  - Requires: JWT authentication (staff role only)

#### **ShelterController** (`/api/shelters`)
- `GET /` - Gets all shelters (public)

#### **ShelterStaffController** (`/api/staff`)
- `GET /me` - Gets current staff member's profile
  - Requires: JWT authentication
- `DELETE /me` - Deletes current staff account
  - Requires: JWT authentication

#### **GlobalExceptionHandler**
- `@RestControllerAdvice` - Global exception handling
- Handles `MethodArgumentNotValidException` (validation errors)
- Handles `RuntimeException` (business logic errors)
- Returns structured error responses

---

### 5. Security Components

#### **SecurityConfig.java**
- **Purpose**: Configures Spring Security
- **Key Features**:
  - Disables CSRF (stateless JWT API)
  - Sets session creation to STATELESS
  - Public endpoints: `/api/auth/**`, `/api/shelters/**`, `/api/pets/**`
  - Protected endpoints: `/api/adopters/**`, `/api/staff/**`, `/api/applications/**`
  - Adds `JwtAuthenticationFilter` before username/password filter
  - Provides `BCryptPasswordEncoder` bean

#### **JwtUtils.java**
- **Purpose**: JWT token generation and validation
- **Methods**:
  - `generateJwtToken(String username, List<String> roles)` - Creates JWT
    - Subject: email/username
    - Claims: roles array
    - Expiration: 24 hours (86400000 ms)
    - Algorithm: HS512
  - `getUserNameFromJwtToken(String)` - Extracts username from token
  - `getRolesFromJwtToken(String)` - Extracts roles from token
  - `validateJwtToken(String)` - Validates token signature and expiration
- **Configuration**: Reads `app.jwtSecret` and `app.jwtExpirationMs` from `application.properties`

#### **JwtAuthenticationFilter.java**
- **Purpose**: Intercepts HTTP requests and validates JWT tokens
- **Process**:
  1. Extracts "Bearer {token}" from Authorization header
  2. Validates token using `JwtUtils`
  3. Extracts username and roles from token
  4. Creates `UsernamePasswordAuthenticationToken` with authorities
  5. Sets authentication in `SecurityContextHolder`
  6. Continues filter chain

---

### 6. DTOs (Data Transfer Objects)

DTOs are used for request/response validation and to avoid exposing entity internals.

- **LoginRequest**: email, password
- **LoginResponse**: token, tokenType
- **RegisterAdopterRequest**: email (validated), password (min 6 chars)
- **RegisterStaffRequest**: email, password, shelterId
- **UpdateAdopterRequest**: password (optional, min 6 chars), profilePersonalInfo, profileResidenceDetails, profilePetExperience
- **ApplicationRequest**: petId, supplementaryAnswers
- **StatusUpdateRequest**: status (string)

---

## Frontend Architecture

### Directory Structure
```
web/src/
├── main.jsx                        # React app entry point
├── App.jsx                         # Main router component
├── index.css                       # Global styles (Tailwind + custom CSS)
├── context/
│   └── AuthContext.jsx             # Authentication context provider
├── services/
│   ├── api.js                      # Axios instance with interceptors
│   ├── auth.js                     # Authentication service
│   ├── petservice.js               # Pet data service
│   └── applicationService.js       # Application service
├── pages/
│   ├── Home.jsx                    # Landing page
│   ├── DiscoverPage.jsx            # Pet discovery/browsing page
│   ├── Login.jsx                   # Login page
│   ├── Register.jsx                # Registration page
│   ├── Profile.jsx                 # User profile page
│   ├── Applications.jsx            # Applications list (staff view)
│   ├── AdoptionForm.jsx            # Adoption form (placeholder)
│   ├── AdoptionSuccess.jsx         # Success page (placeholder)
│   ├── QuizIntro.jsx               # Quiz intro (placeholder)
│   ├── QuizQuestions.jsx           # Quiz questions (placeholder)
│   └── QuizResults.jsx             # Quiz results (placeholder)
├── components/
│   └── PetCard.jsx                 # Reusable pet card component
└── modules/
    └── pets/
        └── PetQuickView.jsx        # Pet detail modal/quick view
```

---

### 1. Core Application Files

#### **main.jsx**
- **Purpose**: Application entry point
- **Structure**:
  - Wraps app in `StrictMode`
  - Provides `BrowserRouter` for routing
  - Provides `AuthProvider` for authentication context
  - Renders `App` component

#### **App.jsx**
- **Purpose**: Defines application routes
- **Routes**:
  - `/` → `Home`
  - `/discover` → `DiscoverPage`
  - `/login` → `Login`
  - `/register` → `Register`
  - `/profile` → `Profile`
  - `/applications` → `Applications`

---

### 2. Context (State Management)

#### **AuthContext.jsx**
- **Purpose**: Global authentication state management
- **Features**:
  - Stores JWT token in localStorage (`happytails_token`)
  - Parses JWT to extract roles and email
  - Provides authentication state to all components
- **Methods**:
  - `login({ email, password })` - Authenticates user and stores token
  - `logout()` - Clears token and redirects to home
  - `registerAdopter(payload)` - Registers new adopter
  - `registerStaff(payload)` - Registers new staff
- **State**:
  - `token` - Current JWT token
  - `roles` - User roles array
  - `email` - User email
  - `isAuthenticated` - Boolean
  - `isAdopter` - Boolean
  - `isStaff` - Boolean
  - `initializing` - Loading state
- **Usage**: Components use `useAuth()` hook to access auth state

---

### 3. Services (API Communication)

#### **api.js**
- **Purpose**: Centralized Axios instance
- **Configuration**:
  - Base URL: `/api` (proxied to backend in development)
  - Content-Type: `application/json`
- **Interceptors**:
  - Request interceptor: Automatically adds `Authorization: Bearer {token}` header from localStorage

#### **auth.js**
- **Purpose**: Authentication API calls
- **Methods**:
  - `login(credentials)` - POST `/auth/login`
  - `registerAdopter(payload)` - POST `/auth/register-adopter`
  - `registerStaff(payload)` - POST `/auth/register-staff`
  - `getProfile()` - GET `/adopters/me` or `/staff/me` (based on role)
  - `updateProfile(payload)` - PUT `/adopters/profile`
  - `deleteAccount()` - DELETE `/adopters/me` or `/staff/me`
- **Helper**: `getUserRoleFromToken()` - Decodes JWT to determine user role

#### **petservice.js**
- **Purpose**: Pet data API calls
- **Methods**:
  - `getAllPets()` - GET `/pets`
    - Normalizes backend data format to UI-friendly format
    - Parses `photosJson` to extract first image URL
    - Parses `temperament` comma-separated string to tags array
  - `getPetById(id)` - GET `/pets/{id}`

#### **applicationService.js**
- **Purpose**: Application API calls
- **Methods**:
  - `submitApplication(petId, supplementaryAnswers)` - POST `/applications`
  - `getMyApplications()` - GET `/applications/me`
  - `getShelterApplications()` - GET `/applications/shelter`
  - `updateApplicationStatus(applicationId, status)` - PUT `/applications/{id}/status`

---

### 4. Pages

#### **Home.jsx**
- **Purpose**: Landing page with featured pets
- **Features**: Hero section, featured pets grid, call-to-action buttons

#### **DiscoverPage.jsx**
- **Purpose**: Browse and search adoptable pets
- **Features**:
  - Pet grid with filtering
  - Search functionality
  - Quick view modal (`PetQuickView`)
  - Header with navigation
  - Footer

#### **Login.jsx**
- **Purpose**: User authentication
- **Features**:
  - Email/password form
  - "Remember me" checkbox
  - "Forgot password" link (placeholder)
  - Redirects after successful login
  - Uses `AuthContext.login()`

#### **Register.jsx**
- **Purpose**: User registration
- **Features**:
  - Toggle between adopter/staff registration
  - Form validation
  - Shelter selection (for staff)
  - Uses `AuthContext.registerAdopter()` or `registerStaff()`

#### **Profile.jsx**
- **Purpose**: User profile management
- **Features**:
  - Displays user information
  - Edit mode for adopters (personal info, residence, experience)
  - Delete account functionality
  - Shows adopted pets (for adopters)
  - Staff view (read-only)

#### **Applications.jsx**
- **Purpose**: Staff view of adoption applications
- **Features**:
  - Lists all applications for staff's shelter
  - Status update buttons (Received, In Review, Interview, Approve, Reject)
  - Uses `applicationService.getShelterApplications()`

---

### 5. Components

#### **PetCard.jsx**
- **Purpose**: Reusable pet card component
- **Props**: pet object (name, breed, age, size, imageUrl, tags, status)
- **Features**: Image, pet info, tags, status badge, action buttons

#### **PetQuickView.jsx**
- **Purpose**: Modal/overlay showing detailed pet information
- **Props**: pet object, onClose callback
- **Features**: Large image, detailed info, temperament tags, "Apply to Adopt" button, "Contact Shelter" button

---

## Data Flow & Request Lifecycle

### Authentication Flow

1. **User Registration**:
   ```
   Frontend (Register.jsx)
   → authService.registerAdopter()
   → POST /api/auth/register-adopter
   → AuthController.registerAdopter()
   → AuthService.registerAdopter()
   → AdopterRepository.save()
   → Database
   ```

2. **User Login**:
   ```
   Frontend (Login.jsx)
   → authService.login()
   → POST /api/auth/login
   → AuthController.login()
   → AuthService.login()
   → Validates credentials
   → JwtUtils.generateJwtToken()
   → Returns JWT token
   → AuthContext.login() stores token in localStorage
   ```

3. **Authenticated Request**:
   ```
   Frontend Component
   → Service method (e.g., authService.getProfile())
   → api.js interceptor adds "Authorization: Bearer {token}" header
   → HTTP Request
   → JwtAuthenticationFilter intercepts request
   → Extracts token from header
   → JwtUtils.validateJwtToken()
   → Extracts username and roles
   → Sets SecurityContext authentication
   → Controller method executes
   → Service layer
   → Repository layer
   → Database
   → Response returned
   ```

### Application Submission Flow

1. **Adopter Submits Application**:
   ```
   Frontend (DiscoverPage or PetQuickView)
   → applicationService.submitApplication(petId, answers)
   → POST /api/applications
   → ApplicationController.submitApplication()
   → Extracts adopter email from SecurityContext
   → ApplicationService.submitApplication()
   → Creates Application entity
   → Sets pet status to "Pending"
   → Saves to database
   → Returns application
   ```

2. **Staff Updates Application Status**:
   ```
   Frontend (Applications.jsx)
   → applicationService.updateApplicationStatus(id, status)
   → PUT /api/applications/{id}/status
   → ApplicationController.updateStatus()
   → Validates staff role
   → ApplicationService.updateApplicationStatus()
   → Updates application status
   → If approved: Sets pet to "Adopted" and links to adopter
   → If rejected: Sets pet back to "Available"
   → Saves changes
   ```

---

## API Endpoints Reference

### Public Endpoints (No Authentication)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/register-adopter` | Register new adopter |
| POST | `/api/auth/register-staff` | Register new staff |
| POST | `/api/auth/login` | Login (returns JWT) |
| GET | `/api/pets` | Get all pets |
| GET | `/api/pets/{id}` | Get pet by ID |
| GET | `/api/shelters` | Get all shelters |

### Protected Endpoints (Require JWT)

#### Adopter Endpoints
| Method | Endpoint | Description | Role |
|--------|----------|-------------|------|
| GET | `/api/adopters/me` | Get current adopter profile | ADOPTER |
| PUT | `/api/adopters/profile` | Update adopter profile | ADOPTER |
| DELETE | `/api/adopters/me` | Delete adopter account | ADOPTER |
| GET | `/api/applications/me` | Get adopter's applications | ADOPTER |
| POST | `/api/applications` | Submit new application | ADOPTER |

#### Staff Endpoints
| Method | Endpoint | Description | Role |
|--------|----------|-------------|------|
| GET | `/api/staff/me` | Get current staff profile | STAFF |
| DELETE | `/api/staff/me` | Delete staff account | STAFF |
| GET | `/api/applications/shelter` | Get shelter's applications | STAFF |
| PUT | `/api/applications/{id}/status` | Update application status | STAFF |

---

## Security Implementation

### JWT Token Structure
```json
{
  "sub": "user@example.com",
  "roles": ["ROLE_ADOPTER"],
  "iat": 1234567890,
  "exp": 1234654290
}
```

### Password Hashing
- Algorithm: BCrypt
- Strength: Default (10 rounds)
- Stored: Hashed in database, never returned in API responses

### Authorization Strategy
- **Role-Based Access Control (RBAC)**:
  - `ROLE_ADOPTER`: Can manage own profile, submit applications, view own applications
  - `ROLE_STAFF`: Can view shelter applications, update application status, manage own profile

### Security Headers
- JWT token sent in `Authorization: Bearer {token}` header
- Token validated on every protected request
- Stateless authentication (no server-side sessions)

---

## Database Schema

### Entity Relationships

```
Shelter (1) ──< (Many) Pet
Shelter (1) ──< (Many) ShelterStaff
Adopter (1) ──< (Many) Application
Pet (1) ──< (Many) Application
Adopter (1) ──< (Many) Pet (when adopted)
```

### Tables

1. **adopter**
   - adopter_id (PK)
   - email (UNIQUE)
   - password (hashed)
   - profile_personal_info (TEXT)
   - profile_residence_details (TEXT)
   - profile_pet_experience (TEXT)

2. **shelter**
   - shelter_id (PK)
   - name
   - location (TEXT)
   - contact_info

3. **shelter_staff**
   - staff_id (PK)
   - shelter_id (FK → shelter)
   - email (UNIQUE)
   - password (hashed)

4. **pet**
   - pet_id (PK)
   - shelter_id (FK → shelter)
   - adopter_id (FK → adopter, nullable)
   - name
   - species
   - breed
   - age
   - size (ENUM)
   - status (ENUM)
   - description (TEXT)
   - temperament
   - photos_json (JSON)

5. **application**
   - application_id (PK)
   - adopter_id (FK → adopter)
   - pet_id (FK → pet)
   - status (ENUM)
   - supplementary_answers (TEXT)
   - submitted_at (TIMESTAMP)

---

## How Everything Works Together

### Example: Complete Adoption Flow

1. **User Registration**:
   - User visits `/register`
   - Fills form → `Register.jsx` calls `AuthContext.registerAdopter()`
   - Frontend service → `POST /api/auth/register-adopter`
   - Backend `AuthController` → `AuthService.registerAdopter()`
   - Password hashed, user saved to database
   - User redirected to login

2. **User Login**:
   - User visits `/login`
   - Fills credentials → `Login.jsx` calls `AuthContext.login()`
   - Frontend service → `POST /api/auth/login`
   - Backend validates credentials, generates JWT
   - Token stored in localStorage via `AuthContext`
   - User redirected to home

3. **Browsing Pets**:
   - User visits `/discover`
   - `DiscoverPage.jsx` loads → calls `petService.getAllPets()`
   - Frontend service → `GET /api/pets` (public endpoint)
   - Backend returns pet list
   - Pets displayed in grid

4. **Viewing Pet Details**:
   - User clicks "View" on pet card
   - `PetQuickView` modal opens with pet details
   - User clicks "Apply to Adopt"
   - Form submission → `applicationService.submitApplication()`
   - Frontend service → `POST /api/applications` (requires JWT)
   - `JwtAuthenticationFilter` validates token
   - Backend creates application, sets pet to "Pending"
   - Success message shown

5. **Staff Reviews Application**:
   - Staff logs in (same flow, gets `ROLE_STAFF` token)
   - Staff visits `/applications`
   - `Applications.jsx` calls `applicationService.getShelterApplications()`
   - Frontend service → `GET /api/applications/shelter` (requires STAFF role)
   - Backend returns applications for staff's shelter
   - Staff clicks "Approve" on application
   - Frontend service → `PUT /api/applications/{id}/status`
   - Backend updates status to "Approved"
   - Pet status set to "Adopted", pet linked to adopter
   - Application list refreshes

---

## Key Design Patterns

1. **Layered Architecture**: Controller → Service → Repository → Database
2. **DTO Pattern**: Separate request/response objects from entities
3. **Repository Pattern**: Abstract data access logic
4. **Dependency Injection**: Spring's `@Autowired` for loose coupling
5. **Context API**: React Context for global auth state
6. **Service Layer**: Centralized API communication in frontend
7. **Interceptor Pattern**: Axios interceptors for automatic token injection

---

## Configuration Files

### Backend (`application.properties`)
- Database connection (MySQL)
- JPA/Hibernate settings (auto-create tables)
- JWT secret and expiration

### Frontend (`vite.config.js`)
- Vite build configuration
- API proxy settings (development)

### Frontend (`tailwind.config.js`)
- Tailwind CSS configuration
- Custom theme variables

---

## Future Enhancements (Placeholders)

- `AdoptionForm.jsx` - Detailed adoption form
- `AdoptionSuccess.jsx` - Success confirmation page
- `QuizIntro.jsx`, `QuizQuestions.jsx`, `QuizResults.jsx` - Pet matching quiz
- Pet creation/editing endpoints (staff)
- Image upload functionality
- Email notifications
- Advanced search and filtering

---

**End of Documentation**


