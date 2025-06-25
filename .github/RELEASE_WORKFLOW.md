# 🚀 Automated Release Workflow Guide

This document explains how to use the automated build and release workflows for the NotizDesktop application.

## 📋 Overview

The project now includes two automated workflows:

1. **`release.yml`** - Automatically builds and publishes releases when version tags are pushed
2. **`build-test.yml`** - Manual workflow for testing builds and creating pre-releases

## 🏷️ Creating Releases

### Automatic Release (Recommended)

To create a new release automatically:

1. **Prepare your code**: Ensure all changes are committed and pushed to the main branch
2. **Create and push a version tag**:
   ```bash
   git tag v1.0.0
   git push origin v1.0.0
   ```
3. **Wait for the workflow**: The release workflow will automatically:
   - Build the project
   - Run tests
   - Generate documentation
   - Create release packages
   - Publish a GitHub release with all artifacts

### Manual Release

You can also trigger a release manually from the GitHub Actions tab:

1. Go to **Actions** → **Build and Release**
2. Click **Run workflow**
3. Enter the version (e.g., `v1.0.0`)
4. Click **Run workflow**

## 🧪 Testing Builds

### Manual Build Testing

To test a build without creating a release:

1. Go to **Actions** → **Build and Test**
2. Click **Run workflow**
3. Optionally check **Create a pre-release** to publish test artifacts
4. Enter a pre-release tag (e.g., `v1.0.0-beta.1`)
5. Click **Run workflow**

## 📦 Release Assets

Each release includes:

### Complete Packages
- **`NotizDesktop-vX.X.X.tar.gz`** - Complete package for Linux/macOS
- **`NotizDesktop-vX.X.X.zip`** - Complete package for Windows

### Individual Files
- **`NotizDesktop.jar`** - Main application JAR (requires dependencies)
- **`NotizDesktop-with-dependencies.jar`** - Standalone JAR with all dependencies

### Package Contents
Each complete package contains:
- Application JAR files
- All required dependencies in `lib/` directory
- Complete JavaDoc documentation
- README and installation guides
- Database schema SQL file
- Platform-specific run scripts

## 🔧 Version Naming Convention

Use semantic versioning for tags:
- **Major releases**: `v1.0.0`, `v2.0.0`
- **Minor releases**: `v1.1.0`, `v1.2.0`
- **Patch releases**: `v1.0.1`, `v1.0.2`
- **Pre-releases**: `v1.0.0-beta.1`, `v1.0.0-rc.1`

## 📝 Release Process Checklist

Before creating a release:

- [ ] All features are complete and tested
- [ ] Documentation is updated (README.md, etc.)
- [ ] Database schema is up to date
- [ ] Version numbers are updated in relevant files
- [ ] All tests pass locally
- [ ] Code is committed and pushed to main branch

## 🛠️ Workflow Configuration

### Environment Requirements
- **Java**: 17 (automatically set up)
- **Build Tool**: Apache Ant
- **Dependencies**: Downloaded automatically from Maven Central

### Customization

To modify the workflows:

1. **Change Java version**: Update the `java-version` in both workflow files
2. **Add dependencies**: Update the dependency download section
3. **Modify build steps**: Edit the Ant targets in the build steps
4. **Change package contents**: Modify the release package creation section

## 🔍 Troubleshooting

### Common Issues

**Build fails with missing dependencies:**
- Check that all required JAR files are being downloaded
- Verify Maven Central URLs are correct
- Ensure network connectivity during build

**Release creation fails:**
- Verify the repository has the correct permissions
- Check that the tag format matches the trigger pattern
- Ensure GITHUB_TOKEN has sufficient permissions

**JAR files are not executable:**
- Verify the main class is correctly specified in build.xml
- Check that all dependencies are included in the classpath
- Test the JAR files locally before releasing

### Debugging

To debug workflow issues:

1. **Check workflow logs**: Go to Actions tab and view the failed workflow
2. **Test locally**: Run the same Ant commands locally
3. **Verify dependencies**: Ensure all required libraries are available
4. **Check permissions**: Verify repository and token permissions

## 📊 Workflow Status

You can monitor workflow status:

- **Badge**: Add workflow status badges to your README
- **Notifications**: Configure GitHub notifications for workflow failures
- **Artifacts**: Download build artifacts from the Actions tab

## 🔗 Related Files

- **`.github/workflows/release.yml`** - Main release workflow
- **`.github/workflows/build-test.yml`** - Test build workflow
- **`.github/workflows/ant.yml`** - Existing CI workflow
- **`build.xml`** - Ant build configuration
- **`README.md`** - Main project documentation

## 💡 Tips

1. **Test first**: Use the build-test workflow before creating releases
2. **Semantic versioning**: Follow semantic versioning for predictable releases
3. **Release notes**: The workflow automatically generates comprehensive release notes
4. **Multiple formats**: Both tar.gz and zip formats are provided for cross-platform compatibility
5. **Documentation**: JavaDoc is automatically generated and included in releases

## 🆘 Support

If you encounter issues with the release workflows:

1. Check the workflow logs in the Actions tab
2. Verify your tag format matches the expected pattern
3. Ensure all required files are present in the repository
4. Test the build process locally using the same Ant commands

For additional help, create an issue in the repository with:
- Workflow run URL
- Error messages
- Steps to reproduce the issue