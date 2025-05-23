#!/bin/bash

# This script downloads the repository as a zip file using the GitHub API
# This method doesn't require authentication

# Colors for output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Configuration
REPO_OWNER="PythonTilk"
REPO_NAME="ITS-Projekt"
BRANCH="html"
DOWNLOAD_DIR="/tmp"
EXTRACT_DIR="/home/appuser"

# Print status messages
print_status() {
    echo -e "${GREEN}[+] $1${NC}"
}

print_warning() {
    echo -e "${YELLOW}[!] $1${NC}"
}

print_error() {
    echo -e "${RED}[-] $1${NC}"
}

# Check if running as root
if [ "$EUID" -ne 0 ]; then
    print_error "Please run as root"
    exit 1
fi

# Install required tools
print_status "Installing required tools..."
apt update
apt install -y curl unzip

# Create appuser if it doesn't exist
if ! id "appuser" &>/dev/null; then
    print_status "Creating application user..."
    useradd -m -s /bin/bash appuser
    echo "appuser:appuser123" | chpasswd
fi

# Download the repository
print_status "Downloading repository from GitHub..."
DOWNLOAD_URL="https://github.com/$REPO_OWNER/$REPO_NAME/archive/refs/heads/$BRANCH.zip"
DOWNLOAD_PATH="$DOWNLOAD_DIR/$REPO_NAME-$BRANCH.zip"

curl -L -o "$DOWNLOAD_PATH" "$DOWNLOAD_URL"

if [ $? -ne 0 ]; then
    print_error "Failed to download repository"
    exit 1
fi

print_status "Download completed: $DOWNLOAD_PATH"

# Extract the repository
print_status "Extracting repository..."
unzip -q -o "$DOWNLOAD_PATH" -d "$DOWNLOAD_DIR"

if [ $? -ne 0 ]; then
    print_error "Failed to extract repository"
    exit 1
fi

# Move to the final location
print_status "Moving repository to $EXTRACT_DIR..."
rm -rf "$EXTRACT_DIR/$REPO_NAME" 2>/dev/null
mv "$DOWNLOAD_DIR/$REPO_NAME-$BRANCH" "$EXTRACT_DIR/$REPO_NAME"

if [ $? -ne 0 ]; then
    print_error "Failed to move repository"
    exit 1
fi

# Set permissions
print_status "Setting permissions..."
chown -R appuser:appuser "$EXTRACT_DIR/$REPO_NAME"

print_status "Repository successfully downloaded to $EXTRACT_DIR/$REPO_NAME"
print_status "Now you can run the setup script:"
print_status "cd $EXTRACT_DIR/$REPO_NAME && chmod +x setup_server.sh && ./setup_server.sh"

# Clean up
rm "$DOWNLOAD_PATH"