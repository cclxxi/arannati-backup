<#assign pageTitle = "Arannati - Управление продуктами">
<#assign pageDescription = "Управление продуктами в административной панели Arannati">
<#include "../layouts/main.ftlh">

<@layout>
    <div class="container mx-auto px-4 py-8">
        <!-- Page Header -->
        <div class="bg-white shadow-md rounded-lg p-6 mb-6">
            <div class="flex items-center justify-between mb-4">
                <h1 class="text-2xl font-bold text-gray-800">Управление продуктами</h1>
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
                            Здесь вы можете управлять продуктами, добавлять новые, редактировать существующие или удалять их.
                        </p>
                    </div>
                </div>
            </div>
        </div>

        <!-- Products Filter -->
        <div class="bg-white shadow-md rounded-lg p-6 mb-6">
            <div class="flex flex-wrap items-center justify-between mb-4">
                <h2 class="text-lg font-semibold text-gray-800">Фильтры</h2>
                <div class="flex space-x-2 mt-2 sm:mt-0">
                    <button id="filter-all" class="filter-btn active px-3 py-1 bg-blue-500 text-white rounded hover:bg-blue-600">
                        Все
                    </button>
                    <button id="filter-active" class="filter-btn px-3 py-1 bg-gray-200 text-gray-700 rounded hover:bg-gray-300">
                        Активные
                    </button>
                    <button id="filter-inactive" class="filter-btn px-3 py-1 bg-gray-200 text-gray-700 rounded hover:bg-gray-300">
                        Неактивные
                    </button>
                </div>
            </div>

            <div class="flex flex-wrap items-center space-y-2 sm:space-y-0">
                <div class="w-full sm:w-auto flex-1 sm:mr-4">
                    <input type="text" id="search-input" placeholder="Поиск по названию или описанию" class="w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500">
                </div>
                <div>
                    <button id="search-btn" class="px-4 py-2 bg-blue-500 text-white rounded-lg hover:bg-blue-600">
                        <i class="fas fa-search mr-2"></i> Поиск
                    </button>
                </div>
            </div>
        </div>

        <!-- Products Table -->
        <div class="bg-white shadow-md rounded-lg overflow-hidden mb-6">
            <div class="overflow-x-auto">
                <table class="min-w-full divide-y divide-gray-200">
                    <thead class="bg-gray-50">
                        <tr>
                            <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                Продукт
                            </th>
                            <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                Категория
                            </th>
                            <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                Цена
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
                        <#if products?size == 0>
                            <tr>
                                <td colspan="5" class="px-6 py-4 text-center text-gray-500">
                                    Продукты не найдены
                                </td>
                            </tr>
                        <#else>
                            <#list products as product>
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
                                        <#if product.active>
                                            <span class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full bg-green-100 text-green-800">
                                                Активный
                                            </span>
                                        <#else>
                                            <span class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full bg-red-100 text-red-800">
                                                Неактивный
                                            </span>
                                        </#if>
                                    </td>
                                    <td class="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                                        <a href="/admin/products/edit/${product.id}" class="text-blue-600 hover:text-blue-900 mr-3">
                                            <i class="fas fa-edit"></i>
                                        </a>
                                        <button class="text-red-600 hover:text-red-900 delete-product" data-id="${product.id}" data-name="${(product.name)!'Продукт'}">
                                            <i class="fas fa-trash"></i>
                                        </button>
                                    </td>
                                </tr>
                            </#list>
                        </#if>
                    </tbody>
                </table>
            </div>
        </div>

        <!-- Add Product Button -->
        <div class="fixed bottom-6 right-6">
            <a href="/admin/products/add" class="bg-blue-600 text-white rounded-full p-4 shadow-lg hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500">
                <i class="fas fa-plus"></i>
            </a>
        </div>
    </div>

    <script>
        document.addEventListener('DOMContentLoaded', function() {
            // Filter buttons
            const filterButtons = document.querySelectorAll('.filter-btn');
            const productRows = document.querySelectorAll('tbody tr');

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

                    const filterId = button.id;

                    // Show/hide products based on filter
                    productRows.forEach(row => {
                        if (filterId === 'filter-all') {
                            row.classList.remove('hidden');
                        } else if (filterId === 'filter-active') {
                            const isActive = row.querySelector('.bg-green-100') !== null;
                            row.classList.toggle('hidden', !isActive);
                        } else if (filterId === 'filter-inactive') {
                            const isInactive = row.querySelector('.bg-red-100') !== null;
                            row.classList.toggle('hidden', !isInactive);
                        }
                    });
                });
            });

            // Search functionality
            const searchInput = document.getElementById('search-input');
            const searchBtn = document.getElementById('search-btn');

            function performSearch() {
                const searchTerm = searchInput.value.toLowerCase();

                productRows.forEach(row => {
                    if (row.querySelector('td:first-child')) {
                        const productName = row.querySelector('td:first-child .text-gray-900').textContent.toLowerCase();
                        const productDesc = row.querySelector('td:first-child .text-gray-500').textContent.toLowerCase();

                        const matchesSearch = productName.includes(searchTerm) || productDesc.includes(searchTerm);

                        // Only hide if it matches the current filter
                        if (!row.classList.contains('hidden') || matchesSearch) {
                            row.classList.toggle('hidden', !matchesSearch);
                        }
                    }
                });
            }

            searchBtn.addEventListener('click', performSearch);
            searchInput.addEventListener('keyup', (e) => {
                if (e.key === 'Enter') {
                    performSearch();
                }
            });

            // Delete product
            document.querySelectorAll('.delete-product').forEach(button => {
                button.addEventListener('click', () => {
                    const productId = button.getAttribute('data-id');
                    const productName = button.getAttribute('data-name');

                    if (confirm(`Вы уверены, что хотите удалить продукт "${productName}"?`)) {
                        fetch(`/admin/products/${productId}/delete`, {
                            method: 'POST',
                            headers: {
                                'Content-Type': 'application/json',
                            }
                        })
                        .then(response => {
                            if (response.ok) {
                                alert('Продукт успешно удален');
                                // Reload the page to update the product list
                                window.location.reload();
                            } else {
                                alert('Ошибка при удалении продукта');
                            }
                        })
                        .catch(error => {
                            console.error('Error deleting product:', error);
                            alert('Ошибка при удалении продукта');
                        });
                    }
                });
            });
        });
    </script>
</@layout>
