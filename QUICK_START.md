# HappyTails Quick Start Guide

Get the HappyTails platform running in 10 minutes! 🚀

## Prerequisites

Before starting, ensure you have:

- ✅ Java 17 or higher (`java -version`)
- ✅ Maven 3.6+ (`mvn -version`)
- ✅ Node.js 18+ and npm (`node -version`)
- ✅ MySQL 8.0+ running
- ✅ Git installed

---

## Step 1: Database Setup (2 minutes)

### Create Database

```sql
-- Open MySQL command line or MySQL Workbench
CREATE DATABASE happytails_db;

-- Create user (optional, or use root)
CREATE USER 'happytails_user'@'localhost' IDENTIFIED BY 'happytails123';
GRANT ALL PRIVILEGES ON happytails_db.* TO 'happytails_user'@'localhost';
FLUSH PRIVILEGES;
```

### Configure Database Connection

Edit `backend/src/main/resources/application.properties`:

```properties
spring.datasource.username=happytails_user
spring.datasource.password=happytails123
```

**Or use root:**
```properties
spring.datasource.username=root
spring.datasource.password=YOUR_ROOT_PASSWORD
```

---

## Step 2: Start Backend (3 minutes)

### Option A: Using Maven (Recommended)

```bash
cd backend
mvn clean install
mvn spring-boot:run
```

### Option B: Using Maven Wrapper (Windows)

```bash
cd backend
.\mvnw.cmd clean install
.\mvnw.cmd spring-boot:run
```

**Expected Output:**
```
Started BackendApplication in X seconds
Tomcat started on port(s): 8080 (http)
```

✅ Backend is now running at `http://localhost:8080`

**Test it:**
```bash
curl http://localhost:8080/api/shelters
```

---

## Step 3: Load Sample Data (1 minute)

### Option A: Via MySQL Command Line

```bash
mysql -u happytails_user -p happytails_db < backend/src/main/resources/sample-data.sql
```

### Option B: Via MySQL Workbench

1. Open `backend/src/main/resources/sample-data.sql`
2. Execute the script

**Verify:**
```sql
SELECT COUNT(*) FROM pet;  -- Should show 13 pets
SELECT COUNT(*) FROM shelter;  -- Should show 5 shelters
```

---

## Step 4: Start Web Frontend (2 minutes)

### Install and Run

```bash
cd web
npm install
npm run dev
```

**Expected Output:**
```
VITE v7.2.2  ready in X ms

➜  Local:   http://localhost:5173/
➜  Network: use --host to expose
```

✅ Web app is now running at `http://localhost:5173`

---

## Step 5: Test the System (2 minutes)

### Open Browser

Navigate to: `http://localhost:5173`

### Test Login

**As Adopter:**
- Email: `john.doe@gmail.com`
- Password: `Test123!`

**As Shelter Staff:**
- Email: `staff1@happypaws.com`
- Password: `Staff123!`

### Test Features

1. **Browse Pets** → Click "Discover Pets"
2. **Filter Pets** → Try filtering by species, size, location
3. **View Pet Details** → Click on any pet card
4. **Apply for Pet** (as Adopter) → Submit an application
5. **Manage Applications** (as Staff) → View and update application status

---

## API Testing with Postman/cURL

### Register New Adopter

```bash
curl -X POST http://localhost:8080/api/auth/register-adopter \
  -H "Content-Type: application/json" \
  -d '{
    "email": "newuser@test.com",
    "password": "Password123!"
  }'
```

### Login

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john.doe@gmail.com",
    "password": "Test123!"
  }'
```

**Copy the token from response!**

### Get All Pets

```bash
curl http://localhost:8080/api/pets
```

### Search Pets (with filters)

```bash
curl "http://localhost:8080/api/pets/search?species=Dog&size=Large&shelterLocation=Cebu"
```

### Get My Profile (Authenticated)

```bash
curl http://localhost:8080/api/adopters/me \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

### Submit Application (Authenticated)

```bash
curl -X POST http://localhost:8080/api/applications \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -d '{
    "petId": 1,
    "supplementaryAnswers": "I have a fenced yard and experience with large dogs."
  }'
```

---

## Common Issues & Solutions

### Issue: Port 8080 already in use

**Solution:**
```bash
# Find process using port 8080
netstat -ano | findstr :8080

# Kill the process (Windows)
taskkill /PID <process_id> /F

# Or change port in application.properties
server.port=8081
```

### Issue: Database connection failed

**Solution:**
1. Verify MySQL is running: `mysql -u root -p`
2. Check credentials in `application.properties`
3. Ensure database `happytails_db` exists

### Issue: Frontend can't connect to backend

**Solution:**
1. Check CORS configuration in `CorsConfig.java`
2. Verify backend is running on port 8080
3. Check `web/src/services/api.js` for correct API URL

### Issue: Maven build fails

