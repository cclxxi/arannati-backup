/**
 * Product details page JavaScript
 */
document.addEventListener('DOMContentLoaded', function() {
    initializeThumbnailSwitching();
    initializeQuantitySelector();
    initializeProductTabs();
    initializeAddToCart();
    initializeFavoriteButton();
    initializeShareFunctionality();
});

/**
 * Initialize thumbnail image switching
 */
function initializeThumbnailSwitching() {
    const thumbnails = document.querySelectorAll('.thumbnail-item');
    const mainImage = document.getElementById('main-product-image');
    
    if (thumbnails.length && mainImage) {
        thumbnails.forEach(thumbnail => {
            thumbnail.addEventListener('click', function() {
                // Update main image
                mainImage.src = this.getAttribute('data-image');
                mainImage.alt = this.getAttribute('data-alt');
                
                // Update active thumbnail
                thumbnails.forEach(t => t.classList.remove('border-blue-500'));
                this.classList.add('border-blue-500');
            });
        });
        
        // Add image zoom functionality
        mainImage.addEventListener('mousemove', function(e) {
            const zoomer = this.closest('.main-image-container');
            if (!zoomer.classList.contains('zoom-enabled')) return;
            
            const offsetX = e.offsetX ? e.offsetX : e.touches[0].pageX;
            const offsetY = e.offsetY ? e.offsetY : e.touches[0].pageY;
            const x = offsetX / zoomer.offsetWidth * 100;
            const y = offsetY / zoomer.offsetHeight * 100;
            
            this.style.transformOrigin = x + '% ' + y + '%';
        });
        
        // Toggle zoom on click
        mainImage.addEventListener('click', function() {
            const zoomer = this.closest('.main-image-container');
            if (zoomer.classList.contains('zoom-enabled')) {
                zoomer.classList.remove('zoom-enabled');
                this.style.transform = 'scale(1)';
            } else {
                zoomer.classList.add('zoom-enabled');
                this.style.transform = 'scale(1.5)';
            }
        });
    }
}

/**
 * Initialize quantity selector
 */
function initializeQuantitySelector() {
    const quantityInput = document.getElementById('quantity');
    const minusBtn = document.querySelector('.quantity-btn.minus');
    const plusBtn = document.querySelector('.quantity-btn.plus');
    
    if (quantityInput && minusBtn && plusBtn) {
        minusBtn.addEventListener('click', function() {
            const currentValue = parseInt(quantityInput.value);
            if (currentValue > 1) {
                quantityInput.value = currentValue - 1;
            }
        });
        
        plusBtn.addEventListener('click', function() {
            const currentValue = parseInt(quantityInput.value);
            const maxValue = parseInt(quantityInput.getAttribute('max'));
            if (currentValue < maxValue) {
                quantityInput.value = currentValue + 1;
            }
        });
        
        quantityInput.addEventListener('change', function() {
            const currentValue = parseInt(this.value);
            const maxValue = parseInt(this.getAttribute('max'));
            
            if (isNaN(currentValue) || currentValue < 1) {
                this.value = 1;
            } else if (currentValue > maxValue) {
                this.value = maxValue;
            }
        });
    }
}

/**
 * Initialize product tabs
 */
function initializeProductTabs() {
    const tabs = document.querySelectorAll('.product-tab');
    const tabContents = document.querySelectorAll('.product-tab-content');
    
    if (tabs.length && tabContents.length) {
        tabs.forEach(tab => {
            tab.addEventListener('click', function() {
                const tabId = this.getAttribute('data-tab');
                
                // Update active tab
                tabs.forEach(t => {
                    t.classList.remove('active', 'border-blue-500', 'text-blue-600');
                    t.classList.add('border-transparent', 'text-gray-500');
                });
                this.classList.remove('border-transparent', 'text-gray-500');
                this.classList.add('active', 'border-blue-500', 'text-blue-600');
                
                // Show active tab content
                tabContents.forEach(content => {
                    content.classList.add('hidden');
                });
                document.getElementById(tabId + '-tab').classList.remove('hidden');
                
                // Save active tab in localStorage
                localStorage.setItem('activeProductTab', tabId);
            });
        });
        
        // Check if there's a saved active tab
        const savedTab = localStorage.getItem('activeProductTab');
        if (savedTab) {
            const tabToActivate = document.querySelector(`.product-tab[data-tab="${savedTab}"]`);
            if (tabToActivate) {
                tabToActivate.click();
            }
        }
    }
}

