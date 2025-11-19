# HappyTails Deployment Guide

## Overview

This guide covers deploying the HappyTails platform to production environments.

## Architecture

```
┌─────────────┐         ┌──────────────┐         ┌──────────────┐
│   React     │ ──────> │  Spring Boot │ ──────> │    MySQL     │
│   Web App   │         │    Backend   │         │   Database   │
│ (Port 80)   │         │  (Port 8080) │         │ (Port 3306)  │
└─────────────┘         └──────────────┘         └──────────────┘
       │
       │
┌─────────────┐
│   Android   │ ──────> (Same Backend)
│  Mobile App │
└─────────────┘
```

## Prerequisites

### Required Software
- Java 17 or higher
- Node.js 18+ and npm
- MySQL 8.0+
- Git

### Recommended Production Environment
- **Backend**: Linux server (Ubuntu 22.04 LTS)
- **Database**: MySQL 8.0 (can be same server or separate)
- **Web**: Nginx as reverse proxy
- **Memory**: Minimum 2GB RAM for backend
- **Storage**: Minimum 10GB

---

## Database Setup (Production)

### 1. Install MySQL

```bash
sudo apt update
sudo apt install mysql-server
sudo mysql_secure_installation
```

### 2. Create Database and User

```sql
-- Login to MySQL
sudo mysql

-- Create database
CREATE DATABASE happytails_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Create dedicated user
CREATE USER 'happytails_user'@'localhost' IDENTIFIED BY 'STRONG_PASSWORD_HERE';

-- Grant privileges
GRANT ALL PRIVILEGES ON happytails_db.* TO 'happytails_user'@'localhost';

FLUSH PRIVILEGES;
EXIT;
```

### 3. Configure MySQL for Production

Edit `/etc/mysql/mysql.conf.d/mysqld.cnf`:

```ini
[mysqld]
max_connections = 200
innodb_buffer_pool_size = 1G
```

Restart MySQL:
```bash
sudo systemctl restart mysql
```

---

## Backend Deployment (Spring Boot)

### Option 1: Standalone JAR (Recommended for Small Scale)

#### 1. Build the Application

```bash
cd backend
./mvnw clean package -DskipTests
```

This creates: `target/backend-0.0.1-SNAPSHOT.jar`

#### 2. Configure Environment

Create `/etc/happytails/application-prod.properties`:

```properties
# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/happytails_db
spring.datasource.username=happytails_user
spring.datasource.password=YOUR_STRONG_PASSWORD

# JPA Configuration
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false

# JWT Configuration
app.jwtSecret=${APP_JWT_SECRET}
app.jwtExpirationMs=86400000

# Logging
logging.level.root=WARN
logging.level.com.happytails=INFO

# Server Configuration
server.port=8080
```

#### 3. Set Environment Variables

```bash
export APP_JWT_SECRET="YOUR_SECURE_64_BYTE_BASE64_SECRET"
export SPRING_PROFILES_ACTIVE=prod
```

To generate secure JWT secret (PowerShell):
```powershell
$bytes = New-Object 'System.Byte[]' 64
[System.Security.Cryptography.RandomNumberGenerator]::Create().GetBytes($bytes)
[System.Convert]::ToBase64String($bytes)
```

#### 4. Create Systemd Service

Create `/etc/systemd/system/happytails-backend.service`:

```ini
[Unit]
Description=HappyTails Backend Service
After=mysql.service

[Service]
Type=simple
User=happytails
WorkingDirectory=/opt/happytails
Environment="APP_JWT_SECRET=YOUR_SECRET_HERE"
Environment="SPRING_PROFILES_ACTIVE=prod"
ExecStart=/usr/bin/java -jar /opt/happytails/backend.jar --spring.config.location=/etc/happytails/application-prod.properties
Restart=always
RestartSec=10

[Install]
WantedBy=multi-user.target
```

#### 5. Deploy and Start

