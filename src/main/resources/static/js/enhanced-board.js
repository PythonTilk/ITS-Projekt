// Enhanced Board JavaScript with Dark Mode, Rich Text Editor, and File Upload
document.addEventListener('DOMContentLoaded', function() {
    console.log('Enhanced Board.js loaded');
    
    // Theme management
    const themeToggle = document.getElementById('theme-toggle');
    const sunIcon = themeToggle.querySelector('.sun-icon');
    const moonIcon = themeToggle.querySelector('.moon-icon');
    
    // Initialize theme
    const savedTheme = localStorage.getItem('theme') || 'light';
    document.documentElement.setAttribute('data-theme', savedTheme);
    updateThemeIcons(savedTheme);
    
    themeToggle.addEventListener('click', () => {
        const currentTheme = document.documentElement.getAttribute('data-theme');
        const newTheme = currentTheme === 'dark' ? 'light' : 'dark';
        
        document.documentElement.setAttribute('data-theme', newTheme);
        localStorage.setItem('theme', newTheme);
        updateThemeIcons(newTheme);
        
        // Add animation
        themeToggle.style.transform = 'scale(0.8)';
        setTimeout(() => {
            themeToggle.style.transform = 'scale(1)';
        }, 150);
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
    const sharedUsersInput = document.getElementById('shared-users');
    
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
            sharedWith: sharedUsersInput.value
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
        
        // Add event listeners
        addNoteEventListeners(noteElement);
        
        // Add animation
        noteElement.classList.add('fade-in');
        
        return noteElement;
    }
    
    function addNoteEventListeners(noteElement) {
        // Double click to edit
        noteElement.addEventListener('dblclick', () => {
            openEditNoteModal(noteElement);
        });
        
        // Edit button
        noteElement.querySelector('.edit-note').addEventListener('click', (e) => {
            e.stopPropagation();
            openEditNoteModal(noteElement);
        });
        
        // Delete button
        noteElement.querySelector('.delete-note').addEventListener('click', (e) => {
            e.stopPropagation();
            if (confirm('Are you sure you want to delete this note?')) {
                deleteNote(noteElement.dataset.id);
            }
        });
        
        // Drag functionality
        noteElement.addEventListener('mousedown', startDrag);
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