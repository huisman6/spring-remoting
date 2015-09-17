package com.youzhixu.springremoting.example.provider.listener;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

/**
 * just print all registered bean (invoker &exporter)
 */
public class ApplicationInitListener implements ApplicationListener<ContextRefreshedEvent> {
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		if (event.getApplicationContext().getParent() == null) {
			ApplicationContext app = event.getApplicationContext();
			for (String beanName : app.getBeanDefinitionNames()) {
				System.out
						.println("registed: name=" + beanName + ",class=" + app.getBean(beanName));
			}
		}
	}

}
