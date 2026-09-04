const API_BASE = 'http://localhost:8080/api/v1';

const state = { view: 'appointments', patients: [], professionals: [], procedures: [], appointments: [] };

const $ = (sel) => document.querySelector(sel);
const alertBox = $('#alert');

function showAlert(message, type = 'error') {
  alertBox.textContent = message;
  alertBox.className = `alert ${type}`;
  setTimeout(() => alertBox.classList.add('hidden'), 4000);
}

async function apiRequest(path, { method = 'GET', body } = {}) {
  const response = await fetch(`${API_BASE}${path}`, {
    method,
    headers: { 'Content-Type': 'application/json' },
    body: body ? JSON.stringify(body) : undefined,
  });
  if (!response.ok) {
    const errorText = await response.text().catch(() => '');
    throw new Error(errorText || `Erro ${response.status} ao acessar ${path}`);
  }
  return response.status === 204 ? null : response.json();
}

const STATUS_LABELS = {
  SCHEDULED: ['Agendado', 'badge-scheduled'],
  CONFIRMED: ['Confirmado', 'badge-confirmed'],
  CANCELED: ['Cancelado', 'badge-canceled'],
  COMPLETED: ['Concluído', 'badge-completed'],
};

function formatDateTime(iso) {
  return new Date(iso).toLocaleString('pt-BR', { dateStyle: 'short', timeStyle: 'short' });
}

// ---------- Renderização ----------

function renderAppointments() {
  $('#appointments-body').innerHTML = state.appointments.map((a) => {
    const [label, cls] = STATUS_LABELS[a.status] || ['—', ''];
    const canAct = a.status === 'SCHEDULED';
    return `
      <tr>
        <td>${formatDateTime(a.appointmentDateTime)}</td>
        <td>${a.patientName}</td>
        <td>${a.professionalName}</td>
        <td>${a.procedureName}</td>
        <td><span class="badge ${cls}">${label}</span></td>
        <td>
          ${canAct ? `<button class="action-btn confirm" data-action="confirm" data-id="${a.id}">Confirmar</button>
          <button class="action-btn cancel" data-action="cancel" data-id="${a.id}">Cancelar</button>` : ''}
        </td>
      </tr>`;
  }).join('') || '<tr><td colspan="6">Nenhum agendamento encontrado.</td></tr>';
}

function renderPatients() {
  $('#patients-body').innerHTML = state.patients.map((p) => `
    <tr>
      <td>${p.name}</td><td>${p.phone}</td><td>${p.email ?? '—'}</td>
      <td><button class="action-btn edit" data-entity="patients" data-id="${p.id}">Editar</button></td>
    </tr>`).join('') || '<tr><td colspan="4">Nenhum paciente cadastrado.</td></tr>';
}

function renderProfessionals() {
  $('#professionals-body').innerHTML = state.professionals.map((p) => `
    <tr>
      <td>${p.name}</td><td>${p.specialty ?? '—'}</td>
      <td><button class="action-btn edit" data-entity="professionals" data-id="${p.id}">Editar</button></td>
    </tr>`).join('') || '<tr><td colspan="3">Nenhum profissional cadastrado.</td></tr>';
}

function renderProcedures() {
  $('#procedures-body').innerHTML = state.procedures.map((p) => `
    <tr>
      <td>${p.name}</td><td>${p.durationMinutes}</td><td>R$ ${Number(p.price).toFixed(2)}</td>
      <td>${p.active ? 'Sim' : 'Não'}</td>
      <td><button class="action-btn edit" data-entity="procedures" data-id="${p.id}">Editar</button></td>
    </tr>`).join('') || '<tr><td colspan="5">Nenhum procedimento cadastrado.</td></tr>';
}

// ---------- Carregamento de dados ----------

async function loadAll() {
  try {
    const [patients, professionals, procedures, appointments] = await Promise.all([
      apiRequest('/patients'),
      apiRequest('/professionals'),
      apiRequest('/procedures'),
      apiRequest('/appointments'),
    ]);
    Object.assign(state, { patients, professionals, procedures, appointments });
    
    // Renderiza a lista de todas as entidades
    renderAppointments();
    renderPatients();
    renderProfessionals();
    renderProcedures();

    // Se o usuário já estiver na aba de calendário, atualiza a grade visual
    if (state.view === 'calendar') {
      renderCalendar();
    }
  } catch (err) {
    showAlert(err.message);
  }
}

// ---------- Navegação ----------

function switchView(view) {
  state.view = view;
  document.querySelectorAll('.nav-btn').forEach((b) => b.classList.toggle('active', b.dataset.view === view));
  document.querySelectorAll('.view').forEach((v) => v.classList.toggle('hidden', v.id !== `view-${view}`));
  
  const titles = { 
    appointments: 'Agendamentos', 
    patients: 'Pacientes', 
    professionals: 'Profissionais', 
    procedures: 'Procedimentos',
    calendar: 'Calendário' 
  };
  
  $('#view-title').textContent = titles[view] || 'Painel';

  // Executa o cálculo e renderização do calendário ao alternar para a aba
  if (view === 'calendar') {
    renderCalendar();
  }
}

