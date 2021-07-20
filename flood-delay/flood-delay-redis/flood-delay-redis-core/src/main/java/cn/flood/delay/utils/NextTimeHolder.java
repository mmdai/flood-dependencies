package cn.flood.delay.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicLong;
import java.util.function.LongUnaryOperator;

/**
 * @Description 记录下一次执行的时间
 * @Author daimi
 * @Date 2019/7/31 5:42 PM
 **/
public class NextTimeHolder {

    private static final Logger logger = LoggerFactory.getLogger(NextTimeHolder.class);

    public static AtomicLong nextTime = new AtomicLong(0);


    /**
     * 此方法为线程安全
     * 尝试将nextTime修改为newTime;
     * 因为是循环CAS操作,修改成功为止
     * 所以如果newTime< nextTime则修改为newTime
     * 否则还是修改为nextTime;
     * 如果修改之后的值是newTime ;则需要通知一下LockUtil.lock 醒过来;
     * 如果仍旧是nextTime说明要么当前的执行时间很后面或者说被另外一个线程修改为了更小的值
     * @param newTime  新的执行时间
     */
    public static void tryUpdate(long newTime){
        LongUnaryOperator updateFunction = (a)->{
            if(newTime<nextTime.get()){
                logger.warn("==================尝试更新为newTime:{}==================",newTime);
                return newTime;
            }
            return nextTime.get();
        };
        long next = nextTime.updateAndGet(updateFunction);

        //如果最终更新的不是  newTime说明已经被其他线程更新为更小的数了;
        if(next == newTime){
            synchronized (LockUtil.lock){
                //更新了通知值更新了
                LockUtil.lock.notifyAll();
            }
        }

    }


    /**
     * 将nextTime设置为0之后  并且通知  就会立马执行一次搬运操作
     */
    public static void setZeroAndNotify(){
        synchronized (LockUtil.lock){
            NextTimeHolder.nextTime.set(0);
            LockUtil.lock.notifyAll();
        }

    }
}
