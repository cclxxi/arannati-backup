<#assign pageTitle = "Arannati - Детали заказа">
<#assign pageDescription = "Просмотр деталей заказа в административной панели Arannati">
<#include "../layouts/main.ftlh">

<@layout>
    <div class="container mx-auto px-4 py-8">
        <!-- Page Header -->
        <div class="bg-white shadow-md rounded-lg p-6 mb-6">
            <div class="flex items-center justify-between mb-4">
                <h1 class="text-2xl font-bold text-gray-800">Заказ #${order.orderNumber}</h1>
                <a href="/admin/orders" class="px-4 py-2 bg-gray-200 text-gray-700 rounded hover:bg-gray-300">
                    <i class="fas fa-arrow-left mr-2"></i> Назад к заказам
                </a>
            </div>
            
            <div class="bg-blue-50 border-l-4 border-blue-500 p-4">
                <div class="flex">
                    <div class="flex-shrink-0">
                        <i class="fas fa-info-circle text-blue-500"></i>
                    </div>
                    <div class="ml-3">
                        <p class="text-sm text-blue-700">
                            Здесь вы можете просмотреть детали заказа и обновить его статус.
                        </p>
                    </div>
                </div>
            </div>
        </div>
        
        <!-- Order Status and Actions -->
        <div class="bg-white shadow-md rounded-lg p-6 mb-6">
            <div class="flex flex-wrap items-center justify-between mb-4">
                <div>
                    <h2 class="text-lg font-semibold text-gray-800">Статус заказа</h2>
                    <div class="mt-2">
                        <#if order.status == "PENDING">
                            <span class="px-3 py-1 inline-flex text-sm leading-5 font-semibold rounded-full bg-yellow-100 text-yellow-800">
                                Ожидает
                            </span>
                        <#elseif order.status == "PROCESSING">
                            <span class="px-3 py-1 inline-flex text-sm leading-5 font-semibold rounded-full bg-blue-100 text-blue-800">
                                В обработке
                            </span>
                        <#elseif order.status == "SHIPPED">
                            <span class="px-3 py-1 inline-flex text-sm leading-5 font-semibold rounded-full bg-purple-100 text-purple-800">
                                Отправлен
                            </span>
                        <#elseif order.status == "DELIVERED">
                            <span class="px-3 py-1 inline-flex text-sm leading-5 font-semibold rounded-full bg-green-100 text-green-800">
                                Доставлен
                            </span>
                        <#elseif order.status == "CANCELLED">
                            <span class="px-3 py-1 inline-flex text-sm leading-5 font-semibold rounded-full bg-red-100 text-red-800">
                                Отменен
                            </span>
                        <#else>
                            <span class="px-3 py-1 inline-flex text-sm leading-5 font-semibold rounded-full bg-gray-100 text-gray-800">
                                ${order.status}
                            </span>
                        </#if>
                    </div>
                </div>
                
                <div class="mt-4 sm:mt-0">
                    <#if order.status != "CANCELLED" && order.status != "DELIVERED">
                        <form id="update-status-form" class="flex items-center">
                            <select id="status" name="status" class="mr-2 px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500">
                                <option value="PENDING" <#if order.status == "PENDING">selected</#if>>Ожидает</option>
                                <option value="PROCESSING" <#if order.status == "PROCESSING">selected</#if>>В обработке</option>
                                <option value="SHIPPED" <#if order.status == "SHIPPED">selected</#if>>Отправлен</option>
                                <option value="DELIVERED" <#if order.status == "DELIVERED">selected</#if>>Доставлен</option>
                            </select>
                            <button type="submit" class="px-4 py-2 bg-blue-500 text-white rounded-lg hover:bg-blue-600">
                                Обновить статус
                            </button>
                        </form>
                    </#if>
                </div>
            </div>
            
            <div class="mt-4 grid grid-cols-1 md:grid-cols-2 gap-6">
                <div>
                    <h3 class="text-md font-medium text-gray-700 mb-2">Информация о заказе</h3>
                    <div class="bg-gray-50 p-4 rounded-lg">
                        <div class="grid grid-cols-2 gap-4">
                            <div>
                                <p class="text-sm text-gray-500">Дата заказа</p>
                                <p class="text-sm font-medium">${order.orderDate}</p>
                            </div>
                            <div>
                                <p class="text-sm text-gray-500">Способ оплаты</p>
                                <p class="text-sm font-medium">${order.paymentMethod}</p>
                            </div>
                            <div>
                                <p class="text-sm text-gray-500">Сумма заказа</p>
                                <p class="text-sm font-medium">${order.totalAmount} ₸</p>
                            </div>
                            <div>
                                <p class="text-sm text-gray-500">Количество товаров</p>
                                <p class="text-sm font-medium">${order.items?size}</p>
                            </div>
                        </div>
                    </div>
                </div>
                
                <div>
                    <h3 class="text-md font-medium text-gray-700 mb-2">Информация о клиенте</h3>
                    <div class="bg-gray-50 p-4 rounded-lg">
                        <div class="flex items-center mb-4">
                            <div class="flex-shrink-0 h-10 w-10 bg-gray-200 rounded-full flex items-center justify-center">
                                <i class="fas fa-user text-gray-500"></i>
                            </div>
                            <div class="ml-4">
                                <p class="text-sm font-medium">${order.customerName}</p>
                                <p class="text-sm text-gray-500">${order.customerEmail}</p>
                            </div>
                        </div>
                        <div class="grid grid-cols-1 gap-2">
                            <div>
                                <p class="text-sm text-gray-500">Телефон</p>
                                <p class="text-sm font-medium">${order.customerPhone}</p>
                            </div>
                            <div>
                                <p class="text-sm text-gray-500">Адрес доставки</p>
                                <p class="text-sm font-medium">${order.shippingAddress}</p>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        
        <!-- Order Items -->
        <div class="bg-white shadow-md rounded-lg overflow-hidden">
            <div class="px-6 py-4 border-b border-gray-200">
                <h2 class="text-lg font-semibold text-gray-800">Товары в заказе</h2>
            </div>
            <div class="overflow-x-auto">
                <table class="min-w-full divide-y divide-gray-200">
                    <thead class="bg-gray-50">
                        <tr>
                            <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                Товар
                            </th>
                            <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                Цена
                            </th>
                            <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                Количество
                            </th>
                            <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                Сумма
                            </th>
                        </tr>
                    </thead>
                    <tbody class="bg-white divide-y divide-gray-200">
                        <#list order.items as item>
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
                                            <div class="text-sm text-gray-500">
                                                ${item.productCategory}
                                            </div>
                                        </div>
                                    </div>
                                </td>
                                <td class="px-6 py-4 whitespace-nowrap">
                                    <div class="text-sm text-gray-900">${item.price} ₸</div>
                                </td>
                                <td class="px-6 py-4 whitespace-nowrap">
                                    <div class="text-sm text-gray-900">${item.quantity}</div>
                                </td>
                                <td class="px-6 py-4 whitespace-nowrap">
                                    <div class="text-sm text-gray-900">${item.price * item.quantity} ₸</div>
                                </td>
                            </tr>
                        </#list>
                    </tbody>
                    <tfoot class="bg-gray-50">
                        <tr>
                            <td colspan="3" class="px-6 py-4 text-right text-sm font-medium text-gray-900">
                                Итого:
                            </td>
                            <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
                                ${order.totalAmount} ₸
                            </td>
                        </tr>
                    </tfoot>
                </table>
            </div>
        </div>
        
        <!-- Order Notes -->
        <div class="bg-white shadow-md rounded-lg p-6 mt-6">
            <h2 class="text-lg font-semibold text-gray-800 mb-4">Примечания к заказу</h2>
            
            <#if order.notes?? && order.notes != "">
                <div class="bg-gray-50 p-4 rounded-lg mb-4">
                    <p class="text-sm text-gray-700 whitespace-pre-line">${order.notes}</p>
                </div>
            <#else>
                <div class="bg-gray-50 p-4 rounded-lg mb-4">
                    <p class="text-sm text-gray-500 italic">Нет примечаний к заказу</p>
                </div>
            </#if>
            
            <form id="add-note-form" class="mt-4">
                <div class="mb-4">
                    <label for="note" class="block text-sm font-medium text-gray-700 mb-1">Добавить примечание</label>
                    <textarea id="note" name="note" rows="3" class="w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"></textarea>
                </div>
                <div class="flex justify-end">
                    <button type="submit" class="px-4 py-2 bg-blue-500 text-white rounded-lg hover:bg-blue-600">
                        Сохранить примечание
                    </button>
                </div>
            </form>
        </div>
    </div>
    
    <script>
        document.addEventListener('DOMContentLoaded', function() {
            // Update order status
            const updateStatusForm = document.getElementById('update-status-form');
            
            if (updateStatusForm) {
                updateStatusForm.addEventListener('submit', function(e) {
                    e.preventDefault();
                    
                    const status = document.getElementById('status').value;
                    
                    fetch(`/admin/orders/${order.id}/status`, {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/x-www-form-urlencoded',
                        },
                        body: `status=${status}`
                    })
                    .then(response => {
                        if (response.ok) {
                            alert('Статус заказа успешно обновлен');
                            window.location.reload();
                        } else {
                            alert('Ошибка при обновлении статуса заказа');
                        }
                    })
                    .catch(error => {
                        console.error('Error updating order status:', error);
                        alert('Ошибка при обновлении статуса заказа');
                    });
                });
            }
            
            // Add note to order
            const addNoteForm = document.getElementById('add-note-form');
            
            addNoteForm.addEventListener('submit', function(e) {
                e.preventDefault();
                
                const note = document.getElementById('note').value.trim();
                
                if (!note) {
                    alert('Пожалуйста, введите примечание');
                    return;
                }
                
                fetch(`/admin/orders/${order.id}/note`, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded',
                    },
                    body: `note=${encodeURIComponent(note)}`
                })
                .then(response => {
                    if (response.ok) {
                        alert('Примечание успешно добавлено');
                        window.location.reload();
                    } else {
                        alert('Ошибка при добавлении примечания');
                    }
                })
                .catch(error => {
                    console.error('Error adding note:', error);
                    alert('Ошибка при добавлении примечания');
                });
            });
        });
    </script>
</@layout>