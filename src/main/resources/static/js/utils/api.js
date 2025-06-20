class ApiClient {
    constructor() {
        this.baseUrl = AppConfig.apiBaseUrl;
        this.csrfToken = AppConfig.csrfToken;
        this.csrfHeader = AppConfig.csrfHeader;
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
                throw new Error(`HTTP ${response.status}: ${response.statusText}`);
            }

            const contentType = response.headers.get('content-type');
            if (contentType && contentType.includes('application/json')) {
                return await response.json();
            }

            return await response.text();
        } catch (error) {
            console.error('API Request failed:', error);
            throw error;
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
}

// Глобальный экземпляр API клиента
window.api = new ApiClient();