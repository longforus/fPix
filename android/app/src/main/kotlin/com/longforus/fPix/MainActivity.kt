package com.longforus.fPix

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.lang.ref.WeakReference

class MainActivity : AppCompatActivity(), View.OnClickListener {
    private var mOpenNative: TextView? = null
    private var mOpenFlutter: TextView? = null
    private var mOpenFlutterFragment: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sRef = WeakReference(this)
        setContentView(R.layout.native_page)
        mOpenNative = findViewById(R.id.open_native)
        mOpenFlutter = findViewById(R.id.open_flutter)
        mOpenFlutterFragment = findViewById(R.id.open_flutter_fragment)
        mOpenNative?.setOnClickListener(this)
        mOpenFlutter?.setOnClickListener(this)
        mOpenFlutterFragment?.setOnClickListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        sRef!!.clear()
        sRef = null
    }

    override fun onClick(v: View) {
        val params = mutableMapOf("pageTitle" to "fPix", "test2" to "v_test2")
        //Add some params if needed.
        when {
            v === mOpenNative -> {
                PageRouter.openPageByUrl(this, PageRouter.NATIVE_PAGE_URL, params)
            }
            v === mOpenFlutter -> {
                PageRouter.openPageByUrl(this, PageRouter.FLUTTER_PAGE_URL, params)
            }
            v === mOpenFlutterFragment -> {
                PageRouter.openPageByUrl(this, PageRouter.FLUTTER_FRAGMENT_PAGE_URL, params)
            }
        }
    }

    companion object {
        var sRef: WeakReference<MainActivity>? = null
    }
}
