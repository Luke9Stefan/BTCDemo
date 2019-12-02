package com.stefan.btcdemo.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stefan.btcdemo.bean.Block;
import com.stefan.btcdemo.bean.MessageBean;
import com.stefan.btcdemo.bean.Notebook;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.util.ArrayList;

public class MyServer extends WebSocketServer {

    private int port;

    public MyServer(int port) {
        super(new InetSocketAddress(port));
        this.port = port;
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        System.out.println("webSocket服务器——" + port + "——打开了连接");
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        System.out.println("webSocket服务器——" + port + "——关闭了连接");
    }

    @Override
    public void onMessage(WebSocket conn, String message) {

        System.out.println("服务器——" + port + "——收到了消息：" + message);
        try {
            if ("请求同步".equals(message)){
                Notebook notebook = Notebook.getInstance();
                ArrayList<Block> list = notebook.showList();

                ObjectMapper objectMapper = new ObjectMapper();
                String blockChainData = objectMapper.writeValueAsString(list);

                MessageBean messageBean = new MessageBean(1, blockChainData);
                String msg = objectMapper.writeValueAsString(messageBean);
                // 广播消息
                broadcast(msg);
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        System.out.println("客户端——" + port + "——发生了错误");
    }

    @Override
    public void onStart() {
        System.out.println("客户端——" + port + "——启动成功");
    }

    public void startServer(){
        new Thread(this).start();
    }
}
