package echoing.tech.bigdata.distributed;

import java.util.function.Consumer;

public abstract class Task<R> {

    //描述具体的任务
    public abstract R exec();

}
