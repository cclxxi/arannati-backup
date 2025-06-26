<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Отклонение заявки косметолога</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
</head>
<body class="bg-gray-50">
<div class="min-h-screen flex items-center justify-center p-4">
    <div class="bg-white rounded-lg shadow-xl max-w-2xl w-full">
        <div class="p-6">
            <!-- Header -->
            <div class="flex items-center justify-between mb-6">
                <div class="flex items-center space-x-3">
                    <div class="w-10 h-10 bg-red-100 rounded-full flex items-center justify-center">
                        <i class="fas fa-times text-red-600"></i>
                    </div>
                    <div>
                        <h3 class="text-lg font-semibold text-gray-900">Отклонение заявки косметолога</h3>
                        <p class="text-sm text-gray-600">Укажите причину отклонения для отправки уведомления</p>
                    </div>
                </div>
                <button onclick="closeModal()" class="text-gray-400 hover:text-gray-600">
                    <i class="fas fa-times"></i>
                </button>
            </div>

            <!-- Cosmetologist Info -->
            <div class="bg-gray-50 rounded-lg p-4 mb-6">
                <div class="flex items-center space-x-3">
                    <div class="w-12 h-12 bg-purple-100 rounded-full flex items-center justify-center">
                        <i class="fas fa-spa text-purple-600"></i>
                    </div>
                    <div>
                        <h4 class="font-semibold text-gray-900">${cosmetologist.firstName} ${cosmetologist.lastName}</h4>
                        <p class="text-sm text-gray-600">${cosmetologist.email}</p>
                        <p class="text-xs text-gray-500">
                            Дата регистрации: ${cosmetologist.createdAt}
                        </p>
                    </div>
                </div>
            </div>

            <!-- Form -->
            <form id="declineForm">
                <div class="mb-6">
                    <label class="block text-sm font-medium text-gray-700 mb-2">
                        Причина отклонения
                        <span class="text-red-500">*</span>
                    </label>
                    <textarea id="declineReason"
                              class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-red-500 focus:border-transparent resize-none"
                              rows="8"
                              required>${defaultReason}</textarea>
                    <div class="flex items-center justify-between mt-1">
                            <span class="text-xs text-gray-500">
                                <span id="charCount">${defaultReason?length}</span>/2000 символов
                            </span>
                        <span class="text-xs text-gray-600">
                                Это сообщение будет отправлено косметологу
                            </span>
                    </div>
                </div>

                <!-- Quick reason templates -->
                <div class="mb-6">
                    <label class="block text-sm font-medium text-gray-700 mb-3">
                        Шаблоны причин отклонения
                    </label>
                    <div class="grid grid-cols-1 md:grid-cols-2 gap-3">
                        <button type="button"
                                onclick="useTemplate('documents')"
                                class="p-3 text-left border border-gray-200 rounded-lg hover:bg-gray-50 transition-colors">
                            <div class="font-medium text-sm text-gray-900">Проблемы с документами</div>
                            <div class="text-xs text-gray-600 mt-1">Нечеткие или недействительные документы</div>
                        </button>

                        <button type="button"
                                onclick="useTemplate('education')"
                                class="p-3 text-left border border-gray-200 rounded-lg hover:bg-gray-50 transition-colors">
                            <div class="font-medium text-sm text-gray-900">Образование не подходит</div>
                            <div class="text-xs text-gray-600 mt-1">Образование не соответствует требованиям</div>
                        </button>

                        <button type="button"
                                onclick="useTemplate('expired')"
                                class="p-3 text-left border border-gray-200 rounded-lg hover:bg-gray-50 transition-colors">
                            <div class="font-medium text-sm text-gray-900">Документы устарели</div>
                            <div class="text-xs text-gray-600 mt-1">Предоставлены устаревшие документы</div>
                        </button>

                        <button type="button"
                                onclick="useTemplate('incomplete')"
                                class="p-3 text-left border border-gray-200 rounded-lg hover:bg-gray-50 transition-colors">
                            <div class="font-medium text-sm text-gray-900">Неполный пакет</div>
                            <div class="text-xs text-gray-600 mt-1">Предоставлены не все документы</div>
                        </button>
                    </div>
                </div>

                <!-- Action Buttons -->
                <div class="flex items-center justify-end space-x-3">
                    <button type="button"
                            onclick="closeModal()"
                            class="px-6 py-2 text-gray-700 border border-gray-300 rounded-lg hover:bg-gray-50 transition-colors">
                        Отмена
                    </button>
                    <button type="submit"
                            class="px-6 py-2 bg-red-600 text-white rounded-lg hover:bg-red-700 transition-colors">
                        <i class="fas fa-times mr-2"></i>
                        Отклонить заявку
                    </button>
                </div>
            </form>
        </div>
    </div>
</div>

