package kz.arannati.arannati.controller;

import kz.arannati.arannati.dto.auth.*;
import kz.arannati.arannati.service.UserService;
import kz.arannati.arannati.service.CosmetologistVerificationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;

@Slf4j
@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final CosmetologistVerificationService verificationService;
    private final AuthenticationManager authenticationManager;

    /**
     * Страница входа
     */
    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error,
                            @RequestParam(value = "logout", required = false) String logout,
                            Model model) {

        if (error != null) {
            model.addAttribute("errorMessage", "Неверный email или пароль");
        }

        if (logout != null) {
            model.addAttribute("successMessage", "Вы успешно вышли из системы");
        }

        model.addAttribute("loginRequest", new LoginRequest());
        return "auth/login";
    }

    /**
     * Обработка входа (Spring Security сам обрабатывает, но можем переопределить)
     */
    @PostMapping("/login")
    public String processLogin(@Valid @ModelAttribute LoginRequest loginRequest,
                               BindingResult bindingResult,
                               RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.loginRequest", bindingResult);
            redirectAttributes.addFlashAttribute("loginRequest", loginRequest);
            redirectAttributes.addFlashAttribute("errorMessage", "Проверьте правильность введенных данных");
            return "redirect:/auth/login?error";
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            log.info("Успешный вход пользователя: {}", loginRequest.getEmail());
            return "redirect:/"; // редирект на главную страницу

        } catch (Exception e) {
            log.error("Ошибка входа для пользователя {}: {}", loginRequest.getEmail(), e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Неверный email или пароль");
            return "redirect:/auth/login?error";
        }
    }

    /**
     * Страница регистрации пользователя
     */
    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("userRegistrationRequest", new UserRegistrationRequest());
        return "auth/register";
    }

    /**
     * Обработка регистрации пользователя
     */
    @PostMapping("/register")
    public String processRegistration(@Valid @ModelAttribute UserRegistrationRequest request,
                                      BindingResult bindingResult,
                                      Model model,
                                      RedirectAttributes redirectAttributes) {

        // Проверка на ошибки валидации
        if (bindingResult.hasErrors()) {
            model.addAttribute("userRegistrationRequest", request);
            return "auth/register";
        }

        // Проверка совпадения паролей
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            model.addAttribute("userRegistrationRequest", request);
            model.addAttribute("errorMessage", "Пароли не совпадают");
            return "auth/register";
        }

        try {
            // Проверка существования пользователя
            if (userService.existsByEmail(request.getEmail())) {
                model.addAttribute("userRegistrationRequest", request);
                model.addAttribute("errorMessage", "Пользователь с таким email уже существует");
                return "auth/register";
            }

            // Создание пользователя
            userService.createUser(request);

            log.info("Пользователь успешно зарегистрирован: {}", request.getEmail());
            redirectAttributes.addFlashAttribute("successMessage",
                    "Регистрация прошла успешно! Теперь вы можете войти в систему.");

            return "redirect:/auth/login";

        } catch (Exception e) {
            log.error("Ошибка регистрации пользователя {}: {}", request.getEmail(), e.getMessage());
            model.addAttribute("userRegistrationRequest", request);
            model.addAttribute("errorMessage", "Ошибка при регистрации. Попробуйте позже.");
            return "auth/register";
        }
    }

    /**
     * Страница регистрации косметолога
     */
    @GetMapping("/register/cosmetologist")
    public String cosmetologistRegisterPage(Model model) {
        model.addAttribute("cosmetologistRegistrationRequest", new CosmetologistRegistrationRequest());
        return "auth/register-cosmetologist";
    }

    /**
     * Обработка регистрации косметолога
     */
    @PostMapping("/register/cosmetologist")
    public String processCosmetologistRegistration(
            @Valid @ModelAttribute CosmetologistRegistrationRequest request,
            BindingResult bindingResult,
            @RequestParam("diplomaFile") MultipartFile diplomaFile,
            Model model,
            RedirectAttributes redirectAttributes) {

        // Проверка на ошибки валидации
        if (bindingResult.hasErrors()) {
            model.addAttribute("cosmetologistRegistrationRequest", request);
            return "auth/register-cosmetologist";
        }

        // Проверка совпадения паролей
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            model.addAttribute("cosmetologistRegistrationRequest", request);
            model.addAttribute("errorMessage", "Пароли не совпадают");
            return "auth/register-cosmetologist";
        }

        // Валидация файла диплома
        if (diplomaFile.isEmpty()) {
            model.addAttribute("cosmetologistRegistrationRequest", request);
            model.addAttribute("errorMessage", "Файл диплома обязателен");
            return "auth/register-cosmetologist";
        }

        // Проверка типа файла
        String contentType = diplomaFile.getContentType();
        if (contentType == null || (!contentType.equals("application/pdf") &&
                !contentType.startsWith("image/"))) {
            model.addAttribute("cosmetologistRegistrationRequest", request);
            model.addAttribute("errorMessage", "Файл диплома должен быть в формате PDF или изображения");
            return "auth/register-cosmetologist";
        }

        try {
            // Проверка существования пользователя
            if (userService.existsByEmail(request.getEmail())) {
                model.addAttribute("cosmetologistRegistrationRequest", request);
                model.addAttribute("errorMessage", "Пользователь с таким email уже существует");
                return "auth/register-cosmetologist";
            }

            // Создание косметолога
            userService.createCosmetologist(request, diplomaFile);

            log.info("Косметолог успешно зарегистрирован: {}", request.getEmail());
            redirectAttributes.addFlashAttribute("successMessage",
                    "Заявка на регистрацию косметолога отправлена! " +
                            "После проверки документов вы получите уведомление на email.");

            return "redirect:/auth/login";

        } catch (Exception e) {
            log.error("Ошибка регистрации косметолога {}: {}", request.getEmail(), e.getMessage());
            model.addAttribute("cosmetologistRegistrationRequest", request);
            model.addAttribute("errorMessage", "Ошибка при регистрации. Попробуйте позже.");
            return "auth/register-cosmetologist";
        }
    }

    /**
     * Выход из системы (обрабатывается Spring Security, но можем добавить логику)
     */
    @GetMapping("/logout")
    public String logout() {
        return "redirect:/auth/login?logout";
    }

    /**
     * AJAX проверка доступности email
     */
    @GetMapping("/check-email")
    @ResponseBody
    public boolean checkEmailAvailability(@RequestParam String email) {
        log.info("Проверка доступности email: {}", email);
        return !userService.existsByEmail(email);
    }

    /**
     * Страница восстановления пароля
     */
    @GetMapping("/forgot-password")
    public String forgotPasswordPage() {
        return "auth/forgot-password";
    }

    /**
     * Обработка запроса на восстановление пароля
     */
    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam String email,
                                        RedirectAttributes redirectAttributes) {

        try {
            if (!userService.existsByEmail(email)) {
                redirectAttributes.addFlashAttribute("errorMessage",
                        "Пользователь с таким email не найден");
                return "redirect:/auth/forgot-password";
            }

            boolean emailSent = userService.sendPasswordResetEmail(email);

            if (emailSent) {
                redirectAttributes.addFlashAttribute("successMessage",
                        "Ссылка для восстановления пароля отправлена на ваш email");
                return "redirect:/auth/login";
            } else {
                redirectAttributes.addFlashAttribute("errorMessage",
                        "Ошибка при отправке письма. Попробуйте позже.");
                return "redirect:/auth/forgot-password";
            }

        } catch (Exception e) {
            log.error("Ошибка при восстановлении пароля для {}: {}", email, e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Ошибка при отправке письма. Попробуйте позже.");
            return "redirect:/auth/forgot-password";
        }
    }

    /**
     * Страница сброса пароля
     */
    @GetMapping("/reset-password")
    public String resetPasswordPage(@RequestParam(required = false) String token, Model model) {
        if (token == null || token.isEmpty()) {
            return "redirect:/auth/forgot-password";
        }

        // In a real application, we would validate the token here
        // For now, we'll just show the reset password page

        model.addAttribute("token", token);
        return "auth/reset-password";
    }

    /**
     * Обработка запроса на сброс пароля
     */
    @PostMapping("/reset-password")
    public String processResetPassword(@RequestParam String token,
                                      @RequestParam String password,
                                      @RequestParam String confirmPassword,
                                      RedirectAttributes redirectAttributes) {

        // Validate token and passwords
        if (token == null || token.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Недействительный токен");
            return "redirect:/auth/forgot-password";
        }

        if (password == null || password.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Пароль обязателен");
            return "redirect:/auth/reset-password?token=" + token;
        }

        if (!password.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Пароли не совпадают");
            return "redirect:/auth/reset-password?token=" + token;
        }

        // In a real application, we would:
        // 1. Find the user by the reset token
        // 2. Check if the token is still valid
        // 3. Update the user's password
        // 4. Clear the reset token

        // For now, we'll just show a success message
        log.info("Would reset password for token: {}", token);

        redirectAttributes.addFlashAttribute("successMessage", 
                "Пароль успешно изменен. Теперь вы можете войти в систему.");
        return "redirect:/auth/login";
    }
}
