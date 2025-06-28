<#assign pageTitle = "Arannati - Чат">
<#assign pageDescription = "Чат с пользователем">
<#include "../layouts/main.ftlh">

<@layout>
<!-- Chat Header -->
<div class="bg-white border-b border-gray-200 p-4 flex items-center justify-between">
    <div class="flex items-center space-x-3">
        <button onclick="goBackToChats()" class="lg:hidden p-2 text-gray-600 hover:bg-gray-50 rounded-lg">
            <i class="fas fa-arrow-left"></i>
        </button>

        <div class="flex items-center space-x-3">
            <!-- Other user avatar -->
            <div class="w-10 h-10 bg-gradient-to-br from-blue-500 to-purple-600 rounded-full flex items-center justify-center text-white font-semibold">
                <#assign otherUser = messages[0]>
                <#if messages?size gt 0>
                    <#assign firstMessage = messages[0]>
                    <#assign otherUserRole = "">
                    <#if firstMessage.senderId == currentUser.id>
                        <#assign otherUserRole = firstMessage.recipientRole>
                    <#else>
                        <#assign otherUserRole = firstMessage.senderRole>
                    </#if>

                    <#if otherUserRole == "ADMIN">
                        <i class="fas fa-user-shield"></i>
                    <#elseif otherUserRole == "COSMETOLOGIST">
                        <i class="fas fa-spa"></i>
                    <#else>
                        <i class="fas fa-user"></i>
                    </#if>
                <#else>
                    <i class="fas fa-user"></i>
                </#if>
            </div>

            <div>
                <#if messages?size gt 0>
                    <#assign firstMessage = messages[0]>
                    <#assign otherUserName = "">
                    <#assign otherUserEmail = "">
                    <#assign otherUserRole = "">

                    <#if firstMessage.senderId == currentUser.id>
                        <#assign otherUserName = firstMessage.recipientName>
                        <#assign otherUserEmail = firstMessage.recipientEmail>
                        <#assign otherUserRole = firstMessage.recipientRole>
                    <#else>
                        <#assign otherUserName = firstMessage.senderName>
                        <#assign otherUserEmail = firstMessage.senderEmail>
                        <#assign otherUserRole = firstMessage.senderRole>
                    </#if>

                    <h2 class="font-semibold text-gray-900">${otherUserName}</h2>
                    <div class="flex items-center text-sm text-gray-600">
                        <#if otherUserRole == "ADMIN">
                            <i class="fas fa-user-shield mr-1 text-red-500"></i>
                            <span>Администратор</span>
                        <#elseif otherUserRole == "COSMETOLOGIST">
                            <i class="fas fa-spa mr-1 text-purple-500"></i>
                            <span>Косметолог</span>
                        <#else>
                            <i class="fas fa-user mr-1 text-blue-500"></i>
                            <span>Пользователь</span>
                        </#if>
                        <span class="mx-1">•</span>
                        <span>${otherUserEmail}</span>
                    </div>
                <#else>
                    <h2 class="font-semibold text-gray-900">Чат</h2>
                    <p class="text-sm text-gray-600">Пустой чат</p>
                </#if>
            </div>
        </div>
    </div>

    <div class="flex items-center space-x-2 relative">
        <!-- Chat options -->
        <button id="chatOptionsButton" class="p-2 text-gray-600 hover:bg-gray-50 rounded-lg" title="Настройки чата" onclick="toggleChatOptions(event)">
            <i class="fas fa-cog"></i>
        </button>

        <!-- Dropdown menu for chat options -->
        <div id="chatOptionsMenu" class="absolute right-0 top-full mt-2 w-48 bg-white rounded-md shadow-lg py-1 z-50 hidden">
            <a href="#" class="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100" onclick="event.stopPropagation(); clearChat(); return false;">
                <i class="fas fa-trash-alt mr-2"></i> Очистить чат
            </a>
            <a href="#" class="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100" onclick="event.stopPropagation(); blockUser(); return false;">
                <i class="fas fa-ban mr-2"></i> Заблокировать пользователя
            </a>
            <a href="#" class="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100" onclick="event.stopPropagation(); reportUser(); return false;">
                <i class="fas fa-flag mr-2"></i> Пожаловаться
            </a>
        </div>
    </div>
