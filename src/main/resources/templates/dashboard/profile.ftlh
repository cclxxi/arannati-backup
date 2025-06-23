<#assign pageTitle = "Arannati - Профиль пользователя">
<#assign pageDescription = "Редактирование профиля пользователя Arannati">
<#include "../layouts/main.ftlh">

<@layout>
    <div class="container mx-auto px-4 py-8">
        <div class="max-w-3xl mx-auto">
            <!-- Header -->
            <div class="bg-white shadow-md rounded-lg p-6 mb-6">
                <div class="flex items-center justify-between mb-4">
                    <h1 class="text-2xl font-bold text-gray-800">Профиль пользователя</h1>
                    <a href="/dashboard" class="text-blue-600 hover:text-blue-800">
                        <i class="fas fa-arrow-left mr-1"></i> Назад к панели
                    </a>
                </div>
                
                <div class="bg-blue-50 border-l-4 border-blue-500 p-4">
                    <div class="flex">
                        <div class="flex-shrink-0">
                            <i class="fas fa-info-circle text-blue-500"></i>
                        </div>
                        <div class="ml-3">
                            <p class="text-sm text-blue-700">
                                Здесь вы можете обновить свои личные данные и изменить пароль.
                            </p>
                        </div>
                    </div>
                </div>
            </div>
            
            <!-- Personal Information Form -->
            <div class="bg-white shadow-md rounded-lg p-6 mb-6">
                <h2 class="text-xl font-semibold text-gray-800 mb-4">Личные данные</h2>
                
                <form id="profile-form" class="space-y-6">
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                    
                    <!-- Email -->
                    <div class="form-group">
                        <label for="email" class="block text-sm font-medium text-gray-700">
                            Email адрес
                        </label>
                        <div class="mt-1 relative">
                            <input
                                id="email"
                                name="email"
                                type="email"
                                readonly
                                class="form-control appearance-none relative block w-full px-3 py-2 border border-gray-300 bg-gray-100 placeholder-gray-500 text-gray-900 rounded-lg focus:outline-none focus:ring-blue-500 focus:border-blue-500 focus:z-10 sm:text-sm"
                                value="${user.email}">
                            <div class="error-message text-red-600 text-sm mt-1 hidden"></div>
                        </div>
                        <p class="text-xs text-gray-500 mt-1">Email нельзя изменить</p>
                    </div>
                    
                    <!-- First Name -->
                    <div class="form-group">
                        <label for="firstName" class="block text-sm font-medium text-gray-700">
                            Имя
                        </label>
                        <div class="mt-1 relative">
                            <input
                                id="firstName"
                                name="firstName"
                                type="text"
                                required
                                class="form-control appearance-none relative block w-full px-3 py-2 border border-gray-300 placeholder-gray-500 text-gray-900 rounded-lg focus:outline-none focus:ring-blue-500 focus:border-blue-500 focus:z-10 sm:text-sm"
                                placeholder="Введите ваше имя"
                                value="${user.firstName}"
                                data-validate="required,minlength:2">
                            <div class="error-message text-red-600 text-sm mt-1 hidden"></div>
                        </div>
                    </div>
                    
                    <!-- Last Name -->
                    <div class="form-group">
                        <label for="lastName" class="block text-sm font-medium text-gray-700">
                            Фамилия
                        </label>
                        <div class="mt-1 relative">
                            <input
                                id="lastName"
                                name="lastName"
                                type="text"
                                required
                                class="form-control appearance-none relative block w-full px-3 py-2 border border-gray-300 placeholder-gray-500 text-gray-900 rounded-lg focus:outline-none focus:ring-blue-500 focus:border-blue-500 focus:z-10 sm:text-sm"
                                placeholder="Введите вашу фамилию"
                                value="${user.lastName}"
                                data-validate="required,minlength:2">
                            <div class="error-message text-red-600 text-sm mt-1 hidden"></div>
                        </div>
                    </div>
                    
                    <!-- Phone -->
                    <div class="form-group">
                        <label for="phone" class="block text-sm font-medium text-gray-700">
                            Телефон
                        </label>
                        <div class="mt-1 relative">
                            <input
                                id="phone"
                                name="phone"
                                type="tel"
                                class="form-control appearance-none relative block w-full px-3 py-2 border border-gray-300 placeholder-gray-500 text-gray-900 rounded-lg focus:outline-none focus:ring-blue-500 focus:border-blue-500 focus:z-10 sm:text-sm"
                                placeholder="Введите ваш телефон"
                                value="${user.phone!''}"
                                data-validate="phone">
                            <div class="error-message text-red-600 text-sm mt-1 hidden"></div>
                        </div>
                    </div>
                    
                    <!-- Submit Button -->
                    <div>
                        <button
                            type="submit"
                            class="profile-submit-btn group relative w-full flex justify-center py-2 px-4 border border-transparent text-sm font-medium rounded-lg text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 disabled:opacity-50 disabled:cursor-not-allowed transition-colors">
                            <span class="absolute left-0 inset-y-0 flex items-center pl-3">
                                <i class="fas fa-save text-blue-500 group-hover:text-blue-400"></i>
                            </span>
                            Сохранить изменения
                        </button>
                    </div>
                </form>
            </div>
            
            <!-- Change Password Form -->
            <div class="bg-white shadow-md rounded-lg p-6">
                <h2 class="text-xl font-semibold text-gray-800 mb-4">Изменение пароля</h2>
                
                <form id="password-form" class="space-y-6">
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                    
                    <!-- Current Password -->
                    <div class="form-group">
                        <label for="currentPassword" class="block text-sm font-medium text-gray-700">
                            Текущий пароль
                        </label>
                        <div class="mt-1 relative">
                            <input
                                id="currentPassword"
                                name="currentPassword"
                                type="password"
                                required
                                class="form-control appearance-none relative block w-full px-3 py-2 border border-gray-300 placeholder-gray-500 text-gray-900 rounded-lg focus:outline-none focus:ring-blue-500 focus:border-blue-500 focus:z-10 sm:text-sm"
                                placeholder="Введите текущий пароль"
                                data-validate="required">
                            <button type="button" class="password-toggle absolute inset-y-0 right-0 pr-3 flex items-center">
                                <i class="fas fa-eye text-gray-400 hover:text-gray-600"></i>
                            </button>
                            <div class="error-message text-red-600 text-sm mt-1 hidden"></div>
                        </div>
                    </div>
                    
                    <!-- New Password -->
                    <div class="form-group">
                        <label for="newPassword" class="block text-sm font-medium text-gray-700">
                            Новый пароль
                        </label>
                        <div class="mt-1 relative">
                            <input
                                id="newPassword"
                                name="newPassword"
                                type="password"
                                required
                                class="form-control appearance-none relative block w-full px-3 py-2 border border-gray-300 placeholder-gray-500 text-gray-900 rounded-lg focus:outline-none focus:ring-blue-500 focus:border-blue-500 focus:z-10 sm:text-sm"
                                placeholder="Введите новый пароль"
                                data-validate="required,minlength:6">
                            <button type="button" class="password-toggle absolute inset-y-0 right-0 pr-3 flex items-center">
                                <i class="fas fa-eye text-gray-400 hover:text-gray-600"></i>
                            </button>
                            <div class="error-message text-red-600 text-sm mt-1 hidden"></div>
                        </div>
                    </div>
                    
                    <!-- Confirm New Password -->
                    <div class="form-group">
                        <label for="confirmPassword" class="block text-sm font-medium text-gray-700">
                            Подтверждение нового пароля
                        </label>
                        <div class="mt-1 relative">
                            <input
                                id="confirmPassword"
                                name="confirmPassword"
                                type="password"
                                required
                                class="form-control appearance-none relative block w-full px-3 py-2 border border-gray-300 placeholder-gray-500 text-gray-900 rounded-lg focus:outline-none focus:ring-blue-500 focus:border-blue-500 focus:z-10 sm:text-sm"
                                placeholder="Подтвердите новый пароль"
                                data-validate="required,match:newPassword">
                            <button type="button" class="password-toggle absolute inset-y-0 right-0 pr-3 flex items-center">
                                <i class="fas fa-eye text-gray-400 hover:text-gray-600"></i>
                            </button>
                            <div class="error-message text-red-600 text-sm mt-1 hidden"></div>
                        </div>
                    </div>
                    
                    <!-- Submit Button -->
                    <div>
                        <button
                            type="submit"
                            class="password-submit-btn group relative w-full flex justify-center py-2 px-4 border border-transparent text-sm font-medium rounded-lg text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 disabled:opacity-50 disabled:cursor-not-allowed transition-colors">
                            <span class="absolute left-0 inset-y-0 flex items-center pl-3">
                                <i class="fas fa-key text-blue-500 group-hover:text-blue-400"></i>
                            </span>
                            Изменить пароль
                        </button>
                    </div>
                </form>
            </div>
        </div>
    </div>
</@layout>