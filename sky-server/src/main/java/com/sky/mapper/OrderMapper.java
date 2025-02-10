package com.sky.mapper;

import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.AddressBook;
import com.sky.entity.Orders;
import com.sky.vo.OrderSubmitVO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface OrderMapper {


    void insert(Orders orders);
}
