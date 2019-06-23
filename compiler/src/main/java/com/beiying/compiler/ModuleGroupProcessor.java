package com.beiying.compiler;

import com.beiying.annotation.ModuleGroup;
import com.beiying.annotation.ModuleMeta;
import com.beiying.annotation.ModuleUnit;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;

import org.apache.commons.collections4.CollectionUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

public class ModuleGroupProcessor {

    public static void parseModulesGroup(Set<? extends Element> modulesElements, Logger logger, Filer mFiler, Elements elements) throws IOException{
        if (CollectionUtils.isNotEmpty(modulesElements)) {
            logger.info(">>> Found moduleGroup, size is " + modulesElements.size() + " <<<");
            for (Element element:modulesElements){
                if (element!=null){
                    //遍历moduleGroup
                    ModuleGroup group = element.getAnnotation(ModuleGroup.class);
                    if (group!=null){
                        ModuleUnit[] units = group.value();
                        //解析ModuleUnit数据，并生成IModuleUnit文件
                        parseModules(units,element,logger,mFiler,elements);
                    }
                }
            }
        }
    }

    public static void parseModules(ModuleUnit[] modulesElements,Element element,Logger logger,Filer mFiler,Elements elements) throws IOException {
        if (modulesElements.length > 0){
            logger.info(">>> Found moduleUnit, size is " + modulesElements.length + " <<<");

            //ModuleMeta类
            ClassName moduleMetaCn = ClassName.get(ModuleMeta.class);

            ParameterizedTypeName inputMapTypeOfGroup = ParameterizedTypeName.get(
                    ClassName.get(List.class),
                    ClassName.get(ModuleMeta.class)
            );

            //groups参数
            ParameterSpec groupParamSpec = ParameterSpec.builder(inputMapTypeOfGroup,"metaSet").build();

            //添加loadInto方法
            MethodSpec.Builder loadIntoMethodOfRootBuilder = MethodSpec.methodBuilder(ModuleUtil.METHOD_LOAD_INTO)
                    .returns(void.class)
                    .addAnnotation(Override.class)
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(groupParamSpec);

            ClassName name = ClassName.get(((TypeElement)element));
            String address = name.packageName()+"."+name.simpleName();  //真实模块入口地址 包名+类名
            for (int i = 0;i<modulesElements.length;i++){
                ModuleUnit moduleUnit=modulesElements[i];
                ModuleMeta moduleMeta= new ModuleMeta(moduleUnit,address);
                Map<String, ModuleMeta> groupMap = new HashMap<>();
                groupMap.put(element.getSimpleName().toString(),moduleMeta);

                String[] nameZone = ModuleUtil.splitDot(moduleMeta.moduleName);
                moduleMeta.title = !moduleMeta.title.equals("CangWang") ? moduleMeta.title:nameZone[ nameZone.length-1];

                loadIntoMethodOfRootBuilder.addStatement("metaSet.add(new $T($S,$S,$S,$L,$L))",
                        moduleMetaCn,
                        moduleMeta.templet,
                        moduleMeta.moduleName,
                        moduleMeta.title,
                        moduleMeta.layoutLevel.getValue(),
                        moduleMeta.extraLevel
                );
                logger.info(">>> build moduleUnit,moduleMeta = " + moduleMeta.toString() + " <<<");
            }

            //构造java文件
            JavaFile.builder(ModuleUtil.FACADE_PACKAGE,
                    TypeSpec.classBuilder(ModuleUtil.NAME_OF_MODULEUNIT+name.simpleName())
                            .addJavadoc(ModuleUtil.WARNING_TIPS)
                            .addSuperinterface(ClassName.get(elements.getTypeElement(ModuleUtil.IMODULE_UNIT)))
                            .addModifiers(Modifier.PUBLIC)
                            .addMethod(loadIntoMethodOfRootBuilder.build())
                            .build()
            ).build().writeTo(mFiler);
        }
    }
}
