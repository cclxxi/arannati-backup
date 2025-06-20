class Validator {
    constructor() {
        this.rules = AppConfig.validation;
    }

    // Валидация email
    validateEmail(email) {
        if (!email) return { isValid: false, message: 'Email обязателен' };
        if (!this.rules.email.test(email)) {
            return { isValid: false, message: 'Некорректный формат email' };
        }
        return { isValid: true };
    }

    // Валидация пароля
    validatePassword(password) {
        if (!password) return { isValid: false, message: 'Пароль обязателен' };
        if (password.length < this.rules.password.minLength) {
            return { isValid: false, message: `Пароль должен содержать минимум ${this.rules.password.minLength} символов` };
        }
        if (password.length > this.rules.password.maxLength) {
            return { isValid: false, message: `Пароль не должен превышать ${this.rules.password.maxLength} символов` };
        }
        return { isValid: true };
    }

    // Проверка силы пароля
    getPasswordStrength(password) {
        if (!password) return { strength: 'none', score: 0 };

        let score = 0;
        let feedback = [];

        // Длина
        if (password.length >= 8) score++;
        else feedback.push('Используйте минимум 8 символов');

        // Заглавные буквы
        if (/[A-Z]/.test(password)) score++;
        else feedback.push('Добавьте заглавные буквы');

        // Строчные буквы
        if (/[a-z]/.test(password)) score++;
        else feedback.push('Добавьте строчные буквы');

        // Цифры
        if (/\d/.test(password)) score++;
        else feedback.push('Добавьте цифры');

        // Специальные символы
        if (/[!@#$%^&*(),.?":{}|<>]/.test(password)) score++;
        else feedback.push('Добавьте специальные символы');

        const strengths = ['none', 'weak', 'medium', 'strong', 'very-strong'];
        const strength = strengths[Math.min(score, 4)];

        return { strength, score, feedback, percentage: (score / 5) * 100 };
    }

    // Валидация телефона
    validatePhone(phone) {
        if (!phone) return { isValid: true }; // телефон необязателен
        if (!this.rules.phone.test(phone)) {
            return { isValid: false, message: 'Некорректный формат телефона' };
        }
        return { isValid: true };
    }

    // Валидация файла
    validateFile(file) {
        if (!file) return { isValid: false, message: 'Файл обязателен' };

        if (file.size > this.rules.file.maxSize) {
            return { isValid: false, message: 'Файл слишком большой (макс. 10MB)' };
        }

        if (!this.rules.file.allowedTypes.includes(file.type)) {
            return { isValid: false, message: 'Недопустимый тип файла' };
        }

        return { isValid: true };
    }

    // Проверка совпадения полей
    validateFieldMatch(value1, value2, fieldName = 'Поля') {
        if (value1 !== value2) {
            return { isValid: false, message: `${fieldName} не совпадают` };
        }
        return { isValid: true };
    }

    // Валидация обязательного поля
    validateRequired(value, fieldName = 'Поле') {
        if (!value || value.toString().trim() === '') {
            return { isValid: false, message: `${fieldName} обязательно` };
        }
        return { isValid: true };
    }

    // Валидация длины
    validateLength(value, min, max, fieldName = 'Поле') {
        if (value && value.length < min) {
            return { isValid: false, message: `${fieldName} должно содержать минимум ${min} символов` };
        }
        if (value && value.length > max) {
            return { isValid: false, message: `${fieldName} не должно превышать ${max} символов` };
        }
        return { isValid: true };
    }
}

// Глобальный экземпляр валидатора
window.validator = new Validator();