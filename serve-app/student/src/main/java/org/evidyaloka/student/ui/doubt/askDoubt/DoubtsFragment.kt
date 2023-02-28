package org.evidyaloka.student.ui.doubt.askDoubt

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.AndroidEntryPoint
import org.evidyaloka.core.Constants.StudentConst
import org.evidyaloka.common.util.ImageUitl
import org.evidyaloka.common.util.Utils
import org.evidyaloka.common.helper.getFileName
import org.evidyaloka.core.helper.Resource
import org.evidyaloka.core.model.Session
import org.evidyaloka.core.model.SubTopic
import org.evidyaloka.student.R
import org.evidyaloka.student.databinding.FragmentDoubtsBinding
import org.evidyaloka.student.ui.BaseFragment
import org.evidyaloka.common.util.SubjectViewUtils
import org.evidyaloka.student.utils.Util

@AndroidEntryPoint
class DoubtsFragment : BaseFragment() {

    lateinit var binding: FragmentDoubtsBinding
    var uri:Uri? = null
    private var session: Session? = null
    private var timetableId: Int? = null
    private var subTopic: SubTopic? = null
    private val viewModel: DoubtsViewModel by viewModels()
    private var doubtPicId:Int = 0

    //Edit doubt args
    private var isEditFlow: Boolean = false
    private var doubtId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            var args = DoubtsFragmentArgs.fromBundle(it)
            uri = args.uri
            session = args.session
            timetableId = args.timetableId
            subTopic = args.subTopic
            isEditFlow = args.isEditFlow
            doubtId = args.doubtId
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDoubtsBinding.inflate(layoutInflater,container,false)

        with(binding){
            subject.text = session?.subjectName
            session?.subjectName?.let {
                val subjectUI = SubjectViewUtils.getCourseUISettings(it)
                icon.setImageResource(subjectUI.icon())
            }
            topic.text = session?.topicName
            binding.subtopic.text = subTopic?.subtopicName
        }

        binding.closeButton.setOnClickListener {
            navController.popBackStack()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setNetworkErrorObserver(viewModel)
        hideToolbar()
        activity?.let { ac ->
            val image:Bitmap? = uri?.let { ImageUitl.getBitmapFormUri(ac, it) }

            context?.let {
                Glide.with(it)
                    .load(image)
                    .error(R.drawable.ic_student_placeholder)
                    .placeholder(R.drawable.ic_student_placeholder)
                    .into(binding.image)
            }
            Log.e("registerForAcyResult", image?.height.toString())
        }

        binding.submit.setOnClickListener {
            uri?.let { imageUri ->
                if(doubtPicId == 0){
                    uploadProfilePic(imageUri)
                }else{
                    if(!isEditFlow) submitDoubt(doubtPicId) else editDoubt(doubtPicId)
                }

            }
        }
    }

    private fun uploadProfilePic(uri: Uri) {
        try {
            activity?.contentResolver?.openInputStream(uri)?.let { streamData ->
                activity?.let {
                    viewModel.uploadFile(
                        StudentConst.DocType.STUDENT_DOUBT.value,
                        StudentConst.DocFormat.JPG.value,
                        uri.getFileName(it)!!,
                        streamData
                    ).observe(viewLifecycleOwner, Observer { response ->
                        when (response) {
                            is Resource.Success -> {
                                response.data?.id?.let {
                                    doubtPicId = it
                                    if(!isEditFlow) submitDoubt(it) else editDoubt(it)
                                }

                            }
                            else -> {
                                handleNetworkError()
                            }
                        }
                    })
                }
            }
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            e.printStackTrace()
        }
    }

    private fun submitDoubt(imageId:Int){

        val data = HashMap<String, Any>()
        session?.id?.let { data[StudentConst.SESSION_ID] = it }
        session?.offeringId?.let { data[StudentConst.OFFERING_ID] = it }
        session?.topicId?.let { data[StudentConst.TOPIC_ID] = it }
        subTopic?.id?.let { data[StudentConst.SUBTOPIC_ID_T_CAPS] = it }
        imageId.let { data[StudentConst.DOUBTPIC_ID] = it }
        data[StudentConst.DOUBT_CREATED_DATE] = Utils.getCurrentDateInyyyyMMddHHmmss()

        viewModel.submitDoubt(data).observe(viewLifecycleOwner, Observer {

            when(it){
                is Resource.Success -> {
                    val successAlert = Util.showPopupDialog(requireContext(),getString(R.string.success),getString(R.string.your_doubt_submitted_successfully),R.drawable.ic_check_outline_green)
                    successAlert?.setOnDismissListener {
                        navController.popBackStack()
                    }
                    successAlert?.show()
                }
                is Resource.GenericError -> {
                    it.error?.code?.let { errorCode -> handleError(errorCode) }
                }
            }
        })
    }

    private fun editDoubt(imageId:Int){

        val data = HashMap<String, Any>()
        doubtId?.let { data[StudentConst.DOUBT_ID] = it }
        imageId.let { data[StudentConst.DOUBTPIC_ID] = it }
        //data[StudentConst.DOUBT_CREATED_DATE] = Utils.getCurrentDateInyyyyMMddHHmmss()

        viewModel.editDoubt(data).observe(viewLifecycleOwner, Observer {

            when(it){
                is Resource.Success -> {
                    val successAlert = Util.showPopupDialog(requireContext(),getString(R.string.success),getString(R.string.your_doubt_submitted_successfully),R.drawable.ic_check_outline_green)
                    successAlert?.setOnDismissListener {
                        navController.popBackStack()
                    }
                    successAlert?.show()
                }
                is Resource.GenericError -> {
                    it.error?.code?.let { errorCode -> handleError(errorCode) }
                }
            }
        })
    }

    private fun handleError(errorCode:Int){
        if(errorCode <= 0)
            return
        val errorMgs = when(errorCode){
            2 -> getString(R.string.field_missing)
            3 -> getString(R.string.invalid_request)
            64 -> getString(R.string.student_is_not_related_to_guardian)
            59 -> getString(R.string.invalid_session)
            61 -> getString(R.string.file_does_not_exist)
            else -> getString(R.string.err_network)
        }
        Util.showPopupDialog(requireContext(),getString(R.string.failed),errorMgs)?.show()
    }

    private fun editError(errorCode:Int){
        val errorMgs = when(errorCode){
            2 -> getString(R.string.field_missing)
            3 -> getString(R.string.invalid_request)
            64 -> getString(R.string.student_is_not_related_to_guardian)
            59 -> getString(R.string.invalid_session)
            61 -> getString(R.string.file_does_not_exist)
            else -> getString(R.string.err_network)
        }
        Util.showPopupDialog(requireContext(),getString(R.string.failed),errorMgs)?.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        showToolbar()
    }
}