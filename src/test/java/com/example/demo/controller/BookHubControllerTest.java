package com.example.demo.controller;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class BookHubControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturnPagedBookList() throws Exception {
        mockMvc.perform(get("/book/list")
                        .param("pageNum", "1")
                        .param("pageSize", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(0)))
                .andExpect(jsonPath("$.message", is("success")))
                .andExpect(jsonPath("$.data.records", hasSize(greaterThan(0))))
                .andExpect(jsonPath("$.data.total", greaterThan(0)));
    }

    @Test
    void shouldFilterBookListByKeyword() throws Exception {
        mockMvc.perform(get("/book/list")
                        .param("keyword", "Pride")
                        .param("pageNum", "1")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(0)))
                .andExpect(jsonPath("$.data.records[0].title", is("Pride and Prejudice")));
    }

    @Test
    void shouldReturnBookDetail() throws Exception {
        mockMvc.perform(get("/book/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(0)))
                .andExpect(jsonPath("$.data.id", is(1)))
                .andExpect(jsonPath("$.data.title", is("Pride and Prejudice")));
    }

    @Test
    void shouldReturnBookReadInfo() throws Exception {
        mockMvc.perform(get("/book/read/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(0)))
                .andExpect(jsonPath("$.data.fileType", is("html")))
                .andExpect(jsonPath("$.data.readUrl").isNotEmpty());
    }

    @Test
    void shouldTriggerBookSync() throws Exception {
        mockMvc.perform(post("/admin/book/sync")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(0)))
                .andExpect(jsonPath("$.data.successCount", greaterThan(0)))
                .andExpect(jsonPath("$.data.message", is("同步完成")));
    }

    @Test
    void shouldUpdateBookStatus() throws Exception {
        mockMvc.perform(put("/admin/book/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":1,\"status\":0}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(0)))
                .andExpect(jsonPath("$.data.id", is(1)))
                .andExpect(jsonPath("$.data.status", is(0)));
    }

    @Test
    void shouldReturnCategoryList() throws Exception {
        mockMvc.perform(get("/book/category/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(0)))
                .andExpect(jsonPath("$.data", hasSize(greaterThan(0))))
                .andExpect(jsonPath("$.data[0].name").isNotEmpty());
    }

    @Test
    void shouldReturnSyncLogList() throws Exception {
        mockMvc.perform(get("/admin/book/sync/log/list")
                        .param("pageNum", "1")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(0)))
                .andExpect(jsonPath("$.data.records", hasSize(greaterThan(0))))
                .andExpect(jsonPath("$.data.records[0].source").isNotEmpty());
    }
}
