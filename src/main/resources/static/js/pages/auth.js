document.addEventListener('DOMContentLoaded', function() {
    // Инициализация форм аутентификации
    initAuthForms();
    initMobileMenu();
    initFlashMessages();
});

function initAuthForms() {
    // Находим все формы с классом auth-form
    const authForms = document.querySelectorAll('.auth-form');

    authForms.forEach(form => {
        // Инициализируем валидацию формы
        new FormValidator(form);

        // Инициализируем загрузку файлов (для регистрации косметолога)
        const fileUploadAreas = form.querySelectorAll('.file-upload-area');
        fileUploadAreas.forEach(area => {
            new FileUploader(area);
        });

        // Добавляем анимации при загрузке
        animateFormElements(form);
    });
}

function animateFormElements(form) {
    const formGroups = form.querySelectorAll('.form-group');
    formGroups.forEach((group, index) => {
        group.style.animationDelay = `${index * 0.1}s`;
    });
}

function initMobileMenu() {
    const mobileMenuBtn = document.getElementById('mobile-menu-button');
    const mobileMenu = document.getElementById('mobile-menu');

    if (mobileMenuBtn && mobileMenu) {
        mobileMenuBtn.addEventListener('click', () => {
            mobileMenu.classList.toggle('hidden');
        });
    }
}

function initFlashMessages() {
    // Автоматическое скрытие flash сообщений
    const alerts = document.querySelectorAll('.alert');
    alerts.forEach(alert => {
        setTimeout(() => {
            alert.style.animation = 'fadeOut 0.5s ease-out forwards';
            setTimeout(() => {
                if (alert.parentNode) {
                    alert.parentNode.removeChild(alert);
                }
            }, 500);
        }, 5000);
    });
}