/**
 * Catalog page JavaScript
 */
document.addEventListener('DOMContentLoaded', function() {
    initializeFilters();
    initializeViewModes();
    initializeFavoriteButtons();
    initializeAddToCartButtons();
    initializePriceRangeFilter();
});

/**
 * Initialize filter functionality
 */
function initializeFilters() {
    const filterForm = document.getElementById('filter-form');
    const resetButton = document.getElementById('reset-filters');
    
    if (filterForm) {
        // Handle form submission
        filterForm.addEventListener('submit', function(e) {
            // Remove empty parameters to keep URL clean
            const formData = new FormData(filterForm);
            const params = new URLSearchParams();
            
            for (const [key, value] of formData.entries()) {
                if (value !== '' && value !== null) {
                    params.append(key, value);
                }
            }
            
            // Redirect to filtered URL
            window.location.href = '?' + params.toString();
            e.preventDefault();
        });
        
        // Handle reset button
        if (resetButton) {
            resetButton.addEventListener('click', function() {
                window.location.href = '/';
            });
        }
    }
}

/**
 * Initialize view mode switching (grid/list)
 */
function initializeViewModes() {
    const viewModeBtns = document.querySelectorAll('.view-mode-btn');
    const productsContainer = document.getElementById('products-grid');
    
    if (viewModeBtns.length && productsContainer) {
        // Check if view mode is stored in localStorage
        const savedViewMode = localStorage.getItem('catalogViewMode');
        if (savedViewMode) {
            setViewMode(savedViewMode);
        }
        
        viewModeBtns.forEach(btn => {
            btn.addEventListener('click', function() {
                const mode = this.getAttribute('data-mode');
                setViewMode(mode);
                
                // Save preference to localStorage
                localStorage.setItem('catalogViewMode', mode);
            });
        });
    }
    
    function setViewMode(mode) {
        // Update buttons
        viewModeBtns.forEach(b => {
            const btnMode = b.getAttribute('data-mode');
            if (btnMode === mode) {
                b.classList.remove('bg-gray-100', 'text-gray-600');
                b.classList.add('bg-gray-200', 'text-gray-800');
            } else {
                b.classList.remove('bg-gray-200', 'text-gray-800');
                b.classList.add('bg-gray-100', 'text-gray-600');
            }
        });
        
        // Update grid
        if (mode === 'grid') {
            productsContainer.classList.remove('grid-cols-1');
            productsContainer.classList.add('grid-cols-1', 'sm:grid-cols-2', 'lg:grid-cols-3');
            
            // Update product cards for grid view
            document.querySelectorAll('.product-card').forEach(card => {
                card.classList.remove('flex', 'flex-row');
                card.querySelector('.product-image-container')?.classList.remove('w-1/3');
                card.querySelector('.product-info-container')?.classList.remove('w-2/3');
            });
        } else {
            productsContainer.classList.remove('sm:grid-cols-2', 'lg:grid-cols-3');
            productsContainer.classList.add('grid-cols-1');
            
            // Update product cards for list view
            document.querySelectorAll('.product-card').forEach(card => {
                card.classList.add('flex', 'flex-row');
                
                // Find image and info containers
                const imageContainer = card.querySelector('.relative');
                const infoContainer = card.querySelector('.p-4');
                
                if (imageContainer && infoContainer) {
                    imageContainer.classList.add('w-1/3', 'product-image-container');
                    infoContainer.classList.add('w-2/3', 'product-info-container');
                }
            });
        }
    }
}

/**
 * Initialize favorite button functionality
 */
function initializeFavoriteButtons() {
    const favoriteBtns = document.querySelectorAll('.favorite-btn');
    
    if (favoriteBtns.length) {
        // Check if user has favorites stored in localStorage
        const favorites = JSON.parse(localStorage.getItem('favorites') || '[]');
        
        // Update initial state of favorite buttons
        favoriteBtns.forEach(btn => {
            const productId = btn.getAttribute('data-product-id');
            const icon = btn.querySelector('i');
            
            if (favorites.includes(productId)) {
                icon.classList.remove('far');
                icon.classList.add('fas');
            }
        });
        
        // Add click event listeners
        favoriteBtns.forEach(btn => {
            btn.addEventListener('click', function(e) {
                e.preventDefault();
                e.stopPropagation();
                
                const productId = this.getAttribute('data-product-id');
                const icon = this.querySelector('i');
                
                // Check if user is authenticated
                const isAuthenticated = document.body.getAttribute('data-authenticated') === 'true';
                if (!isAuthenticated) {
                    window.location.href = '/auth/login';
                    return;
                }
                
                // Toggle favorite status
                if (icon.classList.contains('far')) {
                    // Add to favorites
                    icon.classList.remove('far');
                    icon.classList.add('fas');
                    
                    // Add to localStorage
                    if (!favorites.includes(productId)) {
                        favorites.push(productId);
                        localStorage.setItem('favorites', JSON.stringify(favorites));
                    }
                    
                    // Show toast notification
                    showToast('Товар добавлен в избранное', 'success');
                    
                    // Here you would make an API call to add to favorites
                    console.log('Added product ' + productId + ' to favorites');
                } else {
                    // Remove from favorites
                    icon.classList.remove('fas');
                    icon.classList.add('far');
                    
                    // Remove from localStorage
                    const index = favorites.indexOf(productId);
                    if (index > -1) {
                        favorites.splice(index, 1);
                        localStorage.setItem('favorites', JSON.stringify(favorites));
                    }
                    
                    // Show toast notification
                    showToast('Товар удален из избранного', 'info');
                    
                    // Here you would make an API call to remove from favorites
                    console.log('Removed product ' + productId + ' from favorites');
                }
            });
        });
    }
}

