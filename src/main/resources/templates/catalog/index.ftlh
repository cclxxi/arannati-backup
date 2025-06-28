<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="_csrf" content="${_csrf.token}"/>
    <meta name="_csrf_header" content="${_csrf.headerName}"/>
    <title>Каталог косметики - Arannati</title>
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
<!-- Header -->
<#include "../fragments/header.ftlh">

<!-- Main Content -->
<div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
    <div class="flex flex-col lg:flex-row gap-8">
        <!-- Filters Sidebar -->
        <div class="lg:w-1/4">
            <div class="bg-white rounded-2xl shadow-sm p-6 sticky top-8">
                <h2 class="text-xl font-bold text-gray-900 mb-6">Фильтры</h2>

                <form method="GET" action="/" id="filters-form">
                    <!-- Search -->
                    <div class="mb-6">
                        <label class="block text-sm font-medium text-gray-700 mb-2">Поиск</label>
                        <div class="relative">
                            <input type="text" name="search" value="${search!''}"
                                   placeholder="Название товара..."
                                   class="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary focus:border-transparent">
                            <i class="fas fa-search absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400"></i>
                        </div>
                    </div>

                    <!-- Categories -->
                    <div class="mb-6">
                        <label class="block text-sm font-medium text-gray-700 mb-3">Категории</label>
                        <div class="space-y-2 max-h-48 overflow-y-auto">
                            <label class="flex items-center">
                                <input type="radio" name="categoryId" value="" ${(!categoryId??)?then('checked', '')}
                                       class="text-primary focus:ring-primary border-gray-300">
                                <span class="ml-2 text-sm text-gray-700">Все категории</span>
                            </label>
                            <#if categories??>
                                <#list categories as category>
                                    <label class="flex items-center">
                                        <input type="radio" name="categoryId" value="${category.id}"
                                                ${(categoryId?? && categoryId == category.id)?then('checked', '')}
                                               class="text-primary focus:ring-primary border-gray-300">
                                        <span class="ml-2 text-sm text-gray-700">${category.name}</span>
                                    </label>
                                    <!-- Subcategories -->
                                    <#if category.children?? && category.children?size gt 0>
                                        <div class="ml-4 space-y-1">
                                            <#list category.children as subcategory>
                                                <label class="flex items-center">
                                                    <input type="radio" name="categoryId" value="${subcategory.id}"
                                                            ${(categoryId?? && categoryId == subcategory.id)?then('checked', '')}
                                                           class="text-primary focus:ring-primary border-gray-300">
                                                    <span class="ml-2 text-sm text-gray-600">${subcategory.name}</span>
                                                </label>
                                            </#list>
                                        </div>
                                    </#if>
                                </#list>
                            </#if>
                        </div>
                    </div>

                    <!-- Brands -->
                    <div class="mb-6">
                        <label class="block text-sm font-medium text-gray-700 mb-3">Бренды</label>
                        <div class="space-y-2 max-h-48 overflow-y-auto">
                            <label class="flex items-center">
                                <input type="radio" name="brandId" value="" ${(!brandId??)?then('checked', '')}
                                       class="text-primary focus:ring-primary border-gray-300">
                                <span class="ml-2 text-sm text-gray-700">Все бренды</span>
                            </label>
                            <#if brands??>
                                <#list brands as brand>
                                    <label class="flex items-center">
                                        <input type="radio" name="brandId" value="${brand.id}"
                                                ${(brandId?? && brandId == brand.id)?then('checked', '')}
                                               class="text-primary focus:ring-primary border-gray-300">
                                        <span class="ml-2 text-sm text-gray-700">${brand.name}</span>
                                    </label>
                                </#list>
                            </#if>
                        </div>
                    </div>

                    <!-- Price Range -->
                    <div class="mb-6">
                        <label class="block text-sm font-medium text-gray-700 mb-3">Цена (₸)</label>
                        <div class="grid grid-cols-2 gap-2">
                            <input type="number" name="minPrice" value="${minPrice!''}" placeholder="От"
                                   class="px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary focus:border-transparent text-sm">
                            <input type="number" name="maxPrice" value="${maxPrice!''}" placeholder="До"
                                   class="px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary focus:border-transparent text-sm">
                        </div>
                    </div>

                    <!-- Additional Filters -->
                    <div class="mb-6">
                        <label class="block text-sm font-medium text-gray-700 mb-3">Дополнительно</label>
                        <div class="space-y-2">
                            <label class="flex items-center">
                                <input type="checkbox" name="onSale" value="true" ${onSale?then('checked', '')}
                                       class="text-primary focus:ring-primary border-gray-300 rounded">
                                <span class="ml-2 text-sm text-gray-700">Товары со скидкой</span>
                            </label>
                            <label class="flex items-center">
                                <input type="checkbox" name="inStock" value="true" ${inStock?then('checked', '')}
                                       class="text-primary focus:ring-primary border-gray-300 rounded">
                                <span class="ml-2 text-sm text-gray-700">В наличии</span>
                            </label>
                        </div>
                    </div>

                    <!-- Filter Actions -->
                    <div class="flex space-x-2">
                        <button type="submit" class="flex-1 bg-primary text-white py-2 px-4 rounded-lg hover:bg-purple-700 transition text-sm font-medium">
                            Применить
                        </button>
                        <button type="button" onclick="clearFilters()" class="flex-1 bg-gray-100 text-gray-700 py-2 px-4 rounded-lg hover:bg-gray-200 transition text-sm font-medium">
                            Сбросить
                        </button>
                    </div>
                </form>
            </div>
        </div>

        <!-- Products Section -->
        <div class="lg:w-3/4">
            <!-- Toolbar -->
            <div class="bg-white rounded-2xl shadow-sm p-4 mb-6">
                <div class="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
                    <div class="flex items-center space-x-4">
                            <span class="text-gray-600">
                                <#if products?? && products.content??>
                                    Найдено: ${products.totalElements} товар<#if products.totalElements gt 1>ов</#if>
                                <#else>
                                    Товары не найдены
                                </#if>
                            </span>
                        <#if currentUser?? && (currentUser.role == "COSMETOLOGIST" || currentUser.role == "ADMIN")>
                            <span class="bg-purple-100 text-purple-800 px-3 py-1 rounded-full text-sm font-medium">
                                    <#if currentUser.role == "COSMETOLOGIST">
                                        Цены косметолога (-25%)
                                    <#else>
                                        Цены администратора (-40%)
                                    </#if>
                                </span>
                        </#if>
                    </div>

                    <div class="flex items-center space-x-4">
                        <!-- Sort -->
                        <select name="sort" onchange="updateSort(this.value)" class="border border-gray-300 rounded-lg px-3 py-2 text-sm focus:ring-2 focus:ring-primary focus:border-transparent">
                            <option value="name,asc" ${(sort?? && sort == "name,asc")?then('selected', '')}>По названию</option>
                            <option value="price,asc" ${(sort?? && sort == "price,asc")?then('selected', '')}>Цена: по возрастанию</option>
                            <option value="price,desc" ${(sort?? && sort == "price,desc")?then('selected', '')}>Цена: по убыванию</option>
                            <option value="created,desc" ${(sort?? && sort == "created,desc")?then('selected', '')}>Новинки</option>
                        </select>

                        <!-- View Toggle -->
                        <div class="flex border border-gray-300 rounded-lg">
                            <button onclick="setGridView()" id="grid-view" class="p-2 hover:bg-gray-100 transition rounded-l-lg">
                                <i class="fas fa-th-large"></i>
                            </button>
                            <button onclick="setListView()" id="list-view" class="p-2 hover:bg-gray-100 transition rounded-r-lg">
                                <i class="fas fa-list"></i>
                            </button>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Products Grid -->
            <#if products?? && products.content?? && products.content?size gt 0>
                <div id="products-container" class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
                    <#list products.content as product>
                        <div class="bg-white rounded-2xl shadow-sm hover:shadow-lg transition-shadow overflow-hidden group">
                            <!-- Product Image -->
                            <div class="relative">
                                <#if product.images?? && product.images?size gt 0>
                                    <img src="${product.images[0].imagePath}"
                                         alt="${product.name}"
                                         class="w-full h-48 object-cover group-hover:scale-105 transition-transform duration-300">
                                <#else>
                                    <div class="w-full h-48 bg-gray-200 flex items-center justify-center">
                                        <i class="fas fa-image text-4xl text-gray-400"></i>
                                    </div>
                                </#if>

                                <!-- Badges -->
                                <div class="absolute top-3 left-3 space-y-1">
                                    <#if product.salePrice?? && product.salePrice < product.regularPrice>
                                        <#assign discountPercent = (((product.regularPrice - product.salePrice) / product.regularPrice) * 100)?round>
                                        <span class="bg-red-500 text-white px-2 py-1 rounded-full text-xs font-bold">
                                                -${discountPercent}%
                                            </span>
                                    </#if>
                                    <#if product.stockQuantity?? && product.stockQuantity <= 5 && product.stockQuantity > 0>
                                        <span class="bg-orange-500 text-white px-2 py-1 rounded-full text-xs font-bold">
                                                Осталось ${product.stockQuantity}
                                            </span>
                                    </#if>
                                    <#if product.stockQuantity?? && product.stockQuantity == 0>
                                        <span class="bg-gray-500 text-white px-2 py-1 rounded-full text-xs font-bold">
                                                Нет в наличии
                                            </span>
                                    </#if>
                                </div>

                                <!-- Quick Actions -->
                                <#if isAuthenticated?? && isAuthenticated>
                                    <div class="absolute top-3 right-3 opacity-0 group-hover:opacity-100 transition-opacity space-y-2">
                                        <button class="block w-10 h-10 bg-white rounded-full shadow-md hover:bg-gray-50 transition wishlist-btn"
                                                data-product-id="${product.id}" title="Добавить в избранное">
                                            <i class="fas fa-heart text-gray-400 hover:text-red-500"></i>
                                        </button>
                                        <#if product.stockQuantity?? && product.stockQuantity gt 0>
                                            <button class="block w-10 h-10 bg-white rounded-full shadow-md hover:bg-gray-50 transition quick-add-btn"
                                                    data-product-id="${product.id}" title="Быстро в корзину">
                                                <i class="fas fa-shopping-cart text-gray-400 hover:text-primary"></i>
                                            </button>
                                        </#if>
                                    </div>
                                </#if>
                            </div>

                            <!-- Product Info -->
                            <div class="p-4">
                                <!-- Brand -->
                                <p class="text-sm text-gray-500 mb-1">${product.brandName!'Без бренда'}</p>

                                <!-- Name -->
                                <h3 class="font-semibold text-gray-900 mb-2 line-clamp-2">
                                    <a href="/product/${product.id}" class="hover:text-primary transition">
                                        ${product.name}
                                    </a>
                                </h3>

                                <!-- Short Description -->
                                <#if product.shortDescription??>
                                    <p class="text-sm text-gray-600 mb-3 line-clamp-2">${product.shortDescription}</p>
                                </#if>

                                <!-- Pricing -->
                                <div class="flex items-center space-x-2 mb-3">
                                    <#if isAuthenticated?? && isAuthenticated>
                                    <#-- Authenticated user sees role-based pricing -->
                                        <#if currentUser.role == "COSMETOLOGIST" && product.cosmetologistPrice??>
                                            <span class="text-lg font-bold text-primary">${product.cosmetologistPrice?string.currency} ₸</span>
                                            <span class="text-sm text-gray-400 line-through">${product.regularPrice?string.currency} ₸</span>
                                        <#elseif currentUser.role == "ADMIN" && product.adminPrice??>
                                            <span class="text-lg font-bold text-primary">${product.adminPrice?string.currency} ₸</span>
                                            <span class="text-sm text-gray-400 line-through">${product.regularPrice?string.currency} ₸</span>
                                        <#elseif product.salePrice?? && product.salePrice < product.regularPrice>
                                            <span class="text-lg font-bold text-primary">${product.salePrice?string.currency} ₸</span>
                                            <span class="text-sm text-gray-400 line-through">${product.regularPrice?string.currency} ₸</span>
                                        <#else>
                                            <span class="text-lg font-bold text-gray-900">${product.regularPrice?string.currency} ₸</span>
                                        </#if>
                                    <#else>
                                    <#-- Anonymous user sees sale price or regular price -->
                                        <#if product.salePrice?? && product.salePrice < product.regularPrice>
                                            <span class="text-lg font-bold text-primary">${product.salePrice?string.currency} ₸</span>
                                            <span class="text-sm text-gray-400 line-through">${product.regularPrice?string.currency} ₸</span>
                                        <#else>
                                            <span class="text-lg font-bold text-gray-900">${product.regularPrice?string.currency} ₸</span>
                                        </#if>
                                    </#if>
                                </div>

                                <!-- Reviews -->
                                <#if product.averageRating?? && product.reviewCount?? && product.reviewCount gt 0>
                                    <div class="flex items-center space-x-1 mb-3">
                                        <div class="flex">
                                            <#list 1..5 as star>
                                                <i class="fas fa-star text-xs ${(star <= product.averageRating)?then('text-yellow-400', 'text-gray-300')}"></i>
                                            </#list>
                                        </div>
                                        <span class="text-sm text-gray-500">(${product.reviewCount})</span>
                                    </div>
                                </#if>

                                <!-- Action Button -->
                                <#if isAuthenticated?? && isAuthenticated>
                                    <#if product.stockQuantity?? && product.stockQuantity gt 0>
                                        <button class="w-full bg-gradient-to-r from-primary to-secondary text-white py-2 rounded-lg font-medium hover:shadow-md transition add-to-cart-btn"
                                                data-product-id="${product.id}">
                                            <i class="fas fa-shopping-cart mr-2"></i>
                                            В корзину
                                        </button>
                                    <#else>
                                        <button class="w-full bg-gray-300 text-gray-500 py-2 rounded-lg font-medium cursor-not-allowed" disabled>
                                            Нет в наличии
                                        </button>
                                    </#if>
                                <#else>
                                    <a href="/auth/login" class="block w-full bg-gray-100 text-gray-700 py-2 rounded-lg font-medium text-center hover:bg-gray-200 transition">
                                        Войти для покупки
                                    </a>
                                </#if>
                            </div>
                        </div>
                    </#list>
                </div>

                <!-- Pagination -->
                <#if products.totalPages gt 1>
                    <div class="mt-8 flex justify-center">
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
                                <#elseif pageNum == 0 || pageNum == products.totalPages - 1>
                                    <a href="?page=${pageNum}${getQueryParams()}"
                                       class="px-4 py-2 text-gray-500 hover:text-primary hover:bg-gray-100 rounded-lg transition">
                                        ${pageNum + 1}
                                    </a>
                                    <#if (pageNum == 0 && products.number > 3) || (pageNum == products.totalPages - 1 && products.number < products.totalPages - 4)>
                                        <span class="px-2 py-2 text-gray-400">...</span>
                                    </#if>
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
                </#if>
            <#else>
                <!-- No Products Found -->
                <div class="bg-white rounded-2xl shadow-sm p-12 text-center">
                    <div class="w-24 h-24 bg-gray-100 rounded-full flex items-center justify-center mx-auto mb-6">
                        <i class="fas fa-search text-4xl text-gray-400"></i>
                    </div>
                    <h3 class="text-xl font-semibold text-gray-900 mb-4">Товары не найдены</h3>
                    <p class="text-gray-500 mb-6">Попробуйте изменить параметры поиска или сбросить фильтры</p>
                    <button onclick="clearFilters()" class="bg-primary text-white px-6 py-3 rounded-lg hover:bg-purple-700 transition">
                        Сбросить фильтры
                    </button>
                </div>
            </#if>
        </div>
    </div>
