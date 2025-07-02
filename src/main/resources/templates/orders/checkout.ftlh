<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="_csrf" content="${_csrf.token}"/>
    <meta name="_csrf_header" content="${_csrf.headerName}"/>
    <title>Оформление заказа - Arannati</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <script>
        tailwind.config = {
            theme: {
                extend: {
                    colors: {
                        primary: '#8B5CF6',
                        secondary: '#EC4899',
                        accent: '#06B6D4'
                    }
                }
            }
        }
    </script>
</head>
<body class="bg-gray-50 min-h-screen">
<!-- Header -->
<#include "../fragments/header.ftlh">

<!-- Main Content -->
<main class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
    <!-- Breadcrumb -->
    <nav class="flex mb-8" aria-label="Breadcrumb">
        <ol class="flex items-center space-x-2">
            <li><a href="/" class="text-gray-500 hover:text-primary transition">Главная</a></li>
            <li><i class="fas fa-chevron-right text-gray-400 text-sm"></i></li>
            <li><a href="/cart" class="text-gray-500 hover:text-primary transition">Корзина</a></li>
            <li><i class="fas fa-chevron-right text-gray-400 text-sm"></i></li>
            <li><span class="text-primary font-medium">Оформление заказа</span></li>
        </ol>
    </nav>

    <!-- Checkout Steps -->
    <div class="bg-white rounded-2xl shadow-sm p-6 mb-8">
        <div class="flex items-center justify-center space-x-8">
            <div class="flex items-center">
                <div class="w-8 h-8 bg-green-500 text-white rounded-full flex items-center justify-center text-sm font-bold">
                    <i class="fas fa-check"></i>
                </div>
                <span class="ml-2 text-sm font-medium text-green-600">Корзина</span>
            </div>
            <div class="flex-1 h-1 bg-green-500"></div>
            <div class="flex items-center">
                <div class="w-8 h-8 bg-primary text-white rounded-full flex items-center justify-center text-sm font-bold">2</div>
                <span class="ml-2 text-sm font-medium text-primary">Оформление</span>
            </div>
            <div class="flex-1 h-1 bg-gray-300"></div>
            <div class="flex items-center">
                <div class="w-8 h-8 bg-gray-300 text-gray-500 rounded-full flex items-center justify-center text-sm font-bold">3</div>
                <span class="ml-2 text-sm font-medium text-gray-500">Подтверждение</span>
            </div>
        </div>
    </div>

    <div class="grid grid-cols-1 lg:grid-cols-3 gap-8">
        <!-- Checkout Form -->
        <div class="lg:col-span-2">
            <form id="checkout-form" class="space-y-8">
                <!-- Customer Information -->
                <div class="bg-white rounded-2xl shadow-sm p-6">
                    <h2 class="text-xl font-bold text-gray-900 mb-6">Контактная информация</h2>

                    <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
                        <div>
                            <label for="customer-name" class="block text-sm font-medium text-gray-700 mb-2">ФИО *</label>
                            <input type="text" id="customer-name" name="customerName" required
                                   value="${currentUser.firstName!''} ${currentUser.lastName!''}"
                                   class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary focus:border-transparent">
                        </div>
                        <div>
                            <label for="customer-email" class="block text-sm font-medium text-gray-700 mb-2">Email *</label>
                            <input type="email" id="customer-email" name="customerEmail" required
                                   value="${currentUser.email!''}"
                                   class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary focus:border-transparent">
                        </div>
                        <div class="md:col-span-2">
                            <label for="customer-phone" class="block text-sm font-medium text-gray-700 mb-2">Телефон *</label>
                            <input type="tel" id="customer-phone" name="customerPhone" required
                                   value="${currentUser.phone!''}" placeholder="+7 (___) ___-__-__"
                                   class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary focus:border-transparent">
                        </div>
                    </div>
                </div>

                <!-- Delivery Information -->
                <div class="bg-white rounded-2xl shadow-sm p-6">
                    <h2 class="text-xl font-bold text-gray-900 mb-6">Доставка</h2>

                    <!-- Delivery Method -->
                    <div class="mb-6">
                        <label class="block text-sm font-medium text-gray-700 mb-3">Способ доставки *</label>
                        <div class="space-y-3">
                            <label class="flex items-center p-4 border border-gray-300 rounded-lg hover:bg-gray-50 cursor-pointer">
                                <input type="radio" name="deliveryMethod" value="courier" checked class="text-primary focus:ring-primary">
                                <div class="ml-3 flex-1">
                                    <span class="font-medium text-gray-900">Курьерская доставка</span>
                                    <span class="text-primary font-bold delivery-price" data-price="2000">
                                                <#if totalAmount gte 50000>Бесплатно<#else>2,000 ₸</#if>
                                            </span>
                                    <p class="text-sm text-gray-500">Доставка в течение 1-3 рабочих дней</p>
                                </div>
                        </label>
                        <label class="flex items-center p-4 border border-gray-300 rounded-lg hover:bg-gray-50 cursor-pointer">
                            <input type="radio" name="deliveryMethod" value="pickup" class="text-primary focus:ring-primary">
                            <div class="ml-3 flex-1">
                                <div class="flex items-center justify-between">
                                    <span class="font-medium text-gray-900">Самовывоз</span>
                                    <span class="text-green-600 font-bold delivery-price" data-price="0">Бесплатно</span>
                                </div>
                                <p class="text-sm text-gray-500">Забрать в офисе по адресу: ул. Абая, 123</p>
                            </div>
                        </label>
                    </div>
                </div>

                <!-- Delivery Address -->
                <div id="delivery-address-section">
                    <label for="delivery-address" class="block text-sm font-medium text-gray-700 mb-2">Адрес доставки *</label>
                    <textarea id="delivery-address" name="deliveryAddress" rows="3" required
                              placeholder="Укажите полный адрес: город, улица, дом, квартира"
                              class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary focus:border-transparent"></textarea>
                    <p class="text-sm text-gray-500 mt-1">Пример: г. Алматы, ул. Абая, д. 123, кв. 45</p>
                </div>
        </div>

        <!-- Payment Information -->
        <div class="bg-white rounded-2xl shadow-sm p-6">
            <h2 class="text-xl font-bold text-gray-900 mb-6">Способ оплаты</h2>

            <div class="space-y-3">
                <label class="flex items-center p-4 border border-gray-300 rounded-lg hover:bg-gray-50 cursor-pointer">
                    <input type="radio" name="paymentMethod" value="cash" checked class="text-primary focus:ring-primary">
                    <div class="ml-3">
                        <span class="font-medium text-gray-900">Наличными при получении</span>
                        <p class="text-sm text-gray-500">Оплата курьеру или в пункте самовывоза</p>
                    </div>
                </label>
                <label class="flex items-center p-4 border border-gray-300 rounded-lg hover:bg-gray-50 cursor-pointer">
                    <input type="radio" name="paymentMethod" value="card" class="text-primary focus:ring-primary">
                    <div class="ml-3">
                        <span class="font-medium text-gray-900">Банковской картой</span>
                        <p class="text-sm text-gray-500">Онлайн оплата через Kaspi Pay или другие платежные системы</p>
                    </div>
                </label>
                <label class="flex items-center p-4 border border-gray-300 rounded-lg hover:bg-gray-50 cursor-pointer">
                    <input type="radio" name="paymentMethod" value="transfer" class="text-primary focus:ring-primary">
                    <div class="ml-3">
                        <span class="font-medium text-gray-900">Банковский перевод</span>
                        <p class="text-sm text-gray-500">Оплата по реквизитам (для юридических лиц)</p>
                    </div>
                </label>
            </div>
        </div>

        <!-- Additional Information -->
        <div class="bg-white rounded-2xl shadow-sm p-6">
            <h2 class="text-xl font-bold text-gray-900 mb-6">Дополнительная информация</h2>

            <div>
                <label for="order-notes" class="block text-sm font-medium text-gray-700 mb-2">Комментарий к заказу</label>
                <textarea id="order-notes" name="notes" rows="3"
                          placeholder="Укажите особые пожелания к заказу или доставке"
                          class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary focus:border-transparent"></textarea>
            </div>
        </div>
        </form>
    </div>

    <!-- Order Summary -->
    <div class="lg:col-span-1">
        <div class="bg-white rounded-2xl shadow-sm p-6 sticky top-8">
            <h2 class="text-xl font-bold text-gray-900 mb-6">Ваш заказ</h2>

            <!-- Order Items -->
            <div class="space-y-4 mb-6">
                <#if cartItems?? && cartItems?size gt 0>
                    <#list cartItems as item>
                        <div class="flex items-center space-x-3">
                            <div class="flex-shrink-0">
                                <#if item.product.images?? && item.product.images?size gt 0>
                                    <img src="${item.product.images[0].imagePath}"
                                         alt="${item.product.name}"
                                         class="w-12 h-12 rounded-lg object-cover">
                                <#else>
                                    <div class="w-12 h-12 rounded-lg bg-gray-200 flex items-center justify-center">
                                        <i class="fas fa-image text-gray-400"></i>
                                    </div>
                                </#if>
                            </div>
                            <div class="flex-1 min-w-0">
                                <h4 class="text-sm font-medium text-gray-900 truncate">${item.product.name}</h4>
                                <p class="text-sm text-gray-500">${item.cartItem.quantity} × ${item.effectivePrice?string.currency} ₸</p>
                            </div>
                            <div class="text-sm font-semibold text-gray-900">
                                ${item.itemTotal?string.currency} ₸
                            </div>
                        </div>
                    </#list>
                </#if>
            </div>

            <!-- Order Totals -->
            <div class="border-t pt-4 space-y-3">
                <#assign totalItems = 0>
                <#assign totalRegularPrice = 0>
                <#if cartItems??>
                    <#list cartItems as item>
                        <#assign totalItems = totalItems + item.cartItem.quantity>
                        <#assign totalRegularPrice = totalRegularPrice + (item.product.regularPrice * item.cartItem.quantity)>
                    </#list>
                </#if>

                <div class="flex justify-between text-gray-600">
                    <span>Товары (${totalItems} шт.)</span>
                    <span>${totalRegularPrice?string.currency} ₸</span>
                </div>

                <#if totalRegularPrice != totalAmount>
                    <#assign discount = totalRegularPrice - totalAmount>
                    <div class="flex justify-between text-gray-600">
                                <span>
                                    <#if currentUser?? && currentUser.role == "COSMETOLOGIST">
                                        Скидка косметолога
                                    <#elseif currentUser?? && currentUser.role == "ADMIN">
                                        Скидка администратора
                                    <#else>
                                        Скидка
                                    </#if>
                                </span>
                        <span class="text-green-600">-${discount?string.currency} ₸</span>
                    </div>
                </#if>

                <div class="flex justify-between text-gray-600" id="shipping-cost">
                    <span>Доставка</span>
                    <span id="shipping-price">
                                <#if totalAmount gte 50000>Бесплатно<#else>2,000 ₸</#if>
                            </span>
                </div>

                <div class="border-t pt-3">
                    <div class="flex justify-between text-xl font-bold">
                        <span>Итого:</span>
                        <#assign finalAmount = totalAmount + ((totalAmount < 50000)?then(2000, 0))>
                        <span class="text-primary" id="final-total">${finalAmount?string.currency} ₸</span>
                    </div>
                </div>
            </div>

            <!-- Place Order Button -->
            <button type="submit" form="checkout-form" id="place-order-btn"
                    class="w-full bg-gradient-to-r from-primary to-secondary text-white py-4 rounded-xl font-semibold hover:shadow-lg transition mt-6">
                <i class="fas fa-lock mr-2"></i>
                Оформить заказ
            </button>

            <!-- Security Info -->
            <div class="mt-6 p-4 bg-green-50 rounded-xl">
                <div class="flex items-center text-green-800">
                    <i class="fas fa-shield-alt mr-2"></i>
                    <span class="text-sm font-medium">Безопасная покупка</span>
                </div>
                <p class="text-sm text-green-700 mt-1">Ваши данные защищены SSL-шифрованием</p>
            </div>

            <!-- Benefits -->
            <#if currentUser??>
                <div class="mt-4 p-4 bg-purple-50 rounded-xl">
                    <div class="flex items-center text-purple-800 mb-2">
                        <i class="fas fa-star mr-2"></i>
                        <span class="text-sm font-medium">Ваши преимущества:</span>
                    </div>
                    <ul class="text-sm text-purple-700 space-y-1">
                        <#if currentUser.role == "COSMETOLOGIST">
                            <li>• Профессиональная скидка 25%</li>
                        <#elseif currentUser.role == "ADMIN">
                            <li>• Максимальная скидка 40%</li>
                        <#else>
                            <li>• Накопление бонусных баллов</li>
                        </#if>
                        <li>• Бесплатная доставка от 50,000 ₸</li>
                        <li>• Приоритетная обработка заказов</li>
                        <li>• Гарантия качества</li>
                    </ul>
                </div>
            </#if>
        </div>
    </div>
    </div>
