document.querySelectorAll('.book-card').forEach(card => {
    card.addEventListener('click', (e) => {
        // Игнорируем клики по кнопкам внутри карточки
        if (e.target.tagName === 'A' || e.target.tagName === 'BUTTON') {
            return;
        }
        const url = card.getAttribute('data-href');
        if (url) {
            window.location.href = url;
        }
    });
});