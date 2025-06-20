<#assign pageTitle = "Восстановление пароля - Arannati">
<#assign pageDescription = "Восстановите доступ к аккаунту">
<#assign additionalJS = ["/static/js/pages/auth.js"]>
<#include "../layouts/main.ftlh">

<@layout>
    <div class="min-h-screen flex items-center justify-center py-12 px-4 sm:px-6 lg:px-8">
        <div class="max-w-md w-full space-y-8">
            <!-- Header -->
            <div class="text-center">
                <img class="mx-auto h-12 w-auto" src="/static/images/logo.svg" alt="Arannati">
                <h2 class="mt-6 text-3xl font-extrabold text-gray-900">
                    Восстановление пароля
                </h2>
                <p class="mt-2 text-sm text-gray-600">
                    Введите ваш email для получения ссылки восстановления
                </p>
            </div>

            <!-- Form -->
            <form class="auth-form" method="POST" action="/auth/forgot-password" novalidate>
                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>

                <div class="space-y-6">
                    <!-- Email -->
                    <div class="form-group">
                        <label for="email" class="block text-sm font-medium text-gray-700">
                            Email адрес
                        </label>
                        <input
                                id="email"
                                name="email"
                                type="email"
                                required
                                class="form-control mt-1 appearance-none relative block w-full px-3 py-2 border border-gray-300 placeholder-gray-500 text-gray-900 rounded-lg focus:outline-none focus:ring-blue-500 focus:border-blue-500 sm:text-sm"
                                placeholder="Введите ваш email"
                                data-validate="required,email">
                        <div class="error-message text-red-600 text-sm mt-1 hidden"></div>
                    </div>

                    <!-- Submit Button -->
                    <div>
                        <button
                                type="submit"
                                class="auth-submit-btn group relative w-full flex justify-center py-2 px-4 border border-transparent text-sm font-medium rounded-lg text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 disabled:opacity-50 disabled:cursor-not-allowed transition-colors">
                            <span class="absolute left-0 inset-y-0 flex items-center pl-3">
                                <i class="fas fa-paper-plane text-blue-500 group-hover:text-blue-400"></i>
                            </span>
                            Отправить ссылку
                        </button>
                    </div>
                </div>
            </form>

            <!-- Back to Login -->
            <div class="text-center">
                <a href="/auth/login" class="text-sm text-gray-600 hover:text-blue-600">
                    <i class="fas fa-arrow-left mr-1"></i>
                    Вернуться к входу
                </a>
            </div>
        </div>
    </div>
</@layout>