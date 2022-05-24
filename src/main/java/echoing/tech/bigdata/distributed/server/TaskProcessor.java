package echoing.tech.bigdata.distributed.server;

import echoing.tech.bigdata.distributed.Task;

import java.util.Queue;

/**
 * @Author: WangGuo
 * @Description:
 * @Date: Created in 2:26 PM 2022/5/24
 * @Modified By:
 */
public interface TaskProcessor<R> {
    public void accept(Task<R> task);

    public Queue<R> getResults();

}
