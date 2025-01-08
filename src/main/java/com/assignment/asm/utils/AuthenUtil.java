package com.assignment.asm.utils;

import org.springframework.security.core.context.SecurityContextHolder;

public class AuthenUtil {
    public static String getProfileId(){
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }
}
