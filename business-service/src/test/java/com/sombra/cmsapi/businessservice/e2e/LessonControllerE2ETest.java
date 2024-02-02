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
import com.sombra.cmsapi.businessservice.dto.lesson.CreateLessonRequest;
import com.sombra.cmsapi.businessservice.dto.lesson.UpdateLessonRequest;
import com.sombra.cmsapi.businessservice.entity.Lesson;
import com.sombra.cmsapi.businessservice.exception.EntityNotFoundException;
import com.sombra.cmsapi.businessservice.repository.LessonRepository;
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
class LessonControllerE2ETest extends BaseE2ETest {

  @Autowired private LessonRepository lessonRepository;

  @Test
  @WithMockUser(username = "test", authorities = "ADMIN")
  @DBUnit(caseSensitiveTableNames = true)
  @DataSet(value = "datasets/lessonController/create.xml", cleanAfter = true, cleanBefore = true)
  public void Should_Create_When_ValidRequest() throws Exception {
    // SETUP
    String url = "/lessons";

    CreateLessonRequest request =
        CreateLessonRequest.builder()
            .courseId("5cd171b4-2775-4667-94e9-f0c7aa2e2cd9")
            .content("Content")
            .title("Title")
            .build();

    // ACT
    mockMvc
        .perform(
            MockMvcRequestBuilders.post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.content", equalTo(request.getContent())))
        .andExpect(jsonPath("$.course.id", equalTo(request.getCourseId())))
        .andExpect(jsonPath("$.title", equalTo(request.getTitle())));

    // VERIFY
    assertEquals(1, lessonRepository.findAll().size());
  }

  @Test
  @WithMockUser(username = "test", authorities = "ADMIN")
  @DBUnit(caseSensitiveTableNames = true)
  @DataSet(value = "datasets/lessonController/update.xml", cleanAfter = true, cleanBefore = true)
  public void Should_Update_When_ValidRequest() throws Exception {
    // SETUP
    String url = "/lessons";

    UpdateLessonRequest request =
        UpdateLessonRequest.builder()
            .id("25b75de8-b9c9-11ee-a506-0242ac120002")
            .title("Test title updated")
            .content("Test content updated")
            .build();

    // ACT & VERIFY
    mockMvc
        .perform(
            MockMvcRequestBuilders.put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id", equalTo(request.getId())))
        .andExpect(jsonPath("$.content", equalTo(request.getContent())))
        .andExpect(jsonPath("$.title", equalTo(request.getTitle())));

    // VERIFY
    Lesson lessonFromRepoUpdated =
        lessonRepository
            .findById("25b75de8-b9c9-11ee-a506-0242ac120002")
            .orElseThrow(() -> new EntityNotFoundException("Entity not found"));

    assertEquals(lessonFromRepoUpdated.getTitle(), request.getTitle());
    assertEquals(lessonFromRepoUpdated.getContent(), request.getContent());
  }

  @Test
  @DBUnit(caseSensitiveTableNames = true)
  @DataSet(value = "datasets/lessonController/getById.xml", cleanAfter = true, cleanBefore = true)
  @WithMockUser(username = "test", authorities = "ADMIN")
  public void Should_getById_When_ValidRequest() throws Exception {
    // SETUP
    String url = "/lessons/25b75de8-b9c9-11ee-a506-0242ac120002";

    // ACT & VERIFY
    mockMvc
        .perform(MockMvcRequestBuilders.get(url))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id", equalTo("25b75de8-b9c9-11ee-a506-0242ac120002")))
        .andExpect(jsonPath("$.content", equalTo("Test content")))
        .andExpect(jsonPath("$.title", equalTo("Test title")))
        .andExpect(jsonPath("$.course.id", equalTo("5cd171b4-2775-4667-94e9-f0c7aa2e2cd9")));
  }

  @Test
  @DBUnit(caseSensitiveTableNames = true)
  @DataSet(value = "datasets/lessonController/getAll.xml", cleanAfter = true, cleanBefore = true)
  @WithMockUser(username = "test", authorities = "ADMIN")
  public void Should_getAll_When_ValidRequest() throws Exception {
    // SETUP
    String url = "/lessons";

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
