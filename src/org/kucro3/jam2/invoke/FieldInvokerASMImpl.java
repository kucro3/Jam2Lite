package org.kucro3.jam2.invoke;

class FieldInvokerASMImpl extends FieldInvoker {
	FieldInvokerASMImpl(Class<?> declaringClass, int modifier, String name, Class<?> type,
                        ASMGet getter, ASMSet setter)
	{
		super(declaringClass, modifier, name, type);
		this.getter = getter;
		this.setter = setter;
	}
	
	@Override
	public Object get(Object obj)
	{
		return getter.get(obj);
	}
	
	@Override
	public void set(Object obj, Object args)
	{
		setter.set(obj, args);
	}
	
	final ASMGet getter;
	
	final ASMSet setter;
	
	public static interface ASMGet
	{
		public Object get(Object obj);
	}
	
	public static interface ASMSet
	{
		public void set(Object obj, Object args);
	}
}
