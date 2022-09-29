package uz.harmonic.sockergame

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import uz.harmonic.sockergame.databinding.FragmentGameBinding

@AndroidEntryPoint
class GameFragment : Fragment(R.layout.fragment_game) {
    private val tickObserver = Observer<Long> {
        if (it == -1L) {
            viewModel.tickJobCancel()
            showWinner()
        }
        val sec = (it / 1000) % 60
        val min = (it / (1000 * 60)) % 60
        val formattedTimeStr = formatter(sec, min)
        binding.tvTimer.text = formattedTimeStr.toString()

    }
    private val score1Observer = Observer<Int> {
        binding.tvScoreTeam1.text = getString(R.string.team_score, args.team1, it)
    }
    private val score2Observer = Observer<Int> {
        binding.tvScoreTeam2.text = getString(R.string.team_score, args.team2, it)
    }
    private val winnerCodeObserver = Observer<Int> {
        with(binding) {
            when (it) {
                1 -> {
                    tvTeam1Win.text = getString(R.string.winner, args.team1)
                    tvTeam1Win.visibility = View.VISIBLE
                }
                2 -> {
                    tvTeam2Win.text = getString(R.string.winner, args.team2)
                    tvTeam2Win.visibility = View.VISIBLE
                }
                0 -> {
                    tvTeam1Win.visibility = View.VISIBLE
                    tvTeam2Win.visibility = View.VISIBLE
                    tvTeam1Win.text = "Durrang"
                    tvTeam2Win.text = "Durrang"
                }
            }
        }
    }

    private fun formatter(sec: Long, min: Long): CharSequence {
        val formatTemplate = if (sec <= 9) {
            if (min <= 9) {
                "0$min : 0$sec"
            } else {
                "$min : 0$sec"
            }

        } else {
            if (min > 9) {
                "$min : $sec"
            } else {
                "0$min : $sec"
            }
        }
        return formatTemplate
    }


    private val binding: FragmentGameBinding by viewBinding(FragmentGameBinding::bind)
    private val args: GameFragmentArgs by navArgs()
    private val viewModel: GameViewModel by viewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpView()
        setUpListeners()
        setUpObservers()
    }

    private fun setUpObservers() {
        viewModel.tickState.observe(viewLifecycleOwner, tickObserver)
        viewModel.team1Score.observe(viewLifecycleOwner, score1Observer)
        viewModel.team2Score.observe(viewLifecycleOwner, score2Observer)
        viewModel.winnerCode.observe(viewLifecycleOwner,winnerCodeObserver)
    }

    private fun setUpListeners() {
        with(binding) {
            btnToScore1.setOnClickListener {
                viewModel.scoreToTeam1()
            }
            btnToScore2.setOnClickListener {
                viewModel.scoreToTeam2()
            }
        }

    }

    private fun setUpView() {
        viewModel.tick(args.matchTime * 1000L, 1000L)
    }

    private fun showWinner() {
        with(binding) {
            btnToScore1.isClickable = false
            btnToScore2.isClickable = false
            viewModel.showWinner()
        }
    }

}