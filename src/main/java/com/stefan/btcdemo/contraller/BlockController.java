package com.stefan.btcdemo.contraller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.stefan.btcdemo.BtcdemoApplication;
import com.stefan.btcdemo.bean.Block;
import com.stefan.btcdemo.bean.MessageBean;
import com.stefan.btcdemo.bean.Notebook;
import com.stefan.btcdemo.bean.Transaction;
import com.stefan.btcdemo.websocket.MyClient;
import com.stefan.btcdemo.websocket.MyServer;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;

@RestController
public class BlockController {

    Notebook notebook = Notebook.getInstance();

    // 添加封面，创世区块
    @PostMapping("/addGenesis")
    public String addGenesis(String genesis) {

        try {
            notebook.addGenesis(genesis);
            return "添加封面成功~ ~";
        } catch (Exception e) {
            return "添加封面失败：" + e.getMessage();
        }
    }

    // 添加数据/交易记录，普通区块
    @PostMapping("/addNote")
    public String addNote(Transaction transaction) {

        try {
            if (transaction.verify()) {

                ObjectMapper objectMapper = new ObjectMapper();
                String transactionString = objectMapper.writeValueAsString(transaction);

                MessageBean messageBean = new MessageBean(2, transactionString);
                String msg = objectMapper.writeValueAsString(messageBean);

                server.broadcast(msg);
                notebook.addNote(transactionString);
                return "添加数据成功~ ~";
            } else {
                throw new RuntimeException("交易校验失败");
            }
        } catch (Exception e) {
            return "添加数据失败：" + e.getMessage();
        }

    }

    // 展示区块数据
    @GetMapping("/showList")
    public ArrayList<Block> showList() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return notebook.showList();
    }

    // 校验区块数据
    @GetMapping("/check")
    public String check() {

        String check = notebook.check();
        if (StringUtils.isEmpty(check)) {
            return "数据安全！";
        }
        return check;
    }

    private MyServer server;

    @PostConstruct
    public void init() {
        server = new MyServer(Integer.parseInt(BtcdemoApplication.port) + 1);
        server.startServer();
    }

    private HashSet<String> set = new HashSet<>();

    // 注册节点
    @RequestMapping("/regist")
    public String regist(String node) {
        set.add(node);
        conn();
        return "添加成功";
    }

    private ArrayList<MyClient> clients = new ArrayList<>();

    // 连接
    @PostMapping("/conn")
    public String conn() {
        try {
            for (String s : set) {
                URI uri = new URI("ws://localhost:" + s);
                MyClient client = new MyClient(uri, s);
                client.connect();
                clients.add(client);
            }
            return "连接成功";
        } catch (Exception e) {
            return "连接失败" + e.getMessage();
        }
    }

    // 广播
    @RequestMapping("/broadcast")
    public String broadcast(String msg) {
        server.broadcast(msg);
        return "广播成功";
    }

    // 同步(请求同步其他节点的区块链数据)
    @RequestMapping("/syncData")
    public String syncData() {
        for (MyClient client : clients) {
            client.send("请求同步");
        }
        return "同步成功";
    }

}
