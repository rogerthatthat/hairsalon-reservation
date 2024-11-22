package com.example.salonreservation.domain.menu.controller;

import com.example.salonreservation.domain.menu.dto.MenuDto;
import com.example.salonreservation.domain.menu.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 관리자용 메뉴 관리 API
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/salons")
public class MenuController {

    private final MenuService menuService;

    @GetMapping("/{salonId}/menus")
    public List<MenuDto> getMenuList(@PathVariable("salonId") Long salonId) {
        return menuService.getMenuList(salonId);
    }

    @GetMapping("/{salonId}/menus/{menuId}")
    public MenuDto getMenu(@PathVariable("salonId") Long salonId,
                           @PathVariable("menuId") Long menuId) {
        return menuService.getMenu(menuId);  //지점 통틀어서 menuId 고유하게 관리하므로 menuId로만 조회
    }

    @PostMapping("/{salonId}/menus")
    public ResponseEntity addMenu(@PathVariable("salonId") Long salonId,
                                  @RequestBody MenuDto menuDto) {
        Map<String, Long> result = new HashMap<>();
        Long menuId = menuService.addMenu(salonId, menuDto);
        result.put("menuId", menuId);
        return new ResponseEntity(menuId, HttpStatus.CREATED);
    }

    @PutMapping("/{salonId}/menus/{menuId}")
    public void modifyMenu(@PathVariable("salonId") Long salonId,
                           @PathVariable("menuId") Long menuId,
                           @RequestBody MenuDto menuDto) {
        //지점 검증의 필요성?
        menuService.modifyMenu(menuId, menuDto);
    }

    @DeleteMapping("/{salonId}/menus/{menuId}")
    public void removeMenu(@PathVariable("salonId") Long salonId,
                           @PathVariable("menuId") Long menuId) {
        menuService.removeMenu(menuId);
    }
}
