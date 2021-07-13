/*
package com.katouyi.tools.mybatisPlus;


import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.PackageConfig;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;

*/
/**
 * @description: 代码生成器, 修改 策略配置中setInclude 的内容，即可创建表了
 * @author: ZengGuangfu
 *//*

public class CodeGenerator {

    public static void main(String[] args) {
        // 1.全局配置
        GlobalConfig globalConfig = new GlobalConfig();
        globalConfig.setOutputDir("C:\\Users\\Shinelon\\Desktop\\我的测试项目\\code")
                .setIdType(IdType.AUTO)                                         // 自增长主键
                .setBaseResultMap(true)                                         // 在xml中自动生成resultMap
                .setFileOverride(true)                                          // 重新生成使用覆盖的形式
                .setActiveRecord(true)                                          // 开始AR Model
                .setBaseColumnList(true)                                        // 在xml中生成自动的<sql>标签
                .setServiceName("%sService")                                    // 生成的Service不是I 字母开头的
                .setAuthor("zengg")
                .setServiceImplName("%sServiceImpl")
                .setOpen(false)                                                 // 生成完成后不弹出文件框
        ;

        // 2. 数据源配置
        DataSourceConfig dataSourceConfig = new DataSourceConfig();
        dataSourceConfig.setDbType(DbType.MYSQL)
                //.setDriverName("com.mysql.cj.jdbc.Driver")
                .setDriverName("com.zaxxer.hikari.HikariDataSource")
                .setUrl("jdbc:mysql://localhost:3306/businessman?characterEncoding=UTF-8&useSSL=false&serverTimezone=Asia/Shanghai")
                .setUsername("root")
                .setPassword("root")
        ;

        // 3. 策略配置
        StrategyConfig strategyConfig = new StrategyConfig();
        strategyConfig.setTablePrefix("b_")
                .setNaming(NamingStrategy.underline_to_camel)    // 下划线转驼峰
                //.setVersionFieldName("")      // 没有引入乐观锁插件，不需要这个
                .setInclude("b_items", "b_items_comments", "b_items_img",
                        "b_items_param", "b_items_spec", "b_order_items", "b_order_status", "b_orders", "b_stu"
                        ,"b_user_address")
        ;

        // 4.包配置
        PackageConfig packageConfig = new PackageConfig();
        packageConfig.setParent("com.nagasirened")
                .setMapper("mapper")
                .setService("service")
                .setServiceImpl("service.impl")
                .setController("controller")
                .setXml("mapper.xml")
                .setEntity("pojo")
        ;

        // 5.整合配置
        AutoGenerator autoGenerator = new AutoGenerator();
        autoGenerator.setGlobalConfig(globalConfig)
                .setDataSource(dataSourceConfig)
                .setStrategy(strategyConfig)
                .setPackageInfo(packageConfig);
        autoGenerator.execute();
    }
}
*/
