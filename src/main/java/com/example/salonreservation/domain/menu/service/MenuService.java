package com.example.salonreservation.domain.menu.service;

import com.example.salonreservation.domain.menu.dto.MenuDto;
import com.example.salonreservation.domain.menu.entity.Menu;
import com.example.salonreservation.domain.menu.repository.MenuRepository;
import com.example.salonreservation.domain.salon.entity.Salon;
import com.example.salonreservation.domain.salon.repository.SalonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MenuService {

    private final MenuRepository menuRepository;
    private final SalonRepository salonRepository;

    @Transactional(readOnly = true)
    public List<MenuDto> getMenuList(Long salonId) {
        Salon salon = salonRepository.findById(salonId).orElseThrow();
        List<Menu> menuList = menuRepository.findBySalon(salon);

        List<MenuDto> result = new ArrayList<>();
        menuList.forEach(menu -> result.add(MenuDto.fromEntity(menu)));
        return result;
    }

    @Transactional(readOnly = true)
    public MenuDto getMenu(Long menuId) {
        Menu menu = menuRepository.findById(menuId).orElseThrow();
        return MenuDto.fromEntity(menu);
    }

    public Long addMenu(Long salonId, MenuDto menuDto) {
        Salon salon = salonRepository.findById(salonId).orElseThrow();
        Menu menu = Menu.builder()
                .menuName(menuDto.getMenuName())
                .menuInfo(menuDto.getMenuInfo())
                .price(menuDto.getPrice())
                .time(menuDto.getTime())
                .shampooYN(menuDto.isShampooYN())
                .cutYN(menuDto.isCutYN())
                .target(menuDto.getTarget())
                .salon(salon)
                .build();

        menuRepository.save(menu);
        return menu.getId();
    }

    public void modifyMenu(Long menuId, MenuDto menuDto) {
        Menu menu = menuRepository.findById(menuId).orElseThrow();
        menu.updateMenu(menuDto.getMenuName(), menuDto.getMenuInfo(), menuDto.getPrice(), menu.getTime(), menuDto.isShampooYN(), menuDto.isCutYN(), menu.getTarget());
    }

    public void removeMenu(Long menuId) {
        menuRepository.deleteById(menuId);
    }

}
