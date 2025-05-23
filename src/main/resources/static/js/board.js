document.addEventListener('DOMContentLoaded', function() {
    console.log('Board.js loaded');
    
    // DOM Elements
    const noteBoard = document.getElementById('note-board');
    console.log('Note board element:', noteBoard);
    
    const addNoteBtn = document.getElementById('add-note-btn');
    console.log('Add note button:', addNoteBtn);
    
    const noteModal = document.getElementById('note-modal');
    const closeBtn = document.querySelector('.close-btn');
    const noteForm = document.getElementById('note-form');
    const modalTitle = document.getElementById('modal-title');
    const noteIdInput = document.getElementById('note-id');
    const noteTitleInput = document.getElementById('note-title');
    const noteTagInput = document.getElementById('note-tag');
    const noteContentInput = document.getElementById('note-content');
    const noteColorInput = document.getElementById('note-color');
    const deleteNoteBtn = document.getElementById('delete-note-btn');
    const logoutBtn = document.getElementById('logout-btn');
    console.log('Logout button:', logoutBtn);
    
    // Note template
    const noteTemplate = document.getElementById('note-template');
    console.log('Note template:', noteTemplate);
    
    // Current dragging state
    let isDragging = false;
    let currentNote = null;
    let offsetX = 0;
    let offsetY = 0;
    
    // Debug DOM structure
    console.log('Document body:', document.body.innerHTML);
    
    // Initialize existing notes from the server-rendered HTML
    initializeExistingNotes();
    
    // Also try to load notes from API as a fallback
    loadNotes();
    
    // Event Listeners
    if (addNoteBtn) {
        addNoteBtn.addEventListener('click', openAddNoteModal);
    } else {
        console.error('Add note button not found');
    }
    
    if (closeBtn) {
        closeBtn.addEventListener('click', closeModal);
    } else {
        console.error('Close button not found');
    }
    
    if (noteForm) {
        noteForm.addEventListener('submit', saveNote);
    } else {
        console.error('Note form not found');
    }
    
    if (deleteNoteBtn) {
        deleteNoteBtn.addEventListener('click', deleteNote);
    } else {
        console.error('Delete note button not found');
    }
    
    if (logoutBtn) {
        logoutBtn.addEventListener('click', logout);
    } else {
        console.error('Logout button not found');
    }
    
    // Close modal when clicking outside
    window.addEventListener('click', function(e) {
        if (e.target === noteModal) {
            closeModal();
        }
    });
    
    // Functions
    function initializeExistingNotes() {
        console.log('Initializing existing notes');
        // Get all notes that were rendered by the server
        const existingNotes = document.querySelectorAll('.note');
        console.log(`Found ${existingNotes.length} existing notes`);
        
        if (existingNotes.length === 0) {
            console.log('No existing notes found in the DOM');
            return;
        }
        
        // Add event listeners to each note
        existingNotes.forEach((note, index) => {
            console.log(`Note ${index}:`, note);
            console.log(`Note ${index} data-id:`, note.getAttribute('data-id'));
            console.log(`Note ${index} position:`, note.style.left, note.style.top);
            
            note.addEventListener('mousedown', startDragging);
            note.addEventListener('dblclick', function() {
                const noteId = note.getAttribute('data-id');
                const title = note.querySelector('.note-title').textContent;
                const tag = note.querySelector('.note-tag').textContent;
                const content = note.querySelector('.note-content').textContent;
                const color = note.style.backgroundColor;
                
                openEditNoteModal({
                    id: noteId,
                    title: title,
                    tag: tag,
                    content: content,
                    color: color
                });
            });
        });
    }
    
    function loadNotes() {
        console.log('Loading notes from API');
        fetch('/api/notes')
            .then(response => {
                if (!response.ok) {
                    if (response.status === 401) {
                        window.location.href = '/login';
                        return;
                    }
                    throw new Error('Failed to load notes');
                }
                return response.json();
            })
            .then(notes => {
                console.log(`Loaded ${notes.length} notes from API`);
                renderNotes(notes);
            })
            .catch(error => {
                console.error('Error loading notes:', error);
            });
    }
    
    function renderNotes(notes) {
        // Clear the board
        noteBoard.innerHTML = '';
        
        notes.forEach(note => {
            const noteElement = createNoteElement(note);
            noteBoard.appendChild(noteElement);
        });
    }
    
    function createNoteElement(note) {
        // Clone the template
        const noteElement = document.importNode(noteTemplate.content, true).querySelector('.note');
        
        // Set note data
        noteElement.dataset.id = note.id;
        noteElement.style.backgroundColor = note.color || '#FFFF88';
        noteElement.style.left = `${note.positionX || 0}px`;
        noteElement.style.top = `${note.positionY || 0}px`;
        
        // Set note content
        noteElement.querySelector('.note-title').textContent = note.title;
        noteElement.querySelector('.note-tag').textContent = note.tag || '';
        noteElement.querySelector('.note-content').textContent = note.content || '';
        
        // Add event listeners
        noteElement.addEventListener('mousedown', startDragging);
        noteElement.addEventListener('dblclick', function() {
            openEditNoteModal(note);
        });
        
        return noteElement;
    }
    
    function startDragging(e) {
        console.log('Start dragging');
        // Only handle left mouse button
        if (e.button !== 0) return;
        
        isDragging = true;
        currentNote = e.currentTarget;
        
        // Calculate the offset
        const rect = currentNote.getBoundingClientRect();
        offsetX = e.clientX - rect.left;
        offsetY = e.clientY - rect.top;
        
        // Add move and up event listeners
        document.addEventListener('mousemove', dragNote);
        document.addEventListener('mouseup', stopDragging);
        
        // Prevent text selection during drag
        e.preventDefault();
    }
    
    function dragNote(e) {
        if (!isDragging) return;
        
        // Calculate new position
        const x = e.clientX - offsetX;
        const y = e.clientY - offsetY;
        
        // Update note position
        currentNote.style.left = `${x}px`;
        currentNote.style.top = `${y}px`;
    }
    
    function stopDragging() {
        if (!isDragging) return;
        
        console.log('Stop dragging');
        
        // Save the new position to the server
        const noteId = currentNote.dataset.id;
        const positionX = parseInt(currentNote.style.left);
        const positionY = parseInt(currentNote.style.top);
        
        console.log(`Updating note ${noteId} position to (${positionX}, ${positionY})`);
        updateNotePosition(noteId, positionX, positionY);
        
        // Reset dragging state
        isDragging = false;
        currentNote = null;
        
        // Remove event listeners
        document.removeEventListener('mousemove', dragNote);
        document.removeEventListener('mouseup', stopDragging);
    }
    
    function updateNotePosition(noteId, positionX, positionY) {
        const formData = new FormData();
        formData.append('positionX', positionX);
        formData.append('positionY', positionY);
        
        fetch(`/api/notes/${noteId}/position`, {
            method: 'PUT',
            body: formData
        })
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to update note position');
            }
            return response.json();
        })
        .then(data => {
            console.log('Position updated successfully', data);
        })
        .catch(error => {
            console.error('Error updating note position:', error);
        });
    }
    
    function openAddNoteModal() {
        console.log('Opening add note modal');
        // Reset form
        noteForm.reset();
        noteIdInput.value = '';
        modalTitle.textContent = 'Add New Note';
        deleteNoteBtn.style.display = 'none';
        
        // Set default color
        noteColorInput.value = '#FFFF88';
        
        // Open modal
        noteModal.style.display = 'block';
    }
    
    function openEditNoteModal(note) {
        console.log('Opening edit note modal', note);
        // Fill form with note data
        noteIdInput.value = note.id;
        noteTitleInput.value = note.title;
        noteTagInput.value = note.tag || '';
        noteContentInput.value = note.content || '';
        noteColorInput.value = note.color || '#FFFF88';
        
        // Update modal title and show delete button
        modalTitle.textContent = 'Edit Note';
        deleteNoteBtn.style.display = 'block';
        
        // Open modal
        noteModal.style.display = 'block';
    }
    
    function closeModal() {
        noteModal.style.display = 'none';
    }
    
    function saveNote(e) {
        e.preventDefault();
        console.log('Saving note');
        
        const noteId = noteIdInput.value;
        const title = noteTitleInput.value;
        const tag = noteTagInput.value;
        const content = noteContentInput.value;
        const color = noteColorInput.value;
        
        const formData = new FormData();
        formData.append('title', title);
        formData.append('tag', tag);
        formData.append('content', content);
        
        let url = '/api/notes';
        let method = 'POST';
        
        if (noteId) {
            // Update existing note
            url = `/api/notes/${noteId}`;
            method = 'PUT';
        } else {
            // For new notes, also send position and color
            formData.append('color', color);
            
            // Calculate center position
            const boardRect = noteBoard.getBoundingClientRect();
            const positionX = Math.max(0, (window.innerWidth / 2) - 110 + noteBoard.scrollLeft);
            const positionY = Math.max(0, (window.innerHeight / 2) - 75 + noteBoard.scrollTop);
            
            formData.append('positionX', positionX);
            formData.append('positionY', positionY);
        }
        
        fetch(url, {
            method: method,
            body: formData
        })
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to save note');
            }
            return response.json();
        })
        .then(data => {
            console.log('Note saved successfully', data);
            // Reload the page to show the updated notes
            window.location.reload();
        })
        .catch(error => {
            console.error('Error saving note:', error);
        });
    }
    
    function deleteNote() {
        const noteId = noteIdInput.value;
        
        if (!noteId) return;
        
        console.log(`Deleting note ${noteId}`);
        
        if (confirm('Are you sure you want to delete this note?')) {
            fetch(`/api/notes/${noteId}`, {
                method: 'DELETE'
            })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Failed to delete note');
                }
                return response.json();
            })
            .then(data => {
                console.log('Note deleted successfully', data);
                // Reload the page to show the updated notes
                window.location.reload();
            })
            .catch(error => {
                console.error('Error deleting note:', error);
            });
        }
    }
    
    function logout() {
        console.log('Logging out');
        fetch('/api/auth/logout', {
            method: 'POST'
        })
        .then(() => {
            window.location.href = '/login?logout=true';
        })
        .catch(error => {
            console.error('Error logging out:', error);
        });
    }
});