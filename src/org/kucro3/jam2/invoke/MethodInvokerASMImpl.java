package org.kucro3.jam2.invoke;

import java.lang.reflect.InvocationTargetException;

class MethodInvokerASMImpl extends MethodInvoker {
	MethodInvokerASMImpl(Class<?> declaringClass, int modifier, String name, Class<?> returnType,
						 Class<?>[] arguments, ASMInvocation invocation)
	{
		super(declaringClass, modifier, name, returnType, arguments);
		this.invocation = invocation;
	}
	
	@Override
	public Object invoke(Object obj, Object... args) throws InvocationTargetException
	{
		try {
			return invocation.invoke(obj, args);
		} catch (Exception e) {
			throw new InvocationTargetException(e);
		}
	}
	
	final ASMInvocation invocation;
	
	public static interface ASMInvocation
	{
		public Object invoke(Object obj, Object... arguments);
	}
}
