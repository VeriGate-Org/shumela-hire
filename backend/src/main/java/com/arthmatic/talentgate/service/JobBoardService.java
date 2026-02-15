package com.arthmatic.talentgate.service;

import com.arthmatic.talentgate.entity.JobBoardPosting;
import com.arthmatic.talentgate.entity.JobBoardType;

import java.util.List;

public interface JobBoardService {

    JobBoardPosting postToBoard(String jobPostingId, JobBoardType boardType, String boardConfig);

    JobBoardPosting removePosting(Long postingId);

    JobBoardPosting syncPosting(Long postingId);

    List<JobBoardPosting> getPostingsByJob(String jobPostingId);
}
