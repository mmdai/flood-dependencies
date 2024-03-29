/**
 * Copyright (c) 2018-2028, Chill Zhuang 庄骞 (smallchill@163.com).
 * <p>
 * Licensed under the GNU LESSER GENERAL PUBLIC LICENSE 3.0; you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * <p>
 * http://www.gnu.org/licenses/lgpl.html
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package cn.flood.base.core.note;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 树型节点类
 *
 * @author mmdai
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class TreeNode extends BaseNode {

  private static final long serialVersionUID = 1L;

  private String title;

  @JsonSerialize(using = ToStringSerializer.class)
  private String key;

  @JsonSerialize(using = ToStringSerializer.class)
  private String value;

}
