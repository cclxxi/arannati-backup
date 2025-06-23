<#assign pageTitle = "Arannati - История заказов">
<#assign pageDescription = "История заказов пользователя Arannati">
<#include "../layouts/main.ftlh">

<@layout>
    <div class="container mx-auto px-4 py-8">
        <div class="max-w-6xl mx-auto">
            <!-- Header -->
            <div class="bg-white shadow-md rounded-lg p-6 mb-6">
                <div class="flex items-center justify-between mb-4">
                    <h1 class="text-2xl font-bold text-gray-800">История заказов</h1>
                    <a href="/dashboard" class="text-blue-600 hover:text-blue-800">
                        <i class="fas fa-arrow-left mr-1"></i> Назад к панели
                    </a>
                </div>
                
                <div class="bg-blue-50 border-l-4 border-blue-500 p-4">
                    <div class="flex">
                        <div class="flex-shrink-0">
                            <i class="fas fa-info-circle text-blue-500"></i>
                        </div>
                        <div class="ml-3">
                            <p class="text-sm text-blue-700">
                                Здесь вы можете просмотреть историю ваших заказов и их статус.
                            </p>
                        </div>
                    </div>
                </div>
            </div>
            
            <!-- Orders List -->
            <div class="bg-white shadow-md rounded-lg p-6">
                <h2 class="text-xl font-semibold text-gray-800 mb-4">Все заказы</h2>
                
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
                    <!-- Filters -->
                    <div class="mb-6 flex flex-wrap gap-4">
                        <div class="relative">
                            <select id="status-filter" class="block appearance-none w-full bg-white border border-gray-300 hover:border-gray-400 px-4 py-2 pr-8 rounded-lg shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500">
                                <option value="">Все статусы</option>
                                <option value="PENDING">В обработке</option>
                                <option value="CONFIRMED">Подтвержден</option>
                                <option value="SHIPPED">Отправлен</option>
                                <option value="DELIVERED">Доставлен</option>
                                <option value="CANCELLED">Отменен</option>
                            </select>
                            <div class="pointer-events-none absolute inset-y-0 right-0 flex items-center px-2 text-gray-700">
                                <i class="fas fa-chevron-down"></i>
                            </div>
                        </div>
                        
                        <div class="relative flex-grow">
                            <input type="text" id="search-orders" placeholder="Поиск по номеру заказа" class="block w-full bg-white border border-gray-300 hover:border-gray-400 px-4 py-2 rounded-lg shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500">
                            <div class="absolute inset-y-0 right-0 flex items-center px-2 text-gray-700">
                                <i class="fas fa-search"></i>
                            </div>
                        </div>
                    </div>
                    
                    <!-- Orders Table -->
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
                            <tbody class="bg-white divide-y divide-gray-200" id="orders-table-body">
                                <#list orders as order>
                                    <tr class="order-row" data-status="${order.status}">
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
                                </#list>
                            </tbody>
                        </table>
                    </div>
                    
                    <!-- No Results Message (hidden by default) -->
                    <div id="no-results-message" class="hidden text-center py-8">
                        <div class="text-4xl text-gray-300 mb-3">
                            <i class="fas fa-search"></i>
                        </div>
                        <p class="text-gray-500">Заказы не найдены</p>
                    </div>
                    
                    <!-- Pagination (if needed) -->
                    <div class="mt-6 flex items-center justify-between">
                        <div class="text-sm text-gray-700">
                            Показано <span class="font-medium">${orders?size}</span> заказов
                        </div>
                    </div>
                    
                    <!-- JavaScript for filtering -->
                    <script>
                        document.addEventListener('DOMContentLoaded', function() {
                            const statusFilter = document.getElementById('status-filter');
                            const searchInput = document.getElementById('search-orders');
                            const orderRows = document.querySelectorAll('.order-row');
                            const noResultsMessage = document.getElementById('no-results-message');
                            const ordersTableBody = document.getElementById('orders-table-body');
                            
                            function filterOrders() {
                                const statusValue = statusFilter.value.toLowerCase();
                                const searchValue = searchInput.value.toLowerCase();
                                let visibleCount = 0;
                                
                                orderRows.forEach(row => {
                                    const orderNumber = row.querySelector('td:first-child').textContent.toLowerCase();
                                    const status = row.getAttribute('data-status').toLowerCase();
                                    
                                    const matchesStatus = statusValue === '' || status === statusValue;
                                    const matchesSearch = searchValue === '' || orderNumber.includes(searchValue);
                                    
                                    if (matchesStatus && matchesSearch) {
                                        row.classList.remove('hidden');
                                        visibleCount++;
                                    } else {
                                        row.classList.add('hidden');
                                    }
                                });
                                
                                // Show/hide no results message
                                if (visibleCount === 0) {
                                    noResultsMessage.classList.remove('hidden');
                                    ordersTableBody.classList.add('hidden');
                                } else {
                                    noResultsMessage.classList.add('hidden');
                                    ordersTableBody.classList.remove('hidden');
                                }
                            }
                            
                            statusFilter.addEventListener('change', filterOrders);
                            searchInput.addEventListener('input', filterOrders);
                        });
                    </script>
                </#if>
            </div>
        </div>
    </div>
</@layout>