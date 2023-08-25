package com.vnpay.extension.extensions

import android.os.Build
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat

object VNPTextExt {
    fun TextView.remove() {
        visibility = View.GONE
    }

    fun TextView.show() {
        visibility = View.VISIBLE
    }

    fun TextView.inVisible() {
        visibility = View.INVISIBLE
    }

    fun TextView.clear() {
        text = ""
    }

    class ClickSpan(
        val color: Int,
        val isBold: Boolean = false,
        val isUnderline: Boolean = true,
        val listener: View.OnClickListener?,
    ) : ClickableSpan() {
        override fun onClick(widget: View) {
            listener?.onClick(widget)
        }

        override fun updateDrawState(ds: TextPaint) {
            ds.isUnderlineText = isUnderline
            ds.color = color
            ds.isFakeBoldText = isBold
        }
    }

    fun TextView.setTextHtml(textHtml: String?) {
        textHtml?.let {
            text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Html.fromHtml(textHtml, Html.FROM_HTML_MODE_COMPACT)
            } else {
                Html.fromHtml(textHtml)
            }
        }
    }

    fun TextView.clickify(
        clickableText: String,
        @ColorRes color: Int,
        isBold: Boolean = false,
        isUnderline: Boolean = true,
        listener: View.OnClickListener? = null
    ) {
        val text = text
        val string = text.toString()
        val span = ClickSpan(color, isBold, isUnderline, listener)

        val start = string.indexOf(clickableText)
        val end = start + clickableText.length
        if (start == -1) return

        if (text is Spannable) {
            text.setSpan(span, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            text.setSpan(
                ForegroundColorSpan(ContextCompat.getColor(context, color)),
                start,
                end,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        } else {
            val s = SpannableString.valueOf(text)
            s.setSpan(span, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            s.setSpan(
                ForegroundColorSpan(ContextCompat.getColor(context, color)),
                start,
                end,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            setText(s)
        }

        val m = movementMethod
        if (m == null || m !is LinkMovementMethod) {
            movementMethod = LinkMovementMethod.getInstance()
        }
    }
}