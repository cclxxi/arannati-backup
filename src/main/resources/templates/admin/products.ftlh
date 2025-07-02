<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="_csrf" content="${_csrf.token}"/>
    <meta name="_csrf_header" content="${_csrf.headerName}"/>
    <title>Управление товарами - Админ-панель Arannati</title>
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
<body class="bg-gray-50">
<!-- Admin Header -->
<#include "../fragments/header.ftlh">

<!-- Admin Sidebar -->
<div class="flex">

    <!-- Main Content -->
    <div class="flex-1 ml-64">
        <div class="p-8">
            <!-- Page Header -->
            <div class="flex justify-between items-center mb-8">
                <div>
                    <h1 class="text-3xl font-bold text-gray-900">Управление товарами</h1>
                    <p class="text-gray-600 mt-1">Добавляйте, редактируйте и управляйте товарами в каталоге</p>
                </div>
                <a href="/admin/products/add" class="bg-gradient-to-r from-primary to-secondary text-white px-6 py-3 rounded-xl font-semibold hover:shadow-lg transition">
                    <i class="fas fa-plus mr-2"></i>
                    Добавить товар
                </a>
            </div>

            <!-- Stats Cards -->
            <div class="grid grid-cols-1 md:grid-cols-4 gap-6 mb-8">
                <div class="bg-white rounded-2xl shadow-sm p-6">
                    <div class="flex items-center">
                        <div class="w-12 h-12 bg-blue-100 rounded-lg flex items-center justify-center">
                            <i class="fas fa-box text-blue-600 text-xl"></i>
                        </div>
                        <div class="ml-4">
                            <p class="text-sm font-medium text-gray-600">Всего товаров</p>
                            <p class="text-2xl font-bold text-gray-900">${products.totalElements}</p>
                        </div>
                    </div>
                </div>
                <div class="bg-white rounded-2xl shadow-sm p-6">
                    <div class="flex items-center">
                        <div class="w-12 h-12 bg-green-100 rounded-lg flex items-center justify-center">
                            <i class="fas fa-check-circle text-green-600 text-xl"></i>
                        </div>
                        <div class="ml-4">
                            <p class="text-sm font-medium text-gray-600">В наличии</p>
                            <p class="text-2xl font-bold text-gray-900">
                                <#assign inStockCount = 0>
                                <#if products.content??>
                                    <#list products.content as product>
                                        <#if product.stockQuantity?? && product.stockQuantity gt 0>
                                            <#assign inStockCount = inStockCount + 1>
                                        </#if>
                                    </#list>
                                </#if>
                                ${inStockCount}
                            </p>
                        </div>
                    </div>
                </div>
                <div class="bg-white rounded-2xl shadow-sm p-6">
                    <div class="flex items-center">
                        <div class="w-12 h-12 bg-yellow-100 rounded-lg flex items-center justify-center">
                            <i class="fas fa-exclamation-triangle text-yellow-600 text-xl"></i>
                        </div>
                        <div class="ml-4">
                            <p class="text-sm font-medium text-gray-600">Заканчиваются</p>
                            <p class="text-2xl font-bold text-gray-900">
                                <#assign lowStockCount = 0>
                                <#if products.content??>
                                    <#list products.content as product>
                                        <#if product.stockQuantity?? && product.stockQuantity <= 10 && product.stockQuantity > 0>
                                            <#assign lowStockCount = lowStockCount + 1>
                                        </#if>
                                    </#list>
                                </#if>
                                ${lowStockCount}
                            </p>
                        </div>
                    </div>
                </div>
                <div class="bg-white rounded-2xl shadow-sm p-6">
                    <div class="flex items-center">
                        <div class="w-12 h-12 bg-red-100 rounded-lg flex items-center justify-center">
                            <i class="fas fa-times-circle text-red-600 text-xl"></i>
                        </div>
                        <div class="ml-4">
                            <p class="text-sm font-medium text-gray-600">Нет в наличии</p>
                            <p class="text-2xl font-bold text-gray-900">
                                <#assign outOfStockCount = 0>
                                <#if products.content??>
                                    <#list products.content as product>
                                        <#if !product.stockQuantity?? || product.stockQuantity == 0>
                                            <#assign outOfStockCount = outOfStockCount + 1>
                                        </#if>
                                    </#list>
                                </#if>
                                ${outOfStockCount}
                            </p>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Filters -->
            <div class="bg-white rounded-2xl shadow-sm p-6 mb-8">
                <form method="GET" action="/admin/products" class="grid grid-cols-1 md:grid-cols-4 gap-4">
                    <div>
                        <label class="block text-sm font-medium text-gray-700 mb-1">Поиск</label>
                        <input type="text" name="search" value="${search!''}" placeholder="Название, артикул..."
                               class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary focus:border-transparent">
                    </div>
                    <div>
                        <label class="block text-sm font-medium text-gray-700 mb-1">Категория</label>
                        <select name="categoryId" class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary focus:border-transparent">
                            <option value="">Все категории</option>
                            <#if categories??>
                                <#list categories as category>
                                    <option value="${category.id}" ${(categoryId?? && categoryId == category.id)?then('selected', '')}>
                                        ${category.name}
                                    </option>
                                </#list>
                            </#if>
                        </select>
                    </div>
                    <div>
                        <label class="block text-sm font-medium text-gray-700 mb-1">Бренд</label>
                        <select name="brandId" class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary focus:border-transparent">
                            <option value="">Все бренды</option>
                            <#if brands??>
                                <#list brands as brand>
                                    <option value="${brand.id}" ${(brandId?? && brandId == brand.id)?then('selected', '')}>
                                        ${brand.name}
                                    </option>
                                </#list>
                            </#if>
                        </select>
                    </div>
                    <div class="flex items-end space-x-2">
                        <button type="submit" class="flex-1 bg-primary text-white py-2 px-4 rounded-lg hover:bg-purple-700 transition">
                            <i class="fas fa-search mr-2"></i>
                            Найти
                        </button>
                        <a href="/admin/products" class="bg-gray-100 text-gray-700 py-2 px-4 rounded-lg hover:bg-gray-200 transition">
                            <i class="fas fa-times"></i>
                        </a>
                    </div>
                </form>
            </div>

            <!-- Products Table -->
            <div class="bg-white rounded-2xl shadow-sm overflow-hidden">
                <div class="p-6 border-b border-gray-200">
                    <div class="flex items-center justify-between">
                        <h2 class="text-xl font-bold text-gray-900">Товары</h2>
                        <div class="flex items-center space-x-2">
                                <span class="text-sm text-gray-500">
                                    Показано ${products.content?size} из ${products.totalElements}
                                </span>
                        </div>
                    </div>
                </div>

                <#if products.content?? && products.content?size gt 0>
                    <div class="overflow-x-auto">
                        <table class="min-w-full divide-y divide-gray-200">
                            <thead class="bg-gray-50">
                            <tr>
                                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                    Товар
                                </th>
                                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                    Категория/Бренд
                                </th>
                                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                    Цены
                                </th>
                                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                    Остаток
                                </th>
                                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                    Статус
                                </th>
                                <th class="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">
                                    Действия
                                </th>
                            </tr>
                            </thead>
                            <tbody class="bg-white divide-y divide-gray-200">
                            <#list products.content as product>
                                <tr class="hover:bg-gray-50">
                                    <td class="px-6 py-4 whitespace-nowrap">
                                        <div class="flex items-center">
                                            <div class="flex-shrink-0 h-12 w-12">
                                                <#if product.images?? && product.images?size gt 0>
                                                    <img src="${product.images[0].imagePath}"
                                                         alt="${product.name}"
                                                         class="h-12 w-12 rounded-lg object-cover">
                                                <#else>
                                                    <div class="h-12 w-12 rounded-lg bg-gray-200 flex items-center justify-center">
                                                        <i class="fas fa-image text-gray-400"></i>
                                                    </div>
                                                </#if>
                                            </div>
                                            <div class="ml-4">
                                                <div class="text-sm font-medium text-gray-900">${product.name}</div>
                                                <div class="text-sm text-gray-500">Артикул: ${product.sku!'Н/Д'}</div>
                                            </div>
                                        </div>
                                    </td>
                                    <td class="px-6 py-4 whitespace-nowrap">
                                        <div class="text-sm text-gray-900">${product.categoryName!'Без категории'}</div>
                                        <div class="text-sm text-gray-500">${product.brandName!'Без бренда'}</div>
                                    </td>
                                    <td class="px-6 py-4 whitespace-nowrap">
                                        <div class="space-y-1">
                                            <div class="text-sm">
                                                <span class="text-gray-500">Обычная:</span>
                                                <span class="font-medium">${product.regularPrice?string.currency} ₸</span>
                                            </div>
                                            <#if product.salePrice?? && product.salePrice < product.regularPrice>
                                                <div class="text-sm">
                                                    <span class="text-gray-500">Акция:</span>
                                                    <span class="font-medium text-red-600">${product.salePrice?string.currency} ₸</span>
                                                </div>
                                            </#if>
                                            <#if product.cosmetologistPrice??>
                                                <div class="text-sm">
                                                    <span class="text-gray-500">Косметолог:</span>
                                                    <span class="font-medium text-purple-600">${product.cosmetologistPrice?string.currency} ₸</span>
                                                </div>
                                            </#if>
                                            <#if product.adminPrice??>
                                                <div class="text-sm">
                                                    <span class="text-gray-500">Админ:</span>
                                                    <span class="font-medium text-blue-600">${product.adminPrice?string.currency} ₸</span>
                                                </div>
                                            </#if>
                                        </div>
                                    </td>
                                    <td class="px-6 py-4 whitespace-nowrap">
                                        <div class="flex items-center">
                                            <input type="number" value="${product.stockQuantity!0}" min="0"
                                                   class="w-20 px-2 py-1 text-sm border border-gray-300 rounded focus:ring-2 focus:ring-primary focus:border-transparent stock-input"
                                                   data-product-id="${product.id}">
                                            <button onclick="updateStock(${product.id})"
                                                    class="ml-2 text-primary hover:text-purple-700 transition">
                                                <i class="fas fa-save"></i>
                                            </button>
                                        </div>
                                        <#if product.stockQuantity??>
                                            <#if product.stockQuantity == 0>
                                                <span class="inline-flex px-2 py-1 text-xs font-semibold rounded-full bg-red-100 text-red-800 mt-1">
                                                            Нет в наличии
                                                        </span>
                                            <#elseif product.stockQuantity <= 10>
                                                <span class="inline-flex px-2 py-1 text-xs font-semibold rounded-full bg-yellow-100 text-yellow-800 mt-1">
                                                            Заканчивается
                                                        </span>
                                            <#else>
                                                <span class="inline-flex px-2 py-1 text-xs font-semibold rounded-full bg-green-100 text-green-800 mt-1">
                                                            В наличии
                                                        </span>
                                            </#if>
                                        </#if>
                                    </td>
                                    <td class="px-6 py-4 whitespace-nowrap">
                                        <#if product.active?? && product.active>
                                            <span class="inline-flex px-2 py-1 text-xs font-semibold rounded-full bg-green-100 text-green-800">
                                                        Активен
                                                    </span>
                                        <#else>
                                            <span class="inline-flex px-2 py-1 text-xs font-semibold rounded-full bg-gray-100 text-gray-800">
                                                        Неактивен
                                                    </span>
                                        </#if>
                                    </td>
                                    <td class="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                                        <div class="flex items-center justify-end space-x-2">
                                            <button onclick="openPriceModal(${product.id}, '${product.name}', ${product.regularPrice!0}, ${product.salePrice!0})"
                                                    class="text-blue-600 hover:text-blue-900 transition" title="Изменить цены">
                                                <i class="fas fa-dollar-sign"></i>
                                            </button>
                                            <a href="/admin/products/${product.id}/edit"
                                               class="text-indigo-600 hover:text-indigo-900 transition" title="Редактировать">
                                                <i class="fas fa-edit"></i>
                                            </a>
                                            <a href="/product/${product.id}" target="_blank"
                                               class="text-green-600 hover:text-green-900 transition" title="Просмотр">
                                                <i class="fas fa-eye"></i>
                                            </a>
                                            <button onclick="toggleProductStatus(${product.id}, ${product.active?c})"
                                                    class="text-yellow-600 hover:text-yellow-900 transition"
                                                    title="${product.active?then('Деактивировать', 'Активировать')}">
                                                <i class="fas fa-${product.active?then('pause', 'play')}"></i>
                                            </button>
                                            <button onclick="deleteProduct(${product.id}, '${product.name}')"
                                                    class="text-red-600 hover:text-red-900 transition" title="Удалить">
                                                <i class="fas fa-trash"></i>
                                            </button>
                                        </div>
                                    </td>
                                </tr>
                            </#list>
                            </tbody>
                        </table>
                    </div>

                    <!-- Pagination -->
                    <#if products.totalPages gt 1>
                        <div class="px-6 py-4 border-t border-gray-200">
                            <div class="flex items-center justify-between">
                                <div class="text-sm text-gray-700">
                                    Показано <span class="font-medium">${products.number * products.size + 1}</span> -
                                    <span class="font-medium">${((products.number + 1) * products.size > products.totalElements)?then(products.totalElements, (products.number + 1) * products.size)}</span>
                                    из <span class="font-medium">${products.totalElements}</span> результатов
                                </div>
                                <nav class="flex items-center space-x-2">
                                    <#if products.hasPrevious()>
                                        <a href="?page=${products.number - 1}${getQueryParams()}"
                                           class="px-3 py-2 text-gray-500 hover:text-primary transition">
                                            <i class="fas fa-chevron-left"></i>
                                        </a>
                                    </#if>

                                    <#list 0..products.totalPages-1 as pageNum>
                                        <#if pageNum == products.number>
                                            <span class="px-4 py-2 bg-primary text-white rounded-lg font-medium">${pageNum + 1}</span>
                                        <#elseif (pageNum >= products.number - 2 && pageNum <= products.number + 2)>
                                            <a href="?page=${pageNum}${getQueryParams()}"
                                               class="px-4 py-2 text-gray-500 hover:text-primary hover:bg-gray-100 rounded-lg transition">
                                                ${pageNum + 1}
                                            </a>
                                        </#if>
                                    </#list>

                                    <#if products.hasNext()>
                                        <a href="?page=${products.number + 1}${getQueryParams()}"
                                           class="px-3 py-2 text-gray-500 hover:text-primary transition">
                                            <i class="fas fa-chevron-right"></i>
                                        </a>
                                    </#if>
                                </nav>
                            </div>
                        </div>
                    </#if>
                <#else>
                    <div class="p-12 text-center">
                        <div class="w-24 h-24 bg-gray-100 rounded-full flex items-center justify-center mx-auto mb-6">
                            <i class="fas fa-box text-4xl text-gray-400"></i>
                        </div>
                        <h3 class="text-xl font-semibold text-gray-900 mb-4">Товары не найдены</h3>
                        <p class="text-gray-500 mb-6">Попробуйте изменить параметры поиска или добавьте новый товар</p>
                        <a href="/admin/products/add" class="bg-primary text-white px-6 py-3 rounded-lg hover:bg-purple-700 transition">
                            <i class="fas fa-plus mr-2"></i>
                            Добавить товар
                        </a>
                    </div>
                </#if>
            </div>
        </div>
    </div>
