document.addEventListener('DOMContentLoaded', function() {
    initGlobalComponents();
    initUtilities();
});

function initGlobalComponents() {
    // Инициализация глобальных компонентов
    initTooltips();
    initDropdowns();
    initSearchComponent();
    initCartCounter();
}

function initTooltips() {
    // Инициализация Bootstrap tooltips
    const tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    tooltipTriggerList.map(function (tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });
}

function initDropdowns() {
    // Инициализация Bootstrap dropdowns
    const dropdownElementList = [].slice.call(document.querySelectorAll('.dropdown-toggle'));
    dropdownElementList.map(function (dropdownToggleEl) {
        return new bootstrap.Dropdown(dropdownToggleEl);
    });
}

function initSearchComponent() {
    const searchInputs = document.querySelectorAll('.search-input');
    searchInputs.forEach(input => {
        let searchTimeout;

        input.addEventListener('input', function() {
            clearTimeout(searchTimeout);
            const query = this.value.trim();

            if (query.length >= 2) {
                searchTimeout = setTimeout(() => {
                    performSearch(query, this);
                }, AppConfig.ui.debounceDelay);
            } else {
                hideSearchResults(this);
            }
        });

        // Скрытие результатов при клике вне поиска
        document.addEventListener('click', function(e) {
            if (!input.contains(e.target)) {
                hideSearchResults(input);
            }
        });
    });
}

async function performSearch(query, inputElement) {
    try {
        const results = await api.get('/api/search', { q: query });
        showSearchResults(results, inputElement);
    } catch (error) {
        console.error('Search failed:', error);
    }
}

function showSearchResults(results, inputElement) {
    const container = inputElement.closest('.search-container');
    let resultsContainer = container.querySelector('.search-results');

    if (!resultsContainer) {
        resultsContainer = document.createElement('div');
        resultsContainer.className = 'search-results';
        container.appendChild(resultsContainer);
    }

    if (results && results.length > 0) {
        resultsContainer.innerHTML = results.map(result => `
            <div class="search-result-item" data-url="${result.url}">
                <div class="font-medium">${result.title}</div>
                <div class="text-sm text-gray-600">${result.description}</div>
            </div>
        `).join('');

        // Обработка кликов по результатам
        resultsContainer.querySelectorAll('.search-result-item').forEach(item => {
            item.addEventListener('click', function() {
                window.location.href = this.dataset.url;
            });
        });

        resultsContainer.style.display = 'block';
    } else {
        resultsContainer.innerHTML = '<div class="search-result-item text-gray-500">Ничего не найдено</div>';
        resultsContainer.style.display = 'block';
    }
}

function hideSearchResults(inputElement) {
    const container = inputElement.closest('.search-container');
    const resultsContainer = container?.querySelector('.search-results');
    if (resultsContainer) {
        resultsContainer.style.display = 'none';
    }
}

function initCartCounter() {
    // Обновление счетчика корзины
    updateCartCounter();

    // Слушаем события добавления в корзину
    document.addEventListener('cartUpdated', updateCartCounter);
}

async function updateCartCounter() {
    try {
        const cartData = await api.get('/api/cart/count');
        const counter = document.querySelector('.cart-counter');
        if (counter && cartData.count !== undefined) {
            counter.textContent = cartData.count;
            counter.style.display = cartData.count > 0 ? 'flex' : 'none';
        }
    } catch (error) {
        console.error('Failed to update cart counter:', error);
    }
}

function initUtilities() {
    // Глобальные утилиты
    window.utils = {
        formatPrice: (price) => {
            return new Intl.NumberFormat('ru-RU', {
                style: 'currency',
                currency: 'KZT'
            }).format(price);
        },

        formatDate: (date) => {
            return new Intl.DateTimeFormat('ru-RU', {
                year: 'numeric',
                month: 'long',
                day: 'numeric'
            }).format(new Date(date));
        },

        debounce: (func, wait) => {
            let timeout;
            return function executedFunction(...args) {
                const later = () => {
                    clearTimeout(timeout);
                    func(...args);
                };
                clearTimeout(timeout);
                timeout = setTimeout(later, wait);
            };
        }
    };
}