package com.fladx.otpservice;

import com.fladx.otpservice.dto.UserDto;
import com.fladx.otpservice.dto.otp.GenerateCodeRequestDto;
import com.fladx.otpservice.dto.otp.ValidateCodeRequestDto;
import com.fladx.otpservice.model.user.UserRole;
import com.fladx.otpservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class OtpServiceApplicationTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void cleanup() {
        userRepository.deleteAll();
    }

    @Test
    void contextLoads() {
    }

    @Test
    void shouldNotCreateSecondAdmin() {
        // Первый админ создается успешно
        registerUser(UserRole.ADMIN);

        // Попытка создать второго админа
        ResponseEntity<String> response = registerUser(UserRole.ADMIN);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("Admin user already exists"));
    }

    @Test
    void shouldGenerateAndValidateOtp() {
        // Регистрация и аутентификация пользователя
        registerUser(UserRole.USER);
        String token = loginUser();

        // Генерация OTP
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        GenerateCodeRequestDto generateRequest = new GenerateCodeRequestDto("test-op");
        ResponseEntity<String> generateResponse = restTemplate.exchange(
                "/otp",
                HttpMethod.POST,
                new HttpEntity<>(generateRequest, headers),
                String.class);

        assertEquals(HttpStatus.OK, generateResponse.getStatusCode());
        String otpCode = generateResponse.getBody();

        // Валидация OTP
        ValidateCodeRequestDto validateRequest = new ValidateCodeRequestDto(otpCode);
        ResponseEntity<Void> validateResponse = restTemplate.exchange(
                "/otp/validate",
                HttpMethod.POST,
                new HttpEntity<>(validateRequest, headers),
                Void.class);

        assertEquals(HttpStatus.OK, validateResponse.getStatusCode());
    }

    private ResponseEntity<String> registerUser(UserRole role) {
        UserDto userDto = new UserDto(
                "testuser_" + role.name(),
                "password123",
                role);
        return restTemplate.postForEntity("/auth/register", userDto, String.class);
    }

    private String loginUser() {
        UserDto userDto = new UserDto(
                "testuser_USER",
                "password123",
                UserRole.USER);
        ResponseEntity<String> response = restTemplate.postForEntity("/auth/login", userDto, String.class);
        return response.getBody();
    }
}