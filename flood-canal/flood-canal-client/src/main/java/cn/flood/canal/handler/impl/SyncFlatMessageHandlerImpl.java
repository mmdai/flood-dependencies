package cn.flood.canal.handler.impl;

import cn.flood.canal.handler.AbstractFlatMessageHandler;
import cn.flood.canal.handler.EntryHandler;
import cn.flood.canal.handler.RowDataHandler;
import com.alibaba.otter.canal.protocol.FlatMessage;
import java.util.List;
import java.util.Map;

public class SyncFlatMessageHandlerImpl extends AbstractFlatMessageHandler {


  public SyncFlatMessageHandlerImpl(List<? extends EntryHandler> entryHandlers,
      RowDataHandler<List<Map<String, String>>> rowDataHandler) {
    super(entryHandlers, rowDataHandler);
  }

  @Override
  public void handleMessage(FlatMessage flatMessage) {
    super.handleMessage(flatMessage);
  }
}