/**
 * Initialize add to cart button functionality
 */
function initializeAddToCartButtons() {
    const addToCartBtns = document.querySelectorAll('.add-to-cart-btn');
    
    if (addToCartBtns.length) {
        // Check if user has cart items stored in localStorage
        const cartItems = JSON.parse(localStorage.getItem('cartItems') || '{}');
        
        addToCartBtns.forEach(btn => {
            btn.addEventListener('click', function(e) {
                e.preventDefault();
                e.stopPropagation();
                
                const productId = this.getAttribute('data-product-id');
                
                // Add to cart in localStorage
                if (cartItems[productId]) {
                    cartItems[productId]++;
                } else {
                    cartItems[productId] = 1;
                }
                
                localStorage.setItem('cartItems', JSON.stringify(cartItems));
                
                // Update cart count in header
                updateCartCount();
                
                // Show toast notification
                showToast('Товар добавлен в корзину', 'success');
                
                // Here you would make an API call to add to cart
                console.log('Added product ' + productId + ' to cart');
            });
        });
    }
}

/**
 * Initialize price range filter
 */
function initializePriceRangeFilter() {
    const minPriceInput = document.getElementById('minPrice');
    const maxPriceInput = document.getElementById('maxPrice');
    
    if (minPriceInput && maxPriceInput) {
        // Ensure min price is not greater than max price
        minPriceInput.addEventListener('change', function() {
            if (maxPriceInput.value && parseInt(this.value) > parseInt(maxPriceInput.value)) {
                maxPriceInput.value = this.value;
            }
        });
        
        // Ensure max price is not less than min price
        maxPriceInput.addEventListener('change', function() {
            if (minPriceInput.value && parseInt(this.value) < parseInt(minPriceInput.value)) {
                minPriceInput.value = this.value;
            }
        });
    }
}

/**
 * Update cart count in header
 */
function updateCartCount() {
    const cartCountElement = document.getElementById('cart-count');
    if (cartCountElement) {
        const cartItems = JSON.parse(localStorage.getItem('cartItems') || '{}');
        let count = 0;
        
        // Count total items in cart
        for (const productId in cartItems) {
            count += cartItems[productId];
        }
        
        // Update count
        cartCountElement.textContent = count;
        
        // Show/hide count
        if (count > 0) {
            cartCountElement.classList.remove('hidden');
        } else {
            cartCountElement.classList.add('hidden');
        }
    }
}

/**
 * Show toast notification
 */
function showToast(message, type = 'success') {
    // Remove any existing toasts
    const existingToasts = document.querySelectorAll('.toast-notification');
    existingToasts.forEach(toast => {
        toast.remove();
    });
    
    // Create toast element
    const toast = document.createElement('div');
    toast.className = 'toast-notification fixed bottom-4 right-4 px-4 py-2 rounded-lg shadow-lg z-50 transition-opacity duration-300';
    
    // Set toast style based on type
    if (type === 'success') {
        toast.classList.add('bg-green-500', 'text-white');
        toast.innerHTML = '<i class="fas fa-check-circle mr-2"></i>' + message;
    } else if (type === 'error') {
        toast.classList.add('bg-red-500', 'text-white');
        toast.innerHTML = '<i class="fas fa-exclamation-circle mr-2"></i>' + message;
    } else if (type === 'info') {
        toast.classList.add('bg-blue-500', 'text-white');
        toast.innerHTML = '<i class="fas fa-info-circle mr-2"></i>' + message;
    } else if (type === 'warning') {
        toast.classList.add('bg-yellow-500', 'text-white');
        toast.innerHTML = '<i class="fas fa-exclamation-triangle mr-2"></i>' + message;
    }
    
    // Add toast to document
    document.body.appendChild(toast);
    
    // Remove after 3 seconds
    setTimeout(() => {
        toast.classList.add('opacity-0');
        setTimeout(() => {
            toast.remove();
        }, 300);
    }, 3000);
}