</div>

<!-- Price Edit Modal -->
<div id="price-modal" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 hidden">
    <div class="bg-white rounded-2xl p-8 max-w-md w-full mx-4">
        <div class="flex items-center justify-between mb-6">
            <h2 class="text-xl font-bold text-gray-900">Изменить цены</h2>
            <button onclick="closePriceModal()" class="text-gray-400 hover:text-gray-600">
                <i class="fas fa-times"></i>
            </button>
        </div>

        <form id="price-form">
            <input type="hidden" id="modal-product-id">

            <div class="space-y-4">
                <div>
                    <label class="block text-sm font-medium text-gray-700 mb-1">Обычная цена (₸) *</label>
                    <input type="number" id="modal-regular-price" step="0.01" min="0" required
                           class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary focus:border-transparent">
                </div>

                <div>
                    <label class="block text-sm font-medium text-gray-700 mb-1">Акционная цена (₸)</label>
                    <input type="number" id="modal-sale-price" step="0.01" min="0"
                           class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary focus:border-transparent">
                </div>

                <div>
                    <label class="block text-sm font-medium text-gray-700 mb-1">Скидка (%)</label>
                    <input type="number" id="modal-discount-percent" step="0.01" min="0" max="100"
                           class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary focus:border-transparent">
                    <p class="text-sm text-gray-500 mt-1">Скидка будет рассчитана автоматически от обычной цены</p>
                </div>
            </div>

            <div class="flex space-x-4 mt-8">
                <button type="submit" class="flex-1 bg-primary text-white py-2 px-4 rounded-lg hover:bg-purple-700 transition">
                    Сохранить
                </button>
                <button type="button" onclick="closePriceModal()" class="flex-1 bg-gray-200 text-gray-800 py-2 px-4 rounded-lg hover:bg-gray-300 transition">
                    Отмена
                </button>
            </div>
        </form>
    </div>
