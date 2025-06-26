<#assign pageTitle = "Arannati - Сообщения администратора">
<#assign pageDescription = "Управление сообщениями в административной панели Arannati">
<#include "../layouts/main.ftlh">

<@layout>
    <div class="container mx-auto px-4 py-8">
        <!-- Page Header -->
        <div class="bg-white shadow-md rounded-lg p-6 mb-6">
            <div class="flex items-center justify-between mb-4">
                <h1 class="text-2xl font-bold text-gray-800">Управление сообщениями</h1>
                <a href="/dashboard" class="px-4 py-2 bg-gray-200 text-gray-700 rounded hover:bg-gray-300">
                    <i class="fas fa-arrow-left mr-2"></i> Назад к панели
                </a>
            </div>

            <div class="bg-blue-50 border-l-4 border-blue-500 p-4">
                <div class="flex">
                    <div class="flex-shrink-0">
                        <i class="fas fa-info-circle text-blue-500"></i>
                    </div>
                    <div class="ml-3">
                        <p class="text-sm text-blue-700">
                            Здесь вы можете просматривать и отправлять сообщения пользователям.
                        </p>
                    </div>
                </div>
            </div>
        </div>

        <!-- Messages Tabs -->
        <div class="bg-white shadow-md rounded-lg overflow-hidden mb-6">
            <div class="border-b border-gray-200">
                <nav class="-mb-px flex">
                    <button id="tab-received" class="tab-button active w-1/2 py-4 px-6 text-center border-b-2 border-blue-500 font-medium text-blue-600">
                        Полученные сообщения
                    </button>
                    <button id="tab-sent" class="tab-button w-1/2 py-4 px-6 text-center border-b-2 border-transparent font-medium text-gray-500 hover:text-gray-700 hover:border-gray-300">
                        Отправленные сообщения
                    </button>
                </nav>
            </div>

            <!-- Received Messages Tab -->
            <div id="tab-content-received" class="tab-content block p-6">
                <#if receivedMessages?size == 0>
                    <div class="text-center py-8">
                        <i class="fas fa-inbox text-gray-400 text-5xl mb-4"></i>
                        <p class="text-gray-500">У вас нет полученных сообщений</p>
                    </div>
                <#else>
                    <div class="overflow-x-auto">
                        <table class="min-w-full divide-y divide-gray-200">
                            <thead class="bg-gray-50">
                                <tr>
                                    <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                        Отправитель
                                    </th>
                                    <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                        Сообщение
                                    </th>
                                    <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                        Дата
                                    </th>
                                    <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                        Статус
                                    </th>
                                    <th scope="col" class="relative px-6 py-3">
                                        <span class="sr-only">Действия</span>
                                    </th>
                                </tr>
                            </thead>
                            <tbody class="bg-white divide-y divide-gray-200">
                                <#list receivedMessages as message>
                                    <tr class="${message.isRead?string('', 'bg-blue-50')}">
                                        <td class="px-6 py-4 whitespace-nowrap">
                                            <div class="flex items-center">
                                                <div class="flex-shrink-0 h-10 w-10 bg-gray-200 rounded-full flex items-center justify-center">
                                                    <i class="fas fa-user text-gray-500"></i>
                                                </div>
                                                <div class="ml-4">
                                                    <div class="text-sm font-medium text-gray-900">
                                                        ${message.senderName}
                                                    </div>
                                                    <div class="text-sm text-gray-500">
                                                        ${message.senderEmail}
                                                    </div>
                                                </div>
                                            </div>
                                        </td>
                                        <td class="px-6 py-4">
                                            <div class="text-sm text-gray-900 max-w-md truncate">
                                                ${message.content}
                                            </div>
                                        </td>
                                        <td class="px-6 py-4 whitespace-nowrap">
                                            <div class="text-sm text-gray-500">
                                                ${message.createdAt}
                                            </div>
                                        </td>
                                        <td class="px-6 py-4 whitespace-nowrap">
                                            <#if message.read>
                                                <span class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full bg-green-100 text-green-800">
                                                    Прочитано
                                                </span>
                                            <#else>
                                                <span class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full bg-yellow-100 text-yellow-800">
                                                    Новое
                                                </span>
                                            </#if>
                                        </td>
                                        <td class="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                                            <button class="text-blue-600 hover:text-blue-900 mr-3 view-message" data-id="${message.id}" data-content="${message.content}" data-sender="${message.senderName}">
                                                <i class="fas fa-eye"></i>
                                            </button>
                                            <button class="text-green-600 hover:text-green-900 mr-3 reply-message" data-id="${message.senderId}" data-name="${message.senderName}">
                                                <i class="fas fa-reply"></i>
                                            </button>
                                            <button class="text-red-600 hover:text-red-900 delete-message" data-id="${message.id}">
                                                <i class="fas fa-trash"></i>
                                            </button>
                                        </td>
                                    </tr>
                                </#list>
                            </tbody>
                        </table>
                    </div>
                </#if>
            </div>

            <!-- Sent Messages Tab -->
            <div id="tab-content-sent" class="tab-content hidden p-6">
                <#if sentMessages?size == 0>
                    <div class="text-center py-8">
                        <i class="fas fa-paper-plane text-gray-400 text-5xl mb-4"></i>
                        <p class="text-gray-500">У вас нет отправленных сообщений</p>
                    </div>
                <#else>
                    <div class="overflow-x-auto">
                        <table class="min-w-full divide-y divide-gray-200">
                            <thead class="bg-gray-50">
                                <tr>
                                    <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                        Получатель
                                    </th>
                                    <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                        Сообщение
                                    </th>
                                    <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                        Дата
                                    </th>
                                    <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                        Статус
                                    </th>
                                    <th scope="col" class="relative px-6 py-3">
                                        <span class="sr-only">Действия</span>
                                    </th>
                                </tr>
                            </thead>
                            <tbody class="bg-white divide-y divide-gray-200">
                                <#list sentMessages as message>
                                    <tr>
                                        <td class="px-6 py-4 whitespace-nowrap">
                                            <div class="flex items-center">
                                                <div class="flex-shrink-0 h-10 w-10 bg-gray-200 rounded-full flex items-center justify-center">
                                                    <i class="fas fa-user text-gray-500"></i>
                                                </div>
                                                <div class="ml-4">
                                                    <div class="text-sm font-medium text-gray-900">
                                                        ${message.recipientName}
                                                    </div>
                                                    <div class="text-sm text-gray-500">
                                                        ${message.recipientEmail}
                                                    </div>
                                                </div>
                                            </div>
                                        </td>
                                        <td class="px-6 py-4">
                                            <div class="text-sm text-gray-900 max-w-md truncate">
                                                ${message.content}
                                            </div>
                                        </td>
                                        <td class="px-6 py-4 whitespace-nowrap">
                                            <div class="text-sm text-gray-500">
                                                ${message.createdAt}
                                            </div>
                                        </td>
                                        <td class="px-6 py-4 whitespace-nowrap">
                                            <#if message.read>
                                                <span class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full bg-green-100 text-green-800">
                                                    Прочитано
                                                </span>
                                            <#else>
                                                <span class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full bg-blue-100 text-blue-800">
                                                    Доставлено
                                                </span>
                                            </#if>
                                        </td>
                                        <td class="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                                            <button class="text-blue-600 hover:text-blue-900 mr-3 view-message" data-id="${message.id}" data-content="${message.content}" data-recipient="${message.recipientName}">
                                                <i class="fas fa-eye"></i>
                                            </button>
                                            <button class="text-red-600 hover:text-red-900 delete-message" data-id="${message.id}">
                                                <i class="fas fa-trash"></i>
                                            </button>
                                        </td>
                                    </tr>
                                </#list>
                            </tbody>
                        </table>
                    </div>
                </#if>
            </div>
        </div>

        <!-- New Message Button -->
        <div class="fixed bottom-6 right-6">
            <button id="new-message-btn" class="bg-blue-600 text-white rounded-full p-4 shadow-lg hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500">
                <i class="fas fa-pen"></i>
            </button>
        </div>

        <!-- New Message Modal -->
        <div id="new-message-modal" class="fixed inset-0 bg-gray-600 bg-opacity-50 flex items-center justify-center hidden z-50">
            <div class="bg-white rounded-lg shadow-xl max-w-md w-full mx-4">
                <div class="px-6 py-4 border-b">
                    <div class="flex items-center justify-between">
                        <h3 class="text-lg font-medium text-gray-900">Новое сообщение</h3>
                        <button id="close-modal" class="text-gray-400 hover:text-gray-500">
                            <i class="fas fa-times"></i>
                        </button>
                    </div>
                </div>
                <form id="send-message-form" class="px-6 py-4">
                    <div class="mb-4">
                        <label for="recipient" class="block text-sm font-medium text-gray-700 mb-1">Получатель</label>
                        <select id="recipient" name="userId" class="mt-1 block w-full py-2 px-3 border border-gray-300 bg-white rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500 sm:text-sm" required>
                            <option value="">Выберите получателя</option>
                            <!-- This will be populated with AJAX -->
                        </select>
                    </div>
                    <div class="mb-4">
                        <label for="message" class="block text-sm font-medium text-gray-700 mb-1">Сообщение</label>
                        <textarea id="message" name="message" rows="5" class="mt-1 block w-full py-2 px-3 border border-gray-300 bg-white rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500 sm:text-sm" required></textarea>
                    </div>
                    <div class="flex justify-end">
                        <button type="button" id="cancel-message" class="bg-white py-2 px-4 border border-gray-300 rounded-md shadow-sm text-sm font-medium text-gray-700 hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 mr-3">
                            Отмена
                        </button>
                        <button type="submit" class="inline-flex justify-center py-2 px-4 border border-transparent shadow-sm text-sm font-medium rounded-md text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500">
                            Отправить
                        </button>
                    </div>
                </form>
            </div>
        </div>

        <!-- View Message Modal -->
        <div id="view-message-modal" class="fixed inset-0 bg-gray-600 bg-opacity-50 flex items-center justify-center hidden z-50">
            <div class="bg-white rounded-lg shadow-xl max-w-md w-full mx-4">
                <div class="px-6 py-4 border-b">
                    <div class="flex items-center justify-between">
                        <h3 id="view-message-title" class="text-lg font-medium text-gray-900">Просмотр сообщения</h3>
                        <button id="close-view-modal" class="text-gray-400 hover:text-gray-500">
                            <i class="fas fa-times"></i>
                        </button>
                    </div>
                </div>
                <div class="px-6 py-4">
                    <div class="mb-4">
                        <p id="view-message-content" class="text-gray-700 whitespace-pre-line"></p>
                    </div>
                    <div class="flex justify-end">
                        <button type="button" id="close-view-btn" class="bg-white py-2 px-4 border border-gray-300 rounded-md shadow-sm text-sm font-medium text-gray-700 hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500">
                            Закрыть
                        </button>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <script>
        document.addEventListener('DOMContentLoaded', function() {
            // Tab switching
            const tabButtons = document.querySelectorAll('.tab-button');
            const tabContents = document.querySelectorAll('.tab-content');

            tabButtons.forEach(button => {
                button.addEventListener('click', () => {
                    // Remove active class from all buttons and hide all contents
                    tabButtons.forEach(btn => btn.classList.remove('active', 'border-blue-500', 'text-blue-600'));
                    tabButtons.forEach(btn => btn.classList.add('border-transparent', 'text-gray-500'));
                    tabContents.forEach(content => content.classList.add('hidden'));

                    // Add active class to clicked button and show corresponding content
                    button.classList.add('active', 'border-blue-500', 'text-blue-600');
                    button.classList.remove('border-transparent', 'text-gray-500');

                    const tabId = button.id.replace('tab-', '');
                    document.getElementById('tab-content-' + tabId).classList.remove('hidden');
                });
            });

            // Modal handling
            const newMessageBtn = document.getElementById('new-message-btn');
            const newMessageModal = document.getElementById('new-message-modal');
            const closeModal = document.getElementById('close-modal');
            const cancelMessage = document.getElementById('cancel-message');
            const sendMessageForm = document.getElementById('send-message-form');

            // View message modal
            const viewMessageModal = document.getElementById('view-message-modal');
            const closeViewModal = document.getElementById('close-view-modal');
            const closeViewBtn = document.getElementById('close-view-btn');
            const viewMessageTitle = document.getElementById('view-message-title');
            const viewMessageContent = document.getElementById('view-message-content');

            // Open new message modal
            newMessageBtn.addEventListener('click', () => {
                // Fetch users for recipient dropdown
                fetch('/admin/users/list')
                    .then(response => response.json())
                    .then(users => {
                        const recipientSelect = document.getElementById('recipient');
                        recipientSelect.innerHTML = '<option value="">Выберите получателя</option>';

                        users.forEach(user => {
                            const option = document.createElement('option');
                            option.value = user.id;
                            option.textContent = user.firstName + ' ' + user.lastName + ' (' + user.email + ')';
                            recipientSelect.appendChild(option);
                        });
                    })
                    .catch(error => console.error('Error fetching users:', error));

                newMessageModal.classList.remove('hidden');
            });

            // Close new message modal
            closeModal.addEventListener('click', () => {
                newMessageModal.classList.add('hidden');
            });

            cancelMessage.addEventListener('click', () => {
                newMessageModal.classList.add('hidden');
            });

            // Close view message modal
            closeViewModal.addEventListener('click', () => {
                viewMessageModal.classList.add('hidden');
            });

            closeViewBtn.addEventListener('click', () => {
                viewMessageModal.classList.add('hidden');
            });

            // View message
            document.querySelectorAll('.view-message').forEach(button => {
                button.addEventListener('click', () => {
                    const content = button.getAttribute('data-content');
                    const sender = button.getAttribute('data-sender');
                    const recipient = button.getAttribute('data-recipient');

                    if (sender) {
                        viewMessageTitle.textContent = 'Сообщение от ' + sender;
                    } else if (recipient) {
                        viewMessageTitle.textContent = 'Сообщение для ' + recipient;
                    } else {
                        viewMessageTitle.textContent = 'Просмотр сообщения';
                    }

                    viewMessageContent.textContent = content;
                    viewMessageModal.classList.remove('hidden');

                    // Mark as read if it's a received message
                    if (sender) {
                        const messageId = button.getAttribute('data-id');
                        fetch('/admin/messages/' + messageId + '/read', {
                            method: 'POST',
                            headers: {
                                'Content-Type': 'application/json',
                            }
                        });
                    }
                });
            });

            // Reply to message
            document.querySelectorAll('.reply-message').forEach(button => {
                button.addEventListener('click', () => {
                    const userId = button.getAttribute('data-id');
                    const userName = button.getAttribute('data-name');

                    // Set recipient in dropdown
                    const recipientSelect = document.getElementById('recipient');

                    // First fetch users to populate the dropdown
                    fetch('/admin/users/list')
                        .then(response => response.json())
                        .then(users => {
                            recipientSelect.innerHTML = '<option value="">Выберите получателя</option>';

                            users.forEach(user => {
                                const option = document.createElement('option');
                                option.value = user.id;
                                option.textContent = user.firstName + ' ' + user.lastName + ' (' + user.email + ')';

                                // Select the user we're replying to
                                if (user.id.toString() === userId) {
                                    option.selected = true;
                                }

                                recipientSelect.appendChild(option);
                            });

                            // Open the modal
                            newMessageModal.classList.remove('hidden');
                        })
                        .catch(error => console.error('Error fetching users:', error));
                });
            });

            // Send message form submission
            sendMessageForm.addEventListener('submit', (e) => {
                e.preventDefault();

                const userId = document.getElementById('recipient').value;
                const message = document.getElementById('message').value;

                if (!userId || !message) {
                    alert('Пожалуйста, выберите получателя и введите сообщение');
                    return;
                }

                fetch('/admin/messages/send', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded',
                    },
                    body: 'userId=' + userId + '&message=' + encodeURIComponent(message)
                })
                .then(response => {
                    if (response.ok) {
                        alert('Сообщение успешно отправлено');
                        newMessageModal.classList.add('hidden');
                        sendMessageForm.reset();
                        // Reload the page to show the new message in the sent list
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

            // Delete message
            document.querySelectorAll('.delete-message').forEach(button => {
                button.addEventListener('click', () => {
                    if (confirm('Вы уверены, что хотите удалить это сообщение?')) {
                        const messageId = button.getAttribute('data-id');

                        fetch('/admin/messages/' + messageId + '/delete', {
                            method: 'POST',
                            headers: {
                                'Content-Type': 'application/json',
                            }
                        })
                        .then(response => {
                            if (response.ok) {
                                alert('Сообщение успешно удалено');
                                // Reload the page to update the message lists
                                window.location.reload();
                            } else {
                                alert('Ошибка при удалении сообщения');
                            }
                        })
                        .catch(error => {
                            console.error('Error deleting message:', error);
                            alert('Ошибка при удалении сообщения');
                        });
                    }
                });
            });
        });
    </script>
</@layout>
