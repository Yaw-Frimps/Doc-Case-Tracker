package org.codewithzea.doccasetracker.util;

import lombok.RequiredArgsConstructor;
import org.codewithzea.doccasetracker.entity.User;
import org.codewithzea.doccasetracker.exception.UserNotFoundException;
import org.codewithzea.doccasetracker.repository.UserRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserQueryService {

    private final UserRepository userRepository;

    @Cacheable(value = "users", key = "#email")
    public User getUserByEmail(String email){
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + email));
    }
}
