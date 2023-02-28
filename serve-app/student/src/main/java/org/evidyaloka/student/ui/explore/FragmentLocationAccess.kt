package org.evidyaloka.student.ui.explore

import android.Manifest
import android.annotation.SuppressLint
import android.content.*
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.viewModels
import com.google.android.gms.location.*
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import org.evidyaloka.common.helper.PermissionRequester
import org.evidyaloka.common.view.RecursiveRadioGroup
import org.evidyaloka.common.helper.hideKeyboard
import org.evidyaloka.common.helper.isValidPincode
import org.evidyaloka.common.helper.placeCursorToEnd
import org.evidyaloka.core.Constants.StudentConst
import org.evidyaloka.core.helper.Resource
import org.evidyaloka.core.student.model.ExploreData
import org.evidyaloka.core.student.model.Pincode
import org.evidyaloka.student.R
import org.evidyaloka.student.databinding.FragmentLocationAccessBinding
import org.evidyaloka.student.ui.BaseFragment
import org.evidyaloka.student.ui.ParentExploreActivity
import java.util.*

@AndroidEntryPoint
class FragmentLocationAccess : BaseFragment() {
    private val TAG = "FragmentOnboarding"
    private lateinit var binding: FragmentLocationAccessBinding
    private val viewModel: OnboardingViewModel by viewModels()
    private var exploreData: ExploreData? = null
    private var mLat: String = ""
    private var mLng: String = ""
    private var mPincode: String = ""
    private lateinit var pincodeAdapter: ArrayAdapter<String>
    private var pincodeList = mutableListOf<Pincode>()
    private var pincodeListString = mutableListOf<String>()
    private var mFusedLocationClient: FusedLocationProviderClient? = null
    private var PERMISSION_ID = 123
    private var enableToolBar: Boolean = true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            var args = FragmentLocationAccessArgs.fromBundle(it)
            enableToolBar = args.showToolBar
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLocationAccessBinding.inflate(inflater, container, false)
        exploreData = ExploreData()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pincodeAdapter = ArrayAdapter(requireContext(), R.layout.item_spinner, pincodeListString)
        binding.etvLocation?.apply {
            setAdapter(pincodeAdapter)
            setOnItemClickListener { adapterView, mView, position, l ->
                activity?.let { it.hideKeyboard() }
                val selectedPincode = pincodeList[position]
                mPincode = selectedPincode.pincode
                binding.etvLocation.setText(getPincode(selectedPincode))
                binding.etvLocation.placeCursorToEnd()
            }
        }
        binding.tilLocation.setEndIconOnClickListener {
            activity?.let {
                it.hideKeyboard()
            }
            callLocationUpdate()
        }

        binding.rgPersona.setOnCheckedChangeListener(object :
            RecursiveRadioGroup.OnCheckedChangeListener {
            override fun onCheckedChanged(group: RecursiveRadioGroup?, checkedId: Int) {
                when (checkedId) {
                    R.id.rb_parent -> {
                        exploreData?.relationShip = StudentConst.PARENT
                    }
                    R.id.rb_guardian -> {
                        exploreData?.relationShip = StudentConst.GUARDIAN
                    }
                }
            }

        })

        binding.etvLocation.addTextChangedListener(textListener)

