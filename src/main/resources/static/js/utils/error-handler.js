/**
 * Утилита для обработки ошибок в JavaScript
 */
class ErrorHandler {
    constructor() {
        this.defaultErrorMessage = 'Произошла непредвиденная ошибка';
        this.init();
    }

    /**
     * Инициализация обработчиков ошибок
     */
    init() {
        // Глобальный обработчик необработанных ошибок
        window.addEventListener('error', (event) => {
            this.handleGlobalError(event.error || new Error(event.message));
            // Не предотвращаем стандартное поведение браузера
        });

        // Глобальный обработчик необработанных промисов
        window.addEventListener('unhandledrejection', (event) => {
            this.handleGlobalPromiseError(event.reason);
            // Не предотвращаем стандартное поведение браузера
        });

        // Перехватываем ошибки fetch API
        this.interceptFetchErrors();
    }

    /**
     * Обработка глобальных ошибок JavaScript
     * @param {Error} error - Объект ошибки
     */
    handleGlobalError(error) {
        console.error('Необработанная ошибка:', error);
        
        // Показываем уведомление пользователю, если доступен компонент уведомлений
        if (window.toastNotifications) {
            window.toastNotifications.error(this.formatErrorMessage(error));
        }
    }

    /**
     * Обработка необработанных ошибок промисов
     * @param {any} reason - Причина отклонения промиса
     */
    handleGlobalPromiseError(reason) {
        console.error('Необработанная ошибка промиса:', reason);
        
        // Если это ошибка API, она уже обрабатывается в ApiClient
        if (reason instanceof ApiError) {
            return;
        }
        
        // Показываем уведомление пользователю, если доступен компонент уведомлений
        if (window.toastNotifications) {
            window.toastNotifications.error(this.formatErrorMessage(reason));
        }
    }

    /**
     * Перехват ошибок fetch API
     */
    interceptFetchErrors() {
        const originalFetch = window.fetch;
        
        window.fetch = async (...args) => {
            try {
                const response = await originalFetch(...args);
                return response;
            } catch (error) {
                console.error('Ошибка сети:', error);
                
                // Показываем уведомление пользователю, если доступен компонент уведомлений
                if (window.toastNotifications) {
                    window.toastNotifications.error('Ошибка сети. Пожалуйста, проверьте подключение к интернету.');
                }
                
                throw error;
            }
        };
    }

    /**
     * Форматирование сообщения об ошибке для отображения пользователю
     * @param {Error|string|any} error - Объект ошибки или сообщение
     * @returns {string} - Отформатированное сообщение
     */
    formatErrorMessage(error) {
        if (!error) {
            return this.defaultErrorMessage;
        }
        
        if (typeof error === 'string') {
            return error;
        }
        
        if (error instanceof Error) {
            return error.message || this.defaultErrorMessage;
        }
        
        if (error.message) {
            return error.message;
        }
        
        return this.defaultErrorMessage;
    }

    /**
     * Обработка ошибки и отображение пользователю
     * @param {Error|string|any} error - Объект ошибки или сообщение
     * @param {boolean} showToast - Показывать ли уведомление
     */
    handleError(error, showToast = true) {
        console.error('Ошибка:', error);
        
        if (showToast && window.toastNotifications) {
            window.toastNotifications.error(this.formatErrorMessage(error));
        }
        
        return {
            success: false,
            message: this.formatErrorMessage(error)
        };
    }

    /**
     * Безопасное выполнение асинхронной функции с обработкой ошибок
     * @param {Function} fn - Асинхронная функция для выполнения
     * @param {boolean} showToast - Показывать ли уведомление при ошибке
     * @returns {Promise<{success: boolean, data?: any, error?: string}>} - Результат выполнения
     */
    async safeAsync(fn, showToast = true) {
        try {
            const result = await fn();
            return { success: true, data: result };
        } catch (error) {
            return {
                success: false,
                error: this.formatErrorMessage(error),
                ...this.handleError(error, showToast)
            };
        }
    }
}

// Создаем глобальный экземпляр обработчика ошибок
window.errorHandler = new ErrorHandler();