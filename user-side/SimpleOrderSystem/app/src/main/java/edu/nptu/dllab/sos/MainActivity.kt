package edu.nptu.dllab.sos

import android.animation.Animator
import android.animation.Animator.AnimatorListener
import android.content.Intent
import android.graphics.drawable.AnimatedVectorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import edu.nptu.dllab.sos.databinding.ActivityMainBinding
import edu.nptu.dllab.sos.fragment.ShopListFragment
import edu.nptu.dllab.sos.io.Config
import edu.nptu.dllab.sos.io.Translator
import edu.nptu.dllab.sos.test.TestActivity

/**
 * The main activity of this app
 */
class MainActivity : AppCompatActivity() {
	
	private val tag = "Main"
	
	private lateinit var binding: ActivityMainBinding
	
	private var menuOpen = false
	
	private var lastBackTime = 0L
	
	private val loginRequest =
		registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
			closeMenuCutOut()
			when(it.resultCode) {
				LoginActivity.RESULT_USER -> {
					val t = supportFragmentManager.beginTransaction()
					t.replace(R.id.mainFragment, ShopListFragment.newInstance())
					t.commit()
				}
				LoginActivity.RESULT_SHOP -> {
				
				}
				LoginActivity.RESULT_NONE -> {
				
				}
				LoginActivity.RESULT_ERROR -> {
				
				}
			}
		}
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		
		binding = ActivityMainBinding.inflate(layoutInflater)
		setContentView(binding.root)
		
		Config.init(applicationContext)
		if(Translator.getLang() == null) Translator.initTranslator(applicationContext, Config.getString(Config.Key.LANG))
		
		if(intent.data != null) {
			if(parseNewData(intent.data!!)) {
				return
			}
		}
		
		binding.mainTitle.text = Translator.getString("main.list.title")
		
		binding.mainQrScanner.setOnClickListener {
			startActivity(Intent(applicationContext, QrCodeScanner::class.java))
		}
		
		binding.testBtn.setOnClickListener {
			startActivity(Intent(this, TestActivity::class.java))
		}
		
		binding.mainMenu.setOnClickListener { /*  do nothing  */ }
		binding.mainMenuButton.setOnClickListener {
			it as ImageView
			if(menuOpen) {
				val d = ResourcesCompat.getDrawable(resources, R.drawable.close_to_list, null) as AnimatedVectorDrawable
				it.setImageDrawable(d)
				d.start()
				closeMenuCutOut()
			}
			else {
				val d = ResourcesCompat.getDrawable(resources, R.drawable.list_to_close, null) as AnimatedVectorDrawable
				it.setImageDrawable(d)
				d.start()
				openMenuCutIn()
			}
			menuOpen = !menuOpen
		}
		binding.mainMenuBg.setOnClickListener {
			if(menuOpen) {
				binding.mainMenuButton.callOnClick()
			}
		}
		binding.mainMenuAccount.text = Translator.getString("side.account")
		binding.mainMenuOrderCheck.text = Translator.getString("side.orderCheck")
		binding.mainMenuSettings.text = Translator.getString("side.settings")
		binding.mainMenuSettings.setOnClickListener {
			startActivity(Intent(this, SettingsActivity::class.java))
		}
		binding.mainMenuAccount.setOnClickListener {
			loginRequest.launch(Intent(this, LoginActivity::class.java))
		}
		binding.mainMenuOrderCheck.setOnClickListener {
			startActivity(Intent(this, OrderCheckActivity::class.java))
		}
		
		onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
			override fun handleOnBackPressed() {
				/*
				 * FISH NOTE:
				 *   Close side menu when it is open.
				 *   Click 2 times to finish app if side menu is close.
				 */
				if(menuOpen) {
					binding.mainMenuButton.callOnClick()
				}
				else {
					val t = System.currentTimeMillis()
					if(t - lastBackTime < 1500) {
						lastBackTime = t
						Toast.makeText(applicationContext, Translator.getString("main.back"), Toast.LENGTH_SHORT).show()
					}
					else {
						finish()
					}
				}
			}
		})
		
		// TODO: auto get token and login, or wait for login
		loginRequest.launch(Intent(this, LoginActivity::class.java))
		
	}
	
	private fun openMenuCutIn() {
		val menuRootLeft = resources.getDimensionPixelSize(R.dimen.main_menu_root).toFloat()
		val menuLeft = resources.getDimensionPixelSize(R.dimen.main_menu).toFloat()
		val menuAni = binding.mainMenu.animate()
		menuAni.x(menuLeft - menuRootLeft)
		val bgAni = binding.mainMenuBg.animate()
		bgAni.alpha(1f)
		bgAni.setListener(object : AnimatorListener {
			override fun onAnimationStart(animation: Animator) {
				binding.mainMenuBg.visibility = View.VISIBLE
			}
			override fun onAnimationEnd(animation: Animator) {}
			override fun onAnimationCancel(animation: Animator) {}
			override fun onAnimationRepeat(animation: Animator) {}
		})
		menuAni.start()
		bgAni.start()
	}
	
	private fun closeMenuCutOut() {
		val menuRootLeft = resources.getDimensionPixelSize(R.dimen.main_menu_root).toFloat()
		val menuAni = binding.mainMenu.animate()
		menuAni.x(-menuRootLeft)
		val bgAni = binding.mainMenuBg.animate()
		bgAni.alpha(0f)
		bgAni.setListener(object : AnimatorListener {
			override fun onAnimationStart(animation: Animator) {}
			override fun onAnimationEnd(animation: Animator) {
				binding.mainMenuBg.visibility = View.GONE
			}
			override fun onAnimationCancel(animation: Animator) {}
			override fun onAnimationRepeat(animation: Animator) {}
		})
		menuAni.start()
		bgAni.start()
	}
	
	override fun onNewIntent(intent: Intent?) {
		super.onNewIntent(intent)
		
		if(intent != null && intent.data != null) {
			parseNewData(intent.data!!)
		}
		
	}
	
	/**
	 * Process the new data from intent
	 */
	private fun parseNewData(uri: Uri): Boolean {
		/*
		 * FISH NOTE:
		 *   Parse the uri that open from any app.
		 *   We process the uri path to open specific activity,
		 *    that the app want to open
		 */
		when(uri.host) {
			HOST_TEST -> {
				startActivity(Intent(applicationContext, TestActivity::class.java).setData(intent.data))
			}
			HOST_MENU -> {
				val shopIdStr = uri.getQueryParameter("shopId") ?: return false
				val num = if(shopIdStr == "test") -1 else shopIdStr.toIntOrNull()
				if(num != null) {
					val menuActI = Intent(applicationContext, MenuActivity::class.java)
					menuActI.putExtra(MenuActivity.EXTRA_SHOP_ID, num)
					startActivity(menuActI)
				}
			}
			HOST_CHECK -> {
				startActivity(Intent(applicationContext, OrderCheckActivity::class.java).apply {
					val orderId = uri.getQueryParameter("orderId")
					if(orderId != null) putExtra(OrderCheckActivity.EXTRA_ORDER_ID, orderId)
				})
			}
			HOST_LOGIN -> {
				startActivity(Intent(applicationContext, LoginActivity::class.java).apply {
					val acc = uri.getQueryParameter("account")
					val pass = uri.getQueryParameter("password")
					if(acc != null) putExtra(LoginActivity.EXTRA_ACCOUNT, acc)
					if(pass != null) putExtra(LoginActivity.EXTRA_PASSWORD, pass)
				})
			}
			else -> return false
		}
		return true
	}
	
	companion object {
		const val HOST_TEST = "dllab.test"
		const val HOST_MENU = "dllab.menu"
		const val HOST_CHECK = "dllab.check"
		const val HOST_LOGIN = "dllab.login"
		const val HOST_NONE = "dllab.none"
	}
	
}