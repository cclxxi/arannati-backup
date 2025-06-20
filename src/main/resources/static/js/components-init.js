document.addEventListener('DOMContentLoaded', function() {
    // Initialize password toggles
    initPasswordToggles();

    // Initialize password strength indicators
    initPasswordStrength();

    // Initialize form validation
    initFormValidation();

    // Initialize toast notifications
    initToastNotifications();
});

function initPasswordToggles() {
    // Find all forms with password fields
    const forms = document.querySelectorAll('form');

    forms.forEach(form => {
        // Find password and confirm password fields in this form
        const passwordField = form.querySelector('#password');
        const confirmPasswordField = form.querySelector('#confirmPassword');

        if (passwordField && confirmPasswordField) {
            // Get all toggle buttons in this form
            const toggles = form.querySelectorAll('.password-toggle');

            // Remove any existing event listeners
            toggles.forEach(toggle => {
                const newToggle = toggle.cloneNode(true);
                toggle.parentNode.replaceChild(newToggle, toggle);
            });

            // Get the updated toggle buttons
            const updatedToggles = form.querySelectorAll('.password-toggle');

            // Add click event to each toggle
            updatedToggles.forEach(toggle => {
                toggle.addEventListener('click', function() {
                    // Determine the new type based on the current password field
                    const newType = passwordField.type === 'password' ? 'text' : 'password';

                    // Update both password fields
                    [passwordField, confirmPasswordField].forEach(input => {
                        if (newType === 'text') {
                            input.setAttribute('data-was-password', 'true');
                        } else {
                            input.removeAttribute('data-was-password');
                        }
                        input.type = newType;
                    });

                    // Update all toggle icons in this form
                    updatedToggles.forEach(toggle => {
                        const icon = toggle.querySelector('i');
                        if (icon) {
                            icon.className = newType === 'password' ? 'fas fa-eye' : 'fas fa-eye-slash';
                        }
                    });
                });
            });
        } else {
            // Handle forms with only one password field (like login form)
            const passwordToggles = form.querySelectorAll('.password-toggle');

            // Remove any existing event listeners
            passwordToggles.forEach(toggle => {
                const newToggle = toggle.cloneNode(true);
                toggle.parentNode.replaceChild(newToggle, toggle);
            });

            // Get the updated toggle buttons
            const updatedToggles = form.querySelectorAll('.password-toggle');

            updatedToggles.forEach(toggle => {
                toggle.addEventListener('click', function() {
                    // Get the input field for this toggle
                    const input = this.parentNode.querySelector('input');
                    if (!input) return;

                    // Toggle the input type
                    const newType = input.type === 'password' ? 'text' : 'password';

                    if (newType === 'text') {
                        input.setAttribute('data-was-password', 'true');
                    } else {
                        input.removeAttribute('data-was-password');
                    }
                    input.type = newType;

                    // Update the toggle icon
                    const icon = this.querySelector('i');
                    if (icon) {
                        icon.className = newType === 'password' ? 'fas fa-eye' : 'fas fa-eye-slash';
                    }
                });
            });
        }
    });
}

