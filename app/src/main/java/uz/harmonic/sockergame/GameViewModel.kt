package uz.harmonic.sockergame

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class GameViewModel : ViewModel() {
    private val _tickState = SingleLiveEvent<Long>()
    val tickState: LiveData<Long> get() = _tickState

    private val _team1Score = SingleLiveEvent<Int>()
    val team1Score: LiveData<Int> get() = _team1Score

    private val _team2Score = SingleLiveEvent<Int>()
    val team2Score: LiveData<Int> get() = _team2Score

    private val _winnerCode = SingleLiveEvent<Int>()
    val winnerCode: LiveData<Int> get() = _winnerCode

    init {
        _team1Score.postValue(0)
        _team2Score.postValue(0)
    }

    private var job: Job? = null
    fun tick(millisInFuture: Long, countDownInterval: Long) {
        job?.cancel()
        job = viewModelScope.launch {
            var max = millisInFuture
            while (max > 0) {
                _tickState.postValue(max)
                delay(countDownInterval)
                max -= countDownInterval
            }
            _tickState.postValue(-1L)
        }
    }

    fun tickJobCancel() {
        job?.cancel()
    }

    fun scoreToTeam1() {
        _team1Score.postValue((team1Score.value ?: 0) + 1)
    }

    fun scoreToTeam2() {
        _team2Score.postValue((team2Score.value ?: 0) + 1)
    }

    fun showWinner() {
        if ((team1Score.value ?: 0) > (team2Score.value ?: 0)) {
            _winnerCode.postValue(1)
        } else if ((team1Score.value ?: 0) < (team2Score.value ?: 0)) {
            _winnerCode.postValue(2)
        } else {
            _winnerCode.postValue(0)
        }
    }
}