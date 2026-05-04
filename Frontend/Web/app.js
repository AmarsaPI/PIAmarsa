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

document.getElementById('horarioPersonal').addEventListener('click', function (e) {
  e.preventDefault();
  loadView('horario_personal');
  document.getElementById('mainMenu').hidden = true;
  document.getElementById('subMenu').hidden = true;
});

function loadView(view) {
  const content = document.getElementById('mainContent');

  if (view === 'horario_personal') {
    //placeholder
    const diasHorario = [
      { dia: 'Lunes, 02', entrada: '08:00', salida: '17:00', descanso: '1:00h', total: '8:00h', estado: 'Completado' },
      { dia: 'Martes, 03', entrada: '08:00', salida: '17:00', descanso: '1:00h', total: '8:00h', estado: 'Completado' },
      { dia: 'Miércoles, 04', entrada: '08:00', salida: '17:00', descanso: '1:00h', total: '8:00h', estado: 'Pendiente' },
      { dia: 'Jueves, 05', entrada: '08:00', salida: '17:00', descanso: '1:00h', total: '8:00h', estado: 'Pendiente' },
      { dia: 'Viernes, 06', entrada: '08:00', salida: '15:00', descanso: '0:00h', total: '7:00h', estado: 'Pendiente' },
    ];


    const filas = diasHorario.map(d => `
      <tr>
        <td><strong>${d.dia}</strong></td>
        <td>${d.entrada}</td>
        <td>${d.salida}</td>
        <td>${d.descanso}</td>
        <td>${d.total}</td>
        <td>${d.estado}</td>
      </tr>
    `).join('');


    content.innerHTML = `
      <aside class="schedule-panel">
        <div class="schedule-head">
          <h2>Mi Horario Semanal</h2>
          <span class="date-range">Semana del 02 al 08 de Mayo, 2024</span>
        </div>
        <div class="schedule-body">
          <table class="schedule-table">
            <thead>
              <tr>
                <th>Día</th><th>Entrada</th><th>Salida</th>
                <th>Descanso</th><th>Total Hrs</th><th>Estado</th>
              </tr>
            </thead>
            <tbody>${filas}</tbody>
          </table>
        </div>
        <div class="schedule-actions">
          <button class="btn-secondary">Exportar PDF</button>
          <button class="btn-primary">Solicitar Ausencia</button>
        </div>
      </aside>
    `;
  }
}

renderWeekdays();
renderCalendar(currentDate);