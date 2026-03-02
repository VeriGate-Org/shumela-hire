package com.arthmatic.shumelahire.dto;

import com.arthmatic.shumelahire.entity.JobBoardType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public class BatchPostRequest {

    @NotBlank(message = "Job posting ID is required")
    private String jobPostingId;

    @NotEmpty(message = "At least one board must be selected")
    private List<BoardSelection> boards;

    public BatchPostRequest() {}

    public String getJobPostingId() { return jobPostingId; }
    public void setJobPostingId(String jobPostingId) { this.jobPostingId = jobPostingId; }

    public List<BoardSelection> getBoards() { return boards; }
    public void setBoards(List<BoardSelection> boards) { this.boards = boards; }

    public static class BoardSelection {
        private JobBoardType boardType;
        private String boardConfig;

        public BoardSelection() {}

        public JobBoardType getBoardType() { return boardType; }
        public void setBoardType(JobBoardType boardType) { this.boardType = boardType; }

        public String getBoardConfig() { return boardConfig; }
        public void setBoardConfig(String boardConfig) { this.boardConfig = boardConfig; }
    }
}
