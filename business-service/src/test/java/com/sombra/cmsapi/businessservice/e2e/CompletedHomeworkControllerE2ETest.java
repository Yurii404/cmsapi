package com.sombra.cmsapi.businessservice.e2e;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.github.database.rider.core.api.configuration.DBUnit;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.junit5.DBUnitExtension;
import com.github.database.rider.junit5.api.DBRider;
import com.sombra.cmsapi.businessservice.dto.completedHomework.CheckHomeworkRequest;
import com.sombra.cmsapi.businessservice.dto.completedHomework.CreateCompletedHomeworkRequest;
import com.sombra.cmsapi.businessservice.entity.CompletedHomework;
import com.sombra.cmsapi.businessservice.exception.EntityNotFoundException;
import com.sombra.cmsapi.businessservice.repository.CompletedHomeworkRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.junit.jupiter.Testcontainers;

@DBRider
@Testcontainers
@ExtendWith(DBUnitExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CompletedHomeworkControllerE2ETest extends BaseE2ETest {

  @Autowired private CompletedHomeworkRepository completedHomeworkRepository;

  @Test
  @WithMockUser(username = "test", authorities = "ADMIN")
  @DBUnit(caseSensitiveTableNames = true)
  @DataSet(
      value = "datasets/completedHomeworkController/create.xml",
      cleanAfter = true,
      cleanBefore = true)
  public void Should_Create_When_ValidRequest() throws Exception {
    // SETUP
    String url = "/completed-homeworks";

    CreateCompletedHomeworkRequest requestDto =
        CreateCompletedHomeworkRequest.builder()
            .homeworkId("2411dd96-b9ca-11ee-a506-0242ac120002")
            .studentId("38f595e5-458b-4feb-86c0-ecc929a27005")
            .build();

    MockMultipartFile file =
        new MockMultipartFile(
            "file", "test.txt", MediaType.TEXT_PLAIN_VALUE, "Hello, World!".getBytes());
    MockMultipartFile request =
        new MockMultipartFile(
            "request",
            "",
            MediaType.APPLICATION_JSON_VALUE,
            objectMapper.writeValueAsString(requestDto).getBytes());

    // ACT & VERIFY
    mockMvc
        .perform(multipart(url).file(file).file(request).contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isCreated())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.student.id", equalTo("38f595e5-458b-4feb-86c0-ecc929a27005")))
        .andExpect(jsonPath("$.homework.id", equalTo("2411dd96-b9ca-11ee-a506-0242ac120002")))
        .andExpect(jsonPath("$.homeworkFileUrl").exists());

    // VERIFY
    assertEquals(1, completedHomeworkRepository.findAll().size());
  }

  @Test
  @DBUnit(caseSensitiveTableNames = true)
  @DataSet(
      value = "datasets/completedHomeworkController/getById.xml",
      cleanAfter = true,
      cleanBefore = true)
  @WithMockUser(username = "test", authorities = "ADMIN")
  public void Should_getById_When_ValidRequest() throws Exception {
    // SETUP
    String url = "/completed-homeworks/207d8aa2-b9cc-11ee-a506-0242ac120002";

    // ACT & VERIFY
    mockMvc
        .perform(MockMvcRequestBuilders.get(url))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.student.id", equalTo("38f595e5-458b-4feb-86c0-ecc929a27005")))
        .andExpect(jsonPath("$.instructor.id", equalTo("38a595e5-458b-4feb-86c0-ecc929a27005")))
        .andExpect(jsonPath("$.homework.id", equalTo("2411dd96-b9ca-11ee-a506-0242ac120002")))
        .andExpect(jsonPath("$.comment", equalTo("Test comment")))
        .andExpect(jsonPath("$.mark", equalTo(100)))
        .andExpect(jsonPath("$.homeworkFileUrl").exists());
  }

  @Test
  @DBUnit(caseSensitiveTableNames = true)
  @DataSet(
      value = "datasets/completedHomeworkController/getAll.xml",
      cleanAfter = true,
      cleanBefore = true)
  @WithMockUser(username = "test", authorities = "ADMIN")
  public void Should_getAll_When_ValidRequest() throws Exception {
    // SETUP
    String url = "/completed-homeworks";

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

  @Test
  @WithMockUser(username = "test", authorities = "ADMIN")
  @DBUnit(caseSensitiveTableNames = true)
  @DataSet(
      value = "datasets/completedHomeworkController/checkHomework.xml",
      cleanAfter = true,
      cleanBefore = true)
  public void Should_CheckHomework_When_ValidRequest() throws Exception {
    // SETUP
    String url = "/completed-homeworks/207d8aa2-b9cc-11ee-a506-0242ac120002";

    CheckHomeworkRequest request =
        CheckHomeworkRequest.builder()
            .comment("Test comment")
            .mark(78)
            .instructorId("38a595e5-458b-4feb-86c0-ecc929a27005")
            .build();

    // ACT & VERIFY
    mockMvc
        .perform(
            MockMvcRequestBuilders.put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id", equalTo("207d8aa2-b9cc-11ee-a506-0242ac120002")))
        .andExpect(jsonPath("$.instructor.id", equalTo(request.getInstructorId())))
        .andExpect(jsonPath("$.mark", equalTo(request.getMark())))
        .andExpect(jsonPath("$.comment", equalTo(request.getComment())));

    // VERIFY
    CompletedHomework completedHomeworkFromRepoUpdated =
        completedHomeworkRepository
            .findById("207d8aa2-b9cc-11ee-a506-0242ac120002")
            .orElseThrow(() -> new EntityNotFoundException("Entity not found"));

    assertEquals(
        completedHomeworkFromRepoUpdated.getInstructor().getId(), request.getInstructorId());
    assertEquals(completedHomeworkFromRepoUpdated.getMark(), request.getMark());
    assertEquals(completedHomeworkFromRepoUpdated.getComment(), request.getComment());
  }
}
