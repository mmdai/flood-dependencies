package cn.flood.mybatis.plus.support.ext;

import cn.flood.mybatis.plus.support.Criterion;

/**
 * @author mmdai
 */
public class Disjunction extends Junction {

    protected Disjunction() {
        super( Nature.OR );
    }

    protected Disjunction(Criterion[] conditions) {
        super( Nature.OR, conditions );
    }
}
