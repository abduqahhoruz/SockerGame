package uz.harmonic.sockergame

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.jakewharton.rxbinding4.widget.textChanges
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.functions.Function3
import uz.harmonic.sockergame.databinding.FragmentStartBinding

@AndroidEntryPoint
class StartFragment : Fragment(R.layout.fragment_start) {
    private val cd: CompositeDisposable = CompositeDisposable()
    private val binding: FragmentStartBinding by viewBinding(FragmentStartBinding::bind)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupValidate()
        setUpListener()
    }

    private fun setUpListener() {
        binding.btnStart.setOnClickListener {
            findNavController().navigate(
                StartFragmentDirections.actionStartFragmentToGameFragment(
                    binding.etTime.text.toString().toInt(),
                    binding.etNameT1.text.toString(),
                    binding.etNameT2.text.toString()
                )
            )
        }

    }

    private fun setupValidate() {
        val d = Observable.combineLatest(
            binding.etTime.textChanges().skipInitialValue(),
            binding.etNameT1.textChanges().skipInitialValue(),
            binding.etNameT2.textChanges().skipInitialValue(),

            Function3(this::isValidate)
        ).doOnNext { binding.btnStart.isEnabled = it }
            .subscribe()
        cd.add(d)
    }

    private fun isValidate(
        time: CharSequence,
        team1Name: CharSequence,
        team2Name: CharSequence
    ): Boolean {
        if (time.isEmpty())
            binding.etTime.error = "Ushbu maydonni to`ldirish shart"
        else
            binding.etTime.error = null
        if (team1Name.isEmpty())
            binding.etNameT1.error = "Ushbu maydonni to`ldirish shart"
        else
            binding.etNameT1.error = null
        if (team2Name.isEmpty())
            binding.etNameT2.error = "Ushbu maydonni to`ldirish shart"
        else
            binding.etNameT2.error = null
        return time.isNotEmpty() && team1Name.isNotEmpty() && team2Name.isNotEmpty()
    }
}