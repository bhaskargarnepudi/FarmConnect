document.addEventListener("DOMContentLoaded", () => {
    const searchInput = document.getElementById("searchInput");
    const sortSelect = document.getElementById("sortSelect");
    const categoryButtons = [...document.querySelectorAll(".category-tab")];
    const productGrid = document.getElementById("productGrid");
    const emptyState = document.getElementById("emptyState");
    const resultCount = document.getElementById("resultCount");
    const toast = document.getElementById("toast");
    const modal = document.getElementById("productModal");
    const modalClose = document.getElementById("modalClose");
    let activeCategory = "All";
    let lastFocusedElement = null;
    let toastTimeout;

    const cards = [...productGrid.querySelectorAll(".product-card")];

    function showToast(message) {
        toast.textContent = message;
        toast.classList.add("show");
        clearTimeout(toastTimeout);
        toastTimeout = setTimeout(() => toast.classList.remove("show"), 2600);
    }

    function applyFilters() {
        const query = searchInput.value.trim().toLowerCase();
        const visibleCards = [];

        cards.forEach(card => {
            const name = (card.dataset.name || "").toLowerCase();
            const category = card.dataset.category || "";
            const farmer = (card.dataset.farmer || "").toLowerCase();
            const location = (card.dataset.location || "").toLowerCase();
            const description = (card.dataset.description || "").toLowerCase();
            const matchesQuery = [name, category.toLowerCase(), farmer, location, description]
                .some(value => value.includes(query));
            const matchesCategory = activeCategory === "All" || category === activeCategory;
            const visible = matchesQuery && matchesCategory;
            card.hidden = !visible;
            if (visible) visibleCards.push(card);
        });

        const sort = sortSelect.value;
        visibleCards.sort((a, b) => {
            if (sort === "low") return Number(a.dataset.price) - Number(b.dataset.price);
            if (sort === "high") return Number(b.dataset.price) - Number(a.dataset.price);
            if (sort === "name") return a.dataset.name.localeCompare(b.dataset.name);
            return Number(a.dataset.id) - Number(b.dataset.id);
        });
        visibleCards.forEach(card => productGrid.appendChild(card));

        resultCount.textContent = `${visibleCards.length} product${visibleCards.length === 1 ? "" : "s"} found`;
        emptyState.hidden = visibleCards.length > 0;
        productGrid.hidden = visibleCards.length === 0;
    }

    categoryButtons.forEach(button => {
        button.addEventListener("click", () => {
            activeCategory = button.dataset.category;
            categoryButtons.forEach(item => {
                const selected = item === button;
                item.classList.toggle("active", selected);
                item.setAttribute("aria-pressed", String(selected));
            });
            applyFilters();
        });
        button.setAttribute("aria-pressed", String(button.classList.contains("active")));
    });
    searchInput.addEventListener("input", applyFilters);
    sortSelect.addEventListener("change", applyFilters);

    document.getElementById("clearFilters").addEventListener("click", () => {
        searchInput.value = "";
        activeCategory = "All";
        sortSelect.value = "featured";
        categoryButtons.forEach(button => {
            const selected = button.dataset.category === "All";
            button.classList.toggle("active", selected);
            button.setAttribute("aria-pressed", String(selected));
        });
        applyFilters();
    });

    document.querySelectorAll(".save-button").forEach(button => {
        button.addEventListener("click", () => {
            const saved = button.classList.toggle("saved");
            button.textContent = saved ? "♥" : "♡";
            button.setAttribute("aria-pressed", String(saved));
            showToast(saved ? "Saved for this browsing session." : "Removed from saved items.");
        });
    });

    function openProduct(card) {
        lastFocusedElement = document.activeElement;
        const modalImage = document.getElementById("modalImage");
        modalImage.onerror = () => {
            modalImage.onerror = null;
            modalImage.src = "https://images.unsplash.com/photo-1490645935967-10de6ba17061?auto=format&fit=crop&w=900&q=80";
        };
        modalImage.src = card.dataset.image || "https://images.unsplash.com/photo-1490645935967-10de6ba17061?auto=format&fit=crop&w=900&q=80";
        modalImage.alt = card.dataset.name || "Product image";
        document.getElementById("modalCategory").textContent = card.dataset.category;
        document.getElementById("modalTitle").textContent = card.dataset.name;
        document.getElementById("modalQuality").textContent = card.dataset.quality;
        document.getElementById("modalDescription").textContent = card.dataset.description;
        document.getElementById("modalPrice").textContent = `₹${Number(card.dataset.price).toLocaleString("en-IN", {minimumFractionDigits: 2, maximumFractionDigits: 2})} / ${card.dataset.unit}`;
        document.getElementById("modalFarmer").textContent = card.dataset.farmer;
        document.getElementById("modalLocation").textContent = card.dataset.location;
        document.getElementById("modalQuantity").textContent = `${card.dataset.quantity} ${card.dataset.unit}`;
        const subject = encodeURIComponent(`FarmConnect enquiry: ${card.dataset.name}`);
        const body = encodeURIComponent(`Hello ${card.dataset.farmer},\n\nI am interested in ${card.dataset.name} listed on FarmConnect. Please confirm the current price, quality, available quantity and delivery/pickup options.\n\nThank you.`);
        const farmerEmail = (card.dataset.farmerEmail || "").trim();
        const contactLink = document.getElementById("contactFarmer");
        const modalHint = document.getElementById("modalHint");
        contactLink.href = farmerEmail
            ? `mailto:${farmerEmail}?subject=${subject}&body=${body}`
            : "#contact-farmer";
        contactLink.dataset.hasEmail = farmerEmail ? "true" : "false";
        const contactFallback = document.getElementById("contactFallback");
        const farmerEmailText = document.getElementById("farmerEmailText");
        contactFallback.hidden = !farmerEmail;
        farmerEmailText.textContent = farmerEmail || "";
        farmerEmailText.href = farmerEmail ? `mailto:${farmerEmail}?subject=${subject}&body=${body}` : "#";
        modalHint.textContent = farmerEmail
            ? "Contact the farmer by email. If no email app opens, use the address below. Orders require a buyer account."
            : "No contact email is saved for this listing. Please choose a listing uploaded by a registered farmer or ask them to update their listing.";
        document.getElementById("adminPurchase").href = `/buyer/orders/new?productId=${encodeURIComponent(card.dataset.id)}`;
        modal.hidden = false;
        document.body.style.overflow = "hidden";
        modalClose.focus();
    }

    document.getElementById("contactFarmer").addEventListener("click", event => {
        if (event.currentTarget.dataset.hasEmail !== "true") {
            event.preventDefault();
            showToast("Farmer contact email is not available for this listing.");
        } else {
            showToast(`Farmer email: ${document.getElementById("farmerEmailText").textContent}`);
        }
    });

    document.getElementById("copyFarmerEmail").addEventListener("click", async () => {
        const email = document.getElementById("farmerEmailText").textContent.trim();
        if (!email) return;
        try {
            await navigator.clipboard.writeText(email);
            showToast("Farmer email copied.");
        } catch (error) {
            const temporaryInput = document.createElement("textarea");
            temporaryInput.value = email;
            temporaryInput.setAttribute("readonly", "");
            temporaryInput.style.position = "absolute";
            temporaryInput.style.left = "-9999px";
            document.body.appendChild(temporaryInput);
            temporaryInput.select();
            const copied = document.execCommand("copy");
            temporaryInput.remove();
            showToast(copied ? "Farmer email copied." : `Farmer email: ${email}`);
        }
    });

    document.querySelectorAll(".btn-card").forEach(button => {
        button.addEventListener("click", () => {
            const card = button.closest(".product-card");
            if (card) openProduct(card);
        });
    });

    function closeModal() {
        modal.hidden = true;
        document.body.style.overflow = "";
        if (lastFocusedElement) lastFocusedElement.focus();
    }
    modalClose.addEventListener("click", closeModal);
    modal.addEventListener("click", event => {
        if (event.target === modal) closeModal();
    });
    document.addEventListener("keydown", event => {
        if (event.key === "Escape" && !modal.hidden) closeModal();
    });

        const menuToggle = document.getElementById("menuToggle");
    const nav = document.querySelector(".main-nav");
    menuToggle.addEventListener("click", () => {
        const open = nav.classList.toggle("open");
        menuToggle.setAttribute("aria-expanded", String(open));
        menuToggle.textContent = open ? "×" : "☰";
    });
    nav.querySelectorAll("a").forEach(link => link.addEventListener("click", () => {
        nav.classList.remove("open");
        menuToggle.setAttribute("aria-expanded", "false");
        menuToggle.textContent = "☰";
    }));

    document.getElementById("year").textContent = new Date().getFullYear();
    applyFilters();
});
