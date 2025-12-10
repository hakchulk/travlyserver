package com.study.travly.board;

import java.time.LocalDateTime;



public interface WeeklyTopBoardTempDTO {
    Long getId();
    String getTitle();
    LocalDateTime getCreatedAt();
    Long getViewCount();

    Long getMemberId();
    String getMemberName();
    Long getBadgeId();
    Long getProfileImg();
    
    Long getCardImg();
    
    Long getLikeCount();
    String getContent();
}
