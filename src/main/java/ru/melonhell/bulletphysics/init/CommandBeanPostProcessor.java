package ru.melonhell.bulletphysics.init;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.PaperCommandManager;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommandBeanPostProcessor implements BeanPostProcessor {
    private final PaperCommandManager commandManager;

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof BaseCommand) commandManager.registerCommand((BaseCommand) bean);
        return bean;
    }
}
