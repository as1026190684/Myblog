package com.jx.blogap1;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class BlogAp1ApplicationTests {

    @Test
    void contextLoads() {
        String s = new String();
        String name = s.getClass().getName();
        System.out.println(name);
    }

}
