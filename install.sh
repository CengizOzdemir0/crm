#!/bin/bash

# CRM Sunucu Kurulum Script'i
# Bu script'i sunucuda çalıştırarak uygulamayı otomatik kurabilirsiniz

set -e

echo "========================================="
echo "  CRM Uygulaması Sunucu Kurulumu"
echo "========================================="
echo ""

# Renk kodları
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Fonksiyonlar
print_success() {
    echo -e "${GREEN}✓ $1${NC}"
}

print_info() {
    echo -e "${YELLOW}→ $1${NC}"
}

print_error() {
    echo -e "${RED}✗ $1${NC}"
}

# 1. Sistem güncellemesi
print_info "Sistem güncelleniyor..."
apt update
print_success "Sistem güncellendi"

# 2. Java 17 kurulumu
print_info "Java 17 kuruluyor..."
apt install -y openjdk-17-jdk
java -version
print_success "Java 17 kuruldu"

# 3. Maven kurulumu
print_info "Maven kuruluyor..."
apt install -y maven
mvn -version
print_success "Maven kuruldu"

# 4. Git kurulumu
print_info "Git kuruluyor..."
apt install -y git
git --version
print_success "Git kuruldu"

# 5. PostgreSQL kurulumu
print_info "PostgreSQL kuruluyor..."
apt install -y postgresql postgresql-contrib
systemctl start postgresql
systemctl enable postgresql
print_success "PostgreSQL kuruldu"

# 6. Redis kurulumu
print_info "Redis kuruluyor..."
apt install -y redis-server
systemctl start redis-server
systemctl enable redis-server
print_success "Redis kuruldu"

# 7. Veritabanı oluşturma
print_info "Veritabanı oluşturuluyor..."
sudo -u postgres psql -c "DROP DATABASE IF EXISTS crm_db;"
sudo -u postgres psql -c "DROP USER IF EXISTS crm_user;"
sudo -u postgres psql -c "CREATE DATABASE crm_db;"
sudo -u postgres psql -c "CREATE USER crm_user WITH ENCRYPTED PASSWORD 'crm_password';"
sudo -u postgres psql -c "GRANT ALL PRIVILEGES ON DATABASE crm_db TO crm_user;"
sudo -u postgres psql -c "ALTER DATABASE crm_db OWNER TO crm_user;"
print_success "Veritabanı oluşturuldu"

# 8. Uygulama dizini oluşturma
print_info "Uygulama dizini oluşturuluyor..."
mkdir -p /opt/crm
cd /opt/crm
print_success "Uygulama dizini oluşturuldu"

# 9. GitHub'dan projeyi çekme
print_info "Proje GitHub'dan çekiliyor..."
if [ -d ".git" ]; then
    print_info "Git repository zaten mevcut, güncelleniyor..."
    git pull origin main
else
    print_info "Git repository clone ediliyor..."
    git clone https://github.com/CengizOzdemir0/crm.git .
fi
print_success "Proje çekildi"

# 10. Uygulamayı derleme
print_info "Uygulama derleniyor (bu birkaç dakika sürebilir)..."
mvn clean package -DskipTests
print_success "Uygulama derlendi"

# 11. CRM kullanıcısı oluşturma
print_info "CRM sistem kullanıcısı oluşturuluyor..."
if id "crm" &>/dev/null; then
    print_info "CRM kullanıcısı zaten mevcut"
else
    useradd -r -s /bin/false crm
    print_success "CRM kullanıcısı oluşturuldu"
fi

# 12. Dizin sahipliğini ayarlama
print_info "Dizin izinleri ayarlanıyor..."
chown -R crm:crm /opt/crm
chmod 755 /opt/crm
print_success "Dizin izinleri ayarlandı"

# 13. Systemd servisini kurma
print_info "Systemd servisi kuruluyor..."
cp /opt/crm/crm.service /etc/systemd/system/
systemctl daemon-reload
print_success "Systemd servisi kuruldu"

# 14. Servisi başlatma
print_info "CRM servisi başlatılıyor..."
systemctl stop crm 2>/dev/null || true
systemctl start crm
systemctl enable crm
print_success "CRM servisi başlatıldı"

# 15. Durum kontrolü
echo ""
echo "========================================="
echo "  Kurulum Tamamlandı!"
echo "========================================="
echo ""

sleep 3

print_info "Servis durumu kontrol ediliyor..."
systemctl status crm --no-pager

echo ""
echo "========================================="
echo "  Erişim Bilgileri"
echo "========================================="
echo ""
echo "URL: http://185.136.206.32:8080/crm"
echo "Email: admin@crm.com"
echo "Şifre: Admin@123"
echo ""
echo "========================================="
echo "  Faydalı Komutlar"
echo "========================================="
echo ""
echo "Logları izle:"
echo "  journalctl -u crm -f"
echo ""
echo "Servisi yeniden başlat:"
echo "  systemctl restart crm"
echo ""
echo "Servisi durdur:"
echo "  systemctl stop crm"
echo ""
echo "Servis durumu:"
echo "  systemctl status crm"
echo ""
echo "Güncelleme için:"
echo "  /opt/crm/update.sh"
echo ""
