package org.codewithzea.doccasetracker.util;

import org.codewithzea.doccasetracker.entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthenticatedUserProvider {
    public User getCurrentUser() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null ||
                !authentication.isAuthenticated() ||
                authentication.getPrincipal() == null) {

            throw new IllegalStateException(
                    "No authenticated user found"
            );
        }

        return (User) authentication.getPrincipal();
    }
}
