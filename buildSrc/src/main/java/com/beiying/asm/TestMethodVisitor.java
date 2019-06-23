package com.beiying.asm;

import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.ASM7;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.POP;

/**
 * Created by beiying on 2019/5/4.
 */

public class TestMethodVisitor extends MethodVisitor{
    public TestMethodVisitor(MethodVisitor methodVisitor) {
        super(ASM7, methodVisitor);
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
        //方法执行前打印
        mv.visitLdcInsn("before method exec");
        mv.visitLdcInsn("[ASM 测试] method in" + owner + ";name=" + name);
        mv.visitMethodInsn(INVOKESTATIC, "android/util/Log", "i",
                "(Ljava/lang/String;Ljava/lang/String;)I", false);
        mv.visitInsn(POP);

        super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);

        //方法执行后打印
        mv.visitLdcInsn("after method exec");
        mv.visitLdcInsn("method in" + owner + ";name=" + name);
        mv.visitMethodInsn(INVOKESTATIC, "android/util/Log", "i",
                "(Ljava/lang/String;Ljava/lang/String;)I", false);
        mv.visitInsn(POP);


    }
}
