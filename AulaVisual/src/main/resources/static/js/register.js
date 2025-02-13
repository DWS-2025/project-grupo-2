document.addEventListener('DOMContentLoaded', function() {
    const registerForm = document.getElementById('registerForm');
    const name = document.getElementById('name');
    const surname = document.getElementById('surname');
    const username = document.getElementById('username');
    const password = document.getElementById('password');
    const registerButton = registerForm.querySelector('button');

    function validateForm() {
        if (name.value.trim() !== '' && surname.value.trim() !== '' &&
            username.value.trim() !== '' && password.value.trim() !== '') {
            registerButton.disabled = false;
            registerButton.classList.remove('buttonDisabled');
        } else {
            registerButton.disabled = true;
            registerButton.classList.add('buttonDisabled');
        }
    }

    name.addEventListener('input', validateForm);
    surname.addEventListener('input', validateForm);
    username.addEventListener('input', validateForm);
    password.addEventListener('input', validateForm);

    validateForm(); // Initial validation
});