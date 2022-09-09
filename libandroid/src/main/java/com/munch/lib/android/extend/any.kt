@file:Suppress("NOTHING_TO_INLINE")

package com.munch.lib.android.extend

import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * 寻找[Type]上继承自[target]泛型的类
 *
 * 如果找不到, 则返回null
 */
fun <T> Type.findParameterized(target: Class<in T>): Class<T>? {
    when (this) {
        is ParameterizedType -> {
            val c = actualTypeArguments.find { it is Class<*> && target.isAssignableFrom(it) }
            if (c != null) return c.to()
            val type = this.rawType
            if (type == Any::class.java) return null
            return type.findParameterized(target)
        }
        is Class<*> -> {
            return if (this == Any::class.java) null
            else this.genericSuperclass?.findParameterized(target)
        }
        else -> return null
    }
}

/**
 * 使用非同步的lazy
 */
inline fun <T> lazy(noinline initializer: () -> T): Lazy<T> =
    lazy(LazyThreadSafetyMode.NONE, initializer)

/**
 * 将错误转为null
 */
inline fun <T> catch(block: () -> T): T? {
    return try {
        block.invoke()
    } catch (e: Exception) {
        null
    }
}

//<editor-fold desc="to">
/**
 * 将调用对象转为[T], 避免写as+[]的写法
 */
@Suppress("UNCHECKED_CAST")
inline fun <T> Any.to(): T = this as T

/**
 * 将调用对象转为[T], 避免写as+[]的写法
 */
@Suppress("UNCHECKED_CAST")
inline fun <T> Any.toOrNull(): T? = this as? T
//</editor-fold>