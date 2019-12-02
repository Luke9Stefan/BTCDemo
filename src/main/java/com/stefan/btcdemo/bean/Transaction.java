package com.stefan.btcdemo.bean;


import com.stefan.btcdemo.utils.RSAUtils;
import lombok.Data;

import java.security.PublicKey;

@Data
public class Transaction {
    // 付款方公钥 （发送方地址）
    String senderPublicKey;
    // 收款方公钥（接受地址）
    String receiverPublicKey;
    // 金额
    String content;
    // 签名
    String signatureData;

    public Transaction() {
    }

    // 转账的方法
    public Transaction(String senderPublicKey, String receiverPublicKey, String content, String signatureData) {
        this.senderPublicKey = senderPublicKey;
        this.receiverPublicKey = receiverPublicKey;
        this.content = content;
        this.signatureData = signatureData;
    }

    // 校验交易的方法
    public boolean verify() {
        PublicKey publicKey = RSAUtils.getPublicKeyFromString("RSA", senderPublicKey);
//        return RSAUtils.verify("SHA256withRSA",publicKey,content,signatureData);
        return RSAUtils.verifyDataJS("SHA256withRSA", publicKey, content, signatureData);
    }
}
