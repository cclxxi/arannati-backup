<header class="bg-white shadow-sm border-b border-gray-200">
    <nav class="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8">
        <div class="flex justify-between items-center h-16">
            <!-- Logo -->
            <div class="flex items-center">
                <a href="/" class="flex items-center space-x-2">
                    <img src="/static/images/logo.svg" alt="Arannati" class="h-8 w-auto">
                    <span class="text-xl font-bold text-gray-900">Arannati</span>
                </a>
            </div>

            <!-- Navigation -->
            <div class="hidden md:flex items-center space-x-8">
                <a href="/catalog" class="text-gray-700 hover:text-blue-600 font-medium">Каталог</a>
                <a href="/brands" class="text-gray-700 hover:text-blue-600 font-medium">Бренды</a>
                <a href="/news" class="text-gray-700 hover:text-blue-600 font-medium">Новости</a>
                <a href="/about" class="text-gray-700 hover:text-blue-600 font-medium">О нас</a>
                <a href="/contacts" class="text-gray-700 hover:text-blue-600 font-medium">Контакты</a>
            </div>

            <!-- User Actions -->
            <div class="flex items-center space-x-4">
                <!-- Search -->
                <button class="text-gray-500 hover:text-gray-700">
                    <i class="fas fa-search"></i>
                </button>

                <!-- Cart -->
                <a href="/cart" class="relative text-gray-500 hover:text-gray-700">
                    <i class="fas fa-shopping-cart"></i>
                    <span class="cart-counter absolute -top-2 -right-2 bg-blue-600 text-white text-xs rounded-full h-5 w-5 flex items-center justify-center">0</span>
                </a>

                <!-- User Menu -->
                <#if user??>
                    <div class="relative dropdown">
                        <button class="flex items-center space-x-2 text-gray-700 hover:text-blue-600" data-bs-toggle="dropdown">
                            <i class="fas fa-user"></i>
                            <span>${user.firstName}</span>
                            <i class="fas fa-chevron-down text-xs"></i>
                        </button>
                        <ul class="dropdown-menu dropdown-menu-end">
                            <li><a class="dropdown-item" href="/profile">Личный кабинет</a></li>
                            <li><a class="dropdown-item" href="/orders">Мои заказы</a></li>
                            <li><a class="dropdown-item" href="/wishlist">Избранное</a></li>
                            <#if user.role == 'ADMIN'>
                                <li><hr class="dropdown-divider"></li>
                                <li><a class="dropdown-item" href="/admin">Админ панель</a></li>
                            </#if>
                            <li><hr class="dropdown-divider"></li>
                            <li><a class="dropdown-item" href="/auth/logout">Выйти</a></li>
                        </ul>
                    </div>
                <#else>
                    <a href="/auth/login" class="bg-blue-600 text-white px-4 py-2 rounded-lg hover:bg-blue-700 transition-colors">
                        Войти
                    </a>
                </#if>

                <!-- Mobile menu button -->
                <button class="md:hidden text-gray-500 hover:text-gray-700" id="mobile-menu-button">
                    <i class="fas fa-bars"></i>
                </button>
            </div>
        </div>

        <!-- Mobile menu -->
        <div class="md:hidden hidden" id="mobile-menu">
            <div class="px-2 pt-2 pb-3 space-y-1 border-t border-gray-200">
                <a href="/catalog" class="block px-3 py-2 text-gray-700 hover:text-blue-600">Каталог</a>
                <a href="/brands" class="block px-3 py-2 text-gray-700 hover:text-blue-600">Бренды</a>
                <a href="/news" class="block px-3 py-2 text-gray-700 hover:text-blue-600">Новости</a>
                <a href="/about" class="block px-3 py-2 text-gray-700 hover:text-blue-600">О нас</a>
                <a href="/contacts" class="block px-3 py-2 text-gray-700 hover:text-blue-600">Контакты</a>
            </div>
        </div>
    </nav>
</header>