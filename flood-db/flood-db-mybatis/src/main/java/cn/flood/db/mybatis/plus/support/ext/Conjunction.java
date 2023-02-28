package cn.flood.db.mybatis.plus.support.ext;

import cn.flood.db.mybatis.plus.support.Criterion;
import java.util.Collection;

/**
 * SQL 条件连接符，默认使用 AND
 *
 * @author mmdai
 */
public class Conjunction extends Junction {

  public Conjunction() {
    super(Nature.AND);
  }

  protected Conjunction(Criterion... criterion) {
    super(Nature.AND, criterion);
  }

  public Conjunction(Collection<Criterion> criterions) {
    super(Nature.AND, criterions);
  }

}
