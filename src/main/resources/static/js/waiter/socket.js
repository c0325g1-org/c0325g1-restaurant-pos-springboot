function updateOrderItemStatus(data) {
    if (!data || !data.orderItem) return;

    const orderItem = data.orderItem;
    const itemId = orderItem.id;
    const status = orderItem.status;

    const itemEl = document.querySelector(`#called-items [data-id="${itemId}"]`);
    if (!itemEl) return;

    const badgeContainer = itemEl.querySelector(".badge")?.parentElement;
    if (badgeContainer) {
        badgeContainer.innerHTML = getStatusBadge(status);
    }

    const actionContainer = itemEl.querySelector(".col-2");
    if (actionContainer) {
        actionContainer.innerHTML = getActionButtons(orderItem);
    }
}

export function listenChangeOrderItemStatus() {
    const socket = new SockJS('/ws');
    const stompClient = Stomp.over(socket);
    stompClient.connect({}, function () {
        stompClient.subscribe(`/topic/user.${empId}`, function (message) {
            let data = JSON.parse(message.body);
            updateOrderItemStatus(data);
        });
    });
}