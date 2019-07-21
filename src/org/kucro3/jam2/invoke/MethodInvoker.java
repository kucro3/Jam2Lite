package org.kucro3.jam2.invoke;

import org.kucro3.jam2.ClassDefiner;
import org.kucro3.jam2.invoke.MethodInvokerASMImpl.ASMInvocation;
import org.kucro3.jam2.util.Jam2Util;
import org.kucro3.jam2.util.Jam2Util.CallingType;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public abstract class MethodInvoker implements Opcodes {
	protected MethodInvoker(Class<?> declaringClass, int modifier, String name, Class<?> returnType, Class<?>[] arguments)
	{
		this.declaringClass = declaringClass;
		this.name = name;
		this.returnType = returnType;
		this.arguments = arguments;
		this.modifier = modifier;
		this.descriptor = Jam2Util.toDescriptor(name, returnType, arguments);
	}

	public static MethodInvoker newInvokerByReflection(Method method)
	{
		visibilityCheck(method);

		return new MethodInvokerReflectionImpl(method);
	}

	public static MethodInvoker newInvokerByASM(Method method)
	{
		return newInvokerByASM(method, Jam2Util::newClass);
	}

	public static MethodInvoker newInvokerByASM(Method method, ClassDefiner classDefiner)
	{
		visibilityCheck(method);

		ASMInvocation invocation;

		String name;
		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
		cw.visit(
				V1_8,
				ACC_PUBLIC,
				name = "org/kucro3/jam2/invoke/MethodInvoker$" + Jam2Util.generateUUIDForClassName(),
				null,
				"java/lang/Object",
				new String[]{"org/kucro3/jam2/invoke/MethodInvokerASMImpl$ASMInvocation"});
		Jam2Util.pushCaller(cw, ACC_PUBLIC, "invoke", method, CallingType.fromMethod(method), true, true);
		Jam2Util.pushEmptyConstructor(cw, ACC_PUBLIC, Object.class);
		cw.visitEnd();

		try {
			byte[] invocationByts = cw.toByteArray();
			invocation = (ASMInvocation) classDefiner.defineClass(
					Jam2Util.fromInternalNameToCanonical(name),
					invocationByts,
					0,
					invocationByts.length,
					null).newInstance();
		} catch (Exception e) {
			// unused
			throw new IllegalStateException(e);
		}

		return new MethodInvokerASMImpl(method.getDeclaringClass(), method.getModifiers(),
				method.getName(), method.getReturnType(), method.getParameterTypes(), invocation);
	}

	private static void visibilityCheck(Method method)
	{
		if (!Modifier.isPublic(method.getModifiers()))
			throw new IllegalArgumentException("method inaccessable");
	}
	
	public final Class<?> getDeclaringClass()
	{
		return declaringClass;
	}
	
	public final String getName()
	{
		return name;
	}
	
	public Class<?> getReturnType()
	{
		return returnType;
	}
	
	public Class<?>[] getArguments()
	{
		return arguments;
	}

	public int getArgumentCount()
	{
		return getArguments().length;
	}
	
	public int getModifier()
	{
		return modifier;
	}
	
	public abstract Object invoke(Object obj, Object... args) throws InvocationTargetException;
	
	public String getDescriptor()
	{
		return descriptor;
	}
	
	final String descriptor;
	
	final int modifier;
	
	final Class<?> declaringClass;
	
	final String name;
	
	final Class<?> returnType;
	
	final Class<?>[] arguments;
}
