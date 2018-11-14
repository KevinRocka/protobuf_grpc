package com.rocka;

import java.util.concurrent.TimeUnit;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

/**
 * @author: Rocka
 * @version: 1.0
 * @description: TODO
 * @time:2018/11/12
 */
public class TestClient {

    private final ManagedChannel channel;

    private final GreeterGrpc.GreeterBlockingStub blockingStub;

    public TestClient(String host, int port) {
        channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext(true).build();
        blockingStub = GreeterGrpc.newBlockingStub(channel);
    }

    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    public void greet(String word){
        HelloRequest request = HelloRequest.newBuilder().setName(word).build();
        HelloReply response;

        try{
            response = blockingStub.sayHello(request);
        } catch (StatusRuntimeException e){
            e.printStackTrace();
            return;
        }

        System.out.println(response.getMessage());
    }

    public static void main(String[] args) throws Exception{
        TestClient client = new TestClient("localhost", 8883);
        try {
            String user = "Rocka";
            if(args.length > 0){
                user = args[0];
            }
            client.greet(user);
        }finally {
            client.shutdown();
        }
    }
}
