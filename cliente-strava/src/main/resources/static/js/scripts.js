
// ...existing code...

function submitForm(form, callback) {
    const formData = new FormData(form);
    const xhr = new XMLHttpRequest();
    xhr.open(form.method, form.action, true);
    xhr.onload = function () {
        if (xhr.status >= 200 && xhr.status < 300) {
            callback();
        } else {
            console.error('Error:', xhr.statusText);
        }
    };
    xhr.send(formData);
}

function reloadTrainingSessions() {
    // Implementa la lógica para recargar las sesiones de entrenamiento
    // Por ejemplo, podrías hacer una solicitud AJAX para obtener las sesiones actualizadas
    console.log('Recargando sesiones de entrenamiento...');
    // Aquí deberías actualizar el DOM con las nuevas sesiones
}

// ...existing code...