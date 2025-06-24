// Enhanced Board JavaScript with Dark Mode, Rich Text Editor, and File Upload
document.addEventListener('DOMContentLoaded', function() {
    console.log('Enhanced Board.js loaded');
    
    // Theme management
    const themeToggle = document.getElementById('theme-toggle');
    const sunIcon = themeToggle.querySelector('.sun-icon');
    const moonIcon = themeToggle.querySelector('.moon-icon');
    
    // Initialize theme - dark mode is default
    const savedTheme = localStorage.getItem('theme') || 'dark';
    document.documentElement.setAttribute('data-theme', savedTheme);
    updateThemeIcons(savedTheme);
    
    // Add sound effect for theme toggle
    const toggleSound = new Audio();
    toggleSound.src = '/sounds/toggle.mp3'; // This will be created later
    toggleSound.volume = 0.3;
    
    themeToggle.addEventListener('click', () => {
        const currentTheme = document.documentElement.getAttribute('data-theme');
        const newTheme = currentTheme === 'dark' ? 'light' : 'dark';
        
        // Play sound effect
        try {
            toggleSound.currentTime = 0;
            toggleSound.play().catch(e => console.log('Sound could not be played:', e));
        } catch (e) {
            console.log('Sound error:', e);
        }
        
        // Add switching animation
        themeToggle.classList.add('switching');
        
        setTimeout(() => {
            document.documentElement.setAttribute('data-theme', newTheme);
            localStorage.setItem('theme', newTheme);
            updateThemeIcons(newTheme);
        }, 300);
        
        setTimeout(() => {
            themeToggle.classList.remove('switching');
        }, 600);
    });
    
    function updateThemeIcons(theme) {
        if (theme === 'dark') {
            sunIcon.style.display = 'none';
            moonIcon.style.display = 'block';
        } else {
            sunIcon.style.display = 'block';
            moonIcon.style.display = 'none';
        }
    }
    
    // DOM Elements
    const noteBoard = document.getElementById('note-board');
    const addNoteBtn = document.getElementById('add-note-btn');
    const addNoteMenu = document.getElementById('add-note-menu');
    const noteModal = document.getElementById('note-modal');
    const closeBtn = document.querySelector('.close-btn');
    const noteForm = document.getElementById('note-form');
    const modalTitle = document.getElementById('modal-title');
    const logoutBtn = document.getElementById('logout-btn');
    const profileBtn = document.getElementById('profile-btn');
    
    // Form elements
    const noteIdInput = document.getElementById('note-id');
    const noteTypeInput = document.getElementById('note-type');
    const noteTitleInput = document.getElementById('note-title');
    const noteTagInput = document.getElementById('note-tag');
    const noteColorInput = document.getElementById('note-color');
    const deleteNoteBtn = document.getElementById('delete-note-btn');
    const cancelBtn = document.getElementById('cancel-btn');
    
    // Rich text editor elements
    const richEditorGroup = document.getElementById('rich-editor-group');
    const richEditorContent = document.getElementById('rich-editor-content');
    const richEditorBtns = document.querySelectorAll('.rich-editor-btn');
    
    // Code editor elements
    const codeEditorGroup = document.getElementById('code-editor-group');
    const codeEditorContent = document.getElementById('code-editor-content');
    const codeLanguage = document.getElementById('code-language');
    
    // File upload elements
    const fileUploadArea = document.getElementById('file-upload-area');
    const fileInput = document.getElementById('file-input');
    const uploadedFiles = document.getElementById('uploaded-files');
    
    // Privacy elements
    const privacyOptions = document.querySelectorAll('.privacy-option');
    const sharedUsersGroup = document.getElementById('shared-users-group');
    const userSearchInput = document.getElementById('user-search');
    const searchResults = document.getElementById('search-results');
    const selectedUsersList = document.getElementById('selected-users-list');
    
    // User selection state
    let selectedUsers = [];
    let searchTimeout;
    
    // State
    let isDragging = false;
    let currentNote = null;
    let offsetX = 0;
    let offsetY = 0;
    let isAddMenuOpen = false;
    let uploadedFilesList = [];
    
    // Initialize
    initializeExistingNotes();
    loadNotes();
    
    // Event Listeners
    
    // Add note button and menu
    addNoteBtn.addEventListener('click', (e) => {
        e.stopPropagation();
        toggleAddNoteMenu();
    });
    
    document.addEventListener('click', (e) => {
        if (!addNoteMenu.contains(e.target) && !addNoteBtn.contains(e.target)) {
            closeAddNoteMenu();
        }
    });
    
    // Add note options
    document.querySelectorAll('.add-note-option').forEach(option => {
        option.addEventListener('click', () => {
            const noteType = option.dataset.type;
            openAddNoteModal(noteType);
            closeAddNoteMenu();
        });
    });
    
    // Modal events
    closeBtn.addEventListener('click', closeModal);
    cancelBtn.addEventListener('click', closeModal);
    noteModal.addEventListener('click', (e) => {
        if (e.target === noteModal) closeModal();
    });
    
    // Form submission
    noteForm.addEventListener('submit', handleFormSubmit);
    
    // Rich text editor
    richEditorBtns.forEach(btn => {
        btn.addEventListener('click', (e) => {
            e.preventDefault();
            const command = btn.dataset.command;
            const value = btn.dataset.value;
            
            if (command === 'formatBlock') {
                document.execCommand(command, false, `<${value}>`);
            } else {
                document.execCommand(command, false, value);
            }
            
            richEditorContent.focus();
            updateToolbarState();
        });
    });
    
    richEditorContent.addEventListener('keyup', updateToolbarState);
    richEditorContent.addEventListener('mouseup', updateToolbarState);
    
    // File upload
    fileUploadArea.addEventListener('click', () => fileInput.click());
    fileUploadArea.addEventListener('dragover', handleDragOver);
    fileUploadArea.addEventListener('drop', handleFileDrop);
    fileInput.addEventListener('change', handleFileSelect);
    
    // Privacy settings
    privacyOptions.forEach(option => {
        option.addEventListener('click', () => {
            privacyOptions.forEach(opt => opt.classList.remove('selected'));
            option.classList.add('selected');
            option.querySelector('input').checked = true;
            
            const value = option.dataset.value;
            if (value === 'some_people') {
                sharedUsersGroup.style.display = 'block';
            } else {
                sharedUsersGroup.style.display = 'none';
            }
        });
    });
    
    // Profile
    profileBtn.addEventListener('click', async () => {
        try {
            const response = await fetch('/api/users/current');
            const data = await response.json();
            if (data.success) {
                window.location.href = `/profile/${data.user.id}`;
            } else {
                alert('Error loading profile');
            }
        } catch (error) {
            console.error('Error:', error);
            alert('Error loading profile');
        }
    });
    
    // Logout
    logoutBtn.addEventListener('click', () => {
        if (confirm('Are you sure you want to logout?')) {
            window.location.href = '/logout';
        }
    });
    
    // Functions
    
    function toggleAddNoteMenu() {
        isAddMenuOpen = !isAddMenuOpen;
        addNoteMenu.classList.toggle('show', isAddMenuOpen);
        addNoteBtn.style.transform = isAddMenuOpen ? 'rotate(45deg)' : 'rotate(0deg)';
    }
    
    function closeAddNoteMenu() {
        isAddMenuOpen = false;
        addNoteMenu.classList.remove('show');
        addNoteBtn.style.transform = 'rotate(0deg)';
    }
    
    function openAddNoteModal(noteType = 'text') {
        modalTitle.textContent = `Add New ${noteType === 'code' ? 'Code' : 'Text'} Note`;
        noteTypeInput.value = noteType;
        
        // Show/hide appropriate editor
        if (noteType === 'code') {
            richEditorGroup.style.display = 'none';
            codeEditorGroup.style.display = 'block';
        } else {
            richEditorGroup.style.display = 'block';
            codeEditorGroup.style.display = 'none';
        }
        
        // Reset form
        noteForm.reset();
        noteIdInput.value = '';
        richEditorContent.innerHTML = '';
        codeEditorContent.value = '';
        uploadedFilesList = [];
        updateUploadedFilesDisplay();
        deleteNoteBtn.style.display = 'none';
        
        // Reset privacy
        privacyOptions.forEach(opt => opt.classList.remove('selected'));
        privacyOptions[0].classList.add('selected');
        privacyOptions[0].querySelector('input').checked = true;
        sharedUsersGroup.style.display = 'none';
        
        // Reset selected users
        selectedUsers = [];
        updateSelectedUsersDisplay();
        if (userSearchInput) userSearchInput.value = '';
        hideSearchResults();
        
        showModal();
    }
    
    function openEditNoteModal(note) {
        const noteType = note.dataset.type || 'text';
        modalTitle.textContent = `Edit ${noteType === 'code' ? 'Code' : 'Text'} Note`;
        noteTypeInput.value = noteType;
        
        // Show/hide appropriate editor
        if (noteType === 'code') {
            richEditorGroup.style.display = 'none';
            codeEditorGroup.style.display = 'block';
            codeEditorContent.value = note.querySelector('.note-content').textContent;
        } else {
            richEditorGroup.style.display = 'block';
            codeEditorGroup.style.display = 'none';
            richEditorContent.innerHTML = note.querySelector('.note-content').innerHTML;
        }
        
        // Fill form
        noteIdInput.value = note.dataset.id;
        noteTitleInput.value = note.querySelector('.note-title').textContent;
        noteTagInput.value = note.querySelector('.note-tag')?.textContent || '';
        
        // Set color
        const noteColor = note.style.backgroundColor;
        const colorOptions = noteColorInput.options;
        for (let i = 0; i < colorOptions.length; i++) {
            if (rgbToHex(noteColor) === colorOptions[i].value) {
                noteColorInput.selectedIndex = i;
                break;
            }
        }
        
        deleteNoteBtn.style.display = 'block';
        showModal();
    }
    
    function openViewNoteModal(note) {
        const viewModal = document.getElementById('view-note-modal');
        const viewModalTitle = document.getElementById('view-modal-title');
        const viewNoteTitle = document.getElementById('view-note-title');
        const viewNoteTag = document.getElementById('view-note-tag');
        const viewNoteContent = document.getElementById('view-note-content');
        const viewNoteImagesGroup = document.getElementById('view-note-images-group');
        const viewNoteImages = document.getElementById('view-note-images');
        const viewNotePrivacy = document.getElementById('view-note-privacy');
        const viewNoteAuthor = document.getElementById('view-note-author');
        
        const noteType = note.dataset.type || 'text';
        viewModalTitle.textContent = `View ${noteType === 'code' ? 'Code' : 'Text'} Note`;
        
        // Fill view fields
        viewNoteTitle.textContent = note.querySelector('.note-title').textContent;
        viewNoteTag.textContent = note.querySelector('.note-tag')?.textContent || 'No tag';
        
        // Handle content based on type
        if (noteType === 'code') {
            viewNoteContent.innerHTML = `<pre><code>${note.querySelector('.note-content').textContent}</code></pre>`;
        } else {
            viewNoteContent.innerHTML = note.querySelector('.note-content').innerHTML;
        }
        
        // Privacy level
        viewNotePrivacy.textContent = note.querySelector('.note-privacy-indicator')?.textContent || 'private';
        
        // Author information
        const authorElement = note.querySelector('.note-author');
        if (authorElement && authorElement.style.display !== 'none') {
            viewNoteAuthor.textContent = authorElement.textContent;
        } else {
            viewNoteAuthor.textContent = 'You';
        }
        
        // Handle images (if any)
        const noteImages = note.querySelector('.note-images');
        if (noteImages && noteImages.children.length > 0) {
            viewNoteImagesGroup.style.display = 'block';
            viewNoteImages.innerHTML = noteImages.innerHTML;
        } else {
            viewNoteImagesGroup.style.display = 'none';
        }
        
        // Show view modal
        viewModal.style.display = 'flex';
        viewModal.classList.add('fade-in');
        
        // Add close event listeners
        const closeBtn = viewModal.querySelector('.close-btn');
        const viewCloseBtn = document.getElementById('view-close-btn');
        
        const closeViewModal = () => {
            viewModal.style.display = 'none';
            viewModal.classList.remove('fade-in');
        };
        
        closeBtn.onclick = closeViewModal;
        viewCloseBtn.onclick = closeViewModal;
        
        // Close on outside click
        viewModal.onclick = (e) => {
            if (e.target === viewModal) {
                closeViewModal();
            }
        };
    }
    
    function showModal() {
        noteModal.style.display = 'flex';
        setTimeout(() => {
            noteModal.classList.add('show');
        }, 10);
    }
    
    function closeModal() {
        noteModal.classList.remove('show');
        setTimeout(() => {
            noteModal.style.display = 'none';
        }, 300);
    }
    
    function updateToolbarState() {
        richEditorBtns.forEach(btn => {
            const command = btn.dataset.command;
            if (document.queryCommandState(command)) {
                btn.classList.add('active');
            } else {
                btn.classList.remove('active');
            }
        });
    }
    
    function handleDragOver(e) {
        e.preventDefault();
        fileUploadArea.classList.add('dragover');
    }
    
    function handleFileDrop(e) {
        e.preventDefault();
        fileUploadArea.classList.remove('dragover');
        const files = Array.from(e.dataTransfer.files);
        handleFiles(files);
    }
    
    function handleFileSelect(e) {
        const files = Array.from(e.target.files);
        handleFiles(files);
    }
    
    function handleFiles(files) {
        files.forEach(file => {
            if (file.type.startsWith('image/') && file.size <= 5 * 1024 * 1024) {
                const reader = new FileReader();
                reader.onload = (e) => {
                    uploadedFilesList.push({
                        name: file.name,
                        data: e.target.result,
                        type: file.type
                    });
                    updateUploadedFilesDisplay();
                };
                reader.readAsDataURL(file);
            } else {
                alert('Please select image files under 5MB');
            }
        });
    }
    
    function updateUploadedFilesDisplay() {
        uploadedFiles.innerHTML = '';
        uploadedFilesList.forEach((file, index) => {
            const fileDiv = document.createElement('div');
            fileDiv.className = 'uploaded-file';
            fileDiv.innerHTML = `
                <img src="${file.data}" alt="${file.name}">
                <button class="remove-file" onclick="removeFile(${index})">&times;</button>
            `;
            uploadedFiles.appendChild(fileDiv);
        });
    }
    
    window.removeFile = function(index) {
        uploadedFilesList.splice(index, 1);
        updateUploadedFilesDisplay();
    };
    
    function handleFormSubmit(e) {
        e.preventDefault();
        
        const noteData = {
            id: noteIdInput.value || null,
            title: noteTitleInput.value,
            tag: noteTagInput.value,
            color: noteColorInput.value,
            noteType: noteTypeInput.value,
            privacyLevel: document.querySelector('input[name="privacy"]:checked').value,
            sharedWith: selectedUsers.map(user => user.username).join(',')
        };
        
        // Get content based on note type
        if (noteTypeInput.value === 'code') {
            noteData.content = codeEditorContent.value;
            noteData.language = codeLanguage.value;
        } else {
            noteData.content = richEditorContent.innerHTML;
        }
        
        // Add images
        if (uploadedFilesList.length > 0) {
            noteData.hasImages = true;
            noteData.images = uploadedFilesList;
        }
        
        saveNote(noteData);
    }
    
    function saveNote(noteData) {
        const url = noteData.id ? `/api/notes/${noteData.id}` : '/api/notes';
        const method = noteData.id ? 'PUT' : 'POST';
        
        fetch(url, {
            method: method,
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(noteData)
        })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                closeModal();
                loadNotes();
                showNotification('Note saved successfully!', 'success');
            } else {
                showNotification('Error saving note: ' + data.message, 'error');
            }
        })
        .catch(error => {
            console.error('Error:', error);
            showNotification('Error saving note', 'error');
        });
    }
    
    function loadNotes() {
        fetch('/api/notes')
            .then(response => response.json())
            .then(notes => {
                renderNotes(notes);
            })
            .catch(error => {
                console.error('Error loading notes:', error);
            });
    }
    
    function renderNotes(notes) {
        // Clear existing notes (except server-rendered ones on first load)
        const existingNotes = noteBoard.querySelectorAll('.note[data-js-rendered="true"]');
        existingNotes.forEach(note => note.remove());
        
        notes.forEach(note => {
            const noteElement = createNoteElement(note);
            noteBoard.appendChild(noteElement);
        });
    }
    
    function createNoteElement(note) {
        const template = document.getElementById('note-template');
        const noteElement = template.content.cloneNode(true).querySelector('.note');
        
        noteElement.dataset.id = note.id;
        noteElement.dataset.type = note.noteType || 'text';
        noteElement.dataset.authorId = note.authorId;
        noteElement.dataset.jsRendered = 'true';
        noteElement.className = `note ${note.noteType || 'text'}-note`;
        
        // Position and color
        noteElement.style.left = (note.positionX || 0) + 'px';
        noteElement.style.top = (note.positionY || 0) + 'px';
        noteElement.style.backgroundColor = note.color || '#fef3c7';
        
        // Content
        noteElement.querySelector('.note-title').textContent = note.title;
        noteElement.querySelector('.note-tag').textContent = note.tag || '';
        noteElement.querySelector('.note-content').innerHTML = note.content;
        
        // Type indicator
        const typeIndicator = noteElement.querySelector('.note-type-indicator');
        typeIndicator.className = `note-type-indicator ${note.noteType || 'text'}`;
        typeIndicator.textContent = note.noteType === 'code' ? 'C' : 'T';
        
        // Privacy indicator
        noteElement.querySelector('.note-privacy-indicator').textContent = note.privacyLevel || 'private';
        
        // Author information (show only for shared notes)
        const authorElement = noteElement.querySelector('.note-author');
        if (note.authorId && note.privacyLevel && note.privacyLevel !== 'private') {
            authorElement.textContent = `by ${note.authorDisplayName || note.authorUsername}`;
            authorElement.style.display = 'inline';
            authorElement.style.cursor = 'pointer';
            authorElement.style.color = 'var(--primary-color)';
            authorElement.style.textDecoration = 'underline';
            authorElement.addEventListener('click', (e) => {
                e.stopPropagation();
                window.location.href = `/profile/${note.authorId}`;
            });
        } else {
            authorElement.style.display = 'none';
        }
        

        
        // Add event listeners
        addNoteEventListeners(noteElement);
        
        // Add animation
        noteElement.classList.add('fade-in');
        
        return noteElement;
    }
    
    function addNoteEventListeners(noteElement) {
        const noteId = noteElement.dataset.id;
        const editButton = noteElement.querySelector('.edit-note');
        const viewButton = noteElement.querySelector('.view-note');
        const deleteButton = noteElement.querySelector('.delete-note');
        
        // Check if current user is the owner
        const noteAuthorId = parseInt(noteElement.dataset.authorId);
        const isOwner = noteAuthorId === window.currentUserId;
        
        // For JavaScript-rendered notes, set button visibility
        if (noteElement.dataset.jsRendered === 'true') {
            if (isOwner) {
                // Show edit and delete buttons for owner
                if (editButton) editButton.style.display = 'inline-flex';
                if (viewButton) viewButton.style.display = 'none';
                if (deleteButton) deleteButton.style.display = 'inline-flex';
            } else {
                // Show only view button for non-owners
                if (editButton) editButton.style.display = 'none';
                if (viewButton) viewButton.style.display = 'inline-flex';
                if (deleteButton) deleteButton.style.display = 'none';
            }
        }
        
        // Double click behavior - edit for owners, view for non-owners
        noteElement.addEventListener('dblclick', () => {
            if (isOwner) {
                openEditNoteModal(noteElement);
            } else {
                openViewNoteModal(noteElement);
            }
        });
        
        // Edit button (only for owners)
        if (editButton) {
            editButton.addEventListener('click', (e) => {
                e.stopPropagation();
                openEditNoteModal(noteElement);
            });
        }
        
        // View button (only for non-owners)
        if (viewButton) {
            viewButton.addEventListener('click', (e) => {
                e.stopPropagation();
                openViewNoteModal(noteElement);
            });
        }
        
        // Delete button (only for owners)
        if (deleteButton) {
            deleteButton.addEventListener('click', (e) => {
                e.stopPropagation();
                if (confirm('Are you sure you want to delete this note?')) {
                    deleteNote(noteElement.dataset.id);
                }
            });
        }
        
        // Drag functionality (only for owners)
        if (isOwner) {
            noteElement.addEventListener('mousedown', startDrag);
        }
    }
    
    function startDrag(e) {
        if (e.target.closest('.note-action')) return;
        
        isDragging = true;
        currentNote = e.currentTarget;
        currentNote.classList.add('dragging');
        
        const rect = currentNote.getBoundingClientRect();
        const boardRect = noteBoard.getBoundingClientRect();
        
        offsetX = e.clientX - rect.left;
        offsetY = e.clientY - rect.top;
        
        document.addEventListener('mousemove', drag);
        document.addEventListener('mouseup', stopDrag);
        
        e.preventDefault();
    }
    
    function drag(e) {
        if (!isDragging || !currentNote) return;
        
        const boardRect = noteBoard.getBoundingClientRect();
        const x = e.clientX - boardRect.left - offsetX;
        const y = e.clientY - boardRect.top - offsetY;
        
        currentNote.style.left = Math.max(0, x) + 'px';
        currentNote.style.top = Math.max(0, y) + 'px';
    }
    
    function stopDrag() {
        if (!isDragging || !currentNote) return;
        
        isDragging = false;
        currentNote.classList.remove('dragging');
        
        // Save position
        const noteId = currentNote.dataset.id;
        const x = parseInt(currentNote.style.left);
        const y = parseInt(currentNote.style.top);
        
        updateNotePosition(noteId, x, y);
        
        currentNote = null;
        document.removeEventListener('mousemove', drag);
        document.removeEventListener('mouseup', stopDrag);
    }
    
    function updateNotePosition(noteId, x, y) {
        fetch(`/api/notes/${noteId}/position`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ positionX: x, positionY: y })
        })
        .catch(error => {
            console.error('Error updating position:', error);
        });
    }
    
    function deleteNote(noteId) {
        fetch(`/api/notes/${noteId}`, {
            method: 'DELETE'
        })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                const noteElement = document.querySelector(`[data-id="${noteId}"]`);
                if (noteElement) {
                    noteElement.style.animation = 'fadeOut 0.3s ease-out';
                    setTimeout(() => {
                        noteElement.remove();
                    }, 300);
                }
                showNotification('Note deleted successfully!', 'success');
            } else {
                showNotification('Error deleting note', 'error');
            }
        })
        .catch(error => {
            console.error('Error:', error);
            showNotification('Error deleting note', 'error');
        });
    }
    
    function initializeExistingNotes() {
        const existingNotes = noteBoard.querySelectorAll('.note:not([data-js-rendered])');
        existingNotes.forEach(note => {
            addNoteEventListeners(note);
        });
    }
    
    function showNotification(message, type = 'info') {
        const notification = document.createElement('div');
        notification.className = `notification ${type}`;
        notification.textContent = message;
        notification.style.cssText = `
            position: fixed;
            top: 20px;
            right: 20px;
            padding: 12px 24px;
            border-radius: 8px;
            color: white;
            font-weight: 500;
            z-index: 10000;
            animation: slideIn 0.3s ease-out;
            background: ${type === 'success' ? 'var(--success-color)' : 
                       type === 'error' ? 'var(--danger-color)' : 'var(--primary-color)'};
        `;
        
        document.body.appendChild(notification);
        
        setTimeout(() => {
            notification.style.animation = 'fadeOut 0.3s ease-out';
            setTimeout(() => {
                notification.remove();
            }, 300);
        }, 3000);
    }
    
    function rgbToHex(rgb) {
        if (!rgb) return '#fef3c7';
        const result = rgb.match(/\d+/g);
        if (!result) return '#fef3c7';
        return '#' + result.map(x => parseInt(x).toString(16).padStart(2, '0')).join('');
    }
    
    // User search and selection functionality
    if (userSearchInput) {
        userSearchInput.addEventListener('input', function() {
            clearTimeout(searchTimeout);
            const query = this.value.trim();
            
            if (query.length === 0) {
                hideSearchResults();
                return;
            }
            
            searchTimeout = setTimeout(() => {
                searchUsers(query);
            }, 300);
        });
        
        userSearchInput.addEventListener('focus', function() {
            if (this.value.trim().length > 0) {
                searchUsers(this.value.trim());
            }
        });
        
        // Hide search results when clicking outside
        document.addEventListener('click', function(e) {
            if (!e.target.closest('.user-search')) {
                hideSearchResults();
            }
        });
    }
    
    function searchUsers(query) {
        searchResults.innerHTML = '<div class="search-results loading">Searching users...</div>';
        searchResults.classList.add('loading');
        searchResults.style.display = 'block';
        
        fetch(`/api/users/search?q=${encodeURIComponent(query)}`)
            .then(response => response.json())
            .then(data => {
                searchResults.classList.remove('loading');
                
                if (data.success && data.users.length > 0) {
                    displaySearchResults(data.users);
                } else {
                    searchResults.innerHTML = '<div class="search-results no-results">No users found</div>';
                    searchResults.classList.add('no-results');
                }
            })
            .catch(error => {
                console.error('Error searching users:', error);
                searchResults.classList.remove('loading');
                searchResults.innerHTML = '<div class="search-results no-results">Error searching users</div>';
                searchResults.classList.add('no-results');
            });
    }
    
    function displaySearchResults(users) {
        searchResults.classList.remove('no-results');
        searchResults.innerHTML = '';
        
        users.forEach(user => {
            // Skip if user is already selected
            if (selectedUsers.some(selected => selected.id === user.id)) {
                return;
            }
            
            const resultItem = document.createElement('div');
            resultItem.className = 'search-result-item';
            resultItem.innerHTML = `
                <div class="user-avatar-small">${user.avatar}</div>
                <div class="user-info">
                    <div class="user-name">${user.username}</div>
                    ${user.email ? `<div class="user-email">${user.email}</div>` : ''}
                </div>
            `;
            
            resultItem.addEventListener('click', () => {
                selectUser(user);
                hideSearchResults();
                userSearchInput.value = '';
            });
            
            searchResults.appendChild(resultItem);
        });
        
        searchResults.style.display = 'block';
    }
    
    function selectUser(user) {
        if (!selectedUsers.some(selected => selected.id === user.id)) {
            selectedUsers.push(user);
            updateSelectedUsersDisplay();
        }
    }
    
    function removeUser(userId) {
        selectedUsers = selectedUsers.filter(user => user.id !== userId);
        updateSelectedUsersDisplay();
    }
    
    function updateSelectedUsersDisplay() {
        if (selectedUsers.length === 0) {
            selectedUsersList.innerHTML = '<div class="no-users-selected">No users selected</div>';
        } else {
            selectedUsersList.innerHTML = selectedUsers.map(user => `
                <div class="selected-user-tag">
                    <span>${user.username}</span>
                    <span class="remove-user" onclick="removeUser(${user.id})">×</span>
                </div>
            `).join('');
        }
    }
    
    function hideSearchResults() {
        searchResults.style.display = 'none';
        searchResults.classList.remove('loading', 'no-results');
    }
    
    // Make removeUser function global so it can be called from HTML
    window.removeUser = removeUser;
    
    // Add CSS animations
    const style = document.createElement('style');
    style.textContent = `
        @keyframes fadeOut {
            from { opacity: 1; transform: scale(1); }
            to { opacity: 0; transform: scale(0.8); }
        }
    `;
    document.head.appendChild(style);
});