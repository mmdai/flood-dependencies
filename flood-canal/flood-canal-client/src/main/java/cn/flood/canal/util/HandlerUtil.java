package cn.flood.canal.util;


import cn.flood.canal.annotation.CanalTable;
import cn.flood.canal.enums.TableNameEnum;
import cn.flood.canal.handler.EntryHandler;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yang peng
 * @date 2019/3/2713:33
 */
public class HandlerUtil {


  public static EntryHandler getEntryHandler(List<? extends EntryHandler> entryHandlers,
      String tableName) {
    EntryHandler globalHandler = null;
    for (EntryHandler handler : entryHandlers) {
      String canalTableName = getCanalTableName(handler);
      if (TableNameEnum.ALL.name().toLowerCase().equals(canalTableName)) {
        globalHandler = handler;
        continue;
      }
      if (tableName.equals(canalTableName)) {
        return handler;
      }
      String name = GenericUtil.getTableGenericProperties(handler);
      if (name != null) {
        if (name.equals(tableName)) {
          return handler;
        }
      }
    }
    return globalHandler;
  }


  public static Map<String, List<EntryHandler>> getTableHandlerMap(
      List<? extends EntryHandler> entryHandlers) {
    Map<String, List<EntryHandler>> map = new ConcurrentHashMap<>();
    if (entryHandlers != null && entryHandlers.size() > 0) {
      for (EntryHandler handler : entryHandlers) {
        String canalTableName = getCanalTableName(handler);
        String key = TableNameEnum.ALL.name().toLowerCase();
        if (canalTableName != null) {
          key = canalTableName.toLowerCase();
        } else {
          String name = GenericUtil.getTableGenericProperties(handler);
          if (name != null) {
            key = name.toLowerCase();
          }
        }
        if (map.containsKey(key)) {
          List<EntryHandler> entryHandlerList = map.get(key);
          entryHandlerList.add(handler);
        } else {
          List<EntryHandler> entryHandlerList = Lists.newArrayList();
          entryHandlerList.add(handler);
          map.putIfAbsent(key, entryHandlerList);
        }
      }
    }
    return map;
  }


  public static List<EntryHandler> getEntryHandler(Map<String, List<EntryHandler>> map,
      String tableName) {
    List<EntryHandler> entryHandler = map.get(tableName);
    if (entryHandler == null) {
      return map.get(TableNameEnum.ALL.name().toLowerCase());
    }
    return entryHandler;
  }


  public static String getCanalTableName(EntryHandler entryHandler) {
    CanalTable canalTable = entryHandler.getClass().getAnnotation(CanalTable.class);
    if (canalTable != null) {
      return canalTable.value();
    }
    return null;
  }

}
