# HappyTails Database Schema

## Database: `happytails_db`
**Engine:** MySQL 8.0+

## Entity Relationship Diagram

See `ERD.md` for visual diagram.

## Tables

### 1. `adopter`

Stores information about pet adopters (users who want to adopt pets).

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| `adopter_id` | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Unique identifier |
| `email` | VARCHAR(255) | UNIQUE, NOT NULL | User email (login) |
| `password` | VARCHAR(255) | NOT NULL | BCrypt encrypted password |
| `profile_personal_info` | TEXT | NULL | Personal information (name, age, etc.) |
| `profile_residence_details` | TEXT | NULL | Residence details (house type, yard, etc.) |
| `profile_pet_experience` | TEXT | NULL | Pet ownership experience |
| `created_at` | TIMESTAMP | NOT NULL, DEFAULT CURRENT_TIMESTAMP | Account creation timestamp |
| `updated_at` | TIMESTAMP | NOT NULL, DEFAULT CURRENT_TIMESTAMP ON UPDATE | Last update timestamp |

**Relationships:**
- One-to-Many with `application` (one adopter can have many applications)
- One-to-Many with `pet` (one adopter can adopt many pets)

**Indexes:**
- PRIMARY KEY on `adopter_id`
- UNIQUE INDEX on `email`

---

### 2. `shelter`

Stores information about animal shelters.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| `shelter_id` | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Unique identifier |
| `name` | VARCHAR(255) | NULL | Shelter name |
| `location` | TEXT | NULL | Shelter address/location |
| `contact_info` | VARCHAR(255) | NULL | Contact email/phone |

**Relationships:**
- One-to-Many with `pet` (one shelter has many pets)
- One-to-Many with `shelter_staff` (one shelter has many staff members)

**Indexes:**
- PRIMARY KEY on `shelter_id`

---

### 3. `shelter_staff`

Stores information about shelter staff members (authorized to manage pets and applications).

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| `staff_id` | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Unique identifier |
| `shelter_id` | BIGINT | FOREIGN KEY, NOT NULL | Reference to shelter |
| `email` | VARCHAR(255) | UNIQUE, NOT NULL | Staff email (login) |
| `password` | VARCHAR(255) | NOT NULL | BCrypt encrypted password |
| `created_at` | TIMESTAMP | NOT NULL, DEFAULT CURRENT_TIMESTAMP | Account creation timestamp |
| `updated_at` | TIMESTAMP | NOT NULL, DEFAULT CURRENT_TIMESTAMP ON UPDATE | Last update timestamp |

**Relationships:**
- Many-to-One with `shelter` (many staff belong to one shelter)

**Indexes:**
- PRIMARY KEY on `staff_id`
- UNIQUE INDEX on `email`
- FOREIGN KEY INDEX on `shelter_id`

---

### 4. `pet`

Stores information about pets available for adoption.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| `pet_id` | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Unique identifier |
| `shelter_id` | BIGINT | FOREIGN KEY, NOT NULL | Reference to shelter |
| `adopter_id` | BIGINT | FOREIGN KEY, NULL | Reference to adopter (if adopted) |
| `name` | VARCHAR(100) | NOT NULL | Pet name |
| `species` | VARCHAR(50) | NOT NULL | Species (Dog, Cat, Rabbit, etc.) |
| `breed` | VARCHAR(100) | NULL | Breed |
| `age` | VARCHAR(50) | NULL | Age (e.g., "2 years", "6 months") |
| `size` | ENUM | NULL | Size: Small, Medium, Large |
| `gender` | ENUM | NULL | Gender: Male, Female |
| `status` | ENUM | NOT NULL | Status: Available, Pending, Adopted |
| `description` | TEXT | NOT NULL | Pet description |
| `temperament` | VARCHAR(255) | NULL | Temperament traits (comma-separated) |
| `photos_json` | JSON | NULL | Array of photo URLs in JSON format |

