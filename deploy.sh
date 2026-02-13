#!/bin/bash

# Enterprise CRM Deployment Script
# Usage: ./deploy.sh

set -e

echo "=== Enterprise CRM Deployment ==="

# Configuration
APP_NAME="enterprise-crm"
APP_DIR="/opt/crm"
JAR_FILE="enterprise-crm.jar"
SERVICE_NAME="crm"

# Colors
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${YELLOW}Step 1: Building application...${NC}"
mvn clean package -DskipTests

echo -e "${YELLOW}Step 2: Stopping service...${NC}"
sudo systemctl stop $SERVICE_NAME || true

echo -e "${YELLOW}Step 3: Creating application directory...${NC}"
sudo mkdir -p $APP_DIR
sudo mkdir -p $APP_DIR/logs

echo -e "${YELLOW}Step 4: Copying JAR file...${NC}"
sudo cp target/$JAR_FILE $APP_DIR/

echo -e "${YELLOW}Step 5: Setting permissions...${NC}"
sudo useradd -r -s /bin/false crm || true
sudo chown -R crm:crm $APP_DIR

echo -e "${YELLOW}Step 6: Installing systemd service...${NC}"
sudo cp crm.service /etc/systemd/system/
sudo systemctl daemon-reload

echo -e "${YELLOW}Step 7: Starting service...${NC}"
sudo systemctl start $SERVICE_NAME
sudo systemctl enable $SERVICE_NAME

echo -e "${GREEN}Deployment completed successfully!${NC}"
echo ""
echo "Service status:"
sudo systemctl status $SERVICE_NAME --no-pager

echo ""
echo "Useful commands:"
echo "  View logs: journalctl -u $SERVICE_NAME -f"
echo "  Stop service: sudo systemctl stop $SERVICE_NAME"
echo "  Restart service: sudo systemctl restart $SERVICE_NAME"
echo "  Check status: sudo systemctl status $SERVICE_NAME"
