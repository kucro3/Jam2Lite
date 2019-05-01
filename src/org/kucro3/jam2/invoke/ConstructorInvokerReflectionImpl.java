package org.kucro3.jam2.invoke;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

class ConstructorInvokerReflectionImpl extends ConstructorInvoker {
    protected ConstructorInvokerReflectionImpl(Constructor<?> constructor)
    {
        super(constructor.getDeclaringClass(),
                constructor.getModifiers(),
                constructor.getParameterTypes());
        this.constructor = constructor;
    }

    @Override
    public Object newInstance(Object... args) throws InvocationTargetException
    {
        try {
            return constructor.newInstance(args);
        } catch (IllegalAccessException | InstantiationException e) {
            throw new InvocationTargetException(e);
        }
    }

    private final Constructor<?> constructor;
}
