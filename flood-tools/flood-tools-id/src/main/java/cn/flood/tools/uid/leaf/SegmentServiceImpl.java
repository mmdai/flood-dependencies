package cn.flood.tools.uid.leaf;

import cn.flood.base.core.Func;
import cn.flood.tools.uid.baidu.utils.NamingThreadFactory;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;

/**
 * @类名称 SegmentServiceImpl.java
 * @类描述 <pre>Segment 策略id生成实现类</pre>
 * @作者 庄梦蝶殇 linhuaichuan1989@126.com
 * @创建时间 2018年9月6日 下午4:28:36
 * @版本 1.0.2
 * @修改记录 <pre>
 *     版本                       修改人 		修改日期 		 修改内容描述
 *     ----------------------------------------------
 *     1.0.0    庄梦蝶殇    2018年09月06日
 *     1.0.1    庄梦蝶殇    2019年02月28日             更改阈值碰撞时重复执行问题
 *     1.0.2    庄梦蝶殇    2019年03月06日             突破并发数被step限制的bug
 *     ----------------------------------------------
 * </pre>
 */
public class SegmentServiceImpl implements ISegmentService {

  /**
   * 线程名-心跳
   */
  public static final String THREAD_BUFFER_NAME = "leaf_buffer_sw";
  /**
   * 每次请求获取的ID个数，当系统用完缓存中的ID后会再创请求获取ID号段并更新max_id值
   */
  private static final Long step = 1000L;
  /**
   * 当前最大ID值，如果是原来已经是自增ID且ID已经达到某值（如：92），则这里max_id可以设置为93
   */
  private static final Long maxId = 0L;
  private static ReentrantLock lock = new ReentrantLock();
  /**
   * 异步线程任务
   */
  FutureTask<Boolean> asynLoadSegmentTask = null;
  /**
   * 创建线程池
   */
  private ExecutorService taskExecutor;
  /**
   * 两段buffer
   */
  private volatile IdSegment[] segment = new IdSegment[2];
  /**
   * 缓冲切换标识(true-切换，false-不切换)
   */
  private volatile boolean sw;
  /**
   * 当前id
   */
  private AtomicLong currentId;
  private JdbcTemplate jdbcTemplate;
  /**
   * 业务标识
   */
  private String bizTag;
  /**
   * 异步标识(true-异步，false-同步)
   */
  private boolean asynLoadingSegment = true;

