package com.longforus.cpix.util

import android.app.Activity
import android.content.res.Resources
import android.graphics.Color
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

object StatusBarUtil {
    fun transparentStatusBar(activity: Activity) {
        with(activity) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            val option = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            val vis = window.decorView.systemUiVisibility
            window.decorView.systemUiVisibility = option or vis
            window.statusBarColor = Color.parseColor("#22000000")
        }
    }
}

object DensityUtil {
    fun dxToDp(resources: Resources, px: Int): Int =
        (px / resources.displayMetrics.density + 0.5f).toInt()
}

object ValueUtil {
    fun getRandomDp(fromDp: Dp, toDp: Dp): Dp  = (fromDp.value.toInt()..toDp.value.toInt()).random().dp
}

object LogUtil {
    fun printLog(tag : String = "FlappyBird", message: String) {
        Log.d(tag, message)
    }
}

/**
 * Returns a string containing the first [n] characters from this string, or the entire string if this string is shorter.
 * 如果length超过n返回实际长度,否则截取前n个char
 *
 * @throws IllegalArgumentException if [n] is negative.
 *
 * @sample samples.text.Strings.take
 */
fun String.ifTakeAppendLength(n: Int): String {
    require(n >= 0) { "Requested character count $n is less than zero." }
    val endIndex = n.coerceAtMost(length)
    return substring(0, endIndex) + if (endIndex == n) "......(length = $length)" else ""
}