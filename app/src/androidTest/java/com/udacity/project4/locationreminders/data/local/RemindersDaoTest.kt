package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;
import com.udacity.project4.locationreminders.data.dto.ReminderDTO

import org.junit.runner.RunWith;

import kotlinx.coroutines.ExperimentalCoroutinesApi;
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.*

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Unit test the DAO
@SmallTest
class RemindersDaoTest {

//    TODO: Add testing implementation to the RemindersDao.kt
@get:Rule
var instantExecutorRule = InstantTaskExecutorRule()
    private lateinit var database: RemindersDatabase
    @Before
    fun initDb() {
        // Using an in-memory database so that the information stored here disappears when the
        // process is killed.
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).build()
    }
    @After
    fun closeDb() = database.close()
    @Test
    fun insertandgetdata()=runBlockingTest{
        val rem=  ReminderDTO("test","description","location",29.1693,29.1693)
        val rem2=  ReminderDTO("test2","description","location",29.1693,29.1693)
        val rem3=  ReminderDTO("test3","description","location",29.1693,29.1693)
        database.reminderDao().saveReminder(rem)
        database.reminderDao().saveReminder(rem2)
        database.reminderDao().saveReminder(rem3)
        val list=database.reminderDao().getReminders()
        Assert.assertThat(list.size, `is`(3))
        val remtest=database.reminderDao().getReminderById(rem.id)
        Assert.assertThat<ReminderDTO>(remtest as ReminderDTO, notNullValue())
        Assert.assertThat(remtest.id, `is`(rem.id))
        Assert.assertThat(remtest.title, `is`(rem.title))
        Assert.assertThat(remtest.description, `is`(rem.description))
        Assert.assertThat(remtest.location, `is`(rem.location))
        Assert.assertThat(remtest.latitude, `is`(rem.latitude))
        Assert.assertThat(remtest.longitude, `is`(rem.longitude))
    }
}