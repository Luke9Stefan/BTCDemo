package com.stefan.btcdemo.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stefan.btcdemo.bean.Block;
import com.stefan.btcdemo.bean.MessageBean;
import com.stefan.btcdemo.bean.Notebook;
import com.stefan.btcdemo.bean.Transaction;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.ArrayList;

public class MyClient extends WebSocketClient {

    private String name;

    public MyClient(URI serverUri, String name) {
        super(serverUri);
        this.name = name;

    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("客户端——" + name + "——打开了连接");
    }

    @Override
    public void onMessage(String message) {

        System.out.println("客户端——" + name + "——收到了消息：" + message);
        Notebook notebook = Notebook.getInstance();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            MessageBean messageBean = objectMapper.readValue(message, MessageBean.class);

            if (messageBean.getType() == 1){
                JavaType javaType = objectMapper.getTypeFactory().constructParametricType(ArrayList.class, Block.class);
                ArrayList<Block> newList = objectMapper.readValue(messageBean.getMsg(), javaType);

                notebook.compareData(newList);
            }else if (messageBean.getType() == 2){
                Transaction transaction = objectMapper.readValue(messageBean.getMsg(), Transaction.class);
                if (transaction.verify()){
                    notebook.addNote(messageBean.getMsg());
                }
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("客户端——" + name + "——关闭了连接");
    }

    @Override
    public void onError(Exception ex) {
        System.out.println("客户端——" + name + "——发生了错误");
    }
}
