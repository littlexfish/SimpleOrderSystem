package edu.nptu.dllab.sos.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.Fragment
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationTokenSource
import edu.nptu.dllab.sos.MenuActivity
import edu.nptu.dllab.sos.data.ShopState
import edu.nptu.dllab.sos.data.pull.NearShop
import edu.nptu.dllab.sos.data.push.LinkEvent
import edu.nptu.dllab.sos.databinding.FragmentShopListBinding
import edu.nptu.dllab.sos.dialog.LoadingDialog
import edu.nptu.dllab.sos.io.Config
import edu.nptu.dllab.sos.io.Translator
import edu.nptu.dllab.sos.util.Position
import edu.nptu.dllab.sos.util.StaticData
import edu.nptu.dllab.sos.util.Util
import edu.nptu.dllab.sos.view.ShopItemView

/**
 * The fragment for show shop list
 *
 * @author Little Fish
 */
class ShopListFragment : Fragment() {
	
	private lateinit var binding: FragmentShopListBinding
	
	/**
	 * Request gain permission
	 */
	private val permissionRequest =
		registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
			checkPermission()
		}
	
	private lateinit var loadingDialog: LoadingDialog
	
	private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
	private var cancellationToken = CancellationTokenSource()
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		arguments?.let { }
	}
	
	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
	                          savedInstanceState: Bundle?): View? { // Inflate the layout for this fragment
		binding = FragmentShopListBinding.inflate(inflater)
		return binding.root
	}
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		loadingDialog = LoadingDialog(requireActivity())
		fusedLocationProviderClient =
			LocationServices.getFusedLocationProviderClient(requireActivity())
		
		binding.shopReload.text = Translator.getString("shop.reload")
		binding.shopReload.setOnClickListener {
			checkPermission()
		}
		
		if(Config.getBoolean(Config.Key.LINK_ON_START)) checkPermission()
		else {
			AlertDialog.Builder(requireContext())
				.setMessage(Translator.getString("main.connect"))
				.setPositiveButton(Translator.getString("main.true")) { _, _ ->
					checkPermission()
				}
				.setNegativeButton(Translator.getString("main.false")) { _, _ ->
					binding.shopReload.visibility = View.VISIBLE
				}
				.setOnCancelListener {
					binding.shopReload.visibility = View.VISIBLE
				}
				.create().show()
		}
	}
	
	/**
	 * Check permission and start on permission gain
	 */
	private fun checkPermission() {
		if(context == null) return
		if(checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
				|| checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			if(shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)
					|| shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION)) {
				AlertDialog.Builder(requireContext())
					.setMessage(Translator.getString("main.permission.location"))
					.setPositiveButton(Translator.getString("main.leave")) { _, _ ->
						activity?.finish()
					}
					.setNegativeButton(Translator.getString("main.settings.permission")) { _, _ ->
						startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
							data = Uri.fromParts("package", requireContext().packageName, null)
						})
					}
					.create().show()
			}
			else {
				permissionRequest.launch(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION))
			}
		}
		else start()
	}
	
	/**
	 * Start the shop list finder
	 */
	@SuppressLint("MissingPermission")
	private fun start() {
		if(loadingDialog.isShowing) return
		Log.i(TAG, "get shop data from server")
		binding.shopReload.visibility = View.GONE
		loadingDialog.show()
		cancellationToken = CancellationTokenSource()
		Log.i(TAG, "get location")
		loadingDialog.setMessage(Translator.getString("main.location.wait"))
		fusedLocationProviderClient.getCurrentLocation(CurrentLocationRequest.Builder().setDurationMillis(10 * 1000).build(), cancellationToken.token)
			.addOnSuccessListener {
				if(it == null) {
					Toast.makeText(requireContext(), Translator.getString("main.location.failed"),
					               Toast.LENGTH_SHORT).show()
					loadingDialog.dismissAtLeast()
				}
				else {
					Log.i(TAG, "find location: ${it.latitude}, ${it.longitude}")
					runLinkEvent(Position(it.latitude, it.longitude))
				}
			}
			.addOnFailureListener {
				loadingDialog.dismissAtLeast()
				Toast.makeText(requireContext(), Translator.getString("main.location.error"),
				               Toast.LENGTH_SHORT).show()
				Log.e(TAG, "error", it)
			}
	}
	
	/**
	 * Start when get position
	 */
	private fun runLinkEvent(pos: Position) {
		loadingDialog.setMessage(Translator.getString("main.shop.wait"))
		val evt = LinkEvent()
		evt.position = pos
		StaticData.ensureSocketHandler(requireContext(), {
			StaticData.socketHandler.pushEventRePush(evt)
			val get = StaticData.socketHandler.waitEvent()
			
			requireActivity().runOnUiThread {
				if(get is NearShop) {
					val shops = get.getShopCopy().values
					val sorted = shops.sortedBy { Util.getDistance(pos, it.position) }
					binding.mainList.removeAllViews()
					val showClose = Config.getBoolean(Config.Key.SHOW_CLOSED_SHOP)
					for(v in sorted) {
						if(v.state == ShopState.OPEN || (v.state == ShopState.CLOSE && showClose)) {
							Log.d(TAG, "shopId: ${v.shopId}, position: (${v.position.x}, ${v.position.y})")
							val dist = Util.getDistance(pos, v.position)
							Log.d(TAG, "distance: $dist")
							val view = ShopItemView(requireContext())
							view.name = v.name
							view.distance = dist
							if(v.state == ShopState.OPEN) {
								view.setOnClickListener {
									startActivity(Intent(requireContext(), MenuActivity::class.java).apply {
										putExtra(MenuActivity.EXTRA_SHOP_ID, v.shopId)
									})
								}
							}
							else {
								view.setOnClickListener {
									Toast.makeText(requireContext(), Translator.getString("menu.shop.close"), Toast.LENGTH_SHORT).show()
								}
							}
							binding.mainList.addView(view)
						}
					}
				}
				loadingDialog.dismiss()
			}
		}) {
			requireActivity().runOnUiThread {
				binding.shopReload.visibility = View.VISIBLE
				loadingDialog.dismiss()
			}
		}
	}
	
	override fun onDestroy() {
		super.onDestroy()
		cancellationToken.cancel()
	}
	
	companion object {
		private const val TAG = "shopList"
		fun newInstance() = ShopListFragment().apply {
			arguments = Bundle().apply { }
		}
	}
}