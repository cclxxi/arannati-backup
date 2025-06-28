package kz.arannati.arannati.controller;

import kz.arannati.arannati.dto.ChatDTO;
import kz.arannati.arannati.dto.MessageDTO;
import kz.arannati.arannati.dto.OrderDTO;
import kz.arannati.arannati.dto.UserDTO;
import kz.arannati.arannati.service.ChatService;
import kz.arannati.arannati.service.MessageService;
import kz.arannati.arannati.service.OrderService;
import kz.arannati.arannati.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@ExtendWith(MockitoExtension.class)
public class DashboardControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @Mock
    private OrderService orderService;

    @Mock
    private MessageService messageService;

    @Mock
    private ChatService chatService;

    @InjectMocks
    private DashboardController dashboardController;

    private UserDTO userDTO;
    private MessageDTO messageDTO;
    private List<MessageDTO> messages;
    private List<ChatDTO> chats;

    @BeforeEach
    void setUp() {
        // Initialize MockMvc
        mockMvc = MockMvcBuilders.standaloneSetup(dashboardController).build();

        // Set up test data
        userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setEmail("user@example.com");
        userDTO.setFirstName("Test");
        userDTO.setLastName("User");
        userDTO.setRole("USER");

        messageDTO = new MessageDTO();
        messageDTO.setId(1L);
        messageDTO.setContent("Test message");
        messageDTO.setSenderId(1L);
        messageDTO.setSenderName("Test User");
        messageDTO.setSenderEmail("user@example.com");
        messageDTO.setRecipientId(2L);
        messageDTO.setRecipientName("Admin User");
        messageDTO.setRecipientEmail("admin@example.com");
        messageDTO.setRead(false);
        messageDTO.setCreatedAt(LocalDateTime.now());
        messageDTO.setUpdatedAt(LocalDateTime.now());

        messages = Arrays.asList(messageDTO);

        ChatDTO chatDTO = new ChatDTO();
        chatDTO.setChatId("chat_1_2");
        chatDTO.setOtherUserId(2L);
        chatDTO.setOtherUserName("Admin User");
        chatDTO.setOtherUserEmail("admin@example.com");
        chatDTO.setLastMessage("Test message");
        chatDTO.setLastMessageTime(LocalDateTime.now());
        chatDTO.setHasUnreadMessages(true);
        chatDTO.setUnreadCount(1);

        chats = Arrays.asList(chatDTO);
    }

    @AfterEach
    void tearDown() {
        // Clear security context after each test
        SecurityContextHolder.clearContext();
    }

    private void authenticateUser(String username) {
        // Create authentication token
        Authentication auth = new UsernamePasswordAuthenticationToken(
                username,
                null,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
        // Set authentication in security context
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    void dashboard_shouldShowDashboardWithMessages_whenUserIsAuthenticated() throws Exception {
        // Set up authentication
        authenticateUser("user@example.com");

        // Given
        when(userService.findByEmail("user@example.com")).thenReturn(Optional.of(userDTO));
        when(orderService.findByUserIdOrderByCreatedAtDesc(1L)).thenReturn(Collections.emptyList());
        when(messageService.findByRecipientId(1L)).thenReturn(messages);
        when(messageService.findBySenderId(1L)).thenReturn(Collections.emptyList());
        when(messageService.countUnreadByRecipientId(1L)).thenReturn(1L);
        when(chatService.getUserChats(1L)).thenReturn(chats);

        // When/Then
        mockMvc.perform(get("/dashboard"))
                .andExpect(status().isOk())
                .andExpect(view().name("dashboard/user"))
                .andExpect(model().attribute("user", userDTO))
                .andExpect(model().attribute("unreadMessageCount", 1L))
                .andExpect(model().attribute("receivedMessages", messages))
                .andExpect(model().attribute("chats", chats));
    }

    @Test
    void messages_shouldShowMessagesPage_whenUserIsAuthenticated() throws Exception {
        // Set up authentication
        authenticateUser("user@example.com");

        // Given
        when(userService.findByEmail("user@example.com")).thenReturn(Optional.of(userDTO));
        when(messageService.findByRecipientId(1L)).thenReturn(messages);
        when(messageService.findBySenderId(1L)).thenReturn(Collections.emptyList());
        when(messageService.countUnreadByRecipientId(1L)).thenReturn(1L);

        // When/Then
        mockMvc.perform(get("/dashboard/messages"))
                .andExpect(status().isOk())
                .andExpect(view().name("dashboard/messages"))
                .andExpect(model().attribute("user", userDTO))
                .andExpect(model().attribute("unreadMessageCount", 1L))
                .andExpect(model().attribute("receivedMessages", messages));
    }

    @Test
    void markMessageAsRead_shouldMarkMessageAsRead_whenUserIsAuthenticated() throws Exception {
        // Set up authentication
        authenticateUser("user@example.com");

        // Given
        when(messageService.markAsRead(1L)).thenReturn(true);

        // When/Then
        mockMvc.perform(post("/dashboard/messages/1/read")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("Message marked as read"));

        verify(messageService, times(1)).markAsRead(1L);
    }

    @Test
    void markMessageAsRead_shouldReturnError_whenMessageDoesNotExist() throws Exception {
        // Set up authentication
        authenticateUser("user@example.com");

        // Given
        when(messageService.markAsRead(1L)).thenReturn(false);

        // When/Then
        mockMvc.perform(post("/dashboard/messages/1/read")
                .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Failed to mark message as read"));

        verify(messageService, times(1)).markAsRead(1L);
    }

    @Test
    void sendMessage_shouldSendMessage_whenUserIsAuthenticated() throws Exception {
        // Set up authentication
        authenticateUser("user@example.com");

        // Given
        when(userService.findByEmail("user@example.com")).thenReturn(Optional.of(userDTO));
        when(messageService.sendMessage(1L, 2L, "Test message")).thenReturn(messageDTO);

        // When/Then
        mockMvc.perform(post("/dashboard/messages/send")
                .param("userId", "2")
                .param("message", "Test message")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("Message sent successfully"));

        verify(messageService, times(1)).sendMessage(1L, 2L, "Test message");
    }

    @Test
    void sendMessage_shouldReturnError_whenUserDoesNotExist() throws Exception {
        // Set up authentication
        authenticateUser("user@example.com");

        // Given
        when(userService.findByEmail("user@example.com")).thenReturn(Optional.empty());

        // When/Then
        mockMvc.perform(post("/dashboard/messages/send")
                .param("userId", "2")
                .param("message", "Test message")
                .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("User not found"));

        verify(messageService, times(0)).sendMessage(anyLong(), anyLong(), any());
    }

    @Test
    void dashboard_shouldRedirectToLogin_whenUserIsNotAuthenticated() throws Exception {
        // When/Then
        mockMvc.perform(get("/dashboard"))
                .andExpect(status().isOk())
                .andExpect(view().name("dashboard/guest"));
    }

    @Test
    void messages_shouldRedirectToLogin_whenUserIsNotAuthenticated() throws Exception {
        // When/Then
        mockMvc.perform(get("/dashboard/messages"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/login"));
    }
}
