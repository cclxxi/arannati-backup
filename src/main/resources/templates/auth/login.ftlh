<#assign pageTitle = "Вход в систему - Arannati">
<#assign pageDescription = "Войдите в личный кабинет Arannati">
<#assign additionalJS = ["/static/js/pages/auth.js"]>
<#include "../layouts/main.ftlh">

<@layout>
    <div class="min-h-screen flex items-center justify-center py-12 px-4 sm:px-6 lg:px-8">
        <div class="max-w-md w-full space-y-8">
            <!-- Header -->
            <div class="text-center">
                <img class="mx-auto h-12 w-auto" src="/static/images/logo.svg" alt="Arannati">
                <h2 class="mt-6 text-3xl font-extrabold text-gray-900">
                    Вход в систему
                </h2>
                <p class="mt-2 text-sm text-gray-600">
                    Или
                    <a href="/auth/register" class="font-medium text-blue-600 hover:text-blue-500">
                        зарегистрируйтесь
                    </a>
                </p>
            </div>

            <!-- Login Form -->
            <form class="auth-form" method="POST" action="/auth/login" novalidate>
                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>

                <div class="space-y-6">
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
                                    autocomplete="email"
                                    required
                                    class="form-control appearance-none relative block w-full px-3 py-2 border border-gray-300 placeholder-gray-500 text-gray-900 rounded-lg focus:outline-none focus:ring-blue-500 focus:border-blue-500 focus:z-10 sm:text-sm"
                                    placeholder="Введите ваш email"
                                    value="${(loginRequest.email)!''}"
                                    data-validate="required,email">
                            <div class="error-message text-red-600 text-sm mt-1 hidden"></div>
                        </div>
                    </div>

                    <!-- Password -->
                    <div class="form-group">
                        <label for="password" class="block text-sm font-medium text-gray-700">
                            Пароль
                        </label>
                        <div class="mt-1 relative">
                            <input
                                    id="password"
                                    name="password"
                                    type="password"
                                    autocomplete="current-password"
                                    required
                                    class="form-control appearance-none relative block w-full px-3 py-2 border border-gray-300 placeholder-gray-500 text-gray-900 rounded-lg focus:outline-none focus:ring-blue-500 focus:border-blue-500 focus:z-10 sm:text-sm"
                                    placeholder="Введите ваш пароль"
                                    data-validate="required,minlength:6">
                            <button type="button" class="password-toggle absolute inset-y-0 right-0 pr-3 flex items-center">
                                <i class="fas fa-eye text-gray-400 hover:text-gray-600"></i>
                            </button>
                            <div class="error-message text-red-600 text-sm mt-1 hidden"></div>
                        </div>
                    </div>

                    <!-- Remember Me & Forgot Password -->
                    <div class="flex items-center justify-between">
                        <div class="flex items-center">
                            <input
                                    id="remember-me"
                                    name="remember-me"
                                    type="checkbox"
                                    class="h-4 w-4 text-blue-600 focus:ring-blue-500 border-gray-300 rounded">
                            <label for="remember-me" class="ml-2 block text-sm text-gray-900">
                                Запомнить меня
                            </label>
                        </div>

                        <div class="text-sm">
                            <a href="/auth/forgot-password" class="font-medium text-blue-600 hover:text-blue-500">
                                Забыли пароль?
                            </a>
                        </div>
                    </div>

                    <!-- Submit Button -->
                    <div>
                        <button
                                type="submit"
                                class="auth-submit-btn group relative w-full flex justify-center py-2 px-4 border border-transparent text-sm font-medium rounded-lg text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 disabled:opacity-50 disabled:cursor-not-allowed transition-colors">
                            <span class="absolute left-0 inset-y-0 flex items-center pl-3">
                                <i class="fas fa-sign-in-alt text-blue-500 group-hover:text-blue-400"></i>
                            </span>
                            Войти
                        </button>
                    </div>
                </div>
            </form>

            <!-- Registration Links -->
            <div class="text-center">
                <div class="relative">
                    <div class="absolute inset-0 flex items-center">
                        <div class="w-full border-t border-gray-300"></div>
                    </div>
                    <div class="relative flex justify-center text-sm">
                        <span class="px-2 bg-gray-50 text-gray-500">Нет аккаунта?</span>
                    </div>
                </div>

                <div class="mt-6 grid grid-cols-2 gap-3">
                    <a href="/auth/register"
                       class="w-full inline-flex justify-center py-2 px-4 border border-gray-300 rounded-lg shadow-sm bg-white text-sm font-medium text-gray-500 hover:bg-gray-50 transition-colors">
                        Регистрация
                    </a>

                    <a href="/auth/register/cosmetologist"
                       class="w-full inline-flex justify-center py-2 px-4 border border-blue-300 rounded-lg shadow-sm bg-blue-50 text-sm font-medium text-blue-600 hover:bg-blue-100 transition-colors">
                        Для косметологов
                    </a>
                </div>
            </div>
        </div>
    </div>
</@layout>