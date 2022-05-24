package echoing.tech.bigdata.single;

/**
 * @Author: WangGuo
 * @Description:
 * @Date: Created in 9:35 AM 2022/5/24
 * @Modified By:
 */
public class NumAddTask extends Task<Long> {

    private final long init;

    public NumAddTask(long init) {
        this.init = init;
    }

    @Override
    protected Long exec() {
        long res = 0;
        // 每个线程累加10万
        for (int i = 0; i < 100000; i++) {
            res += i + init;
        }
        return res;
    }
}
