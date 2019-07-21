package org.kucro3.jam2.invoke;

import org.kucro3.jam2.ClassDefiner;
import org.kucro3.jam2.invoke.FieldInvokerASMImpl.ASMGet;
import org.kucro3.jam2.invoke.FieldInvokerASMImpl.ASMSet;
import org.kucro3.jam2.util.Jam2Util;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public abstract class FieldInvoker implements Opcodes {
	protected FieldInvoker(Class<?> declaringClass, int modifier, String name, Class<?> type)
	{
		this.declaringClass = declaringClass;
		this.modifier = modifier;
		this.name = name;
		this.type = type;
	}

	public static FieldInvoker newInvokeByReflection(Field field)
	{
		visibilityCheck(field);

		return new FieldInvokerReflectionImpl(field);
	}

	public static FieldInvoker newInvokerByASM(Field field)
	{
		return newInvokerByASM(field, Jam2Util::newClass);
	}

	public static FieldInvoker newInvokerByASM(Field field, ClassDefiner classDefiner)
	{
		visibilityCheck(field);

		ASMGet get;
		ASMSet set;
		
		String getterName;
		String setterName;
		
		ClassWriter getter = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
		getter.visit(V1_8,
				ACC_PUBLIC,
				getterName = "org/kucro3/jam2/invoke/FieldGetter$" + Jam2Util.generateUUIDForClassName(),
				null,
				"java/lang/Object",
				new String[] {"org/kucro3/jam2/invoke/FieldInvokerASMImpl$ASMGet"});
		Jam2Util.pushFieldGetter(getter, ACC_PUBLIC, "get", field, true, true);
		Jam2Util.pushEmptyConstructor(getter, ACC_PUBLIC, Object.class);
		getter.visitEnd();
		
		ClassWriter setter = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
		setter.visit(V1_8, 
				ACC_PUBLIC, 
				setterName = "org/kucro3/jam2/invoke/FieldSetter$" + Jam2Util.generateUUIDForClassName(),
				null, 
				"java/lang/Object",
				new String[] {"org/kucro3/jam2/invoke/FieldInvokerASMImpl$ASMSet"});
		Jam2Util.pushFieldSetter(setter, ACC_PUBLIC, "set", field, true);
		Jam2Util.pushEmptyConstructor(setter, ACC_PUBLIC, Object.class);
		setter.visitEnd();
		
		try {
			byte[] getByts = getter.toByteArray();
			get = (ASMGet) classDefiner.defineClass(
					Jam2Util.fromInternalNameToCanonical(getterName),
					getByts,
					0,
					getByts.length,
					null).newInstance();

			byte[] setByts = setter.toByteArray();
			set = (ASMSet) classDefiner.defineClass(
					Jam2Util.fromInternalNameToCanonical(setterName),
					setByts,
					0,
					setByts.length,
					null).newInstance();
		} catch (Exception e) {
			// unused
			throw new IllegalStateException(e);
		}
		
		return new FieldInvokerASMImpl(field.getDeclaringClass(), field.getModifiers(),
				field.getName(), field.getType(), get, set);
	}

	static void visibilityCheck(Field field)
	{
		if(!Modifier.isPublic(field.getModifiers()))
			throw new IllegalArgumentException("field inaccessable");
	}
	
	public Class<?> getDeclaringClass()
	{
		return declaringClass;
	}
	
	public int getModifier()
	{
		return modifier;
	}
	
	public String getName()
	{
		return name;
	}
	
	public Class<?> getType()
	{
		return type;
	}
	
	public abstract void set(Object obj, Object ref);
	
	public abstract Object get(Object obj);
	
	final Class<?> declaringClass;
	
	final int modifier;
	
	final String name;
	
	final Class<?> type;
}
