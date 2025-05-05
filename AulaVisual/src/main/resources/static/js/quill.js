document.addEventListener('DOMContentLoaded', () => {
    // Inicializar Quill
    const quill = new Quill('#comment-editor', {
        theme: 'snow',
        placeholder: 'Escribe tu comentario aquí...',
    });

    // Guardar comentario
    const saveCommentButton = document.getElementById('save-comment');
    if (saveCommentButton) {
        saveCommentButton.addEventListener('click', async () => {
            const rawComment = quill.root.innerHTML;
            const cleanedComment = DOMPurify.sanitize(rawComment);
            const csrfToken = document.querySelector('input[name="_csrf"]').value;
            const courseId = document.querySelector('input[name="courseId"]').value;

            await fetch('/courses/' + courseId + '/submission/comment', {
                method: 'POST',
                headers: {
                    'X-CSRF-TOKEN': csrfToken,
                },
                body: new URLSearchParams({ comment: cleanedComment }),
            }).then(response => {
                if (response.ok) {
                    alert('Comentario guardado con éxito');
                } else {
                    alert('Error al guardar el comentario');
                }
            });
        });
    }
});