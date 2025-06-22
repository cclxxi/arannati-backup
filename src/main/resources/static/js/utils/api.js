class ApiClient {
    constructor() {
        this.baseUrl = AppConfig.apiBaseUrl;
        this.csrfToken = AppConfig.csrfToken;
        this.csrfHeader = AppConfig.csrfHeader;
        this.showErrorToasts = true; // Флаг для отображения ошибок в виде тостов
    }

    /**
     * Обработка ошибок API
     * @param {Response} response - Ответ от сервера
     * @param {Error} error - Объект ошибки (опционально)
     * @returns {Promise<Object>} - Объект с информацией об ошибке
     */
    async handleError(response, error = null) {
        let errorData = {
            status: response?.status || 500,
            message: error?.message || 'Неизвестная ошибка',
            errors: []
        };

        // Пытаемся получить детали ошибки из ответа
        if (response) {
            try {
                const contentType = response.headers.get('content-type');
                if (contentType && contentType.includes('application/json')) {
                    const data = await response.json();
                    errorData.message = data.message || errorData.message;
                    errorData.errors = data.errors || [];
                }
            } catch (e) {
                console.error('Ошибка при парсинге ответа:', e);
            }
        }

        // Формируем понятное сообщение об ошибке в зависимости от статуса
        if (!errorData.message || errorData.message.includes('HTTP')) {
            switch (errorData.status) {
                case 400:
                    errorData.message = 'Неверный запрос';
                    break;
                case 401:
                    errorData.message = 'Требуется авторизация';
                    break;
                case 403:
                    errorData.message = 'Доступ запрещен';
                    break;
                case 404:
                    errorData.message = 'Ресурс не найден';
                    break;
                case 409:
                    errorData.message = 'Конфликт данных';
                    break;
                case 422:
                    errorData.message = 'Ошибка валидации';
                    break;
                case 500:
                    errorData.message = 'Внутренняя ошибка сервера';
                    break;
                default:
                    errorData.message = `Ошибка ${errorData.status}`;
            }
        }

        // Показываем уведомление об ошибке, если включено
        if (this.showErrorToasts && window.toastNotifications) {
            window.toastNotifications.error(errorData.message);

            // Если есть ошибки валидации, показываем их тоже
            if (errorData.errors && errorData.errors.length > 0) {
                errorData.errors.forEach(err => {
                    window.toastNotifications.error(`${err.field}: ${err.message}`);
                });
            }
        }

        console.error('API Error:', errorData);
        return errorData;
    }

    // Базовый метод для запросов
    async request(url, options = {}) {
        const config = {
            headers: {
                'Content-Type': 'application/json',
                ...options.headers
            },
            ...options
        };

        // Добавляем CSRF токен для POST/PUT/DELETE запросов
        if (['POST', 'PUT', 'DELETE'].includes(config.method?.toUpperCase())) {
            if (this.csrfToken && this.csrfHeader) {
                config.headers[this.csrfHeader] = this.csrfToken;
            }
        }

        try {
            const response = await fetch(url, config);

            if (!response.ok) {
                const errorData = await this.handleError(response);
                throw new ApiError(errorData.message, errorData.status, errorData.errors);
            }

            const contentType = response.headers.get('content-type');
            if (contentType && contentType.includes('application/json')) {
                return await response.json();
            }

            return await response.text();
        } catch (error) {
            if (error instanceof ApiError) {
                throw error;
            } else {
                console.error('API Request failed:', error);
                const errorData = await this.handleError(null, error);
                throw new ApiError(errorData.message, errorData.status);
            }
        }
    }

    // GET запрос
    async get(url, params = {}) {
        const urlWithParams = new URL(url, window.location.origin);
        Object.keys(params).forEach(key =>
            urlWithParams.searchParams.append(key, params[key])
        );

        return this.request(urlWithParams.toString(), { method: 'GET' });
    }

    // POST запрос
    async post(url, data = {}) {
        return this.request(url, {
            method: 'POST',
            body: JSON.stringify(data)
        });
    }

    // PUT запрос
    async put(url, data = {}) {
        return this.request(url, {
            method: 'PUT',
            body: JSON.stringify(data)
        });
    }

    // DELETE запрос
    async delete(url) {
        return this.request(url, { method: 'DELETE' });
    }

    // Загрузка файла
    async uploadFile(url, formData) {
        const config = {
            method: 'POST',
            body: formData,
            headers: {}
        };

        // Добавляем CSRF токен
        if (this.csrfToken && this.csrfHeader) {
            config.headers[this.csrfHeader] = this.csrfToken;
        }

        // НЕ устанавливаем Content-Type для FormData
        return this.request(url, config);
    }

    // Проверка доступности email
    async checkEmailAvailability(email) {
        try {
            const isAvailable = await this.get('/auth/check-email', { email });
            return { success: true, available: isAvailable };
        } catch (error) {
            return { success: false, error: error.message };
        }
    }

    /**
     * Отключить автоматическое отображение ошибок в виде тостов
     */
    disableErrorToasts() {
        this.showErrorToasts = false;
        return this;
    }

    /**
     * Включить автоматическое отображение ошибок в виде тостов
     */
    enableErrorToasts() {
        this.showErrorToasts = true;
        return this;
    }
}

/**
 * Класс для ошибок API
 */
class ApiError extends Error {
    constructor(message, status = 500, errors = []) {
        super(message);
        this.name = 'ApiError';
        this.status = status;
        this.errors = errors;
    }
}

// Глобальный экземпляр API клиента
window.api = new ApiClient();

// Экспортируем класс ошибки для использования в других модулях
window.ApiError = ApiError;
