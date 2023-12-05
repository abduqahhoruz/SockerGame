package uz.harmonic.sockergame

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

const val FIRST_PLAYER_WINS_CODE = 1
const val SECOND_PLAYER_WINS_CODE = 2
const val DURRANG_CODE = 3

class GameViewModel : ViewModel() {

    private val _tickState = MutableStateFlow<Long>(0)
    val tickState = _tickState.asStateFlow()

    private val _team1Score = MutableStateFlow<Int>(0)
    val team1Score = _team1Score.asStateFlow()

    private val _team2Score = MutableStateFlow<Int>(0)
    val team2Score = _team2Score.asStateFlow()

    private val _winnerCode = MutableStateFlow<Int>(0)
    val winnerCode = _winnerCode.asStateFlow()

    private var job: Job? = null
    fun tick(millisInFuture: Long, countDownInterval: Long) {
        job?.cancel()
        job = viewModelScope.launch {
            var max = millisInFuture
            while (max > 0) {
                _tickState.value = max
                delay(countDownInterval)
                max -= countDownInterval
            }
            _tickState.value = (-1L)
        }
    }

    fun tickJobCancel() {
        job?.cancel()
    }

    fun scoreToTeam1() {
        _team1Score.value = (team1Score.value + 1)
    }

    fun scoreToTeam2() {
        _team2Score.value = (team2Score.value + 1)
    }

    fun showWinner() {
        if (team1Score.value > team2Score.value) {
            _winnerCode.value = (FIRST_PLAYER_WINS_CODE)
        } else if (team1Score.value < team2Score.value) {
            _winnerCode.value = (SECOND_PLAYER_WINS_CODE)
        } else {
            _winnerCode.value = (DURRANG_CODE)
        }
    }
}