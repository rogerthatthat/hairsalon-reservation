package com.example.salonreservation.domain.member.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OIDCPublicKey {

    private String kid;
    private String kty;
    private String alg;
    private String use;
    private String n;
    private String e;

}