</div>

<!-- Toast Notifications -->
<div id="toast-container" class="fixed bottom-4 right-4 z-50 space-y-2"></div>

<script>
    document.addEventListener('DOMContentLoaded', function() {
        // Set grid view as default
        setGridView();

        // Get CSRF token
        window.csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
        window.csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

        // Add to cart buttons
        document.querySelectorAll('.add-to-cart-btn, .quick-add-btn').forEach(btn => {
            btn.addEventListener('click', function() {
                const productId = this.dataset.productId;
                addToCart(productId);
            });
        });

        // Wishlist buttons
        document.querySelectorAll('.wishlist-btn').forEach(btn => {
            btn.addEventListener('click', function() {
                const productId = this.dataset.productId;
                toggleWishlist(productId);
            });
        });

        // Auto-submit filters on change
        document.querySelectorAll('input[name="categoryId"], input[name="brandId"]').forEach(input => {
            input.addEventListener('change', function() {
                document.getElementById('filters-form').submit();
            });
        });
    });

    async function addToCart(productId) {
        try {
            const headers = {
                'Content-Type': 'application/x-www-form-urlencoded',
            };
            headers[window.csrfHeader] = window.csrfToken;

            const response = await fetch('/api/cart/add', {
                method: 'POST',
                headers: headers,
                body: `productId=${productId}&quantity=1`
            });

            const data = await response.json();

            if (data.success) {
                showToast('Товар добавлен в корзину', 'success');
                updateCartCount(data.cartCount);
            } else {
                showToast(data.error || 'Ошибка добавления в корзину', 'error');
            }
        } catch (error) {
            showToast('Ошибка сети', 'error');
        }
    }

    async function toggleWishlist(productId) {
        try {
            const headers = {
                'Content-Type': 'application/x-www-form-urlencoded',
            };
            headers[window.csrfHeader] = window.csrfToken;

            const response = await fetch('/api/wishlist/toggle', {
                method: 'POST',
                headers: headers,
                body: `productId=${productId}`
            });

            const data = await response.json();

            if (data.success) {
                showToast(data.message, 'success');
                // Update wishlist icon
                const btn = document.querySelector(`[data-product-id="${productId}"].wishlist-btn`);
                if (btn) {
                    const icon = btn.querySelector('i');
                    if (data.inWishlist) {
                        icon.classList.remove('text-gray-400');
                        icon.classList.add('text-red-500');
                    } else {
                        icon.classList.remove('text-red-500');
                        icon.classList.add('text-gray-400');
                    }
                }
            } else {
                showToast(data.error || 'Ошибка', 'error');
            }
        } catch (error) {
            showToast('Ошибка сети', 'error');
        }
    }

    function updateSort(value) {
        const url = new URL(window.location);
        url.searchParams.set('sort', value);
        window.location.href = url.toString();
    }

    function setGridView() {
        const container = document.getElementById('products-container');
        const gridBtn = document.getElementById('grid-view');
        const listBtn = document.getElementById('list-view');

        container.className = 'grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6';
        gridBtn.classList.add('bg-primary', 'text-white');
        gridBtn.classList.remove('hover:bg-gray-100');
        listBtn.classList.remove('bg-primary', 'text-white');
        listBtn.classList.add('hover:bg-gray-100');
    }

    function setListView() {
        const container = document.getElementById('products-container');
        const gridBtn = document.getElementById('grid-view');
        const listBtn = document.getElementById('list-view');

        container.className = 'space-y-4';
        listBtn.classList.add('bg-primary', 'text-white');
        listBtn.classList.remove('hover:bg-gray-100');
        gridBtn.classList.remove('bg-primary', 'text-white');
        gridBtn.classList.add('hover:bg-gray-100');
    }

    function clearFilters() {
        window.location.href = '/';
    }

    function updateCartCount(count) {
        const cartCountElement = document.getElementById('cart-count');
        if (cartCountElement) {
            cartCountElement.textContent = count;
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
    <#if minPrice??><#assign params = params + "&minPrice=" + minPrice></#if>
    <#if maxPrice??><#assign params = params + "&maxPrice=" + maxPrice></#if>
    <#if onSale??><#assign params = params + "&onSale=" + onSale></#if>
    <#if inStock??><#assign params = params + "&inStock=" + inStock></#if>
    <#if sort??><#assign params = params + "&sort=" + sort></#if>
    <#return params>
    </#function>
</script>

<style>
    .line-clamp-2 {
        display: -webkit-box;
        -webkit-line-clamp: 2;
        -webkit-box-orient: vertical;
        overflow: hidden;
    }
</style>
</body>
</html>
