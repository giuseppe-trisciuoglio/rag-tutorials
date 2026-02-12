const preBookingIdElement = document.getElementById('preBookingId');
const guestNameElement = document.getElementById('guestName');
const emailElement = document.getElementById('email');
const bookingDateElement = document.getElementById('bookingDate');
const amountLabelElement = document.getElementById('amountLabel');
const confirmButton = document.getElementById('confirmPaymentBtn');
const statusBox = document.getElementById('statusBox');

const params = new URLSearchParams(window.location.search);
const preBookingId = params.get('preBookingId');

if (!preBookingId) {
    showStatus('Pre-booking ID mancante nel link.', 'error');
    confirmButton.disabled = true;
} else {
    loadPreBooking(preBookingId);
}

confirmButton.addEventListener('click', async () => {
    if (!preBookingId) {
        return;
    }
    try {
        confirmButton.disabled = true;
        confirmButton.textContent = 'Pagamento in corso...';
        const response = await fetch(`/api/checkout/pre-bookings/${encodeURIComponent(preBookingId)}/confirm`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' }
        });

        if (!response.ok) {
            const fallbackMessage = 'Impossibile confermare il pagamento.';
            const payload = await safeJson(response);
            throw new Error(payload?.message || fallbackMessage);
        }

        const payload = await response.json();
        const redirectParams = new URLSearchParams({
            preBookingId: payload.preBookingId || preBookingId,
            bookingNumber: payload.bookingNumber || '',
            paymentStatus: payload.paymentStatus || 'CONFIRMED',
            confirmedAt: payload.confirmedAt || '',
            totalPriceEur: typeof payload.totalPriceEur === 'number' ? String(payload.totalPriceEur) : '',
            checkIn: payload.checkIn || '',
            checkOut: payload.checkOut || '',
            summary: payload.summary || ''
        });

        window.location.href = `/thank-you.html?${redirectParams.toString()}`;
    } catch (error) {
        showStatus(error.message, 'error');
        confirmButton.disabled = false;
        confirmButton.textContent = 'Conferma pagamento';
    }
});

async function loadPreBooking(id) {
    try {
        const response = await fetch(`/api/checkout/pre-bookings/${encodeURIComponent(id)}`);
        if (!response.ok) {
            const payload = await safeJson(response);
            throw new Error(payload?.message || 'Pre-booking non trovato.');
        }

        const preBooking = await response.json();
        preBookingIdElement.textContent = preBooking.preBookingId;
        guestNameElement.textContent = `${preBooking.firstName} ${preBooking.lastName}`;
        emailElement.textContent = preBooking.email || '-';
        bookingDateElement.textContent = formatDate(preBooking.bookingDate);
        amountLabelElement.textContent = `${formatCurrency(preBooking.totalPriceEur)} EUR`;

        if (preBooking.status === 'CONFIRMED') {
            confirmButton.disabled = true;
            confirmButton.textContent = 'Pagamento già confermato';
            showStatus(
                `Prenotazione già confermata con numero ${preBooking.bookingNumber}.`,
                'success'
            );
        }
    } catch (error) {
        confirmButton.disabled = true;
        showStatus(error.message, 'error');
    }
}

function showStatus(message, type) {
    statusBox.classList.add('show', type);
    statusBox.classList.remove(type === 'error' ? 'success' : 'error');
    statusBox.textContent = message;
}

function formatDate(value) {
    if (!value) {
        return '-';
    }
    const parsed = new Date(value);
    if (Number.isNaN(parsed.getTime())) {
        return value;
    }
    return parsed.toLocaleString('it-IT');
}

function formatCurrency(value) {
    if (typeof value !== 'number') {
        return '-';
    }
    return new Intl.NumberFormat('it-IT').format(value);
}

async function safeJson(response) {
    try {
        return await response.json();
    } catch (e) {
        return null;
    }
}
