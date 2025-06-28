<#assign pageTitle = "Arannati - Личный кабинет">
<#assign pageDescription = "Личный кабинет пользователя Arannati">
<#include "../layouts/main.ftlh">

<@layout>
    <div class="container mx-auto px-4 py-8">
        <!-- Welcome Section -->
        <div class="bg-white shadow-md rounded-lg p-6 mb-6">
            <div class="flex items-center justify-between mb-4">
                <h1 class="text-2xl font-bold text-gray-800">Личный кабинет</h1>
                <span class="px-3 py-1 bg-green-100 text-green-800 rounded-full text-sm font-medium">
                    Покупатель
                </span>
            </div>

            <div class="bg-blue-50 border-l-4 border-blue-500 p-4 mb-6">
                <div class="flex">
                    <div class="flex-shrink-0">
                        <i class="fas fa-user-circle text-blue-500"></i>
                    </div>
                    <div class="ml-3">
                        <p class="text-sm text-blue-700">
                            Добро пожаловать, <span class="font-semibold">${user.firstName} ${user.lastName}</span>!
                        </p>
                    </div>
                </div>
            </div>
        </div>

        <!-- Dashboard Widgets -->
        <div class="grid grid-cols-1 md:grid-cols-4 gap-6 mb-6">
            <!-- Profile Widget -->
            <div class="bg-white shadow-md rounded-lg p-6">
                <div class="flex items-center justify-between mb-4">
                    <h2 class="text-lg font-semibold text-gray-800">Профиль</h2>
                    <a href="/dashboard/profile" class="text-blue-600 hover:text-blue-800">
                        <i class="fas fa-edit"></i>
                    </a>
                </div>

                <div class="space-y-3">
                    <div class="flex items-center">
                        <i class="fas fa-envelope text-gray-500 w-6"></i>
                        <span class="ml-2">${user.email}</span>
                    </div>
                    <div class="flex items-center">
                        <i class="fas fa-user text-gray-500 w-6"></i>
                        <span class="ml-2">${user.firstName} ${user.lastName}</span>
                    </div>
                    <div class="flex items-center">
                        <i class="fas fa-phone text-gray-500 w-6"></i>
                        <span class="ml-2">${user.phone!'-'}</span>
                    </div>
                </div>

                <div class="mt-4 pt-4 border-t">
                    <a href="/dashboard/profile" class="inline-flex items-center text-sm text-blue-600 hover:text-blue-800">
                        Редактировать профиль
                        <i class="fas fa-chevron-right ml-1 text-xs"></i>
                    </a>
                </div>
            </div>

            <!-- Orders Widget -->
            <div class="bg-white shadow-md rounded-lg p-6">
                <div class="flex items-center justify-between mb-4">
                    <h2 class="text-lg font-semibold text-gray-800">Заказы</h2>
                    <a href="/dashboard/orders" class="text-blue-600 hover:text-blue-800">
                        <i class="fas fa-list"></i>
                    </a>
                </div>

                <div class="text-center py-4">
                    <div class="text-3xl font-bold text-gray-800 mb-2">
                        ${orders?size}
                    </div>
                    <p class="text-sm text-gray-600">Всего заказов</p>
                </div>

                <div class="mt-4 pt-4 border-t">
                    <a href="/dashboard/orders" class="inline-flex items-center text-sm text-blue-600 hover:text-blue-800">
                        Просмотреть все заказы
                        <i class="fas fa-chevron-right ml-1 text-xs"></i>
                    </a>
                </div>
            </div>

            <!-- Messages Widget -->
            <div class="bg-white shadow-md rounded-lg p-6">
                <div class="flex items-center justify-between mb-4">
                    <h2 class="text-lg font-semibold text-gray-800 flex items-center">
                        <i class="fas fa-comments mr-2 text-blue-600"></i>
                        <#if currentUser.role == "COSMETOLOGIST">
                            Служба поддержки
                        <#else>
                            Сообщения
                        </#if>
                    </h2>
                    <div class="flex items-center space-x-2">
                        <#if unreadMessageCount gt 0>
                            <span class="px-2 py-1 bg-red-500 text-white text-xs rounded-full">
                                ${unreadMessageCount}
                            </span>
                        </#if>
                        <a href="/messages" class="text-blue-600 hover:text-blue-800 text-sm">
                            Все сообщения
                        </a>
                    </div>
                </div>

                <#if chats?size == 0>
                    <div class="text-center py-6">
                        <i class="fas fa-comment-alt text-3xl text-gray-300 mb-3"></i>
                        <p class="text-gray-500 mb-3">
                            <#if currentUser.role == "COSMETOLOGIST">
                                У вас нет активных обращений в поддержку
                            <#else>
                                У вас нет активных чатов
                            </#if>
                        </p>
                        <button onclick="openWriteAdminModal()" 
                                class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors">
                            <i class="fas fa-edit mr-2"></i>
                            <#if currentUser.role == "COSMETOLOGIST">
                                Обратиться в поддержку
                            <#else>
                                Написать администратору
                            </#if>
                        </button>
                    </div>
                <#else>
                    <!-- Recent Chats -->
                    <div class="space-y-3 mb-4">
                        <#list chats as chat>
                            <#if chat_index < 3> <!-- Показываем только первые 3 чата -->
                                <div class="flex items-center space-x-3 p-3 rounded-lg hover:bg-gray-50 cursor-pointer transition-colors"
                                     onclick="openChat('${chat.chatId}')">
                                    <!-- Avatar -->
                                    <div class="relative flex-shrink-0">
                                        <div class="w-10 h-10 bg-gradient-to-br from-blue-500 to-purple-600 rounded-full flex items-center justify-center text-white text-sm">
                                            <#if chat.otherUserRole == "ADMIN">
                                                <i class="fas fa-user-shield"></i>
                                            <#elseif chat.otherUserRole == "COSMETOLOGIST">
                                                <i class="fas fa-spa"></i>
                                            <#else>
                                                <i class="fas fa-user"></i>
                                            </#if>
                                        </div>
                                        <#if chat.hasUnreadMessages>
                                            <div class="absolute -top-1 -right-1 w-4 h-4 bg-red-500 rounded-full flex items-center justify-center">
                                                <span class="text-xs text-white font-bold">${chat.unreadCount}</span>
                                            </div>
                                        </#if>
                                    </div>

                                    <!-- Chat Info -->
                                    <div class="flex-1 min-w-0">
                                        <div class="flex items-center justify-between">
                                            <h4 class="text-sm font-medium text-gray-900 truncate">
                                                ${chat.otherUserName}
                                            </h4>
                                            <span class="text-xs text-gray-500">${chat.lastMessageTime}</span>
                                        </div>
                                        <p class="text-xs text-gray-600 truncate">
                                            <#if chat.isSystemChat()>
                                                <i class="fas fa-cog mr-1"></i>
                                                Системное уведомление
                                            <#else>
                                                <#if chat.lastMessageFromMe>
                                                    <span class="text-blue-600">Вы: </span>
                                                </#if>
                                                ${chat.lastMessage}
                                            </#if>
                                        </p>
                                    </div>
                                </div>
                            </#if>
                        </#list>
                    </div>

                    <!-- Actions -->
                    <div class="flex items-center justify-between pt-3 border-t border-gray-200">
                        <button onclick="openWriteAdminModal()" 
                                class="text-sm text-blue-600 hover:text-blue-800 flex items-center">
                            <i class="fas fa-plus mr-1"></i>
                            <#if currentUser.role == "COSMETOLOGIST">
                                Новое обращение
                            <#else>
                                Новое сообщение
                            </#if>
                        </button>
                        <a href="/messages" class="text-sm text-gray-600 hover:text-gray-800">
                            Показать все →
                        </a>
                    </div>
                </#if>
            </div>

            <!-- Favorites Widget -->
            <div class="bg-white shadow-md rounded-lg p-6">
                <div class="flex items-center justify-between mb-4">
                    <h2 class="text-lg font-semibold text-gray-800">Избранное</h2>
                    <a href="/favorites" class="text-blue-600 hover:text-blue-800">
                        <i class="fas fa-heart"></i>
                    </a>
                </div>

                <div class="text-center py-4">
                    <div class="text-3xl font-bold text-gray-800 mb-2">
                        0
                    </div>
                    <p class="text-sm text-gray-600">Товаров в избранном</p>
                </div>

                <div class="mt-4 pt-4 border-t">
                    <a href="/favorites" class="inline-flex items-center text-sm text-blue-600 hover:text-blue-800">
                        Просмотреть избранное
                        <i class="fas fa-chevron-right ml-1 text-xs"></i>
                    </a>
                </div>
            </div>
        </div>

        <!-- Recent Orders -->
        <div class="bg-white shadow-md rounded-lg p-6 mb-6">
            <div class="flex items-center justify-between mb-4">
                <h2 class="text-lg font-semibold text-gray-800">Последние заказы</h2>
                <a href="/dashboard/orders" class="text-blue-600 hover:text-blue-800 text-sm">
                    Все заказы
                </a>
            </div>

            <#if orders?size == 0>
                <div class="text-center py-8">
                    <div class="text-4xl text-gray-300 mb-3">
                        <i class="fas fa-shopping-cart"></i>
                    </div>
                    <p class="text-gray-500">У вас пока нет заказов</p>
                    <div class="mt-4">
                        <a href="/catalog" class="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md shadow-sm text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500">
                            Перейти в каталог
                        </a>
                    </div>
                </div>
            <#else>
                <div class="overflow-x-auto">
                    <table class="min-w-full divide-y divide-gray-200">
                        <thead class="bg-gray-50">
                            <tr>
                                <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                    № заказа
                                </th>
                                <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                    Дата
                                </th>
                                <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                    Сумма
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
                            <#list orders as order>
                                <#if order_index < 5> <!-- Show only 5 most recent orders -->
                                    <tr>
                                        <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
                                            ${order.orderNumber}
                                        </td>
                                        <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                                            ${order.createdAt}
                                        </td>
                                        <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                                            ${order.totalAmount} ₸
                                        </td>
                                        <td class="px-6 py-4 whitespace-nowrap">
                                            <#if order.status == "PENDING">
                                                <span class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full bg-yellow-100 text-yellow-800">
                                                    В обработке
                                                </span>
                                            <#elseif order.status == "CONFIRMED">
                                                <span class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full bg-blue-100 text-blue-800">
                                                    Подтвержден
                                                </span>
                                            <#elseif order.status == "SHIPPED">
                                                <span class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full bg-indigo-100 text-indigo-800">
                                                    Отправлен
                                                </span>
                                            <#elseif order.status == "DELIVERED">
                                                <span class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full bg-green-100 text-green-800">
                                                    Доставлен
                                                </span>
                                            <#elseif order.status == "CANCELLED">
                                                <span class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full bg-red-100 text-red-800">
                                                    Отменен
                                                </span>
                                            <#else>
                                                <span class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full bg-gray-100 text-gray-800">
                                                    ${order.status}
                                                </span>
                                            </#if>
                                        </td>
                                        <td class="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                                            <a href="/orders/${order.id}" class="text-blue-600 hover:text-blue-900">
                                                Детали
                                            </a>
                                        </td>
                                    </tr>
                                </#if>
                            </#list>
                        </tbody>
                    </table>
                </div>
            </#if>
        </div>

        <!-- Recommended Products -->
        <div class="bg-white shadow-md rounded-lg p-6">
            <div class="flex items-center justify-between mb-4">
                <h2 class="text-lg font-semibold text-gray-800">Рекомендуемые товары</h2>
                <a href="/catalog" class="text-blue-600 hover:text-blue-800 text-sm">
                    Все товары
                </a>
            </div>

            <div class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4">
                <!-- Placeholder for recommended products -->
                <div class="border rounded-lg p-4 text-center">
                    <div class="h-40 bg-gray-100 rounded-md flex items-center justify-center mb-3">
                        <i class="fas fa-image text-gray-400 text-4xl"></i>
                    </div>
                    <h3 class="font-medium text-gray-800 mb-1">Название товара</h3>
                    <p class="text-blue-600 font-bold">5,000 ₸</p>
                    <button class="mt-2 w-full px-3 py-1 bg-blue-600 text-white rounded-md hover:bg-blue-700 transition-colors text-sm">
                        В корзину
                    </button>
                </div>

                <div class="border rounded-lg p-4 text-center">
                    <div class="h-40 bg-gray-100 rounded-md flex items-center justify-center mb-3">
                        <i class="fas fa-image text-gray-400 text-4xl"></i>
                    </div>
                    <h3 class="font-medium text-gray-800 mb-1">Название товара</h3>
                    <p class="text-blue-600 font-bold">7,500 ₸</p>
                    <button class="mt-2 w-full px-3 py-1 bg-blue-600 text-white rounded-md hover:bg-blue-700 transition-colors text-sm">
                        В корзину
                    </button>
                </div>

                <div class="border rounded-lg p-4 text-center">
                    <div class="h-40 bg-gray-100 rounded-md flex items-center justify-center mb-3">
                        <i class="fas fa-image text-gray-400 text-4xl"></i>
                    </div>
                    <h3 class="font-medium text-gray-800 mb-1">Название товара</h3>
                    <p class="text-blue-600 font-bold">3,200 ₸</p>
                    <button class="mt-2 w-full px-3 py-1 bg-blue-600 text-white rounded-md hover:bg-blue-700 transition-colors text-sm">
                        В корзину
                    </button>
                </div>

                <div class="border rounded-lg p-4 text-center">
                    <div class="h-40 bg-gray-100 rounded-md flex items-center justify-center mb-3">
                        <i class="fas fa-image text-gray-400 text-4xl"></i>
                    </div>
                    <h3 class="font-medium text-gray-800 mb-1">Название товара</h3>
                    <p class="text-blue-600 font-bold">9,900 ₸</p>
                    <button class="mt-2 w-full px-3 py-1 bg-blue-600 text-white rounded-md hover:bg-blue-700 transition-colors text-sm">
                        В корзину
                    </button>
                </div>
            </div>
        </div>
    </div>
    <!-- Write to Admin Modal (компактная версия для дашборда) -->
    <div id="writeAdminModal" class="fixed inset-0 bg-gray-600 bg-opacity-50 hidden z-50">
        <div class="flex items-center justify-center min-h-screen p-4">
            <div class="bg-white rounded-lg shadow-xl max-w-md w-full">
                <div class="p-6">
                    <div class="flex items-center justify-between mb-4">
                        <h3 class="text-lg font-semibold text-gray-900">
                            <#if currentUser.role == "COSMETOLOGIST">
                                Обратиться в поддержку
                            <#else>
                                Написать администратору
                            </#if>
                        </h3>
                        <button onclick="closeWriteAdminModal()" class="text-gray-400 hover:text-gray-600">
                            <i class="fas fa-times"></i>
                        </button>
                    </div>

                    <div class="mb-4 p-3 bg-blue-50 rounded-lg">
                        <div class="flex items-start">
                            <i class="fas fa-info-circle text-blue-500 mt-0.5 mr-2"></i>
                            <div class="text-sm text-blue-800">
                                <#if currentUser.role == "COSMETOLOGIST">
                                    <p class="font-medium mb-1">Служба поддержки</p>
                                    <p>Ваше сообщение будет отправлено всем администраторам. Первый свободный администратор ответит вам.</p>
                                <#else>
                                    <p class="font-medium mb-1">Обращение к администратору</p>
                                    <p>Ваше сообщение будет отправлено администраторам для обработки.</p>
                                </#if>
                            </div>
                        </div>
                    </div>

                    <form id="writeAdminForm">
                        <div class="mb-4">
                            <label class="block text-sm font-medium text-gray-700 mb-2">Сообщение</label>
                            <textarea id="adminMessage"
                                    class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent resize-none"
                                    rows="4"
                                    placeholder="Опишите вашу проблему или вопрос..."
                                    maxlength="1000"
                                    required></textarea>
                            <div class="text-xs text-gray-500 mt-1">
                                <span id="charCount">0</span>/1000 символов
                            </div>
                        </div>

                        <!-- Quick templates for common requests -->
                        <#if currentUser.role == "COSMETOLOGIST">
                            <div class="mb-4">
                                <label class="block text-sm font-medium text-gray-700 mb-2">Быстрые шаблоны</label>
                                <div class="grid grid-cols-1 gap-2">
                                    <button type="button" onclick="useTemplate('verification')" 
                                            class="text-left p-2 text-xs border border-gray-200 rounded hover:bg-gray-50">
                                        Вопрос по верификации
                                    </button>
                                    <button type="button" onclick="useTemplate('access')" 
                                            class="text-left p-2 text-xs border border-gray-200 rounded hover:bg-gray-50">
                                        Проблемы с доступом к материалам
                                    </button>
                                    <button type="button" onclick="useTemplate('technical')" 
                                            class="text-left p-2 text-xs border border-gray-200 rounded hover:bg-gray-50">
                                        Техническая проблема
                                    </button>
                                </div>
                            </div>
                        </#if>

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

    <script>
        // Шаблоны сообщений для косметологов
        const messageTemplates = {
            verification: "Здравствуйте! У меня вопрос по поводу верификации моего аккаунта косметолога. ",
            access: "Здравствуйте! У меня проблемы с доступом к обучающим материалам. ",
            technical: "Здравствуйте! У меня возникла техническая проблема с сайтом. "
        };

        const adminMessageTextarea = document.getElementById('adminMessage');
        const charCountSpan = document.getElementById('charCount');

        // Обновление счетчика символов
        if (adminMessageTextarea) {
            adminMessageTextarea.addEventListener('input', function() {
                const length = this.value.length;
                charCountSpan.textContent = length;

                if (length > 1000) {
                    charCountSpan.classList.add('text-red-500');
                } else {
                    charCountSpan.classList.remove('text-red-500');
                }
            });
        }

        // Открытие модального окна
        function openWriteAdminModal() {
            document.getElementById('writeAdminModal').classList.remove('hidden');
            if (adminMessageTextarea) {
                adminMessageTextarea.focus();
            }
        }

        // Закрытие модального окна
        function closeWriteAdminModal() {
            document.getElementById('writeAdminModal').classList.add('hidden');
            document.getElementById('writeAdminForm').reset();
            charCountSpan.textContent = '0';
        }

        // Использование шаблона
        function useTemplate(templateKey) {
            if (messageTemplates[templateKey]) {
                adminMessageTextarea.value = messageTemplates[templateKey];
                adminMessageTextarea.dispatchEvent(new Event('input'));
                adminMessageTextarea.focus();
                adminMessageTextarea.setSelectionRange(adminMessageTextarea.value.length, adminMessageTextarea.value.length);
            }
        }

        // Открытие чата
        function openChat(chatId) {
            window.location.href = '/messages/chat/' + chatId;
        }

        // Обработка отправки формы
        if (document.getElementById('writeAdminForm')) {
            document.getElementById('writeAdminForm').addEventListener('submit', async function(e) {
                e.preventDefault();

                const content = adminMessageTextarea.value.trim();
                if (!content) {
                    showNotification('Пожалуйста, введите сообщение', 'error');
                    return;
                }

                if (content.length > 1000) {
                    showNotification('Сообщение слишком длинное', 'error');
                    return;
                }

                try {
                    // Получаем CSRF токен из мета-тегов
                    const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
                    const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

                    const response = await fetch('/messages/support', {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/x-www-form-urlencoded',
                            [csrfHeader]: csrfToken
                        },
                        body: 'content=' + encodeURIComponent(content)
                    });

                    const result = await response.json();

                    if (result.success) {
                        closeWriteAdminModal();
                        showNotification('Ваш запрос отправлен администраторам', 'success');

                        // Обновляем виджет сообщений через 2 секунды
                        setTimeout(() => {
                            window.location.reload();
                        }, 2000);
                    } else {
                        showNotification(result.error || 'Ошибка отправки сообщения', 'error');
                    }
                } catch (error) {
                    console.error('Error:', error);
                    showNotification('Ошибка отправки сообщения', 'error');
                }
            });
        }

        // Показ уведомлений
        function showNotification(message, type = 'info') {
            const notification = document.createElement('div');
            notification.className = "fixed top-4 right-4 z-50 px-6 py-3 rounded-lg shadow-lg text-white " + 
                (type === 'success' ? 'bg-green-500' : 
                type === 'error' ? 'bg-red-500' : 'bg-blue-500');
            notification.textContent = message;

            document.body.appendChild(notification);

            setTimeout(() => {
                notification.remove();
            }, 3000);
        }

        // Закрытие модального окна по клику вне его
        window.addEventListener('click', function(e) {
            const modal = document.getElementById('writeAdminModal');
            if (e.target === modal) {
                closeWriteAdminModal();
            }
        });
    </script>
</@layout>