</div>

<!-- Messages Container -->
<div class="flex-1 overflow-y-auto p-4 space-y-4" id="messagesContainer">
    <#if messages?size == 0>
        <div class="text-center text-gray-500 py-8">
            <i class="fas fa-comment-alt text-4xl mb-4 text-gray-300"></i>
            <p class="text-lg">Начните общение</p>
            <p class="text-sm">Отправьте первое сообщение чтобы начать диалог</p>
        </div>
    <#else>
        <#list messages as message>
            <#assign messageClass = "justify-start">
            <#if message.senderId == currentUser.id>
                <#assign messageClass = "justify-end">
            </#if>
            <div class="flex ${messageClass}">
                <div class="max-w-xs lg:max-w-md">
                    <!-- Message bubble -->
                    <#assign messageBubbleClass = "bg-white border border-gray-200">
                    <#if message.senderId == currentUser.id>
                        <#assign messageBubbleClass = "bg-blue-500 text-white">
                    </#if>
                    <div class="relative ${messageBubbleClass} rounded-lg px-4 py-2 shadow-sm">
                        <#if message.isSystemMessage()>
                            <!-- System message styling -->
                            <div class="bg-yellow-50 border-l-4 border-yellow-400 p-3 rounded">
                                <div class="flex items-start">
                                    <i class="fas fa-info-circle text-yellow-500 mt-0.5 mr-2"></i>
                                    <div class="text-sm text-yellow-800">
                                        <p class="font-medium mb-1">Системное сообщение</p>
                                        <div class="whitespace-pre-wrap">${message.content}</div>
                                    </div>
                                </div>
                            </div>
                        <#else>
                            <!-- Regular message -->
                            <div class="whitespace-pre-wrap break-words">${message.content}</div>
                        </#if>

                        <!-- Message footer -->
                        <#assign footerTextClass = "text-gray-500">
                        <#if message.senderId == currentUser.id>
                            <#assign footerTextClass = "text-blue-100">
                        </#if>
                        <div class="flex items-center justify-between mt-1 text-xs ${footerTextClass}">
                            <span>${message.formattedTime}</span>
                            <#if message.senderId == currentUser.id>
                                <div class="flex items-center ml-2">
                                    <#if message.isRead()>
                                        <i class="fas fa-check-double text-blue-200"></i>
                                    <#else>
                                        <i class="fas fa-check text-blue-200"></i>
                                    </#if>
                                </div>
                            </#if>
                        </div>
                    </div>

                    <!-- Sender name for received messages -->
                    <#if message.senderId != currentUser.id>
                        <div class="text-xs text-gray-500 mt-1 px-2">
                            ${message.senderName}
                        </div>
                    </#if>
                </div>
            </div>
        </#list>
    </#if>
</div>

<!-- Message Input -->
<div class="bg-white border-t border-gray-200 p-4">
    <form id="messageForm" class="flex items-end space-x-3">
        <div class="flex-1">
            <textarea id="messageInput"
                      class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent resize-none"
                      rows="1"
                      placeholder="Введите сообщение..."
                      maxlength="5000"></textarea>
            <div class="flex items-center justify-between mt-1 px-1">
                <span class="text-xs text-gray-500">
                    <span id="charCount">0</span>/5000
                </span>
                <div class="flex items-center space-x-2 text-xs text-gray-500">
                    <span>Enter - отправить</span>
                    <span>Shift+Enter - новая строка</span>
                </div>
            </div>
        </div>

        <button type="submit"
                class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
                id="sendButton">
            <i class="fas fa-paper-plane"></i>
        </button>
    </form>
</div>

