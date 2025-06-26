<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Сообщения - Arannati</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
    <style>
        .chat-item:hover { background-color: #f8fafc; }
        .chat-item.unread { border-left: 4px solid #3b82f6; background-color: #eff6ff; }
        .message-preview { display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; }
        .online-indicator { position: absolute; bottom: 0; right: 0; width: 12px; height: 12px; background: #10b981; border: 2px solid white; border-radius: 50%; }
        .typing-indicator { display: none; }
        .typing-indicator.show { display: inline-block; }
        .fade-in { animation: fadeIn 0.3s ease-in; }
        @keyframes fadeIn { from { opacity: 0; transform: translateY(10px); } to { opacity: 1; transform: translateY(0); } }
    </style>
</head>
<body class="bg-gray-50 min-h-screen">
<div class="flex h-screen">
    <!-- Sidebar with Chats -->
    <div class="w-1/3 bg-white border-r border-gray-200 flex flex-col">
        <!-- Header -->
        <div class="p-4 border-b border-gray-200 bg-white sticky top-0 z-10">
            <div class="flex items-center justify-between mb-4">
                <h1 class="text-xl font-semibold text-gray-800">Сообщения</h1>
                <div class="flex items-center space-x-2">
                    <!-- Write to Admin Button (for non-admins) -->
                    <#if currentUser.role != "ADMIN">
                        <button onclick="openWriteAdminModal()"
                                class="p-2 text-blue-600 hover:bg-blue-50 rounded-lg transition-colors"
                                title="Написать администратору">
                            <i class="fas fa-edit"></i>
                        </button>
                    </#if>

                    <!-- Refresh Button -->
                    <button onclick="refreshChats()"
                            class="p-2 text-gray-600 hover:bg-gray-50 rounded-lg transition-colors"
                            title="Обновить">
                        <i class="fas fa-sync-alt"></i>
                    </button>
                </div>
            </div>

            <!-- Search -->
            <div class="relative">
                <input type="text"
                       id="chatSearch"
                       placeholder="Поиск чатов..."
                       class="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent">
                <i class="fas fa-search absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400"></i>
            </div>
        </div>

        <!-- Support Requests (for admins) -->
        <#if currentUser.role == "ADMIN" && supportRequests?size gt 0>
            <div class="bg-yellow-50 border-b border-yellow-200 p-4">
                <h3 class="text-sm font-medium text-yellow-800 mb-2">
                    <i class="fas fa-exclamation-triangle mr-1"></i>
                    Новые запросы поддержки (${supportRequests?size})
                </h3>
                <div class="space-y-2">
                    <#list supportRequests as request>
                        <div class="bg-white rounded-lg p-3 border border-yellow-200">
                            <div class="flex items-start justify-between">
                                <div class="flex-1">
                                    <p class="text-sm font-medium text-gray-900">${request.senderName}</p>
                                    <p class="text-xs text-gray-600">${request.senderEmail}</p>
                                    <p class="text-sm text-gray-700 mt-1 message-preview">${request.content}</p>
                                </div>
                                <button onclick="replyToSupport(${request.id})"
                                        class="ml-2 px-3 py-1 bg-blue-600 text-white text-xs rounded hover:bg-blue-700 transition-colors">
                                    Ответить
                                </button>
                            </div>
                            <span class="text-xs text-gray-500">${request.formattedTime}</span>
                        </div>
                    </#list>
                </div>
            </div>
        </#if>

        <!-- Chats List -->
        <div class="flex-1 overflow-y-auto" id="chatsList">
            <#if chats?size == 0>
                <div class="p-8 text-center text-gray-500">
                    <i class="fas fa-comments text-4xl mb-4 text-gray-300"></i>
                    <p class="text-lg mb-2">Нет активных чатов</p>
                    <p class="text-sm">
                        <#if currentUser.role == "ADMIN">
                            Ожидайте обращений от пользователей
                        <#else>
                            Нажмите кнопку "Написать администратору" чтобы начать общение
                        </#if>
                    </p>
                </div>
            <#else>
                <#list chats as chat>
                    <div class="chat-item cursor-pointer p-4 border-b border-gray-100 ${chat.hasUnreadMessages?string('unread', '')}"
                         onclick="openChat('${chat.chatId}')"
                         data-chat-id="${chat.chatId}">
                        <div class="flex items-start space-x-3">
                            <!-- Avatar -->
                            <div class="relative flex-shrink-0">
                                <div class="w-12 h-12 bg-gradient-to-br from-blue-500 to-purple-600 rounded-full flex items-center justify-center text-white font-semibold">
                                    <#if chat.otherUserRole == "ADMIN">
                                        <i class="fas fa-user-shield"></i>
                                    <#elseif chat.otherUserRole == "COSMETOLOGIST">
                                        <i class="fas fa-spa"></i>
                                    <#else>
                                        <i class="fas fa-user"></i>
                                    </#if>
                                </div>
                                <#if chat.hasUnreadMessages>
                                    <div class="absolute -top-1 -right-1 w-5 h-5 bg-red-500 rounded-full flex items-center justify-center">
                                        <span class="text-xs text-white font-bold">${chat.unreadCount}</span>
                                    </div>
                                </#if>
                            </div>

                            <!-- Chat Info -->
                            <div class="flex-1 min-w-0">
                                <div class="flex items-center justify-between mb-1">
                                    <h3 class="text-sm font-semibold text-gray-900 truncate">
                                        ${chat.otherUserName}
                                        <#if chat.otherUserRole == "COSMETOLOGIST" && !chat.otherUserVerified>
                                            <span class="ml-1 text-xs text-yellow-600">(не верифицирован)</span>
                                        </#if>
                                    </h3>
                                    <span class="text-xs text-gray-500">${chat.lastMessageTime}</span>
                                </div>

                                <div class="flex items-center text-xs text-gray-600 mb-2">
                                    <i class="${chat.chatIcon} mr-1"></i>
                                    <span class="capitalize">${chat.otherUserRole?lower_case}</span>
                                    <span class="mx-1">•</span>
                                    <span>${chat.otherUserEmail}</span>
                                </div>

                                <p class="text-sm text-gray-700 message-preview">
                                    <#if chat.lastMessageFromMe>
                                        <span class="text-blue-600">Вы: </span>
                                    </#if>
                                    ${chat.lastMessage}
                                </p>

                                <#if chat.isSystemChat()>
                                    <div class="mt-2">
                                            <span class="inline-block px-2 py-1 bg-gray-100 text-gray-700 text-xs rounded-full">
                                                <i class="fas fa-cog mr-1"></i>
                                                Системное сообщение
                                            </span>
                                    </div>
                                </#if>
                            </div>
                        </div>
                    </div>
                </#list>
            </#if>
        </div>
    </div>

    <!-- Chat Area -->
    <div class="flex-1 flex flex-col bg-gray-50">
        <div id="chatArea" class="flex-1 flex items-center justify-center">
            <div class="text-center text-gray-500">
                <i class="fas fa-comments text-6xl mb-4 text-gray-300"></i>
                <h2 class="text-xl font-semibold mb-2">Выберите чат</h2>
                <p>Выберите чат из списка слева, чтобы начать общение</p>
            </div>
        </div>
    </div>
</div>

<!-- Write to Admin Modal -->
<div id="writeAdminModal" class="fixed inset-0 bg-gray-600 bg-opacity-50 hidden z-50">
    <div class="flex items-center justify-center min-h-screen p-4">
        <div class="bg-white rounded-lg shadow-xl max-w-md w-full">
            <div class="p-6">
                <div class="flex items-center justify-between mb-4">
                    <h3 class="text-lg font-semibold text-gray-900">
                        <#if currentUser.role == "COSMETOLOGIST">
                            Обратиться в службу поддержки
                        <#else>
                            Написать администратору
                        </#if>
                    </h3>
                    <button onclick="closeWriteAdminModal()" class="text-gray-400 hover:text-gray-600">
                        <i class="fas fa-times"></i>
                    </button>
                </div>

                <form id="writeAdminForm">
                    <div class="mb-4">
                        <label class="block text-sm font-medium text-gray-700 mb-2">Сообщение</label>
                        <textarea id="adminMessage"
                                  class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent resize-none"
                                  rows="4"
                                  placeholder="Опишите вашу проблему или вопрос..."
                                  required></textarea>
                    </div>

                    <div class="flex items-center justify-end space-x-3">
                        <button type="button"
                                onclick="closeWriteAdminModal()"
                                class="px-4 py-2 text-gray-700 border border-gray-300 rounded-lg hover:bg-gray-50 transition-colors">
                            Отмена
                        </button>
                        <button type="submit"
                                class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors">
                            <i class="fas fa-paper-plane mr-2"></i>
                            Отправить
                        </button>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>

<!-- Reply to Support Modal -->
<div id="replySupportModal" class="fixed inset-0 bg-gray-600 bg-opacity-50 hidden z-50">
    <div class="flex items-center justify-center min-h-screen p-4">
        <div class="bg-white rounded-lg shadow-xl max-w-md w-full">
            <div class="p-6">
                <div class="flex items-center justify-between mb-4">
                    <h3 class="text-lg font-semibold text-gray-900">Ответить на запрос</h3>
                    <button onclick="closeReplySupportModal()" class="text-gray-400 hover:text-gray-600">
                        <i class="fas fa-times"></i>
                    </button>
                </div>

                <div id="originalRequestInfo" class="mb-4 p-3 bg-gray-50 rounded-lg">
                    <!-- Original request info will be inserted here -->
                </div>

                <form id="replySupportForm">
                    <input type="hidden" id="originalMessageId">
                    <div class="mb-4">
                        <label class="block text-sm font-medium text-gray-700 mb-2">Ваш ответ</label>
                        <textarea id="replyMessage"
                                  class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent resize-none"
                                  rows="4"
                                  placeholder="Введите ваш ответ..."
                                  required></textarea>
                    </div>

                    <div class="flex items-center justify-end space-x-3">
                        <button type="button"
                                onclick="closeReplySupportModal()"
                                class="px-4 py-2 text-gray-700 border border-gray-300 rounded-lg hover:bg-gray-50 transition-colors">
                            Отмена
                        </button>
                        <button type="submit"
                                class="px-4 py-2 bg-green-600 text-white rounded-lg hover:bg-green-700 transition-colors">
                            <i class="fas fa-reply mr-2"></i>
                            Ответить
                        </button>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>

<script>
    let currentChatId = null;
    let currentUser = {
        id: ${currentUser.id},
        role: '${currentUser.role}',
        name: '${currentUser.firstName} ${currentUser.lastName}'
    };

    // Открытие модального окна для написания админу
    function openWriteAdminModal() {
        document.getElementById('writeAdminModal').classList.remove('hidden');
        document.getElementById('adminMessage').focus();
    }

    function closeWriteAdminModal() {
        document.getElementById('writeAdminModal').classList.add('hidden');
        document.getElementById('writeAdminForm').reset();
    }

    // Обработка отправки сообщения админу
    document.getElementById('writeAdminForm').addEventListener('submit', async function(e) {
        e.preventDefault();

        const content = document.getElementById('adminMessage').value.trim();
        if (!content) return;

        try {
            const response = await fetch('/messages/support', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: 'content=' + encodeURIComponent(content)
            });

            const result = await response.json();

            if (result.success) {
                closeWriteAdminModal();
                showNotification('Ваш запрос отправлен администраторам', 'success');
                setTimeout(() => {
                    window.location.reload();
                }, 1000);
            } else {
                showNotification(result.error || 'Ошибка отправки сообщения', 'error');
            }
        } catch (error) {
            console.error('Error:', error);
            showNotification('Ошибка отправки сообщения', 'error');
        }
    });

    // Ответ на запрос поддержки (для админов)
    function replyToSupport(messageId) {
        // Находим сообщение в массиве supportRequests
        const supportRequests = [
            <#list supportRequests as request>
            {
                id: ${request.id},
                senderName: '${request.senderName}',
                senderEmail: '${request.senderEmail}',
                content: '${request.content?js_string}',
                formattedTime: '${request.formattedTime}'
            }<#if request_has_next>,</#if>
            </#list>
        ];

        const request = supportRequests.find(r => r.id === messageId);
        if (!request) return;

        // Заполняем информацию об оригинальном запросе
        document.getElementById('originalRequestInfo').innerHTML = `
                <div class="text-sm">
                    <p class="font-medium text-gray-900">${request.senderName}</p>
                    <p class="text-gray-600">${request.senderEmail}</p>
                    <p class="mt-2 text-gray-700">${request.content}</p>
                    <p class="mt-1 text-xs text-gray-500">${request.formattedTime}</p>
                </div>
            `;

        document.getElementById('originalMessageId').value = messageId;
        document.getElementById('replySupportModal').classList.remove('hidden');
        document.getElementById('replyMessage').focus();
    }

    function closeReplySupportModal() {
        document.getElementById('replySupportModal').classList.add('hidden');
        document.getElementById('replySupportForm').reset();
    }

    // Обработка ответа на запрос поддержки
    document.getElementById('replySupportForm').addEventListener('submit', async function(e) {
        e.preventDefault();

        const originalMessageId = document.getElementById('originalMessageId').value;
        const content = document.getElementById('replyMessage').value.trim();

        if (!content) return;

        try {
            const response = await fetch('/messages/support/reply', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: `originalMessageId=${originalMessageId}&content=${encodeURIComponent(content)}`
            });

            const result = await response.json();

            if (result.success) {
                closeReplySupportModal();
                showNotification('Ответ отправлен', 'success');

                // Перенаправляем в открывшийся чат
                if (result.chatId) {
                    setTimeout(() => {
                        openChat(result.chatId);
                    }, 1000);
                } else {
                    setTimeout(() => {
                        window.location.reload();
                    }, 1000);
                }
            } else {
                showNotification(result.error || 'Ошибка отправки ответа', 'error');
            }
        } catch (error) {
            console.error('Error:', error);
            showNotification('Ошибка отправки ответа', 'error');
        }
    });

    // Открытие чата
    function openChat(chatId) {
        currentChatId = chatId;

        // Отмечаем выбранный чат
        document.querySelectorAll('.chat-item').forEach(item => {
            item.classList.remove('bg-blue-50', 'border-blue-200');
        });

        const selectedChat = document.querySelector(`[data-chat-id="${chatId}"]`);
        if (selectedChat) {
            selectedChat.classList.add('bg-blue-50', 'border-blue-200');
        }

        // Загружаем чат в iframe или через AJAX
        loadChatContent(chatId);
    }

    async function loadChatContent(chatId) {
        try {
            const chatArea = document.getElementById('chatArea');
            chatArea.innerHTML = '<div class="flex items-center justify-center h-full"><i class="fas fa-spinner fa-spin text-2xl text-gray-400"></i></div>';

            const response = await fetch(`/messages/chat/${chatId}`);
            const html = await response.text();

            chatArea.innerHTML = html;

            // Отмечаем чат как прочитанный
            markChatAsRead(chatId);

        } catch (error) {
            console.error('Error loading chat:', error);
            document.getElementById('chatArea').innerHTML = `
                    <div class="flex items-center justify-center h-full text-red-500">
                        <div class="text-center">
                            <i class="fas fa-exclamation-triangle text-4xl mb-2"></i>
                            <p>Ошибка загрузки чата</p>
                        </div>
                    </div>
                `;
        }
    }

    async function markChatAsRead(chatId) {
        try {
            await fetch(`/messages/api/chat/${chatId}/read`, {
                method: 'POST'
            });

            // Обновляем счетчики непрочитанных
            updateUnreadCounts();

        } catch (error) {
            console.error('Error marking chat as read:', error);
        }
    }

    // Обновление списка чатов
    async function refreshChats() {
        try {
            const response = await fetch('/messages/api/chats');
            const chats = await response.json();

            // Здесь можно обновить список чатов без перезагрузки страницы
            // Пока просто перезагружаем страницу
            window.location.reload();

        } catch (error) {
            console.error('Error refreshing chats:', error);
        }
    }

    // Поиск по чатам
    document.getElementById('chatSearch').addEventListener('input', function(e) {
        const query = e.target.value.toLowerCase();
        const chatItems = document.querySelectorAll('.chat-item');

        chatItems.forEach(item => {
            const userName = item.querySelector('h3').textContent.toLowerCase();
            const userEmail = item.querySelector('.text-xs').textContent.toLowerCase();
            const lastMessage = item.querySelector('.message-preview').textContent.toLowerCase();

            if (userName.includes(query) || userEmail.includes(query) || lastMessage.includes(query)) {
                item.style.display = 'block';
            } else {
                item.style.display = 'none';
            }
        });
    });

    // Обновление счетчиков непрочитанных сообщений
    async function updateUnreadCounts() {
        try {
            const response = await fetch('/messages/api/unread-count');
            const data = await response.json();

            // Обновляем общий счетчик в навигации (если есть)
            const badge = document.querySelector('.unread-badge');
            if (badge) {
                if (data.count > 0) {
                    badge.textContent = data.count;
                    badge.classList.remove('hidden');
                } else {
                    badge.classList.add('hidden');
                }
            }

        } catch (error) {
            console.error('Error updating unread counts:', error);
        }
    }

    // Показ уведомлений
    function showNotification(message, type = 'info') {
        const notification = document.createElement('div');

        // Определение класса цвета без тернарного оператора
        let colorClass = 'bg-blue-500'; // значение по умолчанию для 'info'

        if (type === 'success') {
            colorClass = 'bg-green-500';
        } else if (type === 'error') {
            colorClass = 'bg-red-500';
        }

        // Применение класса к элементу
        notification.className = `fixed top-4 right-4 z-50 px-6 py-3 rounded-lg shadow-lg text-white ${colorClass} fade-in`;
        notification.textContent = message;

        document.body.appendChild(notification);

        setTimeout(() => {
            notification.remove();
        }, 3000);
    }

    // Автообновление каждые 30 секунд
    setInterval(updateUnreadCounts, 30000);

    // Закрытие модальных окон по клику вне их
    window.addEventListener('click', function(e) {
        const writeModal = document.getElementById('writeAdminModal');
        const replyModal = document.getElementById('replySupportModal');

        if (e.target === writeModal) {
            closeWriteAdminModal();
        }
        if (e.target === replyModal) {
            closeReplySupportModal();
        }
    });

    // Инициализация
    document.addEventListener('DOMContentLoaded', function() {
        updateUnreadCounts();
    });
</script>
</body>
</html>