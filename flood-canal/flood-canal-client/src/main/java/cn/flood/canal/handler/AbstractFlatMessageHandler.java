package cn.flood.canal.handler;

import cn.flood.canal.context.CanalContext;
import cn.flood.canal.model.CanalModel;
import cn.flood.canal.util.HandlerUtil;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.FlatMessage;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractFlatMessageHandler implements MessageHandler<FlatMessage> {

  private Logger log = LoggerFactory.getLogger(AbstractFlatMessageHandler.class);

  private Map<String, List<EntryHandler>> tableHandlerMap;


  private RowDataHandler<List<Map<String, String>>> rowDataHandler;


  private Logger logger = LoggerFactory.getLogger(AbstractFlatMessageHandler.class);


  public AbstractFlatMessageHandler(List<? extends EntryHandler> entryHandlers,
      RowDataHandler<List<Map<String, String>>> rowDataHandler) {
    this.tableHandlerMap = HandlerUtil.getTableHandlerMap(entryHandlers);
    this.rowDataHandler = rowDataHandler;
  }

  @Override
  public void handleMessage(FlatMessage flatMessage) {
    log.info("获取消息 {}", flatMessage);
    List<Map<String, String>> data = flatMessage.getData();
    if (data != null && data.size() > 0) {
      for (int i = 0; i < data.size(); i++) {
        CanalEntry.EventType eventType = CanalEntry.EventType.valueOf(flatMessage.getType());
        List<Map<String, String>> maps;
        if (eventType.equals(CanalEntry.EventType.UPDATE)) {
          Map<String, String> map = data.get(i);
          Map<String, String> oldMap = flatMessage.getOld().get(i);
          maps = Stream.of(map, oldMap).collect(Collectors.toList());
        } else {
          maps = Stream.of(data.get(i)).collect(Collectors.toList());
        }
        try {
          List<EntryHandler> entryHandlerList = HandlerUtil
              .getEntryHandler(tableHandlerMap, flatMessage.getTable());

          CanalModel model = CanalModel.Builder.builder().id(flatMessage.getId())
              .table(flatMessage.getTable())
              .executeTime(flatMessage.getEs()).database(flatMessage.getDatabase())
              .createTime(flatMessage.getTs()).build();
          CanalContext.setModel(model);

          for (EntryHandler entryHandler : entryHandlerList) {
            if (entryHandler != null) {
              rowDataHandler.handlerRowData(maps, entryHandler, eventType);
            }
          }
        } catch (Exception e) {
          throw new RuntimeException("parse event has an error , data:" + data.toString(), e);
        } finally {
          CanalContext.removeModel();
        }
      }
    }
  }
}
