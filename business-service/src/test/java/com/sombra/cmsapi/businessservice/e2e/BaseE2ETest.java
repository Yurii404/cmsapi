package com.sombra.cmsapi.businessservice.e2e;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sombra.cmsapi.businessservice.config.TestContainerConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@AutoConfigureMockMvc
public class BaseE2ETest extends TestContainerConfiguration {

  @Autowired protected MockMvc mockMvc;
  @Autowired protected ObjectMapper objectMapper;
}
