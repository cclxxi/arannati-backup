class FormValidator {
    constructor(form) {
        this.form = form;
        this.fields = new Map();
        this.debounceTimers = new Map();

        this.init();
    }

    init() {
        // Находим все поля с валидацией
        const inputs = this.form.querySelectorAll('[data-validate]');
        inputs.forEach(input => this.initField(input));

        // Обработка отправки формы
        this.form.addEventListener('submit', (e) => this.handleSubmit(e));
    }

    initField(input) {
        const rules = input.getAttribute('data-validate').split(',');
        this.fields.set(input, { rules, isValid: false });

        // События валидации
        input.addEventListener('blur', () => this.validateField(input));
        input.addEventListener('input', () => this.debounceValidation(input));

        // Специальная обработка для разных типов полей
        if (input.type === 'password') {
            this.initPasswordField(input);
        }

        if (input.getAttribute('data-validate').includes('remote:')) {
            this.initRemoteValidation(input);
        }
    }

    debounceValidation(input) {
        const fieldId = input.id || input.name;

        if (this.debounceTimers.has(fieldId)) {
            clearTimeout(this.debounceTimers.get(fieldId));
        }

        const timer = setTimeout(() => {
            this.validateField(input);
        }, AppConfig.ui.debounceDelay);

        this.debounceTimers.set(fieldId, timer);
    }

    async validateField(input) {
        const value = input.value.trim();
        const fieldData = this.fields.get(input);
        const rules = fieldData.rules;

        // Очищаем предыдущие ошибки
        this.clearFieldError(input);

        let isValid = true;
        let errorMessage = '';

        // Проверяем каждое правило
        for (const rule of rules) {
            const result = await this.applyRule(input, rule, value);
            if (!result.isValid) {
                isValid = false;
                errorMessage = result.message;
                break;
            }
        }

        // Обновляем состояние поля
        fieldData.isValid = isValid;
        this.updateFieldUI(input, isValid, errorMessage);

        return isValid;
    }

    async applyRule(input, rule, value) {
        const [ruleName, ruleValue] = rule.split(':');

        switch (ruleName.trim()) {
            case 'required':
                return validator.validateRequired(value, this.getFieldLabel(input));

            case 'email':
                return validator.validateEmail(value);

            case 'phone':
                return validator.validatePhone(value);

            case 'minlength':
                const min = parseInt(ruleValue);
                return validator.validateLength(value, min, Infinity, this.getFieldLabel(input));

            case 'maxlength':
                const max = parseInt(ruleValue);
                return validator.validateLength(value, 0, max, this.getFieldLabel(input));

            case 'equalto':
                const targetField = document.querySelector(ruleValue);
                return validator.validateFieldMatch(value, targetField?.value, 'Пароли');

            case 'remote':
                return await this.validateRemote(input, value, ruleValue);

            case 'filesize':
                if (input.type === 'file' && input.files[0]) {
                    const maxSize = this.parseFileSize(ruleValue);
                    if (input.files[0].size > maxSize) {
                        return { isValid: false, message: `Файл слишком большой (макс. ${ruleValue})` };
                    }
                }
                return { isValid: true };

            case 'filetype':
                if (input.type === 'file' && input.files[0]) {
                    const allowedTypes = ruleValue.split(',').map(type =>
                        type.includes('/') ? type : `image/${type}`
                    );
                    if (!allowedTypes.includes(input.files[0].type)) {
                        return { isValid: false, message: `Недопустимый тип файла` };
                    }
                }
                return { isValid: true };

            default:
                return { isValid: true };
        }
    }

    async validateRemote(input, value, endpoint) {
        if (!value) return { isValid: true };

        try {
            const result = await api.checkEmailAvailability(value);
            if (result.success) {
                return result.available
                    ? { isValid: true }
                    : { isValid: false, message: AppConfig.messages.error.emailTaken };
            }
            return { isValid: false, message: AppConfig.messages.error.networkError };
        } catch (error) {
            return { isValid: false, message: AppConfig.messages.error.networkError };
        }
    }

    initPasswordField(input) {
        const strengthContainer = input.parentNode.querySelector('.password-strength');

        if (strengthContainer) {
            input.addEventListener('input', () => {
                const strength = validator.getPasswordStrength(input.value);
                this.updatePasswordStrength(strengthContainer, strength);
            });
        }

        // Toggle password visibility
        const toggleBtn = input.parentNode.querySelector('.password-toggle');
        if (toggleBtn) {
            toggleBtn.addEventListener('click', () => {
                const type = input.type === 'password' ? 'text' : 'password';
                input.type = type;

                const icon = toggleBtn.querySelector('i');
                icon.className = type === 'password' ? 'fas fa-eye' : 'fas fa-eye-slash';
            });
        }
    }

    updatePasswordStrength(container, strength) {
        const bar = container.querySelector('.strength-bar');
        const text = container.querySelector('.strength-text');

        if (bar) {
            bar.className = `strength-bar ${strength.strength}`;
            bar.style.width = `${strength.percentage}%`;
        }

        if (text) {
            const strengthTexts = {
                none: '',
                weak: 'Слабый пароль',
                medium: 'Средний пароль',
                strong: 'Надежный пароль',
                'very-strong': 'Очень надежный пароль'
            };
            text.textContent = strengthTexts[strength.strength] || '';
            text.className = `strength-text text-${strength.strength === 'weak' ? 'danger' : strength.strength === 'medium' ? 'warning' : 'success'}`;
        }

        container.style.display = strength.strength === 'none' ? 'none' : 'block';
    }

    updateFieldUI(input, isValid, errorMessage) {
        // Обновляем классы поля
        input.classList.remove('is-valid', 'is-invalid');
        input.classList.add(isValid ? 'is-valid' : 'is-invalid');

        // Показываем/скрываем сообщение об ошибке
        const errorElement = input.parentNode.querySelector('.error-message');
        if (errorElement) {
            if (!isValid && errorMessage) {
                errorElement.textContent = errorMessage;
                errorElement.classList.remove('hidden');
            } else {
                errorElement.classList.add('hidden');
            }
        }

        // Показываем индикатор доступности email
        if (input.type === 'email' && isValid) {
            const indicator = input.parentNode.querySelector('.email-availability');
            if (indicator) {
                indicator.classList.remove('hidden');
            }
        }
    }

    clearFieldError(input) {
        input.classList.remove('is-valid', 'is-invalid');
        const errorElement = input.parentNode.querySelector('.error-message');
        if (errorElement) {
            errorElement.classList.add('hidden');
        }
    }

    async handleSubmit(e) {
        e.preventDefault();

        // Валидируем все поля
        const validationPromises = Array.from(this.fields.keys()).map(input =>
            this.validateField(input)
        );

        const results = await Promise.all(validationPromises);
        const isFormValid = results.every(result => result);

        if (isFormValid) {
            // Показываем состояние загрузки
            const submitBtn = this.form.querySelector('[type="submit"]');
            if (submitBtn) {
                submitBtn.classList.add('loading');
                submitBtn.disabled = true;
            }

            // Отправляем форму
            this.form.submit();
        } else {
            // Фокусируемся на первом невалидном поле
            const firstInvalidField = Array.from(this.fields.keys()).find(input =>
                !this.fields.get(input).isValid
            );
            if (firstInvalidField) {
                firstInvalidField.focus();
            }
        }
    }

    getFieldLabel(input) {
        const label = this.form.querySelector(`label[for="${input.id}"]`);
        return label ? label.textContent.replace('*', '').trim() : 'Поле';
    }

    parseFileSize(sizeStr) {
        const units = { 'KB': 1024, 'MB': 1024 * 1024, 'GB': 1024 * 1024 * 1024 };
        const match = sizeStr.match(/^(\d+)(KB|MB|GB)$/i);
        if (match) {
            return parseInt(match[1]) * units[match[2].toUpperCase()];
        }
        return parseInt(sizeStr);
    }
}