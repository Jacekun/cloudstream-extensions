package com.lagradost

import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.lagradost.cloudstream3.AcraApplication.Companion.openBrowser
import com.lagradost.cloudstream3.plugins.Plugin
import com.lagradost.cloudstream3.ui.settings.SettingsAccount.Companion.showLoginInfo
import com.lagradost.cloudstream3.ui.settings.SettingsAccount.Companion.addAccount
import com.lagradost.cloudstream3.utils.UIHelper.colorFromAttribute

const val packageName = "com.lagradost"

class NginxSettingsFragment(private val plugin: Plugin, val nginxApi: NginxApi) :
    BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val id = plugin.resources!!.getIdentifier("nginx_settings", "layout", packageName)
        val layout = plugin.resources!!.getLayout(id)
        return inflater.inflate(layout, container, false)
    }

    private fun <T : View> View.findView(name: String): T {
        val id = plugin.resources!!.getIdentifier(name, "id", packageName)
        return this.findViewById(id)
    }

    private fun getDrawable(name: String): Drawable? {
        val id = plugin.resources!!.getIdentifier(name, "drawable", packageName)
        return ResourcesCompat.getDrawable(plugin.resources!!, id, null)
    }

    private fun getString(name: String): String {
        val id = maxOf(
            // Fuck it lets see if any works
            plugin.resources!!.getIdentifier(name, "string", packageName),
            plugin.resources!!.getIdentifier(name, "string", "$packageName.cloudstream3"),
            this.context?.resources?.getIdentifier(name, "string", "$packageName.cloudstream3")
                ?: 0,
            this.context?.resources?.getIdentifier(name, "string", packageName) ?: 0,
            this.activity?.resources?.getIdentifier(name, "string", "$packageName.cloudstream3") ?: 0,
            this.activity?.resources?.getIdentifier(name, "string", packageName) ?: 0,
        )
        return if (id <= 0)
            ""
        else
            this.getString(id)
    }

    private fun getAttr(name: String): Int {
        return plugin.resources!!.getIdentifier(name, "attr", packageName)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val infoView = view.findView<LinearLayout>("nginx_info")
        val infoTextView = view.findView<TextView>("info_main_text")
        val infoSubTextView = view.findView<TextView>("info_sub_text")
        val infoImageView = view.findView<ImageView>("nginx_info_imageview")

        infoTextView.text = getString("nginx_info_title")
        infoSubTextView.text = getString("nginx_info_summary")
        infoImageView.setImageDrawable(
            getDrawable("nginx_question")
        )
        infoImageView.imageTintList =
            ColorStateList.valueOf(view.context.colorFromAttribute(getAttr("white")))

        val loginView = view.findView<LinearLayout>("nginx_login")
        val loginTextView = view.findView<TextView>("main_text")
        val loginImageView = view.findView<ImageView>("nginx_login_imageview")
        loginImageView.setImageDrawable(getDrawable("nginx"))
        loginImageView.imageTintList =
            ColorStateList.valueOf(view.context.colorFromAttribute(getAttr("white")))

        // object : View.OnClickListener is required to make it compile because otherwise it used invoke-customs
        infoView.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                openBrowser(nginxApi.createAccountUrl)
            }
        })


        loginTextView.text = getString("login_format").format(nginxApi.name, getString("account"))
        loginView.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                val info = nginxApi.loginInfo()
                if (info != null) {
                    showLoginInfo(activity, nginxApi, info)
                } else {
                    addAccount(activity, nginxApi)
                }
            }
        })
    }
}