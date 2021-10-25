package cn.flood.mapstruct;


import cn.flood.mapstruct.domain.Department;
import cn.flood.mapstruct.domain.Staff;
import cn.flood.mapstruct.entity.StaffEntity;
import cn.flood.mapstruct.enums.StatusEnum;
import cn.flood.mapstruct.translator.StaffTranslator;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Administrator
 * @date 2021-06-20 0:57
 * @description
 */
@SpringBootTest
public class TestControllerTest {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private StaffTranslator staffTranslator;

    @Test
    public void testCost(){
        testCost(100000);
    }

    public void testCost(Integer times){
        DecimalFormat decimalFormat = new DecimalFormat("0.000000");
        Double mapStructCost = testMapStructCost(times);
        Double beanUtilsCost = testBeanUtilsCost(times);

        log.info("{}次转换, mapStruct平均耗时: {}, beanUtils平均耗时: {}", times, decimalFormat.format(mapStructCost), decimalFormat.format(beanUtilsCost));
    }

    private Double testMapStructCost(Integer times){
        List<Long> costs = new ArrayList<>(times);

        for (int i = 0; i < times; i++) {
            costs.add(getMapStructCost());
        }
        return costs.stream().mapToLong(Long::longValue).average().orElseThrow(() -> new RuntimeException("求平均出现异常"));
    }

    private Double testBeanUtilsCost(Integer times){
        List<Long> costs = new ArrayList<>(times);
        for (int i = 0; i < times; i++) {
            costs.add(getBeanUtilsCost());
        }
        return costs.stream().mapToLong(Long::longValue).average().orElseThrow(() -> new RuntimeException("求平均出现异常"));
    }

    private Long getBeanUtilsCost(){
        Staff staff = new Staff().setId(1).setName("奈斯兔米特优").setDescription("没有描述");
        StaffEntity entity = new StaffEntity();
        long start = System.currentTimeMillis();
        BeanUtils.copyProperties(staff, entity);
        return System.currentTimeMillis() - start;
    }

    private Long getMapStructCost(){
        Staff staff = new Staff().setId(1).setBirthday(new Date()).setName("奈斯兔米特优").setDescription("没有描述");
        long start = System.currentTimeMillis();
        StaffEntity entity = staffTranslator.source2target(staff);
        return System.currentTimeMillis() - start;
    }

    @Test
    public void testStaff2StaffEntity(){

        Department department = new Department().setId(666).setName("研发部");

        Staff staff = new Staff()
                .setId(1)
                .setName("奈斯兔米特优")
                .setDescription("想不出该怎么来描述")
                .setBirthday(new Date())
                .setSex(1)
                .setSalary(88888.88888)
                .setPassword("1234567890")
                .setStatus(StatusEnum.ENABLED)
                .setDepartment(department)
                .setCreateTime(new Date());

        StaffEntity entity = staffTranslator.source2target(staff);

        log.info("Staff: {}", staff.toString());
        log.info("StaffEntity: {}", entity.toString());
    }

    @Test
    public void testStaffEntityStaff(){

        StaffEntity entity = new StaffEntity()
                .setId(1)
                .setName("奈斯兔米特优")
                .setStaffDescription("想不出该怎么来描述")
                .setBirthday("20210613")
                .setSex(1)
                .setSalary("88888.88888")
                .setPassword("1234567890")
                .setStatus(StatusEnum.ENABLED.getValue())
                .setDepartmentId(666)
                .setCreateTime("2021-06-20 11:03:47");

        Staff staff = staffTranslator.target2Source(entity);
        log.info("StaffEntity: {}", entity.toString());
        log.info("Staff: {}", staff.toString());
    }
}
