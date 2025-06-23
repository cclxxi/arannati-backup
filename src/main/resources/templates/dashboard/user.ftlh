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
        <div class="grid grid-cols-1 md:grid-cols-3 gap-6 mb-6">
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
</@layout>
