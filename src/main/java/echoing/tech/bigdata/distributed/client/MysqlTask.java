package echoing.tech.bigdata.distributed.client;

import echoing.tech.bigdata.distributed.Task;

import java.io.Serializable;

/**
 * @Author: WangGuo
 * @Description:
 * @Date: Created in 1:31 PM 2022/5/24
 * @Modified By:
 */
public class MysqlTask extends Task<Integer> implements Serializable {
    @Override
    public Integer exec() {
        // 模拟执行一个时常为1秒钟的mapreduce任务
        try {
            System.out.println("计算比较复杂, 等待结果中...");
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return 1000;
    }
}
