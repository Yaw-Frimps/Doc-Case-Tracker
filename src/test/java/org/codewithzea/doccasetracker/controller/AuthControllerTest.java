package org.codewithzea.doccasetracker.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.codewithzea.doccasetracker.dto.request.LoginRequest;
import org.codewithzea.doccasetracker.dto.request.RegisterRequest;
import org.codewithzea.doccasetracker.entity.Role;
import org.codewithzea.doccasetracker.entity.RoleType;
import org.codewithzea.doccasetracker.entity.User;
import org.codewithzea.doccasetracker.repository.RoleRepository;
import org.codewithzea.doccasetracker.repository.UserRepository;
import org.codewithzea.doccasetracker.service.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private EmailService emailService;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        
        // Ensure default role exists
        if (roleRepository.findByName(RoleType.ROLE_WORKER).isEmpty()) {
            roleRepository.save(Role.builder().name(RoleType.ROLE_WORKER).build());
        }
        
        doNothing().when(emailService).sendOtpEmail(any(), any());
        doNothing().when(emailService).sendPasswordResetConfirmationEmail(any());
    }

    @Test
    void registerUser_Success() throws Exception {
        RegisterRequest request = RegisterRequest.builder()
                .firstName("Alice")
                .lastName("Smith")
                .email("alice@example.com")
                .phoneNumber("+1999999999")
                .password("Password@123")
                .confirmPassword("Password@123")
                .build();

        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.data.user.email").value("alice@example.com"));
    }

    @Test
    void loginUser_Success() throws Exception {
        Role workerRole = roleRepository.findByName(RoleType.ROLE_WORKER)
                .orElseGet(() -> roleRepository.save(Role.builder().name(RoleType.ROLE_WORKER).build()));

        User user = User.builder()
                .firstName("Bob")
                .lastName("Jones")
                .email("bob@example.com")
                .phoneNumber("+1888888888")
                .password(passwordEncoder.encode("Password@123"))
                .role(workerRole)
                .enabled(true)
                .accountNonLocked(true)
                .build();
        userRepository.save(user);

        LoginRequest request = LoginRequest.builder()
                .email("bob@example.com")
                .password("Password@123")
                .build();

        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.data.user.email").value("bob@example.com"));
    }
}
