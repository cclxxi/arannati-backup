<#assign pageTitle = "Ошибка ${errorCode!''}">
<#assign pageDescription = "Произошла ошибка">
<#include "../layouts/main.ftlh">

<@layout>
    <div class="container mx-auto px-4 py-16">
        <div class="max-w-lg mx-auto bg-white shadow-lg rounded-lg overflow-hidden">
            <div class="p-8">
                <div class="text-center">
                    <h1 class="text-4xl font-bold text-red-600 mb-4">
                        <#if errorCode??>
                            Ошибка ${errorCode}
                        <#else>
                            Произошла ошибка
                        </#if>
                    </h1>
                    
                    <div class="mb-6">
                        <p class="text-gray-700 text-lg mb-4">
                            <#if errorMessage??>
                                ${errorMessage}
                            <#else>
                                Произошла непредвиденная ошибка. Пожалуйста, попробуйте позже.
                            </#if>
                        </p>
                        
                        <#if errors?? && errors?size gt 0>
                            <div class="mt-4 p-4 bg-red-50 border border-red-200 rounded-md">
                                <h3 class="text-red-700 font-semibold mb-2">Детали ошибки:</h3>
                                <ul class="list-disc pl-5 text-red-600">
                                    <#list errors as field, message>
                                        <li>${field}: ${message}</li>
                                    </#list>
                                </ul>
                            </div>
                        </#if>
                        
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
                        <button onclick="window.history.back()" class="px-6 py-2 bg-gray-200 text-gray-800 rounded-md hover:bg-gray-300 transition-colors">
                            Назад
                        </button>
                    </div>
                </div>
            </div>
        </div>
    </div>
</@layout>