<#assign pageTitle = "Arannati - Загрузка материалов">
<#assign pageDescription = "Загрузка материалов в административной панели Arannati">
<#include "../layouts/main.ftlh">

<@layout>
    <div class="container mx-auto px-4 py-8">
        <!-- Page Header -->
        <div class="bg-white shadow-md rounded-lg p-6 mb-6">
            <div class="flex items-center justify-between mb-4">
                <h1 class="text-2xl font-bold text-gray-800">Загрузка материалов</h1>
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
                            Здесь вы можете загружать материалы для сайта, такие как документы, изображения и другие файлы.
                        </p>
                    </div>
                </div>
            </div>
        </div>

        <!-- Success/Error Messages -->
        <#if RequestParameters.success??>
            <div class="bg-green-100 border-l-4 border-green-500 text-green-700 p-4 mb-6" role="alert">
                <p class="font-bold">Успех!</p>
                <p>Материал успешно загружен.</p>
            </div>
        </#if>

        <#if RequestParameters.error??>
            <div class="bg-red-100 border-l-4 border-red-500 text-red-700 p-4 mb-6" role="alert">
                <p class="font-bold">Ошибка!</p>
                <p>Произошла ошибка при загрузке материала. Пожалуйста, попробуйте снова.</p>
            </div>
        </#if>

        <!-- Upload Form -->
        <div class="bg-white shadow-md rounded-lg p-6 mb-6">
            <h2 class="text-lg font-semibold text-gray-800 mb-4">Загрузить новый материал</h2>

            <form action="/admin/materials/upload" method="post" enctype="multipart/form-data">
                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                <div class="mb-4">
                    <label for="title" class="block text-sm font-medium text-gray-700 mb-1">Название*</label>
                    <input type="text" id="title" name="title" class="w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500" required>
                </div>

                <div class="mb-4">
                    <label for="description" class="block text-sm font-medium text-gray-700 mb-1">Описание</label>
                    <textarea id="description" name="description" rows="3" class="w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"></textarea>
                </div>

                <div class="mb-4">
                    <label for="file" class="block text-sm font-medium text-gray-700 mb-1">Файл*</label>
                    <input type="file" id="file" name="file" class="w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500" required>
                    <p class="mt-1 text-sm text-gray-500">Поддерживаемые форматы: PDF, DOC, DOCX, XLS, XLSX, JPG, PNG, GIF</p>
                </div>

                <div class="flex justify-end">
                    <button type="submit" class="inline-flex justify-center py-2 px-4 border border-transparent shadow-sm text-sm font-medium rounded-md text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500">
                        Загрузить материал
                    </button>
                </div>
            </form>
        </div>

        <!-- Recent Uploads -->
        <div class="bg-white shadow-md rounded-lg p-6">
            <h2 class="text-lg font-semibold text-gray-800 mb-4">Недавно загруженные материалы</h2>

            <div class="overflow-x-auto">
                <table class="min-w-full divide-y divide-gray-200">
                    <thead class="bg-gray-50">
                        <tr>
                            <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                Название
                            </th>
                            <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                Описание
                            </th>
                            <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                Тип
                            </th>
                            <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                Дата загрузки
                            </th>
                            <th scope="col" class="relative px-6 py-3">
                                <span class="sr-only">Действия</span>
                            </th>
                        </tr>
                    </thead>
                    <tbody class="bg-white divide-y divide-gray-200">
                        <#if materials?? && materials?size gt 0>
                            <#list materials as material>
                                <tr>
                                    <td class="px-6 py-4 whitespace-nowrap">
                                        <div class="text-sm font-medium text-gray-900">${material.title}</div>
                                    </td>
                                    <td class="px-6 py-4">
                                        <div class="text-sm text-gray-500">${material.description!''}</div>
                                    </td>
                                    <td class="px-6 py-4 whitespace-nowrap">
                                        <div class="text-sm text-gray-500">${material.fileType}</div>
                                    </td>
                                    <td class="px-6 py-4 whitespace-nowrap">
                                        <div class="text-sm text-gray-500">${material.uploadDate}</div>
                                    </td>
                                    <td class="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                                        <a href="/materials/download/${material.id}" class="text-blue-600 hover:text-blue-900 mr-3">
                                            <i class="fas fa-download"></i> Скачать
                                        </a>
                                        <a href="/admin/materials/${material.id}/delete" class="text-red-600 hover:text-red-900" onclick="return confirm('Вы уверены, что хотите удалить этот материал?')">
                                            <i class="fas fa-trash"></i> Удалить
                                        </a>
                                    </td>
                                </tr>
                            </#list>
                        <#else>
                            <tr>
                                <td colspan="5" class="px-6 py-4 text-center text-gray-500">
                                    Нет загруженных материалов
                                </td>
                            </tr>
                        </#if>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</@layout>
