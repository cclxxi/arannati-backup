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
        // We'll use the global password toggle functionality from components-init.js
        // This is just for backward compatibility

        // For password strength, we'll use the parent node's parent node to find the container
        // since the container is now outside the input's parent div
        const strengthContainer = input.parentNode.parentNode.querySelector('.password-strength');

        if (strengthContainer) {
            input.addEventListener('input', () => {
                const strength = validator.getPasswordStrength(input.value);
                this.updatePasswordStrength(strengthContainer, strength);
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

        // Очищаем общую ошибку формы при изменении любого поля
        const formErrorContainer = this.form.querySelector('.form-error-message');
        if (formErrorContainer) {
            formErrorContainer.classList.add('hidden');
        }
    }

    /**
     * Отображение ошибок API в форме
     * @param {ApiError} error - Объект ошибки API
     */
    displayApiErrors(error) {
        // Очищаем предыдущие ошибки
        Array.from(this.fields.keys()).forEach(input => this.clearFieldError(input));

        // Показываем общую ошибку
        const errorContainer = this.form.querySelector('.form-error-message');
        if (errorContainer) {
            errorContainer.textContent = error.message || 'Произошла ошибка';
            errorContainer.classList.remove('hidden');
        }

        // Если есть ошибки валидации, отображаем их в соответствующих полях
        if (error.errors && error.errors.length > 0) {
            error.errors.forEach(err => {
                const input = this.form.querySelector(`[name="${err.field}"]`);
                if (input) {
                    const fieldData = this.fields.get(input);
                    if (fieldData) {
                        fieldData.isValid = false;
                        this.updateFieldUI(input, false, err.message);
                    }
                }
            });
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

            // Проверяем, есть ли обработчик отправки формы через AJAX
            const ajaxSubmit = this.form.getAttribute('data-ajax-submit');
            if (ajaxSubmit === 'true') {
                try {
                    await this.submitFormAjax();
                } catch (error) {
                    // Восстанавливаем состояние кнопки
                    if (submitBtn) {
                        submitBtn.classList.remove('loading');
                        submitBtn.disabled = false;
                    }
                }
            } else {
                // Отправляем форму стандартным способом
                this.form.submit();
            }
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

    /**
     * Отправка формы через AJAX
     */
    async submitFormAjax() {
        const formData = new FormData(this.form);
        const url = this.form.getAttribute('action') || window.location.href;
        const method = this.form.getAttribute('method')?.toUpperCase() || 'POST';

        try {
            // Отключаем автоматические тосты, так как будем обрабатывать ошибки вручную
            api.disableErrorToasts();

            let response;
            if (formData.has('file') || this.form.enctype === 'multipart/form-data') {
                // Если форма содержит файлы, используем uploadFile
                response = await api.uploadFile(url, formData);
            } else {
                // Иначе преобразуем FormData в объект
                const data = {};
                formData.forEach((value, key) => {
                    data[key] = value;
                });

                if (method === 'POST') {
                    response = await api.post(url, data);
                } else if (method === 'PUT') {
                    response = await api.put(url, data);
                } else {
                    response = await api.get(url, data);
                }
            }

            // Успешная отправка
            this.handleSuccessResponse(response);
            return response;

        } catch (error) {
            // Обработка ошибок
            this.handleErrorResponse(error);
            throw error;
        } finally {
            // Включаем автоматические тосты обратно
            api.enableErrorToasts();
        }
    }

    /**
     * Обработка успешного ответа от сервера
     */
    handleSuccessResponse(response) {
        // Показываем сообщение об успехе, если указано
        const successMessage = this.form.getAttribute('data-success-message');
        if (successMessage && window.toastNotifications) {
            window.toastNotifications.success(successMessage);
        }

        // Перенаправляем, если указан URL
        const redirectUrl = this.form.getAttribute('data-redirect');
        if (redirectUrl) {
            window.location.href = redirectUrl;
            return;
        }

        // Очищаем форму, если указано
        const resetForm = this.form.getAttribute('data-reset-on-success');
        if (resetForm === 'true') {
            this.form.reset();

            // Сбрасываем состояние валидации
            Array.from(this.fields.keys()).forEach(input => {
                input.classList.remove('is-valid', 'is-invalid');
                const errorElement = input.parentNode.querySelector('.error-message');
                if (errorElement) {
                    errorElement.classList.add('hidden');
                }
            });
        }

        // Восстанавливаем состояние кнопки отправки
        const submitBtn = this.form.querySelector('[type="submit"]');
        if (submitBtn) {
            submitBtn.classList.remove('loading');
            submitBtn.disabled = false;
        }
    }

    /**
     * Обработка ошибки от сервера
     */
    handleErrorResponse(error) {
        // Показываем общую ошибку
        const errorContainer = this.form.querySelector('.form-error-message');
        if (errorContainer) {
            errorContainer.textContent = error.message || 'Произошла ошибка при отправке формы';
            errorContainer.classList.remove('hidden');
        }

        // Если это ошибка API с валидационными ошибками
        if (error instanceof ApiError && error.errors && error.errors.length > 0) {
            // Отображаем ошибки валидации в соответствующих полях
            error.errors.forEach(err => {
                const input = this.form.querySelector(`[name="${err.field}"]`);
                if (input) {
                    input.classList.add('is-invalid');
                    const errorElement = input.parentNode.querySelector('.error-message');
                    if (errorElement) {
                        errorElement.textContent = err.message;
                        errorElement.classList.remove('hidden');
                    }
                }
            });
        }

        // Восстанавливаем состояние кнопки отправки
        const submitBtn = this.form.querySelector('[type="submit"]');
        if (submitBtn) {
            submitBtn.classList.remove('loading');
            submitBtn.disabled = false;
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
