
document.addEventListener("DOMContentLoaded", () => {
  const weekdays = ["S", "M", "T", "W", "T", "F", "S"];
  const monthNames = [
    "JANUARY", "FEBRUARY", "MARCH", "APRIL", "MAY", "JUNE",
    "JULY", "AUGUST", "SEPTEMBER", "OCTOBER", "NOVEMBER", "DECEMBER"
  ];

  const fichajes = {
    1: { entrada: "08:00", salida: "17:00" },
  2: { entrada: "08:00", salida: "17:00" },
  3: { entrada: "08:00", salida: "17:00" },
  4: { entrada: "08:00", salida: "17:00" },
  5: { entrada: "08:00", salida: "15:00" }
  };

  const weekdaysEl = document.getElementById("weekdays");
  const daysGrid = document.getElementById("daysGrid");
  const monthLabel = document.getElementById("monthLabel");
  const yearText = document.getElementById("yearText");
  const dateText = document.getElementById("dateText");
  const prevMonth = document.getElementById("prevMonth");
  const nextMonth = document.getElementById("nextMonth");

  //CAMBIO DE VISTA MENSUAL
  const toggleBtn = document.getElementById("toggleViewBtn");
  const weeklyView = document.getElementById("weeklyView");
  const monthlyView = document.getElementById("monthlyView");
  const title = document.getElementById("scheduleTitle");

  let isMonthly = false;

  toggleBtn.addEventListener("click", () => {

    isMonthly = !isMonthly;

    if(isMonthly){
      weeklyView.style.display = "none";
      monthlyView.style.display = "block";

      toggleBtn.textContent = "Ver horario semanal";
      title.textContent = "Mi Horario Mensual";

      renderCalendar(currentDate);
    } else {
      weeklyView.style.display = "block";
      monthlyView.style.display = "none";

      toggleBtn.textContent = "Ver horario mensual";
      title.textContent = "Mi Horario Semanal";
    }

  });

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
    //yearText.textContent = String(year);
    /*dateText.textContent = date.toLocaleDateString("en-US", {
      weekday: "short",
      month: "short",
      day: "2-digit"
    });*/

    const firstDayOfMonth = new Date(year, month, 1).getDay();
    const daysInMonth = new Date(year, month + 1, 0).getDate();
    const prevMonthDays = new Date(year, month, 0).getDate();

    daysGrid.innerHTML = "";

    for (let i = firstDayOfMonth - 1; i >= 0; i--) {
      const day = document.createElement("div");
      day.className = "calendar-day muted";
      day.textContent = String(prevMonthDays - i);
      daysGrid.appendChild(day);
    }

    for (let dayNum = 1; dayNum <= daysInMonth; dayNum++) {
      const day = document.createElement("div");
      day.classList.add("calendar-day");

      const dayNumber = document.createElement("div");
      dayNumber.textContent = dayNum;
      dayNumber.style.fontWeight = "bold";
      dayNumber.classList.add("day-number");

      day.appendChild(dayNumber);

      const fechaEspecifica = new Date(year, month, dayNum);
      const numeroDiaSemana = fechaEspecifica.getDay();

      const horarioHoy = fichajes[numeroDiaSemana];

      if(horarioHoy){
        const info = document.createElement("div");
        info.textContent = `${horarioHoy.entrada} - ${horarioHoy.salida}`;
        info.classList.add("day-info");
        day.appendChild(info);
      }
  
      const hoyReal = new Date();
      if (dayNum === hoyReal.getDate() && month === hoyReal.getMonth() && year === hoyReal.getFullYear()) {
        day.classList.add("today");
      }
      daysGrid.appendChild(day);
    }

    const totalCells = firstDayOfMonth + daysInMonth;
    const trailing = (7 - (totalCells % 7)) % 7;
    for (let i = 1; i <= trailing; i++) {
      const day = document.createElement("div");
      day.className = "calendar-day muted";
      day.textContent = String(i);
      daysGrid.appendChild(day);
    }
  }

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
});