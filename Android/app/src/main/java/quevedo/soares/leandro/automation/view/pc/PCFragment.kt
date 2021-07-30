package quevedo.soares.leandro.automation.view.pc

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import quevedo.soares.leandro.automation.databinding.FragmentPcBinding

class PCFragment : Fragment() {

	private lateinit var binding: FragmentPcBinding

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
		binding = FragmentPcBinding.inflate(inflater, container, false)
		return binding.root
	}

}