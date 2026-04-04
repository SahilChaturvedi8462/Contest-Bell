package com.contestBell.baba.Utils;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class PlatformMapper {
    private static final Map<String, String> PLATFORM_MAP = Map.of(
            "codechef.com", "CODECHEF",
            "leetcode.com", "LEETCODE",
            "atcoder.jp", "ATCODER",
            "hackerrank.com", "HACKERRANK",
            "codeforces.com", "CODEFORCES"
    );

    public String getPlatformName(String resource) {
        return PLATFORM_MAP.getOrDefault(resource, resource.toUpperCase());
    }
}
