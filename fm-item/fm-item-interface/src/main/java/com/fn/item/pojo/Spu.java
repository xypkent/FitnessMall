package com.fn.item.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;
import java.util.List;

/**
 * 持久化对象，它跟持久层（通常是关系型数据库）的数据结构形成一一对应的映射关系
 */
@Table(name = "tb_spu")
@Data
public class Spu {

    @Id
    @KeySql(useGeneratedKeys = true)
    private Long id;
    private Long brandId;
    private Long cid1;// 1级类目
    private Long cid2;// 2级类目
    private Long cid3;// 3级类目
    private String title;// 标题
    private String subTitle;// 子标题
    private Boolean saleable;// 是否上架
    @JsonIgnore
    private Boolean valid;// 是否有效，逻辑删除用
    private Date createTime;// 创建时间

    @JsonIgnore//不想返给页面的字段可以加上这个注解
    private Date lastUpdateTime;// 最后修改时间


    //spu所属的分类名称
    @Transient
    private String cname;

    //spu所属品牌名
    @Transient
    private String bname;

    //sku集合
    @Transient
    private List<Sku> skus;

    //spu详情
    @Transient
    private SpuDetail spuDetail;
}
