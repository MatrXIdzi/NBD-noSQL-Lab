package org.example;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        System.out.println("### KAFKA RESTAURANT RECEIVER ###");
        ReservationsReceiver receiver = new ReservationsReceiver("_1");
        ReservationsReceiver receiver2 = new ReservationsReceiver("_2");
        receiver.createConsumer();
        receiver.startConsumer();
        receiver2.createConsumer();
        receiver2.startConsumer();
        System.out.println("### PRESS ENTER TO STOP RECEIVING ###");
        System.in.read();
        receiver.stopConsumer();
        receiver2.stopConsumer();
    }
}