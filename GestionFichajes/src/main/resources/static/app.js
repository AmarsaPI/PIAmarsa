const weekdays = ["L", "M", "X", "J", "V", "S", "D"];
const monthNames = [
  "ENERO", "FEBRERO", "MARZO", "ABRIL", "MAYO", "JUNIO",
  "JULIO", "AGOSTO", "SEPTIEMBRE", "OCTUBRE", "NOVIEMBRE", "DICIEMBRE"
];

const weekdaysEl = document.getElementById("weekdays");
const daysGrid = document.getElementById("daysGrid");
const monthLabel = document.getElementById("monthLabel");
const yearText = document.getElementById("yearText");
const dateText = document.getElementById("dateText");
const prevMonth = document.getElementById("prevMonth");
const nextMonth = document.getElementById("nextMonth");

let currentDate = new Date();
const realToday = new Date();

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
  dateText.textContent = realToday.toLocaleDateString("es-ES", {
    weekday: "short",
    month: "short",
    day: "2-digit"
  }).replace('.', '');

  let firstDayOfMonth = new Date(year, month, 1).getDay();
  // Ajuste para que Lunes sea 0 y Domingo sea 6
  firstDayOfMonth = firstDayOfMonth === 0 ? 6 : firstDayOfMonth - 1;
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
	if (dayNum === realToday.getDate() && 
        month === realToday.getMonth() && 
        year === realToday.getFullYear()) {
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

if (prevMonth) {
    prevMonth.addEventListener("click", () => {
        currentDate = new Date(currentDate.getFullYear(), currentDate.getMonth() - 1, 1);
        renderCalendar(currentDate);
    });
}

if (nextMonth) {
    nextMonth.addEventListener("click", () => {
        currentDate = new Date(currentDate.getFullYear(), currentDate.getMonth() + 1, 1);
        renderCalendar(currentDate);
    });
}

renderWeekdays();
renderCalendar(currentDate);
