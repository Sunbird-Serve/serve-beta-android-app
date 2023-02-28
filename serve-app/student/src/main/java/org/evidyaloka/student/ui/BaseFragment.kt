package org.evidyaloka.student.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.google.firebase.crashlytics.FirebaseCrashlytics
import org.evidyaloka.student.R
import org.evidyaloka.student.utils.Util

/**
 * @author Madhankumar
 * created on 07-01-2021
 *
 */
open class BaseFragment : Fragment() {
    private val TAG = "BaseFragment"

    //Image request code
    val PICK_PROFILE_IMAGE_REQUEST = 1
    val PICK_LOGO_IMAGE_REQUEST = 1
    val PICK_BANNER_IMAGE_REQUEST = 2

    //storage permission code
    val RC_READ_STORAGE_PERM = 120
    val RC_WRITE_STORAGE_PERM = 121
    val RC_CAMERA_STORAGE_PERM = 122
    val RC_LOCATION_PERM = 123


    lateinit var navController: NavController

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()
    }

    fun setNetworkErrorObserver(viewModel: BaseViewModel) {
        viewModel.getNetworkErrorObserable().observe(viewLifecycleOwner, Observer {
            it?.let {
                when (it) {
                    -1 -> handleNetworkDialog(false, viewModel::retry)
                    else -> {
                        activity?.let {
                            Util.showRetryPopupDialog(
                                it,
                                it.resources.getString(R.string.label_sorry),
                                it.resources.getString(R.string.could_not_able_to_fetch_data),
                                onClick = viewModel::retry
                            )?.show()
                        }
                    }
                }
            }
        })
    }

    /**
     * This function will be responsible to create snackbar and display to userEntity
     * @param message String text to show in snackbar
     * @param action String Action button text
     * @param listener action button click listener, if null default action button listener will be created
     */
    fun showSnackBar(
        message: String,
        action: String? = null,
        listener: View.OnClickListener? = null
    ) {
        try {
            activity?.takeIf { it is StudentHomeActivity }?.let { (it as StudentHomeActivity).showSnackBar(message, action, listener) }
        } catch (e: Exception) {
            e.printStackTrace()
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    /**
     * Create generic network error snackbar
     */
    fun handleNetworkError() {
        activity?.takeIf { it is StudentHomeActivity }?.let { (it as StudentHomeActivity).handleNetworkError() }
    }

    fun handleNetworkDialog(isFullScreen: Boolean = true, onClick: () -> Unit) =
        activity?.takeIf { it is StudentHomeActivity }?.let { (it as StudentHomeActivity).showNetworkErrorDialog(isFullScreen, onClick) }

    fun setToolbarTitle(title: String) =
        activity?.takeIf { it is StudentHomeActivity }?.let { (it as StudentHomeActivity).setToolbarTitle(title) }

    /**
     * shows toolbar
     */
    fun showToolbar() = activity?.takeIf { it is StudentHomeActivity }?.let { (it as StudentHomeActivity).showToolbar() }

    /**
     * hides toolbar
     */
    fun hideToolbar() = activity?.takeIf { it is StudentHomeActivity }?.let { (it as StudentHomeActivity).hideToolbar() }

    /**
     * This function hides keyboard
     */
    fun hideKeyboard() = activity?.takeIf { it is StudentHomeActivity }?.let { (it as StudentHomeActivity).hideKeyboard() }

    fun lockDrawer() = activity?.takeIf { it is StudentHomeActivity }?.let { (it as StudentHomeActivity).lockDrawer() }

    fun unlockDrawer() = activity?.takeIf { it is StudentHomeActivity }?.let { (it as StudentHomeActivity).unlockDrawer() }


    open fun showPopup(title:String, message:String){
        try{
            this.context?.let {
                Util.showPopupDialog(
                    it,
                    title = title,
                    message = message
                )?.let {
                    it.show()
                }
            }
        }catch(e : Exception){
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }
}