package com.stefan.btcdemo.bean;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stefan.btcdemo.utils.HashUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Notebook {

    private ArrayList<Block> list = new ArrayList<>();

    private Notebook() {
        init();
    }
    public static volatile Notebook instance;

    public static Notebook getInstance(){
        if (instance == null){
            synchronized (Notebook.class){
                if (instance == null){
                    instance = new Notebook();
                }
            }
        }
        return instance;
    }


    private void init(){
        try {
            File file = new File("账本.json");
            if (file.exists() && file.length() > 0) {
                ObjectMapper objectMapper = new ObjectMapper();

                JavaType javaType = objectMapper.getTypeFactory().constructParametricType(ArrayList.class, Block.class);
                list = objectMapper.readValue(file, javaType);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 添加封面，创世区块
    public void addGenesis(String genesis) {

        if (list.size() > 0) {
            throw new RuntimeException("添加封面必须是新账本");
        }
        if (genesis == null || genesis.length() == 0) {
            throw new RuntimeException("添加封面不能为空");
        }
        String preHash = "0000000000000000000000000000000000000000000000000000000000000000";
        int nonce = mine(genesis + preHash);
        // 添加数据
        list.add(new Block(
                list.size() + 1,
                genesis,
                HashUtils.sha256(nonce + genesis + preHash),
                nonce,
                preHash
        ));
        save2Disk();

    }

    // 挖矿
    private int mine(String data) {
        for (int i = 0; i < Integer.MAX_VALUE; i++) {
            String hash = HashUtils.sha256(i + data);
            if (hash.startsWith("0000")) {
                System.out.println("挖矿成功：" + i);
                return i;
            } else {
                System.out.println("第" + i + "次挖矿");
            }
        }
        throw new RuntimeException("挖矿失败！");
    }

    // 添加数据/交易记录，普通区块
    public void addNote(String note) {

        if (list.size() < 1) {
            throw new RuntimeException("添加数据必须有封面");
        }
        Block block = list.get(list.size() - 1);
        String preHash = block.getHash();

        int nonce = mine(note + preHash);

        list.add(new Block(
                list.size() + 1,
                note,
                HashUtils.sha256(nonce + note + preHash),
                nonce,
                preHash
        ));
        save2Disk();

    }

    // 展示区块数据
    public ArrayList<Block> showList() {

        return list;
    }

    // 保存数据
    public void save2Disk() {

        try {
            // jackson序列化对象的方法
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.writeValue(new File("账本.json"), list);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String check() {
        StringBuilder sb = new StringBuilder();

//        for (Block block : list) {
//            String content = block.getContent();
//            int nonce = block.getNonce();
//            String hash = block.getHash();
//            String diyhash = HashUtils.sha256(content + nonce + );
//            if (!diyhash.equals(hash)){
//                sb.append("编号为" + block.getId() + "的区块数据有问题，请注意检查！");
//            }
//
//        }
        for (int i = 0; i < list.size(); i++) {
            Block block = list.get(i);
            String hash = block.getHash();
            String content = block.getContent();
            int nonce = block.getNonce();
            String preHash = block.getPreHash();
            if (i == 0) {
                String diyHash = HashUtils.sha256(nonce + content + preHash);
                if (!diyHash.equals(hash)) {
                    sb.append("编号为" + block.getId() + "的区块hash有问题，请注意检查！");
                }
            } else {
                String diyHash = HashUtils.sha256(nonce + content + preHash);
                if (!diyHash.equals(hash)) {
                    sb.append("编号为" + block.getId() + "的区块hash有问题，请注意检查！");
                }
                Block preBlock = list.get(i - 1);
                String preBlockHash = preBlock.getHash();
                if (!preBlockHash.equals(preHash)) {
                    sb.append("编号为" + block.getId() + "的区块preHash有问题，请注意检查！");
                }
            }
        }
        return sb.toString();
    }

    public void compareData(ArrayList<Block> newList) {
        // 比较长度，校验
        if (newList.size() > list.size()){
            list = newList;
        }

    }

//    public static void main(String[] args){
//        Notebook notebook = new Notebook();
//
////        notebook.addGenesis("这是添加的第一个封面~ ~");
//
//        notebook.addNote("张三给李四转账5毛");
//
//        notebook.showList();
//
//    }
}
