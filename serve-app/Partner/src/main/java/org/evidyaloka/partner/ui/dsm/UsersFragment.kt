package org.evidyaloka.partner.ui.dsm

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_dsm_users.*
import org.evidyaloka.core.Constants.PartnerConst
import org.evidyaloka.partner.R
import org.evidyaloka.core.helper.Resource
import org.evidyaloka.partner.ui.BaseFragment

@AndroidEntryPoint
class UsersFragment : BaseFragment() {
    private val TAG: String = "UsersFragment"
    private val viewModel: UserViewModel by viewModels()
    private val adapter = UserAdapter(
            { userId: Int -> onDsmUserItemClick(userId) },
            { userId: Int -> onSchoolItemClick(userId) })

    companion object {
        const val OPEN_TYPE = "OPEN_TYPE"

        enum class Type {
            DSM_PROFILE, ASSIGNED_SCHOOL {
                override fun toString(): String {
                    return super.toString()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_dsm_users, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setNetworkErrorObserver(viewModel)
        context?.let {
            rv_users?.let { rv ->
                rv.layoutManager =
                        LinearLayoutManager(it, LinearLayoutManager.VERTICAL, false)
                rv.addItemDecoration(
                        DividerItemDecoration(
                                rv.getContext(),
                                DividerItemDecoration.VERTICAL
                        )
                )
                rv.adapter = adapter
                try {
                    viewModel.getAllUsers(0, "").observe(viewLifecycleOwner, Observer { response ->
                        adapter?.submitList(response)
                        /*viewModel.userCountObserver.observe(viewLifecycleOwner, Observer {
                            it?.let {
                                showNumberOfUsers(it)
                            }
                        })*/
                    })
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        fab_add_users?.setOnClickListener {
        try{
            findNavController().navigate(R.id.addUserFragment)
        }catch (e : Exception){
            FirebaseCrashlytics.getInstance().recordException(e)

        }
        }

        etv_search?.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // Do nothing
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // Do nothing
            }

            override fun afterTextChanged(s: Editable) {
                try {
                    viewModel.getAllUsers(0, s.toString()).observe(viewLifecycleOwner, Observer { response ->
                        adapter?.submitList(response)
                    })
                    /*viewModel.userCountObserver.observe(viewLifecycleOwner, Observer {
                    it?.let {
                        showNumberOfUsers(it)
                    }
                })*/
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })
    }

    /*
     * To Show the number of DSM Users
     */
    private fun showNumberOfUsers(count: Int) {
        try {
            tv_total_users?.visibility = View.VISIBLE
            tv_total_users?.setText(String.format(getString(R.string.total_users), count))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    /*
     * To open Schools on DSM User Details Page
     */

    fun onSchoolItemClick(userId: Int) {
        loadUserDetails(userId)
//        var args = Bundle().apply {
//            putString(OPEN_TYPE, Type.ASSIGNED_SCHOOL.name)
//            putInt(USER_ID, userId)
//        }
//        findNavController().navigate(R.id.action_usersFragment_to_userDetailsFragment, args)
    }

    /*
     * To open DSM User Details Page
     */
    fun onDsmUserItemClick(userId: Int) {
        var args = Bundle().apply {
            putString(OPEN_TYPE, Type.DSM_PROFILE.name)
            putInt(PartnerConst.USER_ID, userId)
        }
        try{
        findNavController().navigate(R.id.action_usersFragment_to_userDetailsFragment, args)
        }catch (e : Exception){
            FirebaseCrashlytics.getInstance().recordException(e)

        }
    }

    private fun loadUserDetails(userId: Int) {
        try {
            viewModel.getUserDetails(userId).observe(viewLifecycleOwner, Observer { response ->
                when (response) {
                    is Resource.Success -> {
                        response.data?.let {
                            it?.takeIf { it.isNotEmpty() }?.get(0)?.let {
                                if (it.schoolsAssigned.isNotEmpty()) {
                                    val dialogFragment = CustomDialogFragment()
                                    val args = Bundle()

                                    args.putString("DIALOG_TYPE", PartnerConst.DIALOG_TYPE.NORMAL.name)
                                    args.putString("VIEW_TYPE", PartnerConst.VIEW_TYPE.DSM_ASSIGNED_SCHOOL.name)
                                    args.putSerializable("SCHOOLS_ASSIGNED", ArrayList(it.schoolsAssigned))
                                    dialogFragment?.arguments = args
                                    dialogFragment?.show(childFragmentManager, "")
                                }
                            }
                        }
                    }
                    is Resource.GenericError -> {

                    }
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}