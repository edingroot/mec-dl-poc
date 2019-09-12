package tw.cchi.mec_dl_poc.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import tw.cchi.mec_dl_poc.MyApplication
import tw.cchi.mec_dl_poc.R

class SettingsFragment : Fragment() {
    private val application = MyApplication.instance

    private lateinit var settingsViewModel: SettingsViewModel
    private lateinit var editMecHost: EditText
    private lateinit var editMecPort: EditText
    private lateinit var btnSet: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        settingsViewModel =
            ViewModelProviders.of(this).get(SettingsViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_settings, container, false)

        // Find views
        editMecHost = root.findViewById(R.id.editMecHost)
        editMecPort = root.findViewById(R.id.editMecPort)
        btnSet = root.findViewById(R.id.btnSet)

        initViewModelSubscription()
        registerListeners()

        return root
    }

    private fun initViewModelSubscription() {
        settingsViewModel.initialize()
        settingsViewModel.mecHost.observe(this, Observer { editMecHost.setText(it) })
        settingsViewModel.mecPort.observe(this, Observer { editMecPort.setText(it.toString()) })
    }

    private fun registerListeners() {
        btnSet.setOnClickListener {
            settingsViewModel.setMecHost(editMecHost.text.toString())
            settingsViewModel.setMecPort(editMecPort.text.toString().toInt())
            settingsViewModel.savePreferences()

            application?.reconnectMecServer()
            Toast.makeText(this.context, "Settings updated", Toast.LENGTH_SHORT).show()
        }
    }
}
