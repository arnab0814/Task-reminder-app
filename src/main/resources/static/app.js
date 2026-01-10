

document.addEventListener("DOMContentLoaded", () => {
    console.log("✅ DOM ready");

    const isTaskListPage =
        document.body &&
        document.body.dataset.page === "task-list";

    const cardBtn = document.getElementById("cardToggleBtn");
    const calendarBtn = document.getElementById("calendarBtn");

    if (!isTaskListPage) {
        if (cardBtn) cardBtn.style.display = "none";
        if (calendarBtn) calendarBtn.style.display = "none";

        console.log("ℹ Header buttons hidden (not task list page)");
        return;
    }

    console.log("✅ Task list page detected");
});

console.log("✅ app.js loaded");

document.addEventListener("DOMContentLoaded", () => {
    console.log("✅ DOM ready");

    initClock();
});



function initClock() {
    const clock = document.getElementById("liveClock");
    if (!clock) return;

    function update() {
        clock.innerText = new Date().toLocaleTimeString();
    }

    update();
    setInterval(update, 1000);
}




function toggleView() {
    const cardView = document.getElementById("cardView");
    const table = document.querySelector("table");

    if (!cardView || !table) {
        console.info("toggleView ignored: not a list page");
        return;
    }

    const isCardVisible = cardView.style.display === "grid";

    cardView.style.display = isCardVisible ? "none" : "grid";
    table.style.display = isCardVisible ? "table" : "none";
}




function openCalendar() {
    const modal = document.getElementById("calendarModal");
    if (!modal) {
        console.info("Calendar not available on this page");
        return;
    }
    modal.style.display = "block";
}


