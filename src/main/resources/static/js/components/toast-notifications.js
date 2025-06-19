class ToastNotifications {
    constructor() {
        this.container = null;
        this.createContainer();
    }

    createContainer() {
        this.container = document.createElement('div');
        this.container.className = 'toast-container';
        document.body.appendChild(this.container);
    }

    show(type = 'info', message, duration = AppConfig.ui.toastDuration) {
        const toast = this.createToast(type, message);
        this.container.appendChild(toast);

        // Автоматическое удаление
        setTimeout(() => {
            this.remove(toast);
        }, duration);

        return toast;
    }

    createToast(type, message) {
        const toast = document.createElement('div');
        toast.className = `toast ${type}`;

        const icons = {
            success: 'fas fa-check-circle',
            error: 'fas fa-exclamation-circle',
            warning: 'fas fa-exclamation-triangle',
            info: 'fas fa-info-circle'
        };

        toast.innerHTML = `
            <div class="flex items-start">
                <i class="${icons[type]} mr-2 mt-0.5"></i>
                <div class="flex-1">
                    <p class="text-sm font-medium">${message}</p>
                </div>
                <button class="toast-close ml-2">
                    <i class="fas fa-times"></i>
                </button>
            </div>
        `;

        // Обработка закрытия
        const closeBtn = toast.querySelector('.toast-close');
        closeBtn.addEventListener('click', () => {
            this.remove(toast);
        });

        return toast;
    }

    remove(toast) {
        if (toast && toast.parentNode) {
            toast.style.animation = 'slideOut 0.3s ease-out forwards';
            setTimeout(() => {
                if (toast.parentNode) {
                    toast.parentNode.removeChild(toast);
                }
            }, 300);
        }
    }

    // Удобные методы
    success(message, duration) {
        return this.show('success', message, duration);
    }

    error(message, duration) {
        return this.show('error', message, duration);
    }

    warning(message, duration) {
        return this.show('warning', message, duration);
    }

    info(message, duration) {
        return this.show('info', message, duration);
    }
}

// Глобальный экземпляр
window.toastNotifications = new ToastNotifications();