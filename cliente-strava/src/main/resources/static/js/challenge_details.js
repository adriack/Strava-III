function highlightParticipant() {
    const element = document.getElementById('highlight');
    if (element) {
        element.style.backgroundColor = 'salmon';
        setTimeout(() => {
            element.style.backgroundColor = '';
        }, 1000);
    }
}