document.querySelectorAll('.nav-btn').forEach((btn) =>
  btn.addEventListener('click', () => switchView(btn.dataset.view))
);

// ---------- Ações de agendamento ----------

document.addEventListener('click', async (e) => {
  const action = e.target.dataset.action;
  const id = e.target.dataset.id;
  if (!action || !id) return;
  try {
    await apiRequest(`/appointments/${id}/${action}`, { method: 'PATCH' });
    showAlert(`Agendamento ${action === 'confirm' ? 'confirmado' : 'cancelado'} com sucesso.`, 'success');
    await loadAll();
  } catch (err) {
    showAlert(err.message);
  }
});

// ---------- Modal: Novo Agendamento ----------

const FORMS = {
  appointments: () => `
    <label>Paciente</label>
    <select name="patientId" required>${state.patients.map((p) => `<option value="${p.id}">${p.name}</option>`).join('')}</select>
    <label>Profissional</label>
    <select name="professionalId" required>${state.professionals.map((p) => `<option value="${p.id}">${p.name}</option>`).join('')}</select>
    <label>Procedimento</label>
    <select name="procedureId" required>${state.procedures.map((p) => `<option value="${p.id}">${p.name}</option>`).join('')}</select>
    <label>Data e Hora</label>
    <input type="datetime-local" name="appointmentDateTime" step="300" required>
    <label>Observações</label>
    <textarea name="notes" rows="2"></textarea>`,
  patients: () => `
    <label>Nome</label><input name="name" required>
    <label>Telefone (WhatsApp)</label><input name="phone" placeholder="+55..." required>
    <label>Email</label><input name="email" type="email">`,
  professionals: () => `
    <label>Nome</label><input name="name" required>
    <label>Especialidade</label><input name="specialty">`,
  procedures: () => `
    <label>Nome</label><input name="name" required>
    <label>Duração (min)</label><input name="durationMinutes" type="number" step="5" min="5" required>
    <label>Preço</label><input name="price" type="number" step="0.01" required>`,
};

const ENDPOINTS = { appointments: '/appointments', patients: '/patients', professionals: '/professionals', procedures: '/procedures' };

$('#btn-new').addEventListener('click', () => {
  const entity = state.view === 'calendar' ? 'appointments' : state.view;
  openModal(entity);
});

function openModal(entity) {
  $('#modal-title').textContent = `Novo registro — ${entity}`;
  $('#modal-form').innerHTML = FORMS[entity]() + `
    <div class="modal-actions">
      <button type="button" class="btn-secondary" id="modal-cancel">Cancelar</button>
      <button type="submit" class="btn-primary">Salvar</button>
    </div>`;
  $('#modal').classList.remove('hidden');
  $('#modal-cancel').onclick = closeModal;
  $('#modal-form').onsubmit = (e) => submitForm(e, entity);
}

function closeModal() {
  $('#modal').classList.add('hidden');
}

async function submitForm(e, entity) {
  e.preventDefault();
  const data = Object.fromEntries(new FormData(e.target).entries());

  if (entity === 'appointments') {
    data.patientId = Number(data.patientId);
    data.professionalId = Number(data.professionalId);
    data.procedureId = Number(data.procedureId);
    // Mantém o horário local (LocalDateTime no backend não tem timezone)
    data.appointmentDateTime = `${data.appointmentDateTime}:00`;
    if (!data.notes) delete data.notes;
  }

  if (entity === 'procedures') {
    data.durationMinutes = Number(data.durationMinutes);
    data.price = Number(data.price);
  }

  try {
    await apiRequest(ENDPOINTS[entity], { method: 'POST', body: data });
    showAlert('Registro criado com sucesso.', 'success');
    closeModal();
    await loadAll();
  } catch (err) {
    showAlert(err.message);
  }
}

loadAll();
// ---------- Calendário (Dia / Semana) ----------

const CAL_START_HOUR = 7;
const CAL_END_HOUR = 20;
const HOUR_HEIGHT = 60; // px

const calendar = { mode: 'day', refDate: new Date() };

const isSameDay = (a, b) =>
  a.getFullYear() === b.getFullYear() && a.getMonth() === b.getMonth() && a.getDate() === b.getDate();

function startOfWeek(date) {
  const d = new Date(date);
  const diff = (d.getDay() + 6) % 7; // segunda-feira como início
  d.setDate(d.getDate() - diff);
  d.setHours(0, 0, 0, 0);
  return d;
}

function minutesFromMidnight(date) {
  return date.getHours() * 60 + date.getMinutes();
}

function appointmentDurationMinutes(appt) {
  if (!appt.endDateTime) return 30;
  return (new Date(appt.endDateTime) - new Date(appt.appointmentDateTime)) / 60000;
}

