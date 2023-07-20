package com.android.sample.mpcupload.ui.main
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.sample.mpcupload.api.ApiInterface
import com.android.sample.mpcupload.model.Photo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

// annotation to connect viewmodel from entry point
@HiltViewModel
class MainViewModel @Inject constructor(private val apiInterface: ApiInterface) : ViewModel() {
    // live data for progress bar observer
    var isLoading = MutableLiveData<Boolean>()

    // viewmodel initialization
    init {

    }

    // api get sample call
    fun apiGetCall(){
        isLoading.postValue(true)
        viewModelScope.launch {
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
    // api post sample call
    fun apiPostCall(event: String, task: String, area: String, comments: String, tags: String, date: String, photoListParam: List<Photo>){
        isLoading.postValue(true)

        // api I believe cant accept lists with items just for params only
        var allPhotosInString = ArrayList<String>()

        viewModelScope.launch {
            val result = apiInterface.postUser(event, task, area, date, comments, tags, allPhotosInString)
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