/**
 * Initialize add to cart functionality
 */
function initializeAddToCart() {
    const addToCartBtn = document.getElementById('add-to-cart');
    const quantityInput = document.getElementById('quantity');
    
    if (addToCartBtn && quantityInput) {
        addToCartBtn.addEventListener('click', function() {
            const productId = this.getAttribute('data-product-id');
            const quantity = parseInt(quantityInput.value);
            
            // Get cart items from localStorage
            const cartItems = JSON.parse(localStorage.getItem('cartItems') || '{}');
            
            // Add to cart
            if (cartItems[productId]) {
                cartItems[productId] += quantity;
            } else {
                cartItems[productId] = quantity;
            }
            
            // Save to localStorage
            localStorage.setItem('cartItems', JSON.stringify(cartItems));
            
            // Update cart count in header
            updateCartCount();
            
            // Show toast notification
            showToast('Товар добавлен в корзину', 'success');
            
            // Add animation to button
            this.classList.add('animate-pulse');
            setTimeout(() => {
                this.classList.remove('animate-pulse');
            }, 1000);
            
            // Here you would make an API call to add to cart
            console.log('Added product ' + productId + ' to cart with quantity ' + quantity);
        });
    }
}

/**
 * Initialize favorite button functionality
 */
function initializeFavoriteButton() {
    const favoriteBtn = document.getElementById('add-to-favorites');
    
    if (favoriteBtn) {
        const productId = favoriteBtn.getAttribute('data-product-id');
        const icon = favoriteBtn.querySelector('i');
        
        // Check if product is in favorites
        const favorites = JSON.parse(localStorage.getItem('favorites') || '[]');
        if (favorites.includes(productId)) {
            icon.classList.remove('far');
            icon.classList.add('fas');
        }
        
        favoriteBtn.addEventListener('click', function() {
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
    }
    
    // Initialize favorite buttons in similar products
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
 * Initialize share functionality
 */
function initializeShareFunctionality() {
    const shareBtn = document.getElementById('share-product');
    const shareModal = document.getElementById('share-modal');
    const closeShareModalBtn = document.getElementById('close-share-modal');
    const copyUrlBtn = document.getElementById('copy-url');
    const shareLinks = document.querySelectorAll('.share-link');
    
    if (shareBtn && shareModal) {
        // Set share URL
        const shareUrl = document.getElementById('share-url');
        if (shareUrl) {
            shareUrl.value = window.location.href;
        }
        
        // Open modal
        shareBtn.addEventListener('click', function() {
            shareModal.classList.remove('hidden');
        });
        
        // Close modal
        if (closeShareModalBtn) {
            closeShareModalBtn.addEventListener('click', function() {
                shareModal.classList.add('hidden');
            });
        }
        
        // Close modal when clicking outside
        shareModal.addEventListener('click', function(e) {
            if (e.target === shareModal) {
                shareModal.classList.add('hidden');
            }
        });
        
        // Copy URL to clipboard
        if (copyUrlBtn && shareUrl) {
            copyUrlBtn.addEventListener('click', function() {
                shareUrl.select();
                document.execCommand('copy');
                
                // Show success message
                this.innerHTML = '<i class="fas fa-check"></i>';
                setTimeout(() => {
                    this.innerHTML = '<i class="fas fa-copy"></i>';
                }, 2000);
                
                // Show toast notification
                showToast('Ссылка скопирована в буфер обмена', 'success');
            });
        }
        
        // Share links
        if (shareLinks.length) {
            shareLinks.forEach(link => {
                link.addEventListener('click', function(e) {
                    e.preventDefault();
                    
                    const platform = this.getAttribute('data-platform');
                    const url = encodeURIComponent(window.location.href);
                    const title = encodeURIComponent(document.title);
                    
                    let shareUrl;
                    switch (platform) {
                        case 'facebook':
                            shareUrl = `https://www.facebook.com/sharer/sharer.php?u=${url}`;
                            break;
                        case 'twitter':
                            shareUrl = `https://twitter.com/intent/tweet?url=${url}&text=${title}`;
                            break;
                        case 'telegram':
                            shareUrl = `https://t.me/share/url?url=${url}&text=${title}`;
                            break;
                        case 'whatsapp':
                            shareUrl = `https://api.whatsapp.com/send?text=${title} ${url}`;
                            break;
                    }
                    
                    window.open(shareUrl, '_blank', 'width=600,height=400');
                    
                    // Close modal
                    shareModal.classList.add('hidden');
                });
            });
        }
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