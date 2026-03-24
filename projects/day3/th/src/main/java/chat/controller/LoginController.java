package chat.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * MVC controller handling the login flow and chat page access.
 *
 * <p>Authentication is intentionally lightweight: users identify themselves
 * with a plain username that is stored in the HTTP session. No passwords or
 * tokens are involved, keeping the app accessible to anyone with the link.
 *
 * <p>Session attribute used: {@code "username"} (String). The presence of
 * this attribute acts as the "logged in" check throughout the application.
 *
 * <p>Branch: feature/thymeleaf-ui
 *
 * @see chat.config.WebSocketConfig
 */
@Controller
public class LoginController {

    /** Session attribute key used to store and look up the username. */
    private static final String SESSION_USERNAME = "username";

    /**
     * Redirects the root URL to the login page.
     *
     * <p>This ensures that navigating to {@code /} always lands the user on
     * the login screen rather than a 404.
     *
     * @return redirect instruction to {@code /login}
     */
    @GetMapping("/")
    public String root() {
        return "redirect:/login";
    }

    /**
     * Renders the login page where users enter their username.
     *
     * <p>If the user already has an active session (username present), they are
     * redirected straight to the chat to avoid a redundant login step.
     *
     * @param session the current HTTP session, used to check existing login state
     * @return the {@code login} view name, or a redirect to {@code /chat}
     */
    @GetMapping("/login")
    public String loginPage(HttpSession session) {
        // Skip the login form if the user is already identified in this session.
        if (session.getAttribute(SESSION_USERNAME) != null) {
            return "redirect:/chat";
        }
        return "login";
    }

    /**
     * Processes the login form submission.
     *
     * <p>Validates that the submitted username is not blank, then stores it in
     * the session and redirects to the chat room. If the username is empty the
     * user is sent back to the login page.
     *
     * <p>Session management: {@code HttpSession.setAttribute} creates the session
     * automatically if one does not exist. The session lifetime is controlled by
     * the servlet container (default 30 minutes of inactivity).
     *
     * @param username the name entered by the user in the login form
     * @param session  the current HTTP session where the username will be stored
     * @return redirect to {@code /chat} on success, redirect to {@code /login} on error
     */
    @PostMapping("/login")
    public String login(@RequestParam(value = "username", defaultValue = "") String username,
                        HttpSession session) {
        // Reject blank usernames — the frontend also validates this, but we
        // guard server-side to handle direct POST requests.
        String trimmed = username.trim();
        if (trimmed.isEmpty()) {
            return "redirect:/login";
        }

        // Store the cleaned-up username in the session. Any subsequent request
        // within this session can retrieve it with session.getAttribute("username").
        session.setAttribute(SESSION_USERNAME, trimmed);
        return "redirect:/chat";
    }

    /**
     * Logs out the current user by invalidating the HTTP session and redirecting
     * to the login page.
     *
     * <p>Using {@code POST} (instead of {@code GET}) prevents accidental logouts
     * triggered by prefetch or link previews. The chat template submits a hidden
     * form to this endpoint when the user clicks the "Leave" button.
     *
     * <p>Invalidating the session removes the {@code "username"} attribute and all
     * other session state, ensuring a clean slate for the next user on the same
     * browser.
     *
     * @param session the current HTTP session to invalidate
     * @return redirect to {@code /login}
     */
    @PostMapping("/logout")
    public String logout(HttpSession session) {
        // Destroy the session entirely rather than just removing the username
        // attribute, so no other session-scoped state leaks to the next user.
        session.invalidate();
        return "redirect:/login";
    }

    /**
     * Renders the main chat page for authenticated users.
     *
     * <p>The username is added to the Thymeleaf model so the template can
     * display it in the header and embed it in the JavaScript that initialises
     * the WebSocket connection.
     *
     * @param session the current HTTP session, checked for a valid username
     * @param model   the Thymeleaf model populated with {@code "username"}
     * @return the {@code chat} view name, or a redirect to {@code /login} if not logged in
     */
    @GetMapping("/chat")
    public String chatPage(HttpSession session, Model model) {
        // If there is no username in the session the user has not gone through
        // the login page; redirect them back rather than showing an empty name.
        String username = (String) session.getAttribute(SESSION_USERNAME);
        if (username == null) {
            return "redirect:/login";
        }

        // Expose the username to the Thymeleaf template and the inline JS.
        model.addAttribute("username", username);
        return "chat";
    }
}
