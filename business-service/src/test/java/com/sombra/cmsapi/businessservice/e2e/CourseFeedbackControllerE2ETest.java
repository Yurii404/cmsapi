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
import com.sombra.cmsapi.businessservice.dto.courseFeedback.CreateCourseFeedbackRequest;
import com.sombra.cmsapi.businessservice.dto.courseFeedback.LeaveCommentRequest;
import com.sombra.cmsapi.businessservice.entity.CourseFeedback;
import com.sombra.cmsapi.businessservice.exception.EntityNotFoundException;
import com.sombra.cmsapi.businessservice.repository.CourseFeedbackRepository;
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
class CourseFeedbackControllerE2ETest extends BaseE2ETest {

  @Autowired private CourseFeedbackRepository courseFeedbackRepository;

  @Test
  @WithMockUser(username = "test", authorities = "ADMIN")
  @DBUnit(caseSensitiveTableNames = true)
  @DataSet(
      value = "datasets/courseFeedbackController/create.xml",
      cleanAfter = true,
      cleanBefore = true)
  public void Should_Create_When_ValidRequest() throws Exception {
    // SETUP
    String url = "/course-feedbacks";

    CreateCourseFeedbackRequest request =
        CreateCourseFeedbackRequest.builder()
            .courseId("5cd171b4-2775-4667-94e9-f0c7aa2e2cd9")
            .studentId("38f595e5-458b-4feb-86c0-ecc929a27005")
            .build();

    // ACT & VERIFY
    mockMvc
        .perform(
            MockMvcRequestBuilders.post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.student.id", equalTo("38f595e5-458b-4feb-86c0-ecc929a27005")))
        .andExpect(jsonPath("$.course.id", equalTo("5cd171b4-2775-4667-94e9-f0c7aa2e2cd9")))
        .andExpect(jsonPath("$.status", equalTo("PASSED")))
        .andExpect(jsonPath("$.finalMark", equalTo(100)));

    // VERIFY
    assertEquals(1, courseFeedbackRepository.findAll().size());
  }

  @Test
  @WithMockUser(username = "test", authorities = "ADMIN")
  @DBUnit(caseSensitiveTableNames = true)
  @DataSet(
      value = "datasets/courseFeedbackController/leaveFeedback.xml",
      cleanAfter = true,
      cleanBefore = true)
  public void Should_LeaveFeedback_When_ValidRequest() throws Exception {
    // SETUP
    String url = "/course-feedbacks/9177421b-e380-4a05-be9e-82a5e90298d1";

    LeaveCommentRequest request =
        LeaveCommentRequest.builder()
            .instructorId("38a595e5-458b-4feb-86c0-ecc929a27005")
            .comment("Test comment")
            .build();

    // ACT & VERIFY
    mockMvc
        .perform(
            MockMvcRequestBuilders.put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id", equalTo("9177421b-e380-4a05-be9e-82a5e90298d1")))
        .andExpect(jsonPath("$.instructor.id", equalTo(request.getInstructorId())))
        .andExpect(jsonPath("$.comment", equalTo(request.getComment())));

    // VERIFY
    CourseFeedback courseFeedbackFromRepoUpdated =
        courseFeedbackRepository
            .findById("9177421b-e380-4a05-be9e-82a5e90298d1")
            .orElseThrow(() -> new EntityNotFoundException("Entity not found"));

    assertEquals(courseFeedbackFromRepoUpdated.getInstructor().getId(), request.getInstructorId());
    assertEquals(courseFeedbackFromRepoUpdated.getComment(), request.getComment());
  }

  @Test
  @DBUnit(caseSensitiveTableNames = true)
  @DataSet(
      value = "datasets/courseFeedbackController/getById.xml",
      cleanAfter = true,
      cleanBefore = true)
  @WithMockUser(username = "test", authorities = "ADMIN")
  public void Should_getById_When_ValidRequest() throws Exception {
    // SETUP
    String url = "/course-feedbacks/9177421b-e380-4a05-be9e-82a5e90298d1";

    // ACT & VERIFY
    mockMvc
        .perform(MockMvcRequestBuilders.get(url))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.student.id", equalTo("38f595e5-458b-4feb-86c0-ecc929a27005")))
        .andExpect(jsonPath("$.course.id", equalTo("5cd171b4-2775-4667-94e9-f0c7aa2e2cd9")))
        .andExpect(jsonPath("$.status", equalTo("PASSED")))
        .andExpect(jsonPath("$.comment", equalTo("Test comment")))
        .andExpect(jsonPath("$.finalMark", equalTo(100)));
  }

  @Test
  @DBUnit(caseSensitiveTableNames = true)
  @DataSet(
      value = "datasets/courseFeedbackController/getAll.xml",
      cleanAfter = true,
      cleanBefore = true)
  @WithMockUser(username = "test", authorities = "ADMIN")
  public void Should_getAll_When_ValidRequest() throws Exception {
    // SETUP
    String url = "/course-feedbacks";

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
