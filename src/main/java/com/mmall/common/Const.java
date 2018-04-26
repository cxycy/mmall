package com.mmall.common;

import com.google.common.collect.Sets;

import java.util.Set;

public class Const {

    public static final String CURRENT_USER = "currentUser";

    public static final String EMAIL = "email";

    public static final String USERNAME = "username";

    public static final String TOKEN_PREFIX = "token_";

    public interface ProductListOrderBy{
        Set<String> PRICE_ASC_DESC = Sets.newHashSet("price_desc","price_asc");
    }

    public interface RedisCacheTime{
        int REDIS_SESSION_EXTIME = 60*30;
    }

    public interface Role{
        int ROLE_CUSTOMER = 0; //ORDINARY USER
        int ROLE_ADMIN = 1; //ADMIN
    }

    public interface Cart{
        int CHECKED = 1;
        int UNCHECKED =0;

        String LIMIT_NUM_FAIL = "LIMIT_NUM_FAIL";
        String LIMIT_NUM_SUCCESS = "LIMIT_NUM_SUCCESS";
    }

    public enum ProductStatusEnum{
        ON_SALE("On Sale",1);

        private String value;
        private int code;

        ProductStatusEnum(String value, int code) {
            this.value = value;
            this.code = code;
        }

        public String getValue() {
            return value;
        }

        public int getCode() {
            return code;
        }
    }

    public enum OrderStatus{
        CANCELLED(0,"CANCELLED"),
        NO_PAY(10,"NOT PAY YET"),
        PAID(20,"ALREADY PAID"),
        SHIPPED(30,"ALREADY SHIPPED"),
        ORDER_SUCCESS(40,"ORDER SUCCESS"),
        ORDER_CLOSE(50,"ORDER CLOSE");



        OrderStatus(int code,String value) {
            this.value = value;
            this.code = code;
        }

        private String value;
        private int code;

        public String getValue() {
            return value;
        }

        public int getCode() {
            return code;
        }

        public static OrderStatus codeOf(int code){
            for(OrderStatus orderStatusEnum : values()){
                if(orderStatusEnum.getCode() == code){
                    return orderStatusEnum;
                }
            }
            throw new RuntimeException("no matching enum!");
        }
    }

    public interface  AlipayCallback{
        String TRADE_STATUS_WAIT_BUYER_PAY = "WAIT_BUYER_PAY";
        String TRADE_STATUS_TRADE_SUCCESS = "TRADE_SUCCESS";

        String RESPONSE_SUCCESS = "success";
        String RESPONSE_FAILED = "failed";
    }

    public enum PayPlatformEnum{
        ALIPAY(1,"支付宝"),
        WECHAT(2,"微信");


        PayPlatformEnum(int code,String value) {
            this.value = value;
            this.code = code;
        }
        private String value;
        private int code;

        public String getValue() {
            return value;
        }

        public int getCode() {
            return code;
        }
    }

    public enum PaymentTypeEnum{

        ONLINE_PAY(1,"ONLINE_PAY");

        PaymentTypeEnum(int code,String value) {
            this.value = value;
            this.code = code;
        }

        private String value;
        private int code;

        public static PaymentTypeEnum codeOf(int code){
            for(PaymentTypeEnum paymentTypeEnum : values()){
                if(paymentTypeEnum.getCode() == code){
                    return paymentTypeEnum;
                }
            }
            throw new RuntimeException("no matching enum!");
        }

        public String getValue() {
            return value;
        }

        public int getCode() {
            return code;
        }
    }

}
