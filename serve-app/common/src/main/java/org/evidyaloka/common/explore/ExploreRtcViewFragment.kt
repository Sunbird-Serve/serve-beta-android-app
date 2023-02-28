package org.evidyaloka.common.explore


import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.crashlytics.FirebaseCrashlytics
import org.evidyaloka.common.R
import org.evidyaloka.common.databinding.FragmentRtcPlayerBinding
import org.evidyaloka.common.ui.rtc.OnItemClickListner
import org.evidyaloka.common.ui.rtc.SessionType
import org.evidyaloka.common.util.FileReaderActivity
import org.evidyaloka.common.util.PdfReaderActivity
import org.evidyaloka.core.Constants.CommonConst
import org.evidyaloka.core.Constants.StudentConst
import org.evidyaloka.core.model.rtc.ContentAttributes
import org.evidyaloka.player.FullScreenListener
import org.evidyaloka.player.PlayerError
import org.evidyaloka.player.PlayerListener
import org.evidyaloka.player.PlayerState


open class ExploreRtcViewFragment : Fragment() {
    private val TAG = "RtcViewFragment"
    lateinit var binding: FragmentRtcPlayerBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //hideToolbar()
        binding = FragmentRtcPlayerBinding.inflate(layoutInflater, container, false)
        lifecycle.addObserver(binding.adaptivePlayerView)
        binding.adaptivePlayerView.setFullScreenListener(object : FullScreenListener {
            override fun enterFullScreen() {
                activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            }

            override fun exitFullScreen() {
                activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            }

        })
        binding.adaptivePlayerView.setPlayerEventListner(object : PlayerListener {
            override fun onError(playerState: PlayerError) {
                binding.video.text = when (playerState) {
                    PlayerError.VIDEO_NOT_FOUND -> getString(R.string.video_not_found)
                    PlayerError.VIDEO_NOT_PLAYABLE -> getString(R.string.video_is_not_playable)
                    PlayerError.UNKNOWN -> getString(R.string.unknown_error)
                    PlayerError.INVALID_PARAMETER_IN_REQUEST -> getString(R.string.invalid_video_url)
                    PlayerError.OUT_OF_MEMORY -> getString(R.string.device_memory_full)
                    PlayerError.VIDEO_NOT_PLAYABLE_EMBEDED -> getString(R.string.video_not_allow_to_play_embed)
                    else -> getString(R.string.unknown_error)
                }
                binding.errorView.visibility = View.VISIBLE
            }

            override fun onPlayeBackEvent(playbackState: PlayerState) {

            }

        })
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.CoursePlayerViewPager.setOnItemClickListner(object : OnItemClickListner{
            override fun OnItemClick(
                type: SessionType,
                contentAttr: ContentAttributes
            ) {
                if(type == SessionType.VIDEO){
                    playVideoCoursePlayerItem(contentAttr)
                }else{
                    openInDocView(contentAttr)
                }
            }
        })
    }

    fun playPrimaryVideo(list: List<ContentAttributes>){
        var item : ContentAttributes? = null
        list.forEach {
            if(item == null || it.viewStatus == 0 ){
                item = it
            }
        }

        item?.let {
            playVideoCoursePlayerItem(it)
        }

    }

    private fun playVideoCoursePlayerItem(contentAttributes: ContentAttributes){
        try {
            contentAttributes.url?.let { url ->
                binding.errorView.visibility = View.GONE
                playVideo(url, contentAttributes.contentHost!!, 0F)
            }
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            Toast.makeText(context, "Video conetent host issue", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openInDocView(contentAttributes: ContentAttributes) {
        var title = contentAttributes.title?.let { it } ?: ""
        var url = contentAttributes.url?.let { it } ?: ""
        var contentType = contentAttributes.contentType?.let { it } ?: ""

        contentAttributes.url?.let {
            if (!it.startsWith(CommonConst.URL_STARTS_WITH_DRIVE_URL) || !it.startsWith(
                    CommonConst.URL_STARTS_WITH_GOOGLE_DOC_URL
                )
            ) {
                if (contentType.equals(CommonConst.MS_DOC) ||
                    contentType.equals(CommonConst.MS_PPT) ||
                    contentType.equals(CommonConst.MS_XLS)
                ) {
                    url = CommonConst.GOOGLE_DOC_URL + it
                }
            }
        }

        var bundle = Bundle()
        bundle.putString(StudentConst.WEB_URL, url)
        bundle.putString(StudentConst.PAGE_TITLE, title)
        bundle.putString(StudentConst.CONTENT_TYPE, contentType)


        var intent: Intent = if (contentType.equals(CommonConst.PDF)) {
            Intent(context, PdfReaderActivity::class.java)
        } else {
            Intent(context, FileReaderActivity::class.java)
        }
        intent.putExtras(bundle)
        startActivity(intent)
    }


    override fun onPause() {
        Log.e("RtcFragment","onPause")
        try {
            binding.adaptivePlayerView.pause()
        }catch (e:Exception){
            e.printStackTrace()
        }
        super.onPause()

    }


    fun playVideo(uri: String, contentHost: String, startTime: Float) {
        try {
            binding.adaptivePlayerView.setUrl(uri, contentHost, startTime)
        } catch (e: Exception) {
        }
    }

}