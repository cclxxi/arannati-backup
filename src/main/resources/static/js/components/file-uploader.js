class FileUploader {
    constructor(container) {
        this.container = container;
        this.input = container.querySelector('input[type="file"]');
        this.placeholder = container.querySelector('.upload-placeholder');
        this.preview = container.querySelector('.upload-preview');

        this.init();
    }

    init() {
        // Click to upload
        this.container.addEventListener('click', () => {
            if (!this.preview.classList.contains('hidden')) return;
            this.input.click();
        });

        // File input change
        this.input.addEventListener('change', (e) => {
            this.handleFileSelect(e.target.files[0]);
        });

        // Drag and drop
        this.container.addEventListener('dragover', (e) => {
            e.preventDefault();
            this.container.classList.add('dragover');
        });

        this.container.addEventListener('dragleave', () => {
            this.container.classList.remove('dragover');
        });

        this.container.addEventListener('drop', (e) => {
            e.preventDefault();
            this.container.classList.remove('dragover');

            const files = e.dataTransfer.files;
            if (files.length > 0) {
                this.handleFileSelect(files[0]);
            }
        });

        // Remove file
        const removeBtn = this.preview.querySelector('.remove-file');
        if (removeBtn) {
            removeBtn.addEventListener('click', (e) => {
                e.stopPropagation();
                this.removeFile();
            });
        }
    }

    handleFileSelect(file) {
        if (!file) return;

        // Валидация файла
        const validation = validator.validateFile(file);
        if (!validation.isValid) {
            window.toastNotifications.show('error', validation.message);
            return;
        }

        // Показываем превью
        this.showPreview(file);

        // Триггерим событие для формы
        this.input.dispatchEvent(new Event('change', { bubbles: true }));
    }

    showPreview(file) {
        const fileName = this.preview.querySelector('.file-name');
        const fileSize = this.preview.querySelector('.file-size');

        if (fileName) {
            fileName.textContent = file.name;
        }

        if (fileSize) {
            fileSize.textContent = this.formatFileSize(file.size);
        }

        this.placeholder.classList.add('hidden');
        this.preview.classList.remove('hidden');
    }

    removeFile() {
        this.input.value = '';
        this.placeholder.classList.remove('hidden');
        this.preview.classList.add('hidden');

        // Триггерим событие для формы
        this.input.dispatchEvent(new Event('change', { bubbles: true }));
    }

    formatFileSize(bytes) {
        if (bytes === 0) return '0 Bytes';

        const k = 1024;
        const sizes = ['Bytes', 'KB', 'MB', 'GB'];
        const i = Math.floor(Math.log(bytes) / Math.log(k));

        return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
    }
}