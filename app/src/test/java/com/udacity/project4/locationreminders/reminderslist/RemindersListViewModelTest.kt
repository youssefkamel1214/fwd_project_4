package com.udacity.project4.locationreminders.reminderslist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.pauseDispatcher
import kotlinx.coroutines.test.resumeDispatcher
import com.udacity.project4.locationreminders.MainCoroutineRule
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.junit.*
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class RemindersListViewModelTest {
    private lateinit var remindersRepository: FakeDataSource

    // Subject under test
    private lateinit var remindersViewModel: RemindersListViewModel

    // Executes each reminder synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()
    @Before
    fun setupViewModel() {
        remindersRepository = FakeDataSource()
        val rem=  ReminderDTO("test","description","location",29.1693,29.1693)
        val rem2=  ReminderDTO("test2","description","location",29.1693,29.1693)
        val rem3=  ReminderDTO("test3","description","location",29.1693,29.1693)
        runBlockingTest {
            remindersRepository.saveReminder(rem)
            remindersRepository.saveReminder(rem2)
            remindersRepository.saveReminder(rem3)
        }
        remindersViewModel = RemindersListViewModel( ApplicationProvider.getApplicationContext(),remindersRepository)
    }
    @After
    fun tearDown() {
        stopKoin()
    }
    @Test
    fun loaddatatest(){
        mainCoroutineRule.pauseDispatcher()
        remindersViewModel.loadReminders()
        Assert.assertThat(remindersViewModel.showLoading.getOrAwaitValue(), CoreMatchers.`is`(true))
        mainCoroutineRule.resumeDispatcher()
        Assert.assertThat(remindersViewModel.showLoading.getOrAwaitValue(), CoreMatchers.`is`(false))
        Assert.assertThat(remindersViewModel.remindersList.getOrAwaitValue().isEmpty(), CoreMatchers.`is`(false))
    }
    @Test
    fun testnodata(){
        remindersRepository.clearalldata()
        remindersViewModel.loadReminders()
        Assert.assertThat(remindersViewModel.remindersList.getOrAwaitValue().isEmpty(), CoreMatchers.`is`(true))
    }
    @Test
    fun returnerror(){
        remindersRepository.seterror(true)
        remindersViewModel.loadReminders()
        Assert.assertThat(remindersViewModel.showSnackBar.getOrAwaitValue(), CoreMatchers.`is`("Data base error"))
        remindersRepository.seterror(false)

    }
    //TODO: provide testing to the RemindersListViewModel and its live data objects

}