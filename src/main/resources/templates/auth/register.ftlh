<#assign pageTitle = "Регистрация - Arannati">
<#assign pageDescription = "Создайте аккаунт в Arannati">
<#assign additionalJS = ["/static/js/pages/auth.js"]>
<#include "../layouts/main.ftlh">

<@layout>
    <div class="min-h-screen flex items-center justify-center py-12 px-4 sm:px-6 lg:px-8">
        <div class="max-w-md w-full space-y-8">
            <!-- Header -->
            <div class="text-center">
                <img class="mx-auto h-12 w-auto" src="/static/images/logo.svg" alt="Arannati">
                <h2 class="mt-6 text-3xl font-extrabold text-gray-900">
                    Регистрация
                </h2>
                <p class="mt-2 text-sm text-gray-600">
                    Уже есть аккаунт?
                    <a href="/auth/login" class="font-medium text-blue-600 hover:text-blue-500">
                        Войти
                    </a>
                </p>
            </div>

            <!-- Registration Form -->
            <form class="auth-form" method="POST" action="/auth/register" novalidate>
                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>

                <div class="space-y-6">
                    <!-- Name Fields -->
                    <div class="grid grid-cols-2 gap-4">
                        <div class="form-group">
                            <label for="firstName" class="block text-sm font-medium text-gray-700">
                                Имя
                            </label>
                            <input
                                    id="firstName"
                                    name="firstName"
                                    type="text"
                                    required
                                    class="form-control mt-1 appearance-none relative block w-full px-3 py-2 border border-gray-300 placeholder-gray-500 text-gray-900 rounded-lg focus:outline-none focus:ring-blue-500 focus:border-blue-500 sm:text-sm"
                                    placeholder="Имя"
                                    value="${(userRegistrationRequest.firstName)!''}"
                                    data-validate="required,minlength:2,maxlength:50">
                            <div class="error-message text-red-600 text-sm mt-1 hidden"></div>
                        </div>

                        <div class="form-group">
                            <label for="lastName" class="block text-sm font-medium text-gray-700">
                                Фамилия
                            </label>
                            <input
                                    id="lastName"
                                    name="lastName"
                                    type="text"
                                    required
                                    class="form-control mt-1 appearance-none relative block w-full px-3 py-2 border border-gray-300 placeholder-gray-500 text-gray-900 rounded-lg focus:outline-none focus:ring-blue-500 focus:border-blue-500 sm:text-sm"
                                    placeholder="Фамилия"
                                    value="${(userRegistrationRequest.lastName)!''}"
                                    data-validate="required,minlength:2,maxlength:50">
                            <div class="error-message text-red-600 text-sm mt-1 hidden"></div>
                        </div>
                    </div>

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
                                    class="form-control appearance-none relative block w-full px-3 py-2 border border-gray-300 placeholder-gray-500 text-gray-900 rounded-lg focus:outline-none focus:ring-blue-500 focus:border-blue-500 sm:text-sm"
                                    placeholder="Введите ваш email"
                                    value="${(userRegistrationRequest.email)!''}"
                                    data-validate="required,email,remote:/auth/check-email">
                            <div class="email-availability absolute right-3 top-2 hidden">
                                <i class="fas fa-check text-green-500"></i>
                            </div>
                            <div class="error-message text-red-600 text-sm mt-1 hidden"></div>
                        </div>
                    </div>

                    <!-- Phone -->
                    <div class="form-group">
                        <label for="phone" class="block text-sm font-medium text-gray-700">
                            Телефон (необязательно)
                        </label>
                        <input
                                id="phone"
                                name="phone"
                                type="tel"
                                class="form-control mt-1 appearance-none relative block w-full px-3 py-2 border border-gray-300 placeholder-gray-500 text-gray-900 rounded-lg focus:outline-none focus:ring-blue-500 focus:border-blue-500 sm:text-sm"
                                placeholder="+7 (___) ___-__-__"
                                value="${(userRegistrationRequest.phone)!''}"
                                data-validate="phone">
                        <div class="error-message text-red-600 text-sm mt-1 hidden"></div>
                    </div>

                    <!-- Password Fields -->
                    <div class="form-group">
                        <label for="password" class="block text-sm font-medium text-gray-700">
                            Пароль
                        </label>
                        <div class="mt-1 relative">
                            <input
                                    id="password"
                                    name="password"
                                    type="password"
                                    required
                                    class="form-control appearance-none relative block w-full px-3 py-2 border border-gray-300 placeholder-gray-500 text-gray-900 rounded-lg focus:outline-none focus:ring-blue-500 focus:border-blue-500 sm:text-sm"
                                    placeholder="Придумайте пароль"
                                    data-validate="required,minlength:6,maxlength:50">
                            <button type="button" class="password-toggle absolute inset-y-0 right-0 pr-3 flex items-center">
                                <i class="fas fa-eye text-gray-400 hover:text-gray-600"></i>
                            </button>
                            <div class="error-message text-red-600 text-sm mt-1 hidden"></div>
                        </div>
                        <div class="password-strength mt-1">
                            <div class="flex space-x-1">
                                <div class="strength-bar h-1 w-full bg-gray-200 rounded"></div>
                            </div>
                            <div class="strength-text text-xs mt-1"></div>
                        </div>
                    </div>

                    <div class="form-group">
                        <label for="confirmPassword" class="block text-sm font-medium text-gray-700">
                            Подтверждение пароля
                        </label>
                        <div class="mt-1 relative">
                            <input
                                    id="confirmPassword"
                                    name="confirmPassword"
                                    type="password"
                                    required
                                    class="form-control appearance-none relative block w-full px-3 py-2 border border-gray-300 placeholder-gray-500 text-gray-900 rounded-lg focus:outline-none focus:ring-blue-500 focus:border-blue-500 sm:text-sm"
                                    placeholder="Повторите пароль"
                                    data-validate="required,equalto:#password">
                            <button type="button" class="password-toggle absolute inset-y-0 right-0 pr-3 flex items-center">
                                <i class="fas fa-eye text-gray-400 hover:text-gray-600"></i>
                            </button>
                            <div class="error-message text-red-600 text-sm mt-1 hidden"></div>
                        </div>
                    </div>

                    <!-- Terms & Privacy -->
                    <div class="form-group">
                        <div class="flex items-start">
                            <input
                                    id="terms"
                                    name="terms"
                                    type="checkbox"
                                    required
                                    class="h-4 w-4 text-blue-600 focus:ring-blue-500 border-gray-300 rounded mt-1"
                                    data-validate="required">
                            <label for="terms" class="ml-2 block text-sm text-gray-900">
                                Я согласен с
                                <a href="/terms" target="_blank" class="text-blue-600 hover:text-blue-500">
                                    условиями использования
                                </a>
                                и
                                <a href="/privacy" target="_blank" class="text-blue-600 hover:text-blue-500">
                                    политикой конфиденциальности
                                </a>
                            </label>
                        </div>
                        <div class="error-message text-red-600 text-sm mt-1 hidden"></div>
                    </div>

                    <!-- Submit Button -->
                    <div>
                        <button
                                type="submit"
                                class="auth-submit-btn group relative w-full flex justify-center py-2 px-4 border border-transparent text-sm font-medium rounded-lg text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 disabled:opacity-50 disabled:cursor-not-allowed transition-colors">
                            <span class="absolute left-0 inset-y-0 flex items-center pl-3">
                                <i class="fas fa-user-plus text-blue-500 group-hover:text-blue-400"></i>
                            </span>
                            Зарегистрироваться
                        </button>
                    </div>
                </div>
            </form>

            <!-- Cosmetologist Registration Link -->
            <div class="text-center">
                <div class="relative">
                    <div class="absolute inset-0 flex items-center">
                        <div class="w-full border-t border-gray-300"></div>
                    </div>
                    <div class="relative flex justify-center text-sm">
                        <span class="px-2 bg-gray-50 text-gray-500">Вы косметолог?</span>
                    </div>
                </div>

                <div class="mt-6">
                    <a href="/auth/register/cosmetologist"
                       class="w-full inline-flex justify-center py-2 px-4 border border-blue-300 rounded-lg shadow-sm bg-blue-50 text-sm font-medium text-blue-600 hover:bg-blue-100 transition-colors">
                        <i class="fas fa-spa mr-2"></i>
                        Регистрация для косметологов
                    </a>
                </div>
            </div>
        </div>
    </div>
</@layout>
