package echoing.tech.bigdata.single;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @Author: WangGuo
 * @Description:
 * @Date: Created in 9:36 AM 2022/5/24
 * @Modified By:
 */
public class TestMaster {
    public static void main(String[] args) throws InterruptedException {
        AtomicLong totalCount = new AtomicLong(0);
        Master<NumAddTask, Long> master = new Master<>(4);
        master.submit(new NumAddTask(1), totalCount::addAndGet);
        master.submit(new NumAddTask(100001), totalCount::addAndGet);
        Thread.sleep(3000);
        System.out.println("total Count = " + totalCount);

    }
}
