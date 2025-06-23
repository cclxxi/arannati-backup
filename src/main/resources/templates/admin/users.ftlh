<#assign pageTitle = "Arannati - Управление пользователями">
<#assign pageDescription = "Управление пользователями в административной панели Arannati">
<#include "../layouts/main.ftlh">

<@layout>
    <div class="container mx-auto px-4 py-8">
        <!-- Page Header -->
        <div class="bg-white shadow-md rounded-lg p-6 mb-6">
            <div class="flex items-center justify-between mb-4">
                <h1 class="text-2xl font-bold text-gray-800">Управление пользователями</h1>
                <a href="/admin" class="px-4 py-2 bg-gray-200 text-gray-700 rounded hover:bg-gray-300">
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
                            Здесь вы можете управлять пользователями системы, блокировать/разблокировать аккаунты и подтверждать косметологов.
                        </p>
                    </div>
                </div>
            </div>
        </div>

        <!-- Users Filter -->
        <div class="bg-white shadow-md rounded-lg p-6 mb-6">
            <div class="flex flex-wrap items-center justify-between mb-4">
                <h2 class="text-lg font-semibold text-gray-800">Фильтры</h2>
                <div class="flex space-x-2 mt-2 sm:mt-0">
                    <button id="filter-all" class="filter-btn active px-3 py-1 bg-blue-500 text-white rounded hover:bg-blue-600">
                        Все
                    </button>
                    <button id="filter-users" class="filter-btn px-3 py-1 bg-gray-200 text-gray-700 rounded hover:bg-gray-300">
                        Пользователи
                    </button>
                    <button id="filter-cosmetologists" class="filter-btn px-3 py-1 bg-gray-200 text-gray-700 rounded hover:bg-gray-300">
                        Косметологи
                    </button>
                    <button id="filter-admins" class="filter-btn px-3 py-1 bg-gray-200 text-gray-700 rounded hover:bg-gray-300">
                        Администраторы
                    </button>
                </div>
            </div>

            <div class="flex flex-wrap items-center space-y-2 sm:space-y-0">
                <div class="w-full sm:w-auto flex-1 sm:mr-4">
                    <input type="text" id="search-input" placeholder="Поиск по имени или email" class="w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500">
                </div>
                <div>
                    <button id="search-btn" class="px-4 py-2 bg-blue-500 text-white rounded-lg hover:bg-blue-600">
                        <i class="fas fa-search mr-2"></i> Поиск
                    </button>
                </div>
            </div>
        </div>

        <!-- Users Table -->
        <div class="bg-white shadow-md rounded-lg overflow-hidden">
            <div class="overflow-x-auto">
                <table class="min-w-full divide-y divide-gray-200">
                    <thead class="bg-gray-50">
                        <tr>
                            <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                Пользователь
                            </th>
                            <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                Роль
                            </th>
                            <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                Статус
                            </th>
                            <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                Дата регистрации
                            </th>
                            <th scope="col" class="relative px-6 py-3">
                                <span class="sr-only">Действия</span>
                            </th>
                        </tr>
                    </thead>
                    <tbody class="bg-white divide-y divide-gray-200" id="users-table-body">
                        <#list users as user>
                            <tr class="user-row" data-role="${user.role}">
                                <td class="px-6 py-4 whitespace-nowrap">
                                    <div class="flex items-center">
                                        <div class="flex-shrink-0 h-10 w-10 bg-gray-200 rounded-full flex items-center justify-center">
                                            <i class="fas fa-user text-gray-500"></i>
                                        </div>
                                        <div class="ml-4">
                                            <div class="text-sm font-medium text-gray-900">
                                                ${user.firstName} ${user.lastName}
                                            </div>
                                            <div class="text-sm text-gray-500">
                                                ${user.email}
                                            </div>
                                            <#if user.phone??>
                                                <div class="text-xs text-gray-400">
                                                    ${user.phone}
                                                </div>
                                            </#if>
                                        </div>
                                    </div>
                                </td>
                                <td class="px-6 py-4 whitespace-nowrap">
                                    <#if user.role == "ADMIN">
                                        <span class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full bg-red-100 text-red-800">
                                            Администратор
                                        </span>
                                    <#elseif user.role == "COSMETOLOGIST">
                                        <span class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full bg-purple-100 text-purple-800">
                                            Косметолог
                                        </span>
                                    <#else>
                                        <span class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full bg-blue-100 text-blue-800">
                                            Пользователь
                                        </span>
                                    </#if>
                                </td>
                                <td class="px-6 py-4 whitespace-nowrap">
                                    <#if user.role == "COSMETOLOGIST" && !user.isVerified>
                                        <span class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full bg-yellow-100 text-yellow-800">
                                            Ожидает подтверждения
                                        </span>
                                    <#elseif user.isActive>
                                        <span class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full bg-green-100 text-green-800">
                                            Активен
                                        </span>
                                    <#else>
                                        <span class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full bg-red-100 text-red-800">
                                            Заблокирован
                                        </span>
                                    </#if>
                                </td>
                                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                                    ${user.createdAt}
                                </td>
                                <td class="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                                    <#if user.role != "ADMIN" || (user.role == "ADMIN" && user.id != currentUserId)>
                                        <#if user.isActive>
                                            <button class="toggle-active-btn bg-red-100 text-red-800 px-3 py-1 rounded-full mr-2 hover:bg-red-200" data-id="${user.id}" data-active="true">
                                                <i class="fas fa-ban mr-1"></i> Заблокировать
                                            </button>
                                        <#else>
                                            <button class="toggle-active-btn bg-green-100 text-green-800 px-3 py-1 rounded-full mr-2 hover:bg-green-200" data-id="${user.id}" data-active="false">
                                                <i class="fas fa-check mr-1"></i> Разблокировать
                                            </button>
                                        </#if>
                                    </#if>

                                    <#if user.role == "COSMETOLOGIST" && !user.isVerified>
                                        <button class="approve-cosmetologist-btn bg-green-100 text-green-800 px-3 py-1 rounded-full mr-2 hover:bg-green-200" data-id="${user.id}">
                                            <i class="fas fa-check mr-1"></i> Подтвердить
                                        </button>
                                        <button class="decline-cosmetologist-btn bg-red-100 text-red-800 px-3 py-1 rounded-full hover:bg-red-200" data-id="${user.id}">
                                            <i class="fas fa-times mr-1"></i> Отклонить
                                        </button>
                                    </#if>

                                    <button class="send-message-btn bg-blue-100 text-blue-800 px-3 py-1 rounded-full hover:bg-blue-200" data-id="${user.id}" data-name="${user.firstName} ${user.lastName}">
                                        <i class="fas fa-envelope mr-1"></i> Сообщение
                                    </button>
                                </td>
                            </tr>
                        </#list>
                    </tbody>
                </table>
            </div>
        </div>

        <!-- New Message Modal -->
        <div id="message-modal" class="fixed inset-0 bg-gray-600 bg-opacity-50 flex items-center justify-center hidden z-50">
            <div class="bg-white rounded-lg shadow-xl max-w-md w-full mx-4">
                <div class="px-6 py-4 border-b">
                    <div class="flex items-center justify-between">
                        <h3 id="message-modal-title" class="text-lg font-medium text-gray-900">Новое сообщение</h3>
                        <button id="close-message-modal" class="text-gray-400 hover:text-gray-500">
                            <i class="fas fa-times"></i>
                        </button>
                    </div>
                </div>
                <form id="send-message-form" class="px-6 py-4">
                    <input type="hidden" id="recipient-id" name="userId">
                    <div class="mb-4">
                        <label for="message-content" class="block text-sm font-medium text-gray-700 mb-1">Сообщение</label>
                        <textarea id="message-content" name="message" rows="5" class="mt-1 block w-full py-2 px-3 border border-gray-300 bg-white rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500 sm:text-sm" required></textarea>
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
    </div>

    <script>
        document.addEventListener('DOMContentLoaded', function() {
            // Filter buttons
            const filterButtons = document.querySelectorAll('.filter-btn');
            const userRows = document.querySelectorAll('.user-row');

            filterButtons.forEach(button => {
                button.addEventListener('click', () => {
                    // Remove active class from all buttons
                    filterButtons.forEach(btn => {
                        btn.classList.remove('active', 'bg-blue-500', 'text-white');
                        btn.classList.add('bg-gray-200', 'text-gray-700');
                    });

                    // Add active class to clicked button
                    button.classList.add('active', 'bg-blue-500', 'text-white');
                    button.classList.remove('bg-gray-200', 'text-gray-700');

                    // Filter users
                    const filterId = button.id;
                    userRows.forEach(row => {
                        const role = row.getAttribute('data-role');

                        if (filterId === 'filter-all') {
                            row.classList.remove('hidden');
                        } else if (filterId === 'filter-users' && role === 'USER') {
                            row.classList.remove('hidden');
                        } else if (filterId === 'filter-cosmetologists' && role === 'COSMETOLOGIST') {
                            row.classList.remove('hidden');
                        } else if (filterId === 'filter-admins' && role === 'ADMIN') {
                            row.classList.remove('hidden');
                        } else {
                            row.classList.add('hidden');
                        }
                    });
                });
            });

            // Search functionality
            const searchInput = document.getElementById('search-input');
            const searchBtn = document.getElementById('search-btn');

            function performSearch() {
                const searchTerm = searchInput.value.toLowerCase();

                userRows.forEach(row => {
                    const userName = row.querySelector('.text-sm.font-medium').textContent.toLowerCase();
                    const userEmail = row.querySelector('.text-sm.text-gray-500').textContent.toLowerCase();

                    if (userName.includes(searchTerm) || userEmail.includes(searchTerm)) {
                        if (!row.classList.contains('hidden-by-filter')) {
                            row.classList.remove('hidden');
                        }
                    } else {
                        row.classList.add('hidden');
                    }
                });
            }

            searchBtn.addEventListener('click', performSearch);
            searchInput.addEventListener('keyup', function(e) {
                if (e.key === 'Enter') {
                    performSearch();
                }
            });

            // Toggle active status
            const toggleActiveButtons = document.querySelectorAll('.toggle-active-btn');

            toggleActiveButtons.forEach(button => {
                button.addEventListener('click', () => {
                    const userId = button.getAttribute('data-id');
                    const isActive = button.getAttribute('data-active') === 'true';
                    const action = isActive ? 'заблокировать' : 'разблокировать';

                    if (confirm(`Вы уверены, что хотите ${action} этого пользователя?`)) {
                        fetch(`/admin/users/${userId}/toggle-active`, {
                            method: 'POST',
                            headers: {
                                'Content-Type': 'application/json',
                            }
                        })
                        .then(response => {
                            if (response.ok) {
                                window.location.reload();
                            } else {
                                alert(`Не удалось ${action} пользователя`);
                            }
                        })
                        .catch(error => {
                            console.error('Error:', error);
                            alert(`Ошибка: ${error}`);
                        });
                    }
                });
            });

            // Approve cosmetologist
            const approveCosmetologistButtons = document.querySelectorAll('.approve-cosmetologist-btn');

            approveCosmetologistButtons.forEach(button => {
                button.addEventListener('click', () => {
                    const userId = button.getAttribute('data-id');

                    if (confirm('Вы уверены, что хотите подтвердить этого косметолога?')) {
                        fetch(`/admin/cosmetologists/${userId}/approve`, {
                            method: 'POST',
                            headers: {
                                'Content-Type': 'application/json',
                            }
                        })
                        .then(response => {
                            if (response.ok) {
                                window.location.reload();
                            } else {
                                alert('Не удалось подтвердить косметолога');
                            }
                        })
                        .catch(error => {
                            console.error('Error:', error);
                            alert(`Ошибка: ${error}`);
                        });
                    }
                });
            });

            // Decline cosmetologist
            const declineCosmetologistButtons = document.querySelectorAll('.decline-cosmetologist-btn');

            declineCosmetologistButtons.forEach(button => {
                button.addEventListener('click', () => {
                    const userId = button.getAttribute('data-id');

                    if (confirm('Вы уверены, что хотите отклонить этого косметолога?')) {
                        fetch(`/admin/cosmetologists/${userId}/decline`, {
                            method: 'POST',
                            headers: {
                                'Content-Type': 'application/json',
                            }
                        })
                        .then(response => {
                            if (response.ok) {
                                window.location.reload();
                            } else {
                                alert('Не удалось отклонить косметолога');
                            }
                        })
                        .catch(error => {
                            console.error('Error:', error);
                            alert(`Ошибка: ${error}`);
                        });
                    }
                });
            });

            // Message modal
            const messageModal = document.getElementById('message-modal');
            const closeMessageModal = document.getElementById('close-message-modal');
            const cancelMessage = document.getElementById('cancel-message');
            const sendMessageForm = document.getElementById('send-message-form');
            const sendMessageButtons = document.querySelectorAll('.send-message-btn');
            const messageModalTitle = document.getElementById('message-modal-title');
            const recipientIdInput = document.getElementById('recipient-id');

            sendMessageButtons.forEach(button => {
                button.addEventListener('click', () => {
                    const userId = button.getAttribute('data-id');
                    const userName = button.getAttribute('data-name');

                    messageModalTitle.textContent = `Сообщение для ${userName}`;
                    recipientIdInput.value = userId;
                    messageModal.classList.remove('hidden');
                });
            });

            closeMessageModal.addEventListener('click', () => {
                messageModal.classList.add('hidden');
            });

            cancelMessage.addEventListener('click', () => {
                messageModal.classList.add('hidden');
            });

            sendMessageForm.addEventListener('submit', (e) => {
                e.preventDefault();

                const userId = recipientIdInput.value;
                const message = document.getElementById('message-content').value;

                fetch('/admin/messages/send', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded',
                    },
                    body: `userId=${userId}&message=${encodeURIComponent(message)}`
                })
                .then(response => {
                    if (response.ok) {
                        alert('Сообщение успешно отправлено');
                        messageModal.classList.add('hidden');
                        sendMessageForm.reset();
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
