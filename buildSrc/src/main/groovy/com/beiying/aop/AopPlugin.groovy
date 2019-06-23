package com.beiying.aop

import com.android.build.gradle.AppExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

class AopPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.task("testTask",{
            println("-----------------")
            println("Hello Aop Plugin")
            println("-----------------")
        });

        //创建插件的自定义参数
//        project.extensions.create('pluginExt', PluginExtension)
//        project.pluginExt.extensions.create('nestExt', PluginNestExtension)
//        project.task('customTask', type: AopCustomTask)

        //监听task执行过程，可以获取每个task的执行时间
//        project.gradle.addListener(new TimeListener())

        //把Transform注入到打包过程中

        def android = project.extensions.getByType(AppExtension);
        android.registerTransform(new AsmTransform(project))
    }
}