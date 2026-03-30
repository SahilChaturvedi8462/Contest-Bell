package com.contestBell.baba.Dto;

import lombok.Data;

@Data
public class CodeforcesContest {
    private int id;
    private String name;
    private String phase;
    private long startTimeSeconds;
    private int durationSeconds;
    private String type;
}