<script>
    const cosmetologistId = ${cosmetologist.id};
    const reasonTextarea = document.getElementById('declineReason');
    const charCount = document.getElementById('charCount');
    const declineForm = document.getElementById('declineForm');

    // Шаблоны сообщений
    const templates = {
        documents: `Ваша заявка на верификацию косметолога была отклонена.

Причина отклонения: Проблемы с предоставленными документами

Детали:
• Документы должны быть четкими и читаемыми
• Сканы или фотографии должны быть хорошего качества
• Все тексты должны быть различимыми
• Документы не должны быть обрезанными

Пожалуйста, загрузите качественные сканы ваших документов и подайте заявку повторно.`,

        education: `Ваша заявка на верификацию косметолога была отклонена.

Причина отклонения: Образование не соответствует требованиям

Для получения статуса косметолога необходимо:
• Диплом о среднем или высшем медицинском образовании
• Сертификат о прохождении курсов косметологии
• Документы о повышении квалификации (при наличии)

Если у вас есть соответствующее образование, пожалуйста, предоставьте корректные документы.`,

        expired: `Ваша заявка на верификацию косметолога была отклонена.

Причина отклонения: Предоставлены устаревшие документы

Детали:
• Некоторые из предоставленных документов утратили актуальность
• Необходимо предоставить действующие документы об образовании
• Сертификаты должны быть актуальными

Пожалуйста, обновите документы и подайте заявку повторно.`,

        incomplete: `Ваша заявка на верификацию косметолога была отклонена.

Причина отклонения: Неполный пакет документов

Для верификации необходимо предоставить:
• Диплом об образовании
• Сертификат косметолога
• Документ, удостоверяющий личность (при необходимости)

Пожалуйста, дополните пакет документов и подайте заявку повторно.`
    };

    // Обновление счетчика символов
    reasonTextarea.addEventListener('input', function() {
        const length = this.value.length;
        charCount.textContent = length;

        if (length > 2000) {
            charCount.classList.add('text-red-500');
            charCount.classList.remove('text-gray-500');
        } else {
            charCount.classList.remove('text-red-500');
            charCount.classList.add('text-gray-500');
        }
    });

    // Использование шаблона
    function useTemplate(templateKey) {
        if (templates[templateKey]) {
            reasonTextarea.value = templates[templateKey];
            reasonTextarea.dispatchEvent(new Event('input'));
            reasonTextarea.focus();
        }
    }

    // Обработка отправки формы
    declineForm.addEventListener('submit', async function(e) {
        e.preventDefault();

        const reason = reasonTextarea.value.trim();
        if (!reason) {
            showNotification('Пожалуйста, укажите причину отклонения', 'error');
            return;
        }

        if (reason.length > 2000) {
            showNotification('Сообщение слишком длинное (максимум 2000 символов)', 'error');
            return;
        }

        // Показываем подтверждение
        if (!confirm(`Вы действительно хотите отклонить заявку косметолога "${cosmetologist.firstName} ${cosmetologist.lastName}"?\n\nЭто действие нельзя отменить.`)) {
            return;
        }

        try {
            // Отключаем форму
            const submitButton = e.target.querySelector('button[type="submit"]');
            submitButton.disabled = true;
            submitButton.innerHTML = '<i class="fas fa-spinner fa-spin mr-2"></i>Отклоняем...';

            // Отклоняем косметолога
            const declineResponse = await fetch(`/admin/cosmetologists/${cosmetologistId}/decline`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: 'reason=' + encodeURIComponent(reason)
            });

            if (declineResponse.ok) {
                // Отправляем сообщение с причиной отклонения
                const messageResponse = await fetch('/messages/decline', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded',
                    },
                    body: `cosmetologistId=${cosmetologistId}&reason=${encodeURIComponent(reason)}`
                });

                if (messageResponse.ok) {
                    showNotification('Заявка отклонена, уведомление отправлено косметологу', 'success');

                    // Перенаправляем обратно
                    setTimeout(() => {
                        window.location.href = '/admin/users';
                    }, 2000);
                } else {
                    showNotification('Заявка отклонена, но не удалось отправить уведомление', 'warning');
                    setTimeout(() => {
                        window.location.href = '/admin/users';
                    }, 2000);
                }
            } else {
                throw new Error('Failed to decline cosmetologist');
            }

        } catch (error) {
            console.error('Error declining cosmetologist:', error);
            showNotification('Ошибка при отклонении заявки', 'error');

            // Включаем форму обратно
            submitButton.disabled = false;
            submitButton.innerHTML = '<i class="fas fa-times mr-2"></i>Отклонить заявку';
        }
    });

    // Закрытие модального окна
    function closeModal() {
        if (confirm('Вы действительно хотите закрыть окно? Введенный текст будет потерян.')) {
            window.location.href = '/admin/users';
        }
    }

    // Показ уведомлений
    function showNotification(message, type = 'info') {
        const notification = document.createElement('div');

        // Определяем класс цвета в зависимости от типа уведомления
        let colorClass = 'bg-blue-500'; // значение по умолчанию для 'info'

        if (type === 'success') {
            colorClass = 'bg-green-500';
        } else if (type === 'error') {
            colorClass = 'bg-red-500';
        } else if (type === 'warning') {
            colorClass = 'bg-yellow-500';
        }

        notification.className = 'fixed top-4 right-4 z-50 px-6 py-3 rounded-lg shadow-lg text-white ' + colorClass;
        notification.textContent = message;

        document.body.appendChild(notification);

        setTimeout(() => {
            notification.remove();
        }, 5000);
    }

    // Закрытие по ESC
    document.addEventListener('keydown', function(e) {
        if (e.key === 'Escape') {
            closeModal();
        }
    });

    // Фокус на textarea при загрузке
    document.addEventListener('DOMContentLoaded', function() {
        reasonTextarea.focus();
        reasonTextarea.setSelectionRange(reasonTextarea.value.length, reasonTextarea.value.length);
    });
</script>
</body>
</html>