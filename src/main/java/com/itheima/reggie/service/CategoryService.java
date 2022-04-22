package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.entity.Category;

/**
 * @author LJM
 * @create 2022/4/16
 */
public interface CategoryService extends IService<Category> {

    //定义自己需要的方法
    void remove(Long id);
}
