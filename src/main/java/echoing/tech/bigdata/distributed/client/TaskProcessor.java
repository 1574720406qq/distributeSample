package echoing.tech.bigdata.distributed.client;

import echoing.tech.bigdata.distributed.Task;

import java.io.Serializable;
import java.util.Queue;

/**
 * @Author: WangGuo
 * @Description:
 * @Date: Created in 1:18 PM 2022/5/24
 * @Modified By:
 */
public interface TaskProcessor<R> extends Serializable {

    public void accept(Task<R> task);
    
    public Queue<R> getResults();

}
