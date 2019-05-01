package org.kucro3.jam2.invoke;

import org.kucro3.jam2.invoke.ConstructorInvokerLambdaImpl.LambdaInvocation;
import org.kucro3.jam2.util.Jam2Util;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

import static org.kucro3.jam2.util.Jam2Util.toConstructorDescriptor;

public abstract class ConstructorInvoker implements Opcodes {
	protected ConstructorInvoker(Class<?> declaringClass, int modifier, Class<?>[] arguments)
	{
		this.declaringClass = declaringClass;
		this.modifier = modifier;
		this.arguments = arguments;
		this.descriptor = toConstructorDescriptor(arguments);
	}

	public static ConstructorInvoker newInvokeByReflection(Constructor<?> constructor)
	{
		visibilityCheck(constructor);

		return new ConstructorInvokerReflectionImpl(constructor);
	}
	
	public static ConstructorInvoker newInvokerByLambda(Constructor<?> constructor)
	{
		visibilityCheck(constructor);
		
		String name = "org/kucro3/jam2/invoke/ConstructorInvoker$" + Jam2Util.generateUUIDForClassName();
		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
		cw.visit(V1_8, ACC_PUBLIC, 
				name,
				null, 
				"java/lang/Object", 
				new String[] {"org/kucro3/jam2/invoke/ConstructorInvokerLambdaImpl$LambdaInvocation"});
		
		Jam2Util.pushEmptyConstructor(cw, ACC_PUBLIC, Object.class);
		Jam2Util.pushNewInstance(cw, ACC_PUBLIC | ACC_VARARGS, "newInstance", constructor, true, true);
		
		LambdaInvocation invocation;
		try {
			invocation = (LambdaInvocation) Jam2Util.newClass(name.replace('/', '.'), cw).newInstance();
		} catch (Exception e) {
			// unused
			throw new IllegalStateException(e);
		}
		
		return new ConstructorInvokerLambdaImpl(constructor.getDeclaringClass(), constructor.getModifiers(),
				constructor.getParameterTypes(), invocation);
	}

	private static void visibilityCheck(Constructor<?> constructor)
	{
		if(!Modifier.isPublic(constructor.getModifiers()) ||
				Modifier.isAbstract(constructor.getModifiers()))
			throw new IllegalArgumentException("constructor unaccessable or not constructable");
	}
	
	public String getDescriptor()
	{
		return descriptor;
	}
	
	public int getModifier()
	{
		return modifier;
	}
	
	public Class<?>[] getArguments()
	{
		return arguments;
	}
	
	public abstract Object newInstance(Object... args) throws InvocationTargetException;
	
	final String descriptor;
	
	final Class<?> declaringClass;
	
	final int modifier;
	
	final Class<?>[] arguments;
}
