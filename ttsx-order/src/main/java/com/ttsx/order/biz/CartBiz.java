package com.ttsx.order.biz;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ttsx.bean.Cartinfo;
import com.ttsx.bean.Goodsinfo;
import com.ttsx.feignApi.FeignApp;
import com.ttsx.order.dao.CartDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author: 86150
 * @CreateDate: 2023-05-08 下午 2:55
 */
@Service
public class CartBiz {
    @Autowired
    private FeignApp feignApp;
    @Autowired
    private CartDao dao;
    private Integer mno=3;   //TODO:  mno


    public List<Cartinfo> showAllCart(){

        QueryWrapper<Cartinfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(Cartinfo::getMno, mno);

        List<Cartinfo> cartinfos = this.dao.selectList(queryWrapper);
        for (Cartinfo cartinfo : cartinfos) {
            cartinfo.setCount(cartinfo.getCount()+1);
            //TODO: 到nacos中查找res-foods服务中的   findById ，要得到菜品对象goods
            Map<String,Object> resultMap=this.feignApp.findById(   Integer.valueOf(cartinfo.getGno()) );
            Goodsinfo gs = null;
            if( "1".equalsIgnoreCase(  resultMap.get("code").toString())){
                Map m= (Map) resultMap.get("data");
                //如何将一个Map转为  一个  Resfood对象  -> 反射.
                // spring的底层对json的处理使用 jackson框架，这个框架有将map转为对象的方法
                ObjectMapper mapper=new ObjectMapper();
                gs=mapper.convertValue(     m,  Goodsinfo.class );
                cartinfo.setSmallCount(gs.getPrice()*cartinfo.getNum());
                cartinfo.setGoodsinfo(gs);
            }

        }
        return cartinfos;
    }
    public int addCart(String gno,String num) {
        int result = 0;
        try {
            //判断购物项是否已经存在
            QueryWrapper<Cartinfo> queryWrapper = new QueryWrapper<>();
            queryWrapper.select("num")
                    .eq("gno", gno)
                    .eq("mno", mno);
            List<Object> numList = dao.selectObjs(queryWrapper);
            int num0 = numList.size()==0 ? 0 : (int) numList.get(0);

            if (num0 > 0) {
                int num2 = num0 + Integer.parseInt(num);
                if (num2 < 1) {
                    num2 = 1;
                }
                UpdateWrapper<Cartinfo> updateWrapper = new UpdateWrapper<>();
                updateWrapper.set("num", num)
                        .eq("gno", gno)
                        .eq("mno", mno);
                int rows = dao.update(null, updateWrapper);
                return rows;
            } else {
//                String cno = String.valueOf((int) db.selectAggreation("select max(cno) from cartinfo") + 1);
//                db.doUpdata("INSERT INTO cartinfo VALUES (?, ?,?, ?)",cno,YcConstants.selectMno(request),gno,num);
                QueryWrapper<Cartinfo> queryWrapper1 = new QueryWrapper<>();
                queryWrapper1.select("max(cno)");
                Object re = dao.selectObjs(queryWrapper1).get(0);
                Integer maxCno = re == null ? null : (Integer.valueOf(re.toString()))+1 ;

                Cartinfo cartinfo = new Cartinfo(maxCno+"",gno,mno+"", Integer.valueOf(num));
                int rows = dao.insert(cartinfo);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
        return 1;
    }
}