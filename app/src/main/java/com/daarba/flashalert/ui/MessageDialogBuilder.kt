package com.daarba.flashalert.ui

import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.Window
import androidx.appcompat.app.AlertDialog
import com.daarba.flashalert.databinding.MessageDialogBinding

class MessageDialogBuilder(val activity: Activity) {
    var message:String? = null
    var title:String? = null
    var buttonOkText:String = "Ok"
    var buttonYesText:String = "Yes"
    var okButtonClickListener: OnOkClick? = null
    var yesClickListener: OnOkClick? = null
    var onCancelClickListener: OnCancel? = null
    var icon:Int? = null

    data class Builder(var activity: Activity) {
        private val obj = MessageDialogBuilder(activity)
        fun withMessage(message: String) = apply { obj.message = message }
        fun withTitle(title: String) = apply { obj.title = title }
        fun withIcon(icon: Int) = apply { obj.icon = icon }
        fun withOkButtonListener(text:String, listener: OnOkClick) = apply {
            obj.buttonOkText = text
            obj.okButtonClickListener = listener
        }
        fun withYesNoListener(text:String, listener: OnOkClick) = apply {
            obj.buttonYesText = text
            obj.yesClickListener = listener
        }
        fun withCancelListener(text:String, listener: OnCancel) = apply {
            obj.onCancelClickListener = listener
        }
        fun build() = obj.buildDialog()
    }

    fun buildDialog(){
        val builder = AlertDialog.Builder(activity)
        val dialogs = builder.create()
        dialogs.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialogs.window!!.setDimAmount(0.6f)
        dialogs.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val dialogBinding: MessageDialogBinding = MessageDialogBinding.inflate(activity.layoutInflater)
        dialogs.setView(dialogBinding.root)
        dialogBinding.tvTitle.text = title
        dialogBinding.tvMessage.text = message
        dialogBinding.btnYes.text = buttonYesText

        if (yesClickListener != null){
            dialogBinding.btnOk.visibility = View.GONE
            dialogBinding.llYesNo.visibility = View.VISIBLE
        }else{
            dialogBinding.btnOk.visibility = View.VISIBLE
            dialogBinding.llYesNo.visibility = View.GONE
        }

        dialogBinding.btnOk.setOnClickListener {
            if (okButtonClickListener != null){
                okButtonClickListener!!.onClick(dialogs)
            }else{
                dialogs.dismiss()
            }
        }
        dialogBinding.btnYes.setOnClickListener {
            if (yesClickListener != null){
                yesClickListener!!.onClick(dialogs)
            }else{
                dialogs.dismiss()
            }
        }
        dialogBinding.btnCancel.setOnClickListener {
            if (onCancelClickListener != null){
                onCancelClickListener!!.onClick(dialogs)
            }else {
                dialogs.dismiss()
            }
        }



        dialogs.setCancelable(false)
        dialogs.show()
    }

    interface OnOkClick{
        fun onClick(dialogs: AlertDialog)
    }

    interface OnCancel{
        fun onClick(dialogs: AlertDialog)
    }
}