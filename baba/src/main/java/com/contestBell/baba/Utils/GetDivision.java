package com.contestBell.baba.Utils;

import org.springframework.stereotype.Component;

@Component
public class GetDivision {

    public String extractDivision(String name) {
        if (name == null) return "OTHER";
        String lower = name.toLowerCase();

        //Codeforces
        if (name.contains("Div. 1") && name.contains("Div. 2")) return "DIV_1_2";
        if (name.contains("Div. 1")) return "DIV_1";
        if (name.contains("Div. 2")) return "DIV_2";
        if (name.contains("Div. 3")) return "DIV_3";
        if (name.contains("Div. 4")) return "DIV_4";
        if (name.contains("Educational")) return "EDUCATIONAL";
        if (name.contains("Global")) return "GLOBAL";

        // LeetCode
        if (lower.contains("biweekly")) return "BIWEEKLY";
        if (lower.contains("weekly")) return "WEEKLY";

        // CodeChef
        if (lower.contains("starters")) return "STARTERS";
        if (lower.contains("cook-off") || lower.contains("cookoff")) return "COOK_OFF";
        if (lower.contains("lunchtime")) return "LUNCHTIME";
        if (lower.contains("long challenge")) return "LONG_CHALLENGE";

        // AtCoder
        if (lower.contains("abc") || lower.contains("beginner")) return "BEGINNER";
        if (lower.contains("arc") || lower.contains("regular")) return "REGULAR";
        if (lower.contains("agc") || lower.contains("grand")) return "GRAND";
        if (lower.contains("ahc") || lower.contains("heuristic")) return "HEURISTIC";

        return "OTHER";
    }
}
