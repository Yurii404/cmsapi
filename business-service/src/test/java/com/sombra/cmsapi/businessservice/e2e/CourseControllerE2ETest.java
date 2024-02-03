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
import com.sombra.cmsapi.businessservice.dto.course.CreateCourseRequest;
import com.sombra.cmsapi.businessservice.repository.CourseRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
class CourseControllerE2ETest extends BaseE2ETest {

  @Autowired private CourseRepository courseRepository;

  @Test
  @WithMockUser(username = "test", authorities = "ADMIN")
  @DBUnit(caseSensitiveTableNames = true)
  @DataSet(value = "datasets/courseController/create.xml", cleanAfter = true, cleanBefore = true)
  public void Should_Create_When_ValidRequest() throws Exception {
    // SETUP
    String url = "/courses";

    CreateCourseRequest request =
        CreateCourseRequest.builder()
            .name("Test course")
            .description("Test description")
            .instructorIds(Collections.singletonList("38a595e5-458b-4feb-86c0-ecc929a27005"))
            .lessonIds(
                new ArrayList<>(
                    List.of(
                        "25b75de8-b9c9-11ee-a506-0242ac120002",
                        "35b75de8-b9c9-11ee-a506-0242ac120002",
                        "45b75de8-b9c9-11ee-a506-0242ac120002",
                        "45b75de8-b9c9-11ee-a506-0242ac120002",
                        "65b75de8-b9c9-11ee-a506-0242ac120002")))
            .build();

    // ACT
    mockMvc
        .perform(
            MockMvcRequestBuilders.post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.name", equalTo(request.getName())))
        .andExpect(jsonPath("$.description", equalTo(request.getDescription())))
        .andExpect(jsonPath("$.instructors").isArray())
        .andExpect(jsonPath("$.lessons").isArray())
        .andExpect(jsonPath("$.instructors", hasSize(1)))
        .andExpect(jsonPath("$.lessons", hasSize(5)));

    // VERIFY
    assertEquals(1, courseRepository.findAll().size());
  }

  @Test
  @DBUnit(caseSensitiveTableNames = true)
  @DataSet(value = "datasets/courseController/getById.xml", cleanAfter = true, cleanBefore = true)
  @WithMockUser(username = "test", authorities = "ADMIN")
  public void Should_getById_When_ValidRequest() throws Exception {
    // SETUP
    String url = "/courses/5cd171b4-2775-4667-94e9-f0c7aa2e2cd9";

    // ACT & VERIFY
    mockMvc
        .perform(MockMvcRequestBuilders.get(url))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id", equalTo("5cd171b4-2775-4667-94e9-f0c7aa2e2cd9")))
        .andExpect(jsonPath("$.description", equalTo("Course for test course getting")))
        .andExpect(jsonPath("$.name", equalTo("Create Lesson")));
  }

  @Test
  @DBUnit(caseSensitiveTableNames = true)
  @DataSet(value = "datasets/courseController/getAll.xml", cleanAfter = true, cleanBefore = true)
  @WithMockUser(username = "test", authorities = "ADMIN")
  public void Should_getAll_When_ValidRequest() throws Exception {
    // SETUP
    String url = "/courses";

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
  @DBUnit(caseSensitiveTableNames = true)
  @DataSet(
      value = "datasets/courseController/getStudentsByCourseId.xml",
      cleanAfter = true,
      cleanBefore = true)
  @WithMockUser(username = "test", authorities = "ADMIN")
  public void Should_GetStudentsByCourseId_When_ValidRequest() throws Exception {
    // SETUP
    String url = "/courses/5cd171b4-2775-4667-94e9-f0c7aa2e2cd9/students";

    // ACT & VERIFY
    mockMvc
        .perform(MockMvcRequestBuilders.get(url))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$", hasSize(2)));
  }

  @Test
  @DBUnit(caseSensitiveTableNames = true)
  @DataSet(
      value = "datasets/courseController/getLessonsByCourseId.xml",
      cleanAfter = true,
      cleanBefore = true)
  @WithMockUser(username = "test", authorities = "ADMIN")
  public void Should_GetLessonsByCourseId_When_ValidRequest() throws Exception {
    // SETUP
    String url = "/courses/5cd171b4-2775-4667-94e9-f0c7aa2e2cd9/lessons";

    // ACT & VERIFY
    mockMvc
        .perform(MockMvcRequestBuilders.get(url))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$", hasSize(5)));
  }

  @Test
  @DBUnit(caseSensitiveTableNames = true)
  @DataSet(
      value = "datasets/courseController/searchByStudentId.xml",
      cleanAfter = true,
      cleanBefore = true)
  @WithMockUser(username = "test", authorities = "ADMIN")
  public void Should_SearchByStudentId_When_ValidRequest() throws Exception {
    // SETUP
    String url = "/courses/search";

    // ACT & VERIFY
    mockMvc
        .perform(
            MockMvcRequestBuilders.get(url)
                .param("searchField", "studentId")
                .param("searchQuery", "37a595e5-458b-4feb-86c0-ecc929a27005"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("totalElements").isNumber())
        .andExpect(jsonPath("totalElements", equalTo(1)))
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.content", hasSize(1)));
  }

  @Test
  @DBUnit(caseSensitiveTableNames = true)
  @DataSet(
      value = "datasets/courseController/searchByInstructorId.xml",
      cleanAfter = true,
      cleanBefore = true)
  @WithMockUser(username = "test", authorities = "ADMIN")
  public void Should_SearchByInstructorId_When_ValidRequest() throws Exception {
    // SETUP
    String url = "/courses/search";

    // ACT & VERIFY
    mockMvc
        .perform(
            MockMvcRequestBuilders.get(url)
                .param("searchField", "instructorId")
                .param("searchQuery", "37a595e5-458b-4feb-86c0-ecc929a27005"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("totalElements").isNumber())
        .andExpect(jsonPath("totalElements", equalTo(1)))
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.content", hasSize(1)));
  }

  @Test
  @DBUnit(caseSensitiveTableNames = true)
  @DataSet(
      value = "datasets/courseController/assignInstructor.xml",
      cleanAfter = true,
      cleanBefore = true)
  @WithMockUser(username = "test", authorities = "ADMIN")
  public void Should_AssignInstructor_When_ValidRequest() throws Exception {
    // SETUP
    String url =
        "/courses/5cd171b4-2775-4667-94e9-f0c7aa2e2cd9/instructors/assign/37a595e5-458b-4feb-86c0-ecc929a27005";

    // ACT & VERIFY
    mockMvc
        .perform(MockMvcRequestBuilders.put(url))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.instructors").isArray())
        .andExpect(jsonPath("$.id", equalTo("5cd171b4-2775-4667-94e9-f0c7aa2e2cd9")))
        .andExpect(jsonPath("$.instructors", hasSize(1)))
        .andExpect(
            jsonPath("$.instructors[0].id", equalTo("37a595e5-458b-4feb-86c0-ecc929a27005")));
  }

  @Test
  @DBUnit(caseSensitiveTableNames = true)
  @DataSet(
      value = "datasets/courseController/withdrawInstructor.xml",
      cleanAfter = true,
      cleanBefore = true)
  @WithMockUser(username = "test", authorities = "ADMIN")
  public void Should_WithdrawInstructor_When_ValidRequest() throws Exception {
    // SETUP
    String url =
        "/courses/5cd171b4-2775-4667-94e9-f0c7aa2e2cd9/instructors/withdraw/37a595e5-458b-4feb-86c0-ecc929a27005";

    // ACT & VERIFY
    mockMvc
        .perform(MockMvcRequestBuilders.put(url))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.instructors").isArray())
        .andExpect(jsonPath("$.id", equalTo("5cd171b4-2775-4667-94e9-f0c7aa2e2cd9")))
        .andExpect(jsonPath("$.instructors", hasSize(1)));
  }

  @Test
  @DBUnit(caseSensitiveTableNames = true)
  @DataSet(
      value = "datasets/courseController/registerStudent.xml",
      cleanAfter = true,
      cleanBefore = true)
  @WithMockUser(username = "test", authorities = "ADMIN")
  public void Should_RegisterStudent_When_ValidRequest() throws Exception {
    // SETUP
    String url =
        "/courses/5cd171b4-2775-4667-94e9-f0c7aa2e2cd9/students/register/38f595e5-458b-4feb-86c0-ecc929a27005";

    // ACT & VERIFY
    mockMvc
        .perform(MockMvcRequestBuilders.put(url))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.students").isArray())
        .andExpect(jsonPath("$.id", equalTo("5cd171b4-2775-4667-94e9-f0c7aa2e2cd9")))
        .andExpect(jsonPath("$.students", hasSize(1)))
        .andExpect(jsonPath("$.students[0].id", equalTo("38f595e5-458b-4feb-86c0-ecc929a27005")));
  }

  @Test
  @DBUnit(caseSensitiveTableNames = true)
  @DataSet(
      value = "datasets/courseController/removeStudent.xml",
      cleanAfter = true,
      cleanBefore = true)
  @WithMockUser(username = "test", authorities = "ADMIN")
  public void Should_RemoveStudent_When_ValidRequest() throws Exception {
    // SETUP
    String url =
        "/courses/5cd171b4-2775-4667-94e9-f0c7aa2e2cd9/students/remove/38f595e5-458b-4feb-86c0-ecc929a27005";

    // ACT & VERIFY
    mockMvc
        .perform(MockMvcRequestBuilders.put(url))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.students").isArray())
        .andExpect(jsonPath("$.id", equalTo("5cd171b4-2775-4667-94e9-f0c7aa2e2cd9")))
        .andExpect(jsonPath("$.students", hasSize(0)));
  }
}
