<#assign pageTitle = "Arannati - Панель администратора">
<#assign pageDescription = "Административная панель Arannati">
<#include "../layouts/main.ftlh">

<@layout>
    <div class="container mx-auto px-4 py-8">
        <!-- Welcome Section -->
        <div class="bg-white shadow-md rounded-lg p-6 mb-6">
            <div class="flex items-center justify-between mb-4">
                <h1 class="text-2xl font-bold text-gray-800">Панель администратора</h1>
                <span class="px-3 py-1 bg-red-100 text-red-800 rounded-full text-sm font-medium">
                    Администратор
                </span>
            </div>

            <div class="bg-blue-50 border-l-4 border-blue-500 p-4">
                <div class="flex">
                    <div class="flex-shrink-0">
                        <i class="fas fa-user-shield text-blue-500"></i>
                    </div>
                    <div class="ml-3">
                        <p class="text-sm text-blue-700">
                            Добро пожаловать, <span class="font-semibold">${user.firstName} ${user.lastName}</span>!
                            Здесь вы можете управлять всеми аспектами сайта.
                        </p>
                    </div>
                </div>
            </div>
        </div>

        <!-- Stats Overview -->
        <div class="grid grid-cols-1 md:grid-cols-4 gap-6 mb-6">
            <!-- Users Stat -->
            <div class="bg-white shadow-md rounded-lg p-6">
                <div class="flex items-center justify-between mb-2">
                    <h2 class="text-lg font-semibold text-gray-800">Пользователи</h2>
                    <div class="bg-blue-100 p-2 rounded-full">
                        <i class="fas fa-users text-blue-600"></i>
                    </div>
                </div>

                <div class="text-3xl font-bold text-gray-800 mb-1">
                    ${users?size}
                </div>
                <p class="text-sm text-gray-600">Всего пользователей</p>

                <div class="mt-4 pt-4 border-t">
                    <a href="/admin/users" class="inline-flex items-center text-sm text-blue-600 hover:text-blue-800">
                        Управление пользователями
                        <i class="fas fa-chevron-right ml-1 text-xs"></i>
                    </a>
                </div>
            </div>

            <!-- Orders Stat -->
            <div class="bg-white shadow-md rounded-lg p-6">
                <div class="flex items-center justify-between mb-2">
                    <h2 class="text-lg font-semibold text-gray-800">Заказы</h2>
                    <div class="bg-green-100 p-2 rounded-full">
                        <i class="fas fa-shopping-cart text-green-600"></i>
                    </div>
                </div>

                <div class="text-3xl font-bold text-gray-800 mb-1">
                    ${orders?size}
                </div>
                <p class="text-sm text-gray-600">Всего заказов</p>

                <div class="mt-4 pt-4 border-t">
                    <a href="/admin/orders" class="inline-flex items-center text-sm text-blue-600 hover:text-blue-800">
                        Управление заказами
                        <i class="fas fa-chevron-right ml-1 text-xs"></i>
                    </a>
                </div>
            </div>

            <!-- Products Stat -->
            <div class="bg-white shadow-md rounded-lg p-6">
                <div class="flex items-center justify-between mb-2">
                    <h2 class="text-lg font-semibold text-gray-800">Товары</h2>
                    <div class="bg-purple-100 p-2 rounded-full">
                        <i class="fas fa-box text-purple-600"></i>
                    </div>
                </div>

                <div class="text-3xl font-bold text-gray-800 mb-1">
                    256
                </div>
                <p class="text-sm text-gray-600">Всего товаров</p>

                <div class="mt-4 pt-4 border-t">
                    <a href="/admin/products" class="inline-flex items-center text-sm text-blue-600 hover:text-blue-800">
                        Управление товарами
                        <i class="fas fa-chevron-right ml-1 text-xs"></i>
                    </a>
                </div>
            </div>

            <!-- Revenue Stat -->
            <div class="bg-white shadow-md rounded-lg p-6">
                <div class="flex items-center justify-between mb-2">
                    <h2 class="text-lg font-semibold text-gray-800">Доход</h2>
                    <div class="bg-yellow-100 p-2 rounded-full">
                        <i class="fas fa-money-bill-wave text-yellow-600"></i>
                    </div>
                </div>

                <div class="text-3xl font-bold text-gray-800 mb-1">
                    1,250,000 ₸
                </div>
                <p class="text-sm text-gray-600">Общий доход</p>

                <div class="mt-4 pt-4 border-t">
                    <a href="/admin/reports" class="inline-flex items-center text-sm text-blue-600 hover:text-blue-800">
                        Финансовые отчеты
                        <i class="fas fa-chevron-right ml-1 text-xs"></i>
                    </a>
                </div>
            </div>
        </div>

        <!-- Quick Actions -->
        <div class="bg-white shadow-md rounded-lg p-6 mb-6">
            <h2 class="text-lg font-semibold text-gray-800 mb-4">Быстрые действия</h2>

            <div class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-5 gap-4">
                <a href="/admin/products/create" class="border rounded-lg p-4 hover:bg-gray-50 transition-colors flex items-center">
                    <div class="bg-blue-100 p-2 rounded-full mr-3">
                        <i class="fas fa-plus text-blue-600"></i>
                    </div>
                    <div>
                        <h3 class="font-medium text-gray-800">Добавить товар</h3>
                        <p class="text-xs text-gray-500">Создать новый товар</p>
                    </div>
                </a>

                <a href="/admin/cosmetologists/verify" class="border rounded-lg p-4 hover:bg-gray-50 transition-colors flex items-center">
                    <div class="bg-green-100 p-2 rounded-full mr-3 relative">
                        <i class="fas fa-check text-green-600"></i>
                        <#if pendingCosmetologists?? && pendingCosmetologists?size gt 0>
                            <span class="absolute -top-1 -right-1 bg-red-500 text-white text-xs rounded-full h-4 w-4 flex items-center justify-center">
                                ${pendingCosmetologists?size}
                            </span>
                        </#if>
                    </div>
                    <div>
                        <h3 class="font-medium text-gray-800">Верификация</h3>
                        <p class="text-xs text-gray-500">
                            <#if pendingCosmetologists?? && pendingCosmetologists?size gt 0>
                                ${pendingCosmetologists?size} ожидающих
                            <#else>
                                Проверить косметологов
                            </#if>
                        </p>
                    </div>
                </a>

                <a href="/admin/orders/pending" class="border rounded-lg p-4 hover:bg-gray-50 transition-colors flex items-center">
                    <div class="bg-yellow-100 p-2 rounded-full mr-3">
                        <i class="fas fa-clock text-yellow-600"></i>
                    </div>
                    <div>
                        <h3 class="font-medium text-gray-800">Новые заказы</h3>
                        <p class="text-xs text-gray-500">Обработать заказы</p>
                    </div>
                </a>

                <a href="/admin/materials/upload" class="border rounded-lg p-4 hover:bg-gray-50 transition-colors flex items-center">
                    <div class="bg-purple-100 p-2 rounded-full mr-3">
                        <i class="fas fa-upload text-purple-600"></i>
                    </div>
                    <div>
                        <h3 class="font-medium text-gray-800">Загрузить</h3>
                        <p class="text-xs text-gray-500">Добавить материалы</p>
                    </div>
                </a>

                <a href="/admin/messages" class="border rounded-lg p-4 hover:bg-gray-50 transition-colors flex items-center">
                    <div class="bg-red-100 p-2 rounded-full mr-3 relative">
                        <i class="fas fa-envelope text-red-600"></i>
                        <#if unreadMessageCount?? && unreadMessageCount gt 0>
                            <span class="absolute -top-1 -right-1 bg-red-500 text-white text-xs rounded-full h-4 w-4 flex items-center justify-center">
                                ${unreadMessageCount}
                            </span>
                        </#if>
                    </div>
                    <div>
                        <h3 class="font-medium text-gray-800">Сообщения</h3>
                        <p class="text-xs text-gray-500">
                            <#if unreadMessageCount?? && unreadMessageCount gt 0>
                                ${unreadMessageCount} новых
                            <#else>
                                Управление сообщениями
                            </#if>
                        </p>
                    </div>
                </a>
            </div>
        </div>

        <!-- Activity Summary -->
        <div class="bg-white shadow-md rounded-lg p-6 mb-6">
            <div class="flex items-center justify-between mb-4">
                <h2 class="text-lg font-semibold text-gray-800">Сводка активности</h2>
                <div class="text-sm text-gray-500">
                    Последние обновления
                </div>
            </div>

            <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
                <!-- Pending Verifications Summary -->
                <div class="border rounded-lg p-4">
                    <div class="flex items-center justify-between mb-3">
                        <h3 class="font-medium text-gray-800">Ожидающие верификации</h3>
                        <a href="/admin/cosmetologists/verify" class="text-blue-600 hover:text-blue-800 text-sm">
                            Все заявки
                        </a>
                    </div>

                    <#if pendingCosmetologists?? && pendingCosmetologists?size gt 0>
                        <div class="space-y-3">
                            <div class="flex items-center justify-between">
                                <div class="text-sm text-gray-600">Всего ожидающих:</div>
                                <div class="font-medium text-gray-900">${pendingCosmetologists?size}</div>
                            </div>
                            <div class="flex items-center justify-between">
                                <div class="text-sm text-gray-600">Последняя заявка:</div>
                                <div class="text-sm text-gray-900">
                                    <#if pendingCosmetologists?size gt 0>
                                        ${pendingCosmetologists[0].firstName} ${pendingCosmetologists[0].lastName}
                                    <#else>
                                        -
                                    </#if>
                                </div>
                            </div>
                        </div>
                        <div class="mt-4 pt-3 border-t">
                            <a href="/admin/cosmetologists/verify" class="inline-flex items-center justify-center w-full px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 transition-colors text-sm">
                                <i class="fas fa-check-circle mr-2"></i> Перейти к верификации
                            </a>
                        </div>
                    <#else>
                        <div class="py-6 text-center">
                            <div class="text-3xl text-gray-300 mb-2">
                                <i class="fas fa-check-circle"></i>
                            </div>
                            <p class="text-gray-500">Нет ожидающих верификации</p>
                        </div>
                    </#if>
                </div>

                <!-- Recent Orders Summary -->
                <div class="border rounded-lg p-4">
                    <div class="flex items-center justify-between mb-3">
                        <h3 class="font-medium text-gray-800">Последние заказы</h3>
                        <a href="/admin/orders" class="text-blue-600 hover:text-blue-800 text-sm">
                            Все заказы
                        </a>
                    </div>

                    <#if orders?? && orders?size gt 0>
                        <div class="space-y-3">
                            <div class="flex items-center justify-between">
                                <div class="text-sm text-gray-600">Всего заказов:</div>
                                <div class="font-medium text-gray-900">${orders?size}</div>
                            </div>
                            <div class="flex items-center justify-between">
                                <div class="text-sm text-gray-600">Последний заказ:</div>
                                <div class="text-sm text-gray-900">
                                    <#if orders?size gt 0>
                                        ${orders[0].orderNumber} (${orders[0].totalAmount} ₸)
                                    <#else>
                                        -
                                    </#if>
                                </div>
                            </div>
                        </div>
                        <div class="mt-4 pt-3 border-t">
                            <a href="/admin/orders/pending" class="inline-flex items-center justify-center w-full px-4 py-2 bg-yellow-600 text-white rounded-md hover:bg-yellow-700 transition-colors text-sm">
                                <i class="fas fa-clock mr-2"></i> Обработать новые заказы
                            </a>
                        </div>
                    <#else>
                        <div class="py-6 text-center">
                            <div class="text-3xl text-gray-300 mb-2">
                                <i class="fas fa-shopping-cart"></i>
                            </div>
                            <p class="text-gray-500">Нет заказов</p>
                        </div>
                    </#if>
                </div>

                <!-- Messages Summary -->
                <div class="border rounded-lg p-4">
                    <div class="flex items-center justify-between mb-3">
                        <h3 class="font-medium text-gray-800">Сообщения</h3>
                        <a href="/admin/messages" class="text-blue-600 hover:text-blue-800 text-sm">
                            Все сообщения
                        </a>
                    </div>

                    <#if unreadMessageCount?? && unreadMessageCount gt 0>
                        <div class="space-y-3">
                            <div class="flex items-center justify-between">
                                <div class="text-sm text-gray-600">Непрочитанных:</div>
                                <div class="font-medium text-gray-900">${unreadMessageCount}</div>
                            </div>
                            <div class="flex items-center justify-between">
                                <div class="text-sm text-gray-600">Всего сообщений:</div>
                                <div class="text-sm text-gray-900">
                                    <#if receivedMessages??>
                                        ${receivedMessages?size}
                                    <#else>
                                        0
                                    </#if>
                                </div>
                            </div>
                        </div>
                        <div class="mt-4 pt-3 border-t">
                            <a href="/admin/messages" class="inline-flex items-center justify-center w-full px-4 py-2 bg-red-600 text-white rounded-md hover:bg-red-700 transition-colors text-sm">
                                <i class="fas fa-envelope mr-2"></i> Просмотреть сообщения
                            </a>
                        </div>
                    <#else>
                        <div class="py-6 text-center">
                            <div class="text-3xl text-gray-300 mb-2">
                                <i class="fas fa-envelope"></i>
                            </div>
                            <p class="text-gray-500">Нет новых сообщений</p>
                        </div>
                    </#if>
                </div>
            </div>
        </div>

        <!-- Recent Orders -->
        <div class="bg-white shadow-md rounded-lg p-6 mb-6">
            <div class="flex items-center justify-between mb-4">
                <h2 class="text-lg font-semibold text-gray-800">Последние заказы</h2>
                <a href="/admin/orders" class="text-blue-600 hover:text-blue-800 text-sm">
                    Все заказы
                </a>
            </div>

            <div class="overflow-x-auto">
                <table class="min-w-full divide-y divide-gray-200">
                    <thead class="bg-gray-50">
                        <tr>
                            <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                № заказа
                            </th>
                            <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                Клиент
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
                                    <td class="px-6 py-4 whitespace-nowrap">
                                        <div class="text-sm text-gray-900">${order.customerName}</div>
                                        <div class="text-sm text-gray-500">${order.customerEmail}</div>
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
                                        <a href="/admin/orders/${order.id}" class="text-blue-600 hover:text-blue-900">
                                            Детали
                                        </a>
                                    </td>
                                </tr>
                            </#if>
                        </#list>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</@layout>
