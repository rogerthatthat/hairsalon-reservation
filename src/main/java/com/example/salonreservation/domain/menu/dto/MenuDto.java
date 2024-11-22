package com.example.salonreservation.domain.menu.dto;

import com.example.salonreservation.domain.menu.entity.Menu;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MenuDto {
    private String menuName;
    private String menuInfo;
    private String price;
    private String time;
    private boolean shampooYN;
    private boolean cutYN;
    private String target;

    public static MenuDto fromEntity(Menu menu) {
        return new MenuDto(menu.getMenuName(),
                menu.getMenuInfo(),
                menu.getPrice(),
                menu.getTime(),
                menu.isShampooYN(),
                menu.isCutYN(),
                menu.getTarget());
    }
}
