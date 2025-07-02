<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="_csrf" content="${_csrf.token}"/>
    <meta name="_csrf_header" content="${_csrf.headerName}"/>
    <title>Корзина - Arannati</title>
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
            <li><span class="text-primary font-medium">Корзина</span></li>
        </ol>
    </nav>

    <#if cartItems?? && cartItems?size gt 0>
        <div class="grid grid-cols-1 lg:grid-cols-3 gap-8">
            <!-- Cart Items -->
            <div class="lg:col-span-2">
                <div class="bg-white rounded-2xl shadow-sm p-6">
                    <div class="flex items-center justify-between mb-6">
                        <h1 class="text-2xl font-bold text-gray-900">Корзина</h1>
                        <span class="text-gray-500">${cartItems?size} товар<#if cartItems?size gt 1>а</#if></span>
                    </div>

                    <!-- Cart Items -->
                    <div class="space-y-6" id="cart-items">
                        <#list cartItems as item>
                            <div class="flex items-center space-x-4 p-4 border border-gray-200 rounded-xl hover:shadow-md transition" data-product-id="${item.cartItem.productId}">
                                <div class="flex-shrink-0">
                                    <#if item.product.images?? && item.product.images?size gt 0>
                                        <img src="${item.product.images[0].imagePath}"
                                             alt="${item.product.name}"
                                             class="w-20 h-20 rounded-lg object-cover">
                                    <#else>
                                        <div class="w-20 h-20 rounded-lg bg-gray-200 flex items-center justify-center">
                                            <i class="fas fa-image text-gray-400"></i>
                                        </div>
                                    </#if>
                                </div>
                                <div class="flex-1 min-w-0">
                                    <h3 class="text-lg font-semibold text-gray-900 truncate">${item.product.name}</h3>
                                    <p class="text-sm text-gray-500">Артикул: ${item.product.sku!'Н/Д'}</p>
                                    <p class="text-sm text-gray-500">Бренд: ${item.product.brandName!'Н/Д'}</p>
                                    <div class="flex items-center space-x-2 mt-1">
                                        <span class="text-lg font-bold text-primary">${item.effectivePrice?string.currency} ₸</span>
                                        <#if item.product.regularPrice != item.effectivePrice>
                                            <span class="text-sm text-gray-400 line-through">${item.product.regularPrice?string.currency} ₸</span>
                                            <#assign discountPercent = (((item.product.regularPrice - item.effectivePrice) / item.product.regularPrice) * 100)?round>
                                            <span class="bg-red-100 text-red-800 px-2 py-1 rounded-full text-xs font-medium">-${discountPercent}%</span>
                                        </#if>
                                        <#if currentUser?? && currentUser.role == "COSMETOLOGIST">
                                            <span class="bg-purple-100 text-purple-800 px-2 py-1 rounded-full text-xs font-medium">Цена косметолога</span>
                                        <#elseif currentUser?? && currentUser.role == "ADMIN">
                                            <span class="bg-blue-100 text-blue-800 px-2 py-1 rounded-full text-xs font-medium">Цена админа</span>
                                        </#if>
                                    </div>
                                </div>
                                <div class="flex items-center space-x-3">
                                    <div class="flex items-center border border-gray-300 rounded-lg">
                                        <button class="p-2 hover:bg-gray-100 transition quantity-btn" data-action="decrease">
                                            <i class="fas fa-minus text-sm"></i>
                                        </button>
                                        <input type="number" value="${item.cartItem.quantity}" min="1" max="${item.product.stockQuantity}"
                                               class="w-16 text-center border-0 focus:ring-0 quantity-input">
                                        <button class="p-2 hover:bg-gray-100 transition quantity-btn" data-action="increase">
                                            <i class="fas fa-plus text-sm"></i>
                                        </button>
                                    </div>
                                    <div class="text-right">
                                        <div class="text-lg font-semibold text-gray-900">${item.itemTotal?string.currency} ₸</div>
                                        <div class="text-sm text-gray-500">за ${item.cartItem.quantity} шт.</div>
                                    </div>
                                    <button class="p-2 text-red-500 hover:bg-red-50 rounded-lg transition remove-item" title="Удалить">
                                        <i class="fas fa-trash"></i>
                                    </button>
                                </div>
                            </div>
                        </#list>
                    </div>

                    <!-- Actions -->
                    <div class="flex items-center justify-between mt-8 pt-6 border-t">
                        <button class="text-red-500 hover:text-red-700 font-medium transition" id="clear-cart">
                            <i class="fas fa-trash mr-2"></i>
                            Очистить корзину
                        </button>
                        <a href="/" class="text-primary hover:text-purple-700 font-medium transition">
                            <i class="fas fa-arrow-left mr-2"></i>
                            Продолжить покупки
                        </a>
                    </div>
                </div>
            </div>

            <!-- Order Summary -->
            <div class="lg:col-span-1">
                <div class="bg-white rounded-2xl shadow-sm p-6 sticky top-8">
                    <h2 class="text-xl font-bold text-gray-900 mb-6">Итого</h2>

                    <div class="space-y-4">
                        <#assign totalItems = 0>
                        <#assign totalRegularPrice = 0>
                        <#list cartItems as item>
                            <#assign totalItems = totalItems + item.cartItem.quantity>
                            <#assign totalRegularPrice = totalRegularPrice + (item.product.regularPrice * item.cartItem.quantity)>
                        </#list>

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
                                        Скидка админа
                                    <#else>
                                        Скидка
                                    </#if>
                                </span>
                                <span class="text-green-600">-${discount?string.currency} ₸</span>
                            </div>
                        </#if>

                        <div class="flex justify-between text-gray-600">
                            <span>Доставка</span>
                            <#if totalAmount gte 50000>
                                <span class="text-green-600">Бесплатно</span>
                            <#else>
                                <span>2,000 ₸</span>
                            </#if>
                        </div>

                        <div class="border-t pt-4">
                            <div class="flex justify-between text-xl font-bold">
                                <span>К оплате:</span>
                                <#assign finalAmount = totalAmount + (totalAmount < 50000)?then(2000, 0)>
                                <span class="text-primary">${finalAmount?string.currency} ₸</span>
                            </div>
                        </div>
                    </div>

                    <button class="w-full bg-gradient-to-r from-primary to-secondary text-white py-4 rounded-xl font-semibold hover:shadow-lg transition mt-6" id="checkout-btn">
                        Оформить заказ
                    </button>

                    <!-- Promo Code -->
                    <div class="mt-6">
                        <div class="flex">
                            <input type="text" placeholder="Промокод" id="promo-code" class="flex-1 px-4 py-2 border border-gray-300 rounded-l-lg focus:ring-2 focus:ring-primary focus:border-transparent">
                            <button class="px-6 py-2 bg-gray-100 text-gray-700 border border-l-0 border-gray-300 rounded-r-lg hover:bg-gray-200 transition" id="apply-promo">
                                Применить
                            </button>
                        </div>
                    </div>

                    <!-- Delivery Info -->
                    <#if totalAmount < 50000>
                        <div class="mt-6 p-4 bg-amber-50 rounded-xl">
                            <div class="flex items-center text-amber-800">
                                <i class="fas fa-truck mr-2"></i>
                                <span class="text-sm font-medium">До бесплатной доставки: ${(50000 - totalAmount)?string.currency} ₸</span>
                            </div>
                        </div>
                    <#else>
                        <div class="mt-6 p-4 bg-green-50 rounded-xl">
                            <div class="flex items-center text-green-800">
                                <i class="fas fa-truck mr-2"></i>
                                <span class="text-sm font-medium">Бесплатная доставка!</span>
                            </div>
                        </div>
                    </#if>

                    <!-- User Benefits -->
                    <#if currentUser??>
                        <div class="mt-6 p-4 bg-purple-50 rounded-xl">
                            <div class="flex items-center text-purple-800 mb-2">
                                <i class="fas fa-star mr-2"></i>
                                <span class="text-sm font-medium">Ваши преимущества:</span>
                            </div>
                            <ul class="text-sm text-purple-700 space-y-1">
                                <#if currentUser.role == "COSMETOLOGIST">
                                    <li>• Скидка косметолога 25%</li>
                                    <li>• Доступ к профессиональным каталогам</li>
                                <#elseif currentUser.role == "ADMIN">
                                    <li>• Максимальная скидка 40%</li>
                                    <li>• Приоритетная обработка заказов</li>
                                <#else>
                                    <li>• Накопление бонусов</li>
                                    <li>• Персональные предложения</li>
                                </#if>
                                <li>• Бесплатная доставка от 50,000 ₸</li>
                                <li>• Приоритетная поддержка</li>
                            </ul>
                        </div>
                    </#if>
                </div>
            </div>
        </div>
    <#else>
        <!-- Empty Cart -->
        <div class="text-center py-16">
            <div class="bg-white rounded-2xl shadow-sm p-12 max-w-md mx-auto">
                <div class="w-24 h-24 bg-gray-100 rounded-full flex items-center justify-center mx-auto mb-6">
                    <i class="fas fa-shopping-cart text-4xl text-gray-400"></i>
                </div>
                <h2 class="text-2xl font-bold text-gray-900 mb-4">Корзина пуста</h2>
                <p class="text-gray-500 mb-8">Добавьте товары из каталога, чтобы оформить заказ</p>
                <a href="/" class="inline-flex items-center px-6 py-3 bg-gradient-to-r from-primary to-secondary text-white rounded-xl font-semibold hover:shadow-lg transition">
                    <i class="fas fa-arrow-left mr-2"></i>
                    Перейти в каталог
                </a>
            </div>
        </div>
    </#if>
