package com.udacity.project4.locationreminders.data

import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result

//Use FakeDataSource that acts as a test double to the LocalDataSource
class FakeDataSource : ReminderDataSource {

    //    TODO: Create a fake data source to act as a double to the real data source
    private  var remidersServiceData = mutableListOf<ReminderDTO>()
    private var shouldReturnError = false
    fun seterror(boolean: Boolean){
        shouldReturnError=boolean
    }
    override suspend fun getReminders(): Result<List<ReminderDTO>> {
        if (shouldReturnError){
            return Result.Error("noData")
        }
        return Result.Success(remidersServiceData.toList())
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        remidersServiceData.add(reminder)
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        if (shouldReturnError){
            return Result.Error("noData")
        }
        return Result.Success((remidersServiceData.takeWhile { it.id==id })[0])
    }

    override suspend fun deleteAllReminders() {
        remidersServiceData.clear()
    }

    fun clearalldata(){
        remidersServiceData.clear()
    }
}