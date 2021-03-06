package org.kucro3.jam2.asm;

import org.kucro3.jam2.util.Jam2Util;
import org.kucro3.jam2.util.Version;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class ASMImplementationBuilder<T> extends ASMCodeBuilderRoot<ASMImplementationBuilder<T>> {
	public static <T> ASMImplementationBuilder<T> newBuilder(Class<T> clz)
	{
		return newBuilder(clz, false);
	}
	
	public static <T> ASMImplementationBuilder<T> newBuilder(Class<T> implClz, Method mthd)
	{
		return newBuilder(implClz, mthd, false);
	}
	
	public static <T> ASMImplementationBuilder<T> newBuilder(Class<T> clz, boolean ensured)
	{
		return newBuilder(clz, filterMethod(clz, ensured), ensured);
	}
	
	public static <T> ASMImplementationBuilder<T> newBuilder(Class<T> implClz, Method mthd, boolean ensured)
	{
		if(!ensured)
			checkConstructor(implClz);
		String name = "org/kucro3/jam2/asm/ASMImplementation_" + Jam2Util.generateUUIDForClassName();
		String superClass;
		ClassWriter ctx = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
		if(implClz.isInterface())
			ctx.visit(Version.getClassVersion(), ACC_PUBLIC, name, null, superClass = "java/lang/Object", new String[] {Type.getInternalName(implClz)});
		else
			ctx.visit(Version.getClassVersion(), ACC_PUBLIC, name, null, superClass = Type.getInternalName(implClz), null);
		MethodVisitor mctx = ctx.visitMethod(
				ACC_PUBLIC,
				mthd.getName(),
				Type.getMethodDescriptor(mthd),
				null,
				Jam2Util.toInternalNames(mthd.getExceptionTypes()));
		Jam2Util.pushEmptyConstructor(ctx, ACC_PUBLIC, superClass);
		return new ASMImplementationBuilder<T>(ctx, mctx, name, implClz);
	}
	
	static Method filterMethod(Class<?> clz, boolean ensured)
	{
		if(clz.isArray())
			throw new IllegalArgumentException("array unsupported");
		
		Method result = null;
		Method temp;
		int modifier;
		
		Method[] methods = clz.getDeclaredMethods();
		if(clz.isInterface())
		{
			BLOCK: 
				if(ensured)
					break BLOCK;
				else if(methods.length == 1)
					break BLOCK;
				else
					throw new IllegalArgumentException("not a functional interface");
			return methods[0];
		}
		else if(!Modifier.isAbstract(clz.getModifiers()))
			throw new IllegalArgumentException("not an abstract class");
		else
			for(int i = 0; i < methods.length; i++)
				if(Modifier.isStatic(modifier = (temp = methods[i]).getModifiers()))
					continue;
				else if(Modifier.isAbstract(modifier))
					if(ensured)
						return temp;
					else if(result != null)
						throw new IllegalArgumentException("not a functional abstract class");
					else
						result = temp;
				else;
		if(result != null)
			return result;
		throw new IllegalArgumentException("no function available");
	}
	
	static void checkConstructor(Class<?> clz)
	{
		if(clz.isInterface())
			return;
		Constructor<?>[] cstrs = clz.getDeclaredConstructors();
		for(Constructor<?> cstr : cstrs)
			if(Modifier.isPublic(cstr.getModifiers()) || Modifier.isProtected(cstr.getModifiers()))
				if(cstr.getParameterCount() == 0)
					return;
		throw new IllegalArgumentException("constructor unavailable");
	}
	
	static boolean available(int modifier)
	{
		return Modifier.isPublic(modifier) || Modifier.isProtected(modifier);
	}
	
	static boolean overridable(int modifier)
	{
		return !Modifier.isFinal(modifier);
	}
	
	ASMImplementationBuilder(ClassWriter ctx, MethodVisitor mv, String name, Class<T> implClz)
	{
		super(mv);
		this.implClz = implClz;
		this.ctx = ctx;
		this.name = name;
		this.isPureFunction = implClz.isInterface();
	}
	
	@SuppressWarnings("unchecked")
	public T build()
	{
		try {
			Class<?> clz = cachedClass;
			if(clz == null)
			{
				super.end(true);
				clz = Jam2Util.newClass(Jam2Util.fromInternalNameToCanonical(name), ctx);
			}
			if(isPureFunction)
				if(cached != null)
					return cached;
				else
					return cached = (T) clz.newInstance();
			return (T) clz.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			// unused
			throw new IllegalStateException(e);
		}
	}
	
	public Class<?> getImplementingClass()
	{
		return implClz;
	}
	
	private final boolean isPureFunction; // isInterface
	
	private T cached;
	
	private Class<?> cachedClass;
	
	private final String name;
	
	private final ClassWriter ctx;
	
	private final Class<?> implClz;
}
