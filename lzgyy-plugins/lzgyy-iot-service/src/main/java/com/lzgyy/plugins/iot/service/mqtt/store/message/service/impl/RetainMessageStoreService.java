package com.lzgyy.plugins.iot.service.mqtt.store.message.service.impl;

import cn.hutool.core.util.StrUtil;
import org.apache.ignite.IgniteCache;
import org.springframework.stereotype.Service;

import com.lzgyy.plugins.iot.core.store.message.bean.RetainMessageStore;
import com.lzgyy.plugins.iot.core.store.message.service.IRetainMessageStoreService;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class RetainMessageStoreService implements IRetainMessageStoreService {

	@Resource
	private IgniteCache<String, RetainMessageStore> retainMessageCache;

	@Override
	public void put(String topic, RetainMessageStore retainMessageStore) {
		retainMessageCache.put(topic, retainMessageStore);
	}

	@Override
	public RetainMessageStore get(String topic) {
		return retainMessageCache.get(topic);
	}

	@Override
	public void remove(String topic) {
		retainMessageCache.remove(topic);
	}
	
	@Override
	public void destroy() {
		retainMessageCache.destroy();
	}

	@Override
	public boolean containsKey(String topic) {
		return retainMessageCache.containsKey(topic);
	}

	@Override
	public List<RetainMessageStore> search(String topicFilter) {
		List<RetainMessageStore> retainMessageStores = new ArrayList<RetainMessageStore>();
		if (!StrUtil.contains(topicFilter, '#') && !StrUtil.contains(topicFilter, '+')) {
			if (retainMessageCache.containsKey(topicFilter)) {
				retainMessageStores.add(retainMessageCache.get(topicFilter));
			}
		} else {
			retainMessageCache.forEach(entry -> {
				String topic = entry.getKey();
				if (StrUtil.split(topic, '/').size() >= StrUtil.split(topicFilter, '/').size()) {
					List<String> splitTopics = StrUtil.split(topic, '/');
					List<String> spliteTopicFilters = StrUtil.split(topicFilter, '/');
					String newTopicFilter = "";
					for (int i = 0; i < spliteTopicFilters.size(); i++) {
						String value = spliteTopicFilters.get(i);
						if (value.equals("+")) {
							newTopicFilter = newTopicFilter + "+/";
						} else if (value.equals("#")) {
							newTopicFilter = newTopicFilter + "#/";
							break;
						} else {
							newTopicFilter = newTopicFilter + splitTopics.get(i) + "/";
						}
					}
					newTopicFilter = StrUtil.removeSuffix(newTopicFilter, "/");
					if (topicFilter.equals(newTopicFilter)) {
						RetainMessageStore retainMessageStore = entry.getValue();
						retainMessageStores.add(retainMessageStore);
					}
				}
			});
		}
		return retainMessageStores;
	}
}