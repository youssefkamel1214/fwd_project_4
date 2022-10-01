package com.udacity.project4.locationreminders.savereminder

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.base.NavigationCommand
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.getOrAwaitValue
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.pauseDispatcher
import kotlinx.coroutines.test.resumeDispatcher
import org.hamcrest.CoreMatchers
import org.junit.*
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class SaveReminderViewModelTest {
    private lateinit var remindersRepository: FakeDataSource

    // Subject under test
    private lateinit var remindersViewModel: SaveReminderViewModel

    // Executes each reminder synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()
    @Before
    fun setupViewModel() {
        remindersRepository = FakeDataSource()
        remindersViewModel = SaveReminderViewModel( ApplicationProvider.getApplicationContext(),remindersRepository)
    }
    @After
    fun tearDown() {
        stopKoin()
    }
    //TODO: provide testing to the SaveReminderView and its live data objects
    @Test
    fun testload(){
        val rem=  ReminderDataItem("test","description","location",29.1693,29.1693)
        mainCoroutineRule.pauseDispatcher()
        remindersViewModel.saveReminder(rem)
        Assert.assertThat(remindersViewModel.showLoading.getOrAwaitValue(), CoreMatchers.`is`(true))
        mainCoroutineRule.resumeDispatcher()
        Assert.assertThat(remindersViewModel.showLoading.getOrAwaitValue(), CoreMatchers.`is`(false))
        Assert.assertThat(remindersViewModel.showToast.getOrAwaitValue(), CoreMatchers.`is`("Reminder Saved !"))
        Assert.assertThat(remindersViewModel.navigationCommand.getOrAwaitValue(), CoreMatchers.`is`(
            NavigationCommand.Back))
    }
    @Test
    fun testentaringdata(){
        val rem=  ReminderDataItem("test","description",null,null,null)
        val rem2=  ReminderDataItem("test","description","location",29.1693,29.1693)
        Assert.assertThat(remindersViewModel.validateEnteredData(rem), CoreMatchers.`is`(false))
        Assert.assertThat(remindersViewModel.validateEnteredData(rem2), CoreMatchers.`is`(true))


    }
}