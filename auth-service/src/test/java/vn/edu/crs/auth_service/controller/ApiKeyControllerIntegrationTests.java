package vn.edu.crs.auth_service.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import vn.edu.crs.auth_service.repository.ApiKeyRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ApiKeyControllerIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ApiKeyRepository apiKeyRepository;

    @BeforeEach
    void clearApiKeys() {
        apiKeyRepository.deleteAll();
    }

    @Test
    void adminCanCreateListAndRevokeKey() throws Exception {
        String adminToken = login("admin", "admin123");
        String studentToken = login("student1", "student123");

        mockMvc.perform(get("/api-keys")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isForbidden());

        String createdBody = mockMvc.perform(post("/api-keys")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "ownerName": "Integration Partner",
                                  "scopes": "courses:read",
                                  "validDays": 30
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.keyValue").isString())
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode created = objectMapper.readTree(createdBody);
        long id = created.get("id").asLong();
        String secret = created.get("keyValue").asText();
        assertThat(secret).startsWith("crs_");

        mockMvc.perform(get("/api-keys")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].keyValue").doesNotExist());

        mockMvc.perform(get("/internal/api-keys/validate")
                        .param("key", secret)
                        .param("scope", "courses:read"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(true));

        mockMvc.perform(delete("/api-keys/{id}", id)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("REVOKED"))
                .andExpect(jsonPath("$.keyValue").doesNotExist());

        mockMvc.perform(get("/internal/api-keys/validate")
                        .param("key", secret)
                        .param("scope", "courses:read"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(false));
    }

    private String login(String username, String password) throws Exception {
        String responseBody = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginPayload(username, password))))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        return objectMapper.readTree(responseBody).get("token").asText();
    }

    private record LoginPayload(String username, String password) {
    }
}
