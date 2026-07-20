// Rates feature — the front-end half of the 01-rates-listing / 02-pair-lookup slice.
// Same pattern as currencies.js: READ the list on load, plus a small lookup form.

// --- READ: GET /api/rates and render the table ---
async function loadRates() {
  const rows = document.getElementById('rows');
  const status = document.getElementById('status');
  try {
    const res = await fetch('/api/rates');
    if (!res.ok) throw new Error('HTTP ' + res.status);
    const rates = await res.json();

    if (rates.length === 0) {
      rows.innerHTML = '<tr><td colspan="4" class="status">No rates found.</td></tr>';
      return;
    }

    rows.innerHTML = rates.map(r => `
      <tr>
        <td class="mono">${r.base}</td>
        <td class="mono">${r.quote}</td>
        <td>${r.rate}</td>
        <td>${r.rateDate}</td>
      </tr>`).join('');
    status.textContent = `${rates.length} pairs loaded from the database.`;
    status.classList.remove('err');
  } catch (err) {
    rows.innerHTML = '<tr><td colspan="4" class="status err">Could not load rates.</td></tr>';
    status.textContent = 'Is the app running and the database seeded? (' + err.message + ')';
    status.classList.add('err');
  }
}

// --- Single-pair lookup: GET /api/rates/{base}/{quote} ---
async function lookupPair(event) {
  event.preventDefault();
  const form = event.target;
  const lookupStatus = document.getElementById('lookup-status');
  const base = form.base.value.trim().toUpperCase();
  const quote = form.quote.value.trim().toUpperCase();
  try {
    const res = await fetch(`/api/rates/${base}/${quote}`);
    if (!res.ok) {
      const err = await res.json().catch(() => ({}));
      throw new Error(err.error || ('HTTP ' + res.status));
    }
    const rate = await res.json();
    lookupStatus.textContent = `${rate.base}/${rate.quote} = ${rate.rate} (as of ${rate.rateDate})`;
    lookupStatus.classList.remove('err');
  } catch (err) {
    lookupStatus.textContent = 'Could not look up pair: ' + err.message;
    lookupStatus.classList.add('err');
  }
}

loadRates();
document.getElementById('lookup-form').addEventListener('submit', lookupPair);
