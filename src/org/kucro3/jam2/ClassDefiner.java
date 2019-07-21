package org.kucro3.jam2;

import java.security.ProtectionDomain;

public interface ClassDefiner {
    public Class<?> defineClass(String name, byte[] byts, int off, int len, ProtectionDomain domain);
}
