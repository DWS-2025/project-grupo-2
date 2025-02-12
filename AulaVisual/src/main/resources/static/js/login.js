document.addEventListener('DOMContentLoaded', function() {
    const loginForm = document.getElementById('loginForm');
    const username = document.getElementById('username');
    const password = document.getElementById('password');
    const loginButton = loginForm.querySelector('button');

    function validateForm() {
        if (username.value.trim() !== '' && password.value.trim() !== '') {
            loginButton.disabled = false;
            loginButton.classList.remove('buttonDisabled');
        } else {
            loginButton.disabled = true;
            loginButton.classList.add('buttonDisabled');
        }
    }

    username.addEventListener('input', validateForm);
    password.addEventListener('input', validateForm);

    validateForm(); // Initial validation
});