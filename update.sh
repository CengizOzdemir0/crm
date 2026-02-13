#!/bin/bash

# CRM Güncelleme Script'i
# Kod değişikliklerinden sonra uygulamayı güncellemek için kullanılır

set -e

echo "========================================="
echo "  CRM Uygulaması Güncelleme"
echo "========================================="
echo ""

# Renk kodları
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

print_success() {
    echo -e "${GREEN}✓ $1${NC}"
}

print_info() {
    echo -e "${YELLOW}→ $1${NC}"
}

# 1. Servisi durdur
print_info "CRM servisi durduruluyor..."
systemctl stop crm
print_success "Servis durduruldu"

# 2. Güncellemeleri çek
print_info "Kod güncellemeleri çekiliyor..."
cd /opt/crm
git pull origin main
print_success "Güncellemeler çekildi"

# 3. Yeniden derle
print_info "Uygulama yeniden derleniyor..."
mvn clean package -DskipTests
print_success "Uygulama derlendi"

# 4. Servisi başlat
print_info "CRM servisi başlatılıyor..."
systemctl start crm
print_success "Servis başlatıldı"

# 5. Durum kontrolü
echo ""
print_info "Servis durumu kontrol ediliyor..."
sleep 3
systemctl status crm --no-pager

echo ""
echo "========================================="
echo "  Güncelleme Tamamlandı!"
echo "========================================="
echo ""
echo "Logları izlemek için:"
echo "  journalctl -u crm -f"
echo ""
