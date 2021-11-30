package com.katouyi.tools.proto.test1.grpc;

/*public class NumberAddServer {

    public static void main(String[] args) throws IOException {
        ServerBuilder.forPort(9999)
                .addService(new AddServer())
                .build()
                .start();
        System.out.println("server start at 9999");
        while (true){

        }
    }

    public void add(AddRequest request, StreamObserver<AddReply> responseObserver) {
        int res = myAdd(request.getA(),request.getB());
        responseObserver.onNext(AddReply.newBuilder().setRes(res).build());
        responseObserver.onCompleted();
    }

    private int myAdd(int a, int b){
        return a + b;
    }

}*/
