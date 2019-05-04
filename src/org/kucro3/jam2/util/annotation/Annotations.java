package org.kucro3.jam2.util.annotation;

import com.theredpixelteam.redtea.util.Optional;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.*;

@SuppressWarnings("all")
public class Annotations {
    private Annotations()
    {
    }

    public static Map<String, Object> values(AnnotationNode node)
    {
        Map<String, Object> map = new HashMap<>();
        if(node != null && node.values != null)
        {
            Iterator<Object> iter = node.values.iterator();
            while(iter.hasNext())
                map.put((String) iter.next(), iter.next());
        }
        return map;
    }

    public static void setValues(AnnotationNode node, Map<String, Object> map)
    {
        if(node.values == null)
            node.values = new ArrayList<>();
        else
            node.values.clear();

        for(Map.Entry<String, Object> entry : map.entrySet())
        {
            node.values.add(entry.getKey());
            node.values.add(entry.getValue());
        }
    }

    public static boolean isAnnotated(ClassNode cn, Class<? extends java.lang.annotation.Annotation> type)
    {
        return getAnnotationNode(cn, type).isPresent();
    }

    public static boolean isAnnotated(MethodNode mn, Class<? extends java.lang.annotation.Annotation> type)
    {
        return getAnnotationNode(mn, type).isPresent();
    }

    public static boolean isAnnotated(FieldNode fn, Class<? extends java.lang.annotation.Annotation> type)
    {
        return getAnnotationNode(fn, type).isPresent();
    }

    static Optional<AnnotationNode> getAnnotationNode(List<AnnotationNode> list, Class<? extends java.lang.annotation.Annotation> type)
    {
        if(list == null)
            return Optional.empty();

        String descriptor = Type.getDescriptor(type);
        for(AnnotationNode an : list)
            if(an.desc.equals(descriptor))
                return Optional.of(an);

        return Optional.empty();
    }

    public static Optional<AnnotationNode> getAnnotationNode(ClassNode cn, Class<? extends java.lang.annotation.Annotation> type)
    {
        return getAnnotationNode(cn.visibleAnnotations, type);
    }

    public static Optional<AnnotationNode> getAnnotationNode(MethodNode mn, Class<? extends java.lang.annotation.Annotation> type)
    {
        return getAnnotationNode(mn.visibleAnnotations, type);
    }

    public static Optional<AnnotationNode> getAnnotationNode(FieldNode fn, Class<? extends java.lang.annotation.Annotation> type)
    {
        return getAnnotationNode(fn.visibleAnnotations, type);
    }
}
