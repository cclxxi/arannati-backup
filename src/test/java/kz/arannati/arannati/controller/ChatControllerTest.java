package kz.arannati.arannati.controller;

import kz.arannati.arannati.dto.ChatDTO;
import kz.arannati.arannati.dto.MessageDTO;
import kz.arannati.arannati.dto.UserDTO;
import kz.arannati.arannati.service.ChatService;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@ExtendWith(MockitoExtension.class)
public class ChatControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ChatService chatService;

    @Mock
    private UserService userService;

    @InjectMocks
    private ChatController chatController;

    private UserDTO userDTO;
    private UserDTO adminDTO;
    private MessageDTO messageDTO;
    private List<MessageDTO> messages;
    private List<ChatDTO> chats;
    private String chatId;

    @BeforeEach
    void setUp() {
        // Initialize MockMvc
        mockMvc = MockMvcBuilders.standaloneSetup(chatController).build();

        // Set up test data
        chatId = "chat_1_2";

        userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setEmail("user@example.com");
        userDTO.setFirstName("Test");
        userDTO.setLastName("User");
        userDTO.setRole("USER");

        adminDTO = new UserDTO();
        adminDTO.setId(2L);
        adminDTO.setEmail("admin@example.com");
        adminDTO.setFirstName("Admin");
        adminDTO.setLastName("User");
        adminDTO.setRole("ADMIN");

        messageDTO = new MessageDTO();
        messageDTO.setId(1L);
        messageDTO.setContent("Test message");
        messageDTO.setSenderId(1L);
        messageDTO.setSenderName("Test User");
        messageDTO.setSenderEmail("user@example.com");
        messageDTO.setSenderRole("USER");
        messageDTO.setRecipientId(2L);
        messageDTO.setRecipientName("Admin User");
        messageDTO.setRecipientEmail("admin@example.com");
        messageDTO.setRecipientRole("ADMIN");
        messageDTO.setRead(false);
        messageDTO.setCreatedAt(LocalDateTime.now());
        messageDTO.setUpdatedAt(LocalDateTime.now());
        messageDTO.setChatId(chatId);
        messageDTO.setMessageType("DIRECT");
        messageDTO.setBroadcast(false);
        messageDTO.setSystemMessage(false);

        messages = Arrays.asList(messageDTO);

        ChatDTO chatDTO = new ChatDTO();
        chatDTO.setChatId(chatId);
        chatDTO.setOtherUserId(2L);
        chatDTO.setOtherUserName("Admin User");
        chatDTO.setOtherUserEmail("admin@example.com");
        chatDTO.setOtherUserRole("ADMIN");
        chatDTO.setOtherUserVerified(true);
        chatDTO.setLastMessage("Test message");
        chatDTO.setLastMessageTime(LocalDateTime.now());
        chatDTO.setLastMessageFromMe(false);
        chatDTO.setHasUnreadMessages(true);
        chatDTO.setUnreadCount(1);
        chatDTO.setChatType(ChatDTO.ChatType.ADMIN_USER);
        chatDTO.setSystemChat(false);

        chats = Arrays.asList(chatDTO);
    }

    @AfterEach
    void tearDown() {
        // Clear security context after each test
        SecurityContextHolder.clearContext();
    }

    private void authenticateUser(String username, String role) {
        // Create authentication token
        Authentication auth = new UsernamePasswordAuthenticationToken(
                username,
                null,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role))
        );
        // Set authentication in security context
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    void messagesPage_shouldShowMessagesPage_whenUserIsAuthenticated() throws Exception {
        // Set up authentication
        authenticateUser("user@example.com", "USER");

        // Given
        when(userService.findByEmail("user@example.com")).thenReturn(Optional.of(userDTO));
        when(chatService.getUserChats(1L)).thenReturn(chats);

        // When/Then
        mockMvc.perform(get("/messages"))
                .andExpect(status().isOk())
                .andExpect(view().name("messages/index"))
                .andExpect(model().attribute("chats", chats))
                .andExpect(model().attribute("currentUser", userDTO));
    }

    @Test
    void messagesPage_shouldShowSupportRequests_whenAdminIsAuthenticated() throws Exception {
        // Set up authentication
        authenticateUser("admin@example.com", "ADMIN");

        // Given
        when(userService.findByEmail("admin@example.com")).thenReturn(Optional.of(adminDTO));
        when(chatService.getUserChats(2L)).thenReturn(chats);
        when(chatService.getActiveSupportRequests()).thenReturn(messages);

        // When/Then
        mockMvc.perform(get("/messages"))
                .andExpect(status().isOk())
                .andExpect(view().name("messages/index"))
                .andExpect(model().attribute("chats", chats))
                .andExpect(model().attribute("currentUser", adminDTO))
                .andExpect(model().attribute("supportRequests", messages));
    }

    @Test
    void chatPage_shouldShowChatPage_whenUserIsAuthenticated() throws Exception {
        // Set up authentication
        authenticateUser("user@example.com", "USER");

        // Given
        when(userService.findByEmail("user@example.com")).thenReturn(Optional.of(userDTO));
        when(chatService.getChatMessages(chatId, 1L)).thenReturn(messages);

        // When/Then
        mockMvc.perform(get("/messages/chat/" + chatId))
                .andExpect(status().isOk())
                .andExpect(view().name("messages/chat"))
                .andExpect(model().attribute("messages", messages))
                .andExpect(model().attribute("chatId", chatId))
                .andExpect(model().attribute("currentUser", userDTO));

        verify(chatService, times(1)).markChatAsRead(chatId, 1L);
    }

    @Test
    void sendMessage_shouldSendMessage_whenUserIsAuthenticated() throws Exception {
        // Set up authentication
        authenticateUser("user@example.com", "USER");

        // Given
        when(userService.findByEmail("user@example.com")).thenReturn(Optional.of(userDTO));
        when(chatService.sendDirectMessage(1L, 2L, "Test message")).thenReturn(messageDTO);

        // When/Then
        mockMvc.perform(post("/messages/send")
                .param("recipientId", "2")
                .param("content", "Test message")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.chatId").value(chatId));

        verify(chatService, times(1)).sendDirectMessage(1L, 2L, "Test message");
    }

    @Test
    void sendSupportRequest_shouldSendRequest_whenUserIsAuthenticated() throws Exception {
        // Set up authentication
        authenticateUser("user@example.com", "USER");

        // Given
        when(userService.findByEmail("user@example.com")).thenReturn(Optional.of(userDTO));
        when(chatService.canSendToAdmins(1L)).thenReturn(true);
        when(chatService.sendSupportRequest(1L, "Support request")).thenReturn(messageDTO);

        // When/Then
        mockMvc.perform(post("/messages/support")
                .param("content", "Support request")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(chatService, times(1)).sendSupportRequest(1L, "Support request");
    }

    @Test
    void replySupportRequest_shouldReplyToRequest_whenAdminIsAuthenticated() throws Exception {
        // Set up authentication
        authenticateUser("admin@example.com", "ADMIN");

        // Given
        when(userService.findByEmail("admin@example.com")).thenReturn(Optional.of(adminDTO));
        when(chatService.replyToSupportRequest(2L, 1L, "Reply to support")).thenReturn(messageDTO);

        // When/Then
        mockMvc.perform(post("/messages/support/reply")
                .param("originalMessageId", "1")
                .param("content", "Reply to support")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.chatId").value(chatId));

        verify(chatService, times(1)).replyToSupportRequest(2L, 1L, "Reply to support");
    }

    @Test
    void getChats_shouldReturnChats_whenUserIsAuthenticated() throws Exception {
        // Set up authentication
        authenticateUser("user@example.com", "USER");

        // Given
        when(userService.findByEmail("user@example.com")).thenReturn(Optional.of(userDTO));
        when(chatService.getUserChats(1L)).thenReturn(chats);

        // When/Then
        mockMvc.perform(get("/messages/api/chats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].chatId").value(chatId));

        verify(chatService, times(1)).getUserChats(1L);
    }

    @Test
    void getChatMessages_shouldReturnMessages_whenUserIsAuthenticated() throws Exception {
        // Set up authentication
        authenticateUser("user@example.com", "USER");

        // Given
        when(userService.findByEmail("user@example.com")).thenReturn(Optional.of(userDTO));
        when(chatService.getChatMessages(chatId, 1L)).thenReturn(messages);

        // When/Then
        mockMvc.perform(get("/messages/api/chat/" + chatId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].content").value("Test message"));

        verify(chatService, times(1)).getChatMessages(chatId, 1L);
    }

    @Test
    void markChatAsRead_shouldMarkChatAsRead_whenUserIsAuthenticated() throws Exception {
        // Set up authentication
        authenticateUser("user@example.com", "USER");

        // Given
        when(userService.findByEmail("user@example.com")).thenReturn(Optional.of(userDTO));

        // When/Then
        mockMvc.perform(post("/messages/api/chat/" + chatId + "/read")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(chatService, times(1)).markChatAsRead(chatId, 1L);
    }

    @Test
    void getUnreadCount_shouldReturnCount_whenUserIsAuthenticated() throws Exception {
        // Set up authentication
        authenticateUser("user@example.com", "USER");

        // Given
        when(userService.findByEmail("user@example.com")).thenReturn(Optional.of(userDTO));
        when(chatService.getUnreadMessagesCount(1L)).thenReturn(5L);

        // When/Then
        mockMvc.perform(get("/messages/api/unread-count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(5));

        verify(chatService, times(1)).getUnreadMessagesCount(1L);
    }
}
