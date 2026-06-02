const BASE_URL = 'http://localhost:8080';

async function request(path, options = {}) {
    try {
        const response = await fetch(BASE_URL + path, options);
        const data = await response.json();
        return data;
    } catch (error) {
        console.error('API Error:', error);
        throw error;
    }
}

function getAuthHeader() {
    const token = localStorage.getItem('token');
    return {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
    };
}

async function getAllProducts() {
    return request('/api/products');
}

async function getProductById(id) {
    return request(`/api/products/${id}`);
}

async function searchProducts(query) {
    return request(`/api/products/search?q=${query}`);
}

async function getProductsByCategory(category) {
    return request(`/api/products/category/${category}`);
}

async function signup(name, email, password) {
    return request('/api/auth/signup', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ name, email, password })
    });
}

async function login(email, password) {
    return request('/api/auth/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email, password })
    });
}

async function placeOrder(items) {
    return request('/api/orders', {
        method: 'POST',
        headers: getAuthHeader(),
        body: JSON.stringify({ items })
    });
}

async function getMyOrders() {
    return request('/api/orders/my', {
        method: 'GET',
        headers: getAuthHeader()
    });
}

function getCart() {
    return JSON.parse(localStorage.getItem('cart') || '[]');
}

function saveCart(cart) {
    localStorage.setItem('cart', JSON.stringify(cart));
}

function addToCart(product) {
    const cart = getCart();
    const existing = cart.find(item => item.id === product.id);
    if (existing) {
        existing.quantity++;
    } else {
        cart.push({ ...product, quantity: 1 });
    }
    saveCart(cart);
    updateCartCount();
}

function removeFromCart(productId) {
    const cart = getCart().filter(item => item.id !== productId);
    saveCart(cart);
    updateCartCount();
}

function getCartCount() {
    return getCart().reduce((total, item) => total + item.quantity, 0);
}

function getCartTotal() {
    return getCart().reduce((total, item) =>
        total + (item.price * item.quantity), 0).toFixed(2);
}

function updateCartCount() {
    const badge = document.getElementById('cartCount');
    if (badge) badge.textContent = getCartCount();
}

function isLoggedIn() {
    return localStorage.getItem('token') !== null;
}

function getUserName() {
    return localStorage.getItem('userName') || 'Account';
}

function logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('userName');
    localStorage.removeItem('userEmail');
    window.location.href = 'login.html';
}
