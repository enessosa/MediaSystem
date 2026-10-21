package de.mediasystem.backend.config;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void authPathIsPermittedWithoutLogin() throws Exception {
        mockMvc.perform(get("/auth/login"))
                .andExpect(status().is(Matchers.not(403)));
    }

    @Test
    void errorPathIsPermittedWithoutLogin() throws Exception {
        mockMvc.perform(get("/error"))
                .andExpect(status().is(Matchers.not(403)));
    }

    @Test
    void unknownProtectedPathIsBlockedWithoutLogin() throws Exception {
        mockMvc.perform(get("/dashboard"))
                .andExpect(status().isForbidden());
    }

    @Test
    void usersMeIsBlockedWithoutLogin() throws Exception {
        mockMvc.perform(get("/users/me"))
                .andExpect(status().isForbidden());
    }

    @Test
    void changePasswordIsBlockedWithoutLogin() throws Exception {
        mockMvc.perform(put("/users/me/password").with(csrf()).contentType("application/json").content("{}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteAccountIsBlockedWithoutLogin() throws Exception {
        mockMvc.perform(delete("/users/me").with(csrf()).contentType("application/json").content("{}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminPathIsBlockedWithoutLogin() throws Exception {
        mockMvc.perform(put("/admin/codeword").with(csrf()).contentType("application/json").content("{}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    void adminPathIsBlockedForPlainUser() throws Exception {
        mockMvc.perform(put("/admin/codeword").with(csrf()).contentType("application/json").content("{}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    void readingCodewordIsBlockedForPlainUser() throws Exception {
        mockMvc.perform(get("/admin/codeword"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    void deletingCodewordIsBlockedForPlainUser() throws Exception {
        mockMvc.perform(delete("/admin/codeword").with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminPathIsNotBlockedForAdmin() throws Exception {
        mockMvc.perform(put("/admin/codeword").with(csrf()).contentType("application/json").content("{}"))
                .andExpect(status().is(Matchers.not(403)));
    }
}