package com.example.salonreservation.domain.member.util;

import org.springframework.stereotype.Component;

@Component
public class SecurityContextHolder {

    private static final ThreadLocal<Long> memberIdStorage = new ThreadLocal<>();

    public Long setContext(Long memberId) {
        memberIdStorage.set(memberId);
        return memberIdStorage.get();
    }

    public static Long getContext() {
        return memberIdStorage.get();
    }

    public void clearContext() {
        memberIdStorage.remove();
    }

}
