package org.mnemosynehermesteam;

import org.mnemosynehermesteam.server.NettyServer;

public class App {
    public static void main(String[] args) {
        try {
            new NettyServer().run();
        }
        catch ( InterruptedException e ) {
            System.out.println("Keyboard interrupted, closing netty server...");
        }
    }
}