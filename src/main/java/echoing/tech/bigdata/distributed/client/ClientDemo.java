package echoing.tech.bigdata.distributed.client;

import echoing.tech.bigdata.distributed.Task;
import echoing.tech.bigdata.distributed.rpc.RpcClient;

import java.util.Queue;

/**
 * @Author: WangGuo
 * @Description:
 * @Date: Created in 1:04 PM 2022/5/24
 * @Modified By:
 */
public class ClientDemo {
    public static void main(String[] args) throws Exception {
        RpcClient client = new RpcClient();
        Task<Integer> task1 = new MysqlTask();
        Task<Integer> task2 = new MysqlTask();
        Task<Integer> task3 = new MysqlTask();
        Task<Integer> task4 = new MysqlTask();
        Task<Integer> task5 = new MysqlTask();

        final TaskProcessor<Integer> processor = client.refer(TaskProcessor.class, "127.0.0.1", 9999);

        processor.accept(task1);
        processor.accept(task2);
        processor.accept(task3);
        processor.accept(task4);
        processor.accept(task5);

        Thread.sleep(10000);

        final Queue<Integer> results = processor.getResults();
        System.out.println(results);
    }
}
