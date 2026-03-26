/*
 * login.js — Client-side validation for the login form.
 *
 * Keeps the submit button disabled while the username input is empty.
 * Server-side validation in LoginController acts as the authoritative guard.
 */
const input = document.getElementById('usernameInput');
const btn   = document.getElementById('submitBtn');

function syncButton() {
    btn.disabled = input.value.trim().length === 0;
}

input.addEventListener('input', syncButton);
input.addEventListener('paste', () => setTimeout(syncButton, 0));

input.addEventListener('keydown', (e) => {
    if (e.key === 'Enter' && input.value.trim().length === 0) {
        e.preventDefault();
    }
});
