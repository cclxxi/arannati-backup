<#assign pageTitle = "Arannati - Управление заказами">
<#assign pageDescription = "Управление заказами в административной панели Arannati">
<#include "../layouts/main.ftlh">

<@layout>
    <div class="container mx-auto px-4 py-8">
        <!-- Page Header -->
        <div class="bg-white shadow-md rounded-lg p-6 mb-6">
            <div class="flex items-center justify-between mb-4">
                <h1 class="text-2xl font-bold text-gray-800">Управление заказами</h1>
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
                            Здесь вы можете просматривать и управлять заказами клиентов.
                        </p>
                    </div>
                </div>
            </div>
        </div>

        <!-- Orders Filter -->
        <div class="bg-white shadow-md rounded-lg p-6 mb-6">
            <div class="flex flex-wrap items-center justify-between mb-4">
                <h2 class="text-lg font-semibold text-gray-800">Фильтры</h2>
                <div class="flex space-x-2 mt-2 sm:mt-0">
                    <button id="filter-all" class="filter-btn <#if !activeFilter?? || activeFilter != 'pending'>active px-3 py-1 bg-blue-500 text-white<#else>px-3 py-1 bg-gray-200 text-gray-700</#if> rounded hover:bg-blue-600">
                        Все
                    </button>
                    <button id="filter-pending" class="filter-btn <#if activeFilter?? && activeFilter == 'pending'>active px-3 py-1 bg-blue-500 text-white<#else>px-3 py-1 bg-gray-200 text-gray-700</#if> rounded hover:bg-gray-300">
                        Ожидающие
                    </button>
                    <button id="filter-processing" class="filter-btn px-3 py-1 bg-gray-200 text-gray-700 rounded hover:bg-gray-300">
                        В обработке
                    </button>
                    <button id="filter-shipped" class="filter-btn px-3 py-1 bg-gray-200 text-gray-700 rounded hover:bg-gray-300">
                        Отправленные
                    </button>
                    <button id="filter-delivered" class="filter-btn px-3 py-1 bg-gray-200 text-gray-700 rounded hover:bg-gray-300">
                        Доставленные
                    </button>
                    <button id="filter-cancelled" class="filter-btn px-3 py-1 bg-gray-200 text-gray-700 rounded hover:bg-gray-300">
                        Отмененные
                    </button>
                </div>
            </div>

            <div class="flex flex-wrap items-center space-y-2 sm:space-y-0">
                <div class="w-full sm:w-auto flex-1 sm:mr-4">
                    <input type="text" id="search-input" placeholder="Поиск по номеру заказа или имени клиента" class="w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500">
                </div>
                <div>
                    <button id="search-btn" class="px-4 py-2 bg-blue-500 text-white rounded-lg hover:bg-blue-600">
                        <i class="fas fa-search mr-2"></i> Поиск
                    </button>
                </div>
            </div>
        </div>

        <!-- Orders Table -->
        <div class="bg-white shadow-md rounded-lg overflow-hidden">
            <div class="overflow-x-auto">
                <table class="min-w-full divide-y divide-gray-200">
                    <thead class="bg-gray-50">
                        <tr>
                            <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                Заказ
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
                        <#if orders?size == 0>
                            <tr>
                                <td colspan="6" class="px-6 py-4 text-center text-gray-500">
                                    Заказы не найдены
                                </td>
                            </tr>
                        <#else>
                            <#list orders as order>
                                <tr>
                                    <td class="px-6 py-4 whitespace-nowrap">
                                        <div class="text-sm font-medium text-gray-900">
                                            #${order.orderNumber}
                                        </div>
                                        <div class="text-sm text-gray-500">
                                            ${order.items?size} товаров
                                        </div>
                                    </td>
                                    <td class="px-6 py-4 whitespace-nowrap">
                                        <div class="flex items-center">
                                            <div class="flex-shrink-0 h-10 w-10 bg-gray-200 rounded-full flex items-center justify-center">
                                                <i class="fas fa-user text-gray-500"></i>
                                            </div>
                                            <div class="ml-4">
                                                <div class="text-sm font-medium text-gray-900">
                                                    ${order.customerName}
                                                </div>
                                                <div class="text-sm text-gray-500">
                                                    ${order.customerEmail}
                                                </div>
                                            </div>
                                        </div>
                                    </td>
                                    <td class="px-6 py-4 whitespace-nowrap">
                                        <div class="text-sm text-gray-900">${order.orderDate}</div>
                                    </td>
                                    <td class="px-6 py-4 whitespace-nowrap">
                                        <div class="text-sm text-gray-900">${order.totalAmount} ₸</div>
                                    </td>
                                    <td class="px-6 py-4 whitespace-nowrap">
                                        <#if order.status == "PENDING">
                                            <span class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full bg-yellow-100 text-yellow-800">
                                                Ожидает
                                            </span>
                                        <#elseif order.status == "PROCESSING">
                                            <span class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full bg-blue-100 text-blue-800">
                                                В обработке
                                            </span>
                                        <#elseif order.status == "SHIPPED">
                                            <span class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full bg-purple-100 text-purple-800">
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
                                        <a href="/admin/orders/${order.id}" class="text-blue-600 hover:text-blue-900 mr-3">
                                            <i class="fas fa-eye"></i>
                                        </a>
                                        <button class="text-red-600 hover:text-red-900 cancel-order" data-id="${order.id}" data-number="${order.orderNumber}" <#if order.status == "CANCELLED" || order.status == "DELIVERED">disabled</#if>>
                                            <i class="fas fa-times-circle"></i>
                                        </button>
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
            // Filter buttons
            const filterButtons = document.querySelectorAll('.filter-btn');
            const orderRows = document.querySelectorAll('tbody tr');

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

                    // Show/hide orders based on filter
                    orderRows.forEach(row => {
                        if (filterId === 'filter-all') {
                            row.classList.remove('hidden');
                        } else if (filterId === 'filter-pending') {
                            const isPending = row.querySelector('.bg-yellow-100') !== null;
                            row.classList.toggle('hidden', !isPending);
                        } else if (filterId === 'filter-processing') {
                            const isProcessing = row.querySelector('.bg-blue-100') !== null;
                            row.classList.toggle('hidden', !isProcessing);
                        } else if (filterId === 'filter-shipped') {
                            const isShipped = row.querySelector('.bg-purple-100') !== null;
                            row.classList.toggle('hidden', !isShipped);
                        } else if (filterId === 'filter-delivered') {
                            const isDelivered = row.querySelector('.bg-green-100') !== null;
                            row.classList.toggle('hidden', !isDelivered);
                        } else if (filterId === 'filter-cancelled') {
                            const isCancelled = row.querySelector('.bg-red-100') !== null;
                            row.classList.toggle('hidden', !isCancelled);
                        }
                    });
                });
            });

            // Search functionality
            const searchInput = document.getElementById('search-input');
            const searchBtn = document.getElementById('search-btn');

            function performSearch() {
                const searchTerm = searchInput.value.toLowerCase();

                orderRows.forEach(row => {
                    if (row.querySelector('td:first-child')) {
                        const orderNumber = row.querySelector('td:first-child .text-gray-900').textContent.toLowerCase();
                        const customerName = row.querySelector('td:nth-child(2) .text-gray-900').textContent.toLowerCase();
                        const customerEmail = row.querySelector('td:nth-child(2) .text-gray-500').textContent.toLowerCase();

                        const matchesSearch = orderNumber.includes(searchTerm) || 
                                             customerName.includes(searchTerm) || 
                                             customerEmail.includes(searchTerm);

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

            // Cancel order
            document.querySelectorAll('.cancel-order').forEach(button => {
                button.addEventListener('click', () => {
                    if (button.hasAttribute('disabled')) {
                        return;
                    }

                    const orderId = button.getAttribute('data-id');
                    const orderNumber = button.getAttribute('data-number');

                    if (confirm(`Вы уверены, что хотите отменить заказ #${orderNumber}?`)) {
                        fetch(`/admin/orders/${orderId}/cancel`, {
                            method: 'POST',
                            headers: {
                                'Content-Type': 'application/json',
                            }
                        })
                        .then(response => {
                            if (response.ok) {
                                alert('Заказ успешно отменен');
                                // Reload the page to update the order status
                                window.location.reload();
                            } else {
                                alert('Ошибка при отмене заказа');
                            }
                        })
                        .catch(error => {
                            console.error('Error cancelling order:', error);
                            alert('Ошибка при отмене заказа');
                        });
                    }
                });
            });
        });
    </script>
</@layout>
