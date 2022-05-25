package echoing.tech.bigdata.distributed.server;

import echoing.tech.bigdata.distributed.Task;

import java.io.Serializable;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author: WangGuo
 * @Description:
 * @Date: Created in 11:09 AM 2022/5/24
 * @Modified By:
 */
public class Worker<R> implements TaskProcessor<R>, Serializable {

    AtomicInteger inputTasks = new AtomicInteger();

    AtomicInteger finishedTasks = new AtomicInteger();
    //执行任务, 并返回结果
    private final Queue<Task<R>> tasksQueue;

    private final Queue<R> resultsQueue;

    public Worker() {
        tasksQueue = new ArrayBlockingQueue<>(100);
        resultsQueue = new ArrayBlockingQueue<>(100);
        final Thread thread = new Thread(this::executeTask);
        thread.start();
    }

    public void executeTask() {
        while (true) {
            if (!tasksQueue.isEmpty()) {
                final Task<R> header = tasksQueue.poll();
                final R exec = header.exec();
                resultsQueue.offer(exec);
                finishedTasks.incrementAndGet();
            }
        }
    }

    @Override
    public Queue<R> getResults() {
        while (finishedTasks.get() != inputTasks.get()) {

        }
        System.out.println("结果出来啰");
        Queue<R> result = new ConcurrentLinkedDeque<>(resultsQueue);
        resultsQueue.clear();
        return result;
    }

    @Override
    public void accept(Task<R> task) {
        inputTasks.incrementAndGet();
        tasksQueue.add(task);
    }
}
