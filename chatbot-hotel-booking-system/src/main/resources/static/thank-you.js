const summaryBox = document.getElementById('summaryBox');
const errorBox = document.getElementById('errorBox');
const bookingNumberElement = document.getElementById('bookingNumber');
const preBookingIdElement = document.getElementById('preBookingId');
const guestNameElement = document.getElementById('guestName');
const emailElement = document.getElementById('email');
const bookingDateElement = document.getElementById('bookingDate');
const checkInElement = document.getElementById('checkIn');
const checkOutElement = document.getElementById('checkOut');
const confirmedAtElement = document.getElementById('confirmedAt');
const paymentStatusElement = document.getElementById('paymentStatus');
const amountLabelElement = document.getElementById('amountLabel');
const backToCheckoutLink = document.getElementById('backToCheckoutLink');

const params = new URLSearchParams(window.location.search);
const preBookingId = params.get('preBookingId');
const bookingNumber = params.get('bookingNumber');
const paymentStatus = params.get('paymentStatus');
const confirmedAt = params.get('confirmedAt');
const totalPriceEur = parseNumeric(params.get('totalPriceEur'));
const summary = params.get('summary');
const checkIn = params.get('checkIn') || params.get('checkin');
const checkOut = params.get('checkOut') || params.get('checkout');

preBookingIdElement.textContent = preBookingId || '-';
bookingNumberElement.textContent = bookingNumber || '-';
paymentStatusElement.textContent = paymentStatus || '-';
confirmedAtElement.textContent = formatDate(confirmedAt);
checkInElement.textContent = formatDateOnly(checkIn);
checkOutElement.textContent = formatDateOnly(checkOut);
amountLabelElement.textContent = typeof totalPriceEur === 'number'
    ? `${formatCurrency(totalPriceEur)} EUR`
    : '- EUR';
summaryBox.textContent = summary || 'Prenotazione completata con successo.';

if (preBookingId) {
    backToCheckoutLink.href = `/checkout.html?preBookingId=${encodeURIComponent(preBookingId)}`;
    loadPreBooking(preBookingId);
} else {
    showError('Pre-booking ID mancante. Non posso mostrare il riepilogo completo.');
}

async function loadPreBooking(id) {
    try {
        const response = await fetch(`/api/checkout/pre-bookings/${encodeURIComponent(id)}`);
        if (!response.ok) {
            const payload = await safeJson(response);
            throw new Error(payload?.message || 'Impossibile recuperare il riepilogo.');
        }

        const preBooking = await response.json();
        guestNameElement.textContent = `${preBooking.firstName} ${preBooking.lastName}`;
        emailElement.textContent = preBooking.email || '-';
        bookingDateElement.textContent = formatDate(preBooking.bookingDate);
        checkInElement.textContent = formatDateOnly(
            preBooking.checkIn
                || preBooking.checkin
                || preBooking.bookingDate
        );
        checkOutElement.textContent = formatDateOnly(
            preBooking.checkOut
                || preBooking.checkout
                || preBooking.checkoutDate
        );

        if (typeof preBooking.totalPriceEur === 'number') {
            amountLabelElement.textContent = `${formatCurrency(preBooking.totalPriceEur)} EUR`;
        }

        if (preBooking.bookingNumber && bookingNumberElement.textContent === '-') {
            bookingNumberElement.textContent = preBooking.bookingNumber;
        }
    } catch (error) {
        showError(error.message);
    }
}

function showError(message) {
    errorBox.classList.add('show');
    errorBox.textContent = message;
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

function formatDateOnly(value) {
    if (!value) {
        return '-';
    }
    const parsed = new Date(value);
    if (Number.isNaN(parsed.getTime())) {
        return value;
    }
    return parsed.toLocaleDateString('it-IT');
}

function formatCurrency(value) {
    if (typeof value !== 'number') {
        return '-';
    }
    return new Intl.NumberFormat('it-IT').format(value);
}

function parseNumeric(value) {
    if (value === null || value.trim() === '') {
        return null;
    }
    const parsed = Number(value);
    return Number.isFinite(parsed) ? parsed : null;
}

async function safeJson(response) {
    try {
        return await response.json();
    } catch (e) {
        return null;
    }
}
