package org.mnemosynehermesteam;

import org.mnemosynehermesteam.server.NettyServer;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class App {
    public static void main(String[] args) {
        try {
            log.info("Server starting...");
            new NettyServer().runSync();
        }
        catch ( InterruptedException e ) {
            System.out.println("Keyboard interrupted, closing netty server...");
        }
    }
}