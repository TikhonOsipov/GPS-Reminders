package com.tixon.reminders

import com.tixon.reminders.model.PlaceLocation
import com.tixon.reminders.model.Reminder
import com.tixon.reminders.storage.RemindersRepository
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

@RunWith(JUnit4::class)
class RemindersRepositoryTest {

    @Mock
    lateinit var repo: RemindersRepository

    @Before
    fun before() {
        MockitoAnnotations.initMocks(this)
    }

    @Test
    fun `pending location is added into pendingLocationsList when method addPendingLocation(place) is called`() {
        val place = PlaceLocation(1.0, 2.0)
        repo.clearPendingLocationList()
        `when`(repo.addPendingLocation(place)).then {
            Assert.assertTrue(repo.getPendingLocationsList().contains(place))
            Assert.assertTrue(repo.getPendingLocationsList().size == 1)
            Assert.assertEquals(
                "Latitude is the same",
                repo.getPendingLocationsList().first().latitude,
                place.latitude
            )
            Assert.assertEquals(
                "Longitude is the same",
                repo.getPendingLocationsList().first().longitude,
                place.longitude
            )
        }
    }

    @Test
    fun `pending location is cleared when method clearPendingLocationList() is called`() {
        repo.clearPendingLocationList()
        val place = PlaceLocation(1.0, 2.0)
        `when`(repo.addPendingLocation(place)).then {
            Assert.assertTrue(repo.getPendingLocationsList().contains(place))
            Assert.assertTrue(repo.getPendingLocationsList().size == 1)
        }

        `when`(repo.clearPendingLocationList()).then {
            Assert.assertFalse(repo.getPendingLocationsList().contains(place))
            Assert.assertTrue(repo.getPendingLocationsList().isEmpty())
        }
    }

    @Test
    fun `pending location is added to repo and is removed after reminder was added`() {
        val place = PlaceLocation(1.0, 2.0)
        `when`(repo.addPendingLocation(place)).then {

            Assert.assertFalse(repo.getPendingLocationsList().isEmpty())

            `when`(
                repo.addReminder(
                    Reminder(
                        title = "mock reminder",
                        isCompleted = false,
                        locations = emptyList()
                    )
                )
            ).then {
                Assert.assertTrue(repo.getPendingLocationsList().isEmpty())
            }
        }
    }
}
