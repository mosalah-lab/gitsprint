// SAMPLE feature — the front-end half of the Currencies slice.
// Shows both halves of the pattern: READ (fetch the list) and WRITE (POST a new row).
// Copy this file as the template for your feature pages (rates.js, convert.js, ...).

// --- READ: GET /api/currencies and render the table ---
let allCurrencies = [];

async function loadCurrencies() {
  const rows = document.getElementById('rows');
  const status = document.getElementById('status');
  try {
    const res = await fetch('/api/currencies');
    if (!res.ok) throw new Error('HTTP ' + res.status);
    allCurrencies = await res.json();
    status.textContent = `${allCurrencies.length} currencies loaded from the database.`;
    status.classList.remove('err');
    renderCurrencies(allCurrencies);
  } catch (err) {
    rows.innerHTML = '<tr><td colspan="3" class="status err">Could not load currencies.</td></tr>';
    status.textContent = 'Is the app running and the database seeded? Try /api/health/db. (' + err.message + ')';
    status.classList.add('err');
  }
}

// --- Client-side render: given a (possibly filtered) list, draw the rows ---
function renderCurrencies(currencies) {
  const rows = document.getElementById('rows');
  if (currencies.length === 0) {
    rows.innerHTML = '<tr><td colspan="3" class="status">No currencies match your filter.</td></tr>';
    return;
  }
  rows.innerHTML = currencies.map(c => `
    <tr>
      <td class="mono">${c.code}</td>
      <td>${c.name}</td>
      <td class="sym">${c.symbol ?? ''}</td>
    </tr>`).join('');
}

// --- Client-side filter: no request, filters the already-loaded list ---
function filterCurrencies() {
  const q = document.getElementById('filter').value.trim().toLowerCase();
  if (!q) {
    renderCurrencies(allCurrencies);
    return;
  }
  const filtered = allCurrencies.filter(c =>
    c.code.toLowerCase().includes(q) ||
    c.name.toLowerCase().includes(q) ||
    (c.symbol ?? '').toLowerCase().includes(q));
  renderCurrencies(filtered);
}


// --- WRITE: POST /api/currencies with a JSON body, then re-read the list ---
async function addCurrency(event) {
  event.preventDefault();                       // don't reload the page
  const form = event.target;
  const formStatus = document.getElementById('form-status');
  const body = {
    code: form.code.value.trim().toUpperCase(),
    name: form.name.value.trim(),
    symbol: form.symbol.value.trim()
  };
  try {
    const res = await fetch('/api/currencies', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(body)
    });
    if (!res.ok) {
      const err = await res.json().catch(() => ({}));
      throw new Error(err.error || ('HTTP ' + res.status));   // show the API's 400 message
    }
    formStatus.textContent = `Added ${body.code}.`;
    formStatus.classList.remove('err');
    form.reset();
    loadCurrencies();                            // the write is only "done" once the read shows it
  } catch (err) {
    formStatus.textContent = 'Could not add: ' + err.message;
    formStatus.classList.add('err');
  }
}

loadCurrencies();
document.getElementById('add-form').addEventListener('submit', addCurrency);
document.getElementById('filter').addEventListener('input', filterCurrencies);
