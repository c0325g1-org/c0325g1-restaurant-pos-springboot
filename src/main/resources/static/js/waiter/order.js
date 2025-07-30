import { initMenuHandlers } from './menu-handler.js';
import { initCategoryFilters } from './menu-filter.js';
import { updateCartView, removeFromCart, updateQuantity, addToCart } from './cart.js';
import { loadCalledItems, loadServedItems, setupCancelHandler } from './order-status.js';

window.updateQuantity = (tableId, menuItemId, delta) => updateQuantity(tableId, menuItemId, delta);
window.removeFromCart = (tableIdFromView, menuItemId) => removeFromCart(tableIdFromView, menuItemId);

initMenuHandlers(tableId);
initCategoryFilters();
updateCartView(tableId);
loadCalledItems(tableId);
loadServedItems(tableId);
setupCancelHandler(tableId);