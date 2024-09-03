package org.unizd.rma.nekic.repository

import android.app.Application
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import org.unizd.rma.nekic.dao.MarineLifeDao
import org.unizd.rma.nekic.database.MarineLifeDatabase
import org.unizd.rma.nekic.models.MarineLife
import org.unizd.rma.nekic.utils.Resource

class MarineLifeRepository(application: Application) {
 private  val marineLifeDao: MarineLifeDao = MarineLifeDatabase.getInstance(application).marineLifeDao

    fun getMarineLifeList() = flow{

    emit(Resource.Loading())

        try {

            val result = marineLifeDao.getMarineLifeList()
            emit(Resource.Success(result))

        }catch (e: Exception){
            emit(Resource.Error(e.message.toString()))
        }

    }

    fun insertMarineLife(marineLife: MarineLife): Any = MutableLiveData<Resource<Long>> ().apply{
        postValue(Resource.Loading())

     try {

      CoroutineScope(Dispatchers.IO).launch {
       val result = marineLifeDao.insertMarineLife(marineLife)
      postValue(Resource.Success(result))
      }

     } catch (e : Exception){
  postValue(Resource.Error(e.message.toString()))

     }
    }

    fun deleteMarineLifeUsingId(marineLifeId : String): Any = MutableLiveData<Resource<Int>> ().apply{
        postValue(Resource.Loading())

        try {

            CoroutineScope(Dispatchers.IO).launch {
                val result = marineLifeDao.deleteMarineLifeUsingId(marineLifeId)
                postValue(Resource.Success(result))
            }

        } catch (e : Exception){
            postValue(Resource.Error(e.message.toString()))

        }
    }


    fun updateMarineLife(marineLife: MarineLife): Any = MutableLiveData<Resource<Int>> ().apply{
        postValue(Resource.Loading())

        try {

            CoroutineScope(Dispatchers.IO).launch {
                val result = marineLifeDao.updateMarineLife(marineLife)
                postValue(Resource.Success(result))
            }

        } catch (e : Exception){
            postValue(Resource.Error(e.message.toString()))

        }
    }

}