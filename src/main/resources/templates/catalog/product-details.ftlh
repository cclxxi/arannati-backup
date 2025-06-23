<#assign pageTitle = "${product.name} - Arannati">
<#assign pageDescription = "${product.shortDescription!'Подробная информация о продукте Arannati'}">
<#assign additionalJS = ["/static/js/pages/product-details.js"]>
<#include "../layouts/main.ftlh">

<@layout>
    <div class="container mx-auto px-4 py-8">
        <!-- Breadcrumbs -->
        <nav class="text-sm mb-6">
            <ol class="list-none p-0 inline-flex">
                <li class="flex items-center">
                    <a href="/" class="text-blue-600 hover:text-blue-800">Главная</a>
                    <svg class="fill-current w-3 h-3 mx-2" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 320 512"><path d="M285.476 272.971L91.132 467.314c-9.373 9.373-24.569 9.373-33.941 0l-22.667-22.667c-9.357-9.357-9.375-24.522-.04-33.901L188.505 256 34.484 101.255c-9.335-9.379-9.317-24.544.04-33.901l22.667-22.667c9.373-9.373 24.569-9.373 33.941 0L285.475 239.03c9.373 9.372 9.373 24.568.001 33.941z"/></svg>
                </li>
                <li class="flex items-center">
                    <a href="/?categoryId=${product.categoryId}" class="text-blue-600 hover:text-blue-800"><#if product.category??>${product.category.name}<#else>Категория не указана</#if></a>
                    <svg class="fill-current w-3 h-3 mx-2" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 320 512"><path d="M285.476 272.971L91.132 467.314c-9.373 9.373-24.569 9.373-33.941 0l-22.667-22.667c-9.357-9.357-9.375-24.522-.04-33.901L188.505 256 34.484 101.255c-9.335-9.379-9.317-24.544.04-33.901l22.667-22.667c9.373-9.373 24.569-9.373 33.941 0L285.475 239.03c9.373 9.372 9.373 24.568.001 33.941z"/></svg>
                </li>
                <li>
                    <span class="text-gray-500">${product.name}</span>
                </li>
            </ol>
        </nav>

        <!-- Product Details -->
        <div class="bg-white shadow-md rounded-lg overflow-hidden">
            <div class="p-6">
                <div class="flex flex-col lg:flex-row">
                    <!-- Product Images -->
                    <div class="lg:w-2/5 mb-6 lg:mb-0 lg:pr-8">
                        <div class="relative">
                            <!-- Main Image -->
                            <div class="main-image-container mb-4 rounded-lg overflow-hidden border border-gray-200">
                                <#if product.images?? && product.images?size gt 0>
                                    <img id="main-product-image" src="${product.images[0].imagePath}" alt="${product.images[0].altText!product.name}" class="w-full h-auto">
                                <#else>
                                    <div class="w-full h-64 bg-gray-200 flex items-center justify-center">
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
                            </div>

                            <!-- Thumbnail Images -->
                            <#if product.images?? && product.images?size gt 1>
                                <div class="thumbnail-container flex space-x-2 overflow-x-auto">
                                    <#list product.images as image>
                                        <div class="thumbnail-item cursor-pointer border border-gray-200 rounded-md overflow-hidden <#if image_index == 0>border-blue-500</#if>" data-image="${image.imagePath}" data-alt="${image.altText!product.name}">
                                            <img src="${image.imagePath}" alt="${image.altText!product.name}" class="w-16 h-16 object-cover">
                                        </div>
                                    </#list>
                                </div>
                            </#if>
                        </div>
                    </div>

                    <!-- Product Info -->
                    <div class="lg:w-3/5">
                        <div class="flex items-center text-sm text-gray-500 mb-2">
                            <span><#if product.brand??>${product.brand.name}<#else>Бренд не указан</#if></span>
                            <span class="mx-2">•</span>
                            <span><#if product.category??>${product.category.name}<#else>Категория не указана</#if></span>
                            <span class="mx-2">•</span>
                            <span>Артикул: ${product.sku}</span>
                        </div>

                        <h1 class="text-3xl font-bold text-gray-800 mb-4">${product.name}</h1>

                        <div class="mb-6">
                            <p class="text-gray-600">${product.shortDescription!''}</p>
                        </div>

                        <!-- Price -->
                        <div class="mb-6">
                            <#if isAuthenticated>
                                <#if product.salePrice?? && product.salePrice lt product.regularPrice>
                                    <div class="flex items-center">
                                        <span class="text-2xl font-bold text-red-600">${product.salePrice} ₸</span>
                                        <span class="text-gray-500 text-lg line-through ml-2">${product.regularPrice} ₸</span>
                                        <span class="ml-2 bg-red-100 text-red-800 text-xs font-semibold px-2 py-1 rounded">
                                            -${((1 - product.salePrice/product.regularPrice) * 100)?int}%
                                        </span>
                                    </div>
                                <#elseif isCosmetologist && product.cosmetologistPrice??>
                                    <div class="flex items-center">
                                        <span class="text-2xl font-bold text-blue-600">${product.cosmetologistPrice} ₸</span>
                                        <span class="text-gray-500 text-lg line-through ml-2">${product.regularPrice} ₸</span>
                                        <span class="ml-2 bg-blue-100 text-blue-800 text-xs font-semibold px-2 py-1 rounded">
                                            Цена для косметологов
                                        </span>
                                    </div>
                                <#else>
                                    <div>
                                        <span class="text-2xl font-bold text-blue-600">${product.regularPrice} ₸</span>
                                    </div>
                                </#if>
                            <#else>
                                <div>
                                    <a href="/auth/login" class="text-blue-600 hover:underline">Войдите, чтобы увидеть цены</a>
                                </div>
                            </#if>
                        </div>

                        <!-- Stock Status -->
                        <div class="mb-6">
                            <#if product.stockQuantity gt 10>
                                <div class="flex items-center text-green-600">
                                    <i class="fas fa-check-circle mr-2"></i>
                                    <span>В наличии</span>
                                </div>
                            <#elseif product.stockQuantity gt 0>
                                <div class="flex items-center text-yellow-600">
                                    <i class="fas fa-exclamation-circle mr-2"></i>
                                    <span>Осталось мало (${product.stockQuantity} шт.)</span>
                                </div>
                            <#else>
                                <div class="flex items-center text-red-600">
                                    <i class="fas fa-times-circle mr-2"></i>
                                    <span>Нет в наличии</span>
                                </div>
                            </#if>
                        </div>

                        <!-- Add to Cart -->
                        <div class="mb-6">
                            <#if isAuthenticated && product.stockQuantity gt 0>
                                <div class="flex items-center">
                                    <div class="quantity-selector flex items-center border border-gray-300 rounded-md mr-4">
                                        <button type="button" class="quantity-btn minus px-3 py-2 text-gray-600 hover:bg-gray-100 focus:outline-none">
                                            <i class="fas fa-minus"></i>
                                        </button>
                                        <input type="number" id="quantity" name="quantity" value="1" min="1" max="${product.stockQuantity}" class="w-12 text-center border-0 focus:outline-none">
                                        <button type="button" class="quantity-btn plus px-3 py-2 text-gray-600 hover:bg-gray-100 focus:outline-none">
                                            <i class="fas fa-plus"></i>
                                        </button>
                                    </div>

                                    <button id="add-to-cart" class="flex-1 bg-blue-600 text-white py-2 px-4 rounded-md hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2" data-product-id="${product.id}">
                                        <i class="fas fa-shopping-cart mr-2"></i> Добавить в корзину
                                    </button>
                                </div>
                            <#elseif isAuthenticated>
                                <button class="w-full bg-gray-300 text-gray-600 py-2 px-4 rounded-md cursor-not-allowed" disabled>
                                    Нет в наличии
                                </button>
                            <#else>
                                <a href="/auth/login" class="block w-full text-center bg-blue-600 text-white py-2 px-4 rounded-md hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2">
                                    Войти для покупки
                                </a>
                            </#if>
                        </div>

                        <!-- Favorite and Share -->
                        <div class="flex space-x-4 mb-6">
                            <button id="add-to-favorites" class="flex items-center text-gray-600 hover:text-red-500 focus:outline-none" data-product-id="${product.id}">
                                <i class="far fa-heart mr-2"></i> В избранное
                            </button>

                            <button id="share-product" class="flex items-center text-gray-600 hover:text-blue-500 focus:outline-none">
                                <i class="fas fa-share-alt mr-2"></i> Поделиться
                            </button>
                        </div>

                        <!-- Key Features -->
                        <div class="border-t border-gray-200 pt-4">
                            <h3 class="text-lg font-semibold text-gray-800 mb-3">Ключевые особенности</h3>
                            <ul class="space-y-2">
                                <#if product.professional>
                                    <li class="flex items-start">
                                        <i class="fas fa-check-circle text-green-500 mt-1 mr-2"></i>
                                        <span>Профессиональное средство</span>
                                    </li>
                                </#if>
                                <li class="flex items-start">
                                    <i class="fas fa-check-circle text-green-500 mt-1 mr-2"></i>
                                    <span>Натуральные ингредиенты</span>
                                </li>
                                <li class="flex items-start">
                                    <i class="fas fa-check-circle text-green-500 mt-1 mr-2"></i>
                                    <span>Без парабенов и сульфатов</span>
                                </li>
                                <li class="flex items-start">
                                    <i class="fas fa-check-circle text-green-500 mt-1 mr-2"></i>
                                    <span>Не тестируется на животных</span>
                                </li>
                            </ul>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Product Tabs -->
            <div class="border-t border-gray-200">
                <div class="px-6">
                    <div class="flex overflow-x-auto">
                        <button class="product-tab active whitespace-nowrap px-4 py-3 border-b-2 border-blue-500 text-blue-600 font-medium" data-tab="description">
                            Описание
                        </button>
                        <button class="product-tab whitespace-nowrap px-4 py-3 border-b-2 border-transparent text-gray-500 hover:text-gray-700 font-medium" data-tab="ingredients">
                            Состав
                        </button>
                        <button class="product-tab whitespace-nowrap px-4 py-3 border-b-2 border-transparent text-gray-500 hover:text-gray-700 font-medium" data-tab="usage">
                            Применение
                        </button>
                        <button class="product-tab whitespace-nowrap px-4 py-3 border-b-2 border-transparent text-gray-500 hover:text-gray-700 font-medium" data-tab="specifications">
                            Характеристики
                        </button>
                    </div>
                </div>

                <div class="p-6">
                    <!-- Description Tab -->
                    <div id="description-tab" class="product-tab-content">
                        <div class="prose max-w-none">
                            <p>${product.description!''}</p>
                        </div>
                    </div>

                    <!-- Ingredients Tab -->
                    <div id="ingredients-tab" class="product-tab-content hidden">
                        <div class="prose max-w-none">
                            <p>${product.ingredients!''}</p>
                        </div>
                    </div>

                    <!-- Usage Tab -->
                    <div id="usage-tab" class="product-tab-content hidden">
                        <div class="prose max-w-none">
                            <p>${product.usageInstructions!''}</p>
                        </div>
                    </div>

                    <!-- Specifications Tab -->
                    <div id="specifications-tab" class="product-tab-content hidden">
                        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                            <div class="border rounded-lg overflow-hidden">
                                <div class="bg-gray-50 px-4 py-2 font-medium">Основная информация</div>
                                <div class="p-4 space-y-2">
                                    <div class="flex justify-between">
                                        <span class="text-gray-600">Бренд:</span>
                                        <span><#if product.brand??>${product.brand.name}<#else>Бренд не указан</#if></span>
                                    </div>
                                    <div class="flex justify-between">
                                        <span class="text-gray-600">Категория:</span>
                                        <span><#if product.category??>${product.category.name}<#else>Категория не указана</#if></span>
                                    </div>
                                    <div class="flex justify-between">
                                        <span class="text-gray-600">Артикул:</span>
                                        <span>${product.sku}</span>
                                    </div>
                                    <div class="flex justify-between">
                                        <span class="text-gray-600">Профессиональное:</span>
                                        <span>${product.professional?string('Да', 'Нет')}</span>
                                    </div>
                                </div>
                            </div>

                            <div class="border rounded-lg overflow-hidden">
                                <div class="bg-gray-50 px-4 py-2 font-medium">Физические характеристики</div>
                                <div class="p-4 space-y-2">
                                    <#if product.weight??>
                                        <div class="flex justify-between">
                                            <span class="text-gray-600">Вес:</span>
                                            <span>${product.weight} г</span>
                                        </div>
                                    </#if>
                                    <#if product.dimensions??>
                                        <div class="flex justify-between">
                                            <span class="text-gray-600">Размеры:</span>
                                            <span>${product.dimensions}</span>
                                        </div>
                                    </#if>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Similar Products -->
        <#if similarProducts?? && similarProducts?size gt 0>
            <div class="mt-12">
                <h2 class="text-2xl font-bold text-gray-800 mb-6">Похожие товары</h2>

                <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
                    <#list similarProducts as similar>
                        <div class="product-card bg-white shadow-md rounded-lg overflow-hidden transition-transform hover:shadow-lg hover:-translate-y-1">
                            <div class="relative">
                                <#if similar.images?? && similar.images?size gt 0>
                                    <img src="${similar.images[0].imagePath}" alt="${similar.images[0].altText!similar.name}" class="w-full h-48 object-cover">
                                <#else>
                                    <div class="w-full h-48 bg-gray-200 flex items-center justify-center">
                                        <i class="fas fa-image text-gray-400 text-4xl"></i>
                                    </div>
                                </#if>

                                <#if similar.salePrice?? && similar.salePrice lt similar.regularPrice>
                                    <span class="absolute top-2 right-2 bg-red-500 text-white text-xs font-bold px-2 py-1 rounded">
                                        СКИДКА
                                    </span>
                                </#if>

                                <#if similar.professional>
                                    <span class="absolute top-2 left-2 bg-purple-500 text-white text-xs font-bold px-2 py-1 rounded">
                                        PROF
                                    </span>
                                </#if>

                                <button class="favorite-btn absolute bottom-2 right-2 bg-white rounded-full p-2 shadow hover:bg-gray-100 focus:outline-none" data-product-id="${similar.id}">
                                    <i class="far fa-heart text-red-500"></i>
                                </button>
                            </div>

                            <div class="p-4">
                                <h3 class="font-medium text-gray-800 mb-2 h-12 overflow-hidden">
                                    <a href="/product/${similar.id}" class="hover:text-blue-600">${similar.name}</a>
                                </h3>

                                <div class="flex items-center justify-between">
                                    <div>
                                        <#if isAuthenticated>
                                            <#if similar.salePrice?? && similar.salePrice lt similar.regularPrice>
                                                <span class="text-red-600 font-bold">${similar.salePrice} ₸</span>
                                                <span class="text-gray-500 text-sm line-through ml-1">${similar.regularPrice} ₸</span>
                                            <#elseif isCosmetologist && similar.cosmetologistPrice??>
                                                <span class="text-blue-600 font-bold">${similar.cosmetologistPrice} ₸</span>
                                                <span class="text-gray-500 text-sm line-through ml-1">${similar.regularPrice} ₸</span>
                                            <#else>
                                                <span class="text-blue-600 font-bold">${similar.regularPrice} ₸</span>
                                            </#if>
                                        <#else>
                                            <a href="/auth/login" class="text-sm text-blue-600 hover:underline">Войдите, чтобы увидеть цены</a>
                                        </#if>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </#list>
                </div>
            </div>
        </#if>
    </div>

    <!-- Share Modal -->
    <div id="share-modal" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 hidden">
        <div class="bg-white rounded-lg p-6 max-w-md w-full">
            <div class="flex justify-between items-center mb-4">
                <h3 class="text-lg font-semibold text-gray-800">Поделиться товаром</h3>
                <button id="close-share-modal" class="text-gray-500 hover:text-gray-700 focus:outline-none">
                    <i class="fas fa-times"></i>
                </button>
            </div>

            <div class="mb-4">
                <p class="text-gray-600 mb-2">Поделитесь ссылкой на товар:</p>
                <div class="flex">
                    <input type="text" id="share-url" value="${pageUrl!''}" class="flex-1 border border-gray-300 rounded-l-md px-3 py-2 focus:outline-none focus:ring-blue-500 focus:border-blue-500" readonly>
                    <button id="copy-url" class="bg-blue-600 text-white px-4 py-2 rounded-r-md hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2">
                        <i class="fas fa-copy"></i>
                    </button>
                </div>
            </div>

            <div>
                <p class="text-gray-600 mb-2">Или поделитесь в социальных сетях:</p>
                <div class="flex space-x-4">
                    <a href="#" class="share-link" data-platform="facebook">
                        <div class="w-10 h-10 rounded-full bg-blue-600 text-white flex items-center justify-center hover:bg-blue-700">
                            <i class="fab fa-facebook-f"></i>
                        </div>
                    </a>
                    <a href="#" class="share-link" data-platform="twitter">
                        <div class="w-10 h-10 rounded-full bg-blue-400 text-white flex items-center justify-center hover:bg-blue-500">
                            <i class="fab fa-twitter"></i>
                        </div>
                    </a>
                    <a href="#" class="share-link" data-platform="telegram">
                        <div class="w-10 h-10 rounded-full bg-blue-500 text-white flex items-center justify-center hover:bg-blue-600">
                            <i class="fab fa-telegram-plane"></i>
                        </div>
                    </a>
                    <a href="#" class="share-link" data-platform="whatsapp">
                        <div class="w-10 h-10 rounded-full bg-green-500 text-white flex items-center justify-center hover:bg-green-600">
                            <i class="fab fa-whatsapp"></i>
                        </div>
                    </a>
                </div>
            </div>
        </div>
    </div>

    <!-- JavaScript for product details page -->
    <script>
        document.addEventListener('DOMContentLoaded', function() {
            // Thumbnail image switching
            const thumbnails = document.querySelectorAll('.thumbnail-item');
            const mainImage = document.getElementById('main-product-image');

            thumbnails.forEach(thumbnail => {
                thumbnail.addEventListener('click', function() {
                    // Update main image
                    mainImage.src = this.getAttribute('data-image');
                    mainImage.alt = this.getAttribute('data-alt');

                    // Update active thumbnail
                    thumbnails.forEach(t => t.classList.remove('border-blue-500'));
                    this.classList.add('border-blue-500');
                });
            });

            // Quantity selector
            const quantityInput = document.getElementById('quantity');
            const minusBtn = document.querySelector('.quantity-btn.minus');
            const plusBtn = document.querySelector('.quantity-btn.plus');

            if (minusBtn && plusBtn && quantityInput) {
                minusBtn.addEventListener('click', function() {
                    const currentValue = parseInt(quantityInput.value);
                    if (currentValue > 1) {
                        quantityInput.value = currentValue - 1;
                    }
                });

                plusBtn.addEventListener('click', function() {
                    const currentValue = parseInt(quantityInput.value);
                    const maxValue = parseInt(quantityInput.getAttribute('max'));
                    if (currentValue < maxValue) {
                        quantityInput.value = currentValue + 1;
                    }
                });

                quantityInput.addEventListener('change', function() {
                    const currentValue = parseInt(this.value);
                    const maxValue = parseInt(this.getAttribute('max'));

                    if (isNaN(currentValue) || currentValue < 1) {
                        this.value = 1;
                    } else if (currentValue > maxValue) {
                        this.value = maxValue;
                    }
                });
            }

            // Product tabs
            const tabs = document.querySelectorAll('.product-tab');
            const tabContents = document.querySelectorAll('.product-tab-content');

            tabs.forEach(tab => {
                tab.addEventListener('click', function() {
                    const tabId = this.getAttribute('data-tab');

                    // Update active tab
                    tabs.forEach(t => {
                        t.classList.remove('active', 'border-blue-500', 'text-blue-600');
                        t.classList.add('border-transparent', 'text-gray-500');
                    });
                    this.classList.remove('border-transparent', 'text-gray-500');
                    this.classList.add('active', 'border-blue-500', 'text-blue-600');

                    // Show active tab content
                    tabContents.forEach(content => {
                        content.classList.add('hidden');
                    });
                    document.getElementById(tabId + '-tab').classList.remove('hidden');
                });
            });

            // Add to cart
            const addToCartBtn = document.getElementById('add-to-cart');
            if (addToCartBtn) {
                addToCartBtn.addEventListener('click', function() {
                    const productId = this.getAttribute('data-product-id');
                    const quantity = parseInt(quantityInput.value);

                    // Here you would make an API call to add to cart
                    console.log('Added product ' + productId + ' to cart with quantity ' + quantity);

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
            }

            // Add to favorites
            const addToFavoritesBtn = document.getElementById('add-to-favorites');
            if (addToFavoritesBtn) {
                addToFavoritesBtn.addEventListener('click', function() {
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
            }

            // Share modal
            const shareBtn = document.getElementById('share-product');
            const shareModal = document.getElementById('share-modal');
            const closeShareModalBtn = document.getElementById('close-share-modal');
            const copyUrlBtn = document.getElementById('copy-url');
            const shareLinks = document.querySelectorAll('.share-link');

            if (shareBtn && shareModal) {
                shareBtn.addEventListener('click', function() {
                    shareModal.classList.remove('hidden');
                });

                closeShareModalBtn.addEventListener('click', function() {
                    shareModal.classList.add('hidden');
                });

                // Close modal when clicking outside
                shareModal.addEventListener('click', function(e) {
                    if (e.target === shareModal) {
                        shareModal.classList.add('hidden');
                    }
                });

                // Copy URL to clipboard
                copyUrlBtn.addEventListener('click', function() {
                    const shareUrl = document.getElementById('share-url');
                    shareUrl.select();
                    document.execCommand('copy');

                    // Show success message
                    this.innerHTML = '<i class="fas fa-check"></i>';
                    setTimeout(() => {
                        this.innerHTML = '<i class="fas fa-copy"></i>';
                    }, 2000);
                });

                // Share links
                shareLinks.forEach(link => {
                    link.addEventListener('click', function(e) {
                        e.preventDefault();

                        const platform = this.getAttribute('data-platform');
                        const url = encodeURIComponent(window.location.href);
                        const title = encodeURIComponent(document.title);

                        let shareUrl;
                        switch (platform) {
                            case 'facebook':
                                shareUrl = `https://www.facebook.com/sharer/sharer.php?u=${url}`;
                                break;
                            case 'twitter':
                                shareUrl = `https://twitter.com/intent/tweet?url=${url}&text=${title}`;
                                break;
                            case 'telegram':
                                shareUrl = `https://t.me/share/url?url=${url}&text=${title}`;
                                break;
                            case 'whatsapp':
                                shareUrl = `https://api.whatsapp.com/send?text=${title} ${url}`;
                                break;
                        }

                        window.open(shareUrl, '_blank', 'width=600,height=400');
                    });
                });
            }

            // Favorite buttons in similar products
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
        });
    </script>
</@layout>
