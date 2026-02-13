# Enterprise CRM Application

Professional CRM application built with Java 17, Spring Boot 2.7.18, PostgreSQL, Redis, and Thymeleaf.

## Features

- **Customer Management**: Complete customer lifecycle management
- **Lead Tracking**: Lead capture, qualification, and conversion
- **Sales Pipeline**: Visual opportunity tracking with stages
- **Activity Management**: Tasks, meetings, calls, and emails
- **Dashboard**: Real-time KPIs and metrics
- **User Management**: Role-based access control
- **Session Management**: Redis-based distributed sessions

## Technology Stack

- Java 17
- Spring Boot 2.7.18
- Maven
- PostgreSQL 15
- Redis 7
- Thymeleaf
- Bootstrap 5
- Spring Security
- Spring Data JPA
- Flyway

## Prerequisites

- JDK 17+
- Maven 3.6+
- PostgreSQL 15+
- Redis 7+
- Docker (optional, for local development)

## Quick Start

### Local Development

1. **Start dependencies** (using Docker Compose):
   ```bash
   docker-compose up -d
   ```

2. **Run application**:
   ```bash
   mvn spring-boot:run -Dspring-boot.run.profiles=local
   ```

3. **Access application**:
   - URL: http://localhost:8080/crm
   - Default credentials: `admin@crm.com` / `Admin@123`

### Production Deployment

1. **Build JAR**:
   ```bash
   mvn clean package -DskipTests
   ```

2. **Deploy to server**:
   ```bash
   chmod +x deploy.sh
   ./deploy.sh
   ```

3. **Or manually**:
   ```bash
   # Copy JAR to server
   scp target/enterprise-crm.jar root@185.136.206.32:/opt/crm/
   
   # Copy service file
   scp crm.service root@185.136.206.32:/etc/systemd/system/
   
   # On server
   sudo systemctl daemon-reload
   sudo systemctl start crm
   sudo systemctl enable crm
   ```

## Configuration

### Environment Profiles

- **local**: Development environment (localhost)
- **prod**: Production environment (server)

Activate profile:
```bash
# Via environment variable
export SPRING_PROFILE=prod

# Via command line
java -jar enterprise-crm.jar --spring.profiles.active=prod
```

### Database Configuration

Edit `src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://YOUR_HOST:5432/crm_db
    username: YOUR_USERNAME
    password: YOUR_PASSWORD
```

### Redis Configuration

```yaml
spring:
  redis:
    host: YOUR_REDIS_HOST
    port: 6379
    password: YOUR_PASSWORD
```

## Linux Server Management

### Service Commands

```bash
# Start application
sudo systemctl start crm

# Stop application
sudo systemctl stop crm

# Restart application
sudo systemctl restart crm

# Check status
sudo systemctl status crm

# Enable auto-start on boot
sudo systemctl enable crm
```

### View Logs

```bash
# Real-time logs
journalctl -u crm -f

# Last 100 lines
journalctl -u crm -n 100 --no-pager

# Application logs
tail -f /opt/crm/logs/crm-application.log
```

### Monitor Resources

```bash
# Check Java process
ps aux | grep java

# Monitor CPU/Memory
top -p $(pgrep -f enterprise-crm)

# Check port
netstat -tulpn | grep 8080
```

## Database Management

### Access PostgreSQL

```bash
# Using Docker
docker exec -it crm-postgres psql -U crm_user -d crm_db

# Direct connection
psql -h localhost -U crm_user -d crm_db
```

### Flyway Migrations

Migrations are automatically applied on startup. Manual execution:

```bash
mvn flyway:migrate
mvn flyway:info
mvn flyway:validate
```

## Default Credentials

- **Email**: admin@crm.com
- **Password**: Admin@123

**Important**: Change default password after first login!

## API Documentation

Swagger UI available at: http://localhost:8080/crm/swagger-ui.html

## Project Structure

```
src/
├── main/
│   ├── java/com/cengiz/crm/
│   │   ├── config/          # Configuration classes
│   │   ├── controller/      # Web controllers
│   │   ├── entity/          # JPA entities
│   │   ├── enums/           # Enumerations
│   │   ├── repository/      # Data repositories
│   │   ├── security/        # Security components
│   │   └── service/         # Business logic
│   └── resources/
│       ├── db/migration/    # Flyway migrations
│       ├── templates/       # Thymeleaf templates
│       └── application.yml  # Configuration
└── test/                    # Unit tests
```

## Migration to Java 21 & Spring Boot 3.x

See [implementation_plan.md](docs/implementation_plan.md) for detailed migration guide.

### Key Changes for Java 21

- Virtual Threads support
- Pattern Matching for Switch
- Record Patterns
- Sequenced Collections

### Key Changes for Spring Boot 3.x

- `javax.*` → `jakarta.*` namespace change
- Security configuration updates
- Minimum Java 17 required

## Troubleshooting

### Application won't start

```bash
# Check logs
journalctl -u crm -n 50

# Check if port is in use
sudo lsof -i :8080

# Check database connection
psql -h YOUR_HOST -U crm_user -d crm_db
```

### Database connection issues

```bash
# Check PostgreSQL status
sudo systemctl status postgresql

# Check Redis status
sudo systemctl status redis
```

### Permission issues

```bash
# Fix ownership
sudo chown -R crm:crm /opt/crm

# Fix permissions
sudo chmod 755 /opt/crm
sudo chmod 644 /opt/crm/enterprise-crm.jar
```

## Contributing

1. Fork the repository
2. Create feature branch (`git checkout -b feature/amazing-feature`)
3. Commit changes (`git commit -m 'Add amazing feature'`)
4. Push to branch (`git push origin feature/amazing-feature`)
5. Open Pull Request

## License

This project is proprietary software.

## Support

For issues and questions, contact: admin@crm.com
