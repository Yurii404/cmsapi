package com.sombra.cmsapi.businessservice.e2e;

import static org.assertj.core.api.Assertions.assertThat;
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
import com.sombra.cmsapi.businessservice.dto.user.ChangeUserRoleDto;
import com.sombra.cmsapi.businessservice.dto.user.UserRegisterDto;
import com.sombra.cmsapi.businessservice.entity.User;
import com.sombra.cmsapi.businessservice.enumerated.UserRole;
import com.sombra.cmsapi.businessservice.exception.EntityNotFoundException;
import com.sombra.cmsapi.businessservice.repository.UserRepository;
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
class UserControllerE2ETest extends BaseE2ETest {

  @Autowired private UserRepository userRepository;

  @Test
  public void Should_RegisterUser_When_ValidRequest() throws Exception {
    // SETUP
    String url = "/users/register";

    UserRegisterDto userRegisterDto =
        UserRegisterDto.builder()
            .email("userRegisterTest@mail.com")
            .firstName("John")
            .lastName("Doe")
            .role(UserRole.STUDENT)
            .password("password")
            .build();

    // ACT
    mockMvc
        .perform(
            MockMvcRequestBuilders.post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRegisterDto)))
        .andExpect(status().isCreated())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.firstName", equalTo(userRegisterDto.getFirstName())))
        .andExpect(jsonPath("$.lastName", equalTo(userRegisterDto.getLastName())))
        .andExpect(jsonPath("$.email", equalTo(userRegisterDto.getEmail())))
        .andExpect(jsonPath("$.role", equalTo(userRegisterDto.getRole().toString())));

    // VERIFY
    assertThat(userRepository.findByEmail(userRegisterDto.getEmail())).isPresent();
  }

  @Test
  @DBUnit(caseSensitiveTableNames = true)
  @DataSet(value = "datasets/userController/changeRole.xml", cleanAfter = true, cleanBefore = true)
  @WithMockUser(username = "test", authorities = "ADMIN")
  public void Should_ChangeUserRole_When_ValidRequest() throws Exception {
    // SETUP
    String url = "/users/role";

    ChangeUserRoleDto changeUserRoleDto =
        ChangeUserRoleDto.builder()
            .id("38f595e5-458b-4feb-86c0-ecc929a27005")
            .newRole(UserRole.INSTRUCTOR)
            .build();

    // ACT & VERIFY
    mockMvc
        .perform(
            MockMvcRequestBuilders.put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(changeUserRoleDto)))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id", equalTo("38f595e5-458b-4feb-86c0-ecc929a27005")))
        .andExpect(jsonPath("$.role", equalTo(changeUserRoleDto.getNewRole().toString())));

    User userFromRepoUpdated =
        userRepository
            .findById("38f595e5-458b-4feb-86c0-ecc929a27005")
            .orElseThrow(() -> new EntityNotFoundException("Entity not found"));

    assertEquals(userFromRepoUpdated.getRole(), changeUserRoleDto.getNewRole());
  }

  @Test
  @DBUnit(caseSensitiveTableNames = true)
  @DataSet(value = "datasets/userController/getById.xml", cleanAfter = true, cleanBefore = true)
  @WithMockUser(username = "test", authorities = "ADMIN")
  public void Should_getById_When_ValidRequest() throws Exception {
    // SETUP
    String url = "/users/38f595e5-458b-4feb-86c0-ecc929a27005";

    // ACT & VERIFY
    mockMvc
        .perform(MockMvcRequestBuilders.get(url))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id", equalTo("38f595e5-458b-4feb-86c0-ecc929a27005")))
        .andExpect(jsonPath("$.firstName", equalTo("John")))
        .andExpect(jsonPath("$.lastName", equalTo("Doe")))
        .andExpect(jsonPath("$.email", equalTo("userToGetById@gmail.com")))
        .andExpect(jsonPath("$.role", equalTo("ADMIN")));
  }

  @Test
  @DBUnit(caseSensitiveTableNames = true)
  @DataSet(value = "datasets/userController/getAll.xml", cleanAfter = true, cleanBefore = true)
  @WithMockUser(username = "test", authorities = "ADMIN")
  public void Should_getAll_When_ValidRequest() throws Exception {
    // SETUP
    String url = "/users";

    // ACT & VERIFY
    mockMvc
        .perform(MockMvcRequestBuilders.get(url))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("totalElements").isNumber())
        .andExpect(jsonPath("totalElements", equalTo(2)))
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.content", hasSize(2)));
  }
}
