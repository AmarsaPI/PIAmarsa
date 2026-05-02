const employees = {
  ana: {
    name: "Ana Martínez",
    meta: "Recepción · Contrato indefinido · Alta 12/02/2024",
    status: "Activo",
    vacation: "15/07/2026 - 28/07/2026",
    schedule: "L-V · 08:00 - 16:00"
  },
  carlos: {
    name: "Carlos Ruiz",
    meta: "Cocina · Contrato temporal · Alta 03/09/2025",
    status: "Activo",
    vacation: "02/08/2026 - 12/08/2026",
    schedule: "L-S · 10:00 - 16:00 / 20:00 - 23:00"
  },
  laura: {
    name: "Laura Gómez",
    meta: "Sala · Contrato indefinido · Alta 18/05/2023",
    status: "Activo",
    vacation: "Pendiente de asignar",
    schedule: "M-D · 16:00 - 00:00"
  },
  diego: {
    name: "Diego Navarro",
    meta: "Mantenimiento · Contrato parcial · Alta 22/01/2026",
    status: "Activo",
    vacation: "10/09/2026 - 17/09/2026",
    schedule: "Flexible · 09:00 - 14:00"
  }
};

const employeeRows = document.querySelectorAll(".employee-row");
const employeeSearch = document.getElementById("employeeSearch");
const employeeName = document.getElementById("employeeName");
const employeeMeta = document.getElementById("employeeMeta");
const employeeStatus = document.getElementById("employeeStatus");
const currentVacation = document.getElementById("currentVacation");
const currentSchedule = document.getElementById("currentSchedule");
const actionTabs = document.querySelectorAll(".action-tab");
const actionPanels = document.querySelectorAll(".employee-action-panel");

function selectEmployee(employeeId) {
  const employee = employees[employeeId];
  if (!employee) return;

  employeeName.textContent = employee.name;
  employeeMeta.textContent = employee.meta;
  employeeStatus.textContent = employee.status;
  currentVacation.textContent = employee.vacation;
  currentSchedule.textContent = employee.schedule;

  employeeRows.forEach((row) => {
    row.classList.toggle("is-selected", row.dataset.employee === employeeId);
  });
}

function showPanel(panelId) {
  actionTabs.forEach((tab) => {
    tab.classList.toggle("is-active", tab.dataset.panel === panelId);
  });

  actionPanels.forEach((panel) => {
    panel.classList.toggle("is-active", panel.id === panelId);
  });
}

employeeRows.forEach((row) => {
  row.addEventListener("click", () => selectEmployee(row.dataset.employee));
});

actionTabs.forEach((tab) => {
  tab.addEventListener("click", () => showPanel(tab.dataset.panel));
});

employeeSearch.addEventListener("input", () => {
  const query = employeeSearch.value.trim().toLowerCase();

  employeeRows.forEach((row) => {
    row.hidden = !row.textContent.toLowerCase().includes(query);
  });
});

document.querySelectorAll(".employee-action-panel").forEach((form) => {
  form.addEventListener("submit", (event) => {
    event.preventDefault();
    form.classList.add("is-saved");
    window.setTimeout(() => form.classList.remove("is-saved"), 1400);
  });
});