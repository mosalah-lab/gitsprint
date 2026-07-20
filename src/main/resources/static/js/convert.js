// Convert feature — the front-end half of the 03-conversion-calculator slice.
// Submits the form to GET /api/convert and renders the result table.

async function convert(event) {
  event.preventDefault();
  const form = event.target;
  const status = document.getElementById('convert-status');
  const table = document.getElementById('result-table');

  const base = form.base.value.trim().toUpperCase();
  const quote = form.quote.value.trim().toUpperCase();
  const amount = form.amount.value;

  if (Number(amount) <= 0) {
    status.textContent = 'Amount must be greater than zero.';
    status.classList.add('err');
    table.style.display = 'none';
    return;
  }

  try {
    const params = new URLSearchParams({ base, quote, amount });
    const res = await fetch(`/api/convert?${params}`);
    if (!res.ok) {
      const err = await res.json().catch(() => ({}));
      throw new Error(err.error || ('HTTP ' + res.status));
    }
    const result = await res.json();

    document.getElementById('r-amount').textContent = `${result.amount} ${base}`;
    document.getElementById('r-rate').textContent = result.rate;
    document.getElementById('r-converted').textContent = `${result.converted} ${quote}`;
    document.getElementById('r-fee').textContent = result.fee;
    document.getElementById('r-total').textContent = `${result.total} ${quote}`;
    table.style.display = '';
    status.textContent = '';
    status.classList.remove('err');
  } catch (err) {
    table.style.display = 'none';
    status.textContent = 'Could not convert: ' + err.message;
    status.classList.add('err');
  }
}

document.getElementById('convert-form').addEventListener('submit', convert);
