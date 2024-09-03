package org.unizd.rma.nekic.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import org.unizd.rma.nekic.models.MarineLife
import org.unizd.rma.nekic.repository.MarineLifeRepository
import org.unizd.rma.nekic.utils.Resource

class MarineLifeViewModel(application: Application) : AndroidViewModel(application) {

    private val marineLifeRepository = MarineLifeRepository(application)

    fun getMarineLifeList() = marineLifeRepository.getMarineLifeList()

    fun insertMarineLife(marineLife: MarineLife) : MutableLiveData<Resource<Long>>{
      return marineLifeRepository.insertMarineLife(marineLife) as MutableLiveData<Resource<Long>>
    }

    fun deleteMarineLifeUsingId(marineLifeId: String) : MutableLiveData<Resource<Int>>{
        return marineLifeRepository.deleteMarineLifeUsingId(marineLifeId) as MutableLiveData<Resource<Int>>
    }

    fun updateMarineLife(marineLife: MarineLife) : MutableLiveData<Resource<Int>>{
        return marineLifeRepository.updateMarineLife(marineLife) as MutableLiveData<Resource<Int>>
    }

}