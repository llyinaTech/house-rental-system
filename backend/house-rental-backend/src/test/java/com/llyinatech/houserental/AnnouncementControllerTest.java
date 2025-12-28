package com.llyinatech.houserental;

import com.llyinatech.houserental.controller.AnnouncementController;
import com.llyinatech.houserental.entity.Announcement;
import com.llyinatech.houserental.service.AnnouncementService;
import com.llyinatech.houserental.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AnnouncementController.class)
public class AnnouncementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AnnouncementService announcementService;

    @MockBean
    private UserService userService;

    @Test
    public void testFindAnnouncements() throws Exception {
        mockMvc.perform(get("/announcements")
                .param("pageNum", "1")
                .param("pageSize", "10"))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetPublishedAnnouncements() throws Exception {
        mockMvc.perform(get("/announcements/published"))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetAnnouncementById() throws Exception {
        mockMvc.perform(get("/announcements/1"))
                .andExpect(status().isOk());
    }

    @Test
    public void testAddAnnouncement() throws Exception {
        Announcement announcement = new Announcement();
        announcement.setTitle("Test Title");
        announcement.setContent("Test Content");

        mockMvc.perform(post("/announcements")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"Test Title\",\"content\":\"Test Content\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    public void testUpdateAnnouncement() throws Exception {
        Announcement announcement = new Announcement();
        announcement.setTitle("Updated Title");
        announcement.setContent("Updated Content");

        mockMvc.perform(put("/announcements/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"Updated Title\",\"content\":\"Updated Content\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    public void testDeleteAnnouncement() throws Exception {
        mockMvc.perform(delete("/announcements/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    public void testPublishAnnouncement() throws Exception {
        mockMvc.perform(put("/announcements/publish/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    public void testRevokeAnnouncement() throws Exception {
        mockMvc.perform(put("/announcements/revoke/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}