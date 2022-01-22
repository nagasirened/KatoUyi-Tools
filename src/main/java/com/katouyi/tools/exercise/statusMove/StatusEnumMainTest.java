package com.katouyi.tools.exercise.statusMove;

import java.util.ArrayList;
import java.util.List;

/**
 * 单据状态 测试类
 */
public class StatusEnumMainTest {

    public static void main(String[] args) {
        new StatusEnumMainTest().test();
    }

    private void test() {
        // 假设从数据库中获取了改数据的状态
        List<StatusEnumTest> list = getInitList();
        // 已新建数据进行提报（成功）
        StatusEnumTest statusEnumTest1 = getStatusEnumTest(1L, list);
        System.out.println("修改前的数据：" + statusEnumTest1.toString());
        statusEnumTest1.setStatus(StatusEnum.valueOf(statusEnumTest1.getStatus()).submit().getIndex());
        System.out.println("修改后的数据：" + statusEnumTest1.toString());
        // 已关闭数据进行修改（失败）
        StatusEnumTest statusEnumTest7 = getStatusEnumTest(7L, list);
        System.out.println("关闭前的数据：" + statusEnumTest7.toString());
        statusEnumTest1.setStatus(StatusEnum.valueOf(statusEnumTest7.getStatus()).save().getIndex());
        System.out.println("关闭后的数据：" + statusEnumTest7.toString());
    }

    private StatusEnumTest getStatusEnumTest(Long id, List<StatusEnumTest> list) {
        for (StatusEnumTest data : list) {
            if (id.equals(data.getId())) {
                return data;
            }
        }
        return null;
    }

    private List<StatusEnumTest> getInitList() {
        List<StatusEnumTest> list = new ArrayList<>();
        list.add(new StatusEnumTest(1L, 1, "这是已新建数据"));
        list.add(new StatusEnumTest(2L, 2, "这是已提报数据"));
        list.add(new StatusEnumTest(3L, 3, "这是已审核数据"));
        list.add(new StatusEnumTest(4L, 4, "这是已退回数据"));
        list.add(new StatusEnumTest(5L, 5, "这是已拒绝数据"));
        list.add(new StatusEnumTest(6L, 6, "这是已使用数据"));
        list.add(new StatusEnumTest(7L, 7, "这是已关闭数据"));
        return list;
    }

    private class StatusEnumTest {
        private Long id;
        private Integer status;
        private String other;

        public StatusEnumTest(Long id, Integer status, String other) {
            this.id = id;
            this.status = status;
            this.other = other;
        }

        public Long getId() {
            return id;
        }

        public Integer getStatus() {
            return status;
        }

        public void setStatus(Integer status) {
            this.status = status;
        }

        public String getOther() {
            return other;
        }

        @Override
        public String toString() {
            return "StatusEnumTest{" + "id=" + id + ", status=" + status + ", other='" + other + '\'' + '}';
        }
    }

}