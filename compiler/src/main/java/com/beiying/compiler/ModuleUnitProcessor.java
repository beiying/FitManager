package com.beiying.compiler;

import com.beiying.annotation.ModuleMeta;
import com.beiying.annotation.ModuleUnit;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;

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

public class ModuleUnitProcessor {

    public static void parseModulesGroup(Set<? extends Element> modulesElements,Logger logger,Filer mFiler,Elements elements) throws IOException {
        if(!modulesElements.isEmpty()) {
            ClassName moduleMetaClassName = ClassName.get(ModuleMeta.class);

            ParameterizedTypeName inputMapTypeOfGroup = ParameterizedTypeName.get(ClassName.get(List.class),
                    ClassName.get(ModuleMeta.class));
            ParameterSpec groupParamSpec = ParameterSpec.builder(inputMapTypeOfGroup, "metaSet").build();

            MethodSpec.Builder loadIntoMethodOfRootBuilder = MethodSpec.methodBuilder(ModuleUtil.METHOD_LOAD_INTO)
                    .returns(void.class)
                    .addAnnotation(Override.class)
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(groupParamSpec);
            for(Element element:modulesElements) {
                ModuleUnit moduleUnit = element.getAnnotation(ModuleUnit.class);
                ClassName name = ClassName.get((TypeElement) element);
                String address = name.packageName() + "." + name.simpleName();
                ModuleMeta moduleMeta = new ModuleMeta(moduleUnit, address);
                Map<String, ModuleMeta> groupMap = new HashMap<>();
                groupMap.put(element.getSimpleName().toString(), moduleMeta);

                String[] nameZone = ModuleUtil.splitDot(moduleMeta.moduleName);
                moduleMeta.title = !moduleMeta.title.equals("FitManager") ? moduleMeta.title:nameZone[nameZone.length - 1];
                String[] templets = ModuleUtil.split(moduleMeta.templet);
                for(String templet:templets) {
                    loadIntoMethodOfRootBuilder.addStatement("metaSet.add(new $T($S,$S,$S,$L,$L)",
                            moduleMetaClassName, templet, moduleMeta.moduleName, moduleMeta.title,
                            moduleMeta.layoutLevel.getValue(), moduleMeta.extraLevel);
                }

                //创建类
                TypeSpec typeSpec = TypeSpec.classBuilder(ModuleUtil.NAME_OF_MODULEUNIT + name.simpleName())
                        .addJavadoc(ModuleUtil.WARNING_TIPS)
                        .addSuperinterface(ClassName.get(elements.getTypeElement(ModuleUtil.IMODULE_UNIT)))
                        .addModifiers(Modifier.PUBLIC)
                        .addMethod(loadIntoMethodOfRootBuilder.build()).build();

                //创建Java文件
                JavaFile file = JavaFile.builder(ModuleUtil.FACADE_PACKAGE, typeSpec)
                        .build();

                file.writeTo(mFiler);
            }


        }

    }


}
