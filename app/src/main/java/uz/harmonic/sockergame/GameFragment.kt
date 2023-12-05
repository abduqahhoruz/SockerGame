package uz.harmonic.sockergame

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import uz.harmonic.sockergame.databinding.FragmentGameBinding

@AndroidEntryPoint
class GameFragment : Fragment(R.layout.fragment_game) {

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
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { viewModel.tickState.collect(::onTick) }
                launch { viewModel.team1Score.collect(::onPlayerOneScoreChange) }
                launch { viewModel.team2Score.collect(::onPlayerTwoScoreChange) }
                launch { viewModel.winnerCode.collect(::onWinnerFound) }
            }
        }

    }

    private fun setUpListeners() {
        with(binding) {
            btnToScore1.setOnClickListener {
                it.elevation = 10f
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

    private fun onTick(tick: Long) {
        if (tick == -1L) {
            viewModel.tickJobCancel()
            showWinner()
        }
        val sec = (tick / 1000) % 60
        val min = (tick / (1000 * 60)) % 60
        val formattedTimeStr = formatter(sec, min)
        binding.tvTimer.text = formattedTimeStr.toString()
    }

    private fun onPlayerOneScoreChange(score: Int) {
        binding.tvScoreTeam1.text = getString(R.string.team_score, args.team1, score)
    }

    private fun onPlayerTwoScoreChange(score: Int) {
        binding.tvScoreTeam2.text = getString(R.string.team_score, args.team2, score)
    }

    private fun onWinnerFound(winnerId: Int) {
        with(binding) {
            when (winnerId) {
                FIRST_PLAYER_WINS_CODE -> {
                    tvTeam1Win.text = getString(R.string.winner, args.team1)
                    tvTeam1Win.visibility = View.VISIBLE
                }

                SECOND_PLAYER_WINS_CODE -> {
                    tvTeam2Win.text = getString(R.string.winner, args.team2)
                    tvTeam2Win.visibility = View.VISIBLE
                }

                DURRANG_CODE -> {
                    tvTeam1Win.visibility = View.VISIBLE
                    tvTeam2Win.visibility = View.VISIBLE
                    tvTeam1Win.text = "Durrang"
                    tvTeam2Win.text = "Durrang"
                }
            }

        }
    }
}