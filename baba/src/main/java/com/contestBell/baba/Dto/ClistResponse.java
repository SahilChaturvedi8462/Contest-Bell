package com.contestBell.baba.Dto;

import lombok.Data;

import java.util.List;

@Data
public class ClistResponse {
    private ClistMeta meta;
    private List<ClistContest> objects;
}
