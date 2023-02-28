package cn.flood.canal.handler.impl;

import cn.flood.canal.handler.AbstractFlatMessageHandler;
import cn.flood.canal.handler.EntryHandler;
import cn.flood.canal.handler.RowDataHandler;
import com.alibaba.otter.canal.protocol.FlatMessage;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

public class AsyncFlatMessageHandlerImpl extends AbstractFlatMessageHandler {


  private ExecutorService executor;


  public AsyncFlatMessageHandlerImpl(List<? extends EntryHandler> entryHandlers,
      RowDataHandler<List<Map<String, String>>> rowDataHandler, ExecutorService executor) {
    super(entryHandlers, rowDataHandler);
    this.executor = executor;
  }

  @Override
  public void handleMessage(FlatMessage flatMessage) {
    executor.execute(() -> super.handleMessage(flatMessage));
  }
}
