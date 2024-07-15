package com.itm.space.backendresources;

import com.itm.space.backendresources.api.request.UserRequest;
import com.itm.space.backendresources.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import java.util.UUID;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
public class UserControllerIntegrationTest  extends BaseIntegrationTest{

    @MockBean
    UserService userService;

    @Test
    @WithMockUser(roles = "MODERATOR")
    public void createUserTestWithModerator() throws Exception {
        UserRequest userRequest = new UserRequest("username", "email@example.com", "password", "firstName", "lastName");
        mvc.perform(requestWithContent(post("/api/users"), userRequest))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "MODERATOR")
    public void createUserTestInvalidUsername() throws Exception {
        UserRequest userRequest = new UserRequest("u", "email@example.com", "password", "firstName", "lastName");
        mvc.perform(requestWithContent(post("/api/users"), userRequest))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "MODERATOR")
    public void createUserTestEmailNotBlank() throws Exception {
        UserRequest userRequest = new UserRequest("username", "email@example.com", "", "firstName", "lastName");
        mvc.perform(requestWithContent(post("/api/users"), userRequest))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithAnonymousUser
    public void createTestWithNoAuthentication() throws Exception {
        UserRequest userRequest = new UserRequest("username", "email@example.com", "password", "firstName", "lastName");
        mvc.perform(requestWithContent(post("/api/users"), userRequest))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void createTestWithUser() throws Exception {
        UserRequest userRequest = new UserRequest("username", "email@example.com", "password", "firstName", "lastName");
        mvc.perform(requestWithContent(post("/api/users"), userRequest))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testNotFoundEndpoint() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/api/nonexistent"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "MODERATOR")
    public void getExistingUserByIdTestWithModerator() throws Exception {
        UUID existingUserId = UUID.fromString("89894cd7-433b-4e21-93bb-6e671e661813");
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/api/users/{id}", existingUserId);
        mvc.perform(requestToJson(request))
                .andExpect(status().isOk());
    }


    @Test
    @WithMockUser(roles = "USER")
    public void getExistingUserByIdTestWithUser() throws Exception {
        UUID existingUserId = UUID.fromString("40853195-e690-4f3e-878c-c1bc1b997d59");
        mvc.perform(MockMvcRequestBuilders.get("/api/users/" + existingUserId))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "MODERATOR")
    public void helloTestWithModerator() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/api/users/hello"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void helloTestWithUser() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/api/users/hello"))
                .andExpect(status().isForbidden());
    }

}
