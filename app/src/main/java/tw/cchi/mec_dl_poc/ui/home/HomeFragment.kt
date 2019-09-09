package tw.cchi.mec_dl_poc.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import tw.cchi.mec_dl_poc.R
import tw.cchi.mec_dl_poc.viewmodel.MecViewModel

class HomeFragment : Fragment() {

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

        txtTitle = root.findViewById(R.id.txt_title)
        txtMecStatus = root.findViewById(R.id.txt_mec_status)

        mecViewModel.title.observe(this, Observer { txtTitle.text = it })
        mecViewModel.mecStatus.observe(this, Observer { txtMecStatus.text = it })

        return root
    }

    override fun onResume() {
        super.onResume()

        mecViewModel.setMecStatus("Connecting to MEC server...")
    }

    fun connectMec() {

    }
}
