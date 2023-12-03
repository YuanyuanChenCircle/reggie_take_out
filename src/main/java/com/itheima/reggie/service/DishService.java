package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Dish;

public interface DishService extends IService<Dish> {
    /**
     * 新增菜品，同时保存对应的口味数据; 新增菜品，同时插入菜品对应的口味数据，需要操作两张表：dish、dish_flavor
     *
     * @param dishDto dishDto
     */
    public void saveWithFlavor(DishDto dishDto);

    /**
     * 根据id查询对应的菜品信息以及口味信息
     *
     * @param id id
     * @return DishDto
     */
    public DishDto getByIdWithFlavor(Long id);

    /**
     * 更新菜品信息，同时更新对应的口味信息， 需要操作两张表：dish、dish_flavor
     *
     * @param dishDto dishDTo
     */
    public void updateWithFlavor(DishDto dishDto);
}
