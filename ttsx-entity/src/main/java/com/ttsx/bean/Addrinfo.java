package com.ttsx.bean;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: mqb
 * @Date: 2023/5/7
 * @Time: 19:20
 * @Description:
 */
@Data
public class Addrinfo implements Serializable {
    private String ano;
    private int mno;
    private String name;
    private String tel;
    private String province;
    private String city;
    private String area;
    private String addr;
    private int flag;
    private int status;
}