function initPasswordStrength() {
    // First, find all forms with password fields
    const forms = document.querySelectorAll('form');

    forms.forEach(form => {
        const passwordField = form.querySelector('#password');
        const confirmPasswordField = form.querySelector('#confirmPassword');

        if (passwordField && confirmPasswordField) {
            // Remove any existing event listeners
            const newPasswordField = passwordField.cloneNode(true);
            passwordField.parentNode.replaceChild(newPasswordField, passwordField);

            const newConfirmField = confirmPasswordField.cloneNode(true);
            confirmPasswordField.parentNode.replaceChild(newConfirmField, confirmPasswordField);

            // Get the updated fields
            const updatedPasswordField = form.querySelector('#password');
            const updatedConfirmField = form.querySelector('#confirmPassword');

            // Find the strength container
            const strengthContainer = updatedPasswordField.parentNode.parentNode.querySelector('.password-strength');
            if (strengthContainer) {
                // Show password strength container by default
                strengthContainer.classList.remove('hidden');

                // Add input event listener to password field
                updatedPasswordField.addEventListener('input', function() {
                    const strength = validator.getPasswordStrength(this.value);

                    const bar = strengthContainer.querySelector('.strength-bar');
                    const text = strengthContainer.querySelector('.strength-text');

                    if (bar) {
                        let barClass = 'strength-bar h-1 w-full rounded ';
                        if (strength.strength === 'weak') {
                            barClass += 'bg-red-500';
                        } else if (strength.strength === 'medium') {
                            barClass += 'bg-yellow-500';
                        } else {
                            barClass += 'bg-green-500';
                        }
                        bar.className = barClass;
                        bar.style.width = strength.percentage + '%';
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

                        let textClass = 'strength-text text-xs mt-1 ';
                        if (strength.strength === 'weak') {
                            textClass += 'text-red-600';
                        } else if (strength.strength === 'medium') {
                            textClass += 'text-yellow-600';
                        } else {
                            textClass += 'text-green-600';
                        }
                        text.className = textClass;
                    }

                    // Only hide if empty password
                    if (this.value === '') {
                        strengthContainer.classList.add('hidden');
                    } else {
                        strengthContainer.classList.remove('hidden');
                    }

                    // Update confirmation field if it exists
                    if (updatedConfirmField && updatedConfirmField.value) {
                        // Trigger validation to update the match status
                        const event = new Event('input', { bubbles: true });
                        updatedConfirmField.dispatchEvent(event);
                    }
                });

                // Add input event listener to confirmation field
                updatedConfirmField.addEventListener('input', function() {
                    const errorElement = this.parentNode.querySelector('.error-message');
                    if (errorElement) {
                        if (this.value && this.value !== updatedPasswordField.value) {
                            errorElement.textContent = 'Пароли не совпадают';
                            errorElement.classList.remove('hidden');
                            this.classList.add('border-red-500');
                            this.classList.remove('border-green-500');
                        } else {
                            errorElement.classList.add('hidden');
                            this.classList.remove('border-red-500');
                            if (this.value && this.value === updatedPasswordField.value) {
                                this.classList.add('border-green-500');
                            } else {
                                this.classList.remove('border-green-500');
                            }
                        }
                    }
                });
            }
        }
    });

    // Handle forms with only one password field (like login form)
    document.querySelectorAll('form').forEach(form => {
        // Skip forms that we've already processed (those with both password and confirm password fields)
        if (form.querySelector('#password') && form.querySelector('#confirmPassword')) {
            return;
        }

        // Find password fields that are not #password or #confirmPassword
        const passwordFields = form.querySelectorAll('input[type="password"]');
        passwordFields.forEach(passwordField => {
            // Skip if this is a #password or #confirmPassword field in a form we've already processed
            if ((passwordField.id === 'password' || passwordField.id === 'confirmPassword') && 
                form.querySelector('#password') && form.querySelector('#confirmPassword')) {
                return;
            }

            const strengthContainer = passwordField.parentNode.parentNode.querySelector('.password-strength');
            if (strengthContainer) {
                // Show password strength container by default
                strengthContainer.classList.remove('hidden');

                // Remove any existing event listeners
                const newPasswordField = passwordField.cloneNode(true);
                passwordField.parentNode.replaceChild(newPasswordField, passwordField);

                // Get the updated field
                const updatedPasswordField = newPasswordField;

                // Add input event listener
                updatedPasswordField.addEventListener('input', function() {
                    const strength = validator.getPasswordStrength(this.value);

                    const bar = strengthContainer.querySelector('.strength-bar');
                    const text = strengthContainer.querySelector('.strength-text');

                    if (bar) {
                        let barClass = 'strength-bar h-1 w-full rounded ';
                        if (strength.strength === 'weak') {
                            barClass += 'bg-red-500';
                        } else if (strength.strength === 'medium') {
                            barClass += 'bg-yellow-500';
                        } else {
                            barClass += 'bg-green-500';
                        }
                        bar.className = barClass;
                        bar.style.width = strength.percentage + '%';
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

                        let textClass = 'strength-text text-xs mt-1 ';
                        if (strength.strength === 'weak') {
                            textClass += 'text-red-600';
                        } else if (strength.strength === 'medium') {
                            textClass += 'text-yellow-600';
                        } else {
                            textClass += 'text-green-600';
                        }
                        text.className = textClass;
                    }

                    // Only hide if empty password
                    if (this.value === '') {
                        strengthContainer.classList.add('hidden');
                    } else {
                        strengthContainer.classList.remove('hidden');
                    }
                });
            }
        });
    });
}

function initFormValidation() {
    const forms = document.querySelectorAll('form');
    forms.forEach(form => {
        if (form.classList.contains('auth-form')) {
            new FormValidator(form);
        }
    });
}

function initToastNotifications() {
    // Check if there are any flash messages
    const flashMessages = document.querySelectorAll('.alert');
    flashMessages.forEach(message => {
        const type = message.classList.contains('alert-success') ? 'success' : 
                    message.classList.contains('alert-danger') ? 'error' : 
                    message.classList.contains('alert-warning') ? 'warning' : 'info';

        const text = message.textContent.trim();
        if (text) {
            toastNotifications.show(type, text);
        }
    });
}

// TODO: 1) Password confirmation live comparison.
//  2) Password show/hide toggle unified for both password and confirmation fields.
