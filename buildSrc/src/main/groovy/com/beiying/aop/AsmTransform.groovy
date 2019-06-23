package com.beiying.aop

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import com.beiying.asm.TestMethodClassAdapter
import org.apache.commons.io.FileUtils
import org.gradle.api.Project
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter

class AsmTransform extends Transform {
    Project mProject;

    AsmTransform(Project project) {
        this.mProject = project;
    }

    /**
     * TransformInvocation包含了输入、输出相关信息。其输出相关内容是由TransformOutputProvider来做处理。
     * TransformOutputProvider的getContentLocation()方法可以获取文件的输出目录，如果目录存在的话直接返回，
     * 如果不存在就会重新创建一个。
     *
     * **/
    @Override
    void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        super.transform(transformInvocation)
        boolean isIncremental = transformInvocation.isIncremental();

        //消费型输入，可以从中获取jar包和class文件夹路径，需要输出下一个任务
        Collection<TransformInput> inputs = transformInvocation.getInputs();

        //引用型输入，无需输出
        Collection<TransformInput> referencedInputs = transformInvocation.getReferencedInputs();

        //OutputProvider管理输出路径，如果消费型输入为空，你会发现OutputProvider == null;
        TransformOutputProvider outputProvider = transformInvocation.getOutputProvider();

        for (TransformInput input: inputs) {
            for (JarInput jarInput: input.getJarInputs()) {
                println("jar :" + jarInput.getFile());
                File dest = outputProvider.getContentLocation(jarInput.getFile().getAbsolutePath(),
                        jarInput.getContentTypes(),
                        jarInput.getScopes(),
                        Format.JAR);
                //将修改过的字节码copy到dest， 就可以实现编译期干预字节码的目的
                transformJar(jarInput.getFile(), dest);
            }

            for (JarInput directoryInput: input.getDirectoryInputs()) {
                File dest = outputProvider.getContentLocation(directoryInput.getName(),
                        directoryInput.getContentTypes(),
                        directoryInput.getScopes(),
                        Format.DIRECTORY);
                //将修改过的字节码copy到dest， 就可以实现编译期干预字节码的目的
                transformDir(directoryInput.getFile(), dest);
            }
        }
    }

    private static void transformJar(File input, File dest) {
        FileUtils.copyFile(input, dest);
    }

    private static void transformDir(File input, File dest) {
        if (dest.exists()) {
            FileUtils.forceDelete(dest)
        }

        FileUtils.forceMkdir(dest);
        String srcDirPath = input.getAbsolutePath();
        String destDirPath= dest.getAbsolutePath();

        for (File file : input.listFiles()) {
            String destFilePath = file.absolutePath.replace(srcDirPath, destDirPath);
            File destFile = new File(destDirPath);
            if (file.isDirectory()) {
                transformDir(file, destFile);
            } else if (file.isFile()) {
                FileUtils.touch(destFile);
                transformSingleFile(file, destFile);
            }
        }
    }

    private static void transformSingleFile(File input, File dest) {
        weave(input.getAbsolutePath(), dest.getAbsolutePath());
    }

    private static void weave(String inputPath, String outputPath) {
        try {
            FileInputStream is = new FileInputStream(inputPath);
            ClassReader cr = new ClassReader(is);
            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
            TestMethodClassAdapter adapter = new TestMethodClassAdapter(cw);
            cr.accept(adapter, 0)
            FileOutputStream fos = new FilterOutputStream(outputPath);
            fos.write(cw.toByteArray())
            fos.close();
        } catch (IOException e) {
            e.printStackTrace()
        }
    }

