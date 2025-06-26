<#assign pageTitle = "Регистрация косметолога - Arannati">
<#assign pageDescription = "Регистрация для специалистов косметологии">
<#assign additionalJS = ["/static/js/pages/auth.js", "/static/js/components/file-uploader.js", "/static/js/pages/cosmetologist-registration.js"]>
<#include "../layouts/main.ftlh">

<@layout>
    <div class="min-h-screen flex items-center justify-center py-12 px-4 sm:px-6 lg:px-8">
        <div class="max-w-2xl w-full space-y-8">
            <!-- Header -->
            <div class="text-center">
                <img class="mx-auto h-12 w-auto" src="/static/images/logo.svg" alt="Arannati">
                <h2 class="mt-6 text-3xl font-extrabold text-gray-900">
                    Регистрация косметолога
                </h2>
                <p class="mt-2 text-sm text-gray-600">
                    Получите доступ к профессиональным ценам и материалам
                </p>
                <p class="text-sm text-gray-600">
                    Обычный пользователь?
                    <a href="/auth/register" class="font-medium text-blue-600 hover:text-blue-500">
                        Обычная регистрация
                    </a>
                </p>
            </div>

            <!-- Registration Form -->
            <form class="auth-form cosmetologist-form" method="POST" action="/auth/register/cosmetologist"
                  enctype="multipart/form-data" novalidate>
                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>

                <div class="space-y-8">
                    <!-- Personal Information -->
                    <div class="bg-white p-6 rounded-lg border border-gray-200">
                        <h3 class="text-lg font-medium text-gray-900 mb-4">
                            <i class="fas fa-user mr-2 text-blue-600"></i>
                            Личные данные
                        </h3>

                        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                            <!-- Name Fields -->
                            <div class="form-group">
                                <label for="firstName" class="block text-sm font-medium text-gray-700">
                                    Имя *
                                </label>
                                <input
                                        id="firstName"
                                        name="firstName"
                                        type="text"
                                        required
                                        class="form-control mt-1 block w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-blue-500 focus:border-blue-500 sm:text-sm"
                                        placeholder="Введите имя"
                                        value="${(cosmetologistRegistrationRequest.firstName)!''}"
                                        data-validate="required,minlength:2,maxlength:50">
                                <div class="error-message text-red-600 text-sm mt-1 hidden"></div>
                            </div>

                            <div class="form-group">
                                <label for="lastName" class="block text-sm font-medium text-gray-700">
                                    Фамилия *
                                </label>
                                <input
                                        id="lastName"
                                        name="lastName"
                                        type="text"
                                        required
                                        class="form-control mt-1 block w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-blue-500 focus:border-blue-500 sm:text-sm"
                                        placeholder="Введите фамилию"
                                        value="${(cosmetologistRegistrationRequest.lastName)!''}"
                                        data-validate="required,minlength:2,maxlength:50">
                                <div class="error-message text-red-600 text-sm mt-1 hidden"></div>
                            </div>

                            <!-- Contact Info -->
                            <div class="form-group">
                                <label for="email" class="block text-sm font-medium text-gray-700">
                                    Email адрес *
                                </label>
                                <input
                                        id="email"
                                        name="email"
                                        type="email"
                                        required
                                        class="form-control mt-1 block w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-blue-500 focus:border-blue-500 sm:text-sm"
                                        placeholder="Введите email"
                                        value="${(cosmetologistRegistrationRequest.email)!''}"
                                        data-validate="required,email,remote:/auth/check-email">
                                <div class="error-message text-red-600 text-sm mt-1 hidden"></div>
                            </div>

                            <div class="form-group">
                                <label for="phone" class="block text-sm font-medium text-gray-700">
                                    Телефон
                                </label>
                                <input
                                        id="phone"
                                        name="phone"
                                        type="tel"
                                        class="form-control mt-1 block w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-blue-500 focus:border-blue-500 sm:text-sm"
                                        placeholder="+7 (___) ___-__-__"
                                        value="${(cosmetologistRegistrationRequest.phone)!''}"
                                        data-validate="phone">
                                <div class="error-message text-red-600 text-sm mt-1 hidden"></div>
                            </div>

                            <!-- Password Fields -->
                            <div class="form-group">
                                <label for="password" class="block text-sm font-medium text-gray-700">
                                    Пароль *
                                </label>
                                <div class="mt-1 relative">
                                    <input
                                            id="password"
                                            name="password"
                                            type="password"
                                            required
                                            class="form-control block w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-blue-500 focus:border-blue-500 sm:text-sm"
                                            placeholder="Придумайте пароль"
                                            data-validate="required,minlength:6,maxlength:50">
                                    <button type="button" class="password-toggle absolute inset-y-0 right-0 pr-3 flex items-center">
                                        <i class="fas fa-eye text-gray-400 hover:text-gray-600"></i>
                                    </button>
                                </div>
                                <div class="error-message text-red-600 text-sm mt-1 hidden"></div>
                                <div class="password-strength mt-1">
                                    <div class="flex space-x-1">
                                        <div class="strength-bar h-1 w-full bg-gray-200 rounded"></div>
                                    </div>
                                    <div class="strength-text text-xs mt-1"></div>
                                </div>
                            </div>

                            <div class="form-group">
                                <label for="confirmPassword" class="block text-sm font-medium text-gray-700">
                                    Подтверждение пароля *
                                </label>
                                <div class="mt-1 relative">
                                    <input
                                            id="confirmPassword"
                                            name="confirmPassword"
                                            type="password"
                                            required
                                            class="form-control block w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-blue-500 focus:border-blue-500 sm:text-sm"
                                            placeholder="Повторите пароль"
                                            data-validate="required,equalto:#password">
                                    <button type="button" class="password-toggle absolute inset-y-0 right-0 pr-3 flex items-center">
                                        <i class="fas fa-eye text-gray-400 hover:text-gray-600"></i>
                                    </button>
                                </div>
                                <div class="error-message text-red-600 text-sm mt-1 hidden"></div>
                            </div>
                        </div>
                    </div>

                    <!-- Professional Information -->
                    <div class="bg-white p-6 rounded-lg border border-gray-200">
                        <h3 class="text-lg font-medium text-gray-900 mb-4">
                            <i class="fas fa-graduation-cap mr-2 text-blue-600"></i>
                            Профессиональная информация
                        </h3>

                        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                            <div class="form-group md:col-span-2">
                                <label for="institutionName" class="block text-sm font-medium text-gray-700">
                                    Учебное заведение *
                                </label>
                                <input
                                        id="institutionName"
                                        name="institutionName"
                                        type="text"
                                        required
                                        class="form-control mt-1 block w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-blue-500 focus:border-blue-500 sm:text-sm"
                                        placeholder="Название учебного заведения"
                                        value="${(cosmetologistRegistrationRequest.institutionName)!''}"
                                        data-validate="required,maxlength:255">
                                <div class="error-message text-red-600 text-sm mt-1 hidden"></div>
                            </div>

                            <div class="form-group">
                                <label for="graduationYear" class="block text-sm font-medium text-gray-700">
                                    Год окончания
                                </label>
                                <input
                                        id="graduationYear"
                                        name="graduationYear"
                                        type="number"
                                        min="1990"
                                        max="2024"
                                        class="form-control mt-1 block w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-blue-500 focus:border-blue-500 sm:text-sm"
                                        placeholder="2020"
                                        value="${(cosmetologistRegistrationRequest.graduationYear)!''}"
                                        data-validate="min:1990,max:2024">
                                <div class="error-message text-red-600 text-sm mt-1 hidden"></div>
                            </div>

                            <div class="form-group">
                                <label for="licenseNumber" class="block text-sm font-medium text-gray-700">
                                    Номер лицензии
                                </label>
                                <input
                                        id="licenseNumber"
                                        name="licenseNumber"
                                        type="text"
                                        class="form-control mt-1 block w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-blue-500 focus:border-blue-500 sm:text-sm"
                                        placeholder="Номер лицензии (если есть)"
                                        value="${(cosmetologistRegistrationRequest.licenseNumber)!''}"
                                        data-validate="maxlength:100">
                                <div class="error-message text-red-600 text-sm mt-1 hidden"></div>
                            </div>

                            <div class="form-group md:col-span-2">
                                <label for="specialization" class="block text-sm font-medium text-gray-700">
                                    Специализация
                                </label>
                                <input
                                        id="specialization"
                                        name="specialization"
                                        type="text"
                                        class="form-control mt-1 block w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-blue-500 focus:border-blue-500 sm:text-sm"
                                        placeholder="Например: эстетическая косметология, инъекционная косметология"
                                        value="${(cosmetologistRegistrationRequest.specialization)!''}"
                                        data-validate="maxlength:255">
                                <div class="error-message text-red-600 text-sm mt-1 hidden"></div>
                            </div>
                        </div>
                    </div>

                    <!-- Document Upload -->
                    <div class="bg-white p-6 rounded-lg border border-gray-200">
                        <h3 class="text-lg font-medium text-gray-900 mb-4">
                            <i class="fas fa-file-upload mr-2 text-blue-600"></i>
                            Документы
                        </h3>

                        <div class="form-group">
                            <label for="diplomaFile" class="block text-sm font-medium text-gray-700 mb-2">
                                Диплом или сертификат *
                            </label>
                            <div class="file-upload-area border-2 border-dashed border-gray-300 rounded-lg p-6 text-center hover:border-blue-400 transition-colors cursor-pointer"
                                 id="diploma-upload-area">
                                <input
                                        type="file"
                                        id="diplomaFile"
                                        name="diplomaFile"
                                        accept=".pdf,.jpg,.jpeg,.png"
                                        required
                                        class="hidden"
                                        data-validate="required,filesize:10MB,filetype:pdf,jpg,jpeg,png">

                                <div class="upload-placeholder">
                                    <i class="fas fa-cloud-upload-alt text-4xl text-gray-400 mb-4"></i>
                                    <p class="text-sm text-gray-600">
                                        <span class="font-medium text-blue-600 hover:text-blue-500 cursor-pointer">
                                            Нажмите для выбора файла
                                        </span>
                                        или перетащите файл сюда
                                    </p>
                                    <p class="text-xs text-gray-500 mt-2">
                                        PDF, JPG, PNG до 10MB
                                    </p>
                                </div>

                                <div class="upload-preview hidden">
                                    <div class="flex items-center justify-center space-x-2">
                                        <i class="fas fa-file text-blue-600"></i>
                                        <span class="file-name text-sm text-gray-700"></span>
                                        <span class="file-size text-xs text-gray-500"></span>
                                        <button type="button" class="remove-file text-red-600 hover:text-red-800">
                                            <i class="fas fa-times"></i>
                                        </button>
                                    </div>
                                </div>
                            </div>
                            <div class="error-message text-red-600 text-sm mt-1 hidden"></div>
                            <p class="text-xs text-gray-500 mt-2">
                                Загрузите скан диплома, сертификата или другого документа, подтверждающего вашу квалификацию
                            </p>
                        </div>
                    </div>

                    <!-- Terms & Privacy -->
                    <div class="bg-gray-50 p-6 rounded-lg">
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
                                    </a>,
                                    <a href="/privacy" target="_blank" class="text-blue-600 hover:text-blue-500">
                                        политикой конфиденциальности
                                    </a>
                                    и даю согласие на обработку персональных данных
                                </label>
                            </div>
                            <div class="error-message text-red-600 text-sm mt-1 hidden"></div>
                        </div>

                        <div class="mt-4 p-4 bg-blue-50 rounded-lg">
                            <div class="flex">
                                <i class="fas fa-info-circle text-blue-600 mt-0.5 mr-2"></i>
                                <div class="text-sm text-blue-800">
                                    <p class="font-medium mb-1">Процесс верификации:</p>
                                    <ol class="list-decimal list-inside space-y-1 text-xs">
                                        <li>Ваша заявка будет рассмотрена в течение 1-3 рабочих дней</li>
                                        <li>Мы проверим предоставленные документы</li>
                                        <li>При одобрении вы получите доступ к специальным ценам и материалам</li>
                                        <li>Уведомление о результате придет на ваш email</li>
                                    </ol>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Submit Button -->
                    <div>
                        <button
                                type="submit"
                                class="auth-submit-btn group relative w-full flex justify-center py-3 px-4 border border-transparent text-sm font-medium rounded-lg text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 disabled:opacity-50 disabled:cursor-not-allowed transition-colors">
                            <span class="absolute left-0 inset-y-0 flex items-center pl-3">
                                <i class="fas fa-spa text-blue-500 group-hover:text-blue-400"></i>
                            </span>
                            Отправить заявку на регистрацию
                        </button>
                    </div>
                </div>
            </form>

            <!-- Back Link -->
            <div class="text-center">
                <a href="/auth/register" class="text-sm text-gray-600 hover:text-blue-600">
                    <i class="fas fa-arrow-left mr-1"></i>
                    Вернуться к обычной регистрации
                </a>
            </div>
        </div>
    </div>
</@layout>
