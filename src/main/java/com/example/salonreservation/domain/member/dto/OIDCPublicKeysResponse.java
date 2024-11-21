package com.example.salonreservation.domain.member.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OIDCPublicKeysResponse {

    OIDCPublicKey[] keys;

}
