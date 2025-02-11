package com.sky.service.impl;


import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ReportServiceImpl implements ReportService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private UserMapper userMapper;
    @Override
    public TurnoverReportVO getTurnoverStatistics(LocalDate begin,LocalDate end){

        List<LocalDate> list = new ArrayList<>();
        list.add(begin);
        while(!begin.equals(end)){
            begin = begin.plusDays(1);
            list.add(begin);
        }
        List<Double> listamout = new ArrayList<>();
        for(LocalDate date:list){
            LocalDateTime begintime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endtime = LocalDateTime.of(date, LocalTime.MAX);
            Map map = new HashMap();
            map.put("begin",begintime);
            map.put("end",endtime);
            map.put("status", Orders.COMPLETED);
            Double turnover = orderMapper.sumByMap(map);
            turnover = turnover==null?0.0:turnover;
            listamout.add(turnover);
        }
        return TurnoverReportVO
                .builder()
                .dateList(StringUtils.join(list,","))
                .turnoverList(StringUtils.join(listamout,","))
                .build();
    }

    @Override
    public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> datelist = new ArrayList<>();
        datelist.add(begin);
        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            datelist.add(begin);
        }
        List<Integer> newUserList = new ArrayList<>();
        List<Integer> totalUserList = new ArrayList<>();
        for (LocalDate date : datelist) {
            LocalDateTime begintime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endtime = LocalDateTime.of(date, LocalTime.MAX);
            Map map = new HashMap();

            map.put("end", endtime);

            Integer newuser = userMapper.countUser(map);
            map.put("begin", begintime);
            Integer totaluser = userMapper.countUser(map);
            newUserList.add(newuser);
            totalUserList.add(totaluser);
        }
        return UserReportVO
                .builder()
                .dateList(StringUtils.join(datelist, ","))
                .newUserList(StringUtils.join(newUserList, ","))
                .totalUserList(StringUtils.join(totalUserList, ","))
                .build();
    }

    @Override
    public OrderReportVO getorderStatistics(LocalDate begin, LocalDate end) {

        List<LocalDate> datelist = new ArrayList<>();
        List<Integer> orderlist = new ArrayList<>();
        List<Integer> validorderlist = new ArrayList<>();
        datelist.add(begin);
        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            datelist.add(begin);
        }

        for(LocalDate date:datelist){
            LocalDateTime begintime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endtime = LocalDateTime.of(date, LocalTime.MAX);
            Map map = new HashMap();

            map.put("end", endtime);


            map.put("begin", begintime);
            Integer orderCount = orderMapper.countOrder(map);
            map.put("status",Orders.COMPLETED);
            Integer validOrderCount = orderMapper.countOrder(map);
            orderlist.add(orderCount);
            validorderlist.add(validOrderCount);
        }
        Integer totalordercount = orderlist.stream().reduce(Integer::sum).get();
        Integer validordercount = validorderlist.stream().reduce(Integer::sum).get();
        Double orderrate = 0.0;
        if(totalordercount!=0){
            orderrate = validordercount.doubleValue()/totalordercount;
        }

        return OrderReportVO.builder()
                .dateList(StringUtils.join(datelist,","))
                .orderCountList(StringUtils.join(orderlist,","))
                .validOrderCountList(StringUtils.join(validorderlist,","))
                .totalOrderCount(totalordercount)
                .validOrderCount(validordercount)
                .orderCompletionRate(orderrate)
                .build();
    }

    @Override
    public SalesTop10ReportVO getSaleStatistics(LocalDate begin, LocalDate end) {
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);
        List<GoodsSalesDTO> list = orderMapper.getsalesTop10(beginTime,endTime);
        List<String> names = list.stream().map(GoodsSalesDTO::getName).collect(Collectors.toList());
        String nameList = StringUtils.join(names,",");
        List<Integer> numbers = list.stream().map(GoodsSalesDTO::getNumber).collect(Collectors.toList());
        String numberList = StringUtils.join(numbers,",");
        return SalesTop10ReportVO
                .builder()
                .nameList(nameList)
                .numberList(numberList)
                .build();

    }
}
