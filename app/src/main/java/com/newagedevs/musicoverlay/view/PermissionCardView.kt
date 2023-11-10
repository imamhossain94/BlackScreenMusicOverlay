package com.newagedevs.musicoverlay.view

import android.content.Context
import android.content.res.TypedArray
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.newagedevs.musicoverlay.R

class PermissionCardView(private var mContext: Context, attrs: AttributeSet) : LinearLayout(
    mContext, attrs
) {
    private var mContainerView: ConstraintLayout? = null
    private var mIconImageView: ImageView? = null
    private var mTitleTextView: TextView? = null
    private var mSummaryTextView: TextView? = null
    private var mIconColor = 0
    private var mIconDrawable: Drawable? = null
    private var mTitleText = ""
    private var mSummaryText = ""

    init {
        setStyleable(attrs)
        init()
    }

    private fun setStyleable(attrs: AttributeSet) {
        val obtainStyledAttributes: TypedArray = mContext.obtainStyledAttributes(attrs, R.styleable.PermissionCardView)
        mIconDrawable = obtainStyledAttributes.getDrawable(R.styleable.PermissionCardView_IconDrawable)
        mTitleText = obtainStyledAttributes.getString(R.styleable.PermissionCardView_TitleText).toString()
        mSummaryText = obtainStyledAttributes.getString(R.styleable.PermissionCardView_SummaryText).toString()
        obtainStyledAttributes.recycle()
    }

    private fun init() {
        removeAllViews()
        View.inflate(mContext, R.layout.oui_view_permissioncardview_layout, this)
        mContainerView = findViewById(R.id.oui_permissioncardview_container)
        mIconImageView = findViewById(R.id.oui_permissioncardview_icon)
        mIconImageView?.setImageDrawable(mIconDrawable)
        mIconImageView?.drawable?.setTint(
            resources.getColor(
                R.color.oui_permissioncardview_item_icon_color,
                mContext.theme
            )
        )
        mTitleTextView = findViewById(R.id.oui_permissioncardview_title)
        mTitleTextView?.text = mTitleText
        mSummaryTextView = findViewById(R.id.oui_permissioncardview_summary)
        mSummaryTextView?.text = mSummaryText
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        isFocusable = enabled
        isClickable = enabled
        mContainerView?.isEnabled = enabled
        mIconImageView?.alpha = if (enabled) 1.0f else 0.4f
        mTitleTextView?.alpha = if (enabled) 1.0f else 0.4f
        mSummaryTextView?.alpha = if (enabled) 1.0f else 0.4f
    }

    var iconDrawable: Drawable?
        get() = mIconDrawable
        set(d) {
            mIconDrawable = d
            mIconImageView!!.setImageDrawable(mIconDrawable)
            init()
        }
    var iconColor: Int
        get() = mIconColor
        set(color) {
            mIconColor = color
            mIconImageView!!.drawable.setTint(mIconColor)
        }
    var titleText: String
        get() = mTitleText
        set(title) {
            mTitleText = title
            mTitleTextView?.text = mTitleText
        }
    var summaryText: String?
        get() = mSummaryText
        set(text) {
            mSummaryText = text ?: ""
            mSummaryTextView?.text = mSummaryText
            if (mSummaryText.isEmpty()) mSummaryTextView?.visibility = View.GONE else mSummaryTextView?.visibility =
                View.VISIBLE
        }
}