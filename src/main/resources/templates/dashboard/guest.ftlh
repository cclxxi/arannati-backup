<#assign pageTitle = "Arannati - Гостевая панель">
<#assign pageDescription = "Гостевая панель Arannati - просмотр доступной информации">
<#include "../layouts/main.ftlh">

<@layout>
    <div class="container mx-auto px-4 py-8">
        <div class="bg-white shadow-md rounded-lg p-6 mb-6">
            <h1 class="text-2xl font-bold text-gray-800 mb-4">Добро пожаловать в Arannati</h1>
            
            <div class="bg-blue-50 border-l-4 border-blue-500 p-4 mb-6">
                <div class="flex">
                    <div class="flex-shrink-0">
                        <i class="fas fa-info-circle text-blue-500"></i>
                    </div>
                    <div class="ml-3">
                        <p class="text-sm text-blue-700">
                            Вы просматриваете сайт как гость. Для доступа к полной функциональности, пожалуйста, 
                            <a href="/auth/login" class="font-medium underline">войдите</a> или 
                            <a href="/auth/register" class="font-medium underline">зарегистрируйтесь</a>.
                        </p>
                    </div>
                </div>
            </div>
            
            <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
                <div class="border rounded-lg p-4">
                    <h2 class="text-xl font-semibold mb-3">Что доступно гостям:</h2>
                    <ul class="list-disc pl-5 space-y-2">
                        <li>Просмотр каталога продукции</li>
                        <li>Просмотр информации о компании</li>
                        <li>Просмотр новостей и акций</li>
                        <li>Просмотр контактной информации</li>
                    </ul>
                </div>
                
                <div class="border rounded-lg p-4">
                    <h2 class="text-xl font-semibold mb-3">Преимущества регистрации:</h2>
                    <ul class="list-disc pl-5 space-y-2">
                        <li>Доступ к ценам на продукцию</li>
                        <li>Возможность оформления заказов</li>
                        <li>Отслеживание статуса заказов</li>
                        <li>Сохранение избранных товаров</li>
                        <li>Персональные скидки и акции</li>
                    </ul>
                </div>
            </div>
        </div>
        
        <div class="bg-white shadow-md rounded-lg p-6">
            <h2 class="text-xl font-bold text-gray-800 mb-4">Популярные категории</h2>
            
            <div class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4">
                <div class="border rounded-lg p-4 text-center hover:bg-gray-50 transition-colors">
                    <div class="text-4xl text-blue-500 mb-2">
                        <i class="fas fa-flask"></i>
                    </div>
                    <h3 class="font-medium">Сыворотки</h3>
                </div>
                
                <div class="border rounded-lg p-4 text-center hover:bg-gray-50 transition-colors">
                    <div class="text-4xl text-blue-500 mb-2">
                        <i class="fas fa-sun"></i>
                    </div>
                    <h3 class="font-medium">Кремы</h3>
                </div>
                
                <div class="border rounded-lg p-4 text-center hover:bg-gray-50 transition-colors">
                    <div class="text-4xl text-blue-500 mb-2">
                        <i class="fas fa-tint"></i>
                    </div>
                    <h3 class="font-medium">Маски</h3>
                </div>
                
                <div class="border rounded-lg p-4 text-center hover:bg-gray-50 transition-colors">
                    <div class="text-4xl text-blue-500 mb-2">
                        <i class="fas fa-leaf"></i>
                    </div>
                    <h3 class="font-medium">БАДы</h3>
                </div>
            </div>
            
            <div class="mt-6 text-center">
                <a href="/catalog" class="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md shadow-sm text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500">
                    Перейти в каталог
                    <i class="fas fa-arrow-right ml-2"></i>
                </a>
            </div>
        </div>
    </div>
</@layout>