function buildDayColumn(date) {
  const dayLabel = date.toLocaleDateString('pt-BR', { weekday: 'short', day: '2-digit', month: '2-digit' });
  const todayClass = isSameDay(date, new Date()) ? 'is-today' : '';

  const hourLines = Array.from(
    { length: CAL_END_HOUR - CAL_START_HOUR },
    () => `<div class="calendar-hour-line"></div>`
  ).join('');

  const dayAppointments = state.appointments.filter((a) => isSameDay(new Date(a.appointmentDateTime), date));

  const events = dayAppointments.map((a) => {
    const start = new Date(a.appointmentDateTime);
    const offsetMin = minutesFromMidnight(start) - CAL_START_HOUR * 60;
    const top = (offsetMin / 60) * HOUR_HEIGHT;
    const height = Math.max((appointmentDurationMinutes(a) / 60) * HOUR_HEIGHT, 22);
    const timeLabel = start.toLocaleTimeString('pt-BR', { hour: '2-digit', minute: '2-digit' });
    return `
      <div class="calendar-event status-${a.status}" style="top:${top}px;height:${height}px"
           data-appt-id="${a.id}" title="${a.patientName} — ${a.procedureName}">
        ${timeLabel} · ${a.patientName}
        <small>${a.procedureName}</small>
      </div>`;
  }).join('');

  return `
    <div class="calendar-day-col ${todayClass}">
      <div class="calendar-day-header">${dayLabel}</div>
      <div class="calendar-day-body" style="height:${(CAL_END_HOUR - CAL_START_HOUR) * HOUR_HEIGHT}px">
        ${hourLines}
        ${events}
      </div>
    </div>`;
}

function renderCalendarLabel(days) {
  const fmt = (d) => d.toLocaleDateString('pt-BR', { day: '2-digit', month: 'short' });
  $('#cal-label').textContent =
    calendar.mode === 'day'
      ? calendar.refDate.toLocaleDateString('pt-BR', { weekday: 'long', day: '2-digit', month: 'long', year: 'numeric' })
      : `${fmt(days[0])} – ${fmt(days[days.length - 1])} de ${days[0].getFullYear()}`;
}

function renderCalendar() {
  const timeLabels = Array.from(
    { length: CAL_END_HOUR - CAL_START_HOUR },
    (_, i) => `<div class="calendar-hour-label">${String(CAL_START_HOUR + i).padStart(2, '0')}:00</div>`
  ).join('');

  const days = calendar.mode === 'day'
    ? [calendar.refDate]
    : Array.from({ length: 7 }, (_, i) => {
        const d = new Date(startOfWeek(calendar.refDate));
        d.setDate(d.getDate() + i);
        return d;
      });

  $('#calendar-grid').innerHTML = `
    <div class="calendar-time-col">
      <div class="calendar-time-header"></div>
      ${timeLabels}
    </div>
    <div class="calendar-days">
      ${days.map(buildDayColumn).join('')}
    </div>`;

  renderCalendarLabel(days);
}

function navigateCalendar(step) {
  const amount = calendar.mode === 'day' ? step : step * 7;
  calendar.refDate.setDate(calendar.refDate.getDate() + amount);
  renderCalendar();
}

$('#cal-prev').addEventListener('click', () => navigateCalendar(-1));
$('#cal-next').addEventListener('click', () => navigateCalendar(1));
$('#cal-today').addEventListener('click', () => { calendar.refDate = new Date(); renderCalendar(); });

document.querySelectorAll('.mode-btn').forEach((btn) =>
  btn.addEventListener('click', () => {
    calendar.mode = btn.dataset.mode;
    document.querySelectorAll('.mode-btn').forEach((b) => b.classList.toggle('active', b === btn));
    renderCalendar();
  })
);

// Popover ao clicar em um evento do calendário
document.addEventListener('click', (e) => {
  document.querySelectorAll('.calendar-popover').forEach((p) => p.remove());
  const eventEl = e.target.closest('.calendar-event');
  if (!eventEl) return;

  const appt = state.appointments.find((a) => String(a.id) === eventEl.dataset.apptId);
  if (!appt) return;

  const [label] = STATUS_LABELS[appt.status] || ['—'];
  const canAct = appt.status === 'SCHEDULED';

  const popover = document.createElement('div');
  popover.className = 'calendar-popover';
  popover.innerHTML = `
    <p><strong>${appt.patientName}</strong></p>
    <p>${appt.procedureName}</p>
    <p>${appt.professionalName}</p>
    <p>${formatDateTime(appt.appointmentDateTime)} · ${label}</p>
    ${canAct ? `
      <div class="modal-actions">
        <button class="action-btn confirm" data-action="confirm" data-id="${appt.id}">Confirmar</button>
        <button class="action-btn cancel" data-action="cancel" data-id="${appt.id}">Cancelar</button>
      </div>` : ''}`;

  const rect = eventEl.getBoundingClientRect();
  popover.style.top = `${window.scrollY + rect.bottom + 4}px`;
  popover.style.left = `${window.scrollX + rect.left}px`;
  document.body.appendChild(popover);
  e.stopPropagation();
});
