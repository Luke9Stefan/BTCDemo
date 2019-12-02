package com.stefan.btcdemo.bean;

import com.stefan.btcdemo.utils.RSAUtils;
import com.sun.org.apache.xml.internal.security.utils.Base64;
import lombok.Data;

import java.io.File;
import java.security.PrivateKey;
import java.security.PublicKey;


@Data
public class Wallet {
    PrivateKey privateKey;
    PublicKey publicKey;
    String name;

    public Wallet(String name) {
        this.name = name;
        File priFile = new File(name + ".pri");
        File pubFile = new File(name + ".pub");

        if (!priFile.exists() || priFile.length() == 0 || !pubFile.exists() || pubFile.length() == 0) {
            RSAUtils.generateKeysJS("RSA", name + ".pri", name + ".pub");
        }
//        privateKey = RSAUtils.getPrivateKey("RSA", name + ".pri");
//        publicKey = RSAUtils.getPublicKeyFromFile("RSA", name + ".pub");
    }

    public Transaction sendMoney(String receiverPublicKey, String content) {
        String signature = RSAUtils.getSignature("SHA256withRSA", privateKey, content);
        String senderPublicKey = Base64.encode(publicKey.getEncoded());

        Transaction transaction = new Transaction(senderPublicKey, receiverPublicKey, content, signature);

        return transaction;
    }

    public static void main(String[] args) {
        Wallet a = new Wallet("a");
        Wallet b = new Wallet("b");

        PublicKey publicKey = b.publicKey;
        String encode = Base64.encode(publicKey.getEncoded());
        Transaction transaction = a.sendMoney(encode, "10");
        boolean verify = transaction.verify();
        System.out.println(verify);

    }
}
