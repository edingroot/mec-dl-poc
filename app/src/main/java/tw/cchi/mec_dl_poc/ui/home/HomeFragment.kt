package tw.cchi.mec_dl_poc.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import tw.cchi.mec_dl_poc.MyApplication
import tw.cchi.mec_dl_poc.R
import tw.cchi.mec_dl_poc.config.MecConnStatus

class HomeFragment : Fragment() {
    private val application = MyApplication.instance
    private val mecHelper = application?.mecHelper

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var txtTitle: TextView
    private lateinit var txtMecStatus: TextView
    private lateinit var btnTest: Button
    private lateinit var txtMessages: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProviders.of(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        // Find views
        txtTitle = root.findViewById(R.id.txt_title)
        txtMecStatus = root.findViewById(R.id.txt_mec_status)
        btnTest = root.findViewById(R.id.btn_test)
        txtMessages = root.findViewById(R.id.txt_messages)

        initViewModelSubscription()
        initListeners()

        // Should be called after initViewModelSubscription: homeViewModel.initialize()
        if (!mecHelper!!.streamingInitialized)
            application?.initializeMec()

        return root
    }

    private fun initViewModelSubscription() {
        homeViewModel.initialize()

        homeViewModel.title.observe(this, Observer { txtTitle.text = it })

        homeViewModel.mecConnStatus.observe(this, Observer {
            when (it) {
                MecConnStatus.DISCONNECTED -> txtMecStatus.text = "MEC server disconnected"
                MecConnStatus.CONNECTING -> txtMecStatus.text = "Connecting to MEC server"
                MecConnStatus.CONNECTED -> txtMecStatus.text = "MEC server connected"
                MecConnStatus.FAILED -> txtMecStatus.text = "Failed to connect MEC server"
            }
        })

        homeViewModel.messages.observe(this, Observer { txtMessages.text = it })
    }

    private fun initListeners() {
        btnTest.setOnClickListener {
            if (mecHelper != null && mecHelper.streamingInitialized) {
                mecHelper.sendUdpString("Test Hello Worrrrrld")
            }
        }
    }
}
