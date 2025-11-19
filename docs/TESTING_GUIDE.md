# HappyTails Testing Guide

## Testing Strategy

This document outlines the testing approach for the HappyTails platform to ensure all functional and non-functional requirements are met.

## Test Levels

### 1. Unit Testing
**Target**: Individual components, services, and utilities

**Backend (Spring Boot)**:
- JUnit 5 for Java unit tests
- Mockito for mocking dependencies
- Test coverage target: 70%+

**Web (React)**:
- Jest for JavaScript unit tests
- React Testing Library for component tests
- Test coverage target: 60%+

### 2. Integration Testing
**Target**: API endpoints and database interactions

**Backend**:
- Spring Boot Test with `@SpringBootTest`
- MockMvc for controller testing
- H2 in-memory database for test isolation

### 3. End-to-End Testing
**Target**: Complete user workflows

**Tools**:
- Manual testing
- Playwright/Cypress (optional for automation)

### 4. Performance Testing
**Target**: NFR-1 compliance (< 400ms response time)

**Tools**:
- JMeter or Apache Bench
- Spring Boot Actuator metrics

---

## Test Cases by Functional Requirement

### FR-1: Adopter Registration

**Test Case 1.1: Successful Registration**
- **Given**: New user with valid email and password
- **When**: User submits registration form
- **Then**: Account created, 200 OK response with adopter object

**Test Case 1.2: Duplicate Email**
- **Given**: Email already exists in database
- **When**: User attempts to register
- **Then**: 400 Bad Request, error message "Email already exists"

**Test Case 1.3: Invalid Email Format**
- **Given**: Invalid email format
- **When**: User submits form
- **Then**: 400 Bad Request, validation error

---

### FR-2: Adopter Login

**Test Case 2.1: Successful Login**
- **Given**: Valid credentials
- **When**: User submits login form
- **Then**: 200 OK with JWT token and user info

**Test Case 2.2: Invalid Credentials**
- **Given**: Wrong password
- **When**: User attempts login
- **Then**: 401 Unauthorized

**Test Case 2.3: Non-existent User**
- **Given**: Unregistered email
- **When**: User attempts login
- **Then**: 401 Unauthorized

---

### FR-3: Adopter Profile Management

**Test Case 3.1: View Profile**
- **Given**: Authenticated adopter
- **When**: GET /api/adopters/me
- **Then**: 200 OK with profile data

**Test Case 3.2: Update Profile**
- **Given**: Authenticated adopter with new profile data
- **When**: PUT /api/adopters/me/profile
- **Then**: 200 OK with updated profile

**Test Case 3.3: Pre-fill Application Form**
- **Given**: Adopter with saved profile
- **When**: Opens adoption application form
- **Then**: Form automatically populated with saved profile data

---

### FR-6, FR-7, FR-8: Pet Management

**Test Case 6.1: Create Pet (Staff)**
- **Given**: Authenticated shelter staff with pet data
- **When**: POST /api/pets
- **Then**: 201 Created with new pet object

**Test Case 6.2: Create Pet (Non-Staff)**
- **Given**: Authenticated adopter
- **When**: POST /api/pets
- **Then**: 403 Forbidden

**Test Case 7.1: Update Pet**
- **Given**: Staff from same shelter as pet
- **When**: PUT /api/pets/{id}
- **Then**: 200 OK with updated pet

**Test Case 7.2: Update Pet (Different Shelter)**
- **Given**: Staff from different shelter
- **When**: PUT /api/pets/{id}
- **Then**: 400 Bad Request

**Test Case 8.1: Change Pet Status**
- **Given**: Staff with valid status (Available/Pending/Adopted)
- **When**: PATCH /api/pets/{id}/status
- **Then**: 200 OK with updated status

---

### FR-9, FR-10, FR-11: Pet Search and Filtering

**Test Case 9.1: Get All Pets**
- **Given**: Database with pets
- **When**: GET /api/pets
- **Then**: 200 OK with array of all pets

**Test Case 10.1: Filter by Species**
- **Given**: Database with Dogs and Cats
- **When**: GET /api/pets/search?species=Dog
- **Then**: 200 OK with only dogs

**Test Case 10.2: Filter by Multiple Criteria**
- **Given**: Database with varied pets
- **When**: GET /api/pets/search?species=Dog&size=Large&shelterLocation=Cebu
- **Then**: 200 OK with pets matching all filters

**Test Case 10.3: Filter by Gender**
- **Given**: Pets with Male/Female gender
- **When**: GET /api/pets/search?gender=Female
- **Then**: 200 OK with only female pets

**Test Case 10.4: No Results**
- **Given**: No pets match criteria
- **When**: GET /api/pets/search?species=Elephant
- **Then**: 200 OK with empty array

---

### FR-12: View Pet Details

**Test Case 12.1: View Existing Pet**
- **Given**: Pet ID exists
- **When**: GET /api/pets/{id}
- **Then**: 200 OK with complete pet details

**Test Case 12.2: View Non-existent Pet**
- **Given**: Invalid pet ID
- **When**: GET /api/pets/999999
- **Then**: 404 Not Found

