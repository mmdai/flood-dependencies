/**
 * 名晟中鑫是一家专业数字化、智慧化研发和运营的服务提供商，公司本着“星火燎原、创新创业、科技顶天、服务立地”的发展理念，
 * 致力于新型智慧城市的建设和运营，业务包括智慧城市顶层设计咨询、互联网+、智慧政务、智慧医疗、智慧社区、智慧交通、智慧农业、智慧教育、智慧应急等。
 * 积极构建以政府为主导，企业和市民为主体、市场为导向、产学研相结合的推进体系，资源有效整合与共享，不断增强建设智慧城市建设的整体合力，
 * 真正推动民众生存智慧化。
 **/
package cn.flood.base.core.page;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

/**
 * @author mmdai
 * @version 1.0.0
 * @ClassName SortablePageDTO
 * @Description
 * @createTime 2024年07月30日 15:37
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class SortablePageDTO extends PageDTO {

    /**
     * 排序字段
     */
    private List<SortingField> sortingFields;
}