//    private static void injectDir(String path, String packageName) {
//        ClassPool pool = ClassPool.getDefault();
//        File dir = new File(path)
//        pool.appendClassPath(path)
//        pool.insertClassPath("/Users/beiying/Develop/android-sdk-macosx/platforms/android-23/android.jar");
//        if (dir.isDirectory()) {
//            dir.eachFileRecurse { File file ->
//                String filePath = file.absolutePath
//                System.out.println("路径:" + filePath);
//                //D:\androidprogram\rkapp\trunk\app\build\intermediates\transforms\MyTrans\custom\release\jars\1\10\
//                //确保当前文件是class文件，并且不是系统自动生成的class文件
//                if (filePath.endsWith(".class")
//                        && !filePath.contains('R$')
//                        && !filePath.contains('R.class')
//                        && !filePath.contains("BuildConfig.class")) {
//                    // 判断当前目录是否是在我们的应用包里面
//                    int index = filePath.indexOf(packageName);
//                    boolean isMyPackage = index != -1;
//                    if (isMyPackage) {
//                        int end = filePath.length() - 6 // .class = 6
//                        String className = filePath.substring(index, end).replace('\\', '.').replace('/', '.')
//                        List<CellModel> cellFilterModelList = getCellModelListByClassName(className);
//                        if (cellFilterModelList == null || cellFilterModelList.size() == 0) {
//                            println className + "对应模型数:无";
//                            return;
//                        } else {
//                            println className + "对应模型数: " + cellFilterModelList.size();
//                        }
//                        //开始修改class文件
//                        CtClass c = pool.getCtClass(className)
//
//                        if (c.isFrozen()) {
//                            c.defrost()
//                        }
//                        pool.importPackage("com.xxxxx.xx.report.api.ReportPoint");
//                        pool.importPackage("com.xxxxx.xx.report.api.ReportClient");
//                        for (int i = 0; i < cellFilterModelList.size(); i++) {
//                            try {
//                                CellModel cellModel = cellFilterModelList.get(i);
//                                System.out.println("方法名:" + cellModel.getMethodName() + " 行为:" + cellModel.getAction());
//                                if (cellModel.getAction().equals("点击事件")) {
//                                    // 获取String类型参数集合
//                                    //   CtClass[] paramTypes = {pool.get(View.class.getName())};
//                                    String methodName = cellModel.getMethodName();
//                                    CtMethod method4 = c.getDeclaredMethod(cellModel.getMethodName());
//                                    if (method4 != null) {
//                                        println methodName + " method is not null"
//                                    } else {
//                                        println methodName + "method is  null"
//                                    }
//                                    method4.insertBefore(ExecuteCode.createReportOnClick(cellModel.getEventId()))
//                                } else {
//                                    CtMethod method4 = c.getDeclaredMethod(cellModel.getMethodName());
//                                    if (cellModel.getAction().equals("页面进入")) {
//                                        //待完善，用方法名不合适，可以构建类型来区别
//                                        println "页面进入 method start insert code"
//                                        String code = ExecuteCode.createReportOnResume(cellModel.getPageId(), cellModel.getBussinessType(), cellModel.getEventId());
//                                        println "页面进入 method start insert code is:${code}"
//                                        method4.insertBefore(code)
//                                    } else if (cellModel.getAction().equals("页面离开")) {
//                                        println "页面离开 start insert code"
//                                        String code = ExecuteCode.createReportOnPause(cellModel.getPageId(), cellModel.getBussinessType(), cellModel.getEventId());
//                                        println "页面离开 method start insert code is:${code}"
//                                        method4.insertBefore(code)
//                                    }
//                                }
//                            } catch (Throwable e) {
//                                System.out.println(e)
//                                System.out.println("改造异常" + e.getMessage());
//                            }
//                        }
//                        c.writeFile(path)
//                        c.detach()/**从类池中移除该对象，避免加载过多造成内存溢出问题*/
//                    }
//                }
//            }
//        }
//    }


    @Override
    String getName() {
        return AsmTransform.simpleName
    }

    /***
     * 包含有CLASSES和RESOURCES类型。
     * CLASSES类型表示的是在jar包或者文件夹中的.class文件。
     * RESOURCES类型表示的是标准的Java源文件。
     *
     * **/
    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }


    /**
     * PROJECT                 只处理当前的项目
     * SUB_PROJECTS            只处理子项目
     * EXTERNAL_LIBRARIES      只处理外部的依赖库
     * TESTED_CODE             只处理测试代码
     * PROVIDED_ONLY           只处理provided-only的依赖库
     * PROJECT_LOCAL_DEPS      只处理当前项目的本地依赖,例如jar, aar（过期，被EXTERNAL_LIBRARIES替代）
     * SUB_PROJECTS_LOCAL_DEPS 只处理子项目的本地依赖,例如jar, aar（过期，被EXTERNAL_LIBRARIES替代）
     *
     * */
    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }


    /***
     * 表示是否支持增量编译，返回true的话表示支持，这个时候可以根据
     * com.android.build.api.transform.TransformInput 来获得更改、移除或者添加的文件目录或者jar包
     * 如果不支持增量编译，就在处理.class之前把之前的输出目录中的文件删除。
     * */
    @Override
    boolean isIncremental() {
        return false
    }
}