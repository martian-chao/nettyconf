package com.lyc;

import org.junit.Test;

/**
 * Created by yanChaoLiu on 2017/9/7.
 */
public class TestStrSplit {
    @Test
    public void StrSplit(){
        String str = "8002,8003,8004,8005,8006,8007,8008,8009,8049";
        String[] strings = str.split(",");
        System.out.println("数组为："+strings.length);
    }
}
