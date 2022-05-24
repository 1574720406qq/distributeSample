package echoing.tech.bigdata.distributed.server;

import echoing.tech.bigdata.distributed.Task;
import echoing.tech.bigdata.distributed.rpc.RpcClient;
import echoing.tech.bigdata.distributed.rpc.RpcServer;

import java.util.Queue;
import java.util.Random;
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
                if (isAlive(senderWorkerId)) {
                    System.out.printf("task send to %d and port is %d\n", senderWorkerId, ports[senderWorkerId]);
                    try {
                        Task<R> task = taskQueue.poll();
                        final TaskProcessor<R> processor = client.refer(TaskProcessor.class, "127.0.0.1", ports[senderWorkerId]);
                        processor.accept(task);

                        final Queue<R> results = processor.getResults();


                        while (!results.isEmpty()) {
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

    //判断worker是否挂了, 如果挂了重新分配任务
    public boolean isAlive(int workerId) {
        return true;
    }

}
