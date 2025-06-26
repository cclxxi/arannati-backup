<#assign pageTitle = "Arannati - Редактирование продукта">
<#assign pageDescription = "Редактирование продукта в административной панели Arannati">
<#include "../layouts/main.ftlh">

<@layout>
    <div class="container mx-auto px-4 py-8">
        <!-- Page Header -->
        <div class="bg-white shadow-md rounded-lg p-6 mb-6">
            <div class="flex items-center justify-between mb-4">
                <h1 class="text-2xl font-bold text-gray-800">
                    <#if product.id??>
                        Редактирование продукта
                    <#else>
                        Добавление нового продукта
                    </#if>
                </h1>
                <a href="/admin/products" class="px-4 py-2 bg-gray-200 text-gray-700 rounded hover:bg-gray-300">
                    <i class="fas fa-arrow-left mr-2"></i> Назад к продуктам
                </a>
            </div>

            <div class="bg-blue-50 border-l-4 border-blue-500 p-4">
                <div class="flex">
                    <div class="flex-shrink-0">
                        <i class="fas fa-info-circle text-blue-500"></i>
                    </div>
                    <div class="ml-3">
                        <p class="text-sm text-blue-700">
                            <#if product.id??>
                                Здесь вы можете изменить информацию о продукте.
                            <#else>
                                Здесь вы можете добавить новый продукт в каталог.
                            </#if>
                        </p>
                    </div>
                </div>
            </div>
        </div>

        <!-- Product Form -->
        <div class="bg-white shadow-md rounded-lg p-6">
            <form id="product-form" method="post" action="<#if product.id??>/admin/products/update/${product.id}<#else>/admin/products/add</#if>" enctype="multipart/form-data">
                <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
                    <!-- Left Column -->
                    <div>
                        <div class="mb-4">
                            <label for="name" class="block text-sm font-medium text-gray-700 mb-1">Название продукта*</label>
                            <input type="text" id="name" name="name" value="${(product.name)!''}" class="w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500" required>
                        </div>

                        <div class="mb-4">
                            <label for="category" class="block text-sm font-medium text-gray-700 mb-1">Категория*</label>
                            <select id="category" name="category" class="w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500" required>
                                <option value="">Выберите категорию</option>
                                <#list categories as category>
                                    <option value="${category}" <#if (product.category)?? && product.category == category>selected</#if>>${category}</option>
                                </#list>
                            </select>
                        </div>

                        <div class="mb-4">
                            <label for="price" class="block text-sm font-medium text-gray-700 mb-1">Цена (₸)*</label>
                            <input type="number" id="price" name="price" value="${(product.price)!''}" min="0" step="0.01" class="w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500" required>
                        </div>

                        <div class="mb-4">
                            <label for="stock" class="block text-sm font-medium text-gray-700 mb-1">Количество на складе*</label>
                            <input type="number" id="stock" name="stock" value="${(product.stock)!''}" min="0" class="w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500" required>
                        </div>
                    </div>

                    <!-- Right Column -->
                    <div>
                        <div class="mb-4">
                            <label for="description" class="block text-sm font-medium text-gray-700 mb-1">Описание*</label>
                            <textarea id="description" name="description" rows="5" class="w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500" required>${(product.description)!''}</textarea>
                        </div>

                        <div class="mb-4">
                            <label for="image" class="block text-sm font-medium text-gray-700 mb-1">Изображение</label>
                            <input type="file" id="image" name="image" accept="image/*" class="w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500">
                            <#if (product.imageUrl)?? && product.imageUrl != "">
                                <div class="mt-2 flex items-center">
                                    <img src="${product.imageUrl}" alt="${(product.name)!'Продукт'}" class="h-20 w-20 object-cover rounded">
                                    <span class="ml-2 text-sm text-gray-500">Текущее изображение</span>
                                </div>
                            </#if>
                        </div>

                        <div class="mb-4">
                            <div class="flex items-center">
                                <input type="checkbox" id="active" name="active" <#if (product.active)!false>checked</#if> class="h-4 w-4 text-blue-600 focus:ring-blue-500 border-gray-300 rounded">
                                <label for="active" class="ml-2 block text-sm text-gray-700">Активный (отображается в каталоге)</label>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="mt-6 flex justify-end">
                    <a href="/admin/products" class="bg-white py-2 px-4 border border-gray-300 rounded-md shadow-sm text-sm font-medium text-gray-700 hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 mr-3">
                        Отмена
                    </a>
                    <button type="submit" class="inline-flex justify-center py-2 px-4 border border-transparent shadow-sm text-sm font-medium rounded-md text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500">
                        <#if product.id??>
                            Сохранить изменения
                        <#else>
                            Добавить продукт
                        </#if>
                    </button>
                </div>
            </form>
        </div>
    </div>

    <script>
        document.addEventListener('DOMContentLoaded', function() {
            const productForm = document.getElementById('product-form');

            productForm.addEventListener('submit', function(e) {
                // Basic validation
                const name = document.getElementById('name').value.trim();
                const category = document.getElementById('category').value;
                const price = document.getElementById('price').value;
                const stock = document.getElementById('stock').value;
                const description = document.getElementById('description').value.trim();

                if (!name || !category || !price || !stock || !description) {
                    e.preventDefault();
                    alert('Пожалуйста, заполните все обязательные поля');
                    return;
                }

                if (parseFloat(price) < 0) {
                    e.preventDefault();
                    alert('Цена не может быть отрицательной');
                    return;
                }

                if (parseInt(stock) < 0) {
                    e.preventDefault();
                    alert('Количество на складе не может быть отрицательным');
                    return;
                }
            });

            // Preview image before upload
            const imageInput = document.getElementById('image');

            imageInput.addEventListener('change', function() {
                const file = this.files[0];
                if (file) {
                    const reader = new FileReader();

                    reader.onload = function(e) {
                        // Check if there's already a preview
                        let previewContainer = imageInput.nextElementSibling;

                        if (!previewContainer || !previewContainer.classList.contains('mt-2')) {
                            // Create new preview
                            previewContainer = document.createElement('div');
                            previewContainer.className = 'mt-2 flex items-center';
                            previewContainer.innerHTML = 
                                '<img src="' + e.target.result + '" alt="Preview" class="h-20 w-20 object-cover rounded">' +
                                '<span class="ml-2 text-sm text-gray-500">Новое изображение</span>';
                            imageInput.parentNode.appendChild(previewContainer);
                        } else {
                            // Update existing preview
                            const img = previewContainer.querySelector('img');
                            img.src = e.target.result;
                            img.alt = 'Preview';

                            const span = previewContainer.querySelector('span');
                            span.textContent = 'Новое изображение';
                        }
                    };

                    reader.readAsDataURL(file);
                }
            });
        });
    </script>
</@layout>
