package com.fm.page.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Data
@NoArgsConstructor
@Table(name = "tb_footprint")
public class FootPrint {

    @Id
    @KeySql(useGeneratedKeys = true)
    private Long id;
    private Long userId;// 订单id
    private Long spuId;// 商品id
    private String genericSpec;// 商品通用规格参数数据
    private String specialSpec;// 商品特有规格
    private Date addTime;// 添加时间
    @JsonIgnore
    private Boolean deleted;// 是否有效，逻辑删除用
}
