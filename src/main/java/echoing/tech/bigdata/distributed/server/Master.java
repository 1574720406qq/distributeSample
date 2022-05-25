package echoing.tech.bigdata.distributed.server;

import echoing.tech.bigdata.distributed.Task;
import echoing.tech.bigdata.distributed.rpc.RpcClient;
import echoing.tech.bigdata.distributed.rpc.RpcServer;

import java.util.Queue;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author: WangGuo
 * @Description:
 * @Date: Created in 11:09 AM 2022/5/24
 * @Modified By:
 */
public class Master<R> implements TaskProcessor<R>{

    Random randomWorker = new Random();

    int[] ports = {6666, 7777, 8888};

    RpcClient client = new RpcClient();

    AtomicInteger portIndex = new AtomicInteger();

    AtomicInteger inputTasks = new AtomicInteger();

    AtomicInteger finishedTasks = new AtomicInteger();

    Queue<Task<R>> taskQueue;

    Queue<R> resultQueue;

    public Master(int workerNumbers) {
        taskQueue = new ConcurrentLinkedDeque<>();
        resultQueue = new ConcurrentLinkedDeque<>();
        startSpecifiedWorker(workerNumbers);
        new Thread(this::distributeTasks).start();
    }

    private void startSpecifiedWorker(int workerNumbers) {
        for (int i = 0; i < workerNumbers; i++) {
            new Thread(this::startWorker).start();
        }
    }

    private void startWorker() {
        int port = portIndex.getAndIncrement();
        System.out.printf("worker %d start and port is %d\n", port, ports[port]);
        TaskProcessor<Task<R>> processor = new Worker<>();
        RpcServer server = new RpcServer();
        try {
            server.export(processor, ports[port]);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    //将任务分发给worker
    public synchronized void distributeTasks() {
        while (true) {
            if (!taskQueue.isEmpty()) {
                final int senderWorkerId = randomWorker.nextInt(3);
                //todo
                // 如果对应的worker挂了, 这里需要将挂掉的worker剔除, 并且重新分配任务
                if (isAlive(senderWorkerId)) {
                    System.out.printf("task send to %d and port is %d\n", senderWorkerId, ports[senderWorkerId]);
                    try {
                        Task<R> task = taskQueue.poll();
                        final TaskProcessor<R> processor = client.refer(TaskProcessor.class, "127.0.0.1", ports[senderWorkerId]);
                        processor.accept(task);

                        final Queue<R> results = processor.getResults();
                        //todo
                        // 这里针对不同client发送的不同阶段的任务好需要进行优化, 因为没办法将不同阶段任务分配好发送到对应的client
                        // 客户端发送的任务携带客户端id和对应的唯一任务id, 服务端在对这类任务进行处理时, 同一阶段的不同client任务统一处理
                        if (!results.isEmpty()) {
                            resultQueue.offer(results.poll());
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    //接收Client发送的任务请求, 并且将任务分发给所有的worker
    @Override
    public void accept(Task<R> task) {
        inputTasks.incrementAndGet();
        taskQueue.add(task);
    }

    @Override
    public Queue<R> getResults() {
        //TODO
        // 这里针对不同客户端的不同任务, 还需要进行优化
        // 1. 单客户端多任务
        // 2. 多客户端单任务
        // 3. 多客户端多任务
        int size = resultQueue.size();
        while (size-- > 0) {
            finishedTasks.getAndIncrement();
        }
        while (inputTasks.get() != finishedTasks.get()) {
            System.out.printf("input tasks number is %d, finish tasks number is %d\n", inputTasks.get(), finishedTasks.get());
        }
        Queue<R> result = new ConcurrentLinkedDeque<>(resultQueue);
        resultQueue.clear();
        return result;
    }

    //TODO worker 定时往master发送心跳证明自己存活
    // 判断worker是否挂了, 如果挂了重新分配任务
    // 这里需要master
    // 这里通过建立长链接即可做简单的实现
    public boolean isAlive(int workerId) {
        return true;
    }

}
