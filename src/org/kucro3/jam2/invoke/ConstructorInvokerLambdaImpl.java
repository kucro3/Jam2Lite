package org.kucro3.jam2.invoke;

import java.lang.reflect.InvocationTargetException;

class ConstructorInvokerLambdaImpl extends ConstructorInvoker {
	ConstructorInvokerLambdaImpl(Class<?> declaringClass, int modifier, Class<?>[] arguments,
                                 LambdaInvocation invocation)
	{
		super(declaringClass, modifier, arguments);
		this.invocation = invocation;
	}
	
	@Override
	public Object newInstance(Object... args) throws InvocationTargetException
	{
		try {
			return invocation.newInstance(args);
		} catch (Exception e) {
			throw new InvocationTargetException(e);
		}
	}
	
	final LambdaInvocation invocation;
	
	public static interface LambdaInvocation
	{
		public Object newInstance(Object... args);
	}
}
