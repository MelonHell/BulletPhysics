package ru.melonhell.bulletphysics.init

import co.aikar.commands.BaseCommand
import co.aikar.commands.PaperCommandManager
import org.springframework.beans.BeansException
import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.stereotype.Component

@Component
class CommandBeanPostProcessor(private val commandManager: PaperCommandManager) : BeanPostProcessor {
    override fun postProcessAfterInitialization(bean: Any, beanName: String): Any {
        if (bean is BaseCommand) commandManager.registerCommand(bean)
        return bean
    }
}