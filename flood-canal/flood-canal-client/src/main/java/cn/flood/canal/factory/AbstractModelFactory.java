package cn.flood.canal.factory;

import cn.flood.canal.enums.TableNameEnum;
import cn.flood.canal.handler.EntryHandler;
import cn.flood.canal.util.GenericUtil;
import cn.flood.canal.util.HandlerUtil;

public abstract class AbstractModelFactory<T> implements IModelFactory<T> {


  @Override
  public <R> R newInstance(EntryHandler entryHandler, T t) throws Exception {
    String canalTableName = HandlerUtil.getCanalTableName(entryHandler);
    if (TableNameEnum.ALL.name().toLowerCase().equals(canalTableName)) {
      return (R) t;
    }
    Class<R> tableClass = GenericUtil.getTableClass(entryHandler);
    if (tableClass != null) {
      return newInstance(tableClass, t);
    }
    return null;
  }


  abstract <R> R newInstance(Class<R> c, T t) throws Exception;
}