        binding.btNext.setOnClickListener {
            if (binding.etvLocation.text.toString()
                    ?.isNullOrEmpty() || binding.etvLocation.text.toString()?.length <= 5
            ) {
                showPopup(
                    getString(R.string.error),
                    getString(R.string.label_enter_pincode)
                )
            } else {
                try {
                    mPincode.takeIf { it.isNullOrEmpty() == false }?.let { pincode ->
                        exploreData?.pincode = pincode.toInt()
                        var map = HashMap<String, Any>().apply {
                            put("pincode", mPincode)
                            put("lat", mLat)
                            put("lng", mLng)
                        }
                        viewModel.updateLocationDetails(map)
                            .observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                                navController?.navigate(
                                    FragmentLocationAccessDirections
                                        .actionFragmentLocationAccessToFragmentSchoolBoard(
                                            exploreData
                                        )
                                )
                            })

                    }
                } catch (e: Exception) {
                    FirebaseCrashlytics.getInstance().recordException(e)
                }
            }
        }
    }

    private fun callLocationUpdate() {
        locationPermission.runWithPermission {
            fetchCurrentLocation()
        }
    }

    private val locationPermission = PermissionRequester(
        this,
        Manifest.permission.ACCESS_FINE_LOCATION,
        R.string.rationale_location
    )

    private fun fetchCurrentLocation() {
        try {
            activity?.let {
                if (mFusedLocationClient == null) {
                    mFusedLocationClient = LocationServices.getFusedLocationProviderClient(it);
                }
                getLastLocation();
            }
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    /**
     * Function to show progress bar
     */
    fun showProgressCircularBar() {
        binding.progressCircular?.visibility = View.VISIBLE
    }

    /**
     * Function to hide progress bar
     */
    fun hideProgressCircularBar() {
        binding.progressCircular?.visibility = View.GONE
    }

    open fun getAddressFromLocation(geocoder: Geocoder, latitude: Double, longitude: Double) {
        showProgressCircularBar()
        try {
            viewModel.getLocations(geocoder, latitude, longitude)
                .observe(viewLifecycleOwner, androidx.lifecycle.Observer { response ->
                    when (response) {
                        is Resource.Success -> {
                            response.data?.let {
                                it as List<Address>
                                it.forEach {
                                    if (it.postalCode != null && it.postalCode.isValidPincode()) {
                                        mLat = it.latitude.toString()
                                        mLng = it.longitude.toString()
                                        mPincode = it.postalCode
                                        var pincodeStr = it.postalCode
                                        if (it.locality != null) {
                                            pincodeStr = it.postalCode.plus(" - ").plus(it.locality)
                                        } else {
                                            it.subLocality?.let { sub ->
                                                pincodeStr = it.postalCode.plus(" - ").plus(sub)
                                            }
                                        }
                                        binding.etvLocation.setText(pincodeStr)
                                        binding.etvLocation.placeCursorToEnd()
                                        return@let
                                    }
                                }
                            }
                            hideProgressCircularBar()
                        }

                        is Resource.GenericError -> {
                            hideProgressCircularBar()
                            when (response.code) {
                                1 -> {
                                    showPopup(
                                        getString(R.string.error),
                                        getString(R.string.geocoder_error)
                                    )
                                }
                            }
                            //Todo show errro , enter pincode manually.
                        }
                    }
                })
        } catch (e: Exception) {
            hideProgressCircularBar()
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    override fun onResume() {
        super.onResume()
        if (!enableToolBar) activity?.let { (it as ParentExploreActivity).hideToolbar() }
        callLocationUpdate()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (!enableToolBar) activity?.let { (it as ParentExploreActivity).showToolbar() }
    }

    private fun getPincodeFromGeocoder(latitude: Double, longitude: Double) {
        activity?.let {
            exploreData?.latitude = latitude
            exploreData?.longitude = longitude

            var geocoder = Geocoder(it, Locale.ENGLISH)
            getAddressFromLocation(geocoder, latitude, longitude)
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        if (isLocationEnabled()) {
            mFusedLocationClient?.lastLocation?.addOnCompleteListener { task ->
                try {
                    task.getResult()?.let { location ->
                        getPincodeFromGeocoder(location.latitude, location.longitude)
                    } ?: requestNewLocationData()

                } catch (e: Exception) {
                }
            }
        } else {
            activity?.let {
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {

        val mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 5
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1

        activity?.let {
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(it)
            mFusedLocationClient?.requestLocationUpdates(
                mLocationRequest,
                mLocationCallback,
                Looper.myLooper()
            )
        }
    }

    private val mLocationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val mLastLocation = locationResult.lastLocation
            getPincodeFromGeocoder(mLastLocation.latitude, mLastLocation.longitude)
            Log.e(TAG, "Latitude: " + mLastLocation.latitude)
            Log.e(TAG, "longi: " + mLastLocation.longitude)
        }
    }

    // method to check
    // if location is enabled
    private fun isLocationEnabled(): Boolean {
        activity?.let {
            val locationManager = it.getSystemService(Context.LOCATION_SERVICE) as LocationManager?
            return locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
            )
        }
        return false
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_ID) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchCurrentLocation()
            }
        }
    }

    private val textListener = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            if (!binding.etvLocation?.text.isBlank()) {
                //TODO call API
                if (binding.etvLocation?.text.toString().length >= 5 && binding.etvLocation?.text.toString().length <= 6) {
                    viewModel.getPincodes(binding.etvLocation?.text.toString())
                        .observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                            when (it) {
                                is Resource.Success -> {
                                    it.data?.let {
                                        pincodeList = it as MutableList<Pincode>
                                        updateLocationSpinner(pincodeList)
                                    }
                                }
                            }
                        })
                }

                binding.btNext.isEnabled = (binding.etvLocation?.text.toString().length >= 6)
            }
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            // do nothing
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

        }
    }

    private fun initPincodeSpinner(list: List<Pincode>) {
        try {
            var l = list.map { getPincode(it) }
            binding.etvLocation?.apply {
                context?.let {
                    var adapter = ArrayAdapter(it, R.layout.item_spinner, l)
                    setAdapter(adapter)
                    adapter?.notifyDataSetChanged()
                    /*if (list.isNotEmpty()) {
                        binding.etvLocation.setSelection(0)
                        binding.etvLocation.setText(
                            getPincode(list[0])
                        )
                        binding.etvLocation.placeCursorToEnd()
                    }*/
                    updateLocationSpinner(list)
                }
            }
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            e.printStackTrace()
        }
    }

    private fun updateLocationSpinner(list: List<Pincode>) {
        try {
            var l = list.map { getPincode(it) }
            if (!pincodeAdapter.isEmpty) {
                pincodeAdapter.clear()
                pincodeAdapter.notifyDataSetChanged()
            }
            pincodeAdapter?.addAll(l)
            pincodeAdapter.notifyDataSetChanged()
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            e.printStackTrace()
        }
    }

    fun getPincode(pincode: Pincode): String {
        var pincodeStr = ""

        if (!pincode.pincode.isNullOrEmpty()) {
            pincodeStr = pincodeStr.plus(pincode.pincode)
        }

        if (!pincode.taluk.isNullOrEmpty()) {
            pincodeStr = pincodeStr.plus(" - ").plus(pincode.taluk)
        } else {
            if (!pincode.district.isNullOrEmpty()) {
                pincodeStr = pincodeStr.plus(" - ").plus(pincode.district)
            }
        }
        return pincodeStr
    }
}