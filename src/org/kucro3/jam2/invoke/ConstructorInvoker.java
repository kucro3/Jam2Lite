package org.kucro3.jam2.invoke;

import org.kucro3.jam2.invoke.ConstructorInvokerASMImpl.ASMInvocation;
import org.kucro3.jam2.util.Jam2Util;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

public abstract class ConstructorInvoker extends MethodInvoker implements Opcodes {
	protected ConstructorInvoker(Class<?> declaringClass, int modifier, Class<?>[] arguments)
	{
		super(declaringClass, modifier, "<init>", declaringClass, arguments);
	}

	public static ConstructorInvoker newInvokerByReflection(Constructor<?> constructor)
	{
		visibilityCheck(constructor);

		return new ConstructorInvokerReflectionImpl(constructor);
	}
	
	public static ConstructorInvoker newInvokerByASM(Constructor<?> constructor)
	{
		visibilityCheck(constructor);
		
		String name = "org/kucro3/jam2/invoke/ConstructorInvoker$" + Jam2Util.generateUUIDForClassName();
		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
		cw.visit(V1_8, ACC_PUBLIC, 
				name,
				null, 
				"java/lang/Object", 
				new String[] {"org/kucro3/jam2/invoke/ConstructorInvokerASMImpl$ASMInvocation"});
		
		Jam2Util.pushEmptyConstructor(cw, ACC_PUBLIC, Object.class);
		Jam2Util.pushNewInstance(cw, ACC_PUBLIC | ACC_VARARGS, "newInstance", constructor, true, true);
		
		ASMInvocation invocation;
		try {
			invocation = (ASMInvocation) Jam2Util.newClass(name.replace('/', '.'), cw).newInstance();
		} catch (Exception e) {
			// unused
			throw new IllegalStateException(e);
		}
		
		return new ConstructorInvokerASMImpl(constructor.getDeclaringClass(), constructor.getModifiers(),
				constructor.getParameterTypes(), invocation);
	}

	private static void visibilityCheck(Constructor<?> constructor)
	{
		if(!Modifier.isPublic(constructor.getModifiers()) ||
				Modifier.isAbstract(constructor.getModifiers()))
			throw new IllegalArgumentException("constructor inaccessable or not constructable");
	}

	public Object newInstance(Object... args) throws InvocationTargetException
	{
		return invoke(null, args);
	}
}
