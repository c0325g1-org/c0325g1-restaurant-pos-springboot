export function initCategoryFilters() {
    document.querySelectorAll(".category-filter button").forEach(btn => {
        btn.addEventListener("click", () => {
            const category = btn.dataset.category;
            filterByCategory(category);
            document.querySelectorAll(".category-filter button").forEach(b => b.classList.remove("active"));
            btn.classList.add("active");
        });
    });

    document.querySelector("input[placeholder='Tìm món...']")
        ?.addEventListener("input", (e) => filterMenu(e.target.value));
}

function filterMenu(query) {
    const q = query.toLowerCase();
    document.querySelectorAll("#menuContainer .menu-item").forEach(item => {
        const name = item.querySelector("h5").innerText.toLowerCase();
        item.closest(".col-md-4").style.display = name.includes(q) ? 'block' : 'none';
    });
}

function filterByCategory(category) {
    document.querySelectorAll("#menuContainer [data-category]").forEach(item => {
        const cat = item.getAttribute("data-category");
        item.style.display = (!category || cat === category) ? 'block' : 'none';
    });
}
