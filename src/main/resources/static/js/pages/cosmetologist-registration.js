document.addEventListener('DOMContentLoaded', function() {
    initDiplomaUpload();
});

function initDiplomaUpload() {
    const diplomaUploadArea = document.getElementById('diploma-upload-area');
    if (!diplomaUploadArea) return;

    const fileInput = diplomaUploadArea.querySelector('input[type="file"]');
    const placeholder = diplomaUploadArea.querySelector('.upload-placeholder');
    const preview = diplomaUploadArea.querySelector('.upload-preview');
    const errorMessage = diplomaUploadArea.nextElementSibling;

    if (!fileInput || !placeholder || !preview) return;

    // Click to upload
    diplomaUploadArea.addEventListener('click', function(e) {
        // Don't trigger if we're clicking on the remove button or if preview is already shown
        if (e.target.closest('.remove-file') || !preview.classList.contains('hidden')) return;
        fileInput.click();
    });

    // File input change
    fileInput.addEventListener('change', function(e) {
        const file = e.target.files[0];
        if (!file) return;

        // Validate file
        if (!validateFile(file)) return;

        // Show preview
        showFilePreview(file);
    });

    // Drag and drop
    diplomaUploadArea.addEventListener('dragover', function(e) {
        e.preventDefault();
        e.stopPropagation();
        this.classList.add('border-blue-500');
    });

    diplomaUploadArea.addEventListener('dragleave', function(e) {
        e.preventDefault();
        e.stopPropagation();
        this.classList.remove('border-blue-500');
    });

    diplomaUploadArea.addEventListener('drop', function(e) {
        e.preventDefault();
        e.stopPropagation();
        this.classList.remove('border-blue-500');

        const file = e.dataTransfer.files[0];
        if (!file) return;

        // Validate file
        if (!validateFile(file)) return;

        // Set the file to the input
        fileInput.files = e.dataTransfer.files;

        // Show preview
        showFilePreview(file);
    });

    // Remove file
    const removeBtn = preview.querySelector('.remove-file');
    if (removeBtn) {
        removeBtn.addEventListener('click', function(e) {
            e.preventDefault();
            e.stopPropagation();
            
            // Clear file input
            fileInput.value = '';
            
            // Hide preview, show placeholder
            preview.classList.add('hidden');
            placeholder.classList.remove('hidden');
            
            // Clear error message
            if (errorMessage) {
                errorMessage.textContent = '';
                errorMessage.classList.add('hidden');
            }
        });
    }

    function validateFile(file) {
        // Check file size (max 10MB)
        const maxSize = 10 * 1024 * 1024; // 10MB
        if (file.size > maxSize) {
            showError('Файл слишком большой (макс. 10MB)');
            return false;
        }

        // Check file type
        const allowedTypes = ['image/jpeg', 'image/jpg', 'image/png', 'application/pdf'];
        if (!allowedTypes.includes(file.type)) {
            showError('Недопустимый тип файла. Разрешены только PDF, JPG, PNG');
            return false;
        }

        // Clear any previous error
        if (errorMessage) {
            errorMessage.textContent = '';
            errorMessage.classList.add('hidden');
        }

        return true;
    }

    function showFilePreview(file) {
        const fileName = preview.querySelector('.file-name');
        const fileSize = preview.querySelector('.file-size');

        if (fileName) {
            fileName.textContent = file.name;
        }

        if (fileSize) {
            fileSize.textContent = formatFileSize(file.size);
        }

        // Hide placeholder, show preview
        placeholder.classList.add('hidden');
        preview.classList.remove('hidden');
    }

    function showError(message) {
        if (errorMessage) {
            errorMessage.textContent = message;
            errorMessage.classList.remove('hidden');
        }
    }

    function formatFileSize(bytes) {
        if (bytes === 0) return '0 Bytes';

        const k = 1024;
        const sizes = ['Bytes', 'KB', 'MB', 'GB'];
        const i = Math.floor(Math.log(bytes) / Math.log(k));

        return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
    }
}