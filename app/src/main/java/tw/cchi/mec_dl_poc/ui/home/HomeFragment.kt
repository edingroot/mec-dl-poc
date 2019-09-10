package tw.cchi.mec_dl_poc.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import tw.cchi.mec_dl_poc.MyApplication
import tw.cchi.mec_dl_poc.R
import tw.cchi.mec_dl_poc.config.Constants
import tw.cchi.mec_dl_poc.config.MecConnStatus
import tw.cchi.mec_dl_poc.viewmodel.MecViewModel
import java.util.*

class HomeFragment : Fragment() {
    private val application = MyApplication.instance
    private val mecHelper = application?.mecHelper
    private lateinit var frameResultObserver: java.util.Observer

    private lateinit var mecViewModel: MecViewModel
    private lateinit var txtTitle: TextView
    private lateinit var txtMecStatus: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mecViewModel =
            ViewModelProviders.of(this).get(MecViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        // Find views
        txtTitle = root.findViewById(R.id.txt_title)
        txtMecStatus = root.findViewById(R.id.txt_mec_status)

        // Set up view model observers
        mecViewModel.title.observe(this, Observer { txtTitle.text = it })
        mecViewModel.mecConnStatus.observe(this, Observer {
            when (it) {
                MecConnStatus.CONNECTING -> txtMecStatus.text = "Connecting to MEC server"
                MecConnStatus.CONNECTED -> txtMecStatus.text = "MEC server connected"
            }
        })

        frameResultObserver = Observer { obs, arg ->
            Log.i(Constants.TAG, "response from observer=%s".format(arg as String))
        }

        return root
    }

    override fun onResume() {
        super.onResume()

        application!!.frameResultObservable.addObserver(frameResultObserver)
    }

    override fun onPause() {
        super.onPause()
        application!!.frameResultObservable.deleteObserver(frameResultObserver)
    }
}
