package com.vnpay.extension

import android.R
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.Rect
import android.text.TextUtils
import android.util.TypedValue
import android.view.*
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.View.OnTouchListener
import androidx.fragment.app.FragmentActivity
import java.math.BigDecimal
import java.math.MathContext
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.ParseException
import java.util.*
import kotlin.math.roundToInt


object VnpayManager {
    const val CURRENCY_VND = "VND"
    const val KEYBOARD_VISIBLE_THRESHOLD_DP = 100
   const val SWIPE_THRESHOLD = 100
   const val SWIPE_VELOCITY_THRESHOLD = 100

    fun FragmentActivity.setEventListener(listener: KeyboardVisibilityEventListener?) {
        if (listener == null) {
            return
        }
        val activityRoot: View = getActivityRoot(this) ?: return
        val listenerGlobal = object :
            ViewTreeObserver.OnGlobalLayoutListener {
            private val r = Rect()
            private val visibleThreshold = getDPtoPX(
                this@setEventListener,
                KEYBOARD_VISIBLE_THRESHOLD_DP.toFloat()
            ).toFloat().roundToInt()
            private var wasOpened = false
            override fun onGlobalLayout() {
                activityRoot.getWindowVisibleDisplayFrame(r)
                val heightDiff = activityRoot.rootView.height - r.height()
                val isOpen = heightDiff > visibleThreshold
                if (isOpen == wasOpened) {
                    return
                }
                wasOpened = isOpen
                listener.onVisibilityChanged(isOpen)
            }
        }
        activityRoot.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(v: View) {
                activityRoot.viewTreeObserver.addOnGlobalLayoutListener(listenerGlobal)
            }

            override fun onViewDetachedFromWindow(v: View) {
                activityRoot.viewTreeObserver.removeOnGlobalLayoutListener(listenerGlobal)
            }

        })
    }
    fun getDPtoPX(context: Context, dp: Float): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, dp, context.resources.displayMetrics
        ).toInt()
    }
    fun Int.toPx() = (this * Resources.getSystem().displayMetrics.density).toInt()

    fun Int.toDp() = (this / Resources.getSystem().displayMetrics.density).toInt()

    fun String?.getMoneyClearText(): String {
        try {
            if (!TextUtils.isEmpty(this)) {
                val b = BigDecimal(
                    this?.replace(",", "")
                        ?.replace("[a-zA-Z]".toRegex(), "")
                        ?.replace("\\s".toRegex(), ""), MathContext.DECIMAL64
                )
                return b.toString()
            }
        } catch (e: java.lang.Exception) {
        }
        return ""
    }

    fun String.getFormatMoneybyCcy(
        ccy: String? = CURRENCY_VND,
        showCcy: Boolean = true
    ): String {
        try {
            val tmpCcy = ccy ?: CURRENCY_VND
            return formatDoubleAmount(this, 2) + if (showCcy) " $tmpCcy" else ""
        } catch (e: java.lang.Exception) {

        }
        return ""
    }

    private fun getActivityRoot(context: FragmentActivity): View? {
        return (context.window.decorView.rootView
            .findViewById(R.id.content) as ViewGroup).getChildAt(0)
    }

    private fun formatDoubleAmount(value: String, num: Int): String? {
        var value = value
        if (value.isNullOrEmpty()) return ""
        val soAm = value.startsWith("-")
        value = value.replace('+', ' ').trim { it <= ' ' }
        value = value.replace('-', ' ').trim { it <= ' ' }
        value = value.replace(",", "").trim { it <= ' ' }
        value = value.replace(".", "").trim { it <= ' ' }
        var s = ""
        val a: String = BigDecimal(convertDouble(value)).toString()
        s = roundDouble(a, num) ?: ""
        val indexCham = s.indexOf(".")
        var dauphay = ""
        var dauCham = ""
        if (indexCham > 0) {
            val ls = s.split("\\.".toRegex()).toTypedArray()
            dauphay = ls[0]
            dauCham = ls[1]
        } else {
            dauphay = s
            dauCham = ""
        }
        val phay: String = formatDauPhay1(dauphay) ?: ""
        var cham = ""
        cham = formatDauCham1(dauCham, num) ?: ""
        var resulft = ""
        resulft = if (cham == ".00") {
            phay
        } else {
            phay + cham
        }
        if (soAm) resulft = "-$resulft"
        return resulft
    }

    private fun convertDouble(value: String?): Double {
        return try {
            val f = DecimalFormat("###,##0.###")
            val format = NumberFormat.getInstance(Locale.CHINESE)
            format.parse(value).toDouble()
        } catch (e: ParseException) {
            0.0
        } catch (e: NullPointerException) {
            0.0
        }
    }

    private fun roundDouble(s: String, num: Int): String? {
        val indexCham = s.indexOf(".")
        var dauphay = ""
        var dauCham = ""
        return if (indexCham > 0) {
            if (num == 0) return s.substring(0, indexCham)
            val ls: Array<String?> = stringSplit(s, ".")
            dauphay = ls[0].toString()
            dauCham = ls[1].toString()
            if (dauCham.length > num) {
                val begin = dauCham.substring(0, num)
                val after = dauCham.substring(num, num + 1)
                if (after.toInt() >= 5) {
                    val v: Int = begin.toInt() + 1
                    "$dauphay.$v"
                } else {
                    "$dauphay.$begin"
                }
            } else {
                s
            }
        } else {
            s
        }
    }

    fun stringSplit(splitStr: String, delimiter: String): Array<String?> {
        if (splitStr.isEmpty()) {
            return arrayOfNulls(0)
        }
        val token = StringBuffer()
        val tokens: Vector<*> = Vector<Any?>()
        // split
        val chars = splitStr.toCharArray()
        for (i in chars.indices) {
            if (delimiter.indexOf(chars[i]) != -1) {
                tokens.addElement(token as Nothing?)
                token.setLength(0)
            } else {
                token.append(chars[i])
            }
        }
        tokens.addElement(token as Nothing?)
        // convert the vector into an array
        val splitArray = arrayOfNulls<String>(tokens.size)
        for (i in splitArray.indices) {
            splitArray[i] = tokens.elementAt(i) as String
        }
        return splitArray
    }

    private fun formatDauCham1(data: String, num: Int): String? {
        if (num == 0) return ""
        val buffer = StringBuffer(".$data")
        while (buffer.length <= num) {
            buffer.append("0")
        }
        return buffer.toString().substring(0, num + 1)
    }

    private fun formatDauPhay1(s1: String): String? {
        val buffer = StringBuffer()
        var leng = s1.length
        while (true) {
            val sp = leng / 3
            if (sp > 0) {
                if (sp == 1 && leng % 3 == 0) {
                    buffer.insert(0, s1.subSequence(leng - 3, leng))
                    leng -= 3
                    break
                } else {
                    buffer.insert(0, s1.subSequence(leng - 3, leng))
                    buffer.insert(0, ",")
                    leng -= 3
                }
            } else {
                if (leng % 3 == 0) {
                    if (leng > 2) {
                        buffer.insert(0, s1.subSequence(leng - 3, leng))
                    }
                } else {
                    buffer.insert(0, s1.subSequence(leng - leng % 3, leng))
                }
                break
            }
        }
        return buffer.toString()
    }
}
interface KeyboardVisibilityEventListener {
    fun onVisibilityChanged(isOpen: Boolean)
}

