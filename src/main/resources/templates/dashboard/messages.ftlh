<#assign pageTitle = "Arannati - Сообщения">
<#assign pageDescription = "Сообщения пользователя Arannati">
<#include "../layouts/main.ftlh">

<@layout>
    <div class="container mx-auto px-4 py-8">
        <!-- Page Header -->
        <div class="bg-white shadow-md rounded-lg p-6 mb-6">
            <div class="flex items-center justify-between mb-4">
                <h1 class="text-2xl font-bold text-gray-800">Сообщения</h1>
                <a href="/dashboard" class="px-4 py-2 bg-gray-200 text-gray-700 rounded hover:bg-gray-300">
                    <i class="fas fa-arrow-left mr-2"></i> Назад к панели
                </a>
            </div>

            <div class="bg-blue-50 border-l-4 border-blue-500 p-4">
                <div class="flex">
                    <div class="flex-shrink-0">
                        <i class="fas fa-envelope text-blue-500"></i>
                    </div>
                    <div class="ml-3">
                        <p class="text-sm text-blue-700">
                            Здесь вы можете просматривать и управлять вашими сообщениями.
                        </p>
                    </div>
                </div>
            </div>
        </div>

        <!-- Messages Tabs -->
        <div class="bg-white shadow-md rounded-lg overflow-hidden mb-6">
            <div class="border-b border-gray-200">
                <nav class="-mb-px flex">
                    <a href="#" class="received-tab w-1/2 py-4 px-1 text-center border-b-2 border-blue-500 font-medium text-sm text-blue-600">
                        Входящие <#if unreadMessageCount?? && unreadMessageCount gt 0><span class="ml-1 bg-red-500 text-white text-xs rounded-full h-5 w-5 inline-flex items-center justify-center">${unreadMessageCount}</span></#if>
                    </a>
                    <a href="#" class="sent-tab w-1/2 py-4 px-1 text-center border-b-2 border-transparent font-medium text-sm text-gray-500 hover:text-gray-700 hover:border-gray-300">
                        Отправленные
                    </a>
                </nav>
            </div>

            <!-- Received Messages -->
            <div id="received-messages" class="p-6">
                <#if receivedMessages?? && receivedMessages?size gt 0>
                    <div class="space-y-4">
                        <#list receivedMessages as message>
                            <div class="border rounded-lg p-4 <#if !message.read>bg-blue-50</#if>">
                                <div class="flex justify-between items-start mb-2">
                                    <div>
                                        <span class="font-medium text-gray-900">От: ${message.senderName}</span>
                                        <span class="text-sm text-gray-500 ml-2">${message.createdAt}</span>
                                    </div>
                                    <div>
                                        <#if !message.read>
                                            <span class="px-2 py-1 bg-blue-100 text-blue-800 rounded-full text-xs font-medium">
                                                Новое
                                            </span>
                                        </#if>
                                    </div>
                                </div>
                                <p class="text-gray-700 mb-3">${message.content}</p>
                                <div class="flex justify-end space-x-2">
                                    <button class="mark-read-btn px-3 py-1 bg-gray-200 text-gray-700 rounded text-sm hover:bg-gray-300" data-id="${message.id}" <#if message.read>disabled</#if>>
                                        <i class="fas fa-check mr-1"></i> Прочитано
                                    </button>
                                    <button class="reply-btn px-3 py-1 bg-blue-600 text-white rounded text-sm hover:bg-blue-700" data-id="${message.id}" data-recipient="${message.senderId}" data-name="${message.senderName}">
                                        <i class="fas fa-reply mr-1"></i> Ответить
                                    </button>
                                </div>
                            </div>
                        </#list>
                    </div>
                <#else>
                    <div class="text-center py-8">
                        <div class="text-4xl text-gray-300 mb-3">
                            <i class="fas fa-inbox"></i>
                        </div>
                        <p class="text-gray-500">У вас нет входящих сообщений</p>
                    </div>
                </#if>
            </div>

            <!-- Sent Messages -->
            <div id="sent-messages" class="p-6 hidden">
                <#if sentMessages?? && sentMessages?size gt 0>
                    <div class="space-y-4">
                        <#list sentMessages as message>
                            <div class="border rounded-lg p-4">
                                <div class="flex justify-between items-start mb-2">
                                    <div>
                                        <span class="font-medium text-gray-900">Кому: ${message.recipientName}</span>
                                        <span class="text-sm text-gray-500 ml-2">${message.createdAt}</span>
                                    </div>
                                    <div>
                                        <#if message.read>
                                            <span class="px-2 py-1 bg-green-100 text-green-800 rounded-full text-xs font-medium">
                                                Прочитано
                                            </span>
                                        <#else>
                                            <span class="px-2 py-1 bg-yellow-100 text-yellow-800 rounded-full text-xs font-medium">
                                                Не прочитано
                                            </span>
                                        </#if>
                                    </div>
                                </div>
                                <p class="text-gray-700">${message.content}</p>
                            </div>
                        </#list>
                    </div>
                <#else>
                    <div class="text-center py-8">
                        <div class="text-4xl text-gray-300 mb-3">
                            <i class="fas fa-paper-plane"></i>
                        </div>
                        <p class="text-gray-500">У вас нет отправленных сообщений</p>
                    </div>
                </#if>
            </div>
        </div>

        <!-- New Message Button -->
        <div class="flex justify-end mb-6">
            <button id="new-message-btn" class="px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700">
                <i class="fas fa-plus mr-2"></i> Новое сообщение
            </button>
        </div>
    </div>

    <!-- New Message Modal -->
    <div id="new-message-modal" class="fixed inset-0 bg-gray-600 bg-opacity-50 flex items-center justify-center hidden z-50">
        <div class="bg-white rounded-lg shadow-xl max-w-md w-full mx-4">
            <div class="px-6 py-4 border-b">
                <div class="flex items-center justify-between">
                    <h3 id="message-title" class="text-lg font-medium text-gray-900">Новое сообщение</h3>
                    <button id="close-message-modal" class="text-gray-400 hover:text-gray-500">
                        <i class="fas fa-times"></i>
                    </button>
                </div>
            </div>
            <form id="message-form">
                <div class="px-6 py-4">
                    <input type="hidden" id="message-recipient-id">
                    <div class="mb-4">
                        <label for="message-recipient" class="block text-sm font-medium text-gray-700 mb-1">Получатель</label>
                        <input type="text" id="message-recipient" class="w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500" readonly>
                    </div>
                    <div class="mb-4">
                        <label for="message-content" class="block text-sm font-medium text-gray-700 mb-1">Сообщение</label>
                        <textarea id="message-content" name="message" rows="4" class="w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500" required></textarea>
                    </div>
                </div>
                <div class="px-6 py-4 border-t flex justify-end">
                    <button type="button" id="cancel-message" class="bg-gray-200 text-gray-700 px-4 py-2 rounded hover:bg-gray-300 mr-2">
                        Отмена
                    </button>
                    <button type="submit" class="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700">
                        Отправить
                    </button>
                </div>
            </form>
        </div>
    </div>

    <script>
        document.addEventListener('DOMContentLoaded', function() {
            // Tab switching
            const receivedTab = document.querySelector('.received-tab');
            const sentTab = document.querySelector('.sent-tab');
            const receivedMessages = document.getElementById('received-messages');
            const sentMessages = document.getElementById('sent-messages');

            receivedTab.addEventListener('click', function(e) {
                e.preventDefault();
                receivedTab.classList.add('border-blue-500', 'text-blue-600');
                receivedTab.classList.remove('border-transparent', 'text-gray-500');
                sentTab.classList.remove('border-blue-500', 'text-blue-600');
                sentTab.classList.add('border-transparent', 'text-gray-500');
                receivedMessages.classList.remove('hidden');
                sentMessages.classList.add('hidden');
            });

            sentTab.addEventListener('click', function(e) {
                e.preventDefault();
                sentTab.classList.add('border-blue-500', 'text-blue-600');
                sentTab.classList.remove('border-transparent', 'text-gray-500');
                receivedTab.classList.remove('border-blue-500', 'text-blue-600');
                receivedTab.classList.add('border-transparent', 'text-gray-500');
                sentMessages.classList.remove('hidden');
                receivedMessages.classList.add('hidden');
            });

            // Mark as read
            const markReadBtns = document.querySelectorAll('.mark-read-btn');
            markReadBtns.forEach(button => {
                button.addEventListener('click', function() {
                    const messageId = this.getAttribute('data-id');

                    // Get CSRF token
                    const token = document.querySelector('meta[name="_csrf"]').getAttribute('content');
                    const header = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

                    fetch('/dashboard/messages/' + messageId + '/read', {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/json',
                            [header]: token
                        }
                    })
                    .then(response => {
                        if (response.ok) {
                            // Update UI
                            const messageDiv = this.closest('.border');
                            messageDiv.classList.remove('bg-blue-50');
                            this.disabled = true;

                            // Remove "New" badge
                            const newBadge = messageDiv.querySelector('.bg-blue-100');
                            if (newBadge) {
                                newBadge.remove();
                            }

                            // Update unread count in tab
                            const unreadCount = document.querySelector('.received-tab span');
                            if (unreadCount) {
                                const count = parseInt(unreadCount.textContent);
                                if (count > 1) {
                                    unreadCount.textContent = count - 1;
                                } else {
                                    unreadCount.remove();
                                }
                            }
                        } else {
                            alert('Ошибка при отметке сообщения как прочитанное');
                        }
                    })
                    .catch(error => {
                        console.error('Error marking message as read:', error);
                        alert('Ошибка при отметке сообщения как прочитанное');
                    });
                });
            });

            // Reply to message
            const replyBtns = document.querySelectorAll('.reply-btn');
            const newMessageModal = document.getElementById('new-message-modal');
            const closeMessageModal = document.getElementById('close-message-modal');
            const cancelMessage = document.getElementById('cancel-message');
            const messageForm = document.getElementById('message-form');
            const messageRecipientId = document.getElementById('message-recipient-id');
            const messageRecipient = document.getElementById('message-recipient');
            const messageContent = document.getElementById('message-content');

            replyBtns.forEach(button => {
                button.addEventListener('click', function() {
                    const recipientId = this.getAttribute('data-recipient');
                    const recipientName = this.getAttribute('data-name');

                    messageRecipientId.value = recipientId;
                    messageRecipient.value = recipientName;
                    messageContent.value = '';

                    newMessageModal.classList.remove('hidden');
                });
            });

            // New message button
            const newMessageBtn = document.getElementById('new-message-btn');
            newMessageBtn.addEventListener('click', function() {
                // In a real app, you would show a recipient selector here
                // For now, we'll just set a default admin recipient
                messageRecipientId.value = '1'; // Admin ID
                messageRecipient.value = 'Администратор';
                messageContent.value = '';

                newMessageModal.classList.remove('hidden');
            });

            // Close message modal
            closeMessageModal.addEventListener('click', function() {
                newMessageModal.classList.add('hidden');
            });

            cancelMessage.addEventListener('click', function() {
                newMessageModal.classList.add('hidden');
            });

            // Submit message form
            messageForm.addEventListener('submit', function(e) {
                e.preventDefault();

                const recipientId = messageRecipientId.value;
                const message = messageContent.value.trim();

                if (!message) {
                    alert('Пожалуйста, введите сообщение');
                    return;
                }

                // Get CSRF token
                const token = document.querySelector('meta[name="_csrf"]').getAttribute('content');
                const header = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

                fetch('/dashboard/messages/send', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded',
                        [header]: token
                    },
                    body: 'userId=' + encodeURIComponent(recipientId) + '&message=' + encodeURIComponent(message)
                })
                .then(response => {
                    if (response.ok) {
                        alert('Сообщение отправлено');
                        newMessageModal.classList.add('hidden');
                        // Reload page to show the new message in sent messages
                        window.location.reload();
                    } else {
                        alert('Ошибка при отправке сообщения');
                    }
                })
                .catch(error => {
                    console.error('Error sending message:', error);
                    alert('Ошибка при отправке сообщения');
                });
            });
        });
    </script>
</@layout>
