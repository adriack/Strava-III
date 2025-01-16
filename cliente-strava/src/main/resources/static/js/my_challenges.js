document.addEventListener('DOMContentLoaded', () => {
    const challengeCards = document.querySelectorAll('.challenge-card');
    challengeCards.forEach(card => {
        const progress = card.getAttribute('data-progress');
        if (progress) {
            card.style.setProperty('--progress', `${progress}%`);
        }
    });
});
