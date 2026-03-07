const weekdays = ["S", "M", "T", "W", "T", "F", "S"];
const monthNames = [
  "JANUARY", "FEBRUARY", "MARCH", "APRIL", "MAY", "JUNE",
  "JULY", "AUGUST", "SEPTEMBER", "OCTOBER", "NOVEMBER", "DECEMBER"
];

const menuToggle = document.getElementById("menuToggle");
const mainMenu = document.getElementById("mainMenu");
const subMenu = document.getElementById("subMenu");
const weekdaysEl = document.getElementById("weekdays");
const daysGrid = document.getElementById("daysGrid");
const monthLabel = document.getElementById("monthLabel");
const yearText = document.getElementById("yearText");
const dateText = document.getElementById("dateText");
const prevMonth = document.getElementById("prevMonth");
const nextMonth = document.getElementById("nextMonth");

let currentDate = new Date(2026, 2, 5);

function renderWeekdays() {
  weekdaysEl.innerHTML = "";
  weekdays.forEach((day) => {
    const el = document.createElement("div");
    el.textContent = day;
    weekdaysEl.appendChild(el);
  });
}

function renderCalendar(date) {
  const year = date.getFullYear();
  const month = date.getMonth();

  monthLabel.textContent = `${monthNames[month]} ${year}`;
  yearText.textContent = String(year);
  dateText.textContent = date.toLocaleDateString("en-US", {
    weekday: "short",
    month: "short",
    day: "2-digit"
  });

  const firstDayOfMonth = new Date(year, month, 1).getDay();
  const daysInMonth = new Date(year, month + 1, 0).getDate();
  const prevMonthDays = new Date(year, month, 0).getDate();

  daysGrid.innerHTML = "";

  for (let i = firstDayOfMonth - 1; i >= 0; i--) {
    const day = document.createElement("div");
    day.className = "muted";
    day.textContent = String(prevMonthDays - i);
    daysGrid.appendChild(day);
  }

  for (let dayNum = 1; dayNum <= daysInMonth; dayNum++) {
    const day = document.createElement("div");
    day.textContent = String(dayNum);
    if (dayNum === date.getDate()) {
      day.classList.add("today");
    }
    daysGrid.appendChild(day);
  }

  const totalCells = firstDayOfMonth + daysInMonth;
  const trailing = (7 - (totalCells % 7)) % 7;
  for (let i = 1; i <= trailing; i++) {
    const day = document.createElement("div");
    day.className = "muted";
    day.textContent = String(i);
    daysGrid.appendChild(day);
  }
}

menuToggle.addEventListener("click", () => {
  mainMenu.hidden = !mainMenu.hidden;
  subMenu.hidden = true;
});

mainMenu.addEventListener("click", (event) => {
  const item = event.target.closest("li");
  if (!item) return;
  subMenu.hidden = item.dataset.submenu !== "schedule";
});

prevMonth.addEventListener("click", () => {
  currentDate = new Date(currentDate.getFullYear(), currentDate.getMonth() - 1, 1);
  renderCalendar(currentDate);
});

nextMonth.addEventListener("click", () => {
  currentDate = new Date(currentDate.getFullYear(), currentDate.getMonth() + 1, 1);
  renderCalendar(currentDate);
});

renderWeekdays();
renderCalendar(currentDate);
