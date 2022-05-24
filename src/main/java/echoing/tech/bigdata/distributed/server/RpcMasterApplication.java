package echoing.tech.bigdata.distributed.server;

import echoing.tech.bigdata.distributed.rpc.RpcServer;
import echoing.tech.bigdata.distributed.client.MysqlTask;

/**
 * @Author: WangGuo
 * @Description:
 * @Date: Created in 8:52 PM 2022/5/24
 * @Modified By:
 */
public class RpcMasterApplication {

    public static void main(String[] args) {
        System.out.println("master started worker...");
        TaskProcessor<MysqlTask> processor = new Master<>(3);
        RpcServer server = new RpcServer();
        try {
            server.export(processor, 9999);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
