package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.OrderDto;
import com.itheima.reggie.entity.OrderDetail;
import com.itheima.reggie.entity.Orders;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.entity.ShoppingCart;
import com.itheima.reggie.service.OrderDetailService;
import com.itheima.reggie.service.OrderService;
import com.itheima.reggie.service.ShoppingCartService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author LJM
 * @create 2022/4/19
 */
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderDetailService orderDetailService;

    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 用户下单
     * @param orders
     * @return
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders){
        orderService.submit(orders);
        return R.success("下单成功");
    }


    /**
     * 用户端展示自己的订单分页查询
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/userPage")
    public R<Page> page(int page, int pageSize){
        //分页构造器对象
        Page<Orders> pageInfo = new Page<>(page,pageSize);
        Page<OrderDto> pageDto = new Page<>(page,pageSize);
        //构造条件查询对象
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        //这里树直接把分页的全部结果查询出来，没有分页条件
        //添加排序条件，根据更新时间降序排列
        queryWrapper.orderByDesc(Orders::getOrderTime);
        orderService.page(pageInfo,queryWrapper);

        //对象的拷贝  注意这里要把分页数据的全集合records给忽略掉  把pageInfo中的数据复制给pageDto
        BeanUtils.copyProperties(pageInfo,pageDto,"records");

        //前端传给我们的数据太少了，所以都需要自己从用户id来查询
        Long userId = BaseContext.getCurrentId();
        LambdaQueryWrapper<Orders> queryWrapper2 = new LambdaQueryWrapper<>();
        //封装查询Orders表的条件
        queryWrapper2.eq(Orders::getUserId, userId);
        //通过用户id查询订单集合
        List<Orders> list = orderService.list(queryWrapper2);

        List<Long> ordersIdList = new ArrayList<>(); //创建一个集合用来存放orders 的Id,为后面查询OrderDetail做准备
        //通过循环拿到所有的ordersId 存在集合中
        for (Orders orders : list) {
            Long ordersId = orders.getId();
            ordersIdList.add(ordersId);
        }
        //通过OrderId查询对应的OrderDetail
        LambdaQueryWrapper<OrderDetail> queryWrapper3 = new LambdaQueryWrapper<>();
        //封装对OrderDetail的查询条件
        queryWrapper3.in(OrderDetail::getOrderId, ordersIdList);
        //查询到OrderDetail集合,里面有我们想要的数据 number  name....
        List<OrderDetail> orderDetailList = orderDetailService.list(queryWrapper3);

        //对OrderDto进行需要的属性赋值
        List<Orders> records = pageInfo.getRecords();
        List<OrderDto> orderDtoList = records.stream().map((item) ->{
            OrderDto orderDto = new OrderDto();
            //此时的orderDto对象里面orderDetails属性还是空 下面准备为它赋值
            BeanUtils.copyProperties(item,orderDto);
            //对orderDto进行OrderDetails属性的赋值
            orderDto.setOrderDetails(orderDetailList);
            return orderDto;
        }).collect(Collectors.toList());

        //使用dto的分页有点难度.....需要重点掌握
        pageDto.setRecords(orderDtoList);
        return R.success(pageDto);
    }


    /**
     * 后台查询订单明细
     * @param page
     * @param pageSize
     * @param number
     * @param beginTime
     * @param endTime
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String number,String beginTime,String endTime){
        //分页构造器对象
        Page<Orders> pageInfo = new Page<>(page,pageSize);
        //构造条件查询对象
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();

        //添加动态查询条件
        queryWrapper.like(number!=null,Orders::getNumber,number)
                .gt(StringUtils.isNotEmpty(beginTime),Orders::getOrderTime,beginTime)
                .lt(StringUtils.isNotEmpty(endTime),Orders::getOrderTime,endTime);

        orderService.page(pageInfo,queryWrapper);
        return R.success(pageInfo);
    }


    /**客户端  再来一单功能
     * 前端点击再来一单是直接跳转到购物车的，所以为了避免数据有问题，再跳转之前我们需要把购物车的数据给清除
     * ①通过orderId获取订单明细
     * ②把订单明细的数据的数据塞到购物车表中，不过在此之前要先把购物车表中的数据给清除(清除的是当前登录用户的购物车表中的数据)，
     * 不然就会导致再来一单的数据有问题；
     * (这样可能会影响用户体验，但是对于外卖来说，用户体验的影响不是很大，电商项目就不能这么干了)
     */
    @PostMapping("/again")
    public R<String> againSubmit(@RequestBody Map<String,String> map){
        String ids = map.get("id");

        long id = Long.parseLong(ids);

        //通过用户id把原来的购物车给清空，这里的clean方法是视频中讲过的,建议抽取到service中,那么这里就可以直接调用了
        shoppingCartService.clean();

        LambdaQueryWrapper<OrderDetail> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderDetail::getOrderId,id);
        //获取该订单对应的所有的订单明细表
        List<OrderDetail> orderDetailList = orderDetailService.list(queryWrapper);

        List<ShoppingCart> shoppingCartList = orderService.againAdd(orderDetailList);

        //把携带数据的购物车批量插入购物车表  这个批量保存的方法要使用熟练！！！
        shoppingCartService.saveBatch(shoppingCartList);

        return R.success("操作成功");
    }


}
