package com.example.jenkinscrud.student;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(StudentController.class)
class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private StudentService studentService;

    @Test
    void createStudent() throws Exception {
        Student request = new Student(null, "Dax", "dax@example.com", "Java");
        Student response = new Student(1L, "Dax", "dax@example.com", "Java");

        given(studentService.create(any(Student.class))).willReturn(response);

        mockMvc.perform(post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Dax"))
                .andExpect(jsonPath("$.email").value("dax@example.com"))
                .andExpect(jsonPath("$.course").value("Java"));
    }

    @Test
    void getAllStudents() throws Exception {
        given(studentService.findAll()).willReturn(List.of(
                new Student(1L, "Dax", "dax@example.com", "Java"),
                new Student(2L, "Patel", "patel@example.com", "Spring Boot")));

        mockMvc.perform(get("/api/students"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Dax"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].course").value("Spring Boot"));
    }

    @Test
    void getStudentById() throws Exception {
        given(studentService.findById(1L)).willReturn(new Student(1L, "Dax", "dax@example.com", "Java"));

        mockMvc.perform(get("/api/students/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Dax"));
    }

    @Test
    void updateStudent() throws Exception {
        Student request = new Student(null, "Dax Updated", "dax.updated@example.com", "AWS");
        Student response = new Student(1L, "Dax Updated", "dax.updated@example.com", "AWS");

        given(studentService.update(eq(1L), any(Student.class))).willReturn(response);

        mockMvc.perform(put("/api/students/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Dax Updated"))
                .andExpect(jsonPath("$.course").value("AWS"));
    }

    @Test
    void deleteStudent() throws Exception {
        doNothing().when(studentService).delete(1L);

        mockMvc.perform(delete("/api/students/1"))
                .andExpect(status().isNoContent());
    }
}