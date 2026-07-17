package com.example.notification;

import com.example.notification.model.Channel;
import com.example.notification.model.NotificationSettings;
import com.example.notification.model.NotificationTemplate;
import com.example.notification.model.SendNotificationRequest;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class NotificationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testGetTemplatesAndCrud() throws Exception {
        // 1. Verify default templates (loaded via data.sql) exist
        mockMvc.perform(get("/api/templates"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(5))))
                .andExpect(jsonPath("$[?(@.templateKey == 'WELCOME')].channel", containsInAnyOrder("EMAIL", "SMS", "IN_APP")));

        // 2. Create a new custom template
        NotificationTemplate customTemplate = new NotificationTemplate(
                "LOW_BALANCE_ALERT",
                Channel.EMAIL,
                "Warning: Low Wallet Balance",
                "Hi {{username}}, your wallet balance is below {{limit}}. Current balance is {{balance}}."
        );

        mockMvc.perform(post("/api/templates")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customTemplate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.templateKey", is("LOW_BALANCE_ALERT")))
                .andExpect(jsonPath("$.subjectTemplate", is("Warning: Low Wallet Balance")));

        // 3. Retrieve the created template
        mockMvc.perform(get("/api/templates/LOW_BALANCE_ALERT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].bodyTemplate", containsString("Hi {{username}}")));

        // 4. Delete the custom template
        mockMvc.perform(delete("/api/templates/LOW_BALANCE_ALERT/EMAIL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)));

        // 5. Verify it is gone
        mockMvc.perform(get("/api/templates/LOW_BALANCE_ALERT"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testNotificationSettingsCrud() throws Exception {
        String userId = "test_user_settings";

        // 1. Fetch default settings (should auto-create with all true)
        mockMvc.perform(get("/api/notifications/settings/" + userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId", is(userId)))
                .andExpect(jsonPath("$.emailEnabled", is(true)))
                .andExpect(jsonPath("$.smsEnabled", is(true)))
                .andExpect(jsonPath("$.inAppEnabled", is(true)));

        // 2. Update settings (disable SMS)
        NotificationSettings updatedSettings = new NotificationSettings(userId, true, false, true);

        mockMvc.perform(put("/api/notifications/settings/" + userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedSettings)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.smsEnabled", is(false)));

        // 3. Verify updated settings
        mockMvc.perform(get("/api/notifications/settings/" + userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.smsEnabled", is(false)))
                .andExpect(jsonPath("$.emailEnabled", is(true)));
    }

    @Test
    public void testSendNotificationsAndInAppFetch() throws Exception {
        String userId = "test_user_dispatch";

        // 1. Initialize user settings: Email enabled, SMS disabled, In-App enabled
        NotificationSettings settings = new NotificationSettings(userId, true, false, true);
        mockMvc.perform(put("/api/notifications/settings/" + userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(settings)))
                .andExpect(status().isOk());

        // 2. Trigger notification send for WALLET_CREDITED
        SendNotificationRequest request = new SendNotificationRequest(
                userId,
                "WALLET_CREDITED",
                List.of(Channel.EMAIL, Channel.SMS, Channel.IN_APP),
                Map.of(
                        "username", "Alice",
                        "amount", "$50.00",
                        "senderName", "Bob",
                        "balance", "$150.00"
                ),
                "alice@example.com",
                "+15551234"
        );

        // 3. Dispatch the notification
        // For Email, SMS, In-App:
        // - Email should be SENT (enabled)
        // - SMS should be OPTED_OUT (disabled in user settings)
        // - In-App should be SENT (enabled)
        mockMvc.perform(post("/api/notifications/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.EMAIL", is("SENT")))
                .andExpect(jsonPath("$.SMS", is("OPTED_OUT")))
                .andExpect(jsonPath("$.IN_APP", is("SENT")));

        // 4. Retrieve and verify in-app notifications in DB
        // The in-app body template is "Your wallet has been credited with {{amount}} by {{senderName}}."
        // Renders to: "Your wallet has been credited with $50.00 by Bob."
        // Subject is "Account Credited"
        mockMvc.perform(get("/api/notifications/in-app?userId=" + userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title", is("Account Credited")))
                .andExpect(jsonPath("$[0].content", is("Your wallet has been credited with $50.00 by Bob.")))
                .andExpect(jsonPath("$[0].read", is(false)));

        // 5. Mark the in-app notification as read
        mockMvc.perform(get("/api/notifications/in-app?userId=" + userId))
                .andDo(result -> {
                    String content = result.getResponse().getContentAsString();
                    List<Map<String, Object>> list = objectMapper.readValue(content, List.class);
                    Number id = (Number) list.get(0).get("id");

                    mockMvc.perform(post("/api/notifications/in-app/" + id.longValue() + "/read"))
                            .andExpect(status().isOk())
                            .andExpect(jsonPath("$.success", is(true)));
                });

        // 6. Verify in-app notification is marked as read
        mockMvc.perform(get("/api/notifications/in-app?userId=" + userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].read", is(true)));
    }
}
