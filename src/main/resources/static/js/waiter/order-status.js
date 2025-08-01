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
            if (data.length > 0) {
                console.log(data)
                data.forEach(item => {
                    const div = document.createElement("div");
                    div.classList.add("border-bottom", "py-2");

                    let statusBadge = getStatusBadge(item.status);
                    let actions = getActionButtons(item);

                    div.innerHTML = `
                        <div class="d-flex justify-content-between align-items-center">
                            <div class="col-10 gap-3 d-flex">
                                <div class="d-flex flex-column">
                                    <strong>${item.menuItem.name}</strong>
                                    <i>SL: ${item.quantity} × ${item.price.toLocaleString()}đ</i>
                                </div>
                                <div>
                                    ${statusBadge}
                                </div>
                            </div>
                            <div class="col-2">${actions}</div>
                    </div>`;
                    container.appendChild(div);
                });
            } else {
                container.innerHTML = "<p>Danh sách món đã gọi đang trống!</p>";
            }
        });
}

export function loadServedItems(tableId) {
    fetch(`/waiter/order/served-items?tableId=${tableId}`)
        .then(res => res.json())
        .then(data => {
            const container = document.getElementById("served-items");
            container.innerHTML = "";
            if (data.length > 0) {
                data.forEach(item => {
                    const div = document.createElement("div");
                    div.classList.add("d-flex", "justify-content-between", "border-bottom", "py-1");
                    div.innerHTML = `<span>${item.menuItem.name}</span><span>${item.quantity} × ${item.price.toLocaleString()}đ</span>`;
                    container.appendChild(div);
                });
            } else {
                container.innerHTML = "<p>Danh sách món đã phục vụ đang trống!</p>";
            }

        });
}

function getStatusBadge(status) {
    switch (status) {
        case "NEW": return '<span class="badge bg-primary">Mới gọi</span>';
        case "COOKING": return '<span class="badge bg-warning">Đang chế biến</span>';
        case "READY": return '<span class="badge bg-success">Sẵn sàng</span>';
        case "SERVED": return '<span class="badge bg-info">Đã phục vụ</span>';
        case "CANCELED": return '<span class="badge bg-secondary">Đã huỷ</span>';
        default: return '';
    }
}

function getActionButtons(item) {
    if (item.status === "NEW" || item.status === "COOKING") {
        return `<button class="btn btn-sm btn-outline-danger ms-2" onclick="window.confirmCancel(${item.id})" title="Huỷ món"><i class="bi bi-x"></i> Huỷ món</button>`;
    } else if (item.status === "READY") {
        return `<button class="btn btn-sm btn-success ms-2" onclick="markAsServed(${item.id})" title="Đã phục vụ"><i class="bi bi-bag-check"></i> Đã phục vụ</button>`;
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

window.markAsServed = markAsServed;
