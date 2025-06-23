document.addEventListener('DOMContentLoaded', function() {
    // DOM Elements
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
});