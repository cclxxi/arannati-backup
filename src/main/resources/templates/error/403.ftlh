<#assign pageTitle = "Доступ запрещен">
<#assign pageDescription = "У вас нет доступа к запрошенной странице">
<#include "../layouts/main.ftlh">

<@layout>
    <div class="container mx-auto px-4 py-16">
        <div class="max-w-lg mx-auto bg-white shadow-lg rounded-lg overflow-hidden">
            <div class="p-8">
                <div class="text-center">
                    <h1 class="text-4xl font-bold text-red-600 mb-4">Ошибка 403</h1>
                    
                    <div class="mb-6">
                        <p class="text-gray-700 text-lg mb-4">
                            У вас нет доступа к запрошенной странице. Пожалуйста, убедитесь, что вы авторизованы и имеете необходимые права доступа.
                        </p>
                        
                        <#if requestUri??>
                            <p class="text-sm text-gray-500 mt-4">
                                Запрошенный URL: ${requestUri}
                            </p>
                        </#if>
                    </div>
                    
                    <div class="flex justify-center space-x-4">
                        <a href="/" class="px-6 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 transition-colors">
                            На главную
                        </a>
                        <a href="/auth/login" class="px-6 py-2 bg-green-600 text-white rounded-md hover:bg-green-700 transition-colors">
                            Войти
                        </a>
                        <button onclick="window.history.back()" class="px-6 py-2 bg-gray-200 text-gray-800 rounded-md hover:bg-gray-300 transition-colors">
                            Назад
                        </button>
                    </div>
                </div>
            </div>
        </div>
    </div>
</@layout>