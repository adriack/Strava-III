function swapDatesIfNeeded() {
    const startDateInput = document.getElementById('startDate');
    const endDateInput = document.getElementById('endDate');
    const startDate = new Date(startDateInput.value);
    const endDate = new Date(endDateInput.value);

    if (startDate > endDate) {
        const temp = startDateInput.value;
        startDateInput.value = endDateInput.value;
        endDateInput.value = temp;
    }
}

document.addEventListener('DOMContentLoaded', () => {
    const startDateInput = document.getElementById('startDate');
    const endDateInput = document.getElementById('endDate');

    startDateInput.addEventListener('change', swapDatesIfNeeded);
    endDateInput.addEventListener('change', swapDatesIfNeeded);
});