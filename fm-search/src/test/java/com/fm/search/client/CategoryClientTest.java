package com.fm.search.client;

import com.fn.item.pojo.Category;
import com.netflix.discovery.converters.Auto;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CategoryClientTest {

    @Autowired
    private CategoryClient categoryClient;

    @Test
    public void queryCategoryByIds() {

        List<Category> categories = categoryClient.queryCategoryByIds(Arrays.asList(1L, 2L, 3L));

        Assert.assertEquals(3L,categories.size());
        for (Category category : categories) {
            System.out.println("category = " + category);
        }


    }
}