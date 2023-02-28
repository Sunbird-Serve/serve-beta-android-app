package org.evidyaloka.partner.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_profile.*
import org.evidyaloka.core.Constants.PartnerConst
import org.evidyaloka.partner.R
import org.evidyaloka.partner.ui.BaseFragment

@AndroidEntryPoint
class ProfileFragment : BaseFragment() {

    private lateinit var profilePagerAdapter: ProfileUpdatePagerAdapter

    companion object {
        var roleType = PartnerConst.RoleType.DSP
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        roleType = arguments?.get(PartnerConst.ROLE) as PartnerConst.RoleType
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return layoutInflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        try {
            profilePagerAdapter =
                    ProfileUpdatePagerAdapter(this)
            view_pager?.adapter = profilePagerAdapter

            //To Create Two tabs and bind tabs with View Pager
            TabLayoutMediator(tab_layout, view_pager) { tab, position ->
                context?.let {
                    when (position) {
                        0 -> {
                            tab.text = it.getString(R.string.tab_personal_details)
                        }
                        1 -> {
                            tab.text = it.getString(R.string.tab_organization_details)
                        }
                    }
                }
            }.attach()
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            e.printStackTrace()
        }
    }


    class ProfileUpdatePagerAdapter(fa: Fragment) :
            FragmentStateAdapter(fa) {

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                1 -> ProfileDetailsFragment.newInstance(
                        ProfileDetailsFragment.Companion.ProfileDetailType.ORGANIZATION,
                        roleType
                )
                else -> ProfileDetailsFragment.newInstance(
                        ProfileDetailsFragment.Companion.ProfileDetailType.PROFILE,
                        roleType
                )
            }
        }

        override fun getItemId(position: Int): Long {
            return super.getItemId(position)
        }

        /*
        This function returns the number of Tabs
         */
        override fun getItemCount(): Int = 2


    }
}

