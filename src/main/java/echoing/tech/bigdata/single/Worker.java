package echoing.tech.bigdata.single;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author: WangGuo
 * @Description:
 * @Date: Created in 9:30 AM 2022/5/24
 * @Modified By:
 */
public class Worker<R> {

    private final ArrayBlockingQueue<Task<R>> taskQueue = new ArrayBlockingQueue<>(32);

    private static final AtomicInteger WORKER_ID_GENERATOR = new AtomicInteger();

    private final int workerId;

    private final Thread workerThread;

    public Worker() {
        this.workerId = WORKER_ID_GENERATOR.getAndIncrement();
        workerThread = new Thread(this::execute);
        workerThread.start();
    }

    private void execute() {
        while (true) {
            try {
                Task<R> task = taskQueue.take();
                task.setWorkerId(workerId);
                task.execute();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void submit(Task<R> task) {
        taskQueue.offer(task);
    }

}

