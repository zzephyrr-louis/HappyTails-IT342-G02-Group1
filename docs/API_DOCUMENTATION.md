# HappyTails API Documentation

Base URL: `http://localhost:8080/api`

## Authentication

All authenticated endpoints require a JWT token in the Authorization header:
```
Authorization: Bearer <token>
```

### Register Adopter (FR-1)
**POST** `/auth/register-adopter`

**Request Body:**
```json
{
  "email": "user@example.com",
  "password": "securePassword123"
}
```

**Response:** `200 OK`
```json
{
  "adopterId": 1,
  "email": "user@example.com",
  "profilePersonalInfo": null,
  "profileResidenceDetails": null,
  "profilePetExperience": null,
  "createdAt": "2024-11-20T03:38:00"
}
```

### Login (FR-2)
**POST** `/auth/login`

**Request Body:**
```json
{
  "email": "user@example.com",
  "password": "securePassword123"
}
```

**Response:** `200 OK`
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "email": "user@example.com",
  "role": "ROLE_ADOPTER"
}
```

### Register Shelter Staff (FR-4)
**POST** `/auth/register-staff`

**Request Body:**
```json
{
  "email": "staff@shelter.com",
  "password": "staffPassword123",
  "shelterId": 1
}
```

**Response:** `200 OK`
```json
{
  "staffId": 1,
  "email": "staff@shelter.com",
  "shelter": {
    "shelterId": 1,
    "name": "Happy Paws Shelter",
    "location": "Cebu City"
  }
}
```

## Pet Management

### Get All Pets (FR-9)
**GET** `/pets`

**Response:** `200 OK`
```json
[
  {
    "petId": 1,
    "name": "Luna",
    "species": "Dog",
    "breed": "Golden Retriever",
    "age": "2 years",
    "size": "Large",
    "gender": "Female",
    "status": "Available",
    "description": "Friendly and energetic dog",
    "temperament": "Friendly, Playful",
    "photosJson": "[\"url1.jpg\", \"url2.jpg\"]",
    "shelter": {
      "shelterId": 1,
      "name": "Happy Paws Shelter",
      "location": "Cebu City"
    }
  }
]
```

### Get Pet by ID (FR-12)
**GET** `/pets/{id}`

**Response:** `200 OK` - Returns single pet object

### Search and Filter Pets (FR-10, FR-11)
**GET** `/pets/search`

**Query Parameters:**
- `species` (String): Filter by species (Dog, Cat, Rabbit, etc.)
- `gender` (String): Filter by gender (Male, Female)
- `breed` (String): Partial match on breed name
- `size` (String): Filter by size (Small, Medium, Large)
- `temperament` (String): Partial match on temperament
- `shelterLocation` (String): Filter by shelter location (Cebu area only)
- `minAge` (Integer): Minimum age in years
- `maxAge` (Integer): Maximum age in years

**Example:**
```
GET /pets/search?species=Dog&size=Large&shelterLocation=Cebu
```

**Response:** `200 OK` - Returns filtered array of pets

### Create Pet Profile (FR-6) [Authenticated - Staff Only]
**POST** `/pets`

**Headers:**
```
Authorization: Bearer <staff-token>
```

**Request Body:**
```json
{
  "name": "Max",
  "species": "Dog",
  "breed": "Beagle",
  "age": "3 years",
  "size": "Medium",
  "gender": "Male",
  "description": "Friendly and curious beagle",
  "temperament": "Curious, Friendly",
  "photosJson": "[\"photo1.jpg\", \"photo2.jpg\"]"
}
```

**Response:** `200 OK` - Returns created pet object

### Update Pet Profile (FR-7) [Authenticated - Staff Only]
**PUT** `/pets/{id}`

**Headers:**
```
Authorization: Bearer <staff-token>
```

**Request Body:** Same as Create Pet (all fields optional for update)

**Response:** `200 OK` - Returns updated pet object

### Change Pet Status (FR-8) [Authenticated - Staff Only]
**PATCH** `/pets/{id}/status`

**Headers:**
```
Authorization: Bearer <staff-token>
```

**Request Body:**
```json
{
  "status": "Adopted"
}
```

**Valid statuses:** `Available`, `Pending`, `Adopted`

**Response:** `200 OK` - Returns updated pet object

## Adopter Profile Management

### Get My Profile (FR-3)
**GET** `/adopters/me`

**Headers:**
```
Authorization: Bearer <adopter-token>
```

**Response:** `200 OK`
```json
{
  "adopterId": 1,
  "email": "user@example.com",
  "profilePersonalInfo": "John Doe, 30 years old",
  "profileResidenceDetails": "Own house with yard in Cebu",
  "profilePetExperience": "Had dogs for 10 years"
}
```

### Update My Profile (FR-3)
**PUT** `/adopters/me/profile`

**Headers:**
```
Authorization: Bearer <adopter-token>
```

**Request Body:**
```json
{
  "profilePersonalInfo": "John Doe, 30 years old",
  "profileResidenceDetails": "Own house with yard",
  "profilePetExperience": "Had dogs for 10 years"
}
```

**Response:** `200 OK` - Returns updated adopter object

## Adoption Applications

### Submit Application (FR-13) [Authenticated - Adopter Only]
**POST** `/applications`

**Headers:**
```
Authorization: Bearer <adopter-token>
```

**Request Body:**
```json
{
  "petId": 1,
  "supplementaryAnswers": "I have experience with large dogs and a fenced yard"
}
```

**Response:** `201 Created`
```json
{
  "applicationId": 1,
  "adopter": { ... },
  "pet": { ... },
  "status": "Received",
  "supplementaryAnswers": "...",
  "submittedAt": "2024-11-20T03:38:00"
}
```

### Get My Applications (FR-13)
**GET** `/applications/me`

**Headers:**
```
Authorization: Bearer <adopter-token>
```

**Response:** `200 OK` - Returns array of applications for logged-in adopter

### Get Shelter Applications (FR-14, FR-15) [Authenticated - Staff Only]
**GET** `/applications/shelter`

**Headers:**
```
Authorization: Bearer <staff-token>
```

**Response:** `200 OK` - Returns array of applications for staff's shelter

### Update Application Status (FR-15) [Authenticated - Staff Only]
**PUT** `/applications/{id}/status`

**Headers:**
```
Authorization: Bearer <staff-token>
```

**Request Body:**
```json
{
  "status": "Approved"
}
```

**Valid statuses:** `Received`, `In_Review`, `Interview_Scheduled`, `Approved`, `Rejected`

**Response:** `200 OK` - Returns updated application object

## Shelter Management

### Get All Shelters
**GET** `/shelters`

**Response:** `200 OK`
```json
[
  {
    "shelterId": 1,
    "name": "Happy Paws Shelter",
    "location": "Cebu City, Philippines",
    "contactInfo": "contact@happypaws.com, +63-123-456-7890"
  }
]
```

### Update Shelter Profile (FR-5) [Authenticated - Staff Only]
**PUT** `/shelters/{id}`

**Headers:**
```
Authorization: Bearer <staff-token>
```

**Request Body:**
```json
{
  "name": "Happy Paws Animal Shelter",
  "location": "123 Main St, Cebu City, Philippines",
  "contactInfo": "info@happypaws.com, +63-999-888-7777"
}
```

**Response:** `200 OK` - Returns updated shelter object

## Error Responses

### 400 Bad Request
```json
{
  "error": "Validation failed",
  "message": "Invalid input data"
}
```

### 401 Unauthorized
```json
{
  "error": "Unauthorized",
  "message": "Invalid or missing authentication token"
}
```

### 403 Forbidden
```json
{
  "error": "Forbidden",
  "message": "You do not have permission to access this resource"
}
```

### 404 Not Found
```json
{
  "error": "Not Found",
  "message": "Resource not found"
}
```

### 500 Internal Server Error
```json
{
  "error": "Internal Server Error",
  "message": "An unexpected error occurred"
}
```

## Performance Requirements (NFR-1)

- Pet listing endpoints (`/pets`, `/pets/search`) target response time: **< 400ms**
- System uptime target: **90% quarterly** (NFR-2)

## Security (NFR-4)

- All passwords are encrypted using BCrypt
- JWT tokens expire after 24 hours
- All endpoints use HTTPS in production
- Sensitive data is excluded from JSON responses using `@JsonProperty(access = WRITE_ONLY)`

## Notes

- The system is designed for the Cebu area (FR-10 constraint on shelter location)
- All timestamps are in ISO 8601 format
- Photo URLs in `photosJson` should be valid URLs or relative paths
- Temperament is stored as comma-separated values
