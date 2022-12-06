/**
 * Copyright (c) 2018-2028, Chill Zhuang 庄骞 (smallchill@163.com).
 * <p>
 * Licensed under the GNU LESSER GENERAL PUBLIC LICENSE 3.0;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.gnu.org/licenses/lgpl.html
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.flood.base.filter;

import cn.flood.base.core.support.xss.FloodRequestFilter;
import cn.flood.base.core.support.xss.XssProperties;
import lombok.AllArgsConstructor;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

import org.springframework.core.Ordered;
import javax.servlet.DispatcherType;

/**
 * Xss配置类
 *
 * @author Chill
 */
@AutoConfiguration
@AllArgsConstructor
@EnableConfigurationProperties({XssProperties.class})
public class XssRequestConfig {

	private final XssProperties xssProperties;

	/**
	 * 防XSS注入
	 *
	 * @return FilterRegistrationBean
	 */
	@Bean
	public FilterRegistrationBean<FloodRequestFilter> xssFilterRegistration() {
		FilterRegistrationBean<FloodRequestFilter> registration = new FilterRegistrationBean<>();
		registration.setDispatcherTypes(DispatcherType.REQUEST);
		registration.setFilter(new FloodRequestFilter(xssProperties));
		registration.addUrlPatterns("/*");
		registration.setName("floodRequestFilter");
		registration.setOrder(Ordered.LOWEST_PRECEDENCE);
		return registration;
	}
}
