#!/usr/bin/env bash

# Exit immediately if a command exits with a non-zero status
set -e

# Color codes for pretty printing
RED='\033[0;31m'
GREEN='\033[0;32m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Print header
echo -e "${BLUE}===================================================${NC}"
echo -e "${CYAN}          Spring Boot Project Runner               ${NC}"
echo -e "${BLUE}===================================================${NC}"

# Check Java installation
if ! command -v java &> /dev/null; then
    echo -e "${RED}Error: Java is not installed or not in PATH.${NC}"
    exit 1
fi

JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d '"' -f 2)
echo -e "${GREEN}✔ Found Java version: ${JAVA_VERSION}${NC}"

# Define menus/commands
show_help() {
    echo -e "\n${YELLOW}Usage:${NC} ./run.sh [command]"
    echo -e "\n${YELLOW}Available Commands:${NC}"
    echo -e "  ${GREEN}dev${NC}      Run in development mode (./gradlew bootRun)"
    echo -e "  ${GREEN}build${NC}    Build the executable JAR (./gradlew bootJar)"
    echo -e "  ${GREEN}prod${NC}     Build and run the executable JAR"
    echo -e "  ${GREEN}test${NC}     Run all tests (./gradlew test)"
    echo -e "  ${GREEN}clean${NC}    Clean build files (./gradlew clean)"
    echo -e "  ${GREEN}help${NC}     Show this help message"
    echo ""
}

# If no argument is provided, show menu
if [ -z "$1" ]; then
    echo -e "\n${YELLOW}What would you like to do?${NC}"
    echo -e "1) ${GREEN}Run in development mode${NC} (bootRun)"
    echo -e "2) ${GREEN}Build and run packaged JAR${NC} (bootJar + java -jar)"
    echo -e "3) ${GREEN}Build only${NC} (bootJar)"
    echo -e "4) ${GREEN}Run tests${NC} (test)"
    echo -e "5) ${GREEN}Clean project${NC} (clean)"
    echo -e "6) ${RED}Exit${NC}"
    echo -n "Choose an option [1-6]: "
    read -r OPTION
    case $OPTION in
        1) ACTION="dev" ;;
        2) ACTION="prod" ;;
        3) ACTION="build" ;;
        4) ACTION="test" ;;
        5) ACTION="clean" ;;
        *) echo -e "${YELLOW}Exiting.${NC}"; exit 0 ;;
    esac
else
    ACTION="$1"
fi

case "$ACTION" in
    dev)
        echo -e "\n${BLUE}🚀 Starting application in development mode...${NC}"
        ./gradlew bootRun
        ;;
    build)
        echo -e "\n${BLUE}📦 Building production executable JAR...${NC}"
        ./gradlew bootJar
        JAR_FILE=$(find build/libs -name "*.jar" ! -name "*-plain.jar" 2>/dev/null | head -n 1 || true)
        if [ -n "$JAR_FILE" ] && [ -f "$JAR_FILE" ]; then
            echo -e "\n${GREEN}✔ Build successful! JAR file is located at: $JAR_FILE${NC}"
        else
            echo -e "\n${YELLOW}⚠ Build succeeded, but JAR file couldn't be auto-detected in build/libs/.${NC}"
        fi
        ;;
    prod)
        echo -e "\n${BLUE}📦 Building production executable JAR...${NC}"
        ./gradlew bootJar
        JAR_FILE=$(find build/libs -name "*.jar" ! -name "*-plain.jar" 2>/dev/null | head -n 1 || true)
        if [ -n "$JAR_FILE" ] && [ -f "$JAR_FILE" ]; then
            echo -e "\n${BLUE}🚀 Launching application from: $JAR_FILE${NC}"
            java -jar "$JAR_FILE"
        else
            echo -e "\n${RED}Error: JAR file not found in build/libs/${NC}"
            exit 1
        fi
        ;;
    test)
        echo -e "\n${BLUE}🧪 Running tests...${NC}"
        ./gradlew test
        ;;
    clean)
        echo -e "\n${BLUE}🧹 Cleaning project...${NC}"
        ./gradlew clean
        echo -e "\n${GREEN}✔ Clean completed.${NC}"
        ;;
    help|--help|-h)
        show_help
        ;;
    *)
        echo -e "${RED}Unknown command: $ACTION${NC}"
        show_help
        exit 1
        ;;
esac
