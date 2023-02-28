package org.evidyaloka.common.ui.banner

import android.R
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import org.evidyaloka.common.databinding.LayoutBannerAdapterBinding
import org.evidyaloka.common.interfaces.IAdapter

class BannersAdapter() :
    RecyclerView.Adapter<BannersAdapter.BannerHolder>(), IAdapter<Banner> {
    private val TAG = "BannersAdapter"
    private var banners: List<Banner> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerHolder {
        val binding =
            LayoutBannerAdapterBinding.inflate(LayoutInflater.from(parent?.context), parent, false)
        return BannerHolder(binding.root)
    }

    override fun setItems(banners: List<Banner>) {
        this.banners = banners
        notifyDataSetChanged()
    }

    override fun getItems(): List<Banner> {
        return this.banners
    }

    override fun getItemCount(): Int {
        return banners.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }


    override fun onBindViewHolder(holder: BannerHolder, position: Int) {
        Log.e(TAG, "onBindViewHolder: ")
        val banner = banners[position]

        banner?.let {
            holder.bind(it)
        }
    }

    inner class BannerHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val binding = LayoutBannerAdapterBinding.bind(view)
        fun bind(banner: Banner) {
            with(binding) {
                when (banner.userType) {
                    org.evidyaloka.core.Constants.CommonConst.PersonaType.Student -> {
                        binding.rlStudent.visibility = View.VISIBLE
                        binding.rlPartner.visibility = View.GONE
                        binding.tvStudentTitle1.text = banner.title
                        binding.tvStudentTitle2.text = banner.title2
                    }
                    else -> {
                        binding.rlPartner.visibility = View.VISIBLE
                        binding.rlStudent.visibility = View.GONE
                        binding.tvPartnerTitle.setText(banner.title)
                    }
                }
                val requestOptions = RequestOptions()
                requestOptions.transforms(RoundedCorners(40))
                Glide.with(binding.ivBanner.context)
                    .load(banner.icon)
                    .apply(requestOptions)
                    .into(binding.ivBanner)
            }
        }
    }
}