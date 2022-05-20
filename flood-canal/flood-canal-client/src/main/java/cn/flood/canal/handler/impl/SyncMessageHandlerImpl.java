package cn.flood.canal.handler.impl;

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.Message;
import cn.flood.canal.handler.AbstractMessageHandler;
import cn.flood.canal.handler.EntryHandler;
import cn.flood.canal.handler.RowDataHandler;


import java.util.List;

/**
 * @author yang peng
 * @date 2019/3/2710:52
 */
public class SyncMessageHandlerImpl extends AbstractMessageHandler {


    public SyncMessageHandlerImpl(List<? extends EntryHandler> entryHandlers, RowDataHandler<CanalEntry.RowData> rowDataHandler) {
        super(entryHandlers, rowDataHandler);
    }

    @Override
    public void handleMessage(Message message) {
        super.handleMessage(message);
    }


}
