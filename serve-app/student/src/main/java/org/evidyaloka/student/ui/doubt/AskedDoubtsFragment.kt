package org.evidyaloka.student.ui.doubt

import android.Manifest
import android.app.AlertDialog
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import android.view.*
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.AndroidEntryPoint
import org.evidyaloka.Doubt.ui.doubt.AskedDoubtAdapter
import org.evidyaloka.common.helper.PermissionRequester
import org.evidyaloka.common.helper.TakePicturePreview
import org.evidyaloka.core.Constants.StudentConst
import org.evidyaloka.core.student.model.Doubt
import org.evidyaloka.core.model.Session
import org.evidyaloka.core.model.SubTopic
import org.evidyaloka.partner.ui.helper.DoubtPreviewDialogFragment
import org.evidyaloka.student.R
import org.evidyaloka.student.databinding.FragmentAskedDoubtsBinding
import org.evidyaloka.student.databinding.LayoutImagePickerDialogBinding
import org.evidyaloka.student.ui.BaseFragment
import org.evidyaloka.student.ui.StudentHomeActivity
import org.evidyaloka.student.ui.rtc.DoubtViewModel

@AndroidEntryPoint
class AskedDoubtsFragment : BaseFragment() {

    private val TAG = "AskedDoubtsFragment"
    private val viewModel: DoubtViewModel by viewModels()
    private lateinit var binding: FragmentAskedDoubtsBinding
    private var askedDoubtAdapter = AskedDoubtAdapter()
    private var session: Session? = null
    private var timetableId: Int? = null
    private var subTopic: SubTopic? = null
    private var mEditDoubt: Doubt? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            var args = AskedDoubtsFragmentArgs.fromBundle(it)
            session = args.session
            timetableId = args.timetableId
            subTopic = args.subTopic
        }

        setHasOptionsMenu(true)
        activity?.let {
            try {
                (it as StudentHomeActivity).hideNavigationIcon()
                (it as StudentHomeActivity).setToolbarTitle(getString(R.string.label_heading_asked_doubts))
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.asked_doubt, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        val item = menu.findItem(R.id.action_notification)
        if (item != null) item.isVisible = false
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId.equals(R.id.nav_close)) {
            Log.e(TAG, "onOptionsItemSelected: ")
            activity?.let {
                it.onBackPressed()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //hideToolbar()
        binding = FragmentAskedDoubtsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setNetworkErrorObserver(viewModel)
        binding.rvDoubts.apply {
            adapter = askedDoubtAdapter
            layoutManager = LinearLayoutManager(
                context,
                LinearLayoutManager.VERTICAL,
                false
            )
        }

        askedDoubtAdapter.setClickListener(object : OnItemClickListner {
            override fun OnItemClick(
                doubt: Doubt,
                type: StudentConst.DoubtViewType?
            ) {
                when (type) {
                    StudentConst.DoubtViewType.EDIT -> {
                        //Todo open EDIT Doubt Screen
                        Log.e(TAG, "Open Edit doubt Screen")
                        mEditDoubt = doubt
                        selectImageDialog()
                    }
                    StudentConst.DoubtViewType.VIEW -> {
                        //Todo open View Doubt Screen
                        Log.e(TAG, "Open View doubt Screen")
                        showPreviewDialog(doubt)
                    }
                }
            }
        })

        //Todo call Get Doubts API
        session?.let {
            viewModel.getDoubts(
                offeringId = it.offeringId,
                topicId = it.topicId,
                subtopicId = subTopic?.id!!
            )
                .observe(viewLifecycleOwner, Observer {
                    //TODO take real values once API is setup
                    askedDoubtAdapter.submitList(null)
                    askedDoubtAdapter.submitList(it)

                })
            viewModel.doubtDetailsObserver.observe(viewLifecycleOwner, Observer {
                if (it <= 0) {
                    binding.llNoDoubts.rlNoDoubts.visibility = View.VISIBLE
                }
            })
        }
    }

    private fun openGalleryForResult() {
        openGallery.launch("image/*")
    }

    private fun openCameraForResult() {
        takePicturePreview.launch(null)
    }

    private fun showPreviewDialog(doubt: Doubt) {
        context?.let {
            DoubtPreviewDialogFragment.Builder(it)
                .setIsDialogCancelable(true)
                .setDoubt(doubt)
                .setViewType(DoubtPreviewDialogFragment.DIALOG_TYPE.FULL_SCREEN)
                .build()
                .show(childFragmentManager, "")
        }
    }

    private fun selectImageDialog() {
        var alertDialog: AlertDialog? = null
        val builder: AlertDialog.Builder = AlertDialog.Builder(this.context)
        val alertBinding = LayoutImagePickerDialogBinding.inflate(layoutInflater, null, false)
        builder.setView(alertBinding.root)
        with(alertBinding) {
            btCamera.setOnClickListener {
                alertDialog?.dismiss()
                cameraPermission.runWithPermission {
                    openCameraForResult()
                }
            }
            btGallery.setOnClickListener {
                alertDialog?.dismiss()
                storagePermission.runWithPermission{
                    openGalleryForResult()
                }
            }
        }
        alertDialog = builder.show()
    }

    private val storagePermission = PermissionRequester(
        this,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        R.string.rationale_storage_doubt
    )
    private val cameraPermission = PermissionRequester(
        this,
        Manifest.permission.CAMERA,
        R.string.rationale_camera_storage_doubt
    )
    private val takePicturePreview = registerForActivityResult(TakePicturePreview()) {
        it?.let {
            NavigateToDoubtView(it)
        }
    }

    private val openGallery = registerForActivityResult(ActivityResultContracts.GetContent()) {
        it?.let {
            NavigateToDoubtView(it)
        }
    }

    private fun NavigateToDoubtView(uri: Uri) {
        try {
            mEditDoubt?.let {
                val session = Session(
                    subjectName = it.subjectName,
                    topicName = it.topicName,
                    topicId = it.topicId,
                    offeringId = it.offeringId
                )
                val subTopic = SubTopic(subtopicName = it.subjectName, id = it.subTopicId)
                val navigate =
                    AskedDoubtsFragmentDirections.actionAskedDoubtsFragmentToDoubtsFragment(
                        uri, session!!, 0, subTopic!!, true, it.id
                    )
                navController.navigate(navigate)
            }


        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            e.printStackTrace()
        }
    }
}