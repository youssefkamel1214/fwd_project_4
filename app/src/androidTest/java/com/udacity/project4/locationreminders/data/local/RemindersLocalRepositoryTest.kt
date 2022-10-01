package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.MatcherAssert.assertThat
import org.junit.*
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Medium Test to test the repository
@MediumTest
class RemindersLocalRepositoryTest {
    private lateinit var database: RemindersDatabase
    private lateinit var LocalRepository:RemindersLocalRepository
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()
    @Before
    fun initDb() {
        // Using an in-memory database so that the information stored here disappears when the
        // process is killed.
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).build()
        LocalRepository= RemindersLocalRepository(database.reminderDao(),Dispatchers.IO)
    }
    @After
    fun closeDb() = database.close()
//    TODO: Add testing implementation to the RemindersLocalRepository.kt
    @Test
    fun test_save_reminders_and_retrive_it()= runBlocking{
            val rem=  ReminderDTO("test","description","location",29.1693,29.1693)
            val rem2=  ReminderDTO("test2","description","location",29.1693,29.1693)
            val rem3=  ReminderDTO("test3","description","location",29.1693,29.1693)
            LocalRepository.saveReminder(rem)
            LocalRepository.saveReminder(rem2)
            LocalRepository.saveReminder(rem3)
            val res1=LocalRepository.getReminders()
            Assert.assertThat(res1 is Result.Success, `is`(true))
            res1 as Result.Success
            Assert.assertThat(res1.data.size, `is`(3))
            val res2=LocalRepository.getReminder(rem.id)
            Assert.assertThat(res2 is Result.Success, `is`(true))
            val remtest=(res2 as Result.Success).data
            Assert.assertThat(remtest.title, `is`(rem.title))
            Assert.assertThat(remtest.description, `is`(rem.description))
            Assert.assertThat(remtest.location, `is`(rem.location))
            Assert.assertThat(remtest.latitude, `is`(rem.latitude))
            Assert.assertThat(remtest.longitude, `is`(rem.longitude))
            LocalRepository.deleteAllReminders()
            val res3=LocalRepository.getReminder(rem.id)
            Assert.assertThat(res3 is Result.Error, `is`(true))
            res3 as Result.Error
            Assert.assertThat(res3.message , `is`("Reminder not found!"))



}

}