</main>

<!-- Loading Overlay -->
<div id="loading-overlay" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 hidden">
    <div class="bg-white rounded-2xl p-8 text-center">
        <div class="animate-spin w-8 h-8 border-4 border-primary border-t-transparent rounded-full mx-auto mb-4"></div>
        <p class="text-gray-600 font-medium">Оформляем ваш заказ...</p>
    </div>
</div>

<!-- Toast Notifications -->
<div id="toast-container" class="fixed bottom-4 right-4 z-50 space-y-2"></div>

<script>
    document.addEventListener('DOMContentLoaded', function() {
            // Get CSRF token
            window.csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
            window.csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');
        const form = document.getElementById('checkout-form');
        const deliveryMethods = document.querySelectorAll('input[name="deliveryMethod"]');
        const addressSection = document.getElementById('delivery-address-section');
        const addressField = document.getElementById('delivery-address');

        // Handle delivery method changes
        deliveryMethods.forEach(method => {
            method.addEventListener('change', function() {
                updateDeliveryPrice();
                toggleAddressField();
            });
        });

        // Initialize delivery options
        updateDeliveryPrice();
        toggleAddressField();

        // Form submission
        form.addEventListener('submit', async function(e) {
            e.preventDefault();
            await submitOrder();
        });

        // Phone number formatting
        const phoneInput = document.getElementById('customer-phone');
        phoneInput.addEventListener('input', function(e) {
            let value = e.target.value.replace(/\D/g, '');
            if (value.startsWith('7')) value = value.substring(1);
            if (value.startsWith('8')) value = value.substring(1);

            let formatted = '+7';
            if (value.length > 0) formatted += ' (' + value.substring(0, 3);
            if (value.length > 3) formatted += ') ' + value.substring(3, 6);
            if (value.length > 6) formatted += '-' + value.substring(6, 8);
            if (value.length > 8) formatted += '-' + value.substring(8, 10);

            e.target.value = formatted;
        });
    });

    function updateDeliveryPrice() {
        const selectedMethod = document.querySelector('input[name="deliveryMethod"]:checked');
        const shippingPrice = document.getElementById('shipping-price');
        const finalTotal = document.getElementById('final-total');

        let deliveryCost = 0;

        if (selectedMethod.value === 'courier') {
            const totalAmount = ${totalAmount};
            deliveryCost = totalAmount >= 50000 ? 0 : 2000;
            shippingPrice.textContent = deliveryCost === 0 ? 'Бесплатно' : '2,000 ₸';
        } else {
            shippingPrice.textContent = 'Бесплатно';
        }

        const newTotal = ${totalAmount} + deliveryCost;
        finalTotal.textContent = newTotal.toLocaleString('ru-RU') + ' ₸';
    }

    function toggleAddressField() {
        const selectedMethod = document.querySelector('input[name="deliveryMethod"]:checked');
        const addressSection = document.getElementById('delivery-address-section');
        const addressField = document.getElementById('delivery-address');

        if (selectedMethod.value === 'pickup') {
            addressSection.style.display = 'none';
            addressField.required = false;
        } else {
            addressSection.style.display = 'block';
            addressField.required = true;
        }
    }

    async function submitOrder() {
        const form = document.getElementById('checkout-form');
        const formData = new FormData(form);
        const loadingOverlay = document.getElementById('loading-overlay');

        // Validate form
        if (!form.checkValidity()) {
            form.reportValidity();
            return;
        }

        // Show loading
        loadingOverlay.classList.remove('hidden');

        try {
            // Prepare order data
            const orderData = {
                customerName: formData.get('customerName'),
                customerEmail: formData.get('customerEmail'),
                customerPhone: formData.get('customerPhone'),
                deliveryAddress: formData.get('deliveryAddress') || 'Самовывоз',
                deliveryMethod: formData.get('deliveryMethod'),
                paymentMethod: formData.get('paymentMethod'),
                notes: formData.get('notes') || '',
                totalAmount: ${totalAmount},
                discountAmount: ${totalRegularPrice - totalAmount},
                shippingAmount: document.querySelector('input[name="deliveryMethod"]:checked').value === 'courier' && ${totalAmount} < 50000 ? 2000 : 0
            };

            const headers = {
                    'Content-Type': 'application/json',
                };
                headers[window.csrfHeader] = window.csrfToken;

                const response = await fetch('/api/orders/create', {
                method: 'POST',
                headers: headers,
                body: JSON.stringify(orderData)
            });

            const data = await response.json();

            if (data.success) {
                // Redirect to success page
                window.location.href = `/orders/${data.orderId}/success`;
            } else {
                throw new Error(data.error || 'Ошибка оформления заказа');
            }

        } catch (error) {
            console.error('Order submission error:', error);
            showToast(error.message || 'Ошибка оформления заказа', 'error');
        } finally {
            loadingOverlay.classList.add('hidden');
        }
    }

    function showToast(message, type = 'info') {
        const toast = document.createElement('div');
        <#noparse>
        toast.className = `p-4 rounded-lg shadow-lg text-white ${
                type === 'success' ? 'bg-green-500' :
                type === 'error' ? 'bg-red-500' : 'bg-blue-500'
            } transform transition-all duration-300 translate-x-full opacity-0`;

        toast.innerHTML = `
                <div class="flex items-center space-x-2">
                    <i class="fas fa-${type === 'success' ? 'check' : type === 'error' ? 'exclamation-triangle' : 'info-circle'}"></i>
                    <span>${message}</span>
                </div>
            `;
        </#noparse>

        document.getElementById('toast-container').appendChild(toast);

        setTimeout(() => {
            toast.classList.remove('translate-x-full', 'opacity-0');
        }, 100);

        setTimeout(() => {
            toast.classList.add('translate-x-full', 'opacity-0');
            setTimeout(() => toast.remove(), 300);
        }, 5000);
    }
</script>
</body>
</html>
