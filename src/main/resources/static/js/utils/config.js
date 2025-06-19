window.AppConfig = {
    apiBaseUrl: '/api',
    csrfToken: document.querySelector('meta[name="_csrf"]')?.getAttribute('content'),
    csrfHeader: document.querySelector('meta[name="_csrf_header"]')?.getAttribute('content'),

    // Validation rules
    validation: {
        email: /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/,
        phone: /^[\+]?[1-9][\d]{0,15}$/,
        password: {
            minLength: 6,
            maxLength: 50
        },
        file: {
            maxSize: 10 * 1024 * 1024, // 10MB
            allowedTypes: ['image/jpeg', 'image/jpg', 'image/png', 'application/pdf']
        }
    },

    // UI settings
    ui: {
        toastDuration: 5000,
        animationDuration: 300,
        debounceDelay: 500
    },

    // Messages
    messages: {
        success: {
            emailAvailable: 'Email доступен',
            passwordStrong: 'Надежный пароль',
            fileUploaded: 'Файл загружен успешно'
        },
        error: {
            emailTaken: 'Email уже используется',
            passwordWeak: 'Пароль слишком простой',
            fileTooLarge: 'Файл слишком большой',
            fileTypeInvalid: 'Недопустимый тип файла',
            networkError: 'Ошибка сети. Попробуйте позже.',
            invalidInput: 'Проверьте правильность введенных данных'
        }
    }
};