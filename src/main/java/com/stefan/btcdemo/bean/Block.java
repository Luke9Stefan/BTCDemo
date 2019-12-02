package com.stefan.btcdemo.bean;


import lombok.Data;

@Data
public class Block {
    int id;
    String content;
    String hash;
    int nonce;
    String preHash;

    public Block() {
    }

    public Block(int id, String content, String hash, int nonce, String preHash) {
        this.id = id;
        this.content = content;
        this.hash = hash;
        this.nonce = nonce;
        this.preHash = preHash;
    }
}
