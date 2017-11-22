package de.welt.widgetadapter

import android.annotation.SuppressLint
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.os.Build
import kotlin.reflect.KClass

@Suppress("UNCHECKED_CAST", "DEPRECATION")
fun <T : Any> getResource(resources: Resources, resourceId: Int, type: KClass<T>) = when (type) {
    String::class              -> resources.getString(resourceId) as T
    Array<String>::class       -> resources.getStringArray(resourceId) as T
    CharSequence::class        -> resources.getText(resourceId) as T
    Array<CharSequence>::class -> resources.getTextArray(resourceId) as T
    Int::class                 -> resources.getInteger(resourceId) as T
    Array<Int>::class          -> resources.getIntArray(resourceId).toTypedArray() as T
    Boolean::class             -> resources.getBoolean(resourceId) as T
    Drawable::class            -> resources.getDrawable(resourceId) as T
    else                       -> throw IllegalArgumentException("cannot get resource of unknown type ${type.java.name}")
}

fun dimensionInPixels(resources: Resources, resourcesId: Int)
        = resources.getDimensionPixelSize(resourcesId)

@SuppressLint("NewApi")
@Suppress("DEPRECATION")
fun Resources.getColorInt(resourcesId: Int, theme: Resources.Theme? = null) =
        if (Build.VERSION.SDK_INT >= 23)
            getColor(resourcesId, theme)
        else
            getColor(resourcesId)