  public SegmentServiceImpl(JdbcTemplate jdbcTemplate, String bizTag) {
    if (Func.isBlank(bizTag)) {
      bizTag = "default";
    }
    this.jdbcTemplate = jdbcTemplate;
    if (taskExecutor == null) {
      taskExecutor = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS,
          new LinkedBlockingQueue<Runnable>(), new NamingThreadFactory(THREAD_BUFFER_NAME));
    }
    this.bizTag = bizTag;
    // 获取第一段buffer缓冲
    segment[0] = doUpdateNextSegment(bizTag);
    setSw(false);
    // 初始id
    currentId = new AtomicLong(segment[index()].getMinId());
  }

  @Override
  public Long getId() {
    // 1.0.1 fix:uid:ecp-190227001 #1(github)更改阈值(middle与max)lock在高速碰撞时的可能多次执行
    // 下一个id
    Long nextId = null;
    if (segment[index()].getMiddleId().equals(currentId.longValue()) || segment[index()].getMaxId()
        .equals(currentId.longValue())) {
      lock.lock();
      try {
        // 阈值50%时，加载下一个buffer
        if (segment[index()].getMiddleId().equals(currentId.longValue())) {
          thresholdHandler();
          nextId = currentId.incrementAndGet();
        }
        if (segment[index()].getMaxId().equals(currentId.longValue())) {
          fullHandler();
          nextId = currentId.incrementAndGet();
        }
      } finally {
        lock.unlock();
      }
    }
    nextId = null == nextId ? currentId.incrementAndGet() : nextId;
    // 1.0.2 fix:uid:ecp-190306001 突破并发数被step限制的bug
    return nextId <= segment[index()].getMaxId() ? nextId : getId();
  }

  /**
   * 阈值处理，是否同/异步加载下一个buffer的值(即更新DB)
   */
  private void thresholdHandler() {
    if (asynLoadingSegment) {
      // 异步处理-启动线程更新DB，由线程池执行
      asynLoadSegmentTask = new FutureTask<>(new Callable<Boolean>() {
        @Override
        public Boolean call()
            throws Exception {
          final int currentIndex = reIndex();
          segment[currentIndex] = doUpdateNextSegment(bizTag);
          return true;
        }
      });
      taskExecutor.submit(asynLoadSegmentTask);
    } else {
      // 同步处理，直接更新DB
      final int currentIndex = reIndex();
      segment[currentIndex] = doUpdateNextSegment(bizTag);
    }
  }

  /**
   * buffer使用完时切换buffer。
   */
  public void fullHandler() {
    if (asynLoadingSegment) {
      // 异步时，需判断 异步线程的状态(是否已经执行)
      try {
        asynLoadSegmentTask.get();
      } catch (Exception e) {
        e.printStackTrace();
        // 未执行，强制同步切换
        segment[reIndex()] = doUpdateNextSegment(bizTag);
      }
    }
    // 设置切换标识
    setSw(!isSw());
    // 进行切换
    currentId = new AtomicLong(segment[index()].getMinId());
  }

  /**
   * 获取下一个buffer的索引
   */
  private int reIndex() {
    return isSw() ? 0 : 1;
  }

  /**
   * 获取当前buffer的索引
   */
  private int index() {
    return isSw() ? 1 : 0;
  }

  /**
   * @param bizTag 业务标识
   * @return 下一个buffer的分段id实体
   * @方法名称 doUpdateNextSegment
   * @功能描述 <pre>更新下一个buffer</pre>
   */
  private IdSegment doUpdateNextSegment(String bizTag) {
    if (Func.isBlank(bizTag)) {
      bizTag = "default";
    }
    try {
      return updateId(bizTag);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  private IdSegment updateId(String bizTag)
      throws Exception {
    String querySql = "select step, max_id, last_update_time, current_update_time from id_segment where biz_tag=?";
    String updateSql = "update id_segment set max_id=?, last_update_time=?, current_update_time=? where biz_tag=? and max_id=?";
    final IdSegment currentSegment = new IdSegment();
    this.jdbcTemplate.query(querySql, new RowCallbackHandler() {
      @Override
      public void processRow(ResultSet rs)
          throws SQLException {
        Long step = null;
        Long currentMaxId = null;
        step = rs.getLong("step");
        currentMaxId = rs.getLong("max_id");
        Date lastUpdateTime = new Date();
        if (rs.getTimestamp("last_update_time") != null) {
          lastUpdateTime = (Date) rs.getTimestamp("last_update_time");
        }
        Date currentUpdateTime = new Date();
        if (rs.getTimestamp("current_update_time") != null) {
          currentUpdateTime = (Date) rs.getTimestamp("current_update_time");
        }
        currentSegment.setStep(step);
        currentSegment.setMaxId(currentMaxId);
        currentSegment.setLastUpdateTime(lastUpdateTime);
        currentSegment.setCurrentUpdateTime(currentUpdateTime);
      }
    }, new String[]{bizTag});
    //不存在插入
    if (currentSegment.getMaxId() == null) {
      String insertSql = "insert into id_segment (biz_tag, step, max_id, last_update_time, current_update_time) VALUES (?, ?, ?, ?, ?)";
      this.jdbcTemplate
          .update(insertSql, new Object[]{bizTag, step, maxId, new Date(), new Date()});
      currentSegment.setStep(step);
      currentSegment.setMaxId(maxId);
    }
    Long newMaxId = currentSegment.getMaxId() + currentSegment.getStep();
    int row = this.jdbcTemplate.update(updateSql,
        new Object[]{newMaxId, currentSegment.getCurrentUpdateTime(), new Date(), bizTag,
            currentSegment.getMaxId()});
    if (row == 1) {
      IdSegment newSegment = new IdSegment();
      newSegment.setStep(currentSegment.getStep());
      newSegment.setMaxId(newMaxId);
      return newSegment;
    } else {
      // 递归，直至更新成功
      return updateId(bizTag);
    }
  }

  public void setTaskExecutor(ExecutorService taskExecutor) {
    this.taskExecutor = taskExecutor;
  }

  private boolean isSw() {
    return sw;
  }

  private void setSw(boolean sw) {
    this.sw = sw;
  }

  public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public void setBizTag(String bizTag) {
    this.bizTag = bizTag;
  }

  public void setAsynLoadingSegment(boolean asynLoadingSegment) {
    this.asynLoadingSegment = asynLoadingSegment;
  }
}