</main>

<!-- Toast Notifications -->
<div id="toast-container" class="fixed bottom-4 right-4 z-50 space-y-2"></div>

<script>
    // Cart functionality
    document.addEventListener('DOMContentLoaded', function() {
            // Get CSRF token
            window.csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
            window.csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');
        // Quantity controls
        document.querySelectorAll('.quantity-btn').forEach(btn => {
            btn.addEventListener('click', function() {
                const action = this.dataset.action;
                const input = this.parentElement.querySelector('.quantity-input');
                const productId = this.closest('[data-product-id]').dataset.productId;
                const maxStock = parseInt(input.getAttribute('max'));

                let value = parseInt(input.value);

                if (action === 'increase' && value < maxStock) {
                    value++;
                } else if (action === 'decrease' && value > 1) {
                    value--;
                }

                input.value = value;
                updateCartItem(productId, value);
            });
        });

        // Direct input change
        document.querySelectorAll('.quantity-input').forEach(input => {
            input.addEventListener('change', function() {
                const productId = this.closest('[data-product-id]').dataset.productId;
                const value = parseInt(this.value);
                const maxStock = parseInt(this.getAttribute('max'));

                if (value < 1) {
                    this.value = 1;
                    return;
                }

                if (value > maxStock) {
                    this.value = maxStock;
                    showToast(`Максимальное количество: ${maxStock}`, 'warning');
                    return;
                }

                updateCartItem(productId, value);
            });
        });

        // Remove item buttons
        document.querySelectorAll('.remove-item').forEach(btn => {
            btn.addEventListener('click', function() {
                const productId = this.closest('[data-product-id]').dataset.productId;
                removeCartItem(productId);
            });
        });

        // Clear cart
        const clearCartBtn = document.getElementById('clear-cart');
        if (clearCartBtn) {
            clearCartBtn.addEventListener('click', function() {
                if (confirm('Вы уверены, что хотите очистить корзину?')) {
                    clearCart();
                }
            });
        }

        // Checkout button
        const checkoutBtn = document.getElementById('checkout-btn');
        if (checkoutBtn) {
            checkoutBtn.addEventListener('click', function() {
                window.location.href = '/checkout';
            });
        }

        // Promo code
        const applyPromoBtn = document.getElementById('apply-promo');
        if (applyPromoBtn) {
            applyPromoBtn.addEventListener('click', function() {
                const promoCode = document.getElementById('promo-code').value.trim();
                if (promoCode) {
                    applyPromoCode(promoCode);
                }
            });
        }
    });

    async function updateCartItem(productId, quantity) {
        try {
            const headers = {
                'Content-Type': 'application/x-www-form-urlencoded',
            };
            headers[window.csrfHeader] = window.csrfToken;

            const response = await fetch('/api/cart/update', {
                method: 'POST',
                headers: headers,
                body: `productId=${productId}&quantity=${quantity}`
            });

            const data = await response.json();

            if (data.success) {
                showToast('Количество обновлено', 'success');
                // Refresh page to update totals
                setTimeout(() => {
                    window.location.reload();
                }, 1000);
            } else {
                showToast(data.error || 'Ошибка обновления', 'error');
            }
        } catch (error) {
            showToast('Ошибка сети', 'error');
        }
    }

    async function removeCartItem(productId) {
        try {
            const headers = {
                'Content-Type': 'application/x-www-form-urlencoded',
            };
            headers[window.csrfHeader] = window.csrfToken;

            const response = await fetch('/api/cart/remove', {
                method: 'POST',
                headers: headers,
                body: `productId=${productId}`
            });

            const data = await response.json();

            if (data.success) {
                showToast('Товар удален из корзины', 'success');
                // Remove item from DOM
                const item = document.querySelector(`[data-product-id="${productId}"]`);
                if (item) {
                    item.remove();
                }
                // Refresh page after short delay
                setTimeout(() => {
                    window.location.reload();
                }, 1000);
            } else {
                showToast(data.error || 'Ошибка удаления', 'error');
            }
        } catch (error) {
            showToast('Ошибка сети', 'error');
        }
    }

    async function clearCart() {
        try {
            const headers = {};
            headers[window.csrfHeader] = window.csrfToken;

            const response = await fetch('/api/cart/clear', {
                method: 'POST',
                headers: headers
            });

            const data = await response.json();

            if (data.success) {
                showToast('Корзина очищена', 'success');
                setTimeout(() => {
                    window.location.reload();
                }, 1000);
            } else {
                showToast(data.error || 'Ошибка очистки', 'error');
            }
        } catch (error) {
            showToast('Ошибка сети', 'error');
        }
    }

    async function applyPromoCode(code) {
        try {
            const headers = {
                'Content-Type': 'application/x-www-form-urlencoded',
            };
            headers[window.csrfHeader] = window.csrfToken;

            const response = await fetch('/api/cart/promo', {
                method: 'POST',
                headers: headers,
                body: `code=${encodeURIComponent(code)}`
            });

            const data = await response.json();

            if (data.success) {
                showToast('Промокод применен', 'success');
                setTimeout(() => {
                    window.location.reload();
                }, 1000);
            } else {
                showToast(data.error || 'Неверный промокод', 'error');
            }
        } catch (error) {
            showToast('Ошибка сети', 'error');
        }
    }

    function showToast(message, type = 'info') {
        const toast = document.createElement('div');
        <#noparse>
        toast.className = `p-4 rounded-lg shadow-lg text-white ${
                type === 'success' ? 'bg-green-500' :
                type === 'error' ? 'bg-red-500' :
                type === 'warning' ? 'bg-yellow-500' : 'bg-blue-500'
            } transform transition-all duration-300 translate-x-full opacity-0`;

        toast.innerHTML = `
                <div class="flex items-center space-x-2">
                    <i class="fas fa-${type === 'success' ? 'check' : type === 'error' ? 'exclamation-triangle' : type === 'warning' ? 'exclamation' : 'info-circle'}"></i>
                    <span>${message}</span>
                </div>
            `;
        </#noparse>

        document.getElementById('toast-container').appendChild(toast);

        // Animate in
        setTimeout(() => {
            toast.classList.remove('translate-x-full', 'opacity-0');
        }, 100);

        // Remove after 3 seconds
        setTimeout(() => {
            toast.classList.add('translate-x-full', 'opacity-0');
            setTimeout(() => {
                toast.remove();
            }, 300);
        }, 3000);
    }
</script>
</body>
</html>
