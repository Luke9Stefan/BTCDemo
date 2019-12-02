package com.stefan.btcdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

import java.util.Scanner;

@SpringBootApplication
public class BtcdemoApplication {

    public static String port;

    public static void main(String[] args) {

//        SpringApplication.run(BtcDemoApplication.class, args);
        Scanner scanner = new Scanner(System.in);

        port = scanner.nextLine();
        new SpringApplicationBuilder(BtcdemoApplication.class).properties("server.port=" + port).run(args);
    }
}
