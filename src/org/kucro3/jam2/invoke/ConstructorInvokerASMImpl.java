package org.kucro3.jam2.invoke;

import java.lang.reflect.InvocationTargetException;

class ConstructorInvokerASMImpl extends ConstructorInvoker {
	ConstructorInvokerASMImpl(Class<?> declaringClass, int modifier, Class<?>[] arguments,
							  ASMInvocation invocation)
	{
		super(declaringClass, modifier, arguments);
		this.invocation = invocation;
	}
	
	@Override
	public Object invoke(Object obj, Object... args) throws InvocationTargetException
	{
		try {
			return invocation.newInstance(args);
		} catch (Exception e) {
			throw new InvocationTargetException(e);
		}
	}
	
	final ASMInvocation invocation;
	
	public static interface ASMInvocation
	{
		public Object newInstance(Object... args);
	}
}
