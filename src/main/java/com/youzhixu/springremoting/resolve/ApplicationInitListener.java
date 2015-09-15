package com.youzhixu.springremoting.resolve;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
public class ApplicationInitListener implements ApplicationListener<ContextRefreshedEvent> {



	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		if (event.getApplicationContext().getParent() == null) {
			ApplicationContext app = event.getApplicationContext();
			for (String beanNames : app.getBeanDefinitionNames()) {
				System.out.println("register bean:" + beanNames);
			}

		}
	}

}
