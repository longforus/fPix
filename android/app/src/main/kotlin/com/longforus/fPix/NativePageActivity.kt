package com.longforus.fPix

import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.longforus.fPix.PageRouter.openPageByUrl

class NativePageActivity : AppCompatActivity(), OnClickListener {
    private var mOpenNative: TextView? = null
    private var mOpenFlutter: TextView? = null
    private var mOpenFlutterFragment: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.native_page)
        mOpenNative = findViewById(R.id.open_native)
        mOpenFlutter = findViewById(R.id.open_flutter)
        mOpenFlutterFragment = findViewById(R.id.open_flutter_fragment)
        mOpenNative?.setOnClickListener(this)
        mOpenFlutter?.setOnClickListener(this)
        mOpenFlutterFragment?.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        val params: MutableMap<String, String> = HashMap<String, String>()
        params["test1"] = "v_test1"
        params["test2"] = "v_test2"
        when {
            v === mOpenNative -> {
                openPageByUrl(this, PageRouter.NATIVE_PAGE_URL, params)
            }
            v === mOpenFlutter -> {
                openPageByUrl(this, PageRouter.FLUTTER_PAGE_URL, params)
            }
            v === mOpenFlutterFragment -> {
                openPageByUrl(this, PageRouter.FLUTTER_FRAGMENT_PAGE_URL, params)
            }
        }
    }
}