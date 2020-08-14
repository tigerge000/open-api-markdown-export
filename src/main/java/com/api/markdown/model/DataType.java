package com.api.markdown.model;

/**
 * @author 飞狐 on 2019/04/17
 */
public enum DataType {
    /**
     * VOID
     */
    VOID {
        @Override
        public String toString() {
            return "Void";
        }
    },
    /**
     * HASH
     */
    MAP {
        @Override
        public String toString() {
            return "Map";
        }
    },
    /**
     * DATE日期类型
     */
    DATE {
        @Override
        public String toString() {
            return "Date";
        }
    },
    /**
     * 数组
     */
    ARRAY {
        @Override
        public String toString() {
            return "Array";
        }
    },
    /**
     * 数值类型
     */
    NUMBER {
        @Override
        public String toString() {
            return "Number";
        }
    },
    /**
     * byte
     */
    BYTE {
        @Override
        public String toString() {
            return "Byte";
        }
    },
    /**
     * short
     */
    SHORT {
        @Override
        public String toString() {
            return "Short";
        }
    },
    /**
     * Integer
     */
    Integer {
        @Override
        public String toString() {
            return "Integer";
        }
    },
    /**
     * Long
     */
    Long {
        @Override
        public String toString() {
            return "Long";
        }
    },
    /**
     * Float
     */
    Float {
        @Override
        public String toString() {
            return "Float";
        }
    },
    /**
     * double
     */
    DOUBLE {
        @Override
        public String toString() {
            return "Double";
        }
    },
    /**
     * BigDecimal
     */
    BIG_DECIMAL {
        @Override
        public String toString() {
            return "BigDecimal";
        }
    },
    /**
     * BigInteger
     */
    BIG_INTEGER {
        @Override
        public String toString() {
            return "BigInteger";
        }
    },
    /**
     * 枚举
     */
    ENUM {
        @Override
        public String toString() {
            return "Enum";
        }
    },
    /**
     * charSeq
     */
    STRING {
        @Override
        public String toString() {
            return "String";
        }
    },
    /**
     * boolean
     */
    BOOLEAN {
        @Override
        public String toString() {
            return "Boolean";
        }
    },
    /**
     * obj
     */
    OBJECT {
        @Override
        public String toString() {
            return "Object";
        }
    }
    //
    ;

    public static DataType of(String ty) {
        for (DataType type : DataType.values()) {
            if(type.toString().equals(ty)){
                return type;
            }
        }
        return null;
    }
}
