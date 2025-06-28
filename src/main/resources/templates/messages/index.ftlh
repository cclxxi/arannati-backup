<#assign pageTitle = "Сообщения - Arannati">
<#assign pageDescription = "Система сообщений Arannati">
<#include "../layouts/main.ftlh">

<@layout>
    <div class="flex h-screen">
        <!-- Sidebar with Chats -->
        <div class="w-1/3 bg-white border-r border-gray-200 flex flex-col">
            <!-- Header -->
            <div class="p-4 border-b border-gray-200 bg-white sticky top-0 z-10">
                <div class="flex items-center justify-between mb-4">
                    <h1 class="text-xl font-semibold text-gray-800">Сообщения</h1>
                    <div class="flex items-center space-x-2">
                        <!-- Action Button -->
                        <#if currentUser?? && currentUser.role??>
                            <#if currentUser.role == "ADMIN">
                                <button id="newChatBtn"
                                        class="p-2 text-blue-600 hover:bg-blue-50 rounded-lg transition-colors"
                                        title="Новый чат">
                                    <i class="fas fa-plus"></i>
                                </button>
                            <#else>
                                <button id="writeAdminBtn"
                                        class="p-2 text-blue-600 hover:bg-blue-50 rounded-lg transition-colors"
                                        title="Написать администратору">
                                    <i class="fas fa-edit"></i>
                                </button>
                            </#if>
                        </#if>

                        <!-- Refresh Button -->
                        <button id="refreshBtn"
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
            <#if currentUser?? && currentUser.role?? && currentUser.role == "ADMIN">
                <#if supportRequests?? && supportRequests?has_content>
                    <div class="bg-yellow-50 border-b border-yellow-200 p-4">
                        <h3 class="text-sm font-medium text-yellow-800 mb-2">
                            <i class="fas fa-exclamation-triangle mr-1"></i>
                            Новые запросы поддержки (${supportRequests?size})
                        </h3>
                        <div class="space-y-2" id="supportRequestsList">
                            <#list supportRequests as request>
                                <div class="bg-white rounded-lg p-3 border border-yellow-200">
                                    <div class="flex items-start justify-between">
                                        <div class="flex-1">
                                            <p class="text-sm font-medium text-gray-900">
                                                ${request.senderName!"Неизвестный пользователь"}
                                            </p>
                                            <p class="text-xs text-gray-600">
                                                ${request.senderEmail!""}
                                            </p>
                                            <p class="text-sm text-gray-700 mt-1 message-preview">
                                                ${request.content!""}
                                            </p>
                                        </div>
                                        <button onclick="MessagesApp.replyToSupport(${request.id?c})"
                                                class="ml-2 px-3 py-1 bg-blue-600 text-white text-xs rounded hover:bg-blue-700 transition-colors">
                                            Ответить
                                        </button>
                                    </div>
                                    <span class="text-xs text-gray-500">
                                    ${request.formattedTime!""}
                                </span>
                                </div>
                            </#list>
                        </div>
                    </div>
                </#if>
            </#if>

            <!-- Chats List -->
            <div class="flex-1 overflow-y-auto" id="chatsList">
                <#if !chats?? || !chats?has_content>
                    <div class="p-8 text-center text-gray-500">
                        <i class="fas fa-comments text-4xl mb-4 text-gray-300"></i>
                        <p class="text-lg mb-2">Нет активных чатов</p>
                        <p class="text-sm">
                            <#if currentUser?? && currentUser.role?? && currentUser.role == "ADMIN">
                                Ожидайте обращений от пользователей или создайте новый чат
                            <#else>
                                Нажмите кнопку "Написать администратору" чтобы начать общение
                            </#if>
                        </p>
                    </div>
                <#else>
                    <#list chats as chat>
                        <#assign isUnread = false>
                        <#if chat.hasUnreadMessages?? && chat.hasUnreadMessages>
                            <#assign isUnread = true>
                        </#if>

                        <div class="chat-item cursor-pointer p-4 border-b border-gray-100 <#if isUnread>unread</#if>"
                             onclick="MessagesApp.openChat('${chat.chatId!""}')"
                             data-chat-id="${chat.chatId!""}">
                            <div class="flex items-start space-x-3">
                                <!-- Avatar -->
                                <div class="relative flex-shrink-0">
                                    <div class="w-12 h-12 bg-gradient-to-br from-blue-500 to-purple-600 rounded-full flex items-center justify-center text-white font-semibold">
                                        <#assign userRole = chat.otherUserRole!"USER">
                                        <#if userRole == "ADMIN">
                                            <i class="fas fa-user-shield"></i>
                                        <#elseif userRole == "COSMETOLOGIST">
                                            <i class="fas fa-spa"></i>
                                        <#else>
                                            <i class="fas fa-user"></i>
                                        </#if>
                                    </div>
                                    <#if isUnread && chat.unreadCount?? && chat.unreadCount gt 0>
                                        <div class="absolute -top-1 -right-1 w-5 h-5 bg-red-500 rounded-full flex items-center justify-center">
                                            <span class="text-xs text-white font-bold">${chat.unreadCount?c}</span>
                                        </div>
                                    </#if>
                                </div>

                                <!-- Chat Info -->
                                <div class="flex-1 min-w-0">
                                    <div class="flex items-center justify-between mb-1">
                                        <h3 class="text-sm font-semibold text-gray-900 truncate">
                                            ${chat.otherUserName!"Неизвестный пользователь"}
                                            <#if userRole == "COSMETOLOGIST">
                                                <#assign isVerified = true>
                                                <#if chat.otherUserVerified?? && !chat.otherUserVerified>
                                                    <#assign isVerified = false>
                                                </#if>
                                                <#if !isVerified>
                                                    <span class="ml-1 text-xs text-yellow-600">(не верифицирован)</span>
                                                </#if>
                                            </#if>
                                        </h3>
                                        <span class="text-xs text-gray-500">
                                        ${chat.lastMessageTime!""}
                                    </span>
                                    </div>

                                    <div class="flex items-center text-xs text-gray-600 mb-2">
                                        <#assign chatIcon = "fas fa-user">
                                        <#if userRole == "ADMIN">
                                            <#assign chatIcon = "fas fa-user-shield">
                                        <#elseif userRole == "COSMETOLOGIST">
                                            <#assign chatIcon = "fas fa-spa">
                                        </#if>
                                        <i class="${chatIcon} mr-1"></i>
                                        <span class="capitalize">${userRole?lower_case}</span>
                                        <span class="mx-1">•</span>
                                        <span>${chat.otherUserEmail!""}</span>
                                    </div>

                                    <p class="text-sm text-gray-700 message-preview">
                                        <#if chat.lastMessageFromMe?? && chat.lastMessageFromMe>
                                            <span class="text-blue-600">Вы: </span>
                                        </#if>
                                        ${chat.lastMessage!""}
                                    </p>

                                    <#if chat.isSystemChat()?? && chat.isSystemChat()>
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
                            <#if currentUser?? && currentUser.role?? && currentUser.role == "COSMETOLOGIST">
                                Обратиться в службу поддержки
                            <#else>
                                Написать администратору
                            </#if>
                        </h3>
                        <button id="closeWriteAdminModal" class="text-gray-400 hover:text-gray-600">
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
                            <div id="writeAdminError" class="text-red-600 text-sm mt-1 hidden"></div>
                        </div>

                        <div class="flex items-center justify-end space-x-3">
                            <button type="button"
                                    id="cancelWriteAdmin"
                                    class="px-4 py-2 text-gray-700 border border-gray-300 rounded-lg hover:bg-gray-50 transition-colors">
                                Отмена
                            </button>
                            <button type="submit"
                                    id="submitWriteAdmin"
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
                        <button id="closeReplySupportModal" class="text-gray-400 hover:text-gray-600">
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
                            <div id="replySupportError" class="text-red-600 text-sm mt-1 hidden"></div>
                        </div>

                        <div class="flex items-center justify-end space-x-3">
                            <button type="button"
                                    id="cancelReplySupport"
                                    class="px-4 py-2 text-gray-700 border border-gray-300 rounded-lg hover:bg-gray-50 transition-colors">
                                Отмена
                            </button>
                            <button type="submit"
                                    id="submitReplySupport"
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

    <!-- New Chat Modal (for admins) -->
    <div id="newChatModal" class="fixed inset-0 bg-gray-600 bg-opacity-50 hidden z-50">
        <div class="flex items-center justify-center min-h-screen p-4">
            <div class="bg-white rounded-lg shadow-xl max-w-md w-full">
                <div class="p-6">
                    <div class="flex items-center justify-between mb-4">
                        <h3 class="text-lg font-semibold text-gray-900">Новый чат</h3>
                        <button id="closeNewChatModal" class="text-gray-400 hover:text-gray-600">
                            <i class="fas fa-times"></i>
                        </button>
                    </div>

                    <form id="newChatForm">
                        <div class="mb-4">
                            <label for="userSelect" class="block text-sm font-medium text-gray-700 mb-2">Выберите пользователя</label>
                            <select id="userSelect"
                                    class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                                    required>
                                <option value="">Выберите пользователя...</option>
                            </select>
                            <div id="userSelectError" class="text-red-600 text-sm mt-1 hidden"></div>
                        </div>

                        <div class="mb-4">
                            <label for="initialMessage" class="block text-sm font-medium text-gray-700 mb-2">Сообщение</label>
                            <textarea id="initialMessage"
                                      class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent resize-none"
                                      rows="4"
                                      placeholder="Введите сообщение..."
                                      required></textarea>
                            <div id="newChatError" class="text-red-600 text-sm mt-1 hidden"></div>
                        </div>

                        <div class="flex items-center justify-end space-x-3">
                            <button type="button"
                                    id="cancelNewChat"
                                    class="px-4 py-2 text-gray-700 border border-gray-300 rounded-lg hover:bg-gray-50 transition-colors">
                                Отмена
                            </button>
                            <button type="submit"
                                    id="submitNewChat"
                                    class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors">
                                <i class="fas fa-paper-plane mr-2"></i>
                                Начать чат
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>

    <style>
        .chat-item:hover {
            background-color: #f8fafc;
        }
        .chat-item.unread {
            border-left: 4px solid #3b82f6;
            background-color: #eff6ff;
        }
        .chat-item.active {
            background-color: #e0f2fe;
            border-left: 4px solid #0284c7;
        }
        .message-preview {
            display: -webkit-box;
            -webkit-line-clamp: 2;
            -webkit-box-orient: vertical;
            overflow: hidden;
        }
        .fade-in {
            animation: fadeIn 0.3s ease-in;
        }
        .fade-out {
            animation: fadeOut 0.3s ease-out;
        }

        @keyframes fadeIn {
            from { opacity: 0; transform: translateY(10px); }
            to { opacity: 1; transform: translateY(0); }
        }

        @keyframes fadeOut {
            from { opacity: 1; }
            to { opacity: 0; }
        }

        .loading {
            pointer-events: none;
            opacity: 0.6;
        }
    </style>

    <script>
        // Основной объект приложения
        const MessagesApp = {
            // Конфигурация
            config: {
                currentUser: {
                    <#if currentUser??>
                    id: ${currentUser.id?c},
                    role: '${currentUser.role!"USER"}',
                    name: '${currentUser.firstName!""} ${currentUser.lastName!""}'
                    <#else>
                    id: null,
                    role: 'GUEST',
                    name: 'Гость'
                    </#if>
                },
                supportRequests: [
                    <#if supportRequests?? && supportRequests?has_content>
                    <#list supportRequests as request>
                    {
                        id: ${request.id?c},
                        senderName: '${(request.senderName?js_string)!"Неизвестный пользователь"}',
                        senderEmail: '${(request.senderEmail?js_string)!""}',
                        content: '${(request.content?js_string)!""}',
                        formattedTime: '${(request.formattedTime?js_string)!""}'
                    }<#if request_has_next>,</#if>
                    </#list>
                    </#if>
                ]
            },

            // Вспомогательные функции
            utils: {
                escapeHtml: function(unsafe) {
                    if (unsafe === undefined || unsafe === null) return '';
                    return String(unsafe)
                        .replace(/&/g, "&amp;")
                        .replace(/</g, "&lt;")
                        .replace(/>/g, "&gt;")
                        .replace(/"/g, "&quot;")
                        .replace(/'/g, "&#039;");
                },

                getCsrfData: function() {
                    const csrfToken = document.querySelector('meta[name="_csrf"]')?.getAttribute('content');
                    const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.getAttribute('content');
                    return { csrfToken: csrfToken || '', csrfHeader: csrfHeader || '' };
                },

                getElement: function(id) {
                    const element = document.getElementById(id);
                    if (!element) {
                        console.warn('Элемент с ID "' + id + '" не найден');
                    }
                    return element;
                },

                showError: function(elementId, message) {
                    const element = this.getElement(elementId);
                    if (element) {
                        element.textContent = message;
                        element.classList.remove('hidden');
                    }
                },

                hideError: function(elementId) {
                    const element = this.getElement(elementId);
                    if (element) {
                        element.classList.add('hidden');
                    }
                },

                setLoading: function(element, loading) {
                    if (!element) return;
                    if (loading) {
                        element.classList.add('loading');
                        element.disabled = true;
                    } else {
                        element.classList.remove('loading');
                        element.disabled = false;
                    }
                }
            },

            // API функции
            api: {
                makeRequest: async function(url, options) {
                    options = options || {};
                    const utils = MessagesApp.utils;
                    const csrfData = utils.getCsrfData();

                    const defaultOptions = {
                        method: 'GET',
                        headers: {
                            'Content-Type': 'application/x-www-form-urlencoded'
                        }
                    };

                    if (csrfData.csrfToken && csrfData.csrfHeader) {
                        defaultOptions.headers[csrfData.csrfHeader] = csrfData.csrfToken;
                    }

                    const mergedOptions = Object.assign({}, defaultOptions, options);
                    if (options.headers) {
                        mergedOptions.headers = Object.assign({}, defaultOptions.headers, options.headers);
                    }

                    const response = await fetch(url, mergedOptions);

                    if (!response.ok) {
                        throw new Error('HTTP ' + response.status + ': ' + response.statusText);
                    }

                    const contentType = response.headers.get('content-type');
                    if (contentType && contentType.includes('application/json')) {
                        return await response.json();
                    }

                    return await response.text();
                },

                sendSupportRequest: async function(content) {
                    return await this.makeRequest('/messages/support', {
                        method: 'POST',
                        body: 'content=' + encodeURIComponent(content)
                    });
                },

                replySupportRequest: async function(originalMessageId, content) {
                    return await this.makeRequest('/messages/support/reply', {
                        method: 'POST',
                        body: 'originalMessageId=' + originalMessageId + '&content=' + encodeURIComponent(content)
                    });
                },

                sendMessage: async function(recipientId, content) {
                    return await this.makeRequest('/messages/send', {
                        method: 'POST',
                        body: 'recipientId=' + recipientId + '&content=' + encodeURIComponent(content)
                    });
                },

                getUsers: async function() {
                    return await this.makeRequest('/admin/users/list');
                },

                markChatAsRead: async function(chatId) {
                    return await this.makeRequest('/messages/api/chat/' + chatId + '/read', {
                        method: 'POST'
                    });
                },

                getUnreadCount: async function() {
                    return await this.makeRequest('/messages/api/unread-count');
                }
            },

            // UI функции
            ui: {
                showNotification: function(message, type) {
                    type = type || 'info';
                    if (!message) return;

                    // Удаляем существующие уведомления
                    const existing = document.querySelectorAll('.notification-toast');
                    existing.forEach(function(el) { el.remove(); });

                    const notification = document.createElement('div');
                    notification.className = 'notification-toast fixed top-4 right-4 z-50 px-6 py-3 rounded-lg shadow-lg text-white fade-in';

                    if (type === 'success') {
                        notification.classList.add('bg-green-500');
                    } else if (type === 'error') {
                        notification.classList.add('bg-red-500');
                    } else if (type === 'warning') {
                        notification.classList.add('bg-yellow-500');
                    } else {
                        notification.classList.add('bg-blue-500');
                    }

                    notification.textContent = message;
                    document.body.appendChild(notification);

                    setTimeout(function() {
                        notification.classList.add('fade-out');
                        setTimeout(function() {
                            if (notification.parentNode) {
                                notification.remove();
                            }
                        }, 300);
                    }, 3000);
                },

                openModal: function(modalId) {
                    const modal = MessagesApp.utils.getElement(modalId);
                    if (modal) {
                        modal.classList.remove('hidden');
                    }
                },

                closeModal: function(modalId) {
                    const modal = MessagesApp.utils.getElement(modalId);
                    if (modal) {
                        modal.classList.add('hidden');
                    }
                }
            },

            // Основные функции
            openChat: function(chatId) {
                if (!chatId) {
                    console.error('Chat ID не указан');
                    return;
                }

                try {
                    // Отмечаем выбранный чат
                    const chatItems = document.querySelectorAll('.chat-item');
                    for (var i = 0; i < chatItems.length; i++) {
                        chatItems[i].classList.remove('active');
                    }

                    const selectedChat = document.querySelector('[data-chat-id="' + chatId + '"]');
                    if (selectedChat) {
                        selectedChat.classList.add('active');
                        selectedChat.classList.remove('unread');

                        const unreadBadge = selectedChat.querySelector('.absolute');
                        if (unreadBadge) {
                            unreadBadge.style.display = 'none';
                        }
                    }

                    // Отмечаем чат как прочитанный
                    this.markChatAsRead(chatId);

                    // Переходим на страницу чата
                    window.location.href = '/messages/chat/' + chatId;
                } catch (error) {
                    console.error('Ошибка при открытии чата:', error);
                    this.ui.showNotification('Ошибка при открытии чата', 'error');
                }
            },

            markChatAsRead: function(chatId) {
                var self = this;
                this.api.markChatAsRead(chatId)
                    .then(function() {
                        return self.updateUnreadCounts();
                    })
                    .catch(function(error) {
                        console.error('Ошибка при отметке чата как прочитанного:', error);
                    });
            },

            updateUnreadCounts: function() {
                return this.api.getUnreadCount()
                    .then(function(data) {
                        const badges = document.querySelectorAll('.unread-badge');
                        for (var i = 0; i < badges.length; i++) {
                            const badge = badges[i];
                            if (data.count > 0) {
                                badge.textContent = data.count;
                                badge.classList.remove('hidden');
                            } else {
                                badge.classList.add('hidden');
                            }
                        }
                    })
                    .catch(function(error) {
                        console.error('Ошибка при обновлении счетчиков:', error);
                    });
            },

            replyToSupport: function(messageId) {
                try {
                    if (!messageId) {
                        throw new Error('ID сообщения не указан');
                    }

                    const request = this.config.supportRequests.find(function(r) {
                        return r.id === messageId;
                    });

                    if (!request) {
                        throw new Error('Запрос не найден');
                    }

                    const infoContainer = this.utils.getElement('originalRequestInfo');
                    if (!infoContainer) return;

                    infoContainer.innerHTML =
                        '<div class="text-sm">' +
                        '<p class="font-medium text-gray-900">' + this.utils.escapeHtml(request.senderName) + '</p>' +
                        '<p class="text-gray-600">' + this.utils.escapeHtml(request.senderEmail) + '</p>' +
                        '<p class="mt-2 text-gray-700">' + this.utils.escapeHtml(request.content) + '</p>' +
                        '<p class="mt-1 text-xs text-gray-500">' + this.utils.escapeHtml(request.formattedTime) + '</p>' +
                        '</div>';

                    const idField = this.utils.getElement('originalMessageId');
                    if (idField) {
                        idField.value = messageId;
                    }

                    this.ui.openModal('replySupportModal');

                    const messageField = this.utils.getElement('replyMessage');
                    if (messageField) {
                        messageField.focus();
                    }
                } catch (error) {
                    console.error('Ошибка при открытии модального окна ответа:', error);
                    this.ui.showNotification('Ошибка: ' + error.message, 'error');
                }
            },

            refreshChats: function() {
                try {
                    window.location.reload();
                } catch (error) {
                    console.error('Ошибка при обновлении чатов:', error);
                    this.ui.showNotification('Ошибка при обновлении чатов', 'error');
                }
            },

            // Инициализация
            init: function() {
                try {
                    this.setupEventListeners();
                    this.setupSearch();
                    this.updateUnreadCounts();

                    // Автообновление каждые 30 секунд
                    var self = this;
                    setInterval(function() {
                        self.updateUnreadCounts();
                    }, 30000);

                    console.log('MessagesApp инициализирован успешно');
                } catch (error) {
                    console.error('Ошибка при инициализации MessagesApp:', error);
                }
            },

            setupEventListeners: function() {
                var self = this;

                // Кнопки для открытия модальных окон
                const writeAdminBtn = this.utils.getElement('writeAdminBtn');
                if (writeAdminBtn) {
                    writeAdminBtn.addEventListener('click', function() {
                        self.ui.openModal('writeAdminModal');
                        const messageField = self.utils.getElement('adminMessage');
                        if (messageField) messageField.focus();
                    });
                }

                const newChatBtn = this.utils.getElement('newChatBtn');
                if (newChatBtn) {
                    newChatBtn.addEventListener('click', function() {
                        self.openNewChatModal();
                    });
                }

                const refreshBtn = this.utils.getElement('refreshBtn');
                if (refreshBtn) {
                    refreshBtn.addEventListener('click', function() {
                        self.refreshChats();
                    });
                }

                // Кнопки для закрытия модальных окон
                const closeButtons = [
                    'closeWriteAdminModal',
                    'closeReplySupportModal',
                    'closeNewChatModal',
                    'cancelWriteAdmin',
                    'cancelReplySupport',
                    'cancelNewChat'
                ];

                for (var i = 0; i < closeButtons.length; i++) {
                    const btnId = closeButtons[i];
                    const btn = this.utils.getElement(btnId);
                    if (btn) {
                        btn.addEventListener('click', function() {
                            if (btnId.indexOf('WriteAdmin') !== -1) {
                                self.closeWriteAdminModal();
                            } else if (btnId.indexOf('ReplySupport') !== -1) {
                                self.closeReplySupportModal();
                            } else if (btnId.indexOf('NewChat') !== -1) {
                                self.closeNewChatModal();
                            }
                        });
                    }
                }

                // Формы
                this.setupFormHandlers();

                // Закрытие модальных окон по клику вне области
                this.setupModalClosing();
            },

            setupFormHandlers: function() {
                var self = this;

                // Форма написания админу
                const writeAdminForm = this.utils.getElement('writeAdminForm');
                if (writeAdminForm) {
                    writeAdminForm.addEventListener('submit', function(e) {
                        e.preventDefault();
                        self.handleWriteAdminSubmit();
                    });
                }

                // Форма ответа на поддержку
                const replySupportForm = this.utils.getElement('replySupportForm');
                if (replySupportForm) {
                    replySupportForm.addEventListener('submit', function(e) {
                        e.preventDefault();
                        self.handleReplySupportSubmit();
                    });
                }

                // Форма нового чата
                const newChatForm = this.utils.getElement('newChatForm');
                if (newChatForm) {
                    newChatForm.addEventListener('submit', function(e) {
                        e.preventDefault();
                        self.handleNewChatSubmit();
                    });
                }
            },

            setupSearch: function() {
                const chatSearch = this.utils.getElement('chatSearch');
                if (chatSearch) {
                    chatSearch.addEventListener('input', function(e) {
                        const query = e.target.value.toLowerCase();
                        const chatItems = document.querySelectorAll('.chat-item');

                        for (var i = 0; i < chatItems.length; i++) {
                            const item = chatItems[i];
                            const userName = (item.querySelector('h3') && item.querySelector('h3').textContent) ? item.querySelector('h3').textContent.toLowerCase() : '';
                            const userEmail = (item.querySelector('.text-xs') && item.querySelector('.text-xs').textContent) ? item.querySelector('.text-xs').textContent.toLowerCase() : '';
                            const lastMessage = (item.querySelector('.message-preview') && item.querySelector('.message-preview').textContent) ? item.querySelector('.message-preview').textContent.toLowerCase() : '';

                            if (userName.indexOf(query) !== -1 || userEmail.indexOf(query) !== -1 || lastMessage.indexOf(query) !== -1) {
                                item.style.display = 'block';
                            } else {
                                item.style.display = 'none';
                            }
                        }
                    });
                }
            },

            setupModalClosing: function() {
                var self = this;
                const modals = ['writeAdminModal', 'replySupportModal', 'newChatModal'];

                for (var i = 0; i < modals.length; i++) {
                    const modalId = modals[i];
                    const modal = this.utils.getElement(modalId);
                    if (modal) {
                        modal.addEventListener('click', function(e) {
                            if (e.target === modal) {
                                self.ui.closeModal(modalId);
                            }
                        });
                    }
                }
            },

            // Обработчики форм
            handleWriteAdminSubmit: function() {
                var self = this;
                const messageField = this.utils.getElement('adminMessage');
                const submitBtn = this.utils.getElement('submitWriteAdmin');

                if (!messageField) return;

                const content = messageField.value.trim();
                if (!content) {
                    this.utils.showError('writeAdminError', 'Пожалуйста, введите сообщение');
                    return;
                }

                this.utils.hideError('writeAdminError');
                this.utils.setLoading(submitBtn, true);

                if (submitBtn) {
                    submitBtn.innerHTML = '<i class="fas fa-spinner fa-spin mr-2"></i> Отправка...';
                }

                this.api.sendSupportRequest(content)
                    .then(function(result) {
                        if (result.success) {
                            self.closeWriteAdminModal();
                            self.ui.showNotification('Ваш запрос отправлен администраторам', 'success');
                            setTimeout(function() {
                                window.location.reload();
                            }, 1000);
                        } else {
                            throw new Error(result.error || 'Неизвестная ошибка');
                        }
                    })
                    .catch(function(error) {
                        console.error('Ошибка при отправке сообщения админу:', error);
                        self.utils.showError('writeAdminError', error.message || 'Ошибка отправки сообщения');
                    })
                    .finally(function() {
                        self.utils.setLoading(submitBtn, false);
                        if (submitBtn) {
                            submitBtn.innerHTML = '<i class="fas fa-paper-plane mr-2"></i> Отправить';
                        }
                    });
            },

            handleReplySupportSubmit: function() {
                var self = this;
                const idField = this.utils.getElement('originalMessageId');
                const messageField = this.utils.getElement('replyMessage');
                const submitBtn = this.utils.getElement('submitReplySupport');

                if (!idField || !messageField) return;

                const originalMessageId = idField.value;
                const content = messageField.value.trim();

                if (!originalMessageId || !content) {
                    this.utils.showError('replySupportError', 'Необходимо заполнить все поля');
                    return;
                }

                this.utils.hideError('replySupportError');
                this.utils.setLoading(submitBtn, true);

                if (submitBtn) {
                    submitBtn.innerHTML = '<i class="fas fa-spinner fa-spin mr-2"></i> Отправка...';
                }

                this.api.replySupportRequest(originalMessageId, content)
                    .then(function(result) {
                        if (result.success) {
                            self.closeReplySupportModal();
                            self.ui.showNotification('Ответ отправлен', 'success');

                            if (result.chatId) {
                                setTimeout(function() {
                                    self.openChat(result.chatId);
                                }, 1000);
                            } else {
                                setTimeout(function() {
                                    window.location.reload();
                                }, 1000);
                            }
                        } else {
                            throw new Error(result.error || 'Неизвестная ошибка');
                        }
                    })
                    .catch(function(error) {
                        console.error('Ошибка при отправке ответа:', error);
                        self.utils.showError('replySupportError', error.message || 'Ошибка отправки ответа');
                    })
                    .finally(function() {
                        self.utils.setLoading(submitBtn, false);
                        if (submitBtn) {
                            submitBtn.innerHTML = '<i class="fas fa-reply mr-2"></i> Ответить';
                        }
                    });
            },

            handleNewChatSubmit: function() {
                var self = this;
                const userSelect = this.utils.getElement('userSelect');
                const messageField = this.utils.getElement('initialMessage');
                const submitBtn = this.utils.getElement('submitNewChat');

                if (!userSelect || !messageField) return;

                const userId = userSelect.value;
                const content = messageField.value.trim();

                if (!userId) {
                    this.utils.showError('userSelectError', 'Выберите пользователя');
                    return;
                }

                if (!content) {
                    this.utils.showError('newChatError', 'Введите сообщение');
                    return;
                }

                this.utils.hideError('userSelectError');
                this.utils.hideError('newChatError');
                this.utils.setLoading(submitBtn, true);

                if (submitBtn) {
                    submitBtn.innerHTML = '<i class="fas fa-spinner fa-spin mr-2"></i> Отправка...';
                }

                this.api.sendMessage(userId, content)
                    .then(function(result) {
                        if (result.success) {
                            self.closeNewChatModal();
                            self.ui.showNotification('Сообщение отправлено', 'success');

                            if (result.chatId) {
                                setTimeout(function() {
                                    window.location.href = '/messages/chat/' + result.chatId;
                                }, 500);
                            } else {
                                setTimeout(function() {
                                    window.location.reload();
                                }, 500);
                            }
                        } else {
                            throw new Error(result.error || 'Неизвестная ошибка');
                        }
                    })
                    .catch(function(error) {
                        console.error('Ошибка при отправке сообщения:', error);
                        self.utils.showError('newChatError', error.message || 'Ошибка отправки сообщения');
                    })
                    .finally(function() {
                        self.utils.setLoading(submitBtn, false);
                        if (submitBtn) {
                            submitBtn.innerHTML = '<i class="fas fa-paper-plane mr-2"></i> Начать чат';
                        }
                    });
            },

            // Модальные окна
            openNewChatModal: function() {
                var self = this;
                this.ui.openModal('newChatModal');

                this.api.getUsers()
                    .then(function(users) {
                        const userSelect = self.utils.getElement('userSelect');

                        if (!userSelect) return;

                        userSelect.innerHTML = '<option value="">Выберите пользователя...</option>';

                        if (Array.isArray(users)) {
                            for (var i = 0; i < users.length; i++) {
                                const user = users[i];
                                if (user.id !== self.config.currentUser.id) {
                                    const option = document.createElement('option');
                                    option.value = user.id;
                                    option.textContent = (user.firstName || '') + ' ' + (user.lastName || '') + ' (' + (user.email || '') + ') - ' + (user.role || '');
                                    userSelect.appendChild(option);
                                }
                            }
                        }

                        userSelect.focus();
                    })
                    .catch(function(error) {
                        console.error('Ошибка при загрузке пользователей:', error);
                        self.utils.showError('userSelectError', 'Ошибка загрузки списка пользователей');
                    });
            },

            closeWriteAdminModal: function() {
                this.ui.closeModal('writeAdminModal');
                const form = this.utils.getElement('writeAdminForm');
                if (form) {
                    form.reset();
                }
                this.utils.hideError('writeAdminError');
            },

            closeReplySupportModal: function() {
                this.ui.closeModal('replySupportModal');
                const form = this.utils.getElement('replySupportForm');
                if (form) {
                    form.reset();
                }
                this.utils.hideError('replySupportError');
            },

            closeNewChatModal: function() {
                this.ui.closeModal('newChatModal');
                const form = this.utils.getElement('newChatForm');
                if (form) {
                    form.reset();
                }
                this.utils.hideError('userSelectError');
                this.utils.hideError('newChatError');
            }
        };

        // Инициализация при загрузке страницы
        document.addEventListener('DOMContentLoaded', function() {
            MessagesApp.init();
        });

        // Глобальная доступность для обратной совместимости
        window.MessagesApp = MessagesApp;
    </script>
</@layout>