package com.itheima.reggie.dto;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.itheima.reggie.entity.OrderDetail;
import com.itheima.reggie.entity.Orders;
import lombok.Data;

import java.util.List;

/**
 * @author LJM
 * @create 2022/5/3
 */
@Data
public class OrderDto extends Orders  {

   private List<OrderDetail> orderDetails;


}
