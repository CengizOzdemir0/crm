y# Enterprise CRM - Linux Management Guide

## Service Management Commands

### Basic Service Control

```bash
# Start the CRM application
sudo systemctl start crm

# Stop the CRM application
sudo systemctl stop crm

# Restart the CRM application
sudo systemctl restart crm

# Check service status
sudo systemctl status crm

# Enable auto-start on boot
sudo systemctl enable crm

# Disable auto-start
sudo systemctl disable crm

# Reload systemd configuration
sudo systemctl daemon-reload
```

### Log Management

```bash
# View real-time logs
journalctl -u crm -f

# View last 100 lines
journalctl -u crm -n 100

# View logs from today
journalctl -u crm --since today

# View logs from specific time
journalctl -u crm --since "2024-01-01 10:00:00"

# View application log file
tail -f /opt/crm/logs/crm-application.log

# View last 200 lines of application log
tail -n 200 /opt/crm/logs/crm-application.log
```

### Process Management

```bash
# Find CRM Java process
ps aux | grep enterprise-crm

# Get process ID
pgrep -f enterprise-crm

# Kill process (if needed)
sudo kill -9 $(pgrep -f enterprise-crm)

# Monitor CPU and memory usage
top -p $(pgrep -f enterprise-crm)

# Detailed process information
htop -p $(pgrep -f enterprise-crm)
```

### Network and Port Management

```bash
# Check if port 8080 is in use
sudo lsof -i :8080

# Check all listening ports
sudo netstat -tulpn | grep LISTEN

# Check CRM application port
sudo netstat -tulpn | grep 8080

# Test application endpoint
curl http://localhost:8080/crm/actuator/health
```

### Database Management

```bash
# Connect to PostgreSQL
psql -h 185.136.206.32 -U crm_user -d crm_db

# Check PostgreSQL status
sudo systemctl status postgresql

# Restart PostgreSQL
sudo systemctl restart postgresql

# View PostgreSQL logs
sudo journalctl -u postgresql -f

# Backup database
pg_dump -h 185.136.206.32 -U crm_user crm_db > backup_$(date +%Y%m%d).sql

# Restore database
psql -h 185.136.206.32 -U crm_user crm_db < backup_20240101.sql
```

### Redis Management

```bash
# Connect to Redis CLI
redis-cli -h 185.136.206.32

# Check Redis status
sudo systemctl status redis

# Restart Redis
sudo systemctl restart redis

# Monitor Redis
redis-cli -h 185.136.206.32 monitor

# Check Redis memory usage
redis-cli -h 185.136.206.32 info memory

# Flush all Redis data (CAUTION!)
redis-cli -h 185.136.206.32 FLUSHALL
```

### System Resources

```bash
# Check disk usage
df -h

# Check disk usage of CRM directory
du -sh /opt/crm

# Check memory usage
free -h

# Check CPU usage
mpstat

# Check system load
uptime

# View system information
uname -a
```

### File Management

```bash
# List CRM files
ls -lah /opt/crm

# Check file permissions
ls -l /opt/crm/enterprise-crm.jar

# Change ownership
sudo chown -R crm:crm /opt/crm

# Change permissions
sudo chmod 755 /opt/crm
sudo chmod 644 /opt/crm/enterprise-crm.jar

# View file size
ls -lh /opt/crm/enterprise-crm.jar

# Find large log files
find /opt/crm/logs -type f -size +100M
```

### Application Deployment

```bash
# Build application
mvn clean package -DskipTests

# Copy JAR to server
scp target/enterprise-crm.jar root@185.136.206.32:/opt/crm/

# Deploy using script
chmod +x deploy.sh
./deploy.sh

# Manual deployment steps
sudo systemctl stop crm
sudo cp target/enterprise-crm.jar /opt/crm/
sudo chown crm:crm /opt/crm/enterprise-crm.jar
sudo systemctl start crm
```

### Troubleshooting

```bash
# Check if application is running
sudo systemctl is-active crm

# Check if application is enabled
sudo systemctl is-enabled crm

# View failed service attempts
sudo systemctl list-units --failed

# Check application errors
journalctl -u crm -p err

# Check last boot logs
journalctl -u crm -b

# Verify Java version
java -version

# Check environment variables
sudo systemctl show crm --property=Environment

# Test database connection
psql -h 185.136.206.32 -U crm_user -d crm_db -c "SELECT 1"

# Test Redis connection
redis-cli -h 185.136.206.32 ping
```

### Performance Monitoring

```bash
# Monitor JVM heap usage
jstat -gc $(pgrep -f enterprise-crm)

# View thread dump
jstack $(pgrep -f enterprise-crm)

# View heap dump (creates file)
jmap -dump:format=b,file=heap.bin $(pgrep -f enterprise-crm)

# Monitor application metrics
curl http://localhost:8080/crm/actuator/metrics

# Check health endpoint
curl http://localhost:8080/crm/actuator/health
```

### Log Rotation

```bash
# Configure logrotate for CRM logs
sudo nano /etc/logrotate.d/crm

# Add this configuration:
/opt/crm/logs/*.log {
    daily
    rotate 30
    compress
    delaycompress
    notifempty
    create 0644 crm crm
    sharedscripts
    postrotate
        systemctl reload crm > /dev/null 2>&1 || true
    endscript
}

# Test logrotate
sudo logrotate -d /etc/logrotate.d/crm

# Force logrotate
sudo logrotate -f /etc/logrotate.d/crm
```

### Backup and Recovery

```bash
# Backup application
sudo tar -czf crm-backup-$(date +%Y%m%d).tar.gz /opt/crm

# Backup database
pg_dump -h 185.136.206.32 -U crm_user crm_db | gzip > crm-db-$(date +%Y%m%d).sql.gz

# Restore application
sudo tar -xzf crm-backup-20240101.tar.gz -C /

# Restore database
gunzip < crm-db-20240101.sql.gz | psql -h 185.136.206.32 -U crm_user crm_db
```

### Security

```bash
# Check firewall status
sudo ufw status

# Allow CRM port
sudo ufw allow 8080/tcp

# Check open ports
sudo ss -tulpn

# View failed login attempts
sudo journalctl -u crm | grep "Failed login"

# Check SELinux status (if applicable)
sestatus
```

## Quick Reference

| Task | Command |
|------|---------|
| Start CRM | `sudo systemctl start crm` |
| Stop CRM | `sudo systemctl stop crm` |
| Restart CRM | `sudo systemctl restart crm` |
| View logs | `journalctl -u crm -f` |
| Check status | `sudo systemctl status crm` |
| Find process | `ps aux \| grep enterprise-crm` |
| Check port | `sudo lsof -i :8080` |
| Test health | `curl localhost:8080/crm/actuator/health` |
| Backup DB | `pg_dump -h HOST -U crm_user crm_db > backup.sql` |
| View errors | `journalctl -u crm -p err` |
