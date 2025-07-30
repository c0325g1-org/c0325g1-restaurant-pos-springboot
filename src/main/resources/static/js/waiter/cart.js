export function addToCart(tableId, menuItemId, quantity) {
    fetch(`/waiter/order/cart/add?tableId=${tableId}&menuItemId=${menuItemId}&quantity=${quantity}`, {
        method: 'POST'
    }).then(res => res.ok ? res.json() : Promise.reject(res))
        .then(() => updateCartView(tableId));
}

export function removeFromCart(tableId, menuItemId) {
    fetch(`/waiter/order/cart/remove?tableId=${tableId}&menuItemId=${menuItemId}`, {
        method: 'POST'
    }).then(() => updateCartView(tableId));
}

export function updateQuantity(tableId, menuItemId, delta) {
    fetch(`/waiter/order/cart/update-quantity?tableId=${tableId}&menuItemId=${menuItemId}&delta=${delta}`, {
        method: 'POST'
    }).then(() => updateCartView(tableId));
}

export function updateCartView(tableId) {
    fetch(`/waiter/order/cart/view?tableId=${tableId}`)
        .then(res => res.text())
        .then(html => {
            document.getElementById("cart-items").innerHTML = html;
            updateTotal();
        });
}

function updateTotal() {
    const prices = document.querySelectorAll("#cart-items .item-price");
    let total = 0;
    prices.forEach(p => total += parseInt(p.dataset.total));
    document.getElementById("totalAmount").innerText = total.toLocaleString() + 'đ';
}
