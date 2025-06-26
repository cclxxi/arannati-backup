<#assign pageTitle = "Arannati - Управление списками желаний">
<#assign pageDescription = "Управление списками желаний в административной панели Arannati">
<#include "../layouts/main.ftlh">

<@layout>
    <div class="container mx-auto px-4 py-8">
        <!-- Page Header -->
        <div class="bg-white shadow-md rounded-lg p-6 mb-6">
            <div class="flex items-center justify-between mb-4">
                <h1 class="text-2xl font-bold text-gray-800">Управление списками желаний</h1>
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
                            Здесь вы можете просматривать списки желаний пользователей и анализировать популярные товары.
                        </p>
                    </div>
                </div>
            </div>
        </div>

        <!-- Wishlists Filter -->
        <div class="bg-white shadow-md rounded-lg p-6 mb-6">
            <div class="flex flex-wrap items-center justify-between mb-4">
                <h2 class="text-lg font-semibold text-gray-800">Фильтры</h2>
            </div>

            <div class="flex flex-wrap items-center space-y-2 sm:space-y-0">
                <div class="w-full sm:w-auto flex-1 sm:mr-4">
                    <input type="text" id="search-input" placeholder="Поиск по имени пользователя или email" class="w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500">
                </div>
                <div>
                    <button id="search-btn" class="px-4 py-2 bg-blue-500 text-white rounded-lg hover:bg-blue-600">
                        <i class="fas fa-search mr-2"></i> Поиск
                    </button>
                </div>
            </div>
        </div>

        <!-- Wishlists Table -->
        <div class="bg-white shadow-md rounded-lg overflow-hidden">
            <div class="overflow-x-auto">
                <table class="min-w-full divide-y divide-gray-200">
                    <thead class="bg-gray-50">
                        <tr>
                            <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                Пользователь
                            </th>
                            <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                Количество товаров
                            </th>
                            <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                Последнее обновление
                            </th>
                            <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                Общая стоимость
                            </th>
                            <th scope="col" class="relative px-6 py-3">
                                <span class="sr-only">Действия</span>
                            </th>
                        </tr>
                    </thead>
                    <tbody class="bg-white divide-y divide-gray-200">
                        <#if wishlists?size == 0>
                            <tr>
                                <td colspan="5" class="px-6 py-4 text-center text-gray-500">
                                    Списки желаний не найдены
                                </td>
                            </tr>
                        <#else>
                            <#list wishlists as wishlist>
                                <tr>
                                    <td class="px-6 py-4 whitespace-nowrap">
                                        <div class="flex items-center">
                                            <div class="flex-shrink-0 h-10 w-10 bg-gray-200 rounded-full flex items-center justify-center">
                                                <i class="fas fa-user text-gray-500"></i>
                                            </div>
                                            <div class="ml-4">
                                                <div class="text-sm font-medium text-gray-900">
                                                    ${wishlist.userName}
                                                </div>
                                                <div class="text-sm text-gray-500">
                                                    ${wishlist.userEmail}
                                                </div>
                                            </div>
                                        </div>
                                    </td>
                                    <td class="px-6 py-4 whitespace-nowrap">
                                        <div class="text-sm text-gray-900">${wishlist.itemCount} товаров</div>
                                    </td>
                                    <td class="px-6 py-4 whitespace-nowrap">
                                        <div class="text-sm text-gray-900">${wishlist.lastUpdated}</div>
                                    </td>
                                    <td class="px-6 py-4 whitespace-nowrap">
                                        <div class="text-sm text-gray-900">${wishlist.totalValue} ₸</div>
                                    </td>
                                    <td class="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                                        <a href="/admin/wishlists/${wishlist.id}" class="text-blue-600 hover:text-blue-900">
                                            <i class="fas fa-eye mr-1"></i> Просмотр
                                        </a>
                                    </td>
                                </tr>
                            </#list>
                        </#if>
                    </tbody>
                </table>
            </div>
        </div>

        <!-- Popular Products in Wishlists -->
        <div class="bg-white shadow-md rounded-lg p-6 mt-6">
            <h2 class="text-lg font-semibold text-gray-800 mb-4">Популярные товары в списках желаний</h2>

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
                                Количество списков
                            </th>
                        </tr>
                    </thead>
                    <tbody class="bg-white divide-y divide-gray-200">
                        <#if popularProducts?size == 0>
                            <tr>
                                <td colspan="4" class="px-6 py-4 text-center text-gray-500">
                                    Нет данных о популярных товарах
                                </td>
                            </tr>
                        <#else>
                            <#list popularProducts as product>
                                <tr>
                                    <td class="px-6 py-4 whitespace-nowrap">
                                        <div class="flex items-center">
                                            <div class="flex-shrink-0 h-10 w-10">
                                                <#if product.imageUrl?? && product.imageUrl != "">
                                                    <img class="h-10 w-10 rounded-full object-cover" src="${product.imageUrl}" alt="${(product.name)!'Продукт'}">
                                                <#else>
                                                    <div class="h-10 w-10 rounded-full bg-gray-200 flex items-center justify-center">
                                                        <i class="fas fa-box text-gray-500"></i>
                                                    </div>
                                                </#if>
                                            </div>
                                            <div class="ml-4">
                                                <div class="text-sm font-medium text-gray-900">
                                                    ${(product.name)!'Без названия'}
                                                </div>
                                                <div class="text-sm text-gray-500 max-w-xs truncate">
                                                    ${(product.description)!'Нет описания'}
                                                </div>
                                            </div>
                                        </div>
                                    </td>
                                    <td class="px-6 py-4 whitespace-nowrap">
                                        <div class="text-sm text-gray-900">${(product.category)!'Не указана'}</div>
                                    </td>
                                    <td class="px-6 py-4 whitespace-nowrap">
                                        <div class="text-sm text-gray-900">${(product.price)!0} ₸</div>
                                    </td>
                                    <td class="px-6 py-4 whitespace-nowrap">
                                        <div class="text-sm text-gray-900">${(product.wishlistCount)!0}</div>
                                        <div class="w-full bg-gray-200 rounded-full h-2.5 mt-1">
                                            <div class="bg-blue-600 h-2.5 rounded-full" style="width: ${(product.percentage)!0}%"></div>
                                        </div>
                                    </td>
                                </tr>
                            </#list>
                        </#if>
                    </tbody>
                </table>
            </div>
        </div>
    </div>

    <script>
        document.addEventListener('DOMContentLoaded', function() {
            // Search functionality
            const searchInput = document.getElementById('search-input');
            const searchBtn = document.getElementById('search-btn');
            const wishlistRows = document.querySelectorAll('tbody tr');

            function performSearch() {
                const searchTerm = searchInput.value.toLowerCase();

                // Only search in the first table (wishlists)
                const firstTableRows = Array.from(wishlistRows).filter(row => 
                    row.closest('table') === document.querySelector('table')
                );

                firstTableRows.forEach(row => {
                    if (row.querySelector('td:first-child')) {
                        const userName = row.querySelector('td:first-child .text-gray-900').textContent.toLowerCase();
                        const userEmail = row.querySelector('td:first-child .text-gray-500').textContent.toLowerCase();

                        const matchesSearch = userName.includes(searchTerm) || userEmail.includes(searchTerm);

                        row.classList.toggle('hidden', !matchesSearch);
                    }
                });
            }

            searchBtn.addEventListener('click', performSearch);
            searchInput.addEventListener('keyup', (e) => {
                if (e.key === 'Enter') {
                    performSearch();
                }
            });
        });
    </script>
</@layout>
