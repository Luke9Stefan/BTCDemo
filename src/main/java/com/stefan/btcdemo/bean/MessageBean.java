package com.stefan.btcdemo.bean;


import lombok.Data;

@Data
public class MessageBean {

    int type; // 消息类型
    String msg; // 消息的具体内容
    

    public MessageBean() {
    }

    public MessageBean(int type, String msg) {
        this.type = type;
        this.msg = msg;
    }
}
