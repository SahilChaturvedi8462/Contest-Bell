package com.contestBell.baba.Dto;

import lombok.Data;

import java.util.List;

@Data
public class CodeForcesResponse {
    private String status;
    private List<CodeforcesContest> result;
}
