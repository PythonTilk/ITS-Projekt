package notizprojekt.web.config;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * A custom password encoder that doesn't actually encode passwords.
 * This is used for compatibility with the existing database where passwords are stored in plain text.
 * In a production environment, you should use a proper password encoder like BCryptPasswordEncoder.
 */
@Component
public class CustomPasswordEncoder implements PasswordEncoder {

    @Override
    public String encode(CharSequence rawPassword) {
        return rawPassword.toString();
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return rawPassword.toString().equals(encodedPassword);
    }
}