</div>

<!-- Toast Notifications -->
<div id="toast-container" class="fixed bottom-4 right-4 z-50 space-y-2"></div>

<script>
    document.addEventListener('DOMContentLoaded', function() {
        // Get CSRF token
        window.csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
        window.csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

        // Price form handling
        document.getElementById('price-form').addEventListener('submit', async function(e) {
            e.preventDefault();
            await updateProductPricing();
        });

        // Auto-calculate discount
        document.getElementById('modal-discount-percent').addEventListener('input', function() {
            const regularPrice = parseFloat(document.getElementById('modal-regular-price').value) || 0;
            const discountPercent = parseFloat(this.value) || 0;

            if (regularPrice > 0 && discountPercent > 0) {
                const salePrice = regularPrice * (1 - discountPercent / 100);
                document.getElementById('modal-sale-price').value = salePrice.toFixed(2);
            }
        });

        // Auto-calculate percentage when sale price changes
        document.getElementById('modal-sale-price').addEventListener('input', function() {
            const regularPrice = parseFloat(document.getElementById('modal-regular-price').value) || 0;
            const salePrice = parseFloat(this.value) || 0;

            if (regularPrice > 0 && salePrice > 0 && salePrice < regularPrice) {
                const discountPercent = ((regularPrice - salePrice) / regularPrice) * 100;
                document.getElementById('modal-discount-percent').value = discountPercent.toFixed(2);
            }
        });
    });

    function openPriceModal(productId, productName, regularPrice, salePrice) {
        document.getElementById('modal-product-id').value = productId;
        document.getElementById('modal-regular-price').value = regularPrice;
        document.getElementById('modal-sale-price').value = salePrice || '';
        document.getElementById('price-modal').classList.remove('hidden');
    }

    function closePriceModal() {
        document.getElementById('price-modal').classList.add('hidden');
        document.getElementById('price-form').reset();
    }

    async function updateProductPricing() {
        const productId = document.getElementById('modal-product-id').value;
        const regularPrice = document.getElementById('modal-regular-price').value;
        const salePrice = document.getElementById('modal-sale-price').value;
        const discountPercentage = document.getElementById('modal-discount-percent').value;

        try {
            const headers = {
                'Content-Type': 'application/x-www-form-urlencoded',
            };
            headers[window.csrfHeader] = window.csrfToken;

            const response = await fetch(`/admin/api/products/${productId}/pricing`, {
                method: 'POST',
                headers: headers,
                body: new URLSearchParams({
                    regularPrice: regularPrice,
                    salePrice: salePrice || '',
                    discountPercentage: discountPercentage || ''
                })
            });

            const data = await response.json();

            if (data.success) {
                showToast('Цены обновлены успешно', 'success');
                closePriceModal();
                setTimeout(() => {
                    window.location.reload();
                }, 1000);
            } else {
                showToast(data.error || 'Ошибка обновления цен', 'error');
            }
        } catch (error) {
            showToast('Ошибка сети', 'error');
        }
    }

    async function updateStock(productId) {
        const stockInput = document.querySelector(`[data-product-id="${productId}"].stock-input`);
        const stockQuantity = parseInt(stockInput.value);

        try {
            const headers = {
                'Content-Type': 'application/x-www-form-urlencoded',
            };
            headers[window.csrfHeader] = window.csrfToken;

            const response = await fetch(`/admin/api/products/${productId}/stock`, {
                method: 'POST',
                headers: headers,
                body: `stockQuantity=${stockQuantity}`
            });

            const data = await response.json();

            if (data.success) {
                showToast('Количество обновлено', 'success');
                setTimeout(() => {
                    window.location.reload();
                }, 1000);
            } else {
                showToast(data.error || 'Ошибка обновления количества', 'error');
            }
        } catch (error) {
            showToast('Ошибка сети', 'error');
        }
    }

    async function toggleProductStatus(productId, currentStatus) {
        const action = currentStatus ? 'деактивировать' : 'активировать';

        if (!confirm(`Вы уверены, что хотите ${action} этот товар?`)) {
            return;
        }

        try {
            const headers = {};
            headers[window.csrfHeader] = window.csrfToken;

            const response = await fetch(`/admin/api/products/${productId}/toggle-status`, {
                method: 'POST',
                headers: headers
            });

            const data = await response.json();

            if (data.success) {
                <#noparse>
                showToast(`Товар ${currentStatus ? 'деактивирован' : 'активирован'}`, 'success');
                </#noparse>
                setTimeout(() => {
                    window.location.reload();
                }, 1000);
            } else {
                showToast(data.error || 'Ошибка изменения статуса', 'error');
            }
        } catch (error) {
            showToast('Ошибка сети', 'error');
        }
    }

    async function deleteProduct(productId, productName) {
        if (!confirm(`Вы уверены, что хотите удалить товар "${productName}"? Это действие нельзя отменить.`)) {
            return;
        }

        try {
            const headers = {};
            headers[window.csrfHeader] = window.csrfToken;

            const response = await fetch(`/admin/api/products/${productId}/delete`, {
                method: 'POST',
                headers: headers
            });

            const data = await response.json();

            if (data.success) {
                showToast('Товар удален', 'success');
                setTimeout(() => {
                    window.location.reload();
                }, 1000);
            } else {
                showToast(data.error || 'Ошибка удаления товара', 'error');
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
        }, 3000);
    }

    <#-- Helper function for pagination links -->
    <#function getQueryParams>
    <#assign params = "">
    <#if search??><#assign params = params + "&search=" + search></#if>
    <#if categoryId??><#assign params = params + "&categoryId=" + categoryId></#if>
    <#if brandId??><#assign params = params + "&brandId=" + brandId></#if>
    <#return params>
    </#function>
</script>
</body>
</html>
