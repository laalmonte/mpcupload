package com.android.sample.mpcupload.ui.main
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.sample.mpcupload.api.ApiInterface
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val apiInterface: ApiInterface) : ViewModel() {
    var isLoading = MutableLiveData<Boolean>()

    init {

    }

    fun apiCall(){
        isLoading.postValue(true)
        Log.e("USERS", "apicall")
        viewModelScope.launch {
            Log.e("USERS","launch")
            val result = apiInterface.getUsers()
            Log.e("USERS", result.toString())
            if (result != null){
                val resBody = result.body()
                resBody?.let { bod ->
                    Log.e("USERS", bod.toString())
                }
                isLoading.postValue(false)
            }
        }
    }
}