```bash
# Create deployment directory
sudo mkdir -p /opt/happytails
sudo useradd -r -s /bin/false happytails

# Copy JAR
sudo cp target/backend-0.0.1-SNAPSHOT.jar /opt/happytails/backend.jar
sudo chown -R happytails:happytails /opt/happytails

# Start service
sudo systemctl daemon-reload
sudo systemctl enable happytails-backend
sudo systemctl start happytails-backend

# Check status
sudo systemctl status happytails-backend
```

#### 6. View Logs

```bash
sudo journalctl -u happytails-backend -f
```

---

### Option 2: Docker (Recommended for Scalability)

#### 1. Create Dockerfile

`backend/Dockerfile`:
```dockerfile
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

COPY target/backend-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
```

#### 2. Create docker-compose.yml

```yaml
version: '3.8'

services:
  mysql:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: rootpassword
      MYSQL_DATABASE: happytails_db
      MYSQL_USER: happytails_user
      MYSQL_PASSWORD: securepassword
    ports:
      - "3306:3306"
    volumes:
      - mysql-data:/var/lib/mysql

  backend:
    build: ./backend
    ports:
      - "8080:8080"
    environment:
      SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/happytails_db
      SPRING_DATASOURCE_USERNAME: happytails_user
      SPRING_DATASOURCE_PASSWORD: securepassword
      APP_JWT_SECRET: ${APP_JWT_SECRET}
    depends_on:
      - mysql

volumes:
  mysql-data:
```

#### 3. Deploy with Docker

```bash
# Build and start
docker-compose up -d

# View logs
docker-compose logs -f backend

# Stop
docker-compose down
```

---

## Web Frontend Deployment (React)

### 1. Configure API URL

Update `web/src/services/api.js`:

```javascript
const BASE_URL = import.meta.env.VITE_API_URL || 'https://api.happytails.com/api';
```

Create `web/.env.production`:

```
VITE_API_URL=https://api.happytails.com/api
```

### 2. Build for Production

```bash
cd web
npm install
npm run build
```

This creates optimized static files in `web/dist/`

### 3. Deploy to Nginx

#### Install Nginx

```bash
sudo apt install nginx
```

#### Configure Nginx

Create `/etc/nginx/sites-available/happytails`:

```nginx
server {
    listen 80;
    server_name happytails.com www.happytails.com;

    root /var/www/happytails;
    index index.html;

    # Gzip compression
    gzip on;
    gzip_types text/plain text/css application/json application/javascript;

    # Serve React app
    location / {
        try_files $uri $uri/ /index.html;
    }

    # Proxy API requests to backend
    location /api/ {
        proxy_pass http://localhost:8080/api/;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection 'upgrade';
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_cache_bypass $http_upgrade;
    }
}
```

#### Deploy

```bash
# Copy build files
sudo mkdir -p /var/www/happytails
sudo cp -r web/dist/* /var/www/happytails/

# Enable site
sudo ln -s /etc/nginx/sites-available/happytails /etc/nginx/sites-enabled/
sudo nginx -t
sudo systemctl restart nginx
```

---

## SSL/HTTPS Setup (Let's Encrypt)

### 1. Install Certbot

```bash
sudo apt install certbot python3-certbot-nginx
```

### 2. Obtain Certificate

```bash
sudo certbot --nginx -d happytails.com -d www.happytails.com
```

### 3. Auto-renewal

Certbot automatically sets up renewal. Test with:

```bash
sudo certbot renew --dry-run
```

---

## Mobile App Deployment (Android)

### 1. Build Release APK

In Android Studio:
1. Build → Generate Signed Bundle/APK
2. Select APK
3. Create/select keystore
4. Build release variant

Or via command line:

```bash
cd mobile
./gradlew assembleRelease
```

### 2. Distribute

**Options**:
- Google Play Store (official)
- Direct APK download from website
- Firebase App Distribution (beta testing)

---

## Monitoring and Maintenance

### 1. Application Monitoring

**Spring Boot Actuator** (already included):

Add to `application-prod.properties`:

```properties
management.endpoints.web.exposure.include=health,metrics,info
management.endpoint.health.show-details=when-authorized
```

