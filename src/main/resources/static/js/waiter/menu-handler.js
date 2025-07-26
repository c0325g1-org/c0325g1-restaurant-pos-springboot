import { addToCart } from './cart.js';

export function initMenuHandlers(tableId) {
    document.querySelectorAll(".menu-item").forEach(item => {
        item.addEventListener("click", () => {
            const id = item.dataset.menuId;
            addToCart(tableId, id, 1);
        });
    });
}