**Solution:**
```bash
# Clean Maven cache
mvn clean

# Update dependencies
mvn dependency:purge-local-repository

# Rebuild
mvn clean install -U
```

### Issue: npm install fails

**Solution:**
```bash
# Clear npm cache
npm cache clean --force

# Delete node_modules and package-lock.json
rm -rf node_modules package-lock.json

# Reinstall
npm install
```

---

## Test Accounts

### Adopters (Password: `Test123!`)
- john.doe@gmail.com
- maria.santos@yahoo.com
- carlos.reyes@outlook.com

### Shelter Staff (Password: `Staff123!`)
- staff1@happypaws.com (Happy Paws Shelter)
- staff2@ceburescue.org (Cebu Animal Rescue)
- staff3@pethavencebu.com (Pet Haven Cebu)

---

## Development Workflow

### Making Backend Changes

```bash
# 1. Edit Java files
# 2. Stop the application (Ctrl+C)
# 3. Rebuild and restart
mvn clean install
mvn spring-boot:run
```

**Or use Spring Boot DevTools (already included):**
- Just save the file, application will auto-restart

### Making Frontend Changes

```bash
# No restart needed!
# Vite will hot-reload automatically when you save files
```

---

## Useful Commands

### Backend

```bash
# Run tests
mvn test

# Skip tests
mvn clean install -DskipTests

# Run with specific profile
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Package as JAR
mvn clean package

# View logs
tail -f logs/application.log
```

### Web Frontend

```bash
# Start dev server
npm run dev

# Build for production
npm run build

# Preview production build
npm run preview

# Run linter
npm run lint

# Run tests
npm test
```

### Database

```bash
# Backup database
mysqldump -u happytails_user -p happytails_db > backup.sql

# Restore database
mysql -u happytails_user -p happytails_db < backup.sql

# Reset database (WARNING: Deletes all data)
mysql -u happytails_user -p -e "DROP DATABASE happytails_db; CREATE DATABASE happytails_db;"
```

---

## Architecture Overview

```
┌─────────────────┐
│   React Web     │ → Port 5173
│   (Frontend)    │
└────────┬────────┘
         │
         │ HTTP/REST
         ↓
┌─────────────────┐
│  Spring Boot    │ → Port 8080
│   (Backend)     │
└────────┬────────┘
         │
         │ JDBC
         ↓
┌─────────────────┐
│     MySQL       │ → Port 3306
│   (Database)    │
└─────────────────┘
```

---

## Next Steps

### For Development
1. Read [API Documentation](docs/API_DOCUMENTATION.md)
2. Review [Database Schema](docs/DATABASE_SCHEMA.md)
3. Check [Testing Guide](docs/TESTING_GUIDE.md)

### For Deployment
1. Follow [Deployment Guide](docs/DEPLOYMENT_GUIDE.md)
2. Configure SSL certificate
3. Set up database backups
4. Enable monitoring

### For Contributing
1. Create a feature branch
2. Write unit tests for new features
3. Follow existing code style
4. Submit pull request with description

---

## Getting Help

**Documentation:**
- [README.md](README.md) - Project overview
- [API_DOCUMENTATION.md](docs/API_DOCUMENTATION.md) - REST API reference
- [DATABASE_SCHEMA.md](docs/DATABASE_SCHEMA.md) - Database structure
- [TESTING_GUIDE.md](docs/TESTING_GUIDE.md) - Testing strategy
- [DEPLOYMENT_GUIDE.md](docs/DEPLOYMENT_GUIDE.md) - Production deployment

**Check Application Health:**
- Backend: http://localhost:8080/actuator/health
- Frontend: http://localhost:5173

**Logs:**
- Backend: Console output or `logs/application.log`
- Frontend: Browser console (F12)
- Database: MySQL error log

---

## Success Checklist

After completing this guide, you should be able to:

- ✅ Access the web app at http://localhost:5173
- ✅ Login as adopter or shelter staff
- ✅ Browse and filter pets
- ✅ View pet details
- ✅ Submit adoption applications
- ✅ Manage applications (as staff)
- ✅ Update pet profiles (as staff)
- ✅ Test API endpoints with cURL/Postman

**Congratulations! You're ready to develop with HappyTails! 🎉**

---

## Quick Reference Card

| Task | Command |
|------|---------|
| Start Backend | `cd backend && mvn spring-boot:run` |
| Start Frontend | `cd web && npm run dev` |
| Access Web App | http://localhost:5173 |
| Access API | http://localhost:8080/api |
| View API Docs | See [API_DOCUMENTATION.md](docs/API_DOCUMENTATION.md) |
| Test Account | john.doe@gmail.com / Test123! |
| Load Sample Data | `mysql -u user -p happytails_db < sample-data.sql` |
| Run Tests | `mvn test` (backend) / `npm test` (frontend) |

---

**Happy Coding! 🐾**
