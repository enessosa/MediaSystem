package de.mediasystem.backend.config;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void authPathIsPermittedWithoutLogin() throws Exception {
        mockMvc.perform(get("/auth/login"))
                .andExpect(status().isNotFound());
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
}