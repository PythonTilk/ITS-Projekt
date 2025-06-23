# ITS-Projekt Enhanced Note-Taking App

## 🎉 Successfully Enhanced Features

### ✅ 1. Dark Mode Toggle
- **Location**: Top-right corner of the application
- **Features**: 
  - Smooth theme transition with animations
  - Persistent theme preference (localStorage)
  - Modern sun/moon icons
  - CSS custom properties for consistent theming
  - Optimized color palette for both light and dark modes

### ✅ 2. Rich Text Editor
- **For Text Notes**: Full-featured rich text editing
- **Features**:
  - Bold, Italic, Underline, Strikethrough formatting
  - Heading levels (H1, H2, H3)
  - Text highlighting and color changes
  - Real-time toolbar state updates
  - HTML content preservation

### ✅ 3. Code Editor
- **For Code Notes**: Specialized code editing experience
- **Features**:
  - Syntax highlighting support for 10+ languages
  - Monospace font family
  - Language selection dropdown
  - Code-specific styling and indicators
  - Preserved formatting for code blocks

### ✅ 4. Image Upload System
- **Multiple Upload Methods**:
  - Click to browse files
  - Drag and drop interface
  - Base64 encoding for inline storage
- **Features**:
  - 5MB per file limit
  - PNG, JPG, GIF support
  - Image preview thumbnails
  - Remove uploaded images
  - User-specific upload directories

### ✅ 5. Privacy Settings
- **Three Privacy Levels**:
  - **Private**: Only you can see the note
  - **Some People**: Share with specific users
  - **Everyone**: Public note visible to all
- **Features**:
  - Visual privacy indicators on notes
  - User sharing input field
  - Database storage of privacy preferences

### ✅ 6. Enhanced UI/UX
- **Modern Design System**:
  - Inter font family for better readability
  - Gradient backgrounds and buttons
  - Smooth animations and transitions
  - Hover effects and micro-interactions
  - Responsive design for mobile devices

### ✅ 7. Improved Note Management
- **Enhanced Note Cards**:
  - Type indicators (Text/Code)
  - Privacy level badges
  - Action buttons (Edit/Delete)
  - Improved drag and drop
  - Better visual hierarchy

### ✅ 8. Database Enhancements
- **New Database Fields**:
  - `note_type` (text/code)
  - `privacy_level` (private/some_people/everyone)
  - `shared_with` (comma-separated usernames)
  - `has_images` (boolean flag)
  - `image_paths` (file paths storage)
  - `Inhalt` upgraded to MEDIUMTEXT for larger content

## 🛠️ Technical Implementation

### Backend Enhancements
- **Enhanced Note Model**: Added enums for note types and privacy levels
- **Enhanced API Endpoints**: JSON-based API for better frontend integration
- **File Upload Service**: Secure file handling with user isolation
- **Enhanced Database Queries**: Support for new fields and relationships

### Frontend Enhancements
- **Modern CSS Architecture**: CSS custom properties, animations, responsive design
- **Enhanced JavaScript**: Modular code with proper event handling
- **Rich Text Integration**: Document.execCommand API for text formatting
- **File Upload Integration**: FileReader API for image handling
- **Theme Management**: localStorage persistence and smooth transitions

### Security Features
- **User Isolation**: Files stored in user-specific directories
- **File Type Validation**: Only image files allowed
- **Size Limits**: 5MB per file, 25MB per request
- **Privacy Controls**: Database-level privacy enforcement

## 🚀 How to Use

### 1. Dark Mode
- Click the sun/moon icon in the top-right corner
- Theme preference is automatically saved

### 2. Creating Notes
- Click the "+" floating button
- Choose "Text Note" for rich text editing
- Choose "Code Note" for code editing

### 3. Rich Text Editing
- Use the toolbar buttons for formatting
- Select text and apply formatting
- Use headings for better organization

### 4. Code Notes
- Select programming language from dropdown
- Write code with proper formatting
- Code is preserved with monospace styling

### 5. Image Upload
- Click the upload area or drag images
- Preview uploaded images
- Remove images with the × button

### 6. Privacy Settings
- Choose privacy level when creating/editing notes
- For "Some People", enter usernames separated by commas
- Privacy indicators show on note cards

## 🎨 Design Features

### Color Palette
- **Light Mode**: Warm, professional colors with good contrast
- **Dark Mode**: Easy on the eyes with proper contrast ratios
- **Accent Colors**: Blue and purple gradients for modern appeal

### Animations
- **Smooth Transitions**: 0.3s ease transitions throughout
- **Hover Effects**: Subtle scale and shadow changes
- **Loading Animations**: Fade-in effects for new content
- **Interactive Feedback**: Button press animations

### Typography
- **Primary Font**: Inter (Google Fonts)
- **Code Font**: Monaco, Menlo, Ubuntu Mono
- **Font Weights**: 300-700 range for proper hierarchy

## 📱 Responsive Design
- **Mobile Optimized**: Touch-friendly interface
- **Tablet Support**: Optimized layouts for medium screens
- **Desktop Enhanced**: Full feature set with hover states

## 🔧 Configuration

### Application Properties
```properties
# File Upload Configuration
spring.servlet.multipart.max-file-size=5MB
spring.servlet.multipart.max-request-size=25MB
spring.servlet.multipart.enabled=true

# Static Resources
spring.web.resources.static-locations=classpath:/static/,file:uploads/
```

### Database Schema
```sql
-- Enhanced notiz table with new columns
ALTER TABLE notiz ADD COLUMN note_type ENUM('text', 'code') DEFAULT 'text';
ALTER TABLE notiz ADD COLUMN privacy_level ENUM('private', 'some_people', 'everyone') DEFAULT 'private';
ALTER TABLE notiz ADD COLUMN shared_with TEXT;
ALTER TABLE notiz ADD COLUMN has_images BOOLEAN DEFAULT FALSE;
ALTER TABLE notiz ADD COLUMN image_paths TEXT;
ALTER TABLE notiz MODIFY COLUMN Inhalt MEDIUMTEXT;
```

## 🎯 Future Enhancements
- Real-time collaboration
- Note templates
- Export functionality
- Advanced search filters
- Note categories and folders
- Markdown support
- Voice notes
- Note sharing via links

## 🏃‍♂️ Running the Application

1. **Start MariaDB**: `sudo systemctl start mariadb`
2. **Build Project**: `mvn clean compile`
3. **Run Application**: `mvn spring-boot:run`
4. **Access**: http://localhost:12000
5. **Login**: Use existing credentials or register new user

## 📊 Current Status
- ✅ All requested features implemented
- ✅ Database schema updated
- ✅ Frontend completely redesigned
- ✅ Backend API enhanced
- ✅ File upload system working
- ✅ Privacy controls functional
- ✅ Dark mode fully implemented
- ✅ Rich text editor operational
- ✅ Code editor functional
- ✅ Responsive design complete

The ITS-Projekt note-taking app has been successfully transformed into a modern, feature-rich application with all requested enhancements implemented and working!