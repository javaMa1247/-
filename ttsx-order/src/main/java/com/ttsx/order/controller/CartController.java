package com.ttsx.order.controller;

import com.ttsx.bean.Cartinfo;
import com.ttsx.order.biz.CartBiz;
import com.ttsx.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author: 86150
 * @CreateDate: 2023-05-08 下午 2:14
 */
@RestController
@RequestMapping("cart")
public class CartController {
    @Autowired
    private CartBiz biz;
    @RequestMapping("showAllcartInfo")
    public List<Cartinfo> showAllcartInfo(){

        String sql = "select cno,mno,num,goodsinfo.*,goodsinfo.price*num as smallCount " +
                "from goodsinfo,cartinfo where goodsinfo.gno=cartinfo.gno and mno= ? ";

        List<Cartinfo> cartinfos = this.biz.showAllCart();
        return cartinfos;
    }

    @RequestMapping("addCart")
    public Map<String,Object> addCart(HttpServletRequest request, HttpServletResponse response) {
        //取出gno和num
        String gno = request.getParameter("gno");
        String num = request.getParameter("num");
        Map<String,Object> map = new HashMap<>();
        int i = this.biz.addCart(gno, num);
        if(i!=0) {
            map.put("code", 1);
            map.put("data", "添加成功");
        }
        return map;

    }
}