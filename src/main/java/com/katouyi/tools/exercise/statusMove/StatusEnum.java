package com.katouyi.tools.exercise.statusMove;

/**
 * 单据状态
 */
public enum StatusEnum {

    /** 已提报 */
    HAD_CREATE(1, "已新建") {
        @Override
        public StatusEnum create() {
            return HAD_CREATE;
        }
        @Override
        public StatusEnum save() {
            return HAD_CREATE;
        }
        @Override
        public StatusEnum submit() {
            return HAD_SUBMIT;
        }
        @Override
        public StatusEnum close() {
            return HAD_CLOSE;
        }
    },

    /** 已提报 */
    HAD_SUBMIT(2, "已提报") {
        @Override
        public StatusEnum pass() {
            return HAD_AUDIT;
        }
        @Override
        public StatusEnum back() {
            return HAD_BACK;
        }
        @Override
        public StatusEnum reject() {
            return HAD_REJECT;
        }
    },

    /** 已审核" */
    HAD_AUDIT(3, "已审核") {
        @Override
        public StatusEnum used() {
            return HAD_USE;
        }
    },

    /** 已退回 */
    HAD_BACK(4, "已退回") {
        @Override
        public StatusEnum save() {
            return HAD_BACK;
        }
        @Override
        public StatusEnum submit() {
            return HAD_SUBMIT;
        }
        @Override
        public StatusEnum close() {
            return HAD_CLOSE;
        }
    },

    /** 已拒绝 */
    HAD_REJECT(5, "已拒绝") {
    },

    /** 已使用 */
    HAD_USE(6, "已使用") {
    },

    /** 已关闭 */
    HAD_CLOSE(7, "已关闭") {
    };

    /** 枚举值 */
    private int index;

    /** 枚举描述 */
    private String name;

    /** 创建 */
    public StatusEnum create() {
        throw new RuntimeException("当前状态【" + this.getName() + "】不允许执行【创建】操作");
    }

    /** 暂存 */
    public StatusEnum save() {
        throw new RuntimeException("当前状态【" + this.getName() + "】不允许执行【暂存】操作");
    }

    /** 提报 */
    public StatusEnum submit() {
        throw new RuntimeException("当前状态【" + this.getName() + "】不允许执行【提报】操作");
    }

    /** 通过 */
    public StatusEnum pass() {
        throw new RuntimeException("当前状态【" + this.getName() + "】不允许执行【通过】操作");
    }

    /** 驳回 */
    public StatusEnum back() {
        throw new RuntimeException("当前状态【" + this.getName() + "】不允许执行【驳回】操作");
    }

    /** 拒绝 */
    public StatusEnum reject() {
        throw new RuntimeException("当前状态【" + this.getName() + "】不允许执行【拒绝】操作");
    }

    /** 使用 */
    public StatusEnum used() {
        throw new RuntimeException("当前状态【" + this.getName() + "】不允许执行【使用】操作");
    }

    /** 关闭 */
    public StatusEnum close() {
        throw new RuntimeException("当前状态【" + this.getName() + "】不允许执行【关闭】操作");
    }

    /** 使用的重点方法 */
    public static StatusEnum valueOf(Integer index) {
        if (index == null) {
            return null;
        }
        for (StatusEnum modeEnum : StatusEnum.values()) {
            if (index == modeEnum.getIndex()) {
                return modeEnum;
            }
        }
        return null;
    }
    
    StatusEnum(int index, String name) {
        this.index = index;
        this.name = name;
    }

    public int getIndex() {
        return index;
    }

    public String getName() {
        return name;
    }

}