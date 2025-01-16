function closePopup(popupId) {
    document.getElementById(popupId).style.display = 'none';
}

function toggleEditForm(formId) {
    //Alterna la visibilidad de un formulario.
    var form = document.getElementById(formId + "-form");
    if (
        form.style.display === "none" ||
        form.style.display === ""
    ) {
        form.style.display = "flex";
    } else {
        form.style.display = "none";
    }
}

function submitForm(form, element) {
    // Ejecuta el método HTTP del formulario y sustituye el contenido del
    // elemento dado con el HTML recibido como respuesta.
    const formData = new FormData(form);
    console.log("Submitting form:", form.action);
    fetch(form.action, {
        method: form.method,
        body: formData
    })
    .then(response => response.text())
    .then(html => {
        console.log("Form submitted successfully.");
        document.getElementById(element).innerHTML = html;
    })
    .catch(error => console.error('Error:', error));
}