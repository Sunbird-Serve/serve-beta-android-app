package org.evidyaloka.partner.ui

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.google.firebase.crashlytics.FirebaseCrashlytics

/**
 * @author Madhankumar
 * created on 07-01-2021
 *
 */
open class BaseFragment: Fragment() {
    private val TAG = "BaseFragment"

    //Image request code
    val PICK_PROFILE_IMAGE_REQUEST = 1
    val PICK_LOGO_IMAGE_REQUEST = 1
    val PICK_BANNER_IMAGE_REQUEST = 2

    //storage permission code
    val RC_READ_STORAGE_PERM = 120

    lateinit var navController:NavController

    private var baseActivity: HomeActivity? = null

    fun setNetworkErrorObserver(viewModel: BaseViewModel){
        viewModel.getNetworkErrorObserable().observe(viewLifecycleOwner, Observer {
            it?.let {
                handleNetworkDialog(viewModel::retry )
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            baseActivity = context as HomeActivity
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            e.printStackTrace()
        }
    }

    override fun onDetach() {
        super.onDetach()
        baseActivity = null
    }

    fun updateToolbarTitle(title: String) = baseActivity?.updateToolbarTitle(title)

    /**
     * This function will be responsible to create snackbar and display to userEntity
     * @param message String text to show in snackbar
     * @param action String Action button text
     * @param listener action button click listener, if null default action button listener will be created
     */
    fun showSnackBar(message: String, action:String?=null, listener: View.OnClickListener?=null){
        baseActivity?.showSnackBar(message, action, listener)
    }

    fun showSuccessDialog(title:String, message: String) = baseActivity?.showSuccessDialog(title, message)
    fun showNavHeaderRoleToggleButton() = baseActivity?.showNavHeaderRoleToggleButton()
    fun hideNavHeaderRoleToggleButton() = baseActivity?.hideNavHeaderRoleToggleButton()
    /**
     * Create generic network error snackbar
     */
    fun handleNetworkError(){
        baseActivity?.handleNetworkError()
    }

    fun handleNetworkDialog(onClick: () -> Unit) = baseActivity?.showNetworkErrorDialog(onClick)
    /**
     * function shows progress bar
     */
    fun showProgressCircularBar() = baseActivity?.showProgressCircularBar()

    /**
     * function hides progress bar
     */
    fun hideProgressCircularBar() = baseActivity?.hideProgressCircularBar()

    /**
     * function shows horizontal bar
     */
    fun showHorizontalDeterminateBar() = baseActivity?.showHorizontalDeterminateBar()

    /**
     * function updates file/image upload progress
     */
    fun UpdateHorizontalDeterminateBar(progress: Int) = baseActivity?.updateHorizontalDeterminateBar(progress)

    /**
     * function hides file/image upload progress
     */
    fun hideHorizontalDeterminateBar() = baseActivity?.hideHorizontalDeterminateBar()

    /**
     * shows toolbar
     */
    fun showToolbar() = baseActivity?.showToolbar()

    /**
     * hides toolbar
     */
    fun hideToolbar() = baseActivity?.hideToolbar()

    /**
     * This function hides keyboard
     */
    fun hideKeyboard() = baseActivity?.hideKeyboard()

    fun lockDrawer() = baseActivity?.lockDrawer()

    fun unlockDrawer() = baseActivity?.unlockDrawer()
}