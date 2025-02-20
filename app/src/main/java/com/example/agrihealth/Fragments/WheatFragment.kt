import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.agrihealth.GetimgActivity
import com.example.agrihealth.R
import com.example.agrihealth.databinding.FragmentWheatBinding

class WheatFragment : Fragment() {
    private var _binding: FragmentWheatBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentWheatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val activity = requireActivity() as? GetimgActivity

        // Retrieve the color value and pass it to the activity method
        val yellowColor = ContextCompat.getColor(requireContext(), R.color.yellow)
        val yellowColorExtra = ContextCompat.getColor(requireContext(), R.color.ExtraYellow)
        activity?.setColor(yellowColor, yellowColorExtra)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
