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
            const submissionId = document.querySelector('input[name="submissionId"]').value;

            await fetch('/courses/' + courseId + '/submission/'+ submissionId +'/comment', {
                method: 'POST',
                headers: {
                    'X-CSRF-TOKEN': csrfToken,
                },
                body: new URLSearchParams({ comment: cleanedComment }),
            }).then(response => {
                if (response.ok) {
                    console.log('Comentario guardado con éxito');
                } else {
                    console.log('Error al guardar el comentario');
                }
            });
            window.location.reload();
        });
    }
});