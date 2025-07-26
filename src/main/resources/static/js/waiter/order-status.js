let cancelItemId = null;
let cancelModal = null;

export function setupCancelHandler(tableId) {
    cancelModal = new bootstrap.Modal(document.getElementById('confirmCancelModal'));
    document.getElementById("confirmCancelBtn")?.addEventListener("click", () => {
        if (cancelItemId) {
            fetch(`/waiter/order/cancel-item?id=${cancelItemId}`, { method: 'POST' })
                .then(() => {
                    loadCalledItems(tableId);
                    cancelModal.hide();
                });
        }
    });
}

export function confirmCancel(itemId) {
    cancelItemId = itemId;
    cancelModal?.show();
}

export function loadCalledItems(tableId) {
    fetch(`/waiter/order/called-items?tableId=${tableId}`)
        .then(res => res.json())
        .then(data => {
            const container = document.getElementById("called-items");
            container.innerHTML = "";
            data.forEach(item => {
                const div = document.createElement("div");
                div.classList.add("border-bottom", "py-2");

                let statusBadge = getStatusBadge(item.status);
                let actions = getActionButtons(item);

                div.innerHTML = `
                <div class="d-flex justify-content-between align-items-center">
                    <div>
                        <strong>${item.menuItem.name}</strong> - ${item.quantity} × ${item.price.toLocaleString()}đ
                        ${statusBadge}
                    </div>
                    <div>${actions}</div>
                </div>`;
                container.appendChild(div);
            });
        });
}

export function loadServedItems(tableId) {
    fetch(`/waiter/order/served-items?tableId=${tableId}`)
        .then(res => res.json())
        .then(data => {
            const container = document.getElementById("served-items");
            container.innerHTML = "";
            data.forEach(item => {
                const div = document.createElement("div");
                div.classList.add("d-flex", "justify-content-between", "border-bottom", "py-1");
                div.innerHTML = `<span>${item.menuItem.name}</span><span>${item.quantity} × ${item.price.toLocaleString()}đ</span>`;
                container.appendChild(div);
            });
        });
}

function getStatusBadge(status) {
    switch (status) {
        case "NEW": return '<span class="badge bg-primary">Mới gọi</span>';
        case "COOKING": return '<span class="badge bg-warning">Đang chế biến</span>';
        case "READY": return '<span class="badge bg-success">Sẵn sàng</span>';
        case "SERVED": return '<span class="badge bg-info">Đã phục vụ</span>';
        case "CANCELLED": return '<span class="badge bg-secondary">Đã huỷ</span>';
        default: return '';
    }
}

function getActionButtons(item) {
    if (item.status === "NEW" || item.status === "COOKING") {
        return `<button class="btn btn-sm btn-outline-danger ms-2" onclick="window.confirmCancel(${item.id})">Huỷ</button>`;
    } else if (item.status === "READY") {
        return `<button class="btn btn-sm btn-success ms-2" onclick="markAsServed(${item.id})">Đã phục vụ</button>`;
    }
    return '';
}

window.confirmCancel = confirmCancel;

function markAsServed(itemId) {
    fetch(`/waiter/order/serve-item?id=${itemId}`, { method: 'POST' })
        .then(() => {
            const tableId = document.querySelector('[name="tableId"]')?.value;
            loadCalledItems(tableId);
            loadServedItems(tableId);
        });
}
