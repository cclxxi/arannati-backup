<#assign pageTitle = "Arannati - Детали списка желаний">
<#assign pageDescription = "Просмотр деталей списка желаний в административной панели Arannati">
<#include "../layouts/main.ftlh">

<@layout>
    <div class="container mx-auto px-4 py-8">
        <!-- Page Header -->
        <div class="bg-white shadow-md rounded-lg p-6 mb-6">
            <div class="flex items-center justify-between mb-4">
                <h1 class="text-2xl font-bold text-gray-800">Список желаний пользователя ${wishlist.userName}</h1>
                <a href="/admin/wishlists" class="px-4 py-2 bg-gray-200 text-gray-700 rounded hover:bg-gray-300">
                    <i class="fas fa-arrow-left mr-2"></i> Назад к спискам желаний
                </a>
            </div>
            
            <div class="bg-blue-50 border-l-4 border-blue-500 p-4">
                <div class="flex">
                    <div class="flex-shrink-0">
                        <i class="fas fa-info-circle text-blue-500"></i>
                    </div>
                    <div class="ml-3">
                        <p class="text-sm text-blue-700">
                            Здесь вы можете просмотреть детали списка желаний пользователя и товары, которые он добавил.
                        </p>
                    </div>
                </div>
            </div>
        </div>
        
        <!-- User Information -->
        <div class="bg-white shadow-md rounded-lg p-6 mb-6">
            <h2 class="text-lg font-semibold text-gray-800 mb-4">Информация о пользователе</h2>
            
            <div class="flex items-center mb-6">
                <div class="flex-shrink-0 h-16 w-16 bg-gray-200 rounded-full flex items-center justify-center">
                    <i class="fas fa-user text-gray-500 text-2xl"></i>
                </div>
                <div class="ml-4">
                    <h3 class="text-lg font-medium text-gray-900">${wishlist.userName}</h3>
                    <p class="text-sm text-gray-500">${wishlist.userEmail}</p>
                </div>
            </div>
            
            <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
                <div class="bg-gray-50 p-4 rounded-lg">
                    <div class="flex items-center">
                        <div class="flex-shrink-0 h-10 w-10 bg-blue-100 rounded-full flex items-center justify-center">
                            <i class="fas fa-shopping-bag text-blue-500"></i>
                        </div>
                        <div class="ml-3">
                            <p class="text-sm text-gray-500">Товаров в списке</p>
                            <p class="text-lg font-semibold text-gray-900">${wishlist.itemCount}</p>
                        </div>
                    </div>
                </div>
                
                <div class="bg-gray-50 p-4 rounded-lg">
                    <div class="flex items-center">
                        <div class="flex-shrink-0 h-10 w-10 bg-green-100 rounded-full flex items-center justify-center">
                            <i class="fas fa-money-bill-wave text-green-500"></i>
                        </div>
                        <div class="ml-3">
                            <p class="text-sm text-gray-500">Общая стоимость</p>
                            <p class="text-lg font-semibold text-gray-900">${wishlist.totalValue} ₸</p>
                        </div>
                    </div>
                </div>
                
                <div class="bg-gray-50 p-4 rounded-lg">
                    <div class="flex items-center">
                        <div class="flex-shrink-0 h-10 w-10 bg-purple-100 rounded-full flex items-center justify-center">
                            <i class="fas fa-clock text-purple-500"></i>
                        </div>
                        <div class="ml-3">
                            <p class="text-sm text-gray-500">Последнее обновление</p>
                            <p class="text-lg font-semibold text-gray-900">${wishlist.lastUpdated}</p>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        
        <!-- Wishlist Items -->
        <div class="bg-white shadow-md rounded-lg overflow-hidden">
            <div class="px-6 py-4 border-b border-gray-200">
                <h2 class="text-lg font-semibold text-gray-800">Товары в списке желаний</h2>
            </div>
            <div class="overflow-x-auto">
                <table class="min-w-full divide-y divide-gray-200">
                    <thead class="bg-gray-50">
                        <tr>
                            <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                Товар
                            </th>
                            <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                Категория
                            </th>
                            <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                Цена
                            </th>
                            <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                Дата добавления
                            </th>
                            <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                Статус
                            </th>
                        </tr>
                    </thead>
                    <tbody class="bg-white divide-y divide-gray-200">
                        <#if wishlist.items?size == 0>
                            <tr>
                                <td colspan="5" class="px-6 py-4 text-center text-gray-500">
                                    В списке желаний нет товаров
                                </td>
                            </tr>
                        <#else>
                            <#list wishlist.items as item>
                                <tr>
                                    <td class="px-6 py-4 whitespace-nowrap">
                                        <div class="flex items-center">
                                            <div class="flex-shrink-0 h-10 w-10">
                                                <#if item.imageUrl?? && item.imageUrl != "">
                                                    <img class="h-10 w-10 rounded-full object-cover" src="${item.imageUrl}" alt="${item.productName}">
                                                <#else>
                                                    <div class="h-10 w-10 rounded-full bg-gray-200 flex items-center justify-center">
                                                        <i class="fas fa-box text-gray-500"></i>
                                                    </div>
                                                </#if>
                                            </div>
                                            <div class="ml-4">
                                                <div class="text-sm font-medium text-gray-900">
                                                    ${item.productName}
                                                </div>
                                                <div class="text-sm text-gray-500 max-w-xs truncate">
                                                    ${item.productDescription}
                                                </div>
                                            </div>
                                        </div>
                                    </td>
                                    <td class="px-6 py-4 whitespace-nowrap">
                                        <div class="text-sm text-gray-900">${item.category}</div>
                                    </td>
                                    <td class="px-6 py-4 whitespace-nowrap">
                                        <div class="text-sm text-gray-900">${item.price} ₸</div>
                                    </td>
                                    <td class="px-6 py-4 whitespace-nowrap">
                                        <div class="text-sm text-gray-900">${item.addedDate}</div>
                                    </td>
                                    <td class="px-6 py-4 whitespace-nowrap">
                                        <#if item.inStock>
                                            <span class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full bg-green-100 text-green-800">
                                                В наличии
                                            </span>
                                        <#else>
                                            <span class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full bg-red-100 text-red-800">
                                                Нет в наличии
                                            </span>
                                        </#if>
                                    </td>
                                </tr>
                            </#list>
                        </#if>
                    </tbody>
                </table>
            </div>
        </div>
        
        <!-- User Activity -->
        <div class="bg-white shadow-md rounded-lg p-6 mt-6">
            <h2 class="text-lg font-semibold text-gray-800 mb-4">Активность пользователя</h2>
            
            <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
                <div>
                    <h3 class="text-md font-medium text-gray-700 mb-2">Последние заказы</h3>
                    <#if userOrders?size == 0>
                        <div class="bg-gray-50 p-4 rounded-lg">
                            <p class="text-sm text-gray-500 italic">У пользователя нет заказов</p>
                        </div>
                    <#else>
                        <div class="bg-gray-50 p-4 rounded-lg">
                            <ul class="divide-y divide-gray-200">
                                <#list userOrders as order>
                                    <li class="py-3 flex justify-between">
                                        <div>
                                            <p class="text-sm font-medium text-gray-900">Заказ #${order.orderNumber}</p>
                                            <p class="text-sm text-gray-500">${order.orderDate}</p>
                                        </div>
                                        <div class="text-right">
                                            <p class="text-sm font-medium text-gray-900">${order.totalAmount} ₸</p>
                                            <p class="text-sm text-gray-500">${order.status}</p>
                                        </div>
                                    </li>
                                </#list>
                            </ul>
                            <div class="mt-3 text-right">
                                <a href="/admin/users/${wishlist.userId}" class="text-sm text-blue-600 hover:text-blue-800">
                                    Просмотреть профиль пользователя <i class="fas fa-arrow-right ml-1"></i>
                                </a>
                            </div>
                        </div>
                    </#if>
                </div>
                
                <div>
                    <h3 class="text-md font-medium text-gray-700 mb-2">Статистика</h3>
                    <div class="bg-gray-50 p-4 rounded-lg">
                        <div class="grid grid-cols-2 gap-4">
                            <div>
                                <p class="text-sm text-gray-500">Всего заказов</p>
                                <p class="text-lg font-semibold text-gray-900">${userStats.totalOrders}</p>
                            </div>
                            <div>
                                <p class="text-sm text-gray-500">Сумма всех заказов</p>
                                <p class="text-lg font-semibold text-gray-900">${userStats.totalSpent} ₸</p>
                            </div>
                            <div>
                                <p class="text-sm text-gray-500">Дата регистрации</p>
                                <p class="text-lg font-semibold text-gray-900">${userStats.registrationDate}</p>
                            </div>
                            <div>
                                <p class="text-sm text-gray-500">Последний вход</p>
                                <p class="text-lg font-semibold text-gray-900">${userStats.lastLogin}</p>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</@layout>