Access metrics: `https://api.happytails.com/actuator/metrics`

### 2. Log Management

Configure centralized logging in `application-prod.properties`:

```properties
logging.file.name=/var/log/happytails/application.log
logging.pattern.file=%d{yyyy-MM-dd HH:mm:ss} - %msg%n
```

Rotate logs with logrotate:

```bash
sudo nano /etc/logrotate.d/happytails
```

```
/var/log/happytails/*.log {
    daily
    rotate 14
    compress
    missingok
    notifempty
}
```

### 3. Database Backups

Create backup script `/usr/local/bin/backup-happytails.sh`:

```bash
#!/bin/bash
BACKUP_DIR="/var/backups/happytails"
DATE=$(date +%Y%m%d_%H%M%S)

mkdir -p $BACKUP_DIR
mysqldump -u happytails_user -p'PASSWORD' happytails_db > $BACKUP_DIR/backup_$DATE.sql
gzip $BACKUP_DIR/backup_$DATE.sql

# Keep only last 30 days
find $BACKUP_DIR -name "*.gz" -mtime +30 -delete
```

Schedule with cron:

```bash
0 2 * * * /usr/local/bin/backup-happytails.sh
```

---

## Performance Optimization

### 1. Backend Optimization

```properties
# Connection pooling (HikariCP is default)
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5

# JPA optimization
spring.jpa.properties.hibernate.jdbc.batch_size=20
spring.jpa.properties.hibernate.order_inserts=true
```

### 2. Database Indexing

Ensure indexes on frequently queried columns:

```sql
CREATE INDEX idx_pet_species ON pet(species);
CREATE INDEX idx_pet_status ON pet(status);
CREATE INDEX idx_pet_shelter ON pet(shelter_id);
CREATE INDEX idx_application_status ON application(status);
```

### 3. Nginx Caching

Add to Nginx config:

```nginx
# Cache static assets
location ~* \.(jpg|jpeg|png|gif|ico|css|js)$ {
    expires 1y;
    add_header Cache-Control "public, immutable";
}
```

---

## Security Checklist

- ✅ Change default database passwords
- ✅ Use strong JWT secret (64+ bytes)
- ✅ Enable HTTPS (SSL certificate)
- ✅ Configure firewall (UFW)
- ✅ Disable root SSH login
- ✅ Keep software updated
- ✅ Use environment variables for secrets
- ✅ Enable database backups
- ✅ Monitor application logs

---

## Rollback Procedure

If deployment fails:

```bash
# Stop new version
sudo systemctl stop happytails-backend

# Restore previous JAR
sudo cp /opt/happytails/backend.jar.backup /opt/happytails/backend.jar

# Restore database backup (if schema changed)
mysql -u happytails_user -p happytails_db < /var/backups/happytails/backup_TIMESTAMP.sql

# Start service
sudo systemctl start happytails-backend
```

---

## Troubleshooting

### Backend won't start

```bash
# Check logs
sudo journalctl -u happytails-backend -n 100

# Common issues:
# - Database connection failed → verify credentials
# - Port already in use → change server.port
# - Out of memory → increase heap size: -Xmx1024m
```

### Web app shows blank page

- Check browser console for errors
- Verify API URL configuration
- Check CORS settings in backend
- Ensure build completed successfully

### Database connection errors

```bash
# Test connection
mysql -u happytails_user -p happytails_db

# Check MySQL status
sudo systemctl status mysql
```

---

## Production Checklist

Before go-live:

- ☐ Database backups configured
- ☐ SSL certificate installed
- ☐ Monitoring enabled
- ☐ Log rotation configured
- ☐ Performance tested (NFR-1: < 400ms)
- ☐ Security audit completed
- ☐ Documentation updated
- ☐ Rollback procedure tested
- ☐ Team trained on deployment process

---

## Support and Maintenance

**Regular Tasks**:
- Daily: Monitor application logs
- Weekly: Review metrics and performance
- Monthly: Security updates, backup verification
- Quarterly: Performance optimization, capacity planning
