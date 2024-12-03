package com.example.salonreservation.domain.member.dto;

import com.example.salonreservation.domain.member.entity.Member;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ProfileDto {
    private String name;
    private String email;
    private String birth;
    private String gender;

    public static ProfileDto fromEntity(Member member) {
        return new ProfileDto(member.getName(), member.getEmail(), member.getBirth(), member.getGender());
    }
}
