package com.sombra.cmsapi.businessservice.e2e;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.github.database.rider.core.api.configuration.DBUnit;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.junit5.DBUnitExtension;
import com.github.database.rider.junit5.api.DBRider;
import com.sombra.cmsapi.businessservice.dto.homework.CreateHomeworkRequest;
import com.sombra.cmsapi.businessservice.repository.HomeworkRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.junit.jupiter.Testcontainers;

@DBRider
@Testcontainers
@ExtendWith(DBUnitExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class HomeworkControllerE2ETest extends BaseE2ETest {

  @Autowired private HomeworkRepository homeworkRepository;

  @Test
  @WithMockUser(username = "test", authorities = "ADMIN")
  @DBUnit(caseSensitiveTableNames = true)
  @DataSet(value = "datasets/homeworkController/create.xml", cleanAfter = true, cleanBefore = true)
  public void Should_Create_When_ValidRequest() throws Exception {
    // SETUP
    String url = "/homeworks";

    CreateHomeworkRequest request =
        CreateHomeworkRequest.builder()
            .lessonId("25b75de8-b9c9-11ee-a506-0242ac120002")
            .task("Test task")
            .build();

    // ACT
    mockMvc
        .perform(
            MockMvcRequestBuilders.post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.lesson.id", equalTo("25b75de8-b9c9-11ee-a506-0242ac120002")))
        .andExpect(jsonPath("$.task", equalTo(request.getTask())));

    // VERIFY
    assertEquals(1, homeworkRepository.findAll().size());
  }

  @Test
  @DBUnit(caseSensitiveTableNames = true)
  @DataSet(value = "datasets/homeworkController/getById.xml", cleanAfter = true, cleanBefore = true)
  @WithMockUser(username = "test", authorities = "ADMIN")
  public void Should_getById_When_ValidRequest() throws Exception {
    // SETUP
    String url = "/homeworks/2411dd96-b9ca-11ee-a506-0242ac120002";

    // ACT & VERIFY
    mockMvc
        .perform(MockMvcRequestBuilders.get(url))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id", equalTo("2411dd96-b9ca-11ee-a506-0242ac120002")))
        .andExpect(jsonPath("$.task", equalTo("Test task")))
        .andExpect(jsonPath("$.lesson.id", equalTo("25b75de8-b9c9-11ee-a506-0242ac120002")));
  }

  @Test
  @DBUnit(caseSensitiveTableNames = true)
  @DataSet(value = "datasets/homeworkController/getAll.xml", cleanAfter = true, cleanBefore = true)
  @WithMockUser(username = "test", authorities = "ADMIN")
  public void Should_getAll_When_ValidRequest() throws Exception {
    // SETUP
    String url = "/homeworks";

    // ACT & VERIFY
    mockMvc
        .perform(MockMvcRequestBuilders.get(url))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("totalElements").isNumber())
        .andExpect(jsonPath("totalElements", equalTo(3)))
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.content", hasSize(3)));
  }
}