@SuppressLint("ClickableViewAccessibility")
abstract class VNPOnSwipeTouchListener(ctx: Context?) :
    OnTouchListener {
    private val gestureDetector: GestureDetector

    init {
        gestureDetector = GestureDetector(ctx, GestureListener())
    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        v.performClick()
        return gestureDetector.onTouchEvent(event)
    }

    private inner class GestureListener : SimpleOnGestureListener() {
        override fun onDown(e: MotionEvent): Boolean {
            return true
        }

        override fun onFling(
            e1: MotionEvent,
            e2: MotionEvent,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            var result = false
            try {
                val diffY = e2.y - e1.y
                val diffX = e2.x - e1.x
                if (Math.abs(diffX) > Math.abs(diffY)) {
                    if (Math.abs(diffX) > VnpayManager.SWIPE_THRESHOLD && Math.abs(velocityX) > VnpayManager.SWIPE_VELOCITY_THRESHOLD) {
                        if (diffX > 0) {
                            onSwipeRight()
                        } else {
                            onSwipeLeft()
                        }
                        result = true
                    }
                } else if (Math.abs(diffY) > VnpayManager.SWIPE_THRESHOLD && Math.abs(velocityY) > VnpayManager.SWIPE_VELOCITY_THRESHOLD) {
                    if (diffY > 0) {
                        onSwipeBottom()
                    } else {
                        onSwipeTop()
                    }
                    result = true
                }
            } catch (exception: Exception) {
                exception.printStackTrace()
            }
            return result
        }
    }

    abstract fun onSwipeRight()
    abstract fun onSwipeLeft()
    abstract fun onSwipeTop()
    abstract fun onSwipeBottom()
}