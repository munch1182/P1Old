@file:Suppress("NOTHING_TO_INLINE")

package com.munch.lib.extend

import android.os.Process
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.viewbinding.ViewBinding
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withTimeoutOrNull
import java.lang.reflect.Method
import kotlin.reflect.KClass
import kotlin.system.exitProcess

/**
 * Create by munch1182 on 2021/8/19 15:05.
 */

inline fun <T> MutableLiveData<T>.toLive(): LiveData<T> = this

inline fun <T> MutableStateFlow<T>.toLive(): StateFlow<T> = this

fun destroy() {
    Process.killProcess(Process.myPid())
    exitProcess(1)
}

@Suppress("UNCHECKED_CAST")
fun <T : Any> KClass<T>.toClass(): Class<T>? = try {
    Class.forName(qualifiedName!!) as Class<T>
} catch (e: Exception) {
    e.printStackTrace()
    null
}

@Suppress("UNCHECKED_CAST")
fun <T : Any> KClass<T>.newInstance(): T? = try {
    Class.forName(qualifiedName!!).newInstance() as T
} catch (e: Exception) {
    e.printStackTrace()
    null
}

inline fun Method.inflate(
    inflater: LayoutInflater,
): ViewBinding? {
    return try {
        invoke(null, inflater) as? ViewBinding
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

inline fun Method.inflate(
    inflater: LayoutInflater,
    container: ViewGroup?,
    attach: Boolean
): ViewBinding? {
    return try {
        invoke(null, inflater, container, attach) as? ViewBinding
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

inline fun <reified VB : ViewBinding> KClass<VB>.inflate(): Method? =
    java.getDeclaredMethod("inflate", LayoutInflater::class.java)

inline fun <reified T : Any> invoke(methodName: String, vararg any: Any): T? {
    return invoke(methodName, *any.map { Pair(it::class.java, it) }.toTypedArray())
}

inline fun <reified T : Any> invoke(
    methodName: String,
    vararg pairs: Pair<Class<out Any>, Any>
): T? {
    return try {
        val method =
            T::class.java.getDeclaredMethod(methodName, *pairs.map { it.first }.toTypedArray())
        method.isAccessible = true
        method.invoke(null, *pairs.map { it.second }.toTypedArray()) as? T
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

suspend inline fun <T> suspendCancellableCoroutine(
    timeout: Long,
    crossinline block: (CancellableContinuation<T>) -> Unit
): T? = withTimeoutOrNull(timeout) { kotlinx.coroutines.suspendCancellableCoroutine(block) }


/**
 * 默认一个参数的单例
 * 使用：私有构造后使用 companion object : SingletonHolder<T, A>(::T)或者companion object : SingletonHolder<T, A>({creator}})
 * 无参数的可以直接使用lazy
 * @see kotlin.SynchronizedLazyImpl
 */
open class SingletonHolder<out T : Any, in A>(creator: (A) -> T) {

    private var creator: ((A) -> T)? = creator

    @Volatile
    private var instance: T? = null

    fun getInstance(arg: A): T {
        val i = instance
        if (i != null) {
            return i
        }
        return synchronized(this) {
            val i2 = instance
            if (i2 != null) {
                i2
            } else {
                val created = creator!!(arg)
                instance = created
                creator = null
                created
            }
        }
    }

}

open class SingletonHolder2<out T : Any, in A, in B>(creator: (A, B) -> T) {

    private var creator: ((A, B) -> T)? = creator

    @Volatile
    private var instance: T? = null

    fun getInstance(argA: A, argB: B): T {
        val i = instance
        if (i != null) {
            return i
        }
        return synchronized(this) {
            val i2 = instance
            if (i2 != null) {
                i2
            } else {
                val created = creator!!(argA, argB)
                instance = created
                creator = null
                created
            }
        }
    }

}
