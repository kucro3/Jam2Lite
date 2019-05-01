package org.kucro3.jam2.invoke;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

class MethodInvokerReflectionImpl extends MethodInvoker {
    MethodInvokerReflectionImpl(Method method)
    {
        super(method.getDeclaringClass(),
                method.getModifiers(),
                method.getName(),
                method.getReturnType(),
                method.getParameterTypes());
        this.method = method;
    }

    @Override
    public Object invoke(Object obj, Object... args) throws InvocationTargetException
    {
        try {
            return method.invoke(obj, args);
        } catch (IllegalAccessException e) {
            throw new InvocationTargetException(e);
        }
    }

    private final Method method;
}
