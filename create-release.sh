#!/bin/bash

# NotizDesktop Release Creation Script
# This script helps create and push version tags for automated releases

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_info() {
    echo -e "${BLUE}ℹ️  $1${NC}"
}

print_success() {
    echo -e "${GREEN}✅ $1${NC}"
}

print_warning() {
    echo -e "${YELLOW}⚠️  $1${NC}"
}

print_error() {
    echo -e "${RED}❌ $1${NC}"
}

# Function to validate version format
validate_version() {
    if [[ $1 =~ ^v[0-9]+\.[0-9]+\.[0-9]+(-[a-zA-Z0-9]+(\.[0-9]+)?)?$ ]]; then
        return 0
    else
        return 1
    fi
}

# Function to check if tag already exists
tag_exists() {
    git tag -l | grep -q "^$1$"
}

# Function to check git status
check_git_status() {
    if ! git diff-index --quiet HEAD --; then
        print_error "You have uncommitted changes. Please commit or stash them first."
        git status --short
        exit 1
    fi
    
    if [ "$(git rev-parse --abbrev-ref HEAD)" != "main" ] && [ "$(git rev-parse --abbrev-ref HEAD)" != "GUI-HTML-Ant" ]; then
        print_warning "You're not on the main or GUI-HTML-Ant branch. Current branch: $(git rev-parse --abbrev-ref HEAD)"
        read -p "Continue anyway? (y/N): " -n 1 -r
        echo
        if [[ ! $REPLY =~ ^[Yy]$ ]]; then
            exit 1
        fi
    fi
}

# Function to show help
show_help() {
    echo "NotizDesktop Release Creation Script"
    echo ""
    echo "Usage: $0 [VERSION]"
    echo ""
    echo "VERSION format: vX.Y.Z or vX.Y.Z-suffix"
    echo "Examples:"
    echo "  $0 v1.0.0        # Major release"
    echo "  $0 v1.1.0        # Minor release"
    echo "  $0 v1.0.1        # Patch release"
    echo "  $0 v1.0.0-beta.1 # Pre-release"
    echo ""
    echo "The script will:"
    echo "1. Validate the version format"
    echo "2. Check git status and branch"
    echo "3. Create and push the version tag"
    echo "4. Trigger the automated release workflow"
    echo ""
    echo "Options:"
    echo "  -h, --help       Show this help message"
    echo "  -n, --dry-run    Show what would be done without executing"
}

# Parse command line arguments
DRY_RUN=false
VERSION=""

while [[ $# -gt 0 ]]; do
    case $1 in
        -h|--help)
            show_help
            exit 0
            ;;
        -n|--dry-run)
            DRY_RUN=true
            shift
            ;;
        v*)
            VERSION="$1"
            shift
            ;;
        *)
            print_error "Unknown option: $1"
            show_help
            exit 1
            ;;
    esac
done

# If no version provided, prompt for it
if [ -z "$VERSION" ]; then
    echo "NotizDesktop Release Creation Script"
    echo "===================================="
    echo ""
    print_info "Current tags:"
    git tag -l | sort -V | tail -5 || echo "No existing tags"
    echo ""
    
    read -p "Enter version (e.g., v1.0.0): " VERSION
    
    if [ -z "$VERSION" ]; then
        print_error "Version cannot be empty"
        exit 1
    fi
fi

# Validate version format
if ! validate_version "$VERSION"; then
    print_error "Invalid version format: $VERSION"
    print_info "Expected format: vX.Y.Z or vX.Y.Z-suffix (e.g., v1.0.0, v1.0.0-beta.1)"
    exit 1
fi

print_success "Version format is valid: $VERSION"

# Check if tag already exists
if tag_exists "$VERSION"; then
    print_error "Tag $VERSION already exists"
    print_info "Existing tags:"
    git tag -l | grep -E "^v[0-9]" | sort -V | tail -10
    exit 1
fi

# Check git status
print_info "Checking git status..."
check_git_status
print_success "Git status is clean"

# Show what will be done
echo ""
print_info "Release Summary:"
echo "  Version: $VERSION"
echo "  Branch: $(git rev-parse --abbrev-ref HEAD)"
echo "  Commit: $(git rev-parse --short HEAD)"
echo "  Remote: $(git remote get-url origin 2>/dev/null || echo 'No remote configured')"
echo ""

if [ "$DRY_RUN" = true ]; then
    print_warning "DRY RUN - No changes will be made"
    echo ""
    print_info "Would execute:"
    echo "  git tag $VERSION"
    echo "  git push origin $VERSION"
    echo ""
    print_info "This would trigger the automated release workflow"
    exit 0
fi

# Confirm before proceeding
read -p "Create and push release tag $VERSION? (y/N): " -n 1 -r
echo
if [[ ! $REPLY =~ ^[Yy]$ ]]; then
    print_info "Release creation cancelled"
    exit 0
fi

# Create and push the tag
print_info "Creating tag $VERSION..."
git tag "$VERSION"
print_success "Tag created locally"

print_info "Pushing tag to remote..."
git push origin "$VERSION"
print_success "Tag pushed to remote"

echo ""
print_success "Release tag $VERSION created and pushed successfully!"
echo ""
print_info "Next steps:"
echo "1. Monitor the GitHub Actions workflow at:"
echo "   https://github.com/$(git remote get-url origin | sed 's/.*github.com[:/]\([^/]*\/[^/]*\)\.git.*/\1/' 2>/dev/null || echo 'YOUR_REPO')/actions"
echo ""
echo "2. The automated workflow will:"
echo "   - Build the project with Ant"
echo "   - Run tests"
echo "   - Generate JavaDoc"
echo "   - Create release packages"
echo "   - Publish the GitHub release"
echo ""
echo "3. Once complete, the release will be available at:"
echo "   https://github.com/$(git remote get-url origin | sed 's/.*github.com[:/]\([^/]*\/[^/]*\)\.git.*/\1/' 2>/dev/null || echo 'YOUR_REPO')/releases/tag/$VERSION"
echo ""
print_success "🚀 Release process initiated!"