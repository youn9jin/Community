#!/bin/bash
set -euo pipefail
exec > >(tee /var/log/user-data.log) 2>&1

REGION="ap-northeast-2"
ACCOUNT_ID="417780655988"
ECR_REGISTRY="${ACCOUNT_ID}.dkr.ecr.${REGION}.amazonaws.com"
IMAGE_TAG="__IMAGE_TAG__"

# AWS CLI가 없으면 설치 (현재 AMI에 안 깔려있는 게 확인됨)
if ! command -v aws &> /dev/null; then
  apt-get update -y
  apt-get install -y unzip curl
  curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "/tmp/awscliv2.zip"
  unzip -q /tmp/awscliv2.zip -d /tmp
  /tmp/aws/install
  rm -rf /tmp/awscliv2.zip /tmp/aws
fi

APP_DIR="/opt/app"
LOG_DIR="${APP_DIR}/logs"
COMPOSE_DIR="${APP_DIR}/compose"
mkdir -p "${LOG_DIR}" "${COMPOSE_DIR}"
chown -R 10001:10001 "${LOG_DIR}"

aws ecr get-login-password --region "${REGION}" \
  | docker login --username AWS --password-stdin "${ECR_REGISTRY}"

DB_PASSWORD=$(aws ssm get-parameter \
  --name "/reeve-community/db-password" \
  --with-decryption --region "${REGION}" \
  --query "Parameter.Value" --output text)

JWT_SECRET=$(aws ssm get-parameter \
  --name "/reeve-community/jwt-secret" \
  --with-decryption --region "${REGION}" \
  --query "Parameter.Value" --output text)

cat > "${COMPOSE_DIR}/.env" <<EOF
ECR_REGISTRY=${ECR_REGISTRY}
IMAGE_TAG=${IMAGE_TAG}
RDS_ENDPOINT=reeve-community-db.czwoa6mm0wln.ap-northeast-2.rds.amazonaws.com
DB_PASSWORD=${DB_PASSWORD}
JWT_SECRET=${JWT_SECRET}
JWT_ACCESS_EXPIRATION=1800000
JWT_REFRESH_EXPIRATION=1209600000
S3_BUCKET_NAME=reeve-community-images-${ACCOUNT_ID}
EOF
chmod 600 "${COMPOSE_DIR}/.env"

cat > "${COMPOSE_DIR}/docker-compose.yml" <<'COMPOSE_EOF'
x-logging: &default-logging
  driver: json-file
  options:
    max-size: "10m"
    max-file: "3"
services:
  nginx:
    image: ${ECR_REGISTRY}/reeve-nginx:${IMAGE_TAG}
    restart: unless-stopped
    ports:
      - "80:8080"
    depends_on:
      - springboot
    networks:
      - reeve-be-net
    mem_limit: 256m
    logging: *default-logging
  springboot:
    image: ${ECR_REGISTRY}/reeve-be:${IMAGE_TAG}
    restart: unless-stopped
    environment:
      SPRING_PROFILES_ACTIVE: prod
      RDS_ENDPOINT: ${RDS_ENDPOINT}
      DB_PASSWORD: ${DB_PASSWORD}
      JWT_SECRET: ${JWT_SECRET}
      JWT_ACCESS_EXPIRATION: ${JWT_ACCESS_EXPIRATION}
      JWT_REFRESH_EXPIRATION: ${JWT_REFRESH_EXPIRATION}
      S3_BUCKET_NAME: ${S3_BUCKET_NAME}
      JAVA_OPTS: "-Xmx512m"
    volumes:
      - /opt/app/logs:/opt/app/logs
    networks:
      - reeve-be-net
    mem_limit: 896m
    logging: *default-logging
networks:
  reeve-be-net:
    driver: bridge
COMPOSE_EOF

cd "${COMPOSE_DIR}"
docker compose pull
docker compose up -d