package com.contestBell.baba.Dto;

import lombok.Data;

@Data
public class ClistContest {
    private long id;
    private String event;
    private String resource;
    private String href;            //contest url
    private String start;
    private String end;
    private double duration;        // in seconds
}
