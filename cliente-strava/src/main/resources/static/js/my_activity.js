document.addEventListener('DOMContentLoaded', (event) => {
    const formElement = document.getElementById('sessions-filter');
    
    if (formElement) {
        formElement.addEventListener('change', function(event) {
            updateURLWithFilters(this);
            submitForm(this, 'training-sessions-container');
        });
    } else {
        console.error("Form element not found");
    }
});

function updateURLWithFilters(form) {
    const url = new URL(window.location.href);
    const params = new URLSearchParams(url.search);

    const sport = form.querySelector('[name="sport"]').value;
    const startDate = form.querySelector('[name="startDate"]').value;
    const endDate = form.querySelector('[name="endDate"]').value;

    console.log("Updating URL with filters:", { sport, startDate, endDate });

    if (sport) {
        params.set('sport', sport);
    } else {
        params.delete('sport');
    }

    if (startDate) {
        params.set('startDate', startDate);
    } else {
        params.delete('startDate');
    }

    if (endDate) {
        params.set('endDate', endDate);
    } else {
        params.delete('endDate');
    }

    const newUrl = `${url.pathname}?${params}`;
    console.log("New URL:", newUrl);
    window.history.replaceState({}, '', newUrl);
}