**Relationships:**
- Many-to-One with `shelter` (many pets belong to one shelter)
- Many-to-One with `adopter` (many pets can be adopted by one adopter)
- One-to-Many with `application` (one pet can have many applications)

**Indexes:**
- PRIMARY KEY on `pet_id`
- FOREIGN KEY INDEX on `shelter_id`
- FOREIGN KEY INDEX on `adopter_id`
- INDEX on `species` (for filtering)
- INDEX on `status` (for filtering)
- INDEX on `size` (for filtering)

---

### 5. `application`

Stores adoption applications submitted by adopters.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| `application_id` | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Unique identifier |
| `adopter_id` | BIGINT | FOREIGN KEY, NOT NULL | Reference to adopter |
| `pet_id` | BIGINT | FOREIGN KEY, NOT NULL | Reference to pet |
| `status` | ENUM | NOT NULL | Status: Received, In_Review, Interview_Scheduled, Approved, Rejected |
| `supplementary_answers` | TEXT | NULL | Additional answers from adopter |
| `submitted_at` | TIMESTAMP | NOT NULL | Application submission timestamp |

**Relationships:**
- Many-to-One with `adopter` (many applications from one adopter)
- Many-to-One with `pet` (many applications for one pet)

**Indexes:**
- PRIMARY KEY on `application_id`
- FOREIGN KEY INDEX on `adopter_id`
- FOREIGN KEY INDEX on `pet_id`
- INDEX on `status` (for filtering)

---

## Enums

### Pet Status
- `Available` - Pet is available for adoption
- `Pending` - Adoption in progress
- `Adopted` - Pet has been adopted

### Pet Size
- `Small` - Small pets (< 20 lbs)
- `Medium` - Medium pets (20-50 lbs)
- `Large` - Large pets (> 50 lbs)

### Pet Gender
- `Male`
- `Female`

### Application Status
- `Received` - Application has been received
- `In_Review` - Application is being reviewed
- `Interview_Scheduled` - Interview scheduled with adopter
- `Approved` - Application approved
- `Rejected` - Application rejected

---

## Sample Data

### Sample Shelter
```sql
INSERT INTO shelter (name, location, contact_info) 
VALUES ('Happy Paws Shelter', 'Cebu City, Philippines', 'contact@happypaws.com');
```

### Sample Pet
```sql
INSERT INTO pet (shelter_id, name, species, breed, age, size, gender, status, description, temperament)
VALUES (1, 'Luna', 'Dog', 'Golden Retriever', '2 years', 'Large', 'Female', 'Available', 
        'Friendly and energetic golden retriever', 'Friendly, Playful, Energetic');
```

### Sample Adopter
```sql
INSERT INTO adopter (email, password, profile_personal_info, profile_residence_details, profile_pet_experience)
VALUES ('john@example.com', '$2a$10$...', 'John Doe, 30 years old', 
        'Own house with fenced yard', 'Had dogs for 10 years');
```

---

## Database Initialization

The database schema is automatically created by Spring Boot using Hibernate DDL auto-update:

```properties
spring.jpa.hibernate.ddl-auto=update
```

This setting in `application.properties` tells Hibernate to:
1. Read all `@Entity` classes
2. Automatically create/update tables based on entity definitions
3. Preserve existing data during updates

**For production:** Change to `validate` to prevent automatic schema modifications.

---

## Backup and Recovery

### Backup Command
```bash
mysqldump -u root -p happytails_db > backup_$(date +%Y%m%d).sql
```

### Restore Command
```bash
mysql -u root -p happytails_db < backup_20241120.sql
```

---

## Performance Considerations

1. **Indexes:** Created on foreign keys and frequently filtered columns
2. **Query Optimization:** Use JPA Specifications for complex filtering
3. **Connection Pooling:** HikariCP (Spring Boot default)
4. **Pagination:** Implement pagination for large result sets

---

## Scalability (NFR-6)

The system is designed to support:
- **At least 5 animal shelters** onboarding within 3 months
- **At least 100 adoption applications** processed within 3 months
- Horizontal scaling through database replication
- Vertical scaling through increased server resources
