package edu.nptu.dllab.sos.dialog

import android.app.Activity
import android.app.ProgressDialog
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import edu.nptu.dllab.sos.io.Translator

class LoadingDialog(private val act: Activity, private var atLeastTime: Long = 2000,
                    private var finishSecondClick: Boolean = true) : ProgressDialog(act) {
	
	/**
	 * Two back button press interval
	 */
	private val waitTime = 1500
	
	/**
	 * Last back button press time
	 */
	private var lastBackTime = 0L
	
	private var lastShowTime = 0L
	
	init {
		setCanceledOnTouchOutside(false)
		setMessage("Loading...")
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
			onBackInvokedDispatcher.registerOnBackInvokedCallback(1) {
				pressBack()
			}
		}
	}
	
	override fun onBackPressed() {
		if(Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
			pressBack()
		}
	}
	
	override fun show() {
		if(!isShowing) {
			super.show()
			lastShowTime = System.currentTimeMillis()
		}
	}
	
	fun dismissAtLeast() {
		val interval = System.currentTimeMillis() - lastShowTime
		if(interval >= atLeastTime) {
			super.dismiss()
		}
		else {
			Handler(Looper.getMainLooper()).postDelayed({
                act.runOnUiThread {
                    super.dismiss()
                }
            }, interval)
		}
	}
	
	private fun pressBack() {
		if(finishSecondClick) {
			val cur = System.currentTimeMillis()
			if(cur - lastBackTime > waitTime) {
				lastBackTime = cur
				Toast.makeText(context, Translator.getString("menu.loading.back"), Toast.LENGTH_SHORT).show()
			}
			else {
				act.finish()
			}
		}
		else {
			act.finish()
		}
	}
	
}