---

### FR-13: Submit Adoption Application

**Test Case 13.1: Submit Application**
- **Given**: Authenticated adopter with valid pet ID
- **When**: POST /api/applications
- **Then**: 201 Created with application object, status = "Received"

**Test Case 13.2: Pre-filled Form**
- **Given**: Adopter with saved profile
- **When**: Opens application form
- **Then**: Personal info, residence, and experience fields pre-filled

**Test Case 13.3: Submit Without Auth**
- **Given**: Unauthenticated user
- **When**: POST /api/applications
- **Then**: 401 Unauthorized

---

### FR-14, FR-15: Application Management (Staff)

**Test Case 14.1: View Shelter Applications**
- **Given**: Authenticated staff
- **When**: GET /api/applications/shelter
- **Then**: 200 OK with applications for staff's shelter only

**Test Case 15.1: Update Application Status**
- **Given**: Staff with valid application ID and status
- **When**: PUT /api/applications/{id}/status
- **Then**: 200 OK with updated application

**Test Case 15.2: Invalid Status**
- **Given**: Staff with invalid status value
- **When**: PUT /api/applications/{id}/status {status: "INVALID"}
- **Then**: 400 Bad Request

---

## Non-Functional Requirements Testing

### NFR-1: Performance Testing

**Test Scenario**: Measure API response times for pet listing

**Setup**:
1. Populate database with 100+ pets
2. Use Apache Bench or JMeter

**Test**:
```bash
ab -n 1000 -c 10 http://localhost:8080/api/pets
```

**Success Criteria**: Average response time < 400ms

---

### NFR-2: Availability Testing

**Test Scenario**: Monitor uptime over extended period

**Setup**:
- Deploy to staging environment
- Use monitoring tool (e.g., UptimeRobot, Pingdom)

**Success Criteria**: 90% uptime over 3-month period

---

### NFR-3: Compatibility Testing

**Test Scenario**: Verify cross-platform accessibility

**Platforms to Test**:
- React web app: Chrome, Firefox, Safari, Edge
- Android app: Android 8.0+ (API 26+)

**Success Criteria**: All features functional on supported platforms

---

### NFR-4: Security Testing

**Test Scenarios**:

1. **Password Encryption**
   - Verify passwords stored as BCrypt hashes
   - Verify passwords not returned in API responses

2. **JWT Authentication**
   - Test token expiration (24 hours)
   - Test invalid/expired token rejection
   - Test role-based access control

3. **SQL Injection Prevention**
   - Attempt SQL injection in search parameters
   - Verify JPA parameterized queries prevent injection

4. **XSS Prevention**
   - Attempt script injection in form fields
   - Verify input sanitization

---

## Manual Test Checklist

### User Journey: Adopter Finding and Applying for Pet

1. ☐ Register new adopter account
2. ☐ Login with credentials
3. ☐ Create/update saved application profile
4. ☐ Browse available pets
5. ☐ Apply filters (species, size, location)
6. ☐ View pet details
7. ☐ Submit adoption application (verify pre-fill)
8. ☐ View my applications
9. ☐ Logout

### User Journey: Shelter Staff Managing Pets and Applications

1. ☐ Register shelter staff account
2. ☐ Login with staff credentials
3. ☐ Create new pet profile
4. ☐ Update existing pet profile
5. ☐ Change pet status
6. ☐ View applications for shelter
7. ☐ Update application status
8. ☐ Update shelter profile
9. ☐ Logout

---

## Regression Testing

**When to Run**: After any code changes to core functionality

**Automated Test Suite**:
```bash
# Backend
cd backend
mvn test

# Web
cd web
npm test
```

**Critical Paths**:
- Authentication flow
- Pet search and filtering
- Application submission
- Status updates

---

## Bug Reporting Template

```markdown
**Title**: [Brief description]

**Environment**: [Dev/Staging/Production]

**Steps to Reproduce**:
1. 
2. 
3. 

**Expected Result**:

**Actual Result**:

**Severity**: [Critical/High/Medium/Low]

**Screenshots**: [If applicable]
```

---

## Test Data

### Sample Users

**Adopter**:
- Email: `adopter@test.com`
- Password: `Test123!`

**Shelter Staff**:
- Email: `staff@shelter.com`
- Password: `Staff123!`

### Sample Shelter

- Name: "Happy Paws Test Shelter"
- Location: "Cebu City, Philippines"
- Contact: "test@shelter.com"

---

## Performance Benchmarks

| Endpoint | Target Response Time | Measured |
|----------|---------------------|----------|
| GET /api/pets | < 400ms | TBD |
| GET /api/pets/search | < 400ms | TBD |
| POST /api/auth/login | < 500ms | TBD |
| POST /api/applications | < 600ms | TBD |

---

## Continuous Integration

**Recommended CI/CD Pipeline**:
1. Code push triggers automated tests
2. All unit tests must pass
3. Integration tests run on staging
4. Manual approval for production deployment

**Tools**: GitHub Actions, Jenkins, or GitLab CI/CD
