/*
 * Copyright 2020. JiaXiaohei easyDebug.net
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package cn.flood.delay.mapper;



import cn.flood.delay.entity.DelayQueueJob;

import java.util.List;
import java.util.Map;

/**
 * 延迟队列任务
 */
public interface TbDelayJobMapper {

    DelayQueueJob get(Map<String, Object> param);

    List<DelayQueueJob> list(Map<String, Object> param);

    int insert(DelayQueueJob delayQueueJob);

    int update(DelayQueueJob delayQueueJob);

    int delete(String id);
    
}