<script>
    const chatId = '${chatId}';
    const currentUserId = ${currentUser.id};

    // Элементы
    const messageInput = document.getElementById('messageInput');
    const messageForm = document.getElementById('messageForm');
    const messagesContainer = document.getElementById('messagesContainer');
    const sendButton = document.getElementById('sendButton');
    const charCount = document.getElementById('charCount');

    // Автоматическое изменение размера textarea
    messageInput.addEventListener('input', function() {
        this.style.height = 'auto';
        this.style.height = Math.min(this.scrollHeight, 120) + 'px';

        // Обновляем счетчик символов
        charCount.textContent = this.value.length;

        // Включаем/отключаем кнопку отправки
        sendButton.disabled = this.value.trim().length === 0;
    });

    // Обработка нажатий клавиш
    messageInput.addEventListener('keydown', function(e) {
        if (e.key === 'Enter' && !e.shiftKey) {
            e.preventDefault();
            messageForm.requestSubmit();
        }
    });

    // Отправка сообщения
    messageForm.addEventListener('submit', async function(e) {
        e.preventDefault();

        const content = messageInput.value.trim();
        if (!content) return;

        // Отключаем форму во время отправки
        sendButton.disabled = true;
        messageInput.disabled = true;

        // Показываем индикатор отправки
        sendButton.innerHTML = '<i class="fas fa-spinner fa-spin"></i>';

        try {
            // Получаем CSRF токен из мета-тегов
            const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
            const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

            console.log('CSRF Token:', csrfToken);
            console.log('CSRF Header:', csrfHeader);

            // Определяем получателя
            let recipientId;
            <#if messages?size gt 0>
                <#assign firstMessage = messages[0]>
                <#if firstMessage.senderId == currentUser.id>
                    <#assign recipientId = firstMessage.recipientId>
                <#else>
                    <#assign recipientId = firstMessage.senderId>
                </#if>
                recipientId = "${recipientId}";
            <#else>
                // Если нет сообщений, пробуем различные способы получения recipientId из URL

                // Сначала проверяем формат /messages/chat/user-{userId}
                let chatIdMatch = window.location.pathname.match(/\/messages\/chat\/user-(\d+)/);
                if (chatIdMatch && chatIdMatch[1]) {
                    recipientId = chatIdMatch[1];
                    console.log('Found recipient ID from user-{userId} format:', recipientId);
                } 
                // Затем проверяем формат /messages/chat/{userId}
                else {
                    const directIdMatch = window.location.pathname.match(/\/messages\/chat\/(\d+)/);
                    if (directIdMatch && directIdMatch[1]) {
                        recipientId = directIdMatch[1];
                        console.log('Found recipient ID from direct {userId} format:', recipientId);
                    } 
                    // Проверяем формат с параметром в URL
                    else {
                        const urlParams = new URLSearchParams(window.location.search);
                        const urlRecipientId = urlParams.get('recipientId');

                        if (urlRecipientId) {
                            recipientId = urlRecipientId;
                            console.log('Found recipient ID from URL parameter:', recipientId);
                        } 
                        // Проверяем другие возможные форматы
                        else {
                            // Пробуем извлечь ID из любого числового значения в пути
                            const anyNumberMatch = window.location.pathname.match(/\/messages\/chat\/([^\/]+)/);
                            if (anyNumberMatch && anyNumberMatch[1]) {
                                const possibleId = anyNumberMatch[1];
                                if (!isNaN(possibleId) || possibleId.match(/^\d+$/)) {
                                    recipientId = possibleId;
                                    console.log('Found recipient ID from path:', recipientId);
                                } else {
                                    console.error('Path segment is not a valid ID:', possibleId);
                                    showNotification('Не удалось определить получателя из URL', 'error');
                                    throw new Error('Invalid recipient ID in path');
                                }
                            } else {
                                console.error('No recipient ID found in URL');
                                showNotification('Не удалось определить получателя', 'error');
                                throw new Error('No recipient found in URL');
                            }
                        }
                    }
                }
            </#if>

            console.log('Sending message to recipient ID:', recipientId);
            console.log('Message content:', content);

            // Формируем тело запроса
            const formData = new FormData();
            formData.append('recipientId', recipientId);
            formData.append('content', content);

            // Преобразуем FormData в строку для application/x-www-form-urlencoded
            const formBody = new URLSearchParams(formData).toString();
            console.log('Request body:', formBody);

            const response = await fetch('/messages/send', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                    [csrfHeader]: csrfToken
                },
                body: formBody
            });
            console.log('Response status:', response.status);

            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }

            const result = await response.json();
            console.log('Response result:', result);

            if (result.success) {
                // Очищаем форму
                messageInput.value = '';
                messageInput.style.height = 'auto';
                charCount.textContent = '0';

                // Добавляем сообщение в чат
                addMessageToChat(result.message, true);

                // Прокручиваем к последнему сообщению
                scrollToBottom();

                // Если это был пустой чат, обновляем страницу для получения полной информации о чате
                if (messagesContainer.querySelectorAll('.flex').length <= 1) {
                    setTimeout(() => {
                        window.location.reload();
                    }, 1000);
                }
            } else {
                showNotification(result.error || 'Ошибка отправки сообщения', 'error');
            }

        } catch (error) {
            console.error('Error sending message:', error);
            showNotification('Ошибка отправки сообщения: ' + error.message, 'error');
        } finally {
            // Включаем форму обратно
            messageInput.disabled = false;
            messageInput.focus();
            sendButton.innerHTML = '<i class="fas fa-paper-plane"></i>';
            sendButton.disabled = messageInput.value.trim().length === 0;
        }
    });

    // Добавление сообщения в чат
    function addMessageToChat(message, isFromCurrentUser) {
        const messageDiv = document.createElement('div');

        // Устанавливаем класс без тернарного оператора
        let flexClass = 'justify-start';
        if (isFromCurrentUser) {
            flexClass = 'justify-end';
        }
        messageDiv.className = 'flex ' + flexClass;

        // Определяем классы для пузыря сообщения
        let bubbleClass = 'bg-white border border-gray-200';
        if (isFromCurrentUser) {
            bubbleClass = 'bg-blue-500 text-white';
        }

        // Определяем класс для текста внизу сообщения
        let textColorClass = 'text-gray-500';
        if (isFromCurrentUser) {
            textColorClass = 'text-blue-100';
        }

        // Формируем HTML для статуса прочтения (без тернарного оператора)
        let readStatusHtml = '';
        if (isFromCurrentUser) {
            readStatusHtml = '<div class="flex items-center ml-2"><i class="fas fa-check text-blue-200"></i></div>';
        }

        // Формируем HTML для имени отправителя (без тернарного оператора)
        let senderNameHtml = '';
        if (!isFromCurrentUser) {
            senderNameHtml = '<div class="text-xs text-gray-500 mt-1 px-2">' + message.senderName + '</div>';
        }

        // Собираем полный HTML
        messageDiv.innerHTML =
            '<div class="max-w-xs lg:max-w-md">' +
            '<div class="relative ' + bubbleClass + ' rounded-lg px-4 py-2 shadow-sm">' +
            '<div class="whitespace-pre-wrap break-words">' + message.content + '</div>' +
            '<div class="flex items-center justify-between mt-1 text-xs ' + textColorClass + '">' +
            '<span>Сейчас</span>' +
            readStatusHtml +
            '</div>' +
            '</div>' +
            senderNameHtml +
            '</div>';

        messagesContainer.appendChild(messageDiv);
    }

    // Прокрутка к последнему сообщению
    function scrollToBottom() {
        messagesContainer.scrollTop = messagesContainer.scrollHeight;
    }

    // Показ уведомлений
    function showNotification(message, type = 'info') {
        const notification = document.createElement('div');

        // Определяем класс цвета без тернарного оператора
        let colorClass = 'bg-blue-500'; // По умолчанию для типа 'info'
        if (type === 'success') {
            colorClass = 'bg-green-500';
        } else if (type === 'error') {
            colorClass = 'bg-red-500';
        }

        notification.className = 'fixed top-4 right-4 z-50 px-6 py-3 rounded-lg shadow-lg text-white ' + colorClass;
        notification.textContent = message;

        document.body.appendChild(notification);

        setTimeout(() => {
            notification.remove();
        }, 3000);
    }

    // Возврат к списку чатов (для мобильных)
    function goBackToChats() {
        window.history.back();
    }

    // Инициализация
    document.addEventListener('DOMContentLoaded', function() {
        messageInput.focus();
        scrollToBottom();

        // Отключаем кнопку отправки если поле пустое
        sendButton.disabled = true;
    });

    // Автообновление чата каждые 10 секунд
    setInterval(async function() {
        try {
            const response = await fetch(`/messages/api/chat/${chatId}`);
            const newMessages = await response.json();

            // Проверяем, есть ли новые сообщения
            const currentMessageCount = messagesContainer.children.length;
            if (newMessages.length > currentMessageCount) {
                // Есть новые сообщения, обновляем чат
                // Здесь можно реализовать более умное обновление
                // Пока просто показываем уведомление
                if (document.hidden) {
                    showNotification('Новое сообщение', 'info');
                }
            }

        } catch (error) {
            console.error('Error checking for new messages:', error);
        }
    }, 10000);

    // Функции для работы с настройками чата

    // Переключение видимости меню настроек
    function toggleChatOptions(event) {
        // Предотвращаем всплытие события, чтобы оно не дошло до document
        if (event) {
            event.stopPropagation();
        }

        console.log('toggleChatOptions called');
        const menu = document.getElementById('chatOptionsMenu');
        const isHidden = menu.classList.contains('hidden');
        console.log('Menu is currently hidden:', isHidden);

        menu.classList.toggle('hidden');
        console.log('Menu visibility toggled. Now hidden:', !isHidden);

        // Если меню открыто, добавляем обработчик для закрытия при клике вне его
        if (!menu.classList.contains('hidden')) {
            // Используем setTimeout, чтобы обработчик добавился после текущего цикла событий
            setTimeout(() => {
                const closeMenuHandler = function(e) {
                    console.log('Document click detected');
                    if (!e.target.closest('#chatOptionsButton') && !e.target.closest('#chatOptionsMenu')) {
                        console.log('Click outside menu detected, hiding menu');
                        menu.classList.add('hidden');
                        document.removeEventListener('click', closeMenuHandler);
                    } else {
                        console.log('Click inside menu or button detected');
                    }
                };

                document.addEventListener('click', closeMenuHandler);
            }, 0);
        }
    }

    // Очистка чата (только визуально)
    function clearChat() {
        console.log('clearChat called');
        if (confirm('Вы уверены, что хотите очистить историю чата?')) {
            // Очищаем контейнер сообщений
            messagesContainer.innerHTML = `
                <div class="text-center text-gray-500 py-8">
                    <i class="fas fa-comment-alt text-4xl mb-4 text-gray-300"></i>
                    <p class="text-lg">Чат очищен</p>
                    <p class="text-sm">Отправьте сообщение чтобы начать диалог</p>
                </div>
            `;
            showNotification('Чат очищен', 'success');
        }
        // Закрываем меню
        const menu = document.getElementById('chatOptionsMenu');
        console.log('Hiding menu after clearChat');
        menu.classList.add('hidden');
    }

    // Блокировка пользователя
    function blockUser() {
        console.log('blockUser called');
        if (confirm('Вы уверены, что хотите заблокировать этого пользователя?')) {
            // Здесь должен быть запрос к API для блокировки пользователя
            // Пока просто показываем уведомление
            showNotification('Пользователь заблокирован', 'success');
        }
        // Закрываем меню
        const menu = document.getElementById('chatOptionsMenu');
        console.log('Hiding menu after blockUser');
        menu.classList.add('hidden');
    }

    // Жалоба на пользователя
    function reportUser() {
        console.log('reportUser called');
        const reason = prompt('Укажите причину жалобы:');
        if (reason) {
            // Здесь должен быть запрос к API для отправки жалобы
            // Пока просто показываем уведомление
            showNotification('Жалоба отправлена', 'success');
        }
        // Закрываем меню
        const menu = document.getElementById('chatOptionsMenu');
        console.log('Hiding menu after reportUser');
        menu.classList.add('hidden');
    }
</script>
</@layout>
