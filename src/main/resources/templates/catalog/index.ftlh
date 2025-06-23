<#assign pageTitle = "Arannati - Каталог продукции">
<#assign pageDescription = "Каталог премиальной космецевтики и БАДов Arannati">
<#assign additionalJS = ["/static/js/pages/catalog.js"]>
<#include "../layouts/main.ftlh">

<@layout>
    <div class="container mx-auto px-4 py-8">
        <!-- Hero Section -->
        <div class="bg-white shadow-md rounded-lg p-6 mb-6">
            <div class="flex flex-col md:flex-row items-center">
                <div class="md:w-1/2 mb-6 md:mb-0 md:pr-8">
                    <h1 class="text-3xl font-bold text-gray-800 mb-4">Каталог продукции Arannati</h1>
                    <p class="text-gray-600 mb-6">
                        Премиальная космецевтика и БАДы для профессионального ухода за кожей. 
                        Наши продукты разработаны с использованием передовых технологий и натуральных ингредиентов.
                    </p>
                    <div class="flex flex-wrap gap-2">
                        <span class="px-3 py-1 bg-blue-100 text-blue-800 rounded-full text-sm">Натуральные ингредиенты</span>
                        <span class="px-3 py-1 bg-green-100 text-green-800 rounded-full text-sm">Без парабенов</span>
                        <span class="px-3 py-1 bg-purple-100 text-purple-800 rounded-full text-sm">Не тестируется на животных</span>
                    </div>
                </div>
                <div class="md:w-1/2">
                    <img src="/static/images/hero-catalog.jpg" alt="Arannati продукция" class="rounded-lg shadow-md w-full h-auto">
                </div>
            </div>
        </div>

        <!-- Filters and Products -->
        <div class="flex flex-col lg:flex-row gap-6">
            <!-- Filters Sidebar -->
            <div class="lg:w-1/4">
                <div class="bg-white shadow-md rounded-lg p-6 sticky top-4">
                    <h2 class="text-xl font-semibold text-gray-800 mb-4">Фильтры</h2>

                    <form id="filter-form" action="/" method="get" class="space-y-6">
                        <!-- Search -->
                        <div>
                            <label for="search" class="block text-sm font-medium text-gray-700 mb-1">Поиск</label>
                            <div class="relative">
                                <input 
                                    type="text" 
                                    id="search" 
                                    name="search" 
                                    value="${search!''}"
                                    class="block w-full pl-3 pr-10 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                                    placeholder="Введите название продукта">
                                <div class="absolute inset-y-0 right-0 flex items-center pr-3">
                                    <i class="fas fa-search text-gray-400"></i>
                                </div>
                            </div>
                        </div>

                        <!-- Categories -->
                        <div>
                            <label class="block text-sm font-medium text-gray-700 mb-1">Категории</label>
                            <div class="space-y-2 max-h-48 overflow-y-auto pr-2">
                                <#list categories as category>
                                    <div class="flex items-center">
                                        <input 
                                            type="radio" 
                                            id="category-${category.id}" 
                                            name="categoryId" 
                                            value="${category.id}"
                                            <#if categoryId?? && categoryId == category.id>checked</#if>
                                            class="h-4 w-4 text-blue-600 focus:ring-blue-500 border-gray-300 rounded">
                                        <label for="category-${category.id}" class="ml-2 block text-sm text-gray-700">
                                            ${category.name}
                                        </label>
                                    </div>
                                </#list>
                            </div>
                        </div>

                        <!-- Brands -->
                        <div>
                            <label class="block text-sm font-medium text-gray-700 mb-1">Бренды</label>
                            <div class="space-y-2 max-h-48 overflow-y-auto pr-2">
                                <#list brands as brand>
                                    <div class="flex items-center">
                                        <input 
                                            type="checkbox" 
                                            id="brand-${brand.id}" 
                                            name="brandId" 
                                            value="${brand.id}"
                                            <#if brandId?? && brandId == brand.id>checked</#if>
                                            class="h-4 w-4 text-blue-600 focus:ring-blue-500 border-gray-300 rounded">
                                        <label for="brand-${brand.id}" class="ml-2 block text-sm text-gray-700">
                                            ${brand.name}
                                        </label>
                                    </div>
                                </#list>
                            </div>
                        </div>

                        <!-- Price Range -->
                        <div>
                            <label class="block text-sm font-medium text-gray-700 mb-1">Цена</label>
                            <div class="flex space-x-2">
                                <div class="w-1/2">
                                    <input 
                                        type="number" 
                                        id="minPrice" 
                                        name="minPrice" 
                                        value="${minPrice!''}"
                                        class="block w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                                        placeholder="От">
                                </div>
                                <div class="w-1/2">
                                    <input 
                                        type="number" 
                                        id="maxPrice" 
                                        name="maxPrice" 
                                        value="${maxPrice!''}"
                                        class="block w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                                        placeholder="До">
                                </div>
                            </div>
                        </div>

                        <!-- Professional Products -->
                        <div>
                            <div class="flex items-center">
                                <input 
                                    type="checkbox" 
                                    id="professional" 
                                    name="professional" 
                                    value="true"
                                    <#if professional?? && professional>checked</#if>
                                    class="h-4 w-4 text-blue-600 focus:ring-blue-500 border-gray-300 rounded">
                                <label for="professional" class="ml-2 block text-sm text-gray-700">
                                    Только профессиональные
                                </label>
                            </div>
                        </div>

                        <!-- Sort -->
                        <div>
                            <label for="sort" class="block text-sm font-medium text-gray-700 mb-1">Сортировка</label>
                            <select 
                                id="sort" 
                                name="sort" 
                                class="block w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-blue-500 focus:border-blue-500">
                                <option value="sortOrder,asc" selected>По умолчанию</option>
                                <option value="name,asc">По названию (А-Я)</option>
                                <option value="name,desc">По названию (Я-А)</option>
                                <option value="regularPrice,asc">По цене (возрастание)</option>
                                <option value="regularPrice,desc">По цене (убывание)</option>
                                <option value="createdAt,desc">Сначала новые</option>
                            </select>
                        </div>

                        <!-- Buttons -->
                        <div class="flex space-x-2">
                            <button 
                                type="submit" 
                                class="flex-1 bg-blue-600 text-white py-2 px-4 rounded-md hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2">
                                Применить
                            </button>
                            <button 
                                type="button" 
                                id="reset-filters" 
                                class="flex-1 bg-gray-200 text-gray-800 py-2 px-4 rounded-md hover:bg-gray-300 focus:outline-none focus:ring-2 focus:ring-gray-500 focus:ring-offset-2">
                                Сбросить
                            </button>
                        </div>
                    </form>
                </div>
            </div>

            <!-- Products Grid -->
            <div class="lg:w-3/4">
                <!-- Results Info -->
                <div class="bg-white shadow-md rounded-lg p-4 mb-6">
                    <div class="flex flex-col sm:flex-row justify-between items-center">
                        <p class="text-gray-600 mb-2 sm:mb-0">
                            Найдено <span class="font-semibold">${products.totalElements}</span> товаров
                        </p>
                        <div class="flex space-x-2">
                            <button class="view-mode-btn bg-gray-200 text-gray-800 py-1 px-3 rounded-md hover:bg-gray-300 focus:outline-none" data-mode="grid">
                                <i class="fas fa-th-large"></i>
                            </button>
                            <button class="view-mode-btn bg-gray-100 text-gray-600 py-1 px-3 rounded-md hover:bg-gray-300 focus:outline-none" data-mode="list">
                                <i class="fas fa-list"></i>
                            </button>
                        </div>
                    </div>
                </div>

                <!-- Products -->
                <div id="products-grid" class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
                    <#if products.content?size == 0>
                        <div class="col-span-full text-center py-12">
                            <div class="text-5xl text-gray-300 mb-4">
                                <i class="fas fa-search"></i>
                            </div>
                            <h3 class="text-xl font-medium text-gray-700 mb-2">Товары не найдены</h3>
                            <p class="text-gray-500 mb-4">Попробуйте изменить параметры поиска или фильтры</p>
                            <a href="/" class="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md shadow-sm text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500">
                                Сбросить все фильтры
                            </a>
                        </div>
                    <#else>
                        <#list products.content as product>
                            <div class="product-card bg-white shadow-md rounded-lg overflow-hidden transition-transform hover:shadow-lg hover:-translate-y-1">
                                <div class="relative">
                                    <#if product.images?? && product.images?size gt 0>
                                        <img src="${product.images[0].imagePath}" alt="${product.images[0].altText!product.name}" class="w-full h-48 object-cover">
                                    <#else>
                                        <div class="w-full h-48 bg-gray-200 flex items-center justify-center">
                                            <i class="fas fa-image text-gray-400 text-4xl"></i>
                                        </div>
                                    </#if>

                                    <#if product.salePrice?? && product.salePrice lt product.regularPrice>
                                        <span class="absolute top-2 right-2 bg-red-500 text-white text-xs font-bold px-2 py-1 rounded">
                                            СКИДКА
                                        </span>
                                    </#if>

                                    <#if product.professional>
                                        <span class="absolute top-2 left-2 bg-purple-500 text-white text-xs font-bold px-2 py-1 rounded">
                                            PROF
                                        </span>
                                    </#if>

                                    <button class="favorite-btn absolute bottom-2 right-2 bg-white rounded-full p-2 shadow hover:bg-gray-100 focus:outline-none" data-product-id="${product.id}">
                                        <i class="far fa-heart text-red-500"></i>
                                    </button>
                                </div>

                                <div class="p-4">
                                    <div class="flex items-center text-xs text-gray-500 mb-2">
                                        <span><#if product.brand??>${product.brand.name}<#else>Бренд не указан</#if></span>
                                        <span class="mx-2">•</span>
                                        <span><#if product.category??>${product.category.name}<#else>Категория не указана</#if></span>
                                    </div>

                                    <h3 class="font-medium text-gray-800 mb-2 h-12 overflow-hidden">
                                        <a href="/product/${product.id}" class="hover:text-blue-600">${product.name}</a>
                                    </h3>

                                    <div class="mb-3 h-12 overflow-hidden">
                                        <p class="text-sm text-gray-600">${product.shortDescription!''}</p>
                                    </div>

                                    <div class="flex items-center justify-between">
                                        <div>
                                            <#if isAuthenticated>
                                                <#if product.salePrice?? && product.salePrice lt product.regularPrice>
                                                    <span class="text-red-600 font-bold">${product.salePrice} ₸</span>
                                                    <span class="text-gray-500 text-sm line-through ml-1">${product.regularPrice} ₸</span>
                                                <#elseif isCosmetologist && product.cosmetologistPrice??>
                                                    <span class="text-blue-600 font-bold">${product.cosmetologistPrice} ₸</span>
                                                    <span class="text-gray-500 text-sm line-through ml-1">${product.regularPrice} ₸</span>
                                                <#else>
                                                    <span class="text-blue-600 font-bold">${product.regularPrice} ₸</span>
                                                </#if>
                                            <#else>
                                                <a href="/auth/login" class="text-sm text-blue-600 hover:underline">Войдите, чтобы увидеть цены</a>
                                            </#if>
                                        </div>

                                        <#if isAuthenticated && product.stockQuantity gt 0>
                                            <button class="add-to-cart-btn bg-blue-600 text-white py-1 px-3 rounded-md hover:bg-blue-700 focus:outline-none text-sm" data-product-id="${product.id}">
                                                <i class="fas fa-shopping-cart mr-1"></i> В корзину
                                            </button>
                                        <#elseif isAuthenticated>
                                            <button class="bg-gray-300 text-gray-600 py-1 px-3 rounded-md cursor-not-allowed text-sm" disabled>
                                                Нет в наличии
                                            </button>
                                        </#if>
                                    </div>
                                </div>
                            </div>
                        </#list>
                    </#if>
                </div>

                <!-- Pagination -->
                <#if products.totalPages gt 1>
                    <div class="mt-8 flex justify-center">
                        <nav class="inline-flex rounded-md shadow">
                            <#if products.number gt 0>
                                <a href="?page=${products.number - 1}<#if search??>&search=${search}</#if><#if categoryId??>&categoryId=${categoryId}</#if><#if brandId??>&brandId=${brandId}</#if><#if professional??>&professional=${professional?string('true', 'false')}</#if><#if minPrice??>&minPrice=${minPrice}</#if><#if maxPrice??>&maxPrice=${maxPrice}</#if>" 
                                   class="relative inline-flex items-center px-2 py-2 rounded-l-md border border-gray-300 bg-white text-sm font-medium text-gray-500 hover:bg-gray-50">
                                    <i class="fas fa-chevron-left"></i>
                                </a>
                            <#else>
                                <span class="relative inline-flex items-center px-2 py-2 rounded-l-md border border-gray-300 bg-gray-100 text-sm font-medium text-gray-400 cursor-not-allowed">
                                    <i class="fas fa-chevron-left"></i>
                                </span>
                            </#if>

                            <#list 0..products.totalPages-1 as i>
                                <#if i == products.number>
                                    <span class="relative inline-flex items-center px-4 py-2 border border-gray-300 bg-blue-50 text-sm font-medium text-blue-600">
                                        ${i + 1}
                                    </span>
                                <#elseif i < 5 || i == products.totalPages - 1 || (i >= products.number - 1 && i <= products.number + 1)>
                                    <a href="?page=${i}<#if search??>&search=${search}</#if><#if categoryId??>&categoryId=${categoryId}</#if><#if brandId??>&brandId=${brandId}</#if><#if professional??>&professional=${professional?string('true', 'false')}</#if><#if minPrice??>&minPrice=${minPrice}</#if><#if maxPrice??>&maxPrice=${maxPrice}</#if>" 
                                       class="relative inline-flex items-center px-4 py-2 border border-gray-300 bg-white text-sm font-medium text-gray-700 hover:bg-gray-50">
                                        ${i + 1}
                                    </a>
                                <#elseif i == 5 || i == products.number - 2>
                                    <span class="relative inline-flex items-center px-4 py-2 border border-gray-300 bg-white text-sm font-medium text-gray-700">
                                        ...
                                    </span>
                                </#if>
                            </#list>

                            <#if products.number lt products.totalPages - 1>
                                <a href="?page=${products.number + 1}<#if search??>&search=${search}</#if><#if categoryId??>&categoryId=${categoryId}</#if><#if brandId??>&brandId=${brandId}</#if><#if professional??>&professional=${professional?string('true', 'false')}</#if><#if minPrice??>&minPrice=${minPrice}</#if><#if maxPrice??>&maxPrice=${maxPrice}</#if>" 
                                   class="relative inline-flex items-center px-2 py-2 rounded-r-md border border-gray-300 bg-white text-sm font-medium text-gray-500 hover:bg-gray-50">
                                    <i class="fas fa-chevron-right"></i>
                                </a>
                            <#else>
                                <span class="relative inline-flex items-center px-2 py-2 rounded-r-md border border-gray-300 bg-gray-100 text-sm font-medium text-gray-400 cursor-not-allowed">
                                    <i class="fas fa-chevron-right"></i>
                                </span>
                            </#if>
                        </nav>
                    </div>
                </#if>
            </div>
        </div>
    </div>

    <!-- JavaScript for catalog page -->
    <script>
        document.addEventListener('DOMContentLoaded', function() {
            // View mode switching (grid/list)
            const viewModeBtns = document.querySelectorAll('.view-mode-btn');
            const productsContainer = document.getElementById('products-grid');

            viewModeBtns.forEach(btn => {
                btn.addEventListener('click', function() {
                    const mode = this.getAttribute('data-mode');

                    // Update buttons
                    viewModeBtns.forEach(b => {
                        b.classList.remove('bg-gray-200', 'text-gray-800');
                        b.classList.add('bg-gray-100', 'text-gray-600');
                    });
                    this.classList.remove('bg-gray-100', 'text-gray-600');
                    this.classList.add('bg-gray-200', 'text-gray-800');

                    // Update grid
                    if (mode === 'grid') {
                        productsContainer.classList.remove('grid-cols-1');
                        productsContainer.classList.add('grid-cols-1', 'sm:grid-cols-2', 'lg:grid-cols-3');
                    } else {
                        productsContainer.classList.remove('sm:grid-cols-2', 'lg:grid-cols-3');
                        productsContainer.classList.add('grid-cols-1');
                    }
                });
            });

            // Reset filters button
            const resetBtn = document.getElementById('reset-filters');
            resetBtn.addEventListener('click', function() {
                window.location.href = '/';
            });

            // Favorite button functionality
            const favoriteBtns = document.querySelectorAll('.favorite-btn');
            favoriteBtns.forEach(btn => {
                btn.addEventListener('click', function() {
                    const productId = this.getAttribute('data-product-id');
                    const icon = this.querySelector('i');

                    // Check if user is authenticated
                    const isAuthenticated = ${isAuthenticated?string('true', 'false')};
                    if (!isAuthenticated) {
                        window.location.href = '/auth/login';
                        return;
                    }

                    // Toggle favorite status
                    if (icon.classList.contains('far')) {
                        icon.classList.remove('far');
                        icon.classList.add('fas');
                        // Here you would make an API call to add to favorites
                        console.log('Added product ' + productId + ' to favorites');
                    } else {
                        icon.classList.remove('fas');
                        icon.classList.add('far');
                        // Here you would make an API call to remove from favorites
                        console.log('Removed product ' + productId + ' from favorites');
                    }
                });
            });

            // Add to cart functionality
            const addToCartBtns = document.querySelectorAll('.add-to-cart-btn');
            addToCartBtns.forEach(btn => {
                btn.addEventListener('click', function() {
                    const productId = this.getAttribute('data-product-id');

                    // Here you would make an API call to add to cart
                    console.log('Added product ' + productId + ' to cart');

                    // Show success message
                    const toastNotification = document.createElement('div');
                    toastNotification.className = 'fixed bottom-4 right-4 bg-green-500 text-white px-4 py-2 rounded-lg shadow-lg z-50';
                    toastNotification.innerHTML = '<i class="fas fa-check-circle mr-2"></i> Товар добавлен в корзину';
                    document.body.appendChild(toastNotification);

                    // Remove after 3 seconds
                    setTimeout(() => {
                        toastNotification.remove();
                    }, 3000);
                });
            });
        });
    </script>
</@layout>
