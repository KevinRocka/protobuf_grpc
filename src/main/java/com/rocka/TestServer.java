package com.rocka;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

/**
 * @author: Rocka
 * @version: 1.0
 * @description: TODO
 * @time:2018/11/12
 */
public class TestServer {

    private int port = 8883;
    private Server server;

    public static void main(String[] args) throws IOException, InterruptedException {
        final TestServer testServer = new TestServer();
        testServer.start();
        testServer.blockUntilShtudown();
    }

    private void start() throws IOException {
        server = ServerBuilder.forPort(port)
                .addService(new GreeterImpl())
                .build()
                .start();
        System.out.println("Sever started , listening on " + port);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                // Use stderr here since the logger may have been reset by its JVM shutdown hook.
                System.err.println("*** shutting down gRPC server since JVM is shutting down");
                TestServer.this.stop();
                System.err.println("*** server shut down");
            }
        });
    }

    private void stop() {
        if (server != null) {
            server.shutdown();
        }
    }

    private void blockUntilShtudown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }


    private class GreeterImpl extends GreeterGrpc.GreeterImplBase {
        public AtomicInteger count = new AtomicInteger(0);

        @Override
        public void sayHello(HelloRequest request, StreamObserver<HelloReply> responseObserver) {
            System.out.println("SayHello。。");
            HelloReply reply = HelloReply.newBuilder().setMessage("Hello " + request.getName()).build();
            responseObserver.onNext(reply);
            responseObserver.onCompleted();
            System.out.println("TT:" + count.incrementAndGet() + Thread.currentThread().getName());
        }
    }

}
