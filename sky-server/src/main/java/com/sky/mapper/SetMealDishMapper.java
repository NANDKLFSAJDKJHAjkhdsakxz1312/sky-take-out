package com.sky.mapper;


import com.sky.entity.Dish;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SetMealDishMapper {
    List<Long> getSetMealIdsByDishIds(List<Long> dishIds);
}
