package org.kucro3.jam2.invoke;

import java.lang.reflect.InvocationTargetException;

class MethodInvokerLambdaImpl extends MethodInvoker {
	MethodInvokerLambdaImpl(Class<?> declaringClass, int modifier, String name, Class<?> returnType,
                            Class<?>[] arguments, LambdaInvocation invocation)
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
	
	final LambdaInvocation invocation;
	
	public static interface LambdaInvocation
	{
		public Object invoke(Object obj, Object... arguments);
	}
}
