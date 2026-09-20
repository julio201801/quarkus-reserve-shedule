#!/bin/bash

# Mover el contexto de ejecución a la raíz del proyecto dinámicamente
cd "$(dirname "$0")/.."

# 1. Variables de configuración
APP_NAME="reserve-schedule-native-community"
IMAGE_NAME="mitocode/reserve-schedule-native-community" # O el nombre que uses
TAG="latest"
DOCKERFILE="docker/Dockerfile.native-community" #
NETWORK_NAME="mitocode-network"

# Colores
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'


# 0. (Paso previo recomendado) Limpiar contenedor anterior si existe
echo -e "${GREEN}0. Deteniendo y eliminando contenedor previo...${NC}"
# El '|| true' evita que el script falle si el contenedor no existe
docker stop $APP_NAME 2>/dev/null || true
docker rm $APP_NAME 2>/dev/null || true

# 1. Construir la nueva imagen (Tu parte)
echo -e "${GREEN}1. Construyendo imagen Docker...${NC}"
docker build -f $DOCKERFILE -t $IMAGE_NAME:$TAG .

# Verificar si el build fue exitoso antes de continuar
if [ $? -ne 0 ]; then
    echo -e "${RED}Error al construir la imagen. Abortando.${NC}"
    exit 1
fi

# 2. EJECUTAR EL CONTENEDOR (La parte que faltaba)
echo -e "${GREEN}2. Ejecutando nuevo contenedor...${NC}"

docker run -d \
  --name $APP_NAME \
  --network $NETWORK_NAME \
  -p 8083:8080 \
  $IMAGE_NAME:$TAG

# 3. Mostrar logs para verificar arranque
echo -e "${GREEN}3. Logs del contenedor (Ctrl+C para salir):${NC}"
docker logs -f $APP_NAME