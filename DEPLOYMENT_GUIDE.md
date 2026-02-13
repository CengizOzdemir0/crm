# GitHub'dan Sunucuya Deployment Rehberi

## Ön Hazırlık

### 1. GitHub'a Projeyi Push Etme

```bash
# Proje dizinine git
cd c:\Users\User\Desktop\Cengiz\Projeler\CRM

# Git başlat (eğer başlatılmadıysa)
git init

# Tüm dosyaları ekle
git add .

# İlk commit
git commit -m "İlk commit: Kurumsal CRM uygulaması"

# Ana branch'i main olarak ayarla
git branch -M main

# Remote repository ekle
git remote add origin https://github.com/CengizOzdemir0/crm.git

# GitHub'a push et
git push -u origin main
```

## Sunucuda Deployment

### 2. Sunucuya Bağlan

```bash
ssh root@185.136.206.32
```

### 3. Gerekli Yazılımları Kur

```bash
# Java 17 kurulumu
apt update
apt install -y openjdk-17-jdk

# Maven kurulumu
apt install -y maven

# Git kurulumu
apt install -y git

# PostgreSQL kurulumu (eğer yoksa)
apt install -y postgresql postgresql-contrib

# Redis kurulumu (eğer yoksa)
apt install -y redis-server

# Versiyonları kontrol et
java -version
mvn -version
git --version
```

### 4. PostgreSQL Veritabanını Hazırla

```bash
# PostgreSQL'e geç
sudo -u postgres psql

# Veritabanı ve kullanıcı oluştur
CREATE DATABASE crm_db;
CREATE USER crm_user WITH ENCRYPTED PASSWORD 'crm_password';
GRANT ALL PRIVILEGES ON DATABASE crm_db TO crm_user;
\q
```

### 5. Redis'i Başlat

```bash
# Redis'i başlat
systemctl start redis-server
systemctl enable redis-server

# Redis durumunu kontrol et
systemctl status redis-server
```

### 6. Projeyi GitHub'dan Çek

```bash
# Uygulama dizini oluştur
mkdir -p /opt/crm
cd /opt/crm

# GitHub'dan projeyi clone et
git clone https://github.com/CengizOzdemir0/crm.git .

# Proje içeriğini kontrol et
ls -la
```

### 7. Uygulamayı Derle

```bash
# Maven ile derle
cd /opt/crm
mvn clean package -DskipTests

# JAR dosyasının oluştuğunu kontrol et
ls -lh target/enterprise-crm.jar
```

### 8. CRM Kullanıcısı Oluştur

```bash
# Sistem kullanıcısı oluştur
useradd -r -s /bin/false crm

# Dizin sahipliğini ayarla
chown -R crm:crm /opt/crm

# İzinleri kontrol et
ls -la /opt/crm
```

### 9. Systemd Servisini Kur

```bash
# Servis dosyasını kopyala
cp /opt/crm/crm.service /etc/systemd/system/

# Systemd'yi yeniden yükle
systemctl daemon-reload

# Servisi başlat
systemctl start crm

# Servisi otomatik başlatmayı etkinleştir
systemctl enable crm

# Servis durumunu kontrol et
systemctl status crm
```

### 10. Logları Kontrol Et

```bash
# Gerçek zamanlı log takibi
journalctl -u crm -f

# Son 100 satır
journalctl -u crm -n 100

# Hata logları
journalctl -u crm -p err
```

### 11. Uygulamayı Test Et

```bash
# Port kontrolü
netstat -tulpn | grep 8080

# Health check
curl http://localhost:8080/crm/actuator/health

# Tarayıcıdan erişim
# http://185.136.206.32:8080/crm
```

## Güncelleme (Update) İşlemi

Kod değişikliği yaptıktan sonra sunucuya deploy etmek için:

```bash
# 1. Local'de değişiklikleri commit et ve push et
cd c:\Users\User\Desktop\Cengiz\Projeler\CRM
git add .
git commit -m "Değişiklik açıklaması"
git push origin main

# 2. Sunucuya bağlan
ssh root@185.136.206.32

# 3. Servisi durdur
systemctl stop crm

# 4. Güncellemeleri çek
cd /opt/crm
git pull origin main

# 5. Yeniden derle
mvn clean package -DskipTests

# 6. Servisi başlat
systemctl start crm

# 7. Durumu kontrol et
systemctl status crm
journalctl -u crm -f
```

## Otomatik Deployment Script

Daha kolay güncelleme için script kullan:

```bash
# Sunucuda script oluştur
nano /opt/crm/update.sh
```

Script içeriği:

```bash
#!/bin/bash

echo "=== CRM Uygulaması Güncelleme ==="

# Servisi durdur
echo "Servis durduruluyor..."
systemctl stop crm

# Git güncellemelerini çek
echo "Kod güncelleniyor..."
cd /opt/crm
git pull origin main

# Yeniden derle
echo "Uygulama derleniyor..."
mvn clean package -DskipTests

# Servisi başlat
echo "Servis başlatılıyor..."
systemctl start crm

# Durum kontrolü
echo "Durum kontrol ediliyor..."
sleep 3
systemctl status crm --no-pager

echo "=== Güncelleme Tamamlandı ==="
```

Script'i çalıştırılabilir yap:

```bash
chmod +x /opt/crm/update.sh
```

Kullanım:

```bash
/opt/crm/update.sh
```

## Firewall Ayarları

Port 8080'i aç:

```bash
# UFW kullanıyorsan
ufw allow 8080/tcp
ufw reload

# iptables kullanıyorsan
iptables -A INPUT -p tcp --dport 8080 -j ACCEPT
iptables-save > /etc/iptables/rules.v4
```

## Yedekleme (Backup)

### Veritabanı Yedeği

```bash
# Yedek al
pg_dump -h 185.136.206.32 -U crm_user crm_db > /opt/backups/crm_db_$(date +%Y%m%d).sql

# Geri yükle
psql -h 185.136.206.32 -U crm_user crm_db < /opt/backups/crm_db_20240213.sql
```

### Uygulama Yedeği

```bash
# Tüm uygulamayı yedekle
tar -czf /opt/backups/crm_app_$(date +%Y%m%d).tar.gz /opt/crm

# Geri yükle
tar -xzf /opt/backups/crm_app_20240213.tar.gz -C /
```

## Sorun Giderme

### Uygulama Başlamıyor

```bash
# Logları kontrol et
journalctl -u crm -n 50

# Port kullanımda mı?
lsof -i :8080

# Java process var mı?
ps aux | grep java
```

### Veritabanı Bağlantı Hatası

```bash
# PostgreSQL çalışıyor mu?
systemctl status postgresql

# Bağlantıyı test et
psql -h 185.136.206.32 -U crm_user -d crm_db

# application.yml'i kontrol et
cat /opt/crm/src/main/resources/application.yml
```

### Redis Bağlantı Hatası

```bash
# Redis çalışıyor mu?
systemctl status redis-server

# Redis'e bağlan
redis-cli -h 185.136.206.32 ping
```

## Performans İzleme

```bash
# CPU ve Memory kullanımı
top -p $(pgrep -f enterprise-crm)

# Disk kullanımı
df -h

# Log dosya boyutları
du -sh /opt/crm/logs/*
```

## Güvenlik

### SSL/TLS Sertifikası (Opsiyonel)

Nginx reverse proxy ile HTTPS:

```bash
# Nginx kur
apt install -y nginx

# Certbot kur
apt install -y certbot python3-certbot-nginx

# SSL sertifikası al
certbot --nginx -d crm.yourdomain.com
```

## Varsayılan Giriş Bilgileri

- **URL**: http://185.136.206.32:8080/crm
- **Email**: admin@crm.com
- **Şifre**: Admin@123

**ÖNEMLİ**: İlk girişten sonra şifreyi değiştir!

## Faydalı Komutlar

```bash
# Servisi yeniden başlat
systemctl restart crm

# Servisi durdur
systemctl stop crm

# Servisi başlat
systemctl start crm

# Servis durumu
systemctl status crm

# Logları izle
journalctl -u crm -f

# Son hatalar
journalctl -u crm -p err -n 20
```

## Destek

Sorun yaşarsan logları kontrol et:

```bash
journalctl -u crm -n 100 > /tmp/crm_logs.txt
cat /tmp/